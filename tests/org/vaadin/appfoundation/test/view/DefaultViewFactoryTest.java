package org.vaadin.appfoundation.test.view;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.vaadin.appfoundation.view.AbstractView;
import org.vaadin.appfoundation.view.DefaultViewFactory;

import com.vaadin.ui.VerticalLayout;

public class DefaultViewFactoryTest {

    @Test
    public void initView() {
        DefaultViewFactory factory = new DefaultViewFactory();
        assertNull(factory.initView("foobar"));
        assertTrue(factory.initView(MockView.class) instanceof MockView);
    }

    @Test
    public void initPrivateConstructorView() {
        DefaultViewFactory factory = new DefaultViewFactory();
        assertNull(factory.initView(MockView1.class));
    }

    @Test
    public void initViewWithParamInConstructor() {
        DefaultViewFactory factory = new DefaultViewFactory();
        assertNull(factory.initView(MockView2.class));
    }

    public static class MockView1 extends AbstractView<VerticalLayout> {
        private static final long serialVersionUID = 1L;

        private MockView1() {
            super(new VerticalLayout());
        }

        public void activated(Object... params) {
            // TODO Auto-generated method stub

        }

        public void deactivated(Object... params) {
            // TODO Auto-generated method stub

        }
    }

    public static class MockView2 extends AbstractView<VerticalLayout> {
        private static final long serialVersionUID = 1L;

        public MockView2(String foo) {
            super(new VerticalLayout());
        }

        public void activated(Object... params) {
            // TODO Auto-generated method stub

        }

        public void deactivated(Object... params) {
            // TODO Auto-generated method stub

        }
    }
}
