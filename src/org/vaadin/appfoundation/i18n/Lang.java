package org.vaadin.appfoundation.i18n;

import java.util.Locale;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * A utility class for keeping track of the used locale instance for each
 * application
 * 
 * @author Kim
 * 
 */
public class Lang implements TransactionListener {

    private static final long serialVersionUID = -217583268167388861L;

    private Locale locale = Locale.getDefault();

    private static ThreadLocal<Lang> instance = new ThreadLocal<Lang>();

    private Application application;

    /**
     * Constructor. Takes as parameter the application instance.
     * 
     * @param application
     *            Application instance.
     */
    public Lang(Application application) {
        this.application = application;
        instance.set(this);
    }

    /**
     * Get the current locale for the application
     * 
     * @return Locale
     */
    public static Locale getLocale() {
        return instance.get().locale;
    }

    /**
     * Set the current locale for the application
     * 
     * @param locale
     *            Locale
     */
    public static void setLocale(Locale locale) {
        instance.get().locale = locale;
    }

    /**
     * Get the translated message for the locale set in Lang
     * 
     * @param identifier
     *            Key for the translation message
     * @param params
     *            Parameters for the translation message
     * @return Translated message string
     */
    public static String getMessage(String identifier, Object... params) {
        return InternationalizationServlet.getMessage(
                getLocale().getLanguage(), identifier, params);
    }

    /**
     * {@inheritDoc}
     */
    public void transactionEnd(Application application, Object transactionData) {
        // Clear thread local instance at the end of the transaction
        if (this.application == application) {
            instance.set(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void transactionStart(Application application, Object transactionData) {
        // Set the thread local instance
        if (this.application == application) {
            instance.set(this);
        }
    }

    /**
     * Initializes the {@link Lang} for the given {@link Application}
     * 
     * @param application
     */
    public static void initialize(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("Application may not be null");
        }
        Lang handler = new Lang(application);
        application.getContext().addTransactionListener(handler);
    }

}
