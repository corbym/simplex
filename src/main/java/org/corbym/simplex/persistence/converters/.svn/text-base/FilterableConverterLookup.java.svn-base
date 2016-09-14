package org.corbym.simplex.persistence.converters;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;

import java.util.*;

public class FilterableConverterLookup implements ConverterLookup, ConverterRegistry {
    IterablePrioritizedList<Converter> converters = new IterablePrioritizedList<Converter>();
    private Map<Class, Converter> converterCache = Collections.synchronizedMap(new HashMap<Class, Converter>());

    public Converter lookupConverterForType(Class type) {
        return lookupConverterForTypeExcluding(type, null);
    }

    public Converter lookupConverterForTypeExcluding(Class type, Class<? extends Converter>... notToChoose) {
        Converter found = converterCache.get(type);
        final List<Class<? extends Converter>> invalidConverters = notToChoose == null ? new ArrayList() : Arrays.asList(notToChoose);
        if (found == null || invalidConverters.contains(found.getClass())) {
            for (Converter converter : converters) {
                if (converter.canConvert(type) && !invalidConverters.contains(converter.getClass())) {
                    if (found == null) {
                        converterCache.put(type, converter);
                    }
                    return converter;
                }
            }
        } else if (found != null) {
            return found;
        }
        System.err.println("invalid converter. valid converters are:" + converters);
        throw new ConversionException("converter not found for type:" + type);
    }

    public void registerConverter(Converter converter, int priority) {
        converters.add(converter, priority);
    }
}
