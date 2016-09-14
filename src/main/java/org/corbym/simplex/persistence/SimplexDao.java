package org.corbym.simplex.persistence;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import net.sf.cglib.proxy.Enhancer;
import org.corbym.simplex.persistence.converters.CollectionConverter;
import org.corbym.simplex.persistence.converters.FilterableConverterLookup;
import org.corbym.simplex.persistence.converters.IdFieldAnnotatedObjectConverter;
import org.corbym.simplex.persistence.converters.ParentFieldTrackingMarshalListener;
import org.corbym.simplex.persistence.marshalling.AccessibleReferenceByXpathMarshallingStrategy;
import org.corbym.simplex.persistence.util.FieldReflectionDelegate;
import org.corbym.simplex.persistence.util.SequenceGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.corbym.simplex.persistence.util.CurrentContextHelper.currentContextClassLoader;
import static org.corbym.simplex.persistence.util.FileHelper.*;

public class SimplexDao {
    protected String store = SimplexDaoFactory.DEFAULT_STORE_LOCATION;
    private static final Object lock = new Object();
    protected static final String SEQUENCE_MAP = "sequenceMap";

    private final FieldReflectionDelegate idFieldReflectionDelegate;
    private final XStream xstream;
    private SequenceGenerator generator;


    protected SimplexDao() throws SimplexPersistenceException {
        idFieldReflectionDelegate = new FieldReflectionDelegate();
        final FilterableConverterLookup converterLookup = new FilterableConverterLookup();
        xstream = new XStream(null, new DomDriver(), new ClassLoaderReference(new CompositeClassLoader()), (Mapper) null, converterLookup, null);
        setUpXStream(converterLookup);
        loadGenerator();
    }

    protected SimplexDao(String storeLocation) throws SimplexPersistenceException {
        this();
        store = storeLocation;
    }

    private void setUpXStream(FilterableConverterLookup converterLookup) {
        AccessibleReferenceByXpathMarshallingStrategy marshallingStrategy = new AccessibleReferenceByXpathMarshallingStrategy(AccessibleReferenceByXpathMarshallingStrategy.RELATIVE);
        Mapper mapper = xstream.getMapper();
        ParentFieldTrackingMarshalListener marshalListener = new ParentFieldTrackingMarshalListener(mapper);
        IdFieldAnnotatedObjectConverter converter = new IdFieldAnnotatedObjectConverter(this, mapper, converterLookup, marshalListener, new FieldReflectionDelegate());
        CollectionConverter collectionConverter = new CollectionConverter(converterLookup, marshalListener, mapper);
        marshallingStrategy.registerListener(marshalListener);
        xstream.setMarshallingStrategy(marshallingStrategy);
        xstream.registerConverter(converter);
        //TODO: fix the converter :(
        xstream.registerConverter(collectionConverter);

    }

    private void loadGenerator() throws SimplexPersistenceException {
        generator = (SequenceGenerator) loadSingleton(SEQUENCE_MAP);
        if (generator == null) {
            generator = new SequenceGenerator();
        }
    }

    public boolean singletonExists(String name) {
        return new File(concatenateStorePathAndFilename(name) + ".xml").exists();
    }

    public List<Object> saveAll(Object... items) throws SimplexPersistenceException {
        List<Object> itemsAsList = Arrays.asList(items);
        for (Object obj : itemsAsList) {
            this.save(obj);
        }
        return itemsAsList;
    }

    public Object save(Object item) throws SimplexPersistenceException {
        long longObjectId;

        synchronized (lock) {
            final Number objectId = idFieldReflectionDelegate.getIdFrom(item);
            if (objectId != null) {
                longObjectId = objectId.longValue();
            } else {
                longObjectId = generator.nextId();
                saveSingleton(SEQUENCE_MAP, generator);
                idFieldReflectionDelegate.setIdUsing(item, longObjectId);
            }
        }
        final String fileName = createFileNameWithId(item.getClass(), longObjectId);
        internalSave(item, fileName);
        return item;
    }

    public Object load(Class itemClazz, Number id) throws SimplexPersistenceException {
        final String fileName = createFileNameWithId(itemClazz, id);
        return internalLoad(fileName);
    }

    public Object load(final Number id) throws SimplexPersistenceException {
        String[] possibleFileNames = new File(store).list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("@" + id + ".xml");
            }
        });
        if (possibleFileNames == null || possibleFileNames.length != 1) {
            throw new SimplexPersistenceException("Ambiguous object item with id:" + id + ", found " + (possibleFileNames == null ? 0 : possibleFileNames.length) + " items with that id.");
        }
        return internalLoad(possibleFileNames[0]);
    }

    public List<Object> loadAll(final Class itemClazz) throws SimplexPersistenceException {
        String[] possibleFileNames = new File(store).list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(realItemClassName(itemClazz));
            }
        });
        List<Object> loadedObjects = new ArrayList<Object>();
        for (String possibleFileName : possibleFileNames) {
            String idPart = possibleFileName.split("@")[1].split("\\.")[0];
            if (!idPart.trim().isEmpty()) {
                loadedObjects.add(this.load(itemClazz, Integer.parseInt(idPart)));
            }
        }
        return loadedObjects;
    }

    public Object loadSingleton(String identifier) throws SimplexPersistenceException {
        return internalLoad(identifier + ".xml");
    }

    public Object saveSingleton(String identifier, Object object) throws SimplexPersistenceException {
        return internalSave(object, identifier + ".xml");
    }

    private Object internalSave(Object item, String fileName) throws SimplexPersistenceException {
        final File directory = new File(store);
        final String pathName = concatenateStorePathAndFilename(fileName);

        directory.mkdirs();
        FileWriter out = null;
        try {
            out = new FileWriter(pathName, false);
            xstream.setClassLoader(currentContextClassLoader());
            xstream.toXML(item, out);
        } catch (FileNotFoundException e) {
            throw new SimplexPersistenceException("Simplex: could not find file" + pathName, e);
        } catch (IOException e) {
            throw new SimplexPersistenceException("Simplex: could not create file: " + pathName, e);
        } finally {
            closeFileWriter(out);
        }
        return item;
    }

    private Object internalLoad(String fileName) throws SimplexPersistenceException {
        Object item = null;
        final File file = new File(concatenateStorePathAndFilename(fileName));
        if (file.exists()) {
            FileReader ins = null;
            try {
                ins = new FileReader(file);
                xstream.setClassLoader(currentContextClassLoader());
                item = xstream.fromXML(ins);
            } catch (FileNotFoundException e) {
                throw new SimplexPersistenceException("Simplex: could not find file" + file.getAbsolutePath(), e);
            } finally {
                closeFileReader(ins);
            }
        } else {
            System.err.println("Simplex: file does not exist, possibly deleted: " + file.getAbsolutePath());
        }

        return item;
    }


    public void delete(Object object, boolean... cascade) throws SimplexPersistenceException {
        Number idFrom = idFieldReflectionDelegate.getIdFrom(object);
        Class clazz = object.getClass();
        delete(clazz, idFrom, cascade);
    }

    public void delete(Class clazz, Number id, boolean... cascade) throws SimplexPersistenceException {
        final String fileNameWithId = createFileNameWithId(clazz, id);
        final File file = new File(concatenateStorePathAndFilename(fileNameWithId));
        if (file.exists()) {
            if (!file.delete()) {
                throw new SimplexPersistenceException("Simplex: could not delete object; " + fileNameWithId);
            }
        } else {
            System.err.println("Simplex: file already deleted " + fileNameWithId);
        }
    }

    protected String createFileNameWithId(Class itemClazz, Number id) {
        String itemClazzName = realItemClassName(itemClazz);
        return itemClazzName + "@" + id + ".xml";
    }

    private String realItemClassName(Class itemClazz) {
        String itemClazzName = itemClazz.getName();
        if (Enhancer.isEnhanced(itemClazz)) {
            itemClazzName = itemClazzName.substring(0, itemClazzName.indexOf("$$EnhancerByCGLIB$$"));
        }
        return itemClazzName;
    }

    private String concatenateStorePathAndFilename(String fileName) {
        return store + "/" + fileName;
    }


    public void clean() {
        final File file = new File(store);
        System.out.println("removing store in " + file.getAbsolutePath());
        if (!deleteDir(file)) {
            System.err.println("Simple-x could not remove directory " + file.getAbsolutePath() + ":(");
        }
    }

}