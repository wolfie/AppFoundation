package org.vaadin.appfoundation.view;

import java.io.Serializable;

/**
 * The view item is a a wrapper class used by the ViewHandler. It contains
 * information about the views added to the ViewHandler.
 * 
 * @author Kim
 * 
 */
public class ViewItem implements Serializable {

    private static final long serialVersionUID = 3400671352364794499L;

    private final Object viewId;

    private View view;

    private Class<? extends View> viewClass = null;

    private ViewFactory factory = null;

    /**
     * Constructor. Takes as input the viewId. If the viewId is an instance of
     * class object of the view, then the viewId is used as the default
     * viewClass.
     * 
     * @param viewId
     *            The id for the ViewItem
     */
    @SuppressWarnings("unchecked")
    public ViewItem(Object viewId) {
        if (viewId instanceof Class) {
            if (AbstractView.class.isAssignableFrom((Class<?>) viewId)) {
                setFactory(new DefaultViewFactory());
                viewClass = (Class<? extends View>) viewId;
            }

        }
        this.viewId = viewId;
    }

    /**
     * Set the view object for this ViewItem
     * 
     * @param view
     *            The view instance
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Get the view object for this item. If view isn't set and the viewClass is
     * defined, then an instance of the class is created and returned.
     * 
     * @return The view instance
     */
    public View getView() {
        if (view == null && factory == null) {
            throw new NullPointerException(
                    "Factory or view instance must be defined");
        }

        if (view == null) {
            view = factory.initView(getViewId());
        }
        return view;
    }

    /**
     * Returns the view id for this ViewItem.
     * 
     * @return The item's id
     */
    public Object getViewId() {
        return viewId;
    }

    /**
     * Get the current view class.
     * 
     * @return The view's class
     */
    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    /**
     * Set the ViewFactory for this ViewItem.
     * 
     * @param factory
     */
    public void setFactory(ViewFactory factory) {
        this.factory = factory;
    }

    /**
     * Get the ViewFactory of this ViewItem.
     * 
     * @return
     */
    public ViewFactory getFactory() {
        return factory;
    }

}
