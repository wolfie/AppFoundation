package org.vaadin.appfoundation.authorization.memory;

import org.vaadin.appfoundation.authorization.AbstractPermissionManager;
import org.vaadin.appfoundation.authorization.PermissionManager;
import org.vaadin.appfoundation.authorization.PermissionResultType;
import org.vaadin.appfoundation.authorization.Resource;
import org.vaadin.appfoundation.authorization.Role;

/**
 * An implementation of the {@link PermissionManager} interface in which all the
 * permission details are kept in memory. No permission are persisted.
 * 
 * @author Kim
 * 
 */
public class MemoryPermissionManager extends AbstractPermissionManager {

    private static final long serialVersionUID = 4925563808162478919L;

    /**
     * Contains the "allowed" permissions for those permission where an explicit
     * action has been defined.
     */
    private final PermissionMap allowed;

    /**
     * Contains the "denied" permissions for those permission where an explicit
     * action has been defined.
     */
    private final PermissionMap denied;

    /**
     * Contains all global "allowed" permissions, these include permissions set
     * with allowDefault and allowAll.
     */
    private final PermissionMap globalAllowed;

    /**
     * Contains all global "denied" permissions, these include permissions set
     * with denyDefault and denyAll.
     */
    private final PermissionMap globalDenied;

    public MemoryPermissionManager() {
        allowed = new PermissionMap();
        denied = new PermissionMap();
        globalAllowed = new PermissionMap();
        globalDenied = new PermissionMap();
    }

    /**
     * {@inheritDoc}
     */
    public void allow(Role role, String action, Resource resource) {
        checkRoleAndResourceNotNull(role, resource);

        if (denied.contains(role, action, resource)) {
            denied.remove(role, action, resource);
        }

        allowed.put(role, action, resource);
    }

    /**
     * {@inheritDoc}
     */
    public void allowAll(Role role, Resource resource) {
        checkRoleAndResourceNotNull(role, resource);

        denied.removeAll(role, resource);
        globalDenied.removeAll(role, resource);
        globalAllowed.put(role, "all", resource);
    }

    /**
     * {@inheritDoc}
     */
    public void deny(Role role, String action, Resource resource) {
        checkRoleAndResourceNotNull(role, resource);

        if (allowed.contains(role, action, resource)) {
            allowed.remove(role, action, resource);
        }

        denied.put(role, action, resource);
    }

    /**
     * {@inheritDoc}
     */
    public void denyAll(Role role, Resource resource) {
        checkRoleAndResourceNotNull(role, resource);

        allowed.removeAll(role, resource);
        globalAllowed.removeAll(role, resource);
        globalDenied.put(role, "all", resource);
    }

    @Override
    protected PermissionResultType getPermissionResultType(Role role,
            String action, Resource resource) {
        checkRoleAndResourceNotNull(role, resource);

        if (allowed.contains(role, action, resource)) {
            return PermissionResultType.ALLOW_EXPLICITLY;
        }

        if (denied.contains(role, action, resource)) {
            return PermissionResultType.DENY_EXPLICITLY;
        }

        if (globalAllowed.contains(role, "all", resource)) {
            return PermissionResultType.ALLOW_EXPLICITLY;
        }

        if (globalDenied.contains(role, "all", resource)) {
            return PermissionResultType.DENY_EXPLICITLY;
        }

        if (globalAllowed.hasPermissions(resource, "all")
                || allowed.hasPermissions(resource, action)) {
            return PermissionResultType.DENY_IMPLICITLY;
        }

        return PermissionResultType.ALLOW_IMPLICITLY;
    }

    public void removeAllPermission(Role role, Resource resource) {
        checkRoleAndResourceNotNull(role, resource);
        globalAllowed.removeAll(role, resource);
        globalAllowed.removeAll(role, resource);
    }

    public void removeAllPermissions(Role role, Resource resource) {
        checkRoleAndResourceNotNull(role, resource);

        allowed.removeAll(role, resource);
        denied.removeAll(role, resource);
        globalAllowed.removeAll(role, resource);
        globalDenied.removeAll(role, resource);
    }

    public void removePermission(Role role, String action, Resource resource) {
        checkRoleAndResourceNotNull(role, resource);
        allowed.remove(role, action, resource);
        denied.remove(role, action, resource);
    }

}
