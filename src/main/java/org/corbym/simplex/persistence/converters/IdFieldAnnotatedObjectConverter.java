package org.corbym.simplex.persistence.converters;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import net.sf.cglib.proxy.Enhancer;
import org.corbym.simplex.persistence.SimplexDao;
import org.corbym.simplex.persistence.SimplexPersistenceException;
import org.corbym.simplex.persistence.proxy.IdFieldAnnotatedLazyLoader;
import org.corbym.simplex.persistence.util.FieldReflectionDelegate;

import java.lang.reflect.Field;

public class IdFieldAnnotatedObjectConverter implements Converter {
    private final SimplexDao dao;
    private final FieldReflectionDelegate fieldReflectionDelegate;
    private final Mapper mapper;
    private final FilterableConverterLookup lookup;
    private final ParentFieldTrackingMarshalListener parentFieldTrackingMarshalListener;

    public IdFieldAnnotatedObjectConverter(SimplexDao dao, Mapper mapper, FilterableConverterLookup lookup, ParentFieldTrackingMarshalListener parentFieldTrackingMarshalListener, FieldReflectionDelegate fieldReflectionDelegate) {
        this.mapper = mapper;
        this.lookup = lookup;
        this.dao = dao;
        this.fieldReflectionDelegate = fieldReflectionDelegate;
        this.parentFieldTrackingMarshalListener = parentFieldTrackingMarshalListener;
    }

    public boolean canConvert(Class clazz) {
        return fieldReflectionDelegate.findIdAnnotatedField(clazz) != null;
    }


    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (embedded(value)) {
            marshalEmbeddedObject(value, writer, context);
        } else {
            marshalObject(value, writer, context);
        }
    }

    private boolean embedded(Object value) {
        boolean hasIdField = hasIdField(value);
        Field field = parentFieldTrackingMarshalListener.getParentField();
        return field != null && hasIdField;
    }

    private boolean hasIdField(Object value) {
        return fieldReflectionDelegate.findIdAnnotatedField(value.getClass()) != null;
    }

    private void marshalEmbeddedObject(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        final boolean inCollection = isParentACollectionTypeField();
        try {
            Field savedParentField = parentFieldTrackingMarshalListener.getParentField();
            parentFieldTrackingMarshalListener.setParentField(null);
            value = dao.save(value);
            parentFieldTrackingMarshalListener.setParentField(savedParentField);
        } catch (SimplexPersistenceException e) {
            throw new XStreamException("cannot marshal object:" + value, e);
        }
        writer.addAttribute("embedded", "" + getIdFrom(value));
        if (parentFieldTrackingMarshalListener.isFieldLazyLoadable() && !Enhancer.isEnhanced(value.getClass())) {
            writer.addAttribute("lazyload", "true");
        }
        if (!inCollection) {
            writer.addAttribute(mapper.aliasForAttribute("class"), value.getClass().getName());
        }
    }

    private boolean isParentACollectionTypeField() {
        return parentFieldTrackingMarshalListener.getParentField() != null && CollectionUtils.isCollectionClass(parentFieldTrackingMarshalListener.getParentField().getType());
    }

    private void marshalObject(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        Converter converter = lookup.lookupConverterForTypeExcluding(value.getClass(), this.getClass());
        if (converter.canConvert(value.getClass())) {
            converter.marshal(value, writer, context);
        }
    }

    private Number getIdFrom(Object value) {
        return fieldReflectionDelegate.getIdFrom(value);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Object obj;
        do {
            final Class clazz = HierarchicalStreams.readClassType(reader, mapper);
            final String embeddedAttr = reader.getAttribute("embedded");
            if (embeddedAttr != null) {
                long id = Long.parseLong(embeddedAttr);
                obj = unmarshalEmbedded(clazz, id, reader);
            } else {
                Converter converter = lookup.lookupConverterForTypeExcluding(clazz, this.getClass());
                obj = converter.unmarshal(reader, context);
            }
        } while (reader.hasMoreChildren());
        return obj;
    }

    private Object unmarshalEmbedded(Class clazz, long id, HierarchicalStreamReader reader) {
        Object obj;
        final String lazyAttr = reader.getAttribute("lazyload");
        if (lazyAttr == null) {
            try {
                obj = dao.load(clazz, id);
            } catch (SimplexPersistenceException e) {
                throw new XStreamException("cannot unmarshal object with class: " + clazz + " and id:" + id, e);
            }
        } else {
            obj = Enhancer.create(clazz, clazz.getInterfaces(), new IdFieldAnnotatedLazyLoader(id, dao, clazz));
        }
        return obj;
    }

    public ParentFieldTrackingMarshalListener getParentFieldTrackingMarshalListener() {
        return parentFieldTrackingMarshalListener;
    }

}
