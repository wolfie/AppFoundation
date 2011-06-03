package org.vaadin.appfoundation.test.i18n;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.i18n.FieldTranslation;
import org.vaadin.appfoundation.i18n.InternationalizationServlet;
import org.vaadin.appfoundation.i18n.Lang;
import org.vaadin.appfoundation.i18n.TmxSourceReader;
import org.vaadin.appfoundation.i18n.TranslationSource;
import org.vaadin.appfoundation.i18n.TranslationUtil;
import org.vaadin.appfoundation.test.MockApplication;

public class TranslationUtilTest {

    @Before
    public void setUp() {
        URL url = TranslationUtilTest.class.getClassLoader().getResource(
                "org/vaadin/appfoundation/test/i18n/field.translations.xml");
        TranslationSource fieldTranslations = new TmxSourceReader(new File(url
                .getFile()));

        InternationalizationServlet.loadTranslations(fieldTranslations);

        // Initialize the Lang class with the MockApplication
        new Lang(new MockApplication());
    }

    @After
    public void tearDown() {
        InternationalizationServlet.clear();
    }

    @Test
    public void getFieldTranslation() {
        Locale fi = new Locale("fi", "fi");

        Lang.setLocale(Locale.ENGLISH);
        assertEquals("foo", TranslationUtil.getFieldTranslation(B.class, "foo"));
        assertEquals("bar", TranslationUtil.getFieldTranslation(B.class, "bar"));
        assertEquals("Non Existing Field", TranslationUtil.getFieldTranslation(
                B.class, "nonExistingField"));
        assertEquals("No Translation Field", TranslationUtil
                .getFieldTranslation(B.class, "noTranslationField"));

        Lang.setLocale(fi);
        assertEquals("fifoo", TranslationUtil.getFieldTranslation(B.class,
                "foo"));
        assertEquals("fibar", TranslationUtil.getFieldTranslation(B.class,
                "bar"));
        assertEquals("Non Existing Field", TranslationUtil.getFieldTranslation(
                B.class, "nonExistingField"));
        assertEquals("No Translation Field", TranslationUtil
                .getFieldTranslation(B.class, "noTranslationField"));
    }

    public static class A {

        @FieldTranslation(tuid = "FOO")
        private String foo;

        private String noTranslationField;

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }

        public void setNoTranslationField(String noTranslationField) {
            this.noTranslationField = noTranslationField;
        }

        public String getNoTranslationField() {
            return noTranslationField;
        }
    }

    public static class B extends A {

        @FieldTranslation(tuid = "BAR")
        private String bar;

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBar() {
            return bar;
        }

    }
}
