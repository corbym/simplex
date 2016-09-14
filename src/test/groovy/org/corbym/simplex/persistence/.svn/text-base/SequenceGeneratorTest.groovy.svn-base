package org.corbym.simplex.persistence
//@Grapes(
//    @Grab(group='org.mockito', module='mockito-core', version='1.6')
//)


import org.corbym.simplex.persistence.util.SequenceGenerator
import org.junit.Before
import org.junit.Test

class SequenceGeneratorTest {
    SequenceGenerator generator

    @Before
    void setupGenerator() {
        generator = new SequenceGenerator()
    }

    @Test
    void "id generated for class value should be a number"() {
        assert generator.nextId(SequenceGeneratorTest) instanceof Number, "id should be numeric"
    }

    @Test
    void "id number for class value should start at 1"() {
        final id = generator.nextId(SequenceGeneratorTest)
        assert id == 1, "should start at 1, started at $id"
    }

    @Test
    void "id number for class value should be sequentially incremental"() {
        generator.nextId(SequenceGeneratorTest)
        final id = generator.nextId(SequenceGeneratorTest)
        assert id == 2, "next number should be 2, was "
    }

    @Test
    void "id generated for null value should be a number"() {
        assert generator.nextId() instanceof Number, "id should be numeric"
    }

    @Test
    void "id number for null value should start at 1"() {
        final id = generator.nextId()
        assert id == 1, "should start at 1, started at $id"
    }

    @Test
    void "id number for null value should be sequentially incremental"() {
        generator.nextId()
        final id = generator.nextId()
        assert id == 2, "next number should be 2, was "
    }

    @Test
    void "id number should be unique per class"() {
        final idInteger = generator.nextId(Integer)
        final idNumber = generator.nextId(Number)

        assert idInteger == idNumber
        idNumber = generator.nextId(Number)
        idInteger = generator.nextId(Integer)
        assert idNumber == 2
        assert idInteger == 2
    }
}
