package org.corbym.simplex.persistence.converters

import com.thoughtworks.xstream.io.path.Path
import com.thoughtworks.xstream.mapper.Mapper
import org.corbym.simplex.persistence.stubs.ObjectWithLazyLoaderAnnotatedField
import org.corbym.simplex.persistence.stubs.SomeObjectWithId
import org.junit.Test

class ParentFieldTrackingMarshallListenerTest {
    Mapper mapper = [realClass: {SomeObjectWithId}] as Mapper;
    ParentFieldTrackingMarshalListener underTest = new ParentFieldTrackingMarshalListener(mapper);

    @Test
    public void "valid reference should populate parentField with a declared field"() throws Exception {
        final path = new Path(SomeObjectWithId.getName() + "/somefield")
        underTest.notifyOfValidReference(path);
        assert underTest.getParentField() == SomeObjectWithId.getDeclaredField("somefield")
    }

    @Test
    public void "valid reference should replace parentField with a declared field from the next call"() throws Exception {
        int call = 0
        Mapper mapper = [realClass: {
            if (call == 0) {
                call++
                SomeObjectWithId
            } else {
                ObjectWithLazyLoaderAnnotatedField
            }
        }] as Mapper;
        ParentFieldTrackingMarshalListener underTest = new ParentFieldTrackingMarshalListener(mapper);

        def path = new Path(SomeObjectWithId.getName() + "/somefield")
        underTest.notifyOfValidReference(path);
        assert underTest.getParentField() == SomeObjectWithId.getDeclaredField("somefield")
        path = new Path(SomeObjectWithId.getName() + "/somefield/" + ObjectWithLazyLoaderAnnotatedField.getName() + "/somefield")
        underTest.notifyOfValidReference(path);
        assert underTest.getParentField() == ObjectWithLazyLoaderAnnotatedField.getDeclaredField("somefield")
    }

    @Test
    public void "valid reference should not replace parentField with a declared field from the next call if no field found"() throws Exception {
        def path = new Path(SomeObjectWithId.getName() + "/somefield")
        underTest.notifyOfValidReference(path);
        assert underTest.getParentField() == SomeObjectWithId.getDeclaredField("somefield")
        path = new Path(SomeObjectWithId.getName() + "/somefield/" + ObjectWithLazyLoaderAnnotatedField.getName())
        underTest.notifyOfValidReference(path);
        assert underTest.getParentField() == SomeObjectWithId.getDeclaredField("somefield")
    }
}
