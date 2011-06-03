package org.vaadin.appfoundation.test.authorization.jpa;

import org.junit.After;
import org.junit.Before;
import org.vaadin.appfoundation.authorization.PermissionManager;
import org.vaadin.appfoundation.authorization.Resource;
import org.vaadin.appfoundation.authorization.Role;
import org.vaadin.appfoundation.authorization.jpa.JPAPermissionManager;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;
import org.vaadin.appfoundation.test.authorization.AbstractPermissionManagerTest;
import org.vaadin.appfoundation.test.authorization.ResourceMock;
import org.vaadin.appfoundation.test.authorization.RoleMock;

public class JPAPermissionManagerTest extends AbstractPermissionManagerTest {

    private String persistenceName = "default";

    @Before
    public void setUp() throws InstantiationException, IllegalAccessException {
        FacadeFactory.registerFacade(persistenceName, true);
    }

    @After
    public void tearDown() {
        FacadeFactory.removeFacade(persistenceName);

        if (persistenceName.equals("oracle")) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Resource createResource() {
        return new ResourceMock();
    }

    @Override
    public Role createRole() {
        return new RoleMock();
    }

    @Override
    public PermissionManager getPermissionHandler() {
        return new JPAPermissionManager();
    }

}
