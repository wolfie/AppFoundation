package org.vaadin.appfoundation.test.persistence;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AbstractPojoTest {

    @Test
    public void id() {
        MockPojo pojo = new MockPojo();
        pojo.setId(1L);

        assertEquals(Long.valueOf(1L), pojo.getId());
        pojo.setId(2L);
        assertEquals(Long.valueOf(2L), pojo.getId());
    }

    @Test
    public void consistencyVersion() {
        MockPojo pojo = new MockPojo();
        pojo.setConsistencyVersion(1L);

        assertEquals(Long.valueOf(1L), pojo.getConsistencyVersion());
        pojo.setConsistencyVersion(2L);
        assertEquals(Long.valueOf(2L), pojo.getConsistencyVersion());
    }
}
