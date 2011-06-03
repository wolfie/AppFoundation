package org.vaadin.appfoundation.test.i18n;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.vaadin.appfoundation.i18n.TranslationMessage;

public class TranslationMessageTest {

    @Test
    public void tuid() {
        TranslationMessage message = new TranslationMessage("foo", "en", "test");
        assertEquals("foo", message.getTuid());
        message = new TranslationMessage("foo2", "en", "test");
        assertEquals("foo2", message.getTuid());
    }

    @Test
    public void language() {
        TranslationMessage message = new TranslationMessage("foo", "en", "test");
        assertEquals("en", message.getLanguage());
        message = new TranslationMessage("foo", "fi", "test");
        assertEquals("fi", message.getLanguage());
    }

    @Test
    public void value() {
        TranslationMessage message = new TranslationMessage("foo", "en", "test");
        assertEquals("test", message.getValue());
        message = new TranslationMessage("foo", "en", "test2");
        assertEquals("test2", message.getValue());
    }

    @Test
    public void testToString() {
        TranslationMessage message = new TranslationMessage("foo", "en", "test");
        assertEquals("foo;en;test", message.toString());
    }
}
