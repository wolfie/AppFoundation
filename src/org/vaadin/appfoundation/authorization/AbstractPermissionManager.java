package org.vaadin.appfoundation.authorization;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An abstract implementation of the {@link PermissionManager}. This class
 * implements those methods which are common to all the permission managers.
 * 
 * @author Kim
 * 
 */
public abstract class AbstractPermissionManager implements PermissionManager {

    private static final long serialVersionUID = 8059417567360426160L;

    /**
     * Checks that neither role or resource is null.
     * 
     * @param role
     * @param resource
     * @throws IllegalArgumentException
     *             Thrown if either role or resource is null
     */
    protected void checkRoleAndResourceNotNull(Role role, Resource resource) {
        if (role == null) {
            throw new IllegalArgumentException("Role may not be null");
        }

        if (resource == null) {
            throw new IllegalArgumentException("Role may not be null");
        }
    }

    /**
     * Determines the permission result type for the given role for the given
     * resource and action combination. This method should not take into account
     * sub roles.
     * 
     * @param role
     *            Role whose permissions we are checking
     * @param action
     *            Action for which permissions are being checked
     * @param resource
     *            Resource for which permissions are being checked
     * @return The permission result's type for the given role
     */
    protected abstract PermissionResultType getPermissionResultType(Role role,
            String action, Resource resource);

    /**
     * {@inheritDoc}
     */
    public boolean hasAccess(Role role, String action, Resource resource) {
        checkRoleAndResourceNotNull(role, resource);

        return hasAccess(Collections.singleton(role), action, resource);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasAccess(Set<Role> roles, String action, Resource resource) {
        if (roles == null) {
            throw new IllegalArgumentException("Role may not be null");
        }

        if (resource == null) {
            throw new IllegalArgumentException("Role may not be null");
        }

        Set<Role> rolesInternal = new HashSet<Role>(roles);

        Set<PermissionResultType> results = new HashSet<PermissionResultType>();
        Set<String> checkedRoles = new HashSet<String>();

        PermissionResultType currentResult = PermissionResultType.ALLOW_IMPLICITLY;

        while (!rolesInternal.isEmpty()) {
            Set<Role> subRoles = new HashSet<Role>();

            for (Role role : rolesInternal) {
                if (role == null) {
                    continue;
                }

                if (checkedRoles.contains(role.getIdentifier())) {
                    continue;
                }

                checkedRoles.add(role.getIdentifier());
                if (role.getRoles() != null) {
                    subRoles.addAll(role.getRoles());
                }
                results.add(getPermissionResultType(role, action, resource));
            }

            if (results.contains(PermissionResultType.ALLOW_EXPLICITLY)) {
                return true;
            } else if (results.contains(PermissionResultType.DENY_EXPLICITLY)) {
                return false;
            } else if (results.contains(PermissionResultType.DENY_IMPLICITLY)) {
                currentResult = PermissionResultType.DENY_IMPLICITLY;
            }

            rolesInternal.clear();
            rolesInternal.addAll(subRoles);
        }

        if (currentResult.equals(PermissionResultType.DENY_IMPLICITLY)) {
            return false;
        } else {
            return true;
        }

    }

}
