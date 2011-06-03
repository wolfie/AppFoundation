package org.vaadin.appfoundation.test.authorization.memory;

import org.vaadin.appfoundation.authorization.PermissionManager;
import org.vaadin.appfoundation.authorization.Resource;
import org.vaadin.appfoundation.authorization.Role;
import org.vaadin.appfoundation.authorization.memory.MemoryPermissionManager;
import org.vaadin.appfoundation.test.authorization.AbstractPermissionManagerTest;
import org.vaadin.appfoundation.test.authorization.ResourceMock;
import org.vaadin.appfoundation.test.authorization.RoleMock;

public class MemoryPermissionManagerTest extends AbstractPermissionManagerTest {

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
        return new MemoryPermissionManager();
    }

}
