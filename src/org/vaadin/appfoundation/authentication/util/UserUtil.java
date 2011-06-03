package org.vaadin.appfoundation.authentication.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.vaadin.appfoundation.authentication.SessionHandler;
import org.vaadin.appfoundation.authentication.data.User;
import org.vaadin.appfoundation.authentication.exceptions.InvalidCredentialsException;
import org.vaadin.appfoundation.authentication.exceptions.PasswordRequirementException;
import org.vaadin.appfoundation.authentication.exceptions.PasswordsDoNotMatchException;
import org.vaadin.appfoundation.authentication.exceptions.TooShortPasswordException;
import org.vaadin.appfoundation.authentication.exceptions.TooShortUsernameException;
import org.vaadin.appfoundation.authentication.exceptions.UsernameExistsException;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;

/**
 * Controller for managing user accounts
 * 
 * @author Kim
 * 
 */
public class UserUtil implements Serializable {

    private static final long serialVersionUID = 6394812141386916155L;

    /**
     * Get the user object with the given primary key
     * 
     * @param id
     * @return An instance of User
     */
    public static User getUser(Long id) {
        return FacadeFactory.getFacade().find(User.class, id);
    }

    /**
     * This method tries to register a new user
     * 
     * @param username
     *            Desired username
     * @param password
     *            Desired password
     * @param verifyPassword
     *            Verification of the desired password
     * @return The created user instance
     * @throws TooShortPasswordException
     *             Thrown if the given password is too short
     * @throws TooShortUsernameException
     *             Thrown i the given username is too short
     * @throws PasswordsDoNotMatchException
     *             Thrown i the password verification fails
     * @throws UsernameExistsException
     *             Thrown i the username already exists
     */
    public static User registerUser(String username, String password,
            String verifyPassword) throws TooShortPasswordException,
            TooShortUsernameException, PasswordsDoNotMatchException,
            UsernameExistsException, PasswordRequirementException {

        verifyUsernameLength(username);
        verifyPasswordLength(password);
        validatePassword(password);
        checkPasswordVerification(password, verifyPassword);
        verifyUsernameAvailability(username);

        // Everything is ok, create the user
        User user = createUser(username, password);

        return user;
    }

    /**
     * Validates that the given password fulfills all the set requirements
     * 
     * @param password
     *            Password to check
     * @throws Exception
     *             Exception thrown if the password doesn't meet the
     *             requirements
     */
    private static void validatePassword(String password)
            throws PasswordRequirementException {
        if (!PasswordUtil.isValid(password)) {
            throw new PasswordRequirementException();
        }
    }

    /**
     * Creates and stores a user with the given username and password
     * 
     * @param username
     *            The username for the user
     * @param password
     *            The user's password
     * @return The created {@link User} object
     */
    private static User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.generateHashedPassword(password));

        FacadeFactory.getFacade().store(user);
        return user;
    }

    /**
     * Verifies that the given username doesn't exist
     * 
     * @param username
     *            The username to check
     * @throws UsernameExistsException
     *             Thrown if username already exists
     */
    private static void verifyUsernameAvailability(String username)
            throws UsernameExistsException {
        if (!checkUsernameAvailability(username)) {
            throw new UsernameExistsException();
        }
    }

    /**
     * Makes sure the given username is long enough
     * 
     * @param username
     *            Username to check
     * @throws TooShortUsernameException
     *             Thrown if username is null or too short
     */
    private static void verifyUsernameLength(String username)
            throws TooShortUsernameException {// Make sure that the username and
        if (username == null || username.length() < getMinUsernameLength()) {
            throw new TooShortUsernameException();
        }
    }

    /**
     * Makes sure the given password is long enough
     * 
     * @param password
     *            Password to check
     * @throws TooShortPasswordException
     *             Thrown if password is null or too short
     */
    private static void verifyPasswordLength(String password)
            throws TooShortPasswordException {
        if (password == null || password.length() < getMinPasswordLength()) {
            throw new TooShortPasswordException();
        }
    }

    /**
     * Verifies that the given passwords match with eachother
     * 
     * @param password
     *            Password to check
     * @param verifyPassword
     *            Verification of the first password
     * @throws PasswordsDoNotMatchException
     *             Thrown if the given parameters do not equal eachother
     */
    private static void checkPasswordVerification(String password,
            String verifyPassword) throws PasswordsDoNotMatchException {
        if (!password.equals(verifyPassword)) {
            throw new PasswordsDoNotMatchException();
        }
    }

    /**
     * Checks if the given username is available.
     * 
     * @param username
     *            Desired username
     * @return Returns true if the username doesn't exist, false if it exists
     */
    private static boolean checkUsernameAvailability(String username) {
        String query = "SELECT u FROM User u WHERE u.username = :username";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("username", username);

        User user = FacadeFactory.getFacade().find(query, parameters);

        return user != null ? false : true;
    }

    /**
     * Change the password of the given user. Verifies that the new password
     * meets all the set conditions.
     * 
     * @param user
     *            User object for which we want to change the password
     * @param currentPassword
     *            Current password
     * @param newPassword
     *            Desired new password
     * @param verifiedNewPassword
     *            New password verification
     * @throws InvalidCredentialsException
     *             Thrown if the current password is incorrect
     * @throws TooShortPasswordException
     *             Thrown if the new password is too short
     * @throws PasswordsDoNotMatchException
     *             Thrown if the password verification fails
     */
    public static void changePassword(User user, String currentPassword,
            String newPassword, String verifiedNewPassword)
            throws InvalidCredentialsException, TooShortPasswordException,
            PasswordsDoNotMatchException, PasswordRequirementException {

        // Verify that the current password is correct
        if (!PasswordUtil.verifyPassword(user, currentPassword)) {
            incrementFailedPasswordChangeAttempts(user);
            throw new InvalidCredentialsException();
        }

        user.clearFailedPasswordChangeAttempts();

        // Check the new password's constraints
        verifyPasswordLength(newPassword);
        validatePassword(newPassword);

        checkPasswordVerification(newPassword, verifiedNewPassword);

        // Password is ok, hash it and change it
        user.setPassword(PasswordUtil.generateHashedPassword(newPassword));
        // Store the user

        FacadeFactory.getFacade().store(user);
    }

    /**
     * Increments the number of failed login attempts by one and if maximum is
     * reached, logs out the user.
     * 
     * @param user
     */
    private static void incrementFailedPasswordChangeAttempts(User user) {
        user.incrementFailedPasswordChangeAttempts();

        if (user.getFailedPasswordChangeAttemps() > numberOfAllowedFailedPasswordChangeAttempts()) {
            SessionHandler.logout();
        }
    }

    /**
     * Stores to the database any changes made to the user object
     * 
     * @param user
     *            An instance of the User object
     */
    public static void storeUser(User user) {
        FacadeFactory.getFacade().store(user);
    }

    /**
     * Set the properties for the UserUtil. Minimum lengths for the username and
     * password must be defined.
     * 
     * @param properties
     *            Configuration properties
     * @deprecated Use System.setProperties() instead
     */
    @Deprecated
    public static void setProperties(Properties properties) {
        // Make sure properties is not null
        if (properties == null) {
            throw new IllegalArgumentException("Properties may not be null");
        }

        // Make sure we have both required properties. Check them separately so
        // we can give more detailed messages to the user
        if (!properties.containsKey("username.length.min")) {
            throw new IllegalArgumentException(
                    "Properties must contain username.length.min");
        }

        if (!properties.containsKey("password.length.min")) {
            throw new IllegalArgumentException(
                    "Properties must contain password.length.min");
        }

        // Get the property values
        String minUsernameLengthString = properties
                .getProperty("username.length.min");
        String minPasswordLengthString = properties
                .getProperty("password.length.min");

        // Make sure the values are integers
        try {
            Integer.valueOf(minUsernameLengthString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "username.length.min must be an integer");
        }

        try {
            Integer.valueOf(minPasswordLengthString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "password.length.min must be an integer");
        }

        // Everything is ok, now we can define the new values
        System.setProperty("authentication.username.validation.length",
                minUsernameLengthString);
        System.setProperty("authentication.password.validation.length",
                minPasswordLengthString);
    }

    /**
     * Returns the minimum length of a username
     * 
     * @return Minimum username length
     */
    public static int getMinUsernameLength() {
        String minLenghtStr = System
                .getProperty("authentication.username.validation.length");
        int minLenght = 4;
        if (minLenghtStr == null) {
            System
                    .setProperty("authentication.username.validation.length",
                            "4");
            return minLenght;
        }

        try {
            minLenght = Integer.valueOf(minLenghtStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "authentication.username.validation.length must be an integer");
        }

        return minLenght;
    }

    /**
     * Returns the minimum length of a password
     * 
     * @return Minimum password length
     */
    public static int getMinPasswordLength() {
        return PasswordUtil.getMinPasswordLength();
    }

    /**
     * Returns the maximum number of consecutive failed login attempts.
     * 
     * @return
     */
    private static int numberOfAllowedFailedPasswordChangeAttempts() {
        String maxAttemptsStr = System
                .getProperty("authentication.maxFailedPasswordChangeAttempts");
        int maxAttempts = 5;
        if (maxAttemptsStr == null) {
            System.setProperty(
                    "authentication.maxFailedPasswordChangeAttempts", "5");
            return maxAttempts;
        }

        try {
            maxAttempts = Integer.valueOf(maxAttemptsStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "authentication.maxFailedPasswordChangeAttempts must be an integer");
        }

        return maxAttempts;
    }

}
