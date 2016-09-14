package org.corbym.groovymud.persistence

import org.corbym.simplex.persistence.SimplexPersistenceException
import org.junit.Ignore
import org.junit.Test
import org.corbym.simplex.persistence.stubs.*
import static org.hamcrest.core.IsEqual.equalTo
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertThat
import static org.junit.internal.matchers.IsCollectionContaining.hasItem

class GeneralPersistenceIntegrationTest extends PersistenceIntegerationTest {
    @Test
    void "dao can save a list of objects and load all objects of a certain class"() {
        SomeObjectWithDefFieldAndId objectOne = new SomeObjectWithDefFieldAndId(somefield: "blah")
        SomeObjectWithDefFieldAndId objectTwo = new SomeObjectWithDefFieldAndId(somefield: "blah")

        dao.saveAll(objectOne, objectTwo)

        def loadedList = dao.loadAll(SomeObjectWithDefFieldAndId)

        assertThat loadedList, hasItem(objectOne)
    }

    @Test
    void "dao can load an object with the id only"() {
        SomeObjectWithDefFieldAndId objectOne = new SomeObjectWithDefFieldAndId(somefield: "blah")
        dao.save(objectOne)
        def deserialized = dao.load(objectOne.id)

        assertThat(objectOne, equalTo(deserialized));
    }

    @Test(expected = SimplexPersistenceException)
    void "dao throws exception if object id not found."() {
        dao.load(1);
    }

    @Test
    void "an object with an id closure should be persistable and deserializable"() {
        SomeObjectWithDefFieldAndId guardedContainer = new SomeObjectWithDefFieldAndId()
        guardedContainer.somefield = {item ->
            false
        }
        dao.save(guardedContainer)
        def deserialized = dao.load(SomeObjectWithDefFieldAndId, guardedContainer.id)

        assert deserialized.somefield != null

        deserialized.somefield.call()
    }

    @Test
    void "an object with an id field should be saved as a separate file when it's a field of another"() {
        SomeObjectWithId idObject = new SomeObjectWithId()
        SomeObjectWithId other = new SomeObjectWithId(somefield: idObject)
        dao.save(other)

        assert idObject.id != null

        def loaded = dao.load(SomeObjectWithId, idObject.id)
        assert loaded, "object should be loaded but was $loaded"
        def otherThatContainedIdObject = dao.load(SomeObjectWithId, other.id)
        assert otherThatContainedIdObject.somefield
        assertThatFilenameExists(idObject)
    }

    @Test
    void "an object with an inherited id field should be saved as a separate file when it's a field of another"() {
        def two = new SomeObjectWithId()
        def one = new ObjectWithInheritedId(somefield: two, someotherfield: "foo")

        dao.save(one)

        assertThatFilenameExists(two)
        assertThatFilenameExists one

        def loaded = assertLoadObject(one)
        def otherThatContainedIdObject = assertLoadObject(two)

        assert loaded.somefield
    }

    @Test
    void "both objects with an id field should be saved as a separate file when they're in fields of another"() {
        SomeObjectWithId idObject = new SomeObjectWithId()
        SomeObjectWithId idObject2 = new SomeObjectWithId()
        SomeObjectWithTwoFieldsAndId other = new SomeObjectWithTwoFieldsAndId(somefield: idObject, another: idObject2)
        dao.save(other)
        assertThatFilenameExists(idObject)
        assertThatFilenameExists(idObject2)

        assert idObject.id != null
        assert idObject2.id != null

        def loaded = dao.load(SomeObjectWithId, idObject.id)
        assert loaded, "object should be loaded but was $loaded"
        loaded = dao.load(SomeObjectWithId, idObject2.id)
        assert loaded, "object should be loaded but was $loaded"

        def otherThatContainedIdObject = dao.load(SomeObjectWithTwoFieldsAndId, other.id)
        assert otherThatContainedIdObject.somefield
        assert otherThatContainedIdObject.another

    }

    @Test
    void "all fields of an object should be saved correctly"() {
        SomeObjectWithId one = new SomeObjectWithId()
        SomeObjectWithoutId two = new SomeObjectWithoutId();
        SomeObjectWithId four = new SomeObjectWithId()
        ObjectWithLazyLoaderAnnotatedField three = new ObjectWithLazyLoaderAnnotatedField();
        SomeObjectWithTwoFieldsAndId other = new SomeObjectWithTwoFieldsAndId(somefield: one, another: four)

        SomeObjectWithManyFieldsAndId manyFieldsAndId = new SomeObjectWithManyFieldsAndId(one: one, two: two, three: three, four: four)
        dao.save(manyFieldsAndId)
        assertThatFilenameExists(one)
        assertThatFilenameExists(three)
        assertThatFilenameExists(four)

        assertLoadObject(one)
        assertLoadObject(three)
        assertLoadObject(four)
        assertLoadObject(manyFieldsAndId)

    }



    @Test
    void "loading an object where an embedded object does not exist does not fail"() {
        final objectWithId = new SomeObjectWithId()
        final otherObjectWithId = new SomeObjectWithId()

        objectWithId.somefield = otherObjectWithId
        dao.save(objectWithId)

        dao.delete(otherObjectWithId)

        dao.load(SomeObjectWithId, objectWithId.id)

    }


    @Test
    void "deleting an object with an id should remove it"() {
        SomeObjectWithId objectWithId = new SomeObjectWithId()
        dao.save(objectWithId)

        dao.delete(SomeObjectWithId, objectWithId.id)
        assertNull dao.load(SomeObjectWithId, objectWithId.id)
    }

    @Test
    void "deleting an object with an id and an embedded object should remove only the first"() {
        final first = new SomeObjectWithId()
        final second = new SomeObjectWithId()

        first.somefield = second
        dao.save(first)

        dao.delete(SomeObjectWithId, first.id)
        assertNull dao.load(SomeObjectWithId, first.id)

        assertLoadObject second
    }

    @Test
    @Ignore
    //TODO: this needs to be implemented
    void "deleting an object with an id in cascade mode and an embedded object should remove all of them"() {
        final objectWithId = new SomeObjectWithId()
        final otherObjectWithId = new SomeObjectWithId()

        objectWithId.somefield = otherObjectWithId
        dao.save(objectWithId)

        dao.delete(SomeObjectWithId, objectWithId.id, true)
        assertNull dao.load(SomeObjectWithId, objectWithId.id)
        assertNull dao.load(SomeObjectWithId, otherObjectWithId.id)
    }

    @Test
    void "saving a singleton saves the id object in an external file"() {
        SomeObjectWithId two = new SomeObjectWithId()
        SomeObjectWithoutId one = new SomeObjectWithoutId(somefield: two)

        dao.saveSingleton("one", one)

        assertLoadObject two

    }

    @Test
    void "objects should have the same xml when saved more than once without being changed"() {
        SomeObjectWithId idObject = new SomeObjectWithId()
        ObjectWithLazyLoaderAnnotatedField other = new ObjectWithLazyLoaderAnnotatedField(somefield: idObject)
        dao.save(other)
        def ob1 = xStream.fromXML(assertThatFilenameExists(other).newInputStream())
        dao.save(other)
        def ob2 = xStream.fromXML(assertThatFilenameExists(other).newInputStream())
        assertXStreamEquals(ob1, ob2)

    }

}
