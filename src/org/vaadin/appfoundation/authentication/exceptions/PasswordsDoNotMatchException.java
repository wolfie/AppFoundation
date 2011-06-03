package org.vaadin.appfoundation.authentication.exceptions;

/**
 * Exception is thrown when two passwords are compared and when they do not
 * match. This shouldn't be used in authentication, only in registration forms
 * and for changing passwords.
 * 
 * @author Kim
 * 
 */
public class PasswordsDoNotMatchException extends Exception {

    private static final long serialVersionUID = 3229052626713099288L;

}
