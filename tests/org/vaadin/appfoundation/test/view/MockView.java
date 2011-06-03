package org.vaadin.appfoundation.test.view;

import org.vaadin.appfoundation.view.AbstractView;

import com.vaadin.ui.VerticalLayout;

public class MockView extends AbstractView<VerticalLayout> {

    private static final long serialVersionUID = 12449835835472800L;

    private Object[] params;

    public MockView() {
        super(new VerticalLayout());
    }

    public void activated(Object... params) {
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

    public void deactivated(Object... params) {
        // TODO Auto-generated method stub

    }

}
