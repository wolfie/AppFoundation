package org.vaadin.appfoundation.test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.ApplicationResource;

/**
 * Mock implementation of a Vaadin application
 * 
 * @author Kim
 * 
 */
public class MockApplication extends Application {

    private static final long serialVersionUID = -7592972194969208126L;

    private MockContext context = new MockContext();

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public ApplicationContext getContext() {
        return context;
    }

    public static class MockContext implements ApplicationContext {

        private static final long serialVersionUID = -1789702867726537303L;

        private List<TransactionListener> listeners = new ArrayList<TransactionListener>();

        public void addTransactionListener(TransactionListener listener) {
            listeners.add(listener);
        }

        public List<TransactionListener> getListeners() {
            return listeners;
        }

        public String generateApplicationResourceURL(
                ApplicationResource resource, String urlKey) {
            // TODO Auto-generated method stub
            return null;
        }

        public Collection<Application> getApplications() {
            // TODO Auto-generated method stub
            return null;
        }

        public File getBaseDirectory() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getURLKey(URL context, String relativeUri) {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isApplicationResourceURL(URL context, String relativeUri) {
            // TODO Auto-generated method stub
            return false;
        }

        public void removeTransactionListener(TransactionListener listener) {
            // TODO Auto-generated method stub

        }

    }

}
