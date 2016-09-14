package org.corbym.simplex.persistence.proxy;

import net.sf.cglib.proxy.LazyLoader;
import org.corbym.simplex.persistence.SimplexDao;

public class IdFieldAnnotatedLazyLoader implements LazyLoader {
    final Long objectId;
    final Class actualClass;
    final transient SimplexDao dao;

    public IdFieldAnnotatedLazyLoader(Long objectId, SimplexDao dao, Class actualClass) {
        this.objectId = objectId;
        this.dao = dao;
        this.actualClass = actualClass;
    }

    public Object loadObject() throws Exception {
        return dao.load(actualClass, objectId);
    }
}
