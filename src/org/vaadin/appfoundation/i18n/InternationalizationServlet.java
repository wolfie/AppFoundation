package org.vaadin.appfoundation.i18n;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

/**
 * This servlet reads the xml file of all translations and loads them into a
 * map.
 * 
 * @author Kim
 * 
 */
public class InternationalizationServlet extends HttpServlet {

    private static final long serialVersionUID = 6849398292635918231L;

    // <lang, <id, message>>
    private static Map<String, Map<String, String>> translations = new HashMap<String, Map<String, String>>();

    /**
     * Load a translation file into memory. Any duplicated translations will be
     * discarded.
     * 
     * @param file
     *            The File object of the translation file
     * @deprecated Use TranslationSource instead of file
     */
    @Deprecated
    public static void loadTranslations(File file) {
        loadTranslations(file, false);
    }

    /**
     * Load translations from the source into memory. Any duplicated
     * translations will be discarded.
     * 
     * @param source
     *            The source for translation message
     */
    public static void loadTranslations(TranslationSource source) {
        loadTranslations(source, false);
    }

    /**
     * Clear all translations from the memory
     */
    public static void clear() {
        translations.clear();
    }

    /**
     * Load a translation file into memory.
     * 
     * @param source
     *            The source for translation message
     * @param force
     *            If a translation already exists in-memory, should the new
     *            translation override the existing translation
     */
    public static void loadTranslations(TranslationSource source, boolean force) {
        if (source == null) {
            throw new IllegalArgumentException("Source did not exist");
        }

        while (source.hasNext()) {
            TranslationMessage message = source.getNext();
            addMessage(message.getLanguage(), message.getTuid(), message
                    .getValue(), force);
        }
    }

    /**
     * Load a translation file into memory.
     * 
     * @param file
     *            The File object of the translation file
     * @param force
     *            If a translation already exists in-memory, should the new
     *            translation override the existing translation
     * @deprecated Use TranslationSource instead of File
     */
    @Deprecated
    public static void loadTranslations(File file, boolean force) {
        TranslationSource source = new TmxSourceReader(file);
        loadTranslations(source, force);
    }

    /**
     * Adds a translated message to the translations map
     * 
     * @param language
     *            Language of the translation
     * @param identifier
     *            Key string for the translation message
     * @param message
     *            The translated message
     * @param force
     *            If a translation already exists in-memory, should the new
     *            translation override the existing translation
     */
    private static void addMessage(String language, String identifier,
            String message, boolean force) {
        Map<String, String> messages = null;
        // Check if there are any existing translations for this language
        if (!translations.containsKey(language)) {
            // No translations existed for this language, so create a new map
            // for it
            messages = new HashMap<String, String>();
            translations.put(language, messages);
        } else {
            // Use the existing map for this language's translations
            messages = translations.get(language);
        }
        // Add the translation message to this language's translation map. If
        // force is true, then override any previous translations.
        if (!messages.containsKey(identifier) || force) {
            messages.put(identifier, message);
        }
    }

    /**
     * Get the translated message for a specific language
     * 
     * @param language
     *            Language for which we want the translation
     * @param identifier
     *            Key for the translation message
     * @param params
     *            Parameters for the translation message
     * @return Translated message string
     */
    public static String getMessage(String language, String identifier,
            Object... params) {
        if (!translations.containsKey(language)) {
            return "";
        } else {
            // Get the raw translation message
            Map<String, String> messages = translations.get(language);
            String msg = messages.containsKey(identifier) ? messages
                    .get(identifier) : "";

            // Check if any parameters are defined
            if (params != null) {
                // Replace the placeholders in the raw message with the given
                // parameters
                for (int i = 0; i < params.length; i++) {
                    String value = String.valueOf(params[i]);
                    msg = msg.replace("{" + i + "}", value);
                }
            }

            return msg;
        }
    }
}
