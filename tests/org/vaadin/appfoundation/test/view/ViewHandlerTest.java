package org.vaadin.appfoundation.test.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.test.MockApplication;
import org.vaadin.appfoundation.test.ValueContainer;
import org.vaadin.appfoundation.test.MockApplication.MockContext;
import org.vaadin.appfoundation.view.AbstractView;
import org.vaadin.appfoundation.view.DefaultViewFactory;
import org.vaadin.appfoundation.view.DispatchEvent;
import org.vaadin.appfoundation.view.DispatchEventListener;
import org.vaadin.appfoundation.view.DispatchException;
import org.vaadin.appfoundation.view.View;
import org.vaadin.appfoundation.view.ViewContainer;
import org.vaadin.appfoundation.view.ViewFactory;
import org.vaadin.appfoundation.view.ViewHandler;
import org.vaadin.appfoundation.view.ViewItem;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;

public class ViewHandlerTest {

    private final MockApplication application = new MockApplication();
    private ViewHandler handler = null;

    @Before
    public void setUp() {
        // Initialize the Lang class with the MockApplication
        handler = new ViewHandler(application);
        handler.transactionStart(application, null);
    }

    @After
    public void tearDown() {
        handler.transactionEnd(application, null);
        handler = null;
    }

    @Test
    public void getViewItem() {
        ViewItem item1 = ViewHandler.addView("test");
        ViewItem item2 = ViewHandler.addView("test2");

        assertTrue(ViewHandler.getViewItem("test").getViewId().equals("test"));
        assertEquals(item1, ViewHandler.getViewItem("test"));

        assertTrue(ViewHandler.getViewItem("test2").getViewId().equals("test2"));
        assertEquals(item2, ViewHandler.getViewItem("test2"));

        assertNull(ViewHandler.getViewItem("nonExistingId"));
    }

    @Test
    public void addViewNoParams() {
        // With no parameters
        Object id1 = ViewHandler.addView();
        assertNotNull(id1);
        assertNotNull(ViewHandler.getViewItem(id1));
    }

    @Test
    public void addViewObjectParam() {
        // With a view id param
        String id2 = "id2";
        ViewHandler.addView(id2);
        ViewItem item = ViewHandler.getViewItem(id2);
        assertNotNull(item);
        assertEquals(id2, item.getViewId());
    }

    @Test
    public void addViewClassParam() {
        // Add view with an id of a View class
        ViewItem item2 = ViewHandler.addView(MockView.class);
        assertEquals(MockView.class, item2.getViewClass());
        assertTrue(item2.getFactory() instanceof DefaultViewFactory);
    }

    @Test
    public void addViewExistingView() {
        String id2 = "id2";
        ViewHandler.addView(id2);

        // Try adding a view with an existing id
        assertNull(ViewHandler.addView(id2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addViewNullParam() {
        ViewHandler.addView(null);
    }

    @Test
    public void addViewWithParent() {
        final ValueContainer value = new ValueContainer();

        ViewContainer container = new ViewContainer() {
            public void activate(View view) {
                value.setValue(view);
            }

            public void deactivate(View view) {
                // TODO Auto-generated method stub

            }
        };

        ViewItem item = ViewHandler.addView(MockView.class, container);

        ViewHandler.activateView(MockView.class);
        assertNotNull(value.getValue());
        assertEquals(item.getView(), value.getValue());
    }

    @Test
    public void setDefaultViewFactory() {
        ViewFactory factory = new ViewFactory() {
            private static final long serialVersionUID = -3121514093680829422L;

            public View initView(Object viewId) {
                return null;
            }
        };

        // Make sure the new view factory is set
        assertNull(ViewHandler.getDefaultViewFactory());
        ViewHandler.setDefaultViewFactory(factory);
        assertNotNull(ViewHandler.getDefaultViewFactory());
        assertEquals(factory, ViewHandler.getDefaultViewFactory());

        // Add new view and make sure it got our new default view factory as its
        // factory
        ViewItem item = ViewHandler.addView(MockView.class);
        assertNotNull(item.getFactory());
        assertEquals(factory, item.getFactory());
    }

    @Test
    public void removeViewNonExisting() {
        assertFalse(ViewHandler.removeView("test"));
    }

    @Test
    public void removeView() {
        ViewHandler.addView("test");
        assertTrue(ViewHandler.removeView("test"));
        assertNull(ViewHandler.getViewItem("test"));
        assertFalse(ViewHandler.removeView("test"));
    }

    @Test
    /**
     * Tests that the uri is removed for a view when the view is removed.
     */
    public void removeViewUriIsRemoved() {
        // Add two views
        ViewHandler.addView("test");
        ViewHandler.addView("test2");
        // Add an uri to the first view
        ViewHandler.addUri("test", "test");

        ViewHandler.removeView("test");
        // The "test" uri should have been removed when the view was removed.
        // Hence we should now be able to define a new view for the same uri
        ViewHandler.addUri("test", "test2");
    }

    @Test
    public void activateView() {
        final ValueContainer viewActivated = new ValueContainer();
        final ValueContainer parentCalled = new ValueContainer();
        viewActivated.setValue(false);
        parentCalled.setValue(false);

        AbstractView<ComponentContainer> view = new AbstractView<ComponentContainer>(
                new VerticalLayout()) {
            private static final long serialVersionUID = 1L;

            public void activated(Object... params) {
                viewActivated.setValue(true);
            }

            public void deactivated(Object... params) {
                // TODO Auto-generated method stub

            }
        };

        ViewItem item = ViewHandler.addView("test");
        item.setView(view);

        ViewHandler.activateView("test");
        // Parent not set
        assertFalse((Boolean) viewActivated.getValue());

        ViewContainer container = new ViewContainer() {
            public void activate(View view) {
                parentCalled.setValue(true);
            }

            public void deactivate(View view) {
                // TODO Auto-generated method stub

            }
        };

        ViewHandler.setParent("test", container);

        ViewHandler.activateView("test");
        // Parent is now set
        assertTrue((Boolean) viewActivated.getValue());
        assertTrue((Boolean) parentCalled.getValue());
    }

    @Test
    public void deactivateView() {
        final ValueContainer viewDeactivated = new ValueContainer();
        final ValueContainer parentCalled = new ValueContainer();
        viewDeactivated.setValue(false);
        parentCalled.setValue(false);

        AbstractView<ComponentContainer> view = new AbstractView<ComponentContainer>(
                new VerticalLayout()) {
            private static final long serialVersionUID = 1L;

            public void activated(Object... params) {

            }

            public void deactivated(Object... params) {
                viewDeactivated.setValue(true);
            }
        };

        ViewItem item = ViewHandler.addView("test");
        item.setView(view);

        ViewHandler.activateView("test");
        // Parent not set
        assertFalse((Boolean) viewDeactivated.getValue());

        ViewContainer container = new ViewContainer() {
            public void activate(View view) {

            }

            public void deactivate(View view) {
                parentCalled.setValue(true);
            }
        };

        ViewHandler.setParent("test", container);

        ViewHandler.deactivateView("test");
        // Parent is now set
        assertTrue((Boolean) viewDeactivated.getValue());
        assertTrue((Boolean) parentCalled.getValue());
    }

    @Test
    public void activateViewParamsPassed() {
        final ValueContainer parameters = new ValueContainer();

        AbstractView<ComponentContainer> view = new AbstractView<ComponentContainer>(
                new VerticalLayout()) {
            private static final long serialVersionUID = 1L;

            public void activated(Object... params) {
                parameters.setValue(params);
            }

            public void deactivated(Object... params) {
                // TODO Auto-generated method stub

            }
        };

        ViewItem item = ViewHandler.addView("test");
        item.setView(view);
        ViewContainer container = new ViewContainer() {
            public void activate(View view) {
            }

            public void deactivate(View view) {
                // TODO Auto-generated method stub

            }
        };

        ViewHandler.setParent("test", container);

        ViewHandler.activateView("test", "foo", "bar");
        assertEquals(2, ((Object[]) parameters.getValue()).length);
        assertEquals("foo", ((Object[]) parameters.getValue())[0]);
        assertEquals("bar", ((Object[]) parameters.getValue())[1]);
    }

    @Test
    public void deactivateViewParamsPassed() {
        final ValueContainer parameters = new ValueContainer();

        AbstractView<ComponentContainer> view = new AbstractView<ComponentContainer>(
                new VerticalLayout()) {
            private static final long serialVersionUID = 1L;

            public void activated(Object... params) {

            }

            public void deactivated(Object... params) {
                parameters.setValue(params);
            }
        };

        ViewItem item = ViewHandler.addView("test");
        item.setView(view);
        ViewContainer container = new ViewContainer() {
            public void activate(View view) {
            }

            public void deactivate(View view) {

            }
        };

        ViewHandler.setParent("test", container);

        ViewHandler.deactivateView("test", "foo", "bar");
        assertEquals(2, ((Object[]) parameters.getValue()).length);
        assertEquals("foo", ((Object[]) parameters.getValue())[0]);
        assertEquals("bar", ((Object[]) parameters.getValue())[1]);
    }

    @Test
    public void activationEventListeners() {
        final ValueContainer viewActivated = new ValueContainer(false);
        final ValueContainer parentCalled = new ValueContainer(false);

        final AbstractView<ComponentContainer> view = new AbstractView<ComponentContainer>(
                new VerticalLayout()) {
            private static final long serialVersionUID = 1L;

            public void activated(Object... params) {
                viewActivated.setValue(true);
            }

            public void deactivated(Object... params) {
                // TODO Auto-generated method stub

            }
        };

        final ViewItem item = ViewHandler.addView("test");
        item.setView(view);

        final ValueContainer preCalls = new ValueContainer(0);
        final ValueContainer preActivation = new ValueContainer(0);
        final ValueContainer postCalls = new ValueContainer(0);
        final ValueContainer postActiovation = new ValueContainer(0);

        DispatchEventListener listener = new DispatchEventListener() {

            public void preDispatch(DispatchEvent event)
                    throws DispatchException {
                assertEquals(item, event.getViewItem());
                assertEquals(1, (event.getActivationParameters()).length);
                assertEquals("testParam", (event.getActivationParameters())[0]);

                preCalls.setValue(((Integer) preCalls.getValue()) + 1);
            }

            public void postDispatch(DispatchEvent event) {
                postCalls.setValue(((Integer) postCalls.getValue()) + 1);

            }

            public void postActivation(DispatchEvent event) {
                postActiovation
                        .setValue(((Integer) postActiovation.getValue()) + 1);
            }

            public void postDeactivation(DispatchEvent event) {
                // TODO Auto-generated method stub

            }

            public void preActivation(DispatchEvent event)
                    throws DispatchException {
                preActivation
                        .setValue(((Integer) preActivation.getValue()) + 1);
            }

            public void preDeactivation(DispatchEvent event)
                    throws DispatchException {
                // TODO Auto-generated method stub

            }
        };

        DispatchEventListener listener2 = new DispatchEventListener() {

            public void preDispatch(DispatchEvent event)
                    throws DispatchException {
                preCalls.setValue(((Integer) preCalls.getValue()) + 1);
            }

            public void postDispatch(DispatchEvent event) {
                postCalls.setValue(((Integer) postCalls.getValue()) + 1);
            }

            public void postActivation(DispatchEvent event) {
                postActiovation
                        .setValue(((Integer) postActiovation.getValue()) + 1);
            }

            public void postDeactivation(DispatchEvent event) {
                // TODO Auto-generated method stub

            }

            public void preActivation(DispatchEvent event)
                    throws DispatchException {
                preActivation
                        .setValue(((Integer) preActivation.getValue()) + 1);
            }

            public void preDeactivation(DispatchEvent event)
                    throws DispatchException {
                // TODO Auto-generated method stub

            }
        };

        ViewHandler.addListener(listener);
        ViewHandler.addListener(listener2);

        ViewHandler.activateView("test");

        ViewContainer container = new ViewContainer() {
            public void activate(View view) {
                parentCalled.setValue(true);
            }

            public void deactivate(View view) {
                // TODO Auto-generated method stub

            }
        };
        ViewHandler.setParent("test", container);
        ViewHandler.activateView("test", "testParam");

        assertEquals(2, ((Integer) preCalls.getValue()).intValue());
        assertEquals(2, ((Integer) preActivation.getValue()).intValue());
        assertEquals(2, ((Integer) postCalls.getValue()).intValue());
        assertEquals(2, ((Integer) postActiovation.getValue()).intValue());
        assertTrue((Boolean) viewActivated.getValue());
        assertTrue((Boolean) parentCalled.getValue());
    }

    @Test
    public void deactivationEventListeners() {
        final ValueContainer viewDeactivated = new ValueContainer(false);
        final ValueContainer parentCalled = new ValueContainer(false);

        final AbstractView<ComponentContainer> view = new AbstractView<ComponentContainer>(
                new VerticalLayout()) {
            private static final long serialVersionUID = 1L;

            public void activated(Object... params) {
            }

            public void deactivated(Object... params) {
                viewDeactivated.setValue(true);
            }
        };

        final ViewItem item = ViewHandler.addView("test");
        item.setView(view);

        final ValueContainer preDeactivation = new ValueContainer(0);
        final ValueContainer postDeactiovation = new ValueContainer(0);

        DispatchEventListener listener = new DispatchEventListener() {

            public void preDispatch(DispatchEvent event)
                    throws DispatchException {

            }

            public void postDispatch(DispatchEvent event) {

            }

            public void postActivation(DispatchEvent event) {

            }

            public void postDeactivation(DispatchEvent event) {
                postDeactiovation.setValue(((Integer) postDeactiovation
                        .getValue()) + 1);
            }

            public void preActivation(DispatchEvent event)
                    throws DispatchException {
            }

            public void preDeactivation(DispatchEvent event)
                    throws DispatchException {
                assertEquals(item, event.getViewItem());
                assertEquals(1, (event.getActivationParameters()).length);
                assertEquals("testParam", (event.getActivationParameters())[0]);

                preDeactivation
                        .setValue(((Integer) preDeactivation.getValue()) + 1);
            }
        };

        DispatchEventListener listener2 = new DispatchEventListener() {

            public void preDispatch(DispatchEvent event)
                    throws DispatchException {
            }

            public void postDispatch(DispatchEvent event) {
            }

            public void postActivation(DispatchEvent event) {

            }

            public void postDeactivation(DispatchEvent event) {
                postDeactiovation.setValue(((Integer) postDeactiovation
                        .getValue()) + 1);
            }

            public void preActivation(DispatchEvent event)
                    throws DispatchException {

            }

            public void preDeactivation(DispatchEvent event)
                    throws DispatchException {
                preDeactivation
                        .setValue(((Integer) preDeactivation.getValue()) + 1);
            }
        };

        ViewHandler.addListener(listener);
        ViewHandler.addListener(listener2);

        ViewHandler.activateView("test");

        ViewContainer container = new ViewContainer() {
            public void activate(View view) {

            }

            public void deactivate(View view) {
                parentCalled.setValue(true);

            }
        };
        ViewHandler.setParent("test", container);
        ViewHandler.deactivateView("test", "testParam");

        assertEquals(2, ((Integer) preDeactivation.getValue()).intValue());
        assertEquals(2, ((Integer) postDeactiovation.getValue()).intValue());
        assertTrue((Boolean) viewDeactivated.getValue());
        assertTrue((Boolean) parentCalled.getValue());
    }

    @Test
    public void removeListener() {
        final ValueContainer preCalls = new ValueContainer(0);

        DispatchEventListener listener = new DispatchEventListener() {

            public void preDispatch(DispatchEvent event)
                    throws DispatchException {
                preCalls.setValue(((Integer) preCalls.getValue()) + 1);
            }

            public void postDispatch(DispatchEvent event) {
            }

            public void postActivation(DispatchEvent event) {
                // TODO Auto-generated method stub

            }

            public void postDeactivation(DispatchEvent event) {
                // TODO Auto-generated method stub

            }

            public void preActivation(DispatchEvent event)
                    throws DispatchException {
                preCalls.setValue(((Integer) preCalls.getValue()) + 1);
            }

            public void preDeactivation(DispatchEvent event)
                    throws DispatchException {
                // TODO Auto-generated method stub

            }
        };

        // Add view
        ViewHandler.addListener(listener);

        // Remove it immediately
        ViewHandler.removeListener(listener);

        ViewHandler.addView(MockView.class, new MockViewContainer());
        ViewHandler.activateView(MockView.class);

        assertEquals(0, ((Integer) preCalls.getValue()).intValue());
    }

    @Test
    public void cancelActivation() {
        final ValueContainer preCalls = new ValueContainer(0);
        final ValueContainer preActivation = new ValueContainer(0);
        final ValueContainer postCalls = new ValueContainer(0);
        final ValueContainer postActivation = new ValueContainer(0);
        final ValueContainer parentCalled = new ValueContainer(false);

        DispatchEventListener listener = new DispatchEventListener() {

            public void preDispatch(DispatchEvent event)
                    throws DispatchException {
                preCalls.setValue(((Integer) preCalls.getValue()) + 1);
            }

            public void postDispatch(DispatchEvent event) {
                postCalls.setValue(((Integer) postCalls.getValue()) + 1);
            }

            public void postActivation(DispatchEvent event) {
                postActivation
                        .setValue(((Integer) postActivation.getValue()) + 1);
            }

            public void postDeactivation(DispatchEvent event) {

            }

            public void preActivation(DispatchEvent event)
                    throws DispatchException {
                preActivation
                        .setValue(((Integer) preActivation.getValue()) + 1);
                throw new DispatchException();
            }

            public void preDeactivation(DispatchEvent event)
                    throws DispatchException {
            }
        };

        DispatchEventListener listener2 = new DispatchEventListener() {
            public void preDispatch(DispatchEvent event)
                    throws DispatchException {
                preCalls.setValue(((Integer) preCalls.getValue()) + 1);
            }

            public void postDispatch(DispatchEvent event) {
                postCalls.setValue(((Integer) postCalls.getValue()) + 1);
            }

            public void postActivation(DispatchEvent event) {
                postActivation
                        .setValue(((Integer) postActivation.getValue()) + 1);

            }

            public void postDeactivation(DispatchEvent event) {

            }

            public void preActivation(DispatchEvent event)
                    throws DispatchException {
                preActivation
                        .setValue(((Integer) preActivation.getValue()) + 1);

            }

            public void preDeactivation(DispatchEvent event)
                    throws DispatchException {
                // TODO Auto-generated method stub

            }
        };

        ViewHandler.addListener(listener);
        ViewHandler.addListener(listener2);

        ViewHandler.addView(MockView.class, new ViewContainer() {

            public void deactivate(View view) {

            }

            public void activate(View view) {
                parentCalled.setValue(true);
            }
        });

        assertEquals(0, ((Integer) preCalls.getValue()).intValue());
        assertEquals(0, ((Integer) preActivation.getValue()).intValue());
        assertEquals(0, ((Integer) postCalls.getValue()).intValue());
        assertEquals(0, ((Integer) postActivation.getValue()).intValue());

        ViewHandler.activateView(MockView.class);

        assertEquals(1, ((Integer) preCalls.getValue()).intValue());
        assertEquals(1, ((Integer) preActivation.getValue()).intValue());
        assertEquals(0, ((Integer) postCalls.getValue()).intValue());
        assertEquals(0, ((Integer) postActivation.getValue()).intValue());
        assertFalse((Boolean) parentCalled.getValue());
    }

    @Test
    public void cancelDeactivation() {
        final ValueContainer preDeactivation = new ValueContainer(0);
        final ValueContainer postDeactivation = new ValueContainer(0);

        DispatchEventListener listener = new DispatchEventListener() {

            public void preDispatch(DispatchEvent event)
                    throws DispatchException {
            }

            public void postDispatch(DispatchEvent event) {
            }

            public void postActivation(DispatchEvent event) {

            }

            public void postDeactivation(DispatchEvent event) {
                postDeactivation.setValue(((Integer) postDeactivation
                        .getValue()) + 1);
            }

            public void preActivation(DispatchEvent event)
                    throws DispatchException {

            }

            public void preDeactivation(DispatchEvent event)
                    throws DispatchException {
                preDeactivation
                        .setValue(((Integer) preDeactivation.getValue()) + 1);
                throw new DispatchException();
            }
        };

        DispatchEventListener listener2 = new DispatchEventListener() {

            public void preDispatch(DispatchEvent event)
                    throws DispatchException {
            }

            public void postDispatch(DispatchEvent event) {
            }

            public void postActivation(DispatchEvent event) {
            }

            public void postDeactivation(DispatchEvent event) {
                postDeactivation.setValue(((Integer) postDeactivation
                        .getValue()) + 1);

            }

            public void preActivation(DispatchEvent event)
                    throws DispatchException {

            }

            public void preDeactivation(DispatchEvent event)
                    throws DispatchException {
                preDeactivation
                        .setValue(((Integer) preDeactivation.getValue()) + 1);
            }
        };

        ViewHandler.addListener(listener);
        ViewHandler.addListener(listener2);

        MockViewContainer parent = new MockViewContainer();
        ViewHandler.addView(MockView.class, parent);

        assertEquals(0, ((Integer) preDeactivation.getValue()).intValue());
        assertEquals(0, ((Integer) postDeactivation.getValue()).intValue());

        ViewHandler.deactivateView(MockView.class);

        assertEquals(1, ((Integer) preDeactivation.getValue()).intValue());
        assertEquals(0, ((Integer) postDeactivation.getValue()).intValue());
    }

    @Test
    public void uriChangedOnActivation() {
        MockViewContainer parent = new MockViewContainer();
        ViewHandler.addView(MockView.class, parent);
        ViewHandler.addUri("test", MockView.class);
        UriFragmentUtility util = ViewHandler.getUriFragmentUtil();

        assertNull(util.getFragment());
        ViewHandler.activateView(MockView.class, true);
        assertEquals("test", util.getFragment());
    }

    @Test
    public void uriNotChangedOnActivation() {
        MockViewContainer parent = new MockViewContainer();
        ViewHandler.addView(MockView.class, parent);
        ViewHandler.addUri("test", MockView.class);
        UriFragmentUtility util = ViewHandler.getUriFragmentUtil();

        assertNull(util.getFragment());
        ViewHandler.activateView(MockView.class, false);
        assertNull(util.getFragment());
    }

    @Test
    public void viewActivatedOnUriChange() {
        final ValueContainer viewActivated = new ValueContainer(false);

        AbstractView<ComponentContainer> view = new AbstractView<ComponentContainer>(
                new VerticalLayout()) {
            private static final long serialVersionUID = 1L;

            public void activated(Object... params) {
                viewActivated.setValue(true);
            }

            public void deactivated(Object... params) {
                // TODO Auto-generated method stub

            }
        };

        ViewItem item = ViewHandler.addView("test", new MockViewContainer());
        item.setView(view);

        // Add two uris for the same view
        ViewHandler.addUri("test", "test");
        ViewHandler.addUri("test2", "test");

        UriFragmentUtility util = ViewHandler.getUriFragmentUtil();
        util.setFragment("test", false);
        assertFalse((Boolean) viewActivated.getValue());
        // Clear the fragment so that a change will happen
        util.setFragment("clear", false);

        util.setFragment("test", true);
        assertTrue((Boolean) viewActivated.getValue());

        viewActivated.setValue(false);
        util.setFragment("test2", true);
        assertTrue((Boolean) viewActivated.getValue());

    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullUri() {
        ViewHandler.addUri(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addEmptyUri() {
        ViewHandler.addUri("", "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addUriNullView() {
        ViewHandler.addUri("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addExistingUri() {
        ViewHandler.addView("viewId");
        ViewHandler.addUri("test", "viewId");
        ViewHandler.addUri("test", "viewId");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addUriNonExistingViewId() {
        ViewHandler.addUri("test", "viewId");
    }

    @Test
    public void removeUri() {
        ViewHandler.addView("viewId");
        ViewHandler.addUri("test", "viewId");
        ViewHandler.removeUri("test");
        // This should cause an exception if this test fails
        ViewHandler.addUri("test", "viewId");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullUri() {
        ViewHandler.removeUri(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEmptyUri() {
        ViewHandler.removeUri("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeWithNullApplication() {
        ViewHandler.initialize(null);

    }

    @Test
    public void initialize() {
        MockContext context = (MockContext) application.getContext();
        assertEquals(0, context.getListeners().size());
        ViewHandler.initialize(application);
        assertEquals(1, context.getListeners().size());
        assertEquals(ViewHandler.class, context.getListeners().get(0)
                .getClass());
    }

    @Test
    public void uriFragmentParameters() {
        final ValueContainer viewActivated = new ValueContainer(false);
        final ValueContainer parameters = new ValueContainer();

        AbstractView<ComponentContainer> view = new AbstractView<ComponentContainer>(
                new VerticalLayout()) {
            private static final long serialVersionUID = 1L;

            public void activated(Object... params) {
                viewActivated.setValue(true);
                parameters.setValue(params);
            }

            public void deactivated(Object... params) {
                // TODO Auto-generated method stub

            }
        };

        ViewItem item = ViewHandler.addView("test", new MockViewContainer());
        item.setView(view);

        // Add two uris for the same view
        ViewHandler.addUri("test", "test");
        UriFragmentUtility util = ViewHandler.getUriFragmentUtil();
        util.setFragment("test/foo/bar", true);
        assertTrue((Boolean) viewActivated.getValue());
        Object[] params = (Object[]) parameters.getValue();
        assertNotNull(params);
        assertEquals(2, params.length);
        assertEquals("foo", params[0]);
        assertEquals("bar", params[1]);

        parameters.setValue(null);
        util.setFragment("clear", false);
        util.setFragment("test", true);
        params = (Object[]) parameters.getValue();
        assertEquals(0, params.length);
    }
}
