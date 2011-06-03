package org.vaadin.appfoundation.test.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.authentication.LogoutEvent;
import org.vaadin.appfoundation.authentication.LogoutListener;
import org.vaadin.appfoundation.authentication.SessionHandler;
import org.vaadin.appfoundation.authentication.data.User;
import org.vaadin.appfoundation.authentication.exceptions.InvalidCredentialsException;
import org.vaadin.appfoundation.authentication.exceptions.PasswordRequirementException;
import org.vaadin.appfoundation.authentication.exceptions.PasswordsDoNotMatchException;
import org.vaadin.appfoundation.authentication.exceptions.TooShortPasswordException;
import org.vaadin.appfoundation.authentication.exceptions.TooShortUsernameException;
import org.vaadin.appfoundation.authentication.exceptions.UsernameExistsException;
import org.vaadin.appfoundation.authentication.util.PasswordUtil;
import org.vaadin.appfoundation.authentication.util.UserUtil;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;
import org.vaadin.appfoundation.test.MockApplication;
import org.vaadin.appfoundation.test.ValueContainer;

public class UserUtilTest {

    @Before
    public void setUp() throws InstantiationException, IllegalAccessException {
        Properties properties = new Properties();
        properties.setProperty("password.salt", "test");
        PasswordUtil.setProperties(properties);

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
        System.clearProperty("authentication.maxFailedPasswordChangeAttempts");
        System.clearProperty("authentication.password.validation.length");
        System.clearProperty("authentication.username.validation.length");
        System
                .clearProperty("authentication.password.validation.upperCaseRequired");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPropertiesWithNull() {
        UserUtil.setProperties(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPropertiesMissingProperty() {
        UserUtil.setProperties(new Properties());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPropertiesInvalidUsernameLenght() {
        Properties properties = new Properties();
        properties.setProperty("username.length.min", "test");
        properties.setProperty("password.length.min", "3");
        extracted(properties);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPropertiesInvalidPasswordLenght() {
        Properties properties = new Properties();
        properties.setProperty("username.length.min", "3");
        properties.setProperty("password.length.min", "test");
        extracted(properties);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPropertiesMissingUsername() {
        Properties properties = new Properties();
        properties.setProperty("password.length.min", "3");
        extracted(properties);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPropertiesMissingPassword() {
        Properties properties = new Properties();
        properties.setProperty("username.length.min", "3");
        extracted(properties);
    }

    @Test
    public void setProperties() {
        Properties properties = new Properties();
        properties.setProperty("username.length.min", "5");
        properties.setProperty("password.length.min", "3");
        extracted(properties);

        assertEquals(5, UserUtil.getMinUsernameLength());
        assertEquals(3, UserUtil.getMinPasswordLength());
    }

    @Test
    public void setPropertiesTwice() {
        Properties properties = new Properties();
        properties.setProperty("username.length.min", "5");
        properties.setProperty("password.length.min", "3");
        extracted(properties);

        // Properties is already set
        Properties properties2 = new Properties();
        properties2.setProperty("username.length.min", "3");
        properties2.setProperty("password.length.min", "5");
        extracted(properties2);

        assertEquals(3, UserUtil.getMinUsernameLength());
        assertEquals(5, UserUtil.getMinPasswordLength());
    }

    private void extracted(Properties properties2) {
        UserUtil.setProperties(properties2);
    }

    @Test
    public void storeUser() {
        User user = new User();
        assertNull(user.getId());

        UserUtil.storeUser(user);
        assertNotNull(user.getId());
    }

    @Test(expected = TooShortUsernameException.class)
    public void registerUserNullUsername() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        UserUtil.registerUser(null, "test1", "test1");
    }

    @Test(expected = TooShortUsernameException.class)
    public void registerUserShortUsername() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        UserUtil.registerUser("a", "test1", "test1");
    }

    @Test(expected = TooShortPasswordException.class)
    public void registerUserNullPassword() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        UserUtil.registerUser("test", null, null);
    }

    @Test(expected = TooShortPasswordException.class)
    public void registerUserShortPassword() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        UserUtil.registerUser("test", "a", "a");
    }

    @Test(expected = PasswordsDoNotMatchException.class)
    public void registerUserIncompatiblePassword()
            throws TooShortPasswordException, TooShortUsernameException,
            PasswordsDoNotMatchException, UsernameExistsException,
            PasswordRequirementException {
        System.setProperty("authentication.password.validation.length", "4");
        UserUtil.registerUser("test", "test1", "test2");
    }

    @Test(expected = UsernameExistsException.class)
    public void registerUserUsernameTaken() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        User user = new User();
        user.setUsername("test");
        FacadeFactory.getFacade().store(user);

        System.setProperty("authentication.password.validation.length", "4");
        UserUtil.registerUser("test", "test1", "test1");
    }

    @Test
    public void registerUser() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        System.setProperty("authentication.password.validation.length", "4");
        User user = UserUtil.registerUser("test", "test1", "test1");
        assertNotNull(user.getId());
        assertEquals("test", user.getUsername());
    }

    @Test
    public void getUser() {
        User user = new User();
        user.setUsername(UUID.randomUUID().toString());
        FacadeFactory.getFacade().store(user);

        User user2 = UserUtil.getUser(user.getId());
        assertEquals(user.getUsername(), user2.getUsername());
    }

    @Test(expected = InvalidCredentialsException.class)
    public void changePasswordIncorrectOld()
            throws InvalidCredentialsException, TooShortPasswordException,
            PasswordsDoNotMatchException, PasswordRequirementException {
        User user = new User();
        user.setUsername("test");
        // Hashed value of "foobar"+"test" (the salt value)
        user.setPassword("61e38e2b77827e10777ee8f1a138b7cfb1eb895");

        UserUtil.changePassword(user, "test", null, null);
    }

    @Test(expected = TooShortPasswordException.class)
    public void changePasswordTooShortNew() throws InvalidCredentialsException,
            TooShortPasswordException, PasswordsDoNotMatchException,
            PasswordRequirementException {
        User user = new User();
        user.setUsername("test");
        // Hashed value of "foobar"+"test" (the salt value)
        user.setPassword("61e38e2b77827e10777ee8f1a138b7cfb1eb895");

        UserUtil.changePassword(user, "foobar", "a", "a");
    }

    @Test(expected = TooShortPasswordException.class)
    public void changePasswordNullNew() throws InvalidCredentialsException,
            TooShortPasswordException, PasswordsDoNotMatchException,
            PasswordRequirementException {
        User user = new User();
        user.setUsername("test");
        // Hashed value of "foobar"+"test" (the salt value)
        user.setPassword("61e38e2b77827e10777ee8f1a138b7cfb1eb895");

        UserUtil.changePassword(user, "foobar", null, null);
    }

    @Test(expected = PasswordsDoNotMatchException.class)
    public void changePasswordNotMatch() throws InvalidCredentialsException,
            TooShortPasswordException, PasswordsDoNotMatchException,
            PasswordRequirementException {
        System.setProperty("authentication.password.validation.length", "4");
        User user = new User();
        user.setUsername("test");
        // Hashed value of "foobar"+"test" (the salt value)
        user.setPassword("61e38e2b77827e10777ee8f1a138b7cfb1eb895");

        UserUtil.changePassword(user, "foobar", "test1", "test2");
    }

    @Test
    public void changePassword() throws InvalidCredentialsException,
            TooShortPasswordException, PasswordsDoNotMatchException,
            PasswordRequirementException {
        System.setProperty("authentication.password.validation.length", "4");
        User user = new User();
        user.setUsername("test");
        // Hashed value of "foobar"+"test" (the salt value)
        user.setPassword("61e38e2b77827e10777ee8f1a138b7cfb1eb895");

        UserUtil.changePassword(user, "foobar", "testing", "testing");

        // Make sure the new hashed password is correct.
        // Hashed value of "testing"+"test" (the salt value)
        assertEquals("6b399df23c6b76d667f5e043d2dd13407a2245bb", user
                .getPassword());
    }

    @Test
    public void getMinPasswordLengthNullSetting() {
        System.clearProperty("authentication.password.validation.length");
        assertNull(System
                .getProperty("authentication.password.validation.length"));
        assertEquals(8, UserUtil.getMinPasswordLength());
        assertEquals("8", System
                .getProperty("authentication.password.validation.length"));
    }

    @Test(expected = TooShortPasswordException.class)
    public void minPasswordLength() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        System.setProperty("authentication.password.validation.length", "4");
        UserUtil.registerUser("testing", "123", "123");
    }

    @Test
    public void minPasswordLengthOk() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        System.setProperty("authentication.password.validation.length", "4");
        User user = UserUtil.registerUser("testing", "1234", "1234");
        assertNotNull(user);
    }

    @Test(expected = TooShortUsernameException.class)
    public void minUsernameLength() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        System.setProperty("authentication.username.validation.length", "4");
        UserUtil.registerUser("tes", "123456789", "123456789");

    }

    @Test
    public void minUsernameLengthOk() throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {
        System.setProperty("authentication.username.validation.length", "4");
        User user = UserUtil.registerUser("test", "123456789", "123456789");
        assertNotNull(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMinPasswordLengthInvalidSetting() {
        System.setProperty("authentication.password.validation.length", "test");
        UserUtil.getMinPasswordLength();
    }

    @Test
    public void getMinUsernameLengthNullSetting() {
        System.clearProperty("authentication.username.validation.length");
        assertNull(System
                .getProperty("authentication.username.validation.length"));
        assertEquals(4, UserUtil.getMinUsernameLength());
        assertEquals("4", System
                .getProperty("authentication.username.validation.length"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMinUsernameLengthInvalidSetting() {
        System.setProperty("authentication.username.validation.length", "test");
        UserUtil.getMinUsernameLength();
    }

    @Test
    public void logoutAfterFailedPasswordChangeAttempts()
            throws TooShortPasswordException, TooShortUsernameException,
            PasswordsDoNotMatchException, UsernameExistsException,
            PasswordRequirementException {
        final ValueContainer value = new ValueContainer(false);
        LogoutListener listener = new LogoutListener() {
            public void logout(LogoutEvent event) {
                value.setValue(true);
            }
        };

        SessionHandler.initialize(new MockApplication());
        SessionHandler.addListener(listener);

        User user = UserUtil.registerUser("user", "foobar123", "foobar123");

        for (int i = 0; i < 6; i++) {
            try {
                UserUtil.changePassword(user, "test", "testing123",
                        "testing123");
            } catch (InvalidCredentialsException e) {
                // Ignore
            }
        }

        assertTrue((Boolean) value.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidValueForMaxAllowedPasswordChangeAttempts()
            throws TooShortPasswordException, TooShortUsernameException,
            PasswordsDoNotMatchException, UsernameExistsException,
            PasswordRequirementException {
        System.setProperty("authentication.maxFailedPasswordChangeAttempts",
                "test");
        User user = UserUtil.registerUser("user", "foobar123", "foobar123");

        try {
            UserUtil.changePassword(user, "test", "testing123", "testing123");
        } catch (InvalidCredentialsException e) {
            // Ignore
        }
    }

    @Test
    public void clearingOfFailedPwdAttempts() throws TooShortPasswordException,
            TooShortUsernameException, UsernameExistsException,
            InvalidCredentialsException, PasswordsDoNotMatchException,
            PasswordRequirementException {
        User user = UserUtil.registerUser("user", "foobar123", "foobar123");

        try {
            UserUtil.changePassword(user, "test", "testing123", "testing123");
        } catch (InvalidCredentialsException e) {
            // Ignore
        }

        assertEquals(1, user.getFailedPasswordChangeAttemps());

        try {
            UserUtil.changePassword(user, "foobar123", "testing1234",
                    "testing123");
        } catch (PasswordsDoNotMatchException e) {
            // Expected
        }
        assertEquals(0, user.getFailedPasswordChangeAttemps());
    }

    @Test(expected = PasswordRequirementException.class)
    public void passwordRequirementsInRegistering()
            throws TooShortPasswordException, TooShortUsernameException,
            PasswordsDoNotMatchException, UsernameExistsException,
            PasswordRequirementException {
        System.setProperty("authentication.password.validation.length", "4");
        System.setProperty(
                "authentication.password.validation.upperCaseRequired", "true");
        UserUtil.registerUser("testing", "test", "test");
    }

    @Test(expected = PasswordRequirementException.class)
    public void passwordRequirementsInChangePassword()
            throws TooShortPasswordException, TooShortUsernameException,
            PasswordsDoNotMatchException, UsernameExistsException,
            PasswordRequirementException, InvalidCredentialsException {
        System.setProperty("authentication.password.validation.length", "4");
        User user = UserUtil.registerUser("testing", "test", "test");
        System.setProperty(
                "authentication.password.validation.upperCaseRequired", "true");
        UserUtil.changePassword(user, "test", "test", "test");
    }

}
