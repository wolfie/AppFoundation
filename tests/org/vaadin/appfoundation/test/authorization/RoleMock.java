package org.vaadin.appfoundation.test.authorization;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.vaadin.appfoundation.authorization.Role;

public class RoleMock implements Role {

    private final String id;
    private final Set<Role> roles = new HashSet<Role>();

    public RoleMock() {
        id = UUID.randomUUID().toString();
    }

    public String getIdentifier() {
        return id;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public Set<Role> getRoles() {
        return new HashSet<Role>(roles);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public void setRoles(Set<Role> roles) {
        roles.clear();
        roles.addAll(roles);
    }

}
