package org.vaadin.appfoundation.test.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.test.MockApplication;
import org.vaadin.appfoundation.view.AbstractView;
import org.vaadin.appfoundation.view.SimpleViewContainer;
import org.vaadin.appfoundation.view.View;
import org.vaadin.appfoundation.view.ViewHandler;
import org.vaadin.appfoundation.view.ViewItem;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;

public class SimpleViewContainerTest {

    private SimpleViewContainer viewContainer;
    private Panel panel;

    @Before
    public void setUp() throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        ViewHandler.initialize(new MockApplication());
        viewContainer = new SimpleViewContainer();
        Field field = AbstractView.class.getDeclaredField("content");
        field.setAccessible(true);
        panel = (Panel) field.get(viewContainer);
    }

    @After
    public void tearDown() {
        viewContainer = null;
        panel = null;
    }

    @Test
    public void activate() {
        MockView view = new MockView();
        ViewItem item = ViewHandler.addView(MockView.class, viewContainer);
        item.setView(view);
        assertFalse(panel.getComponentIterator().hasNext());
        ViewHandler.activateView(MockView.class);
        assertTrue(panel.getComponentIterator().hasNext());

        Iterator<Component> it = panel.getComponentIterator();
        assertEquals(view, it.next());
        assertFalse(it.hasNext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void activateInvalidView() {
        View view = new View() {

            public void deactivated(Object... params) {
                // TODO Auto-generated method stub

            }

            public void activated(Object... params) {
                // TODO Auto-generated method stub

            }
        };
        ViewItem item = ViewHandler.addView("test", viewContainer);
        item.setView(view);
        ViewHandler.activateView("test");
    }

    @Test
    public void replaceView() {
        MockView view = new MockView();
        ViewItem item = ViewHandler.addView(MockView.class, viewContainer);
        item.setView(view);

        MockView view2 = new MockView();
        ViewItem item2 = ViewHandler.addView("view2", viewContainer);
        item2.setView(view2);

        ViewHandler.activateView(MockView.class);
        ViewHandler.activateView("view2");
        assertTrue(panel.getComponentIterator().hasNext());

        Iterator<Component> it = panel.getComponentIterator();
        assertEquals(view2, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void deactivate() {
        MockView view = new MockView();
        ViewItem item = ViewHandler.addView(MockView.class, viewContainer);
        item.setView(view);
        ViewHandler.activateView(MockView.class);
        ViewHandler.deactivateView(MockView.class);
        assertFalse(panel.getComponentIterator().hasNext());
    }
}
