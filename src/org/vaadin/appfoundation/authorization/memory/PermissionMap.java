package org.vaadin.appfoundation.authorization.memory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.appfoundation.authorization.Resource;
import org.vaadin.appfoundation.authorization.Role;

public class PermissionMap implements Serializable {

    private static final long serialVersionUID = 923392126142283039L;
    private final Map<String, Map<String, List<String>>> permissions;

    public PermissionMap() {
        permissions = new HashMap<String, Map<String, List<String>>>();
    }

    /**
     * Add permission for the given role, for the given action in the given
     * resource
     * 
     * @param role
     *            The role for whom we are setting the permission
     * @param action
     *            The action to which we are setting the permission
     * @param resource
     *            The resource which owns the action
     */
    public void put(Role role, String action, Resource resource) {
        List<String> actions = getActions(role, resource);
        actions.add(action);

        Map<String, List<String>> rolesPermissions = getRolesPermissions(role);
        rolesPermissions.put(resource.getIdentifier(), actions);
        permissions.put(role.getIdentifier(), rolesPermissions);
    }

    /**
     * Removes the permission for the given role, for the given action in the
     * given resource
     * 
     * @param role
     *            The role of whom we are removing the permission
     * @param action
     *            The action of which we are removing the permission
     * @param resource
     *            The resource which owns the action
     */
    public void remove(Role role, String action, Resource resource) {
        List<String> actions = getActions(role, resource);
        actions.remove(action);

        Map<String, List<String>> rolesPermissions = getRolesPermissions(role);
        rolesPermissions.put(resource.getIdentifier(), actions);
        permissions.put(role.getIdentifier(), rolesPermissions);
    }

    /**
     * Removes all permissions set for the given role in the given resource.
     * 
     * @param role
     *            The role of whom we are removing the permission
     * @param resource
     *            The resource whose permissions are being removed
     */
    public void removeAll(Role role, Resource resource) {
        Map<String, List<String>> rolesPermissions = getRolesPermissions(role);
        rolesPermissions.remove(resource.getIdentifier());
    }

    /**
     * Checks if a permission has been assigned for the given role, in the given
     * resource, for the given action.
     * 
     * @param role
     *            The role whose permissions we are checking
     * @param action
     *            The action for which we are checking the permission
     * @param resource
     *            The resource who owns the action
     * @return True if permission is set, false if not
     */
    public boolean contains(Role role, String action, Resource resource) {
        List<String> actions = getActions(role, resource);

        return actions.contains(action);
    }

    /**
     * Checks if the given resource have any permissions set for any role for
     * the given action.
     * 
     * @param resouce
     *            Resource whose permissions we are checking
     * @param action
     *            Action whose permissions we are checking
     * @return True if at least one permission is set for any action in the
     *         resource, else false
     */
    public boolean hasPermissions(Resource resource, String action) {
        Collection<Map<String, List<String>>> allPermissions = permissions
                .values();
        if (allPermissions == null) {
            return false;
        }

        for (Map<String, List<String>> rolePermissions : allPermissions) {
            if (roleHasPermissionsSetForResourceAndAction(rolePermissions,
                    resource, action)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the role permissions given contains any permissions for the
     * given resource.
     * 
     * @param rolePermissions
     *            A role's all permissions
     * @param resource
     *            The resource whose permissions we are checking
     * @param action
     *            The action whose permissions we are checking
     * @return True if the resource has permissions set, else false
     */
    private boolean roleHasPermissionsSetForResourceAndAction(
            Map<String, List<String>> rolePermissions, Resource resource,
            String action) {
        if (rolePermissions == null) {
            return false;
        }

        if (!rolePermissions.containsKey(resource.getIdentifier())) {
            return false;
        }

        if (rolePermissions.get(resource.getIdentifier()).size() == 0) {
            return false;
        }

        if (!rolePermissions.get(resource.getIdentifier()).contains(action)) {
            return false;
        }

        return true;
    }

    /**
     * Get all actions set for the given resource in the given role's
     * permissions.
     * 
     * @param role
     *            The role whose permissions we want to fetch
     * @param resource
     *            The resource whose action we want to fetch
     * @return A list of defined actions
     */
    private List<String> getActions(Role role, Resource resource) {
        Map<String, List<String>> rolesPermissions = getRolesPermissions(role);
        List<String> list = rolesPermissions.get(resource.getIdentifier());
        if (list == null) {
            list = new ArrayList<String>();
        }

        return list;
    }

    /**
     * Get all the permissions set for the given role
     * 
     * @param role
     *            The role whose permissions we want to fetch
     * @return The role's permissions
     */
    private Map<String, List<String>> getRolesPermissions(Role role) {
        Map<String, List<String>> map = permissions.get(role.getIdentifier());
        if (map == null) {
            map = new HashMap<String, List<String>>();
        }

        return map;
    }
}
