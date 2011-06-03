package org.vaadin.appfoundation.authorization;

import java.io.Serializable;
import java.util.Set;

/**
 * This interface defines a set of methods for permission managers, which are
 * used by the application foundation's authorization module. Each
 * implementation of the different permission managers needs to implement this
 * interface.
 * 
 * @author Kim
 * 
 */
public interface PermissionManager extends Serializable {

    /**
     * Grants the given role the permission to perform the given action for the
     * given resource.
     * 
     * @param role
     *            The role which is being assigned the permission
     * @param action
     *            The identifier for the action
     * @param resource
     *            The resource to which the permission applies
     * 
     * @throws IllegalArgumentException
     *             If either role or resource is null
     */
    public void allow(Role role, String action, Resource resource);

    /**
     * <p>
     * Grants the given role to perform <b>any action</b> for the given
     * resource.
     * </p>
     * 
     * <p>
     * Example:<br />
     * allowAll(role, resource);<br />
     * allow(anotherRole, "write", resource);
     * </p>
     * <p>
     * hasAccess(role, "read", resource) - returns true<br />
     * hasAccess(role, "close", resource) - returns true<br />
     * hasAccess(role, "write", resource) - returns true<br />
     * deny(role, "write", resource);<br />
     * hasAccess(role, "write", resource) - returns false
     * </p>
     * 
     * @param role
     *            The role which is being assigned the permission
     * @param resource
     *            The resource to which the permission applies
     * @throws IllegalArgumentException
     *             If either role or resource is null
     */
    public void allowAll(Role role, Resource resource);

    /**
     * Denies the given role the permission to perform the given action for the
     * given resource.
     * 
     * @param role
     *            The role which is being assigned the permission
     * @param action
     *            The identifier for the action
     * @param resource
     *            The resource to which the permission applies
     * @throws IllegalArgumentException
     *             If either role or resource is null
     */
    public void deny(Role role, String action, Resource resource);

    /**
     * <p>
     * Denies the given role to perform <b>any action</b> for the given
     * resource.
     * </p>
     * 
     * <p>
     * Example:<br />
     * denyAll(role, resource);<br />
     * deny(anotherRole, "write", resource);
     * </p>
     * <p>
     * hasAccess(role, "read", resource) - returns false<br />
     * hasAccess(role, "close", resource) - returns false<br />
     * hasAccess(role, "write", resource) - returns false<br />
     * allow(role, "write", resource);<br />
     * hasAccess(role, "write", resource) - returns true
     * </p>
     * 
     * @param role
     *            The role which is being assigned the permission
     * @param resource
     *            The resource to which the permission applies
     * @throws IllegalArgumentException
     *             If either role or resource is null
     */
    public void denyAll(Role role, Resource resource);

    /**
     * Removes any permissions set for the given role, for the given action in
     * the given resource.
     * 
     * @param role
     *            The role whose permissions are being removed
     * @param action
     *            The identifier for the action
     * @param resource
     *            The resource from which permissions are removed
     * 
     * @throws IllegalArgumentException
     *             If either role or resource is null
     */
    public void removePermission(Role role, String action, Resource resource);

    /**
     * Removes the ALL permission set for the given role in the given resource.
     * 
     * @param role
     *            The role whose permissions are being removed
     * @param resource
     *            The resource from which permissions are removed
     * 
     * @throws IllegalArgumentException
     *             If either role or resource is null
     */
    public void removeAllPermission(Role role, Resource resource);

    /**
     * Removes all permissions set for the given role in the given resource.
     * 
     * @param role
     *            The role whose permissions are being removed
     * @param resource
     *            The resource from which permissions are removed
     * 
     * @throws IllegalArgumentException
     *             If either role or resource is null
     */
    public void removeAllPermissions(Role role, Resource resource);

    /**
     * Checks if the given role has the permission to perform the given action
     * for the given resource. If no restrictions have been set for the
     * action-resource pair, then the role is granted access.
     * 
     * @param role
     *            The Role for which we want to check the permissions
     * @param action
     *            The identifier for the action for which we want to check the
     *            permissions. This value should be null if we want to check
     *            default permissions.
     * @param resource
     *            The resource for which the permission is being requested
     * @return True if role has access to the given action in the given resource
     * @throws IllegalArgumentException
     *             If either role or resource is null
     */
    public boolean hasAccess(Role role, String action, Resource resource);

    /**
     * Checks if the given roles has the permission to perform the given action
     * for the given resource. If no restrictions have been set for the
     * action-resource pair, then access is granted.
     * 
     * @param roles
     *            A set of roles for which we want to check the permissions
     * @param action
     *            The identifier for the action for which we want to check the
     *            permissions. This value should be null if we want to check
     *            default permissions.
     * @param resource
     *            The resource for which the permission is being requested
     * @return True if role has access to the given action in the given resource
     * @throws IllegalArgumentException
     *             If either role or resource is null
     */
    public boolean hasAccess(Set<Role> roles, String action, Resource resource);

}
