package org.corbym.simplex.persistence.stubs

import org.corbym.simplex.persistence.annotations.LazyLoad

/**
 * Created by IntelliJ IDEA.
 * User: Matt.Corby
 * Date: 21/02/11
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
class SingletonWithLazyLoadField {
    @LazyLoad
    def somefield
}
