package org.corbym.simplex.persistence.util;

import org.corbym.simplex.persistence.annotations.Id;
import org.corbym.simplex.persistence.annotations.LazyLoad;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class FieldReflectionDelegate {


    public Number getIdFrom(Object value) {
        try {
            final Field idAnnotatedField = findIdAnnotatedField(value.getClass());
            assert idAnnotatedField.getType().isAssignableFrom(Number.class);
            idAnnotatedField.setAccessible(true);
            Number number = (Number) idAnnotatedField.get(value);
            idAnnotatedField.setAccessible(false);
            return number;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("simplex error: cannot get id value", e);
        }
    }

    public Field findIdAnnotatedField(Class clazz) {
        return findFieldWithAnnotation(clazz, Id.class);
    }

    private Field findFieldWithAnnotation(Class objectOrSuperClass, Class annotationClass) {
        Field found = null;
        do {
            List<Field> fieldList = Arrays.asList(objectOrSuperClass.getDeclaredFields());
            for (Field field : fieldList) {
                if (field.getAnnotation(annotationClass) != null) {
                    found = field;
                    break;
                }
            }
            if (found != null) {
                break;
            }
            objectOrSuperClass = objectOrSuperClass.getSuperclass();
        } while (objectOrSuperClass != null);
        return found;
    }


    public void setIdUsing(Object item, Number id) {
        final Field field = findIdAnnotatedField(item.getClass());
        try {
            field.setAccessible(true);
            field.set(item, id);
            field.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("cannot set id on item " + item.toString() + " id: " + id, e);
        }
    }

    public String getGuessedSetterForIdField(Class object) {
        Field annotatedField = findIdAnnotatedField(object);
        String getter = null;
        if (annotatedField != null) {
            String name = annotatedField.getName();
            getter = "get" + toCamelCase(name);
            try {
                object.getDeclaredMethod(getter);
            } catch (NoSuchMethodException e) {
                System.err.println("simplex warning: could not find getter method on object. guessed method might not exist...");
            }
        }
        return getter;
    }

    private String toCamelCase(String string) {
        String firstLetter = string.substring(0, 1);
        String remainder = string.substring(1);
        return firstLetter.toUpperCase() + remainder.toLowerCase();
    }

    public String getFieldAnnotatedWithIdFieldName(Class actualClass) {
        return findIdAnnotatedField(actualClass).getName();
    }


    public Field findDeclaredField(Class clazz, String fieldCandidate) {
        Class objectOrSuperClass = clazz;
        Field found = null;
        do {
            try {
                found = objectOrSuperClass.getDeclaredField(fieldCandidate);
            } catch (NoSuchFieldException e) {
                //ignore
            }
            if (found != null) {
                break;
            }
            objectOrSuperClass = objectOrSuperClass.getSuperclass();
        } while (objectOrSuperClass != null);
        return found;
    }

    public Field findLazyLoadAnnotatedField(Class clazz) {
        return findFieldWithAnnotation(clazz, LazyLoad.class);
    }
}