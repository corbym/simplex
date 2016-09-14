package org.corbym.simplex.persistence.util;

public class CurrentContextHelper {

    public static ClassLoader currentContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}