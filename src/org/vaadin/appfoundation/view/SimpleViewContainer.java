package org.vaadin.appfoundation.view;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;

/**
 * A simple implementation of the {@link ViewContainer} interface. This class
 * simply contains a panel and adds the activated view to the panel. When a view
 * is activated, then any previous views are removed from the panel.
 * Deactivating a view will clear the panel from any components. This class is a
 * view itself, so it can be added to the {@link ViewHandler} as any other view.
 * 
 * @author Kim
 * 
 */
public class SimpleViewContainer extends AbstractView<Panel> implements
        ViewContainer {

    private static final long serialVersionUID = 9010669373711637452L;

    public SimpleViewContainer() {
        super(new Panel());
    }

    /**
     * {@inheritDoc}
     */
    public void activated(Object... params) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void deactivated(Object... params) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void activate(View view) {
        if (!(view instanceof Component)) {
            throw new IllegalArgumentException("View must be a component");
        }
        getContent().removeAllComponents();
        getContent().addComponent((Component) view);
    }

    /**
     * {@inheritDoc}
     */
    public void deactivate(View view) {
        getContent().removeAllComponents();
    }

}
