package org.corbym.simplex.persistence.converters;

import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import org.corbym.simplex.persistence.annotations.LazyLoad;
import org.corbym.simplex.persistence.marshalling.MarshallingListener;
import org.corbym.simplex.persistence.util.CurrentContextHelper;
import org.corbym.simplex.persistence.util.FieldReflectionDelegate;

import java.lang.reflect.Field;

public class ParentFieldTrackingMarshalListener implements MarshallingListener {
    private final Mapper mapper;
    private final ThreadLocal<Field> parentFieldThreadLocal = new ThreadLocal<Field>();
    private final FieldReflectionDelegate fieldReflectionDelegate;

    public ParentFieldTrackingMarshalListener() {
        mapper = new DefaultMapper(CurrentContextHelper.currentContextClassLoader());
        fieldReflectionDelegate = new FieldReflectionDelegate();
    }

    public ParentFieldTrackingMarshalListener(Mapper mapper) {
        this.mapper = mapper;
        fieldReflectionDelegate = new FieldReflectionDelegate();
    }

    public void notifyOfValidReference(Object referenceKey) {
        if (referenceKey instanceof Path) {
            Path path = (Path) referenceKey;
            Path parent = path.apply(new Path(".."));
            String parentCandidateAsString = findCurrentNode(parent.toString());
            if (parentCandidateAsString.length() > 0) {
                try {
                    Class parentClass = mapper.realClass(parentCandidateAsString);
                    if (parentClass != null) {
                        String fieldCandidate = findCurrentNode(referenceKey.toString());
                        Field declaredField = fieldReflectionDelegate.findDeclaredField(parentClass, fieldCandidate);
                        if (declaredField != null) {
                            setParentField(declaredField);
                        }
                    }
                } catch (CannotResolveClassException e) {
                    // ignore
                }
            } else {
                setParentField(null);
            }
        }
    }

    private String findCurrentNode(String pathAsString) {
        return pathAsString.substring(pathAsString.lastIndexOf("/") + 1, pathAsString.length());
    }

    public void setParentField(Field field) {
        parentFieldThreadLocal.set(field);
    }

    public Field getParentField() {
        return parentFieldThreadLocal.get();
    }

    boolean isFieldLazyLoadable() {
        Field parentField = getParentField();
        return (parentField != null && parentField.getAnnotation(LazyLoad.class) != null);
    }
}