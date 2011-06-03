package org.vaadin.appfoundation.test.authorization.memory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.authorization.Resource;
import org.vaadin.appfoundation.authorization.Role;
import org.vaadin.appfoundation.authorization.memory.PermissionMap;
import org.vaadin.appfoundation.test.authorization.ResourceMock;
import org.vaadin.appfoundation.test.authorization.RoleMock;

public class PermissionMapTest {

    private PermissionMap map;

    @Before
    public void setUp() {
        map = new PermissionMap();
    }

    @After
    public void tearDown() {
        map = null;
    }

    @Test
    public void containsEmpty() {
        Role role = new RoleMock();
        Resource resource = new ResourceMock();

        assertFalse(map.contains(role, "test", resource));
    }

    @Test
    public void containsTrueWhenOneIsPut() {
        Role role = new RoleMock();
        Resource resource = new ResourceMock();

        map.put(role, "test", resource);
        assertTrue(map.contains(role, "test", resource));
    }

    @Test
    public void containsNoConflicts() {
        Role role = new RoleMock();
        Resource resource = new ResourceMock();
        Role role2 = new RoleMock();
        Resource resource2 = new ResourceMock();

        map.put(role, "test", resource);
        assertFalse(map.contains(role2, "test", resource));
        assertFalse(map.contains(role2, "test", resource2));
        assertFalse(map.contains(role, "test", resource2));

        map.put(role2, "test", resource);
        assertTrue(map.contains(role2, "test", resource));
        assertFalse(map.contains(role2, "test", resource2));
    }

    @Test
    public void hasPermissionsEmpty() {
        Resource resource = new ResourceMock();
        assertFalse(map.hasPermissions(resource, "test"));
    }

    @Test
    public void hasPermissionsAnotherIsSet() {
        Role role = new RoleMock();
        Resource resource = new ResourceMock();
        Resource resource2 = new ResourceMock();

        map.put(role, "test", resource2);
        assertFalse(map.hasPermissions(resource, "test"));
    }

    @Test
    public void hasPermissions() {
        Role role = new RoleMock();
        Resource resource = new ResourceMock();

        map.put(role, "test", resource);
        assertTrue(map.hasPermissions(resource, "test"));
    }

    @Test
    public void nullActions() {
        Role role = new RoleMock();
        Resource resource = new ResourceMock();

        map.put(role, null, resource);
        assertTrue(map.hasPermissions(resource, null));
        assertTrue(map.contains(role, null, resource));
    }

    @Test
    public void remove() {
        Role role = new RoleMock();
        Resource resource = new ResourceMock();
        map.put(role, "test", resource);
        assertTrue(map.hasPermissions(resource, "test"));
        assertTrue(map.contains(role, "test", resource));

        map.remove(role, "test", resource);
        assertFalse(map.hasPermissions(resource, "test"));
        assertFalse(map.contains(role, "test", resource));
    }

    @Test
    public void removeAll() {
        Role role = new RoleMock();
        Resource resource = new ResourceMock();
        map.put(role, "test", resource);
        map.put(role, "test2", resource);
        map.put(role, "test3", resource);

        map.removeAll(role, resource);
        assertFalse(map.hasPermissions(resource, "test"));
    }
}
