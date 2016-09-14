package org.corbym.simplex.persistence.stubs

class SomeObjectWithoutId {
    def somefield


    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o instanceof SomeObjectWithoutId)) return false;

        SomeObjectWithoutId that = (SomeObjectWithoutId) o;

        if (somefield != that.somefield) return false;

        return true;
    }

    int hashCode() {
        return (somefield != null ? somefield.hashCode() : 0);
    }
}
