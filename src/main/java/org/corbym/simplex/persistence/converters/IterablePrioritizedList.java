package org.corbym.simplex.persistence.converters;

import com.thoughtworks.xstream.core.util.PrioritizedList;

public class IterablePrioritizedList<T> extends PrioritizedList implements Iterable<T> {
    public String toString() {
        StringBuffer buffer = new StringBuffer("PrioritorizedList [\n");
        for (T item : this) {
            buffer.append(item.toString()).append('\n');
        }
        return buffer.toString();
    }
}
