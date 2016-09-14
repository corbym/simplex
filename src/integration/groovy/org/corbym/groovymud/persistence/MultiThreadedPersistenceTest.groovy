package org.corbym.groovymud.persistence

import org.corbym.simplex.persistence.stubs.SomeObjectWithDefFieldAndId
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: Matt.Corby
 * Date: 19/02/11
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
class MultiThreadedPersistenceTest extends PersistenceIntegerationTest {
    @Test
    void "multi-threaded saving object test"() {
        Exception e = null
        def t1 = Thread.start("one") {
            try {
                for (int x = 0; x < 25; x++) {
                    def foo = new SomeObjectWithDefFieldAndId(somefield: "foo$x")
                    println "t1 saving:" + foo
                    dao.save(foo)
                    println "t1 loading: $foo.id" + foo
                    def obj = dao.load(SomeObjectWithDefFieldAndId, foo.id)
                    Thread.yield()
                    assert obj.somefield == "foo$x"

                }
            } catch (all) {
                e = all
            }
        }
        Exception e1 = null
        def t2 = Thread.start("two") {
            try {
                for (int y = 0; y < 25; y++) {
                    def bar = new SomeObjectWithDefFieldAndId(somefield: "bar$y")
                    println "t2 saving:" + bar
                    dao.save(bar)
                    def loaded = dao.load(SomeObjectWithDefFieldAndId, bar.id)
                    println "t2 loading: $bar.id" + bar
                    Thread.yield()
                    assert loaded.somefield == "bar$y"

                }
            } catch (all) {
                e1 = all
            }
        }
        t1.join()
        t2.join()
        if (e != null) { throw e }
        if (e1 != null) { throw e1 }
    }
}
