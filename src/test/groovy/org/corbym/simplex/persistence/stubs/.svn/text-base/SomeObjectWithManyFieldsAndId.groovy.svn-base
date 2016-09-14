package org.corbym.simplex.persistence.stubs

import org.corbym.simplex.persistence.annotations.Id
import org.corbym.simplex.persistence.annotations.LazyLoad

/**
 * Created by IntelliJ IDEA.
 * User: Matt.Corby
 * Date: 20/02/11
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
class SomeObjectWithManyFieldsAndId {
    @Id
    def id

    SomeObjectWithId one
    SomeObjectWithoutId two
    ObjectWithLazyLoaderAnnotatedField three;
    @LazyLoad
    def four;
}
