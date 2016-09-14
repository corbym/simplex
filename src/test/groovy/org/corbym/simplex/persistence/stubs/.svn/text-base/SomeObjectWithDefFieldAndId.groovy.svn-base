package org.corbym.simplex.persistence.stubs

import org.corbym.simplex.persistence.annotations.Id

class SomeObjectWithDefFieldAndId {
    @Id def id
    def somefield

    boolean equals(o) {
        if (this.is(o)) return true;
        if (getClass() != o.class) return false;

        SomeObjectWithDefFieldAndId that = (SomeObjectWithDefFieldAndId) o;

        if (id != that.id) return false;
        if (somefield != that.somefield) return false;

        return true;
    }

    int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (somefield != null ? somefield.hashCode() : 0);
        return result;
    }
}
