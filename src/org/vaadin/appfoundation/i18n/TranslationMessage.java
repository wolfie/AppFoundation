package org.vaadin.appfoundation.i18n;

/**
 * An object representation of a translation message. Contains the necessary
 * details, the translation unit id, the language and the actual translation
 * message value.
 * 
 * @author Kim
 * 
 */
public class TranslationMessage {

    private final String tuid;

    private final String language;

    private final String value;

    public TranslationMessage(String tuid, String language, String value) {
        this.tuid = tuid;
        this.language = language;
        this.value = value;
    }

    /**
     * Gets the translation unit id
     * 
     * @return Translation unit id
     */
    public String getTuid() {
        return tuid;
    }

    /**
     * Gets the string representation of the language for which this message is.
     * 
     * @return Language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Gets the actual translated message for the given tuid and language.
     * 
     * @return Message
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return tuid + ";" + language + ";" + value;
    }
}
