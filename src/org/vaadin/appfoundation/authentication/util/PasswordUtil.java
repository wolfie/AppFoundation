package org.vaadin.appfoundation.authentication.util;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vaadin.appfoundation.authentication.data.User;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.TextField;

/**
 * Utility class containing useful helper methods related to passwords.
 * 
 * @author Kim
 * 
 */
public class PasswordUtil implements Serializable {

    private static final long serialVersionUID = 7823991334001022227L;

    // Store the password salt in a static variable
    private static String salt = null;

    /**
     * Get the salt value for the passwords
     * 
     * @return
     */
    protected static String getSalt() {
        // Check if the salt has been set. If not, then create a default salt
        // value.
        String systemSalt = System.getProperty("authentication.password.salt");
        if (salt != null && !salt.equals(systemSalt)) {
            throw new UnsupportedOperationException(
                    "Password salt is already set");
        }

        if (salt == null && systemSalt != null) {
            salt = systemSalt;
        }

        if (salt == null) {
            salt = ")%gersK43q5)=%3qiyt34389py43pqhgwer8l9";
            System.setProperty("authentication.password.salt", salt);
        }

        return salt;
    }

    /**
     * Set the properties for the PasswordUtil. The properties must contain the
     * password.salt -property.
     * 
     * @param properties
     * @deprecated Use System.setProperties instead
     * 
     */
    @Deprecated
    public static void setProperties(Properties properties) {
        // Make sure we don't get null values
        if (properties == null) {
            throw new IllegalArgumentException("Properties may not be null");
        }

        // Make sure we have the needed property
        if (!properties.containsKey("password.salt")) {
            throw new IllegalArgumentException(
                    "Properties must contain the password.salt -property");
        }

        // Salt should only be defined once. If it is already defined, then an
        // exception should be thrown
        if (salt == null) {
            salt = properties.getProperty("password.salt");
            System.setProperty("authentication.password.salt", salt);
        } else {
            throw new UnsupportedOperationException(
                    "Password salt is already set");
        }
    }

    /**
     * Verify if the given password (unhashed) matches with the user's password
     * 
     * @param user
     *            User to whome's password we are comparing
     * @param password
     *            The unhashed password we are comparing
     * @return Returns true if passwords match, otherwise false
     */
    public static boolean verifyPassword(User user, String password) {
        // Return null if either the username or password is null
        if (user == null || password == null) {
            return false;
        }

        // Hash the generated password
        String hashedPassword = generateHashedPassword(password);

        // Check if the password matches with the one stored in the User object
        if (user.getPassword().equals(hashedPassword)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generates a hashed password of the given string.
     * 
     * @param password
     *            String which is to be hashed
     * @return Hashed password
     */
    public static String generateHashedPassword(String password) {
        StringBuffer hashedPassword = new StringBuffer();

        // Get a byte array of the password concatenated with the password salt
        // value
        byte[] defaultBytes = (password + getSalt()).getBytes();
        try {
            // Perform the hashing with SHA
            MessageDigest algorithm = MessageDigest.getInstance("SHA");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();

            for (int i = 0; i < messageDigest.length; i++) {
                hashedPassword.append(Integer
                        .toHexString(0xFF & messageDigest[i]));
            }
        } catch (NoSuchAlgorithmException nsae) {

        }

        return hashedPassword.toString();
    }

    /**
     * Validates that the password has met all set requirements
     * 
     * @param password
     *            String to be checked
     * @return True if password meets all requirements, false if not
     */
    public static boolean isValid(String password) {
        List<Validator> validators = getValidators();
        for (Validator validator : validators) {
            if (!validator.isValid(password)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a list of {@link Validator} objects which can be attached to
     * Vaadin {@link TextField}s. The Validators are built based on the the
     * paswword requirements defined.
     * 
     * @return List of validators
     */
    public static List<Validator> getValidators() {
        List<Validator> validators = new ArrayList<Validator>();

        validators.add(new StringLengthValidator("Password is too short",
                getMinPasswordLength(), 999999, false));

        if (isLowerCaseRequired()) {
            validators.add(new RegexpValidator(".*[a-z].*",
                    "The password must contain lower case letters (a-z)"));
        }

        if (isUpperCaseRequired()) {
            validators.add(new RegexpValidator(".*[A-Z].*",
                    "The password must contain upper case latters (A-Z)"));
        }

        if (isNumericRequired()) {
            validators.add(new RegexpValidator(".*[0-9].*",
                    "The password must contain numbers)"));
        }

        if (isSpecialCharacterRequired()) {
            validators.add(new RegexpValidator(".*[^a-zA-Z0-9].*",
                    "The password must contain characters other than "
                            + "letters from A to Z or numbers"));
        }
        return validators;
    }

    /**
     * Checks if lower case letters (a-z) are required to be present in the
     * password.
     * 
     * @return True if lower case letters are required, otherwise false
     */
    private static boolean isLowerCaseRequired() {
        return Boolean
                .getBoolean("authentication.password.validation.lowerCaseRequired");
    }

    /**
     * Checks if upper case letters (A-Z) are required to be present in the
     * password.
     * 
     * @return True if upper case letters are required, otherwise false
     */
    private static boolean isUpperCaseRequired() {
        return Boolean
                .getBoolean("authentication.password.validation.upperCaseRequired");
    }

    /**
     * Checks if numbers (0-9) are required to be present in the password.
     * 
     * @return True if numbers are required, otherwise false
     */
    private static boolean isNumericRequired() {
        return Boolean
                .getBoolean("authentication.password.validation.numericRequired");
    }

    /**
     * Checks if special characters (anything else than numbers and letters from
     * a to z) are required to be present in the password.
     * 
     * @return True if special characters are required, otherwise false
     */
    private static boolean isSpecialCharacterRequired() {
        return Boolean
                .getBoolean("authentication.password.validation.specialCharacterRequired");
    }

    /**
     * Returns the minimum length of a password
     * 
     * @return Minimum password length
     */
    public static int getMinPasswordLength() {
        String minLenghtStr = System
                .getProperty("authentication.password.validation.length");
        int minLenght = 8;
        if (minLenghtStr == null) {
            System
                    .setProperty("authentication.password.validation.length",
                            "8");
            return minLenght;
        }

        try {
            minLenght = Integer.valueOf(minLenghtStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "authentication.password.validation.length must be an integer");
        }

        return minLenght;
    }
}
