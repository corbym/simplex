package org.corbym.groovymud.persistence

import net.sf.cglib.proxy.Enhancer
import org.corbym.simplex.persistence.stubs.ObjectWithLazyLoaderAnnotatedField
import org.corbym.simplex.persistence.stubs.SingletonWithLazyLoadField
import org.corbym.simplex.persistence.stubs.SomeObjectWithDefFieldAndId
import org.corbym.simplex.persistence.stubs.SomeObjectWithId
import org.junit.Test

class LazyLoaderTest extends PersistenceIntegerationTest {

    @Test
    void "an id object as a lazy load member of another should be loaded as a proxy"() {
        SomeObjectWithId idObject = new SomeObjectWithId()
        ObjectWithLazyLoaderAnnotatedField other = new ObjectWithLazyLoaderAnnotatedField(somefield: idObject)
        dao.save(other)

        assert other.id != null

        def loaded = assertLoadObject(other)
        assert Enhancer.isEnhanced(loaded.somefield.class)
        assertThatFilenameExists idObject
        assertThatFilenameExists other


    }


    @Test
    void "an object without an id containing a lazyload field should have the object loaded as a proxy"() {
        ObjectWithLazyLoaderAnnotatedField other = new ObjectWithLazyLoaderAnnotatedField()
        SingletonWithLazyLoadField one = new SingletonWithLazyLoadField(somefield: other)
        dao.saveSingleton("lazy", one)

        assert other.id != null

        def loaded = dao.loadSingleton("lazy")
        assert Enhancer.isEnhanced(loaded.somefield.class)
        assertThatFilenameExists other
    }

    @Test
    void "lazy loader should be able to cast like an interface and class associated with it"() {
        final two = new SomeObjectWithId()
        final one = new ObjectWithLazyLoaderAnnotatedField(somefield: two)
        dao.save(one)
        ObjectWithLazyLoaderAnnotatedField loaded = dao.load(one.class, one.id)
        assert Enhancer.isEnhanced(loaded.somefield.class)
        SomeObjectWithId fieldObject = loaded.somefield
        GroovyObject groo = loaded.somefield
    }

    @Test
    void "an object containing a lazyload collection should have the collection loaded as a proxy"() {
        List list = []
        ObjectWithLazyLoaderAnnotatedField one = new ObjectWithLazyLoaderAnnotatedField(somefield: list)

        dao.save(one)

        def loaded = dao.load(ObjectWithLazyLoaderAnnotatedField, one.id)
        assert Enhancer.isEnhanced(loaded.somefield.class)
    }

    @Test
    void "an enhanced object should only be proxied once"() {
        SomeObjectWithDefFieldAndId other = new SomeObjectWithDefFieldAndId()
        ObjectWithLazyLoaderAnnotatedField one = new ObjectWithLazyLoaderAnnotatedField(somefield: other)
        dao.save(one)
        assert other.id != null

        def expected = dao.load(ObjectWithLazyLoaderAnnotatedField, one.id)
        dao.save(expected)
        def actual = dao.load(ObjectWithLazyLoaderAnnotatedField, expected.id)
        assertXStreamEquals(expected, actual)
    }
}