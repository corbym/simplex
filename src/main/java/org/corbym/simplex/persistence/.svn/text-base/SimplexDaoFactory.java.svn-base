package org.corbym.simplex.persistence;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimplexDaoFactory {
    private static Map<String, SimplexDao> singleInstances = Collections.synchronizedMap(new HashMap<String, SimplexDao>());
    protected static String DEFAULT_STORE_LOCATION = "simplex-store/";

    public static SimplexDao getInstance() throws SimplexPersistenceException {
        return getInstance(DEFAULT_STORE_LOCATION);
    }

    public static SimplexDao getInstance(String storeLocation) throws SimplexPersistenceException {
        SimplexDao simplexDao;
        synchronized (singleInstances) {
            if (!singleInstances.containsKey(storeLocation)) {
                simplexDao = new SimplexDao(storeLocation);
                singleInstances.put(storeLocation, simplexDao);
            } else {
                simplexDao = singleInstances.get(storeLocation);
            }
        }
        return simplexDao;
    }
}
