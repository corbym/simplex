package org.corbym.simplex.persistence.converters

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import com.thoughtworks.xstream.mapper.Mapper
import net.sf.cglib.proxy.Enhancer
import org.corbym.simplex.persistence.stubs.ObjectWithLazyLoaderAnnotatedField
import org.junit.Test
import static org.junit.Assert.fail

class CollectionConverterTest {
    def marshalCalled = false

    final Class collectionClazz = List.class
    final reader = [getAttribute: {attributeName -> "true"},
            getNodeName: {"nodeName"}] as HierarchicalStreamReader
    final listener = [:] as ParentFieldTrackingMarshalListener
    final converter = new CollectionConverter(null, null, null)
    final converterFound = [marshal: {ob, writer, context ->
        assert ob instanceof List
        marshalCalled = true
    }] as Converter
    final safeLookup = [lookupConverterForTypeExcluding: {clazz, converter ->
        return converterFound
    }] as FilterableConverterLookup
    final mapper = [
            aliasForSystemAttribute: {resolveTo -> "system"},
            realClass: {thing -> List},
            defaultImplementationOf: {thing -> ArrayList}
    ] as Mapper

    @Test
    void "collections can be converted"() {
        def collections = [List, Map, Collection, ArrayList, HashMap, TreeMap, Set]
        collections.each {
            assert converter.canConvert(it), "should be able to convert $it classes"
        }

    }

    @Test
    void "can marshal a list by calling the default converter"() {
        def converter = new CollectionConverter(safeLookup, listener, mapper)
        converter.marshal([], null, null)

        assert marshalCalled
    }


    @Test
    void "marks a collection with lazyload if the parent field is lazyload annotation"() {
        ObjectWithLazyLoaderAnnotatedField.getDeclaredField("somefield")
        def parentFieldListener = [getParentField: {
            ObjectWithLazyLoaderAnnotatedField.getDeclaredField("somefield")
        }] as ParentFieldTrackingMarshalListener

        def converter = new CollectionConverter(safeLookup, parentFieldListener, mapper)

        def writer = [addAttribute: {name, val -> assert name == "lazyload"; assert val == "true"}] as HierarchicalStreamWriter
        converter.marshal([], writer, null)

        assert marshalCalled
    }

    @Test
    void "can unmarshal a list using the default converter"() {
        boolean unmarshallCalled
        def converter
        def converterFound = [unmarshal: {reader, context ->
            unmarshallCalled = true
        }] as Converter
        def safeLookup = [lookupConverterForTypeExcluding: {clazz, ... exclude ->
            return converterFound
        }] as FilterableConverterLookup
        converter = new CollectionConverter(safeLookup, listener, mapper)
        def reader = [getAttribute: {attributeName -> null },
                getNodeName: {"nodeName"}
        ] as HierarchicalStreamReader
        converter.unmarshal(reader, null)

        assert unmarshallCalled
    }

    @Test
    void "marshals the object with the dao when lazyload annotation is present"() {

    }

    @Test
    void "can unmarshal a list with a lazy load parent field as an enhanced proxy"() {
        boolean unmarshallCalled
        def converterFound = [unmarshal: {reader, context ->
            fail("should not call unmarshal")
        }] as Converter
        def safeLookup = [lookupConverterWithTypeExcluding: {clazz, excludes ->
            return converterFound
        }] as FilterableConverterLookup
        def listener = [:] as ParentFieldTrackingMarshalListener
        def converter = new CollectionConverter(safeLookup, listener, mapper)
        def object = converter.unmarshal(reader, null)

        assert Enhancer.isEnhanced(object.class)

    }
}
