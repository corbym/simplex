package org.corbym.simplex.persistence.marshalling;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.mapper.Mapper;
import org.corbym.simplex.persistence.writer.AccessiblePathTrackingWriter;

import java.util.Set;

public class AccessibleReferenceByXPathMarshaller extends ReferenceByXPathMarshaller {
    private final PathTracker pathTracker;
    private final Set<MarshallingListener> listeners;

    public AccessibleReferenceByXPathMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper, int mode, Set<MarshallingListener> listeners) {
        super(writer, converterLookup, mapper, mode);
        pathTracker = new PathTracker();
        this.writer = new AccessiblePathTrackingWriter(this.writer, pathTracker);
        this.listeners = listeners;
    }

    @Override
    protected String createReference(Path currentPath, Object existingReferenceKey) {
        fireValidReference(currentPath);
        return super.createReference(currentPath, existingReferenceKey);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Object createReferenceKey(Path currentPath, Object item) {
        fireValidReference(currentPath);
        return super.createReferenceKey(currentPath, item);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void fireValidReference(Object referenceKey) {
        for (MarshallingListener listener : listeners) {
            listener.notifyOfValidReference(referenceKey);
        }
    }
}
