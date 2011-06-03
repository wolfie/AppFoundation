package org.vaadin.appfoundation.authorization;

/**
 * <p>
 * This is an enumeration that lists the different types of results a permission
 * check may return. The idea with this enumeration is to allow the application
 * to check if a permission check granted/denied access based on explicit or
 * implicit access rules.
 * </p>
 * 
 * <p>
 * The Permission rules can be read at
 * 
 * {@link http://code.google.com/p/vaadin-appfoundation/wiki/PermissionRules}
 * </p>
 * 
 * @author Kim
 * 
 */
public enum PermissionResultType {

    /**
     * The role is explicitly allowed the access to the resource/action
     */
    ALLOW_EXPLICITLY,

    /**
     * No explicit rules found, role is implicitly allowed access to the
     * resource/action
     */
    ALLOW_IMPLICITLY,

    /**
     * The role is explicitly denied the access to the resource/action
     */
    DENY_EXPLICITLY,

    /**
     * No explicit rules found, role is implicitly denied access to the
     * resource/action
     */
    DENY_IMPLICITLY;

}
