package org.vaadin.appfoundation.test.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.vaadin.appfoundation.authorization.PermissionManager;
import org.vaadin.appfoundation.authorization.Resource;
import org.vaadin.appfoundation.authorization.Role;

public class PermissionManagerMock implements PermissionManager {

    private static final long serialVersionUID = -1182254914654563528L;
    private final List<String> invokedMethods = new ArrayList<String>();

    public void allow(Role role, String action, Resource resource) {
        invokedMethods.add("allow");
    }

    public void allowAll(Role role, Resource resource) {
        invokedMethods.add("allowAll");
    }

    public void deny(Role role, String action, Resource resource) {
        invokedMethods.add("deny");
    }

    public void denyAll(Role role, Resource resource) {
        invokedMethods.add("denyAll");
    }

    public boolean hasAccess(Role role, String action, Resource resource) {
        invokedMethods.add("hasAccessObject");
        return false;
    }

    public boolean hasAccess(Set<Role> roles, String action, Resource resource) {
        invokedMethods.add("hasAccessSet");
        return false;
    }

    public boolean wasInvoked(String method) {
        return invokedMethods.contains(method);
    }

    public void removeAllPermission(Role role, Resource resource) {
        invokedMethods.add("removeAllPermission");
    }

    public void removeAllPermissions(Role role, Resource resource) {
        invokedMethods.add("removeAllPermissions");
    }

    public void removePermission(Role role, String action, Resource resource) {
        invokedMethods.add("removePermission");
    }

}
