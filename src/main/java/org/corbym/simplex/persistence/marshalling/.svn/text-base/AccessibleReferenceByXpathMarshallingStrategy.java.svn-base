package org.corbym.simplex.persistence.marshalling;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy;
import com.thoughtworks.xstream.core.ReferenceByXPathUnmarshaller;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.core.TreeUnmarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.HashSet;
import java.util.Set;

public class AccessibleReferenceByXpathMarshallingStrategy extends ReferenceByXPathMarshallingStrategy {
    private final int myMode;
    private final Set listeners = new HashSet();

    public AccessibleReferenceByXpathMarshallingStrategy(int myMode) {
        super(myMode);
        this.myMode = myMode;
    }

    protected TreeUnmarshaller createUnmarshallingContext(Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper) {
        return new ReferenceByXPathUnmarshaller(root, reader, converterLookup, mapper);
    }

    protected TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
        return new AccessibleReferenceByXPathMarshaller(writer, converterLookup, mapper, myMode, listeners);
    }

    public void registerListener(MarshallingListener listener) {
        listeners.add(listener);
    }
}
