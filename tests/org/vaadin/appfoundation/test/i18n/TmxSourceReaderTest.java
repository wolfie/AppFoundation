package org.vaadin.appfoundation.test.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.vaadin.appfoundation.i18n.TmxSourceReader;
import org.vaadin.appfoundation.i18n.TranslationMessage;
import org.vaadin.appfoundation.i18n.TranslationSource;

public class TmxSourceReaderTest {

    @Test
    public void readFile() {
        URL url = TranslationUtilTest.class.getClassLoader().getResource(
                "org/vaadin/appfoundation/test/i18n/translations.xml");
        TranslationSource source = new TmxSourceReader(new File(url.getFile()));

        List<String> expected = new ArrayList<String>();
        expected.add("GENERIC_NAME;en;test1");
        expected.add("GENERIC_NAME;fi;testi1");
        expected.add("GENERIC_PASSWORD;en;test2");
        expected.add("GENERIC_PASSWORD;fi;testi2");
        expected.add("TEST;en;Foo {0} bar {1}");

        while (source.hasNext()) {
            TranslationMessage message = source.getNext();
            assertTrue(expected.contains(message.toString()));
            expected.remove(message.toString());
        }

        assertEquals(0, expected.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileIsAFolder() {
        URL url = TranslationUtilTest.class.getClassLoader().getResource(
                "org/vaadin/appfoundation/test/i18n/");
        new TmxSourceReader(new File(url.getFile()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullParameter() {
        new TmxSourceReader(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileDoesntExist() {
        new TmxSourceReader(new File(UUID.randomUUID().toString()));
    }
}
