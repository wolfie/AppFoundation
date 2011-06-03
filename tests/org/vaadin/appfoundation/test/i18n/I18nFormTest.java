package org.vaadin.appfoundation.test.i18n;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.i18n.FieldTranslation;
import org.vaadin.appfoundation.i18n.I18nForm;
import org.vaadin.appfoundation.i18n.InternationalizationServlet;
import org.vaadin.appfoundation.i18n.Lang;
import org.vaadin.appfoundation.i18n.TmxSourceReader;
import org.vaadin.appfoundation.i18n.TranslationSource;
import org.vaadin.appfoundation.test.MockApplication;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;

public class I18nFormTest {

    @Before
    public void setUp() {
        URL url = TranslationUtilTest.class.getClassLoader().getResource(
                "org/vaadin/appfoundation/test/i18n/field.translations.xml");
        TranslationSource fieldTranslations = new TmxSourceReader(new File(url
                .getFile()));

        InternationalizationServlet.loadTranslations(fieldTranslations);

        // Initialize the Lang class with the MockApplication
        new Lang(new MockApplication());
        Lang.setLocale(Locale.ENGLISH);
    }

    @After
    public void tearDown() {
        InternationalizationServlet.clear();
    }

    @Test
    public void formContainsCorrectCaptions() {
        Lang.setLocale(Locale.ENGLISH);

        I18nForm form = new I18nForm(A.class);

        BeanItem<A> item = new BeanItem<A>(new A());
        form.setItemDataSource(item);

        Field fooField = form.getField("foo");
        assertEquals("foo", fooField.getCaption());
        Field barField = form.getField("bar");
        assertEquals("bar", barField.getCaption());
        Field missingTranslationField = form.getField("translationMissing");
        assertEquals("Translation Missing", missingTranslationField
                .getCaption());

        // Change locale
        Locale fi = new Locale("fi", "fi");
        Lang.setLocale(fi);

        item = new BeanItem<A>(new A());
        form.setItemDataSource(item);

        fooField = form.getField("foo");
        assertEquals("fifoo", fooField.getCaption());
        barField = form.getField("bar");
        assertEquals("fibar", barField.getCaption());
        missingTranslationField = form.getField("translationMissing");
        assertEquals("Translation Missing", missingTranslationField
                .getCaption());

    }

    public static class A {

        @FieldTranslation(tuid = "FOO")
        private String foo;

        @FieldTranslation(tuid = "BAR")
        private String bar;

        private String translationMissing;

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBar() {
            return bar;
        }

        public void setTranslationMissing(String translationMissing) {
            this.translationMissing = translationMissing;
        }

        public String getTranslationMissing() {
            return translationMissing;
        }

    }
}
