package org.corbym.groovymud.persistence

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.DomDriver
import org.corbym.simplex.persistence.stubs.SomeObjectWithDefFieldAndId
import org.corbym.simplex.persistence.stubs.SomeObjectWithId
import org.corbym.simplex.persistence.stubs.SomeObjectWithManyFieldsAndId
import org.corbym.simplex.persistence.stubs.SomeObjectWithoutId
import org.junit.Test

class CollectionPersistenceIntegrationTest extends PersistenceIntegerationTest {
    @Test
    void "xstream test"() {
        def xstream = new XStream(new DomDriver());
        xstream.toXML(Collections.synchronizedMap(new HashMap()));
    }

    @Test
    void "an object with an id field should be saved as a separate file when it's in a list of another"() {
        SomeObjectWithDefFieldAndId idObject = new SomeObjectWithDefFieldAndId()
        SomeObjectWithDefFieldAndId other = new SomeObjectWithDefFieldAndId(somefield: [idObject])
        dao.save(other)

        assert idObject.id != null

        def loaded = dao.load(SomeObjectWithDefFieldAndId, idObject.id)
        assert loaded, "object should be loaded but was $loaded"
        def otherThatContainedIdObject = dao.load(SomeObjectWithDefFieldAndId, other.id)
        assert otherThatContainedIdObject.somefield.size() == 1
        assertLoadObject idObject
    }

    @Test
    void "must be able to save one object without an id inside a list of another"() {
        def something = new SomeObjectWithDefFieldAndId(somefield: [])
        def newLoc = new SomeObjectWithoutId(somefield: "blah")

        something.somefield.add(newLoc)
        dao.save(something)
        def locX = dao.load(something.class, something.id)
        assert locX.id == something.id
        assert locX.somefield.size() > 0
        assert locX.somefield[0].somefield == "blah"
    }

    @Test
    void "an object with an id field should be saved as a separate file when it's in a map value field of another"() {
        final idObject = new SomeObjectWithId()
        final someObject = new SomeObjectWithDefFieldAndId(somefield: ["thingy": idObject])
        someObject.somefield.put "thing2", new SomeObjectWithoutId()
        dao.save(someObject)

        assert someObject.id != null
        assert idObject != null

        def loaded = dao.load(SomeObjectWithId, idObject.id)
        assert loaded, "object should be loaded but was $loaded"
        def otherThatContainedIdObject = dao.load(SomeObjectWithDefFieldAndId, someObject.id)
        assert otherThatContainedIdObject.somefield.values().size() == 2
        assertThatFilenameExists idObject

    }

    @Test
    void "an object with an id field should be saved as a reference when it's in a treemap value field of another"() {
        final idObject = new SomeObjectWithId()
        final someObject = new SomeObjectWithDefFieldAndId(somefield: ["thingy": idObject] as TreeMap)
        someObject.somefield.put "thing2", new SomeObjectWithoutId()
        dao.save(someObject)

        assert someObject.id != null
        assert idObject.id != null

        def loaded = assertLoadObject(idObject)
        def otherThatContainedIdObject = assertLoadObject(someObject)
        assert otherThatContainedIdObject.somefield.values().size() == 2
        assertThatFilenameExists(idObject)
    }

    @Test
    void "an object with an id field should be saved as a reference when it's in a treemapped treemap value field of another"() {
        final one = new SomeObjectWithId()
        final two = new SomeObjectWithDefFieldAndId(somefield: ["thingy": ["thong": one] as TreeMap] as TreeMap)
        two.somefield.put "thing2", new SomeObjectWithoutId()
        dao.save(two)

        assert two.id != null
        assert one.id != null

        def loaded = assertLoadObject(one)
        def otherThatContainedIdObject = assertLoadObject(two)
        assert otherThatContainedIdObject.somefield.values().size() == 2
        assertThatFilenameExists(one)
    }

    @Test
    void "an object with an id field should be saved as a reference when it's in a mapped treemap value field of another"() {
        final one = new SomeObjectWithId()
        final map = ["thong": one] as TreeMap
        final two = new SomeObjectWithDefFieldAndId(somefield: ["thingy": map])
        final manyFields = new SomeObjectWithManyFieldsAndId()
        two.somefield.put "thing2", new SomeObjectWithoutId()
        map.put "many", manyFields
        dao.save(two)

        assert two.id != null
        assert one.id != null

        assertLoadObject(one)

        SomeObjectWithDefFieldAndId otherThatContainedIdObject = assertLoadObject(two)
        assert otherThatContainedIdObject.somefield.values().size() == 2
        assertThatFilenameExists(one)
    }
}
