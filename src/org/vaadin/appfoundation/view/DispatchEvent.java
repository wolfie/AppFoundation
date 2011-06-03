package org.vaadin.appfoundation.view;

import java.io.Serializable;

/**
 * Event class which contains information about which event has been or is being
 * activated.
 * 
 * @author Kim
 * 
 */
public class DispatchEvent implements Serializable {

    private static final long serialVersionUID = 3831823067244403530L;

    private final ViewItem item;

    private final Object[] params;

    /**
     * 
     * @param item
     *            The ViewItem object of the view that is being activated
     */
    public DispatchEvent(ViewItem item, Object... params) {
        this.item = item;
        this.params = params;
    }

    /**
     * Get the ViewItem that is the target of the event.
     * 
     * @return Target ViewItem
     */
    public ViewItem getViewItem() {
        return item;
    }

    /**
     * Get the parameters which are used for activating the view.
     * 
     * @return Parameters used to activate the view
     */
    public Object[] getActivationParameters() {
        return params;
    }

}
