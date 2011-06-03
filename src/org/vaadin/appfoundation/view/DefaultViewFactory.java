package org.vaadin.appfoundation.view;

/**
 * Default implementation for the ViewFactory interface. This factory expects
 * the viewId to an instance of the view's class object and uses that class
 * object to initialize the view.
 * 
 * @author Kim
 * 
 */
public class DefaultViewFactory implements ViewFactory {

    private static final long serialVersionUID = -3997486649672703615L;

    /**
     * {@inheritDoc}
     */
    public View initView(Object viewId) {
        if (viewId instanceof Class<?>) {
            if (View.class.isAssignableFrom((Class<?>) viewId))
                try {
                    return (View) ((Class<?>) viewId).newInstance();
                } catch (InstantiationException e) {
                    return null;
                } catch (IllegalAccessException e) {
                    return null;
                }
        }
        return null;
    }

}
