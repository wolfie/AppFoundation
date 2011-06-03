package org.vaadin.appfoundation.test.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Test;
import org.vaadin.appfoundation.authentication.data.User;
import org.vaadin.appfoundation.authentication.util.PasswordUtil;

import com.vaadin.data.Validator;

public class PasswordUtilTest {

    @After
    public void tearDown() throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field field = PasswordUtil.class.getDeclaredField("salt");
        field.setAccessible(true);
        field.set(null, null);

        System.clearProperty("authentication.password.salt");
        System
                .clearProperty("authentication.password.validation.specialCharacterRequired");
        System
                .clearProperty("authentication.password.validation.numericRequired");
        System
                .clearProperty("authentication.password.validation.upperCaseRequired");
        System
                .clearProperty("authentication.password.validation.lowerCaseRequired");
        System.clearProperty("authentication.password.validation.length");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPropertiesWithNull() {
        PasswordUtil.setProperties(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPropertiesMissingProperty() {
        PasswordUtil.setProperties(new Properties());
    }

    @Test
    public void setProperties() {
        Properties properties = new Properties();
        properties.setProperty("password.salt", "test");
        PasswordUtil.setProperties(properties);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setPropertiesTwice() {
        // Properties is already set
        Properties properties = new Properties();
        properties.setProperty("password.salt", "foobar");
        PasswordUtil.setProperties(properties);

        properties.setProperty("password.salt", "foobar2");
        PasswordUtil.setProperties(properties);
    }

    @Test
    public void verifyPasswordNullUser() {
        assertFalse(PasswordUtil.verifyPassword(null, "foo"));
    }

    @Test
    public void verifyPasswordNullPassword() {
        assertFalse(PasswordUtil.verifyPassword(new User(), null));
    }

    @Test
    public void verifyPasswordWrongPassword() {
        User user = new User();
        // Note that the password should be hashed for this to pass
        user.setPassword("test");
        assertFalse(PasswordUtil.verifyPassword(user, "test"));
    }

    @Test
    public void verifyPassword() {
        Properties properties = new Properties();
        properties.setProperty("password.salt", "test");
        PasswordUtil.setProperties(properties);

        User user = new User();
        // Hashed value of "foobar"+"test" (the salt value)
        user.setPassword("61e38e2b77827e10777ee8f1a138b7cfb1eb895");
        assertTrue(PasswordUtil.verifyPassword(user, "foobar"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getSaltSaltAlreadySet() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Field field = PasswordUtil.class.getDeclaredField("salt");
        field.setAccessible(true);
        field.set(null, "foobar");

        System.setProperty("authentication.password.salt", "test");
        PasswordUtil.generateHashedPassword("test");
    }

    @Test
    public void generateDefaultSalt() {
        assertEquals("a4f1fbb1274f1fceba9dfae181d7afe6fca96f", PasswordUtil
                .generateHashedPassword("foobar"));
    }

    @Test
    public void useSystemSalt() {
        System.setProperty("authentication.password.salt", "test");
        assertEquals("51abb9636078defbf888d8457a7c76f85c8f114c", PasswordUtil
                .generateHashedPassword("test"));
    }

    @Test
    public void isValidNull() {
        assertFalse(PasswordUtil.isValid(null));
    }

    @Test
    public void isValidOnlyLenght() {
        assertFalse(PasswordUtil.isValid("test"));
        assertTrue(PasswordUtil.isValid("test-test-test"));
    }

    @Test
    public void isValidLengthAndLowerCase() {
        System.setProperty("authentication.password.validation.length", "4");
        System.setProperty(
                "authentication.password.validation.lowerCaseRequired", "true");
        assertFalse(PasswordUtil.isValid("TEST"));
        assertFalse(PasswordUtil.isValid("tes"));
        assertTrue(PasswordUtil.isValid("test"));
    }

    @Test
    public void isValidLengthAndUpperCase() {
        System.setProperty("authentication.password.validation.length", "4");
        System.setProperty(
                "authentication.password.validation.upperCaseRequired", "true");
        assertFalse(PasswordUtil.isValid("test"));
        assertFalse(PasswordUtil.isValid("TES"));
        assertTrue(PasswordUtil.isValid("TEST"));
    }

    @Test
    public void isValidNumeric() {
        System.setProperty("authentication.password.validation.length", "4");
        System.setProperty(
                "authentication.password.validation.numericRequired", "true");
        assertFalse(PasswordUtil.isValid("test"));
        assertTrue(PasswordUtil.isValid("test1"));
    }

    @Test
    public void isValidSpecialCharacters() {
        System.setProperty("authentication.password.validation.length", "4");
        System.setProperty(
                "authentication.password.validation.specialCharacterRequired",
                "true");
        assertFalse(PasswordUtil.isValid("test"));
        assertTrue(PasswordUtil.isValid("test-"));
        assertTrue(PasswordUtil.isValid("testå"));
    }

    @Test
    public void getValidatorsNull() {
        List<Validator> validators = PasswordUtil.getValidators();
        boolean passed = true;
        for (Validator v : validators) {
            if (!v.isValid(null)) {
                passed = false;
            }
        }

        assertFalse(passed);
    }

    @Test
    public void getValidatorsOnlyLenght() {
        List<Validator> validators = PasswordUtil.getValidators();
        boolean passed = true;
        for (Validator v : validators) {
            if (!v.isValid("test")) {
                passed = false;
            }
        }

        assertFalse(passed);

        passed = true;
        for (Validator v : validators) {
            if (!v.isValid("test-test-test")) {
                passed = false;
            }
        }

        assertTrue(passed);
    }

    @Test
    public void getValidatorsLengthAndLowerCase() {
        System.setProperty("authentication.password.validation.length", "4");
        System.setProperty(
                "authentication.password.validation.lowerCaseRequired", "true");
        List<Validator> validators = PasswordUtil.getValidators();
        boolean passed = true;
        for (Validator v : validators) {
            if (!v.isValid("TEST")) {
                passed = false;
            }
        }

        assertFalse(passed);
        passed = true;

        for (Validator v : validators) {
            if (!v.isValid("tes")) {
                passed = false;
            }
        }

        assertFalse(passed);

        passed = true;
        for (Validator v : validators) {
            if (!v.isValid("test")) {
                passed = false;
            }
        }

        assertTrue(passed);
    }

    @Test
    public void getValidatorsLengthAndUpperCase() {
        System.setProperty("authentication.password.validation.length", "4");
        System.setProperty(
                "authentication.password.validation.upperCaseRequired", "true");

        List<Validator> validators = PasswordUtil.getValidators();
        boolean passed = true;
        for (Validator v : validators) {
            if (!v.isValid("test")) {
                passed = false;
            }
        }

        assertFalse(passed);
        passed = true;

        for (Validator v : validators) {
            if (!v.isValid("TES")) {
                passed = false;
            }
        }

        assertFalse(passed);

        passed = true;
        for (Validator v : validators) {
            if (!v.isValid("TEST")) {
                passed = false;
            }
        }

        assertTrue(passed);
    }

    @Test
    public void getValidatorsNumeric() {
        System.setProperty("authentication.password.validation.length", "4");
        System.setProperty(
                "authentication.password.validation.numericRequired", "true");
        List<Validator> validators = PasswordUtil.getValidators();
        boolean passed = true;
        for (Validator v : validators) {
            if (!v.isValid("test")) {
                passed = false;
            }
        }

        assertFalse(passed);

        passed = true;
        for (Validator v : validators) {
            if (!v.isValid("test1")) {
                passed = false;
            }
        }

        assertTrue(passed);
    }

    @Test
    public void getValidatorsSpecialCharacters() {
        System.setProperty("authentication.password.validation.length", "4");
        System.setProperty(
                "authentication.password.validation.specialCharacterRequired",
                "true");
        List<Validator> validators = PasswordUtil.getValidators();
        boolean passed = true;
        for (Validator v : validators) {
            if (!v.isValid("test")) {
                passed = false;
            }
        }

        assertFalse(passed);
        passed = true;

        for (Validator v : validators) {
            if (!v.isValid("test-")) {
                passed = false;
            }
        }

        assertTrue(passed);

        passed = true;
        for (Validator v : validators) {
            if (!v.isValid("testå")) {
                passed = false;
            }
        }

        assertTrue(passed);
    }

}
