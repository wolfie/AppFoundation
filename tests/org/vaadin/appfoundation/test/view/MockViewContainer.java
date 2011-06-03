package org.vaadin.appfoundation.test.view;

import org.vaadin.appfoundation.view.View;
import org.vaadin.appfoundation.view.ViewContainer;

public class MockViewContainer implements ViewContainer {

    private View previouslyActivatedView = null;

    public void activate(View view) {
        setPreviouslyActivatedView(null);
    }

    public void setPreviouslyActivatedView(View previouslyActivatedView) {
        this.previouslyActivatedView = previouslyActivatedView;
    }

    public View getPreviouslyActivatedView() {
        return previouslyActivatedView;
    }

    public void deactivate(View view) {
        // TODO Auto-generated method stub

    }

}
