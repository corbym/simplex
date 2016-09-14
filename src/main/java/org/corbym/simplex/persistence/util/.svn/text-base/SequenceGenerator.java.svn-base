package org.corbym.simplex.persistence.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SequenceGenerator {
    private final Map ids;

    public SequenceGenerator() {
        ids = Collections.synchronizedMap(new HashMap());
    }

    public Long nextId() {
        return nextId(null);
    }

    public Long nextId(Class clazz) {
        Long number = (Long) ids.get(clazz);
        if (number == null) {
            synchronized (ids) {
                number = (Long) ids.get(clazz);
                if (number == null) {
                    number = 0L;
                }
            }
        }
        ids.put(clazz, ++number);
        return (Long) ids.get(clazz);
    }

}
