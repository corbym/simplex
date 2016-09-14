package org.corbym.groovymud.persistence;


import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy
import com.thoughtworks.xstream.core.util.ClassLoaderReference
import com.thoughtworks.xstream.core.util.CompositeClassLoader
import com.thoughtworks.xstream.io.xml.DomDriver
import com.thoughtworks.xstream.mapper.Mapper
import org.corbym.simplex.persistence.converters.CollectionConverter
import org.corbym.simplex.persistence.converters.FilterableConverterLookup
import org.corbym.simplex.persistence.converters.ParentFieldTrackingMarshalListener
import org.corbym.simplex.persistence.marshalling.AccessibleReferenceByXpathMarshallingStrategy
import org.corbym.simplex.persistence.stubs.SomeObjectWithoutId
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertEquals

class CollectionConverterIsolationTest {
    final lookup = new FilterableConverterLookup()
    XStream control = new XStream(new DomDriver())
    XStream underTest = new XStream(null, new DomDriver(), new ClassLoaderReference(new CompositeClassLoader()), (Mapper) null, lookup, null)
    final mapper = underTest.getMapper()
    final listener = new ParentFieldTrackingMarshalListener(mapper)
    final strategy = new AccessibleReferenceByXpathMarshallingStrategy(ReferenceByXPathMarshallingStrategy.RELATIVE)

    @Before
    void setup() {
        strategy.registerListener listener
        underTest.setMarshallingStrategy(strategy)
        underTest.registerConverter new CollectionConverter(lookup, listener, mapper)
    }

    @Test
    void "xstream with a CollectionConverter behaves the same as an xstream without it on a List"() {
        final list = new ArrayList()
        list << new SomeObjectWithoutId() << new SomeObjectWithoutId()
        final controlXML = control.toXML(list)
        final underTestXML = underTest.toXML(list)
        assertEquals(controlXML, underTestXML)
        List controlList = control.fromXML(underTestXML)
        List unmarshalledList = (List) underTest.fromXML(underTestXML)
        assertEquals(controlList, unmarshalledList)
    }

    @Test
    void "xstream with a CollectionConverter behaves the same as an xstream without it on a Map"() {
        final map = new HashMap()
        map.put "foo", new SomeObjectWithoutId()
        map.put "bar", new SomeObjectWithoutId()
        final controlXML = control.toXML(map)
        final underTestXML = underTest.toXML(map)
        assertEquals(controlXML, underTestXML)
        Map unmarshalledMap = underTest.fromXML(underTestXML)
        Map controlMap = control.fromXML(underTestXML)
        assertEquals(controlMap, unmarshalledMap)
    }

}
