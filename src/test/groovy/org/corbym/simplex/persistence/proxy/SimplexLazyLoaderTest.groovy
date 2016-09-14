package org.corbym.simplex.persistence.proxy

import net.sf.cglib.proxy.Enhancer
import org.corbym.simplex.persistence.stubs.FailableLazyLoader
import org.corbym.simplex.persistence.stubs.SomeObjectWithId
import org.junit.Ignore
import org.junit.Test

class SimplexLazyLoaderTest {
    @Test
    @Ignore
    void "lazyload proxy should be able to check an id field without loading the object"() {
        SomeObjectWithId one = Enhancer.create(SomeObjectWithId, SomeObjectWithId.class.getInterfaces(), new FailableLazyLoader(123, null, SomeObjectWithId))

        assert one.getId() == 123
    }
}
