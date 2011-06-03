package org.vaadin.appfoundation.view;

import java.io.Serializable;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;

/**
 * Interface which all main views should implement
 * 
 * @author Kim
 * 
 */
public abstract class AbstractView<A extends ComponentContainer> extends
        CustomComponent implements View, Serializable {

    private static final long serialVersionUID = -1420553541682132603L;

    private A content;

    protected AbstractView(A layout) {
        setContent(layout);
        setSizeFull();
    }

    /**
     * Set a new content container. Default value is the object provided as
     * parameter to the constructor.
     * 
     * @param content
     */
    protected void setContent(A content) {
        this.content = content;
        setCompositionRoot(content);
    }

    /**
     * Get the content container. Default value is the object provided as
     * parameter to the constructor.
     * 
     * @param content
     */
    protected A getContent() {
        return content;
    }

}
