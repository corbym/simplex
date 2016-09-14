package org.corbym.simplex.persistence.converters

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import org.junit.Test

public class FilterableConverterLookupTest {
    FilterableConverterLookup underTest = new FilterableConverterLookup();
    final converter = [marshall: {}, unmarshal: {}, canConvert: {clazz -> true}] as Converter
    final anotherConverter = new Converter() {
        void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        boolean canConvert(Class type) {
            return true //To change body of implemented methods use File | Settings | File Templates.
        }

    }
    final yetAnotherConverter = new Converter() {
        void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        boolean canConvert(Class type) {
            return true  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @Test
    void "lookup converter for class returns the converter"() {
        underTest.registerConverter(converter, 1)
        assert underTest.lookupConverterForType(Object) == converter;
    }

    @Test
    void "lookup converter excluding one other"() {
        underTest.registerConverter(converter, 1)
        underTest.registerConverter(anotherConverter, 2)
        assert underTest.lookupConverterForTypeExcluding(Object, anotherConverter.class) == converter;
    }

    @Test
    void "lookup converter excluding two others"() {
        underTest.registerConverter(converter, 1)
        underTest.registerConverter(anotherConverter, 2)
        underTest.registerConverter(yetAnotherConverter, 3)
        assert underTest.lookupConverterForTypeExcluding(Object, anotherConverter.class, yetAnotherConverter.class) == converter;
    }
}
