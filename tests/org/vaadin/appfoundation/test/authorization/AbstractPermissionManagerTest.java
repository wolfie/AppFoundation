package org.vaadin.appfoundation.test.authorization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.vaadin.appfoundation.authorization.PermissionManager;
import org.vaadin.appfoundation.authorization.Resource;
import org.vaadin.appfoundation.authorization.Role;

public abstract class AbstractPermissionManagerTest {

    public abstract Role createRole();

    public abstract Resource createResource();

    public abstract PermissionManager getPermissionHandler();

    @Test(expected = IllegalArgumentException.class)
    public void denyNullRole() {
        PermissionManager pm = getPermissionHandler();
        pm.deny(null, "test", createResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void denyNullResource() {
        PermissionManager pm = getPermissionHandler();
        pm.deny(createRole(), "test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void allowNullRole() {
        PermissionManager pm = getPermissionHandler();
        pm.allow(null, "test", createResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void allowNullResource() {
        PermissionManager pm = getPermissionHandler();
        pm.allow(createRole(), "test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void denyAllNullRole() {
        PermissionManager pm = getPermissionHandler();
        pm.denyAll(null, createResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void denyAllNullResource() {
        PermissionManager pm = getPermissionHandler();
        pm.denyAll(createRole(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void allowAllNullRole() {
        PermissionManager pm = getPermissionHandler();
        pm.allowAll(null, createResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void allowAllNullResource() {
        PermissionManager pm = getPermissionHandler();
        pm.allowAll(createRole(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hasAccessNullRole() {
        PermissionManager pm = getPermissionHandler();
        pm.hasAccess((Role) null, "test", createResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void hasAccessNullRole2() {
        PermissionManager pm = getPermissionHandler();
        pm.hasAccess((Set<Role>) null, "test", createResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void hasAccessNullResource() {
        PermissionManager pm = getPermissionHandler();
        pm.hasAccess(createRole(), "test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hasAccessNullResource2() {
        PermissionManager pm = getPermissionHandler();
        pm.hasAccess(Collections.singleton(createRole()), "test", null);
    }

    @Test
    public void deny() {
        Role role = createRole();
        Role role2 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        assertTrue(pm.hasAccess(role, "test", resource));

        pm.deny(role, "test", resource);
        assertFalse(pm.hasAccess(role, "test", resource));
        assertTrue(pm.hasAccess(role2, "test", resource));
    }

    @Test
    public void allow() {
        Role role = createRole();
        Role role2 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.allow(role, "test", resource);
        assertTrue(pm.hasAccess(role, "test", resource));
        assertFalse(pm.hasAccess(role2, "test", resource));
    }

    @Test
    public void allowAll() {
        Role role = createRole();
        Role role2 = createRole();
        Role role3 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.allowAll(role, resource);
        assertFalse(pm.hasAccess(role3, "write", resource));

        pm.allow(role2, "write", resource);

        assertTrue(pm.hasAccess(role, "read", resource));
        assertTrue(pm.hasAccess(role, "test", resource));
        assertTrue(pm.hasAccess(role, "write", resource));
        pm.deny(role, "write", resource);
        assertFalse(pm.hasAccess(role, "write", resource));
    }

    @Test
    public void denyAll() {
        Role role = createRole();
        Role role2 = createRole();
        Role role3 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.denyAll(role, resource);
        assertTrue(pm.hasAccess(role3, "test", resource));

        pm.deny(role2, "write", resource);

        assertFalse(pm.hasAccess(role, "read", resource));
        assertFalse(pm.hasAccess(role, "test", resource));
        assertFalse(pm.hasAccess(role, "write", resource));
        pm.allow(role, "write", resource);
        assertTrue(pm.hasAccess(role, "write", resource));
    }

    @Test
    public void denyOverride() {
        Role role = createRole();
        Role role2 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.allow(role, "test", resource);
        pm.deny(role, "test", resource);
        assertFalse(pm.hasAccess(role, "test", resource));
        assertTrue(pm.hasAccess(role2, "test", resource));
    }

    @Test
    public void allowOverride() {
        Role role = createRole();
        Role role2 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.deny(role, "test", resource);
        pm.allow(role, "test", resource);
        assertTrue(pm.hasAccess(role, "test", resource));
        assertFalse(pm.hasAccess(role2, "test", resource));
    }

    @Test
    public void allowAllOverride() {
        Role role = createRole();
        Role role2 = createRole();
        Role role3 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.denyAll(role, resource);
        pm.allowAll(role, resource);
        assertFalse(pm.hasAccess(role3, "write", resource));

        pm.allow(role2, "write", resource);

        assertTrue(pm.hasAccess(role, "read", resource));
        assertTrue(pm.hasAccess(role, "test", resource));
        assertTrue(pm.hasAccess(role, "write", resource));
        pm.deny(role, "write", resource);
        assertFalse(pm.hasAccess(role, "write", resource));
    }

    @Test
    public void denyAllOverride() {
        Role role = createRole();
        Role role2 = createRole();
        Role role3 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.allowAll(role, resource);
        pm.denyAll(role, resource);
        assertTrue(pm.hasAccess(role3, "test", resource));

        pm.deny(role2, "write", resource);

        assertFalse(pm.hasAccess(role, "read", resource));
        assertFalse(pm.hasAccess(role, "test", resource));
        assertFalse(pm.hasAccess(role, "write", resource));
        pm.allow(role, "write", resource);
        assertTrue(pm.hasAccess(role, "write", resource));
    }

    @Test
    public void combinationAllowed() {
        // If one is denied, then others are allowed
        Role role = createRole();
        Role role2 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.deny(role2, "test", resource);
        assertTrue(pm.hasAccess(role, "test", resource));
    }

    @Test
    public void combinationDenied() {
        // If one is allowed, then others are denied
        Role role = createRole();
        Role role2 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.allow(role2, "test", resource);
        assertFalse(pm.hasAccess(role, "test", resource));
    }

    @Test
    public void denyOthersWhenAllowAllIsSet() {
        Role role = createRole();
        Role role2 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.allowAll(role, resource);
        assertFalse(pm.hasAccess(role2, "test", resource));
    }

    @Test
    public void allowOthersWhenDenyAllIsSet() {
        Role role = createRole();
        Role role2 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.denyAll(role, resource);
        assertTrue(pm.hasAccess(role2, "test", resource));
    }

    @Test
    public void allowActionEvenIfOtherActionsHasPermissions() {
        Role role = createRole();
        Role role2 = createRole();
        Resource resource = createResource();

        PermissionManager pm = getPermissionHandler();
        pm.allow(role2, "test", resource);
        assertFalse(pm.hasAccess(role, "test", resource));
        assertTrue(pm.hasAccess(role, "test2", resource));
    }

    @Test
    public void conflictionRule1() {
        Role role1 = createRole();
        Role role2 = createRole();
        Role role3 = createRole();
        Role role4 = createRole();

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.allow(role1, "test", resource);
        pm.allow(role3, "test", resource);
        pm.deny(role2, "test", resource);
        pm.deny(role4, "test", resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);
        roles.add(role2);

        assertTrue(pm.hasAccess(roles, "test", resource));
    }

    @Test
    public void conflictionRule2() {
        Role role1 = createRole();
        Role role2 = createRole();
        Role role3 = createRole();
        Role role4 = createRole();

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.allow(role3, "test", resource);
        pm.deny(role2, "test", resource);
        pm.deny(role4, "test", resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);
        roles.add(role2);

        assertFalse(pm.hasAccess(roles, "test", resource));
    }

    @Test
    public void conflictionRule3() {
        Role role1 = createRole();
        Role role2 = createRole();
        Role role3 = createRole();
        Role role4 = createRole();

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.allow(role3, "test", resource);
        pm.deny(role4, "test", resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);
        roles.add(role2);

        assertFalse(pm.hasAccess(roles, "test", resource));
    }

    @Test
    public void conflictionRule4() {
        Role role1 = createRole();
        Role role2 = createRole();

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);
        roles.add(role2);

        assertTrue(pm.hasAccess(roles, "test", resource));
    }

    @Test
    public void hierarchyRule1And2() {
        Role role1 = createRole();
        Role role2 = createRole();
        Role role3 = createRole();
        Role role4 = createRole();
        Role role5 = createRole();
        Role role6 = createRole();

        role1.addRole(role2);
        role2.addRole(role3);
        role3.addRole(role4);

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.deny(role3, "test", resource);
        pm.allow(role4, "test", resource);
        pm.deny(role5, "test", resource);
        pm.allow(role6, "test", resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);

        assertFalse(pm.hasAccess(roles, "test", resource));
    }

    @Test
    public void hierarchyRule3() {
        Role role1 = createRole();
        Role role2 = createRole();
        Role role3 = createRole();
        Role role4 = createRole();

        role1.addRole(role2);
        role2.addRole(role3);
        role2.addRole(role4);

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.allow(role3, "test", resource);
        pm.deny(role4, "test", resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);

        assertTrue(pm.hasAccess(roles, "test", resource));
    }

    @Test
    public void hierarchyRule4() {
        Role role1 = createRole();
        Role role2 = createRole();
        Role role3 = createRole();

        role1.addRole(role2);

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.deny(role3, "test", resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);

        assertTrue(pm.hasAccess(roles, "test", resource));
    }

    @Test
    public void hierarchyRule5() {
        Role role1 = createRole();
        Role role2 = createRole();
        Role role3 = createRole();

        role1.addRole(role2);

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.allow(role3, "test", resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);
        roles.add(null);

        assertFalse(pm.hasAccess(roles, "test", resource));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removePermissionNullRole() {
        PermissionManager pm = getPermissionHandler();
        pm.removePermission(null, null, createResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void removePermissionNullResource() {
        PermissionManager pm = getPermissionHandler();
        pm.removePermission(createRole(), null, null);
    }

    @Test
    public void removePermission() {
        Role role1 = createRole();
        Role role2 = createRole();

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.allow(role1, "test", resource);
        pm.allow(role2, "test", resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);

        assertTrue(pm.hasAccess(roles, "test", resource));

        pm.removePermission(role1, "test", resource);
        assertFalse(pm.hasAccess(roles, "test", resource));
        pm.removePermission(role2, "test", resource);
        assertTrue(pm.hasAccess(roles, "test", resource));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeAllPermissionNullRole() {
        PermissionManager pm = getPermissionHandler();
        pm.removeAllPermission(null, createResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeAllPermissionNullResource() {
        PermissionManager pm = getPermissionHandler();
        pm.removeAllPermission(createRole(), null);
    }

    @Test
    public void removeAllPermission() {
        Role role1 = createRole();
        Role role2 = createRole();

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.allowAll(role1, resource);
        pm.allowAll(role2, resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);

        assertTrue(pm.hasAccess(roles, "test", resource));

        pm.removeAllPermission(role1, resource);
        assertFalse(pm.hasAccess(roles, "test", resource));

        pm.removeAllPermission(role2, resource);
        assertTrue(pm.hasAccess(roles, "test", resource));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeAllPermissionsNullRole() {
        PermissionManager pm = getPermissionHandler();
        pm.removeAllPermissions(null, createResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeAllPermissionsNullResource() {
        PermissionManager pm = getPermissionHandler();
        pm.removeAllPermissions(createRole(), null);
    }

    @Test
    public void removeAllPermissions() {
        Role role1 = createRole();
        Role role2 = createRole();

        Resource resource = createResource();
        PermissionManager pm = getPermissionHandler();
        pm.allow(role1, "test", resource);
        pm.allow(role1, "test2", resource);
        pm.allow(role2, "test", resource);
        pm.allow(role2, "test2", resource);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role1);

        assertTrue(pm.hasAccess(roles, "test", resource));
        assertTrue(pm.hasAccess(roles, "test2", resource));

        pm.removeAllPermissions(role1, resource);
        assertFalse(pm.hasAccess(roles, "test", resource));
        assertFalse(pm.hasAccess(roles, "test2", resource));

        pm.removeAllPermissions(role2, resource);
        assertTrue(pm.hasAccess(roles, "test", resource));
        assertTrue(pm.hasAccess(roles, "test2", resource));
    }

}
