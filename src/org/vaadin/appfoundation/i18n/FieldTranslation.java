package org.vaadin.appfoundation.i18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to define a translation for a field name. This
 * translation is used for example by field factories to set the caption for the
 * fields.
 * 
 * @author Kim
 * 
 */
@Target(value = { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldTranslation {
    /**
     * Get the key for the translation message
     * 
     * @return Translation unit id
     */
    public String tuid();
}
