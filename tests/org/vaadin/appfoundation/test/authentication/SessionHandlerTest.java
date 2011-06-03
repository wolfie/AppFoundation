package org.vaadin.appfoundation.test.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.authentication.LogoutEvent;
import org.vaadin.appfoundation.authentication.LogoutListener;
import org.vaadin.appfoundation.authentication.SessionHandler;
import org.vaadin.appfoundation.authentication.data.User;
import org.vaadin.appfoundation.test.MockApplication;
import org.vaadin.appfoundation.test.ValueContainer;
import org.vaadin.appfoundation.test.MockApplication.MockContext;

public class SessionHandlerTest {

    private MockApplication application;
    private SessionHandler handler = null;

    @Before
    public void setUp() {
        // Create a new instance of the MockApplication
        application = new MockApplication();
        // Initialize the SessionHandler class with the MockApplication
        handler = new SessionHandler(application);
        handler.transactionStart(application, null);
    }

    @After
    public void tearDown() {
        handler.transactionEnd(application, null);
        handler = null;
    }

    @Test
    public void user() {
        User user = new User();
        assertNull(SessionHandler.get());

        SessionHandler.setUser(user);
        assertEquals(user, SessionHandler.get());
    }

    @Test
    public void logout() {
        User user = new User();
        SessionHandler.setUser(user);
        SessionHandler.logout();
        assertNull(SessionHandler.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeWithNullApplication() {
        SessionHandler.initialize(null);

    }

    @Test
    public void initialize() {
        MockContext context = (MockContext) application.getContext();
        assertEquals(0, context.getListeners().size());
        SessionHandler.initialize(application);
        assertEquals(1, context.getListeners().size());
        assertEquals(SessionHandler.class, context.getListeners().get(0)
                .getClass());
    }

    @Test
    public void addLogoutListener() {
        final ValueContainer value = new ValueContainer();
        LogoutListener listener = new LogoutListener() {

            public void logout(LogoutEvent event) {
                value.setValue(event.getUser());
            }
        };
        User user = new User();
        SessionHandler.initialize(new MockApplication());
        SessionHandler.addListener(listener);
        SessionHandler.setUser(user);
        SessionHandler.logout();

        assertEquals(user, value.getValue());
    }

    @Test
    public void removeLogoutListener() {
        final ValueContainer value = new ValueContainer();
        LogoutListener listener = new LogoutListener() {

            public void logout(LogoutEvent event) {
                value.setValue(event.getUser());
            }
        };
        User user = new User();
        SessionHandler.initialize(new MockApplication());
        SessionHandler.addListener(listener);
        SessionHandler.removeListener(listener);
        SessionHandler.setUser(user);
        SessionHandler.logout();

        assertNull(value.getValue());
    }
}
