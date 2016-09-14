package org.corbym.simplex.persistence.stubs

import groovy.transform.InheritConstructors
import org.corbym.simplex.persistence.proxy.IdFieldAnnotatedLazyLoader
import static org.junit.Assert.fail

@InheritConstructors
class FailableLazyLoader extends IdFieldAnnotatedLazyLoader {

    @Override
    Object loadObject() {
        fail("should not lazy load object")
    }

}
