package org.vaadin.appfoundation.authentication;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.appfoundation.authentication.data.User;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * A utility class for handling user sessions
 * 
 * @author Kim
 * 
 */
public class SessionHandler implements TransactionListener {

    private static final long serialVersionUID = 4142938996955537395L;

    private final Application application;

    private User user;

    private List<LogoutListener> listeners = new ArrayList<LogoutListener>();

    // Store the user object of the currently inlogged user
    private static ThreadLocal<SessionHandler> instance = new ThreadLocal<SessionHandler>();

    /**
     * Constructor
     * 
     * @param application
     *            Current application instance
     */
    public SessionHandler(Application application) {
        this.application = application;
        instance.set(this);
    }

    /**
     * {@inheritDoc}
     */
    public void transactionEnd(Application application, Object transactionData) {
        // Clear the currentApplication field
        if (this.application == application) {
            instance.set(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void transactionStart(Application application, Object transactionData) {
        // Check if the application instance we got as parameter is actually
        // this application instance. If it is, then we should define the thread
        // local variable for this request.
        if (this.application == application) {
            // Set the current user
            instance.set(this);
        }
    }

    /**
     * Set the User object for the currently inlogged user for this application
     * instance
     * 
     * @param user
     */
    public static void setUser(User user) {
        instance.get().user = user;
    }

    /**
     * Get the User object of the currently inlogged user for this application
     * instance.
     * 
     * @return The currently inlogged user
     */
    public static User get() {
        return instance.get().user;
    }

    /**
     * Method for logging out a user
     */
    public static void logout() {
        LogoutEvent event = new LogoutEvent(instance.get().user);
        setUser(null);
        dispatchLogoutEvent(event);
    }

    /**
     * Dispatches the {@link LogoutEvent} to all registered logout listeners.
     * 
     * @param event
     *            The LogoutEvent
     */
    private static void dispatchLogoutEvent(LogoutEvent event) {
        for (LogoutListener listener : instance.get().listeners) {
            listener.logout(event);
        }
    }

    /**
     * Initializes the {@link SessionHandler} for the given {@link Application}
     * 
     * @param application
     */
    public static void initialize(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("Application may not be null");
        }
        SessionHandler handler = new SessionHandler(application);
        application.getContext().addTransactionListener(handler);
    }

    /**
     * Add a logout listener.
     * 
     * @param listener
     */
    public static void addListener(LogoutListener listener) {
        instance.get().listeners.add(listener);
    }

    /**
     * Remove the given listener from the active logout listeners.
     * 
     * @param listener
     */
    public static void removeListener(LogoutListener listener) {
        instance.get().listeners.remove(listener);
    }

}
