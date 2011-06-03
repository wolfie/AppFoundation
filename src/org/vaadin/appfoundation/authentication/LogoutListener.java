package org.vaadin.appfoundation.authentication;

/**
 * Interface for listening to logout events.
 * 
 * @author Kim
 * 
 */
public interface LogoutListener {

    /**
     * Called when a logout occurs in the application instance.
     * 
     * @param event
     *            LogoutEvent containing the user who has been logged out
     */
    public void logout(LogoutEvent event);

}
