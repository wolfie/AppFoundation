package org.vaadin.appfoundation.test.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.authentication.SessionHandler;
import org.vaadin.appfoundation.authentication.data.User;
import org.vaadin.appfoundation.authentication.exceptions.AccountLockedException;
import org.vaadin.appfoundation.authentication.exceptions.InvalidCredentialsException;
import org.vaadin.appfoundation.authentication.util.AuthenticationUtil;
import org.vaadin.appfoundation.authentication.util.PasswordUtil;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;
import org.vaadin.appfoundation.test.MockApplication;

public class AuthenticationUtilTest {

    @Before
    public void setUp() throws InstantiationException, IllegalAccessException {
        SessionHandler.initialize(new MockApplication());
        FacadeFactory.registerFacade("default", true);
    }

    @After
    public void tearDown() throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        FacadeFactory.clear();
        Field field = PasswordUtil.class.getDeclaredField("salt");
        field.setAccessible(true);
        field.set(null, null);

        System.clearProperty("authentication.password.salt");
    }

    @Test(expected = InvalidCredentialsException.class)
    public void authenticateNoUsername() throws InvalidCredentialsException,
            AccountLockedException {
        AuthenticationUtil.authenticate(null, "foo");
    }

    @Test(expected = InvalidCredentialsException.class)
    public void authenticateNoPassword() throws InvalidCredentialsException,
            AccountLockedException {
        AuthenticationUtil.authenticate("foo", null);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void authenticationUserNotFound()
            throws InvalidCredentialsException, AccountLockedException {
        AuthenticationUtil.authenticate("foo", "foo");
    }

    @Test(expected = InvalidCredentialsException.class)
    public void authenticationInvalidPassword()
            throws InvalidCredentialsException, AccountLockedException {
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        FacadeFactory.getFacade().store(user);

        AuthenticationUtil.authenticate("test", "foo");
    }

    @Test
    public void authenticate() throws InvalidCredentialsException,
            AccountLockedException {
        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.generateHashedPassword("foobar"));

        FacadeFactory.getFacade().store(user);
        User authenticatedUser = AuthenticationUtil.authenticate("test",
                "foobar");
        assertEquals(user.getUsername(), authenticatedUser.getUsername());
        assertEquals(user.getPassword(), authenticatedUser.getPassword());
        assertEquals(user.getId(), authenticatedUser.getId());
    }

    @Test
    public void incrementFailedLoginAttempts() throws AccountLockedException {
        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.generateHashedPassword("foobar"));
        assertEquals(0, user.getFailedLoginAttempts());

        FacadeFactory.getFacade().store(user);
        try {
            AuthenticationUtil.authenticate("test", "test");
        } catch (InvalidCredentialsException e) {
            // This is expected
        }

        user = FacadeFactory.getFacade().find(User.class, user.getId());
        assertEquals(1, user.getFailedLoginAttempts());
    }

    @Test
    public void clearFailedLoginAttempts() throws InvalidCredentialsException,
            AccountLockedException {
        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.generateHashedPassword("foobar"));
        assertEquals(0, user.getFailedLoginAttempts());

        FacadeFactory.getFacade().store(user);
        try {
            AuthenticationUtil.authenticate("test", "test");
        } catch (InvalidCredentialsException e) {
            // This is expected
        }
        AuthenticationUtil.authenticate("test", "foobar");
        user = FacadeFactory.getFacade().find(User.class, user.getId());
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test(expected = AccountLockedException.class)
    public void lockAccountAfterFailedAttempts() throws AccountLockedException {
        System.setProperty("authentication.maxFailedLoginAttempts", "3");

        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.generateHashedPassword("foobar"));

        FacadeFactory.getFacade().store(user);
        for (int i = 0; i < 4; i++) {
            if (i == 3) {
                assertTrue(true);
            }
            try {
                AuthenticationUtil.authenticate("test", "test");
            } catch (InvalidCredentialsException e) {
                // This is expected
            }
        }
    }

    @Test
    public void getReasonForLocking() {
        System.setProperty("authentication.maxFailedLoginAttempts", "0");

        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.generateHashedPassword("foobar"));

        FacadeFactory.getFacade().store(user);
        try {
            AuthenticationUtil.authenticate("test", "test");
        } catch (InvalidCredentialsException e) {
            // This is expected
        } catch (AccountLockedException e) {
            // Expected
        }

        user = FacadeFactory.getFacade().find(User.class, user.getId());
        assertEquals("tooManyLoginAttempts", user.getReasonForLockedAccount());
    }

    @Test(expected = AccountLockedException.class)
    public void exceptionWhenAccountIsLocked() throws AccountLockedException,
            InvalidCredentialsException {
        User user = new User();
        user.setUsername("test");
        user.setAccountLocked(true);
        user.setPassword(PasswordUtil.generateHashedPassword("foobar"));

        FacadeFactory.getFacade().store(user);
        AuthenticationUtil.authenticate("test", "foobar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidNumberOfFailedAttempts() throws AccountLockedException,
            InvalidCredentialsException {
        System.setProperty("authentication.maxFailedLoginAttempts", "test");

        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.generateHashedPassword("foobar"));

        FacadeFactory.getFacade().store(user);
        AuthenticationUtil.authenticate("test", "test");
    }

    @Test
    public void accountUnlocked() throws AccountLockedException,
            InvalidCredentialsException {
        System.setProperty("authentication.maxFailedLoginAttempts", "3");

        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.generateHashedPassword("foobar"));

        FacadeFactory.getFacade().store(user);

        long id = user.getId();
        try {
            for (int i = 0; i < 4; i++) {
                try {
                    AuthenticationUtil.authenticate("test", "test");
                } catch (InvalidCredentialsException e) {
                    // This is expected
                }
            }
        } catch (AccountLockedException e) {
            user = FacadeFactory.getFacade().find(User.class, user.getId());
            user.clearFailedLoginAttempts();
            user.setAccountLocked(false);
            FacadeFactory.getFacade().store(user);
            user = AuthenticationUtil.authenticate("test", "foobar");
            assertNotNull(user);
            assertEquals((long) id, (long) user.getId());
        }
    }

}
