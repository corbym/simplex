package org.corbym.simplex.persistence.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import net.sf.cglib.proxy.Enhancer;
import org.corbym.simplex.persistence.proxy.CollectionLazyLoader;


public class CollectionConverter extends AbstractCollectionConverter {
    private final ParentFieldTrackingMarshalListener parentFieldTrackingMarshalListener;
    private final FilterableConverterLookup lookup;

    public CollectionConverter(FilterableConverterLookup lookup, ParentFieldTrackingMarshalListener listener, Mapper mapper) {
        super(mapper);
        this.lookup = lookup;
        parentFieldTrackingMarshalListener = listener;

    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (parentFieldTrackingMarshalListener.isFieldLazyLoadable()) {
            writer.addAttribute("lazyload", "true");
        }
        Converter converter = lookup.lookupConverterForTypeExcluding(source.getClass(), this.getClass());
        converter.marshal(source, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Class clazz = HierarchicalStreams.readClassType(reader, mapper());

        Object object;
        final boolean lazyloadAttribute = reader.getAttribute("lazyload") != null;
        if (lazyloadAttribute) {
            object = Enhancer.create(clazz, clazz.getInterfaces(), new CollectionLazyLoader());
        } else {
            Class realClass = canConvert(clazz) ? mapper().defaultImplementationOf(clazz) : clazz;
            Converter converter = lookup.lookupConverterForTypeExcluding(realClass, this.getClass());
            object = converter.unmarshal(reader, context);
        }
        return object;
    }

    public boolean canConvert(Class type) {
        return CollectionUtils.isCollectionClass(type);
    }
}
