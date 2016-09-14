package org.corbym.simplex.persistence.converters;

import java.util.Collection;
import java.util.Map;

public class CollectionUtils {

    public static boolean isCollectionClass(Class clazz) {
        return Collection.class.isAssignableFrom(clazz) ||
                clazz.isAssignableFrom(Collection.class) ||
                Map.class.isAssignableFrom(clazz) ||
                clazz.isAssignableFrom(Map.class);
    }
}