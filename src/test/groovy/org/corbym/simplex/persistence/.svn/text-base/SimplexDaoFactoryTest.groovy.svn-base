package org.corbym.simplex.persistence

import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.LazyLoader
import org.corbym.simplex.persistence.stubs.SomeObjectWithId
import org.junit.Test
import static junit.framework.Assert.assertEquals

class SimplexDaoFactoryTest {

    @Test
    void "can change the dao's path for storage"() {
        assert SimplexDaoFactory.getInstance("./daoStore")
    }

    @Test
    void "getting an instance for a store in a second location returns a different instance with two calls"() {
        def dao = SimplexDaoFactory.getInstance("./daoStore")
        def dao2 = SimplexDaoFactory.getInstance("./otherStore")
        assert dao != dao2
    }

    @Test
    void "check filename excludes proxy name if class is enhanced"() {
        def dao = SimplexDaoFactory.getInstance()
        String expected = dao.createFileNameWithId(SomeObjectWithId.class, 1)
        def enhanced = Enhancer.create(SomeObjectWithId, [loadObject: {new SomeObjectWithId(id: 1)}] as LazyLoader)
        String actual = dao.createFileNameWithId(enhanced.getClass(), 1)

        assertEquals(expected, actual)
    }
}
