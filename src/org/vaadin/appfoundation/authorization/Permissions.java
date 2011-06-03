package org.vaadin.appfoundation.authorization;

import java.util.Set;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * Utility class for invoking permission manager's method in a static way.
 * 
 * @author Kim
 * 
 */
public class Permissions implements TransactionListener {

    private static final long serialVersionUID = -7370158934115935499L;

    private static ThreadLocal<Permissions> instance = new ThreadLocal<Permissions>();

    private final Application application;

    private final PermissionManager pm;

    /**
     * Constructor.
     * 
     * @param application
     *            Application instance for which this object belongs
     * @param manager
     *            The permission manager to be used
     */
    public Permissions(Application application, PermissionManager manager) {
        if (application == null) {
            throw new IllegalArgumentException("Application must be set");
        }

        if (manager == null) {
            throw new IllegalArgumentException("PermissionManager must be set");
        }

        instance.set(this);
        this.application = application;
        pm = manager;
    }

    /**
     * {@inheritDoc}
     */
    public void transactionEnd(Application application, Object transactionData) {
        // Clear thread local instance at the end of the transaction
        if (this.application == application) {
            instance.set(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void transactionStart(Application application, Object transactionData) {
        // Set the thread local instance
        if (this.application == application) {
            instance.set(this);
        }
    }

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
    public static void allow(Role role, String action, Resource resource) {
        instance.get().pm.allow(role, action, resource);
    }

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
    public static void allowAll(Role role, Resource resource) {
        instance.get().pm.allowAll(role, resource);
    }

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
    public static void deny(Role role, String action, Resource resource) {
        instance.get().pm.deny(role, action, resource);
    }

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
    public static void denyAll(Role role, Resource resource) {
        instance.get().pm.denyAll(role, resource);
    }

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
    public static boolean hasAccess(Role role, String action, Resource resource) {
        return instance.get().pm.hasAccess(role, action, resource);
    }

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
    public static boolean hasAccess(Set<Role> roles, String action,
            Resource resource) {
        return instance.get().pm.hasAccess(roles, action, resource);
    }

    /**
     * Initializes the {@link PermissionManager} for the given
     * {@link Application}
     * 
     * @param application
     */
    public static void initialize(Application application,
            PermissionManager manager) {
        if (application == null) {
            throw new IllegalArgumentException("Application may not be null");
        }

        if (manager == null) {
            throw new IllegalArgumentException("PermissionManager must be set");
        }

        Permissions p = new Permissions(application, manager);
        application.getContext().addTransactionListener(p);

    }

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
    public static void removePermission(Role role, String action,
            Resource resource) {
        instance.get().pm.removePermission(role, action, resource);
    }

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
    public static void removeAllPermission(Role role, Resource resource) {
        instance.get().pm.removeAllPermission(role, resource);
    }

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
    public static void removeAllPermissions(Role role, Resource resource) {
        instance.get().pm.removeAllPermissions(role, resource);
    }

}
