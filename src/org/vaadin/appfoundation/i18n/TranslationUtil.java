package org.vaadin.appfoundation.i18n;

import java.lang.reflect.Field;

import com.vaadin.ui.DefaultFieldFactory;

public class TranslationUtil {

    private static final long serialVersionUID = -7106195086736773045L;

    /**
     * Fetch the translated caption of the given field.
     * 
     * @param c
     *            Class of the object
     * @param fieldName
     *            Name of the field
     * @return
     */
    public static String getFieldTranslation(Class<?> c, String fieldName) {
        Field field = getField(c, fieldName);
        if (field != null && field.isAnnotationPresent(FieldTranslation.class)) {
            FieldTranslation translation = field
                    .getAnnotation(FieldTranslation.class);
            return InternationalizationServlet.getMessage(Lang.getLocale()
                    .getLanguage(), translation.tuid());
        }

        // No translation found, use field name as the default value
        return DefaultFieldFactory.createCaptionByPropertyId(fieldName);
    }

    /**
     * Recursively search for the given field.
     * 
     * @param c
     *            Class name
     * @param fieldName
     *            Field's name
     * @return
     */
    private static Field getField(Class<?> c, String fieldName) {
        // If either the field name or the class is null, then the field cannot
        // be found
        if (c == null || fieldName == null) {
            return null;
        }

        try {
            // Try to get the field
            return c.getDeclaredField(fieldName);
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchFieldException e) {
            // The field didn't exist, maybe it exists in the superclass?
            return getField(c.getSuperclass(), fieldName);
        }
    }
}
