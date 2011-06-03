package org.vaadin.appfoundation.i18n;

/**
 * Interface for fetching translation messages. The implementation should be
 * statefull and keep track of which translation units have been served to the
 * reader.
 * 
 * @author Kim
 * 
 */
public interface TranslationSource {

    /**
     * Checks if there are any translation messages left.
     * 
     * @return True if there are any TranslationMessages left in the queue,
     *         false if all TranslationMessages have been read.
     */
    public boolean hasNext();

    /**
     * Fetches the next unread TranslationMessages in the queue
     * 
     * @return The next unread TranslationMessage
     */
    public TranslationMessage getNext();

}
