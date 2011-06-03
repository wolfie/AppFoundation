package org.vaadin.appfoundation.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;
import org.vaadin.appfoundation.persistence.facade.JPAFacade;

public class FacadeFactoryTest {

    @After
    public void tearDown() {
        FacadeFactory.clear();
    }

    @Test
    public void registerDefaultFacade() throws InstantiationException,
            IllegalAccessException {
        FacadeFactory.registerFacade("default", true);

        assertNotNull(FacadeFactory.getFacade());
        assertTrue(FacadeFactory.getFacade() instanceof JPAFacade);
    }

    @Test
    public void registerNonDefaultFacade() throws InstantiationException,
            IllegalAccessException {
        FacadeFactory.registerFacade("default", false);

        assertNull(FacadeFactory.getFacade());
        assertTrue(FacadeFactory.getFacade("default") instanceof JPAFacade);
    }

    @Test
    public void registerCustomFacade() throws InstantiationException,
            IllegalAccessException {
        FacadeFactory.registerFacade(MockFacade.class, "default", true);

        assertNotNull(FacadeFactory.getFacade());
        assertTrue(FacadeFactory.getFacade() instanceof MockFacade);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerSameNameTwice() throws InstantiationException,
            IllegalAccessException {
        FacadeFactory.registerFacade(MockFacade.class, "default", true);
        FacadeFactory.registerFacade(MockFacade.class, "default", false);
    }

    @Test
    public void getFacade() throws InstantiationException,
            IllegalAccessException {
        FacadeFactory.registerFacade("default", true);

        assertNotNull(FacadeFactory.getFacade());
        assertTrue(FacadeFactory.getFacade() instanceof JPAFacade);
    }

    @Test
    public void getFacadeWithName() throws InstantiationException,
            IllegalAccessException {
        FacadeFactory.registerFacade(JPAFacade.class, "default", true);
        FacadeFactory.registerFacade(MockFacade.class, "test2", false);

        assertTrue(FacadeFactory.getFacade("test2") instanceof MockFacade);
        assertTrue(FacadeFactory.getFacade("default") instanceof JPAFacade);
    }

    @Test
    public void removeFacade() throws InstantiationException,
            IllegalAccessException {
        FacadeFactory.registerFacade(MockFacade.class, "test", false);
        FacadeFactory.registerFacade(MockFacade.class, "test2", false);
        FacadeFactory.removeFacade("test");
        assertNull(FacadeFactory.getFacade("test"));
        assertNotNull(FacadeFactory.getFacade("test2"));
    }

    @Test
    public void clear() throws InstantiationException, IllegalAccessException {
        FacadeFactory.registerFacade(MockFacade.class, "test", false);
        FacadeFactory.registerFacade(MockFacade.class, "test2", false);
        FacadeFactory.registerFacade(MockFacade.class, "test3", false);
        FacadeFactory.clear();
        assertNull(FacadeFactory.getFacade("test"));
        assertNull(FacadeFactory.getFacade("test2"));
        assertNull(FacadeFactory.getFacade("test3"));
    }

    @Test
    public void setDefaultFacade() throws InstantiationException,
            IllegalAccessException {
        FacadeFactory.registerFacade(MockFacade.class, "test", true);
        FacadeFactory.registerFacade(MockFacade.class, "test2", false);
        FacadeFactory.registerFacade(MockFacade.class, "test3", false);

        assertEquals(FacadeFactory.getFacade("test"), FacadeFactory.getFacade());
        FacadeFactory.setDefaultFacade("test2");
        assertEquals(FacadeFactory.getFacade("test2"), FacadeFactory
                .getFacade());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultFacadeNullName() {
        FacadeFactory.setDefaultFacade(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultFacadeEmptyName() {
        FacadeFactory.setDefaultFacade("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultFacadeNonExistingName() {
        FacadeFactory.setDefaultFacade("test");
    }

}
