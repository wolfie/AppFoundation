package org.vaadin.appfoundation.i18n;

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;

public class I18nForm extends Form {

    private static final long serialVersionUID = 2156245932485876166L;

    public I18nForm(Class<?> pojoClass) {
        super();
        setFormFieldFactory(new I18nFormFieldFactory(pojoClass));
    }

    public static class I18nFormFieldFactory extends DefaultFieldFactory {

        private static final long serialVersionUID = 5324073411117437070L;

        protected Class<?> pojoClass;

        public I18nFormFieldFactory(Class<?> pojoClass) {
            this.pojoClass = pojoClass;
        }

        public Field createField(Item item, Object propertyId,
                Component uiContext) {
            Field field = super.createField(item, propertyId, uiContext);
            field.setCaption(TranslationUtil.getFieldTranslation(pojoClass,
                    (String) propertyId));
            return field;
        }
    }
}
