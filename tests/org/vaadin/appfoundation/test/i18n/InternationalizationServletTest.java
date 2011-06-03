package org.vaadin.appfoundation.test.i18n;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.i18n.InternationalizationServlet;
import org.vaadin.appfoundation.i18n.TmxSourceReader;
import org.vaadin.appfoundation.i18n.TranslationSource;

public class InternationalizationServletTest {

    @Before
    public void setUp() {
        InternationalizationServlet.clear();
    }

    @Test
    public void loadAndGetMessage() {
        URL url = InternationalizationServletTest.class.getClassLoader()
                .getResource(
                        "org/vaadin/appfoundation/test/i18n/translations.xml");
        TranslationSource all = new TmxSourceReader(new File(url.getFile()));

        InternationalizationServlet.loadTranslations(all);

        assertEquals("test1", InternationalizationServlet.getMessage("en",
                "GENERIC_NAME"));
        assertEquals("testi1", InternationalizationServlet.getMessage("fi",
                "GENERIC_NAME"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void loadFromFile() {
        URL url = InternationalizationServletTest.class.getClassLoader()
                .getResource(
                        "org/vaadin/appfoundation/test/i18n/translations.xml");
        File all = new File(url.getFile());

        InternationalizationServlet.loadTranslations(all);

        assertEquals("test1", InternationalizationServlet.getMessage("en",
                "GENERIC_NAME"));
        assertEquals("testi1", InternationalizationServlet.getMessage("fi",
                "GENERIC_NAME"));
    }

    @Test
    public void loadFromMultipleFiles() {
        URL url = InternationalizationServletTest.class
                .getClassLoader()
                .getResource(
                        "org/vaadin/appfoundation/test/i18n/translations.fi.xml");
        TranslationSource fi = new TmxSourceReader(new File(url.getFile()));

        url = InternationalizationServletTest.class
                .getClassLoader()
                .getResource(
                        "org/vaadin/appfoundation/test/i18n/translations.en.xml");
        TranslationSource en = new TmxSourceReader(new File(url.getFile()));

        InternationalizationServlet.loadTranslations(fi);
        InternationalizationServlet.loadTranslations(en);

        assertEquals("Name", InternationalizationServlet.getMessage("en",
                "GENERIC_NAME"));
        assertEquals("Nimi", InternationalizationServlet.getMessage("fi",
                "GENERIC_NAME"));
    }

    @Test
    public void overrideTranslations() {
        URL url = InternationalizationServletTest.class.getClassLoader()
                .getResource(
                        "org/vaadin/appfoundation/test/i18n/translations.xml");
        TranslationSource all = new TmxSourceReader(new File(url.getFile()));

        InternationalizationServlet.loadTranslations(all);

        url = InternationalizationServletTest.class
                .getClassLoader()
                .getResource(
                        "org/vaadin/appfoundation/test/i18n/translations.en.xml");
        TranslationSource en = new TmxSourceReader(new File(url.getFile()));

        InternationalizationServlet.loadTranslations(en, false);

        assertEquals("test1", InternationalizationServlet.getMessage("en",
                "GENERIC_NAME"));
        assertEquals("testi1", InternationalizationServlet.getMessage("fi",
                "GENERIC_NAME"));

        en = new TmxSourceReader(new File(url.getFile()));
        InternationalizationServlet.loadTranslations(en, true);
        assertEquals("Name", InternationalizationServlet.getMessage("en",
                "GENERIC_NAME"));
        assertEquals("testi1", InternationalizationServlet.getMessage("fi",
                "GENERIC_NAME"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void overrideFromFile() {
        URL url = InternationalizationServletTest.class.getClassLoader()
                .getResource(
                        "org/vaadin/appfoundation/test/i18n/translations.xml");
        TranslationSource all = new TmxSourceReader(new File(url.getFile()));

        InternationalizationServlet.loadTranslations(all);

        url = InternationalizationServletTest.class
                .getClassLoader()
                .getResource(
                        "org/vaadin/appfoundation/test/i18n/translations.en.xml");
        TranslationSource en = new TmxSourceReader(new File(url.getFile()));

        InternationalizationServlet.loadTranslations(en, false);

        assertEquals("test1", InternationalizationServlet.getMessage("en",
                "GENERIC_NAME"));
        assertEquals("testi1", InternationalizationServlet.getMessage("fi",
                "GENERIC_NAME"));

        InternationalizationServlet.loadTranslations(new File(url.getFile()),
                true);
        assertEquals("Name", InternationalizationServlet.getMessage("en",
                "GENERIC_NAME"));
        assertEquals("testi1", InternationalizationServlet.getMessage("fi",
                "GENERIC_NAME"));
    }

    @Test
    public void getMessage() {
        URL url = InternationalizationServletTest.class.getClassLoader()
                .getResource(
                        "org/vaadin/appfoundation/test/i18n/translations.xml");
        TranslationSource all = new TmxSourceReader(new File(url.getFile()));

        InternationalizationServlet.loadTranslations(all);

        assertEquals("", InternationalizationServlet.getMessage("DOESNTEXIST",
                "DOESNEXIST"));
        assertEquals("", InternationalizationServlet.getMessage("en",
                "DOESNEXIST"));
        assertEquals("Foo {0} bar {1}", InternationalizationServlet.getMessage(
                "en", "TEST"));
        assertEquals("Foo Hello bar world", InternationalizationServlet
                .getMessage("en", "TEST", "Hello", "world"));

    }

    @SuppressWarnings("deprecation")
    @Test(expected = IllegalArgumentException.class)
    public void invalidFileNull() {
        InternationalizationServlet.loadTranslations((File) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidSourceNull() {
        InternationalizationServlet.loadTranslations((TranslationSource) null);
    }

    @SuppressWarnings("deprecation")
    @Test(expected = IllegalArgumentException.class)
    public void invalidFileDoesntExist() {
        InternationalizationServlet.loadTranslations(new File("foobar.xml"));

    }

    @SuppressWarnings("deprecation")
    @Test(expected = IllegalArgumentException.class)
    public void invalidFileFolder() {
        InternationalizationServlet.loadTranslations(new File("org/"));
    }
}
