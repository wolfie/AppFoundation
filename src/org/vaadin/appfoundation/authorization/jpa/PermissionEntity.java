package org.vaadin.appfoundation.authorization.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.vaadin.appfoundation.persistence.data.AbstractPojo;

/**
 * This entity class is used for persisting permissions used by the
 * {@link JPAPermissionManager}.
 * 
 * @author Kim
 * 
 */
@Entity
public class PermissionEntity extends AbstractPojo {

    private static final long serialVersionUID = 3895345053504819128L;

    @Enumerated(EnumType.STRING)
    private PermissionType type;

    private String role;

    @Column(name = "perm_resource")
    private String resource;

    private String action;

    /**
     * Default constructor.
     */
    public PermissionEntity() {

    }

    /**
     * Alternative constructor, sets the permission type upon initialization.
     * 
     * @param type
     *            The permission's type
     */
    public PermissionEntity(PermissionType type) {
        this.type = type;
    }

    /**
     * Sets the type of the permission
     * 
     * @param type
     *            The permission's type
     */
    public void setType(PermissionType type) {
        this.type = type;
    }

    /**
     * Returns the type of the permission
     * 
     * @return The permission's type
     */
    public PermissionType getType() {
        return type;
    }

    /**
     * Get the identifier of the role for which this permission is being applied
     * on.
     * 
     * @return The role for which the permission is being set
     */
    public String getRole() {
        return role;
    }

    /**
     * Set the identifier of the role for which this permission is being applied
     * on.
     * 
     * @param role
     *            The role for which the permission is being set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Get the resource's identifier for which this permission is being applied
     * on.
     * 
     * @return The resource's identifier
     */
    public String getResource() {
        return resource;
    }

    /**
     * Set the resource's identifier for which this permission is being applied
     * on.
     * 
     * @param resource
     *            The resource's identifier
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * Get the action for which this permission is being applied on.
     * 
     * @return The action parameter for this permission
     */
    public String getAction() {
        return action;
    }

    /**
     * Set the action for which this permission is being applied on.
     * 
     * @param action
     *            The action parameter for this permission
     */
    public void setAction(String action) {
        this.action = action;
    }
}
