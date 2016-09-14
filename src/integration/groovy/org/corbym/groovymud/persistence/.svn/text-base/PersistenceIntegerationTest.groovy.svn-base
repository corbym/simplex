package org.corbym.groovymud.persistence

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.DomDriver
import org.corbym.simplex.persistence.SimplexDao
import org.corbym.simplex.persistence.SimplexDaoFactory
import org.junit.AfterClass
import org.junit.BeforeClass
import static junit.framework.Assert.assertEquals

class PersistenceIntegerationTest {

    transient static SimplexDao dao = SimplexDaoFactory.instance
    transient XStream xStream = new XStream(new DomDriver())

    @BeforeClass
    static void "print cwd"() {
        dao.clean();
        println "Store in ${new File(SimplexDaoFactory.DEFAULT_STORE_LOCATION).absolutePath}"
    }

    @AfterClass
    static void "remove persistent store"() {
        dao.clean()
    }


    void assertXStreamEquals(def expected, def actual) {
        final xmlExpected = xStream.toXML(expected)
        final xmlActual = xStream.toXML(actual)
        assertEquals("Xstream xml not equal", xmlExpected, xmlActual)
    }


    def assertLoadObject(def one) {
        def loaded = dao.load(one.getClass(), one.id)
        assert loaded, "object should be loaded but was $loaded"
        return loaded
    }

    def assertThatFilenameExists(def idObject) {
        final fileName = dao.store + "/" + idObject.getClass().getName() + "@" + idObject.id + ".xml"
        final file = new File(fileName)
        assert file.exists()
        return file
    }

}

