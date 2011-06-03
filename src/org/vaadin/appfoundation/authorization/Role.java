package org.vaadin.appfoundation.authorization;

import java.util.Set;

/**
 * Interface for roles
 * 
 * @author Kim
 * 
 */
public interface Role {

    /**
     * A unique identifier for this role instance
     * 
     * @return
     */
    public String getIdentifier();

    /**
     * Roles are hierarchical, this method returns other roles that have been
     * <b>directly</b> assigned to this role.
     * 
     * @return
     */
    public Set<Role> getRoles();

    /**
     * Set all the roles which are <b>directly</b> assigned to this role.
     * 
     * @param roles
     */
    public void setRoles(Set<Role> roles);

    /**
     * Assign this role another sub-role.
     * 
     * @param role
     */
    public void addRole(Role role);

    /**
     * Remove an assigned sub-role from this role.
     * 
     * @param role
     */
    public void removeRole(Role role);

}
