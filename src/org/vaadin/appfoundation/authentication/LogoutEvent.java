package org.vaadin.appfoundation.authentication;

import java.io.Serializable;

import org.vaadin.appfoundation.authentication.data.User;

/**
 * Event which is dispatched when a user logs out.
 * 
 * @author Kim
 * 
 */
public class LogoutEvent implements Serializable {

    private static final long serialVersionUID = -5497435495187618081L;

    private User user;

    /**
     * 
     * @param user
     *            The user who is been logged out
     */
    public LogoutEvent(User user) {
        this.user = user;
    }

    /**
     * Get the user who has been logged out
     * 
     * @return The user object of the logged out user
     */
    public User getUser() {
        return user;
    }

}
