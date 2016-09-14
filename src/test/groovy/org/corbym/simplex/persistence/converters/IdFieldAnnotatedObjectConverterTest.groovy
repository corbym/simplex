package org.corbym.simplex.persistence.converters

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import com.thoughtworks.xstream.mapper.DefaultMapper
import com.thoughtworks.xstream.mapper.Mapper
import net.sf.cglib.proxy.Enhancer
import org.corbym.simplex.persistence.SimplexDao
import org.corbym.simplex.persistence.stubs.ObjectWithLazyLoaderAnnotatedField
import org.corbym.simplex.persistence.stubs.SomeObjectWithId
import org.corbym.simplex.persistence.stubs.SomeObjectWithoutAnnotationOnTheIdField
import org.corbym.simplex.persistence.stubs.SomeObjectWithoutId
import org.corbym.simplex.persistence.util.FieldReflectionDelegate
import org.junit.Test
import static org.junit.Assert.assertFalse

class IdFieldAnnotatedObjectConverterTest {
    def object = new SomeObjectWithId()

    Mapper mapper = new DefaultMapper(this.getClass().classLoader)
    boolean saveCalled = false;
    long internalId = 0
    def dao = [load: {clazz, id ->
        object
    }, save: {
        saveCalled = true
        object.id = internalId++
        return object
    }] as SimplexDao

    def reader = [
            getNodeName: { "somefield"},
            getAttribute: { attribute ->
                if (attribute == "embedded") {
                    return "123"
                }
                if (attribute == "lazyload") {
                    return "true"
                }
                if (attribute == "class") {
                    return "org.corbym.simplex.persistence.stubs.SomeObjectWithId"
                }
            },
            hasMoreChildren: {
                false
            }
    ] as HierarchicalStreamReader

    def converterThatWasFound = [
            canConvert: {clazz -> true },
            unmarshal: { rdr, ctx ->
                called = true;
                return object
            }] as Converter

    final safeConverterLookupDelegate = [lookupConverterForTypeExcluding: { clazz -> converterThatWasFound }] as FilterableConverterLookup
    final converter = new IdFieldAnnotatedObjectConverter(dao, mapper, null, new ParentFieldTrackingMarshalListener(mapper), new FieldReflectionDelegate())

    @Test
    void "can convert class with annotated id in it"() {

        assert converter.canConvert(SomeObjectWithId), "should be able to convert a class with an annotated id field on it"
    }

    @Test
    void "cannot convert class without an id field in it"() {

        assertFalse("should not be able to convert an object without an id field", converter.canConvert(SomeObjectWithoutId))
    }

    @Test
    void "cannot convert class without an annotation on the id field "() {

        assertFalse("should not be able to convert an object without an id field", converter.canConvert(SomeObjectWithoutAnnotationOnTheIdField))
    }

    @Test
    void "object with out lazyload attribute should be loaded using a converter"() {
        def context = [:] as UnmarshallingContext
        def unmarshalled = converter.unmarshal(reader, context)
        assert unmarshalled == object
    }

    @Test
    void "unmarshalled object with a lazyload attribute should be proxied"() {
        def context = [:] as UnmarshallingContext
        def unmarshalled = converter.unmarshal(reader, context)
        assert Enhancer.isEnhanced(unmarshalled.class)
    }

    @Test
    void "any object with a lazyload annotation should be persisted with lazyload attribute"() {
        Map attributes = [:]
        object = new ObjectWithLazyLoaderAnnotatedField();
        def context = [get: {true}] as MarshallingContext
        def writer = [startNode: { assert it == object.getClass().getName() },
                addAttribute: {name, value -> attributes.put(name, value)},
                endNode: {}] as HierarchicalStreamWriter
        def tracker = converter.getParentFieldTrackingMarshalListener()
        tracker.setParentField(ObjectWithLazyLoaderAnnotatedField.getDeclaredField("somefield"))
        converter.marshal(object, writer, context)
        assert saveCalled
        assert attributes.containsKey("embedded")
        assert attributes.containsKey("lazyload")
    }
}
