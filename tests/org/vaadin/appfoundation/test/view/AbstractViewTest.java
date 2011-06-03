package org.vaadin.appfoundation.test.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.vaadin.appfoundation.view.AbstractView;

import com.vaadin.ui.VerticalLayout;

public class AbstractViewTest {

    @Test
    public void getContent() {
        final VerticalLayout layout = new VerticalLayout();

        MockView view = new MockView(layout);
        assertEquals(layout, view.getLayout());
    }

    @Test
    public void setContent() {
        final VerticalLayout layout = new VerticalLayout();
        final VerticalLayout layout2 = new VerticalLayout();

        MockView view = new MockView(layout);
        assertEquals(layout, view.getLayout());
        assertNotSame(layout2, view.getLayout());

        view.setLayout(layout2);
        assertEquals(layout2, view.getLayout());
        assertNotSame(layout, view.getLayout());
    }

    private static class MockView extends AbstractView<VerticalLayout> {

        public MockView(VerticalLayout layout) {
            super(layout);
        }

        private static final long serialVersionUID = 1L;

        public void activated(Object... params) {
            // TODO Auto-generated method stub

        }

        public VerticalLayout getLayout() {
            return getContent();
        }

        public void setLayout(VerticalLayout layout) {
            setContent(layout);
        }

        public void deactivated(Object... params) {
            // TODO Auto-generated method stub

        }

    }

}
