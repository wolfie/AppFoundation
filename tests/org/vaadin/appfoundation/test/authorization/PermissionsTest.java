package org.vaadin.appfoundation.test.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.authorization.Permissions;
import org.vaadin.appfoundation.authorization.Role;
import org.vaadin.appfoundation.authorization.memory.MemoryPermissionManager;
import org.vaadin.appfoundation.test.MockApplication;
import org.vaadin.appfoundation.test.MockApplication.MockContext;

public class PermissionsTest {

    private MockApplication application;
    private Permissions permissions = null;
    private PermissionManagerMock manager;

    @Before
    public void setUp() {
        application = new MockApplication();
        manager = new PermissionManagerMock();
        permissions = new Permissions(application, manager);
        permissions.transactionStart(application, null);
    }

    @After
    public void tearDown() {
        permissions.transactionEnd(application, null);
        permissions = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullApplication() {
        new Permissions(null, manager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullManager() {
        new Permissions(application, null);
    }

    @Test
    public void allow() {
        assertFalse(manager.wasInvoked("allow"));
        Permissions.allow(null, null, null);
        assertTrue(manager.wasInvoked("allow"));
    }

    @Test
    public void allowAll() {
        assertFalse(manager.wasInvoked("allowAll"));
        Permissions.allowAll(null, null);
        assertTrue(manager.wasInvoked("allowAll"));
    }

    @Test
    public void deny() {
        assertFalse(manager.wasInvoked("deny"));
        Permissions.deny(null, null, null);
        assertTrue(manager.wasInvoked("deny"));
    }

    @Test
    public void denyAll() {
        assertFalse(manager.wasInvoked("denyAll"));
        Permissions.denyAll(null, null);
        assertTrue(manager.wasInvoked("denyAll"));
    }

    @Test
    public void hasAccessObject() {
        assertFalse(manager.wasInvoked("hasAccessObject"));
        Permissions.hasAccess((Role) null, null, null);
        assertTrue(manager.wasInvoked("hasAccessObject"));
    }

    @Test
    public void hasAccessSet() {
        assertFalse(manager.wasInvoked("hasAccessSet"));
        Permissions.hasAccess((Set<Role>) null, null, null);
        assertTrue(manager.wasInvoked("hasAccessSet"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeWithNullApplication() {
        Permissions.initialize(null, new MemoryPermissionManager());

    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeWithNullManager() {
        Permissions.initialize(application, null);

    }

    @Test
    public void initialize() {
        MockContext context = (MockContext) application.getContext();
        assertEquals(0, context.getListeners().size());
        Permissions.initialize(application, new MemoryPermissionManager());
        assertEquals(1, context.getListeners().size());
        assertEquals(Permissions.class, context.getListeners().get(0)
                .getClass());
    }

    @Test
    public void removePermission() {
        assertFalse(manager.wasInvoked("removePermission"));
        Permissions.removePermission(null, null, null);
        assertTrue(manager.wasInvoked("removePermission"));
    }

    @Test
    public void removeAllPermission() {
        assertFalse(manager.wasInvoked("removeAllPermission"));
        Permissions.removeAllPermission(null, null);
        assertTrue(manager.wasInvoked("removeAllPermission"));
    }

    @Test
    public void removeAllPermissions() {
        assertFalse(manager.wasInvoked("removeAllPermissions"));
        Permissions.removeAllPermissions(null, null);
        assertTrue(manager.wasInvoked("removeAllPermissions"));
    }

}
