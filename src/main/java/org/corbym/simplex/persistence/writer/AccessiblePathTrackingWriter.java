package org.corbym.simplex.persistence.writer;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingWriter;

public class AccessiblePathTrackingWriter implements HierarchicalStreamWriter {
    private final PathTracker pathTracker;
    private final PathTrackingWriter pathTrackingWriter;

    public AccessiblePathTrackingWriter(HierarchicalStreamWriter writer, PathTracker pathTracker) {
        pathTrackingWriter = new PathTrackingWriter(writer, pathTracker);
        this.pathTracker = pathTracker;
    }

    public PathTracker getPathTracker() {
        return pathTracker;
    }

    public void flush() {
        pathTrackingWriter.flush();
    }


    public void close() {
        pathTrackingWriter.close();
    }

    public void startNode(String name) {
        pathTrackingWriter.startNode(name);
    }

    public void startNode(String name, Class clazz) {
        pathTrackingWriter.startNode(name, clazz);
    }

    public void endNode() {
        pathTrackingWriter.endNode();
    }

    public void addAttribute(String key, String value) {
        pathTrackingWriter.addAttribute(key, value);
    }

    public void setValue(String text) {
        pathTrackingWriter.setValue(text);
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this; //lie
    }
}
