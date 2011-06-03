package org.vaadin.appfoundation.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;

/**
 * Utility class for handling and chaning views in an application.
 * 
 * @author Kim
 * 
 */
public class ViewHandler implements TransactionListener,
        FragmentChangedListener {

    private static final long serialVersionUID = -3548570790687380424L;

    // A map between the view id and the view item
    private final Map<Object, ViewItem> viewMap = new HashMap<Object, ViewItem>();

    // A map between parent ids and parent views
    private final Map<Object, ViewContainer> parentMap = new HashMap<Object, ViewContainer>();

    // A list of all known dispatch event listeners.
    private final List<DispatchEventListener> listeners = new ArrayList<DispatchEventListener>();

    // Store this instance of the view handler in this thread local variable
    private static final ThreadLocal<ViewHandler> instance = new ThreadLocal<ViewHandler>();

    // A map between URIs and view ids
    private final Map<String, Object> uriMap = new HashMap<String, Object>();

    private final Application application;

    private ViewFactory defaultViewFactory = null;

    private UriFragmentUtility uriFragmentUtil = null;

    /**
     * 
     * @param application
     *            Current application instance
     */
    public ViewHandler(Application application) {
        instance.set(this);
        this.application = application;
    }

    /**
     * {@inheritDoc}
     */
    public void transactionEnd(Application application, Object transactionData) {
        // Clear thread local instance at the end of the transaction
        if (this.application == application) {
            instance.set(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void transactionStart(Application application, Object transactionData) {
        // Set the thread local instance
        if (this.application == application) {
            instance.set(this);
        }
    }

    /**
     * Add a new View to the ViewHandler. Takes as input a viewId. The user is
     * responsible of setting the view class. If the viewId is already in use,
     * then null is returned.
     * 
     * @param viewId
     *            The view's id
     * @return The resulting ViewItem object
     */
    public static ViewItem addView(Object viewId) {
        if (viewId == null) {
            throw new IllegalArgumentException("View id may not be null");
        }

        // Check if the viewId is already in use. If it is, then return null.
        if (instance.get().viewMap.containsKey(viewId)) {
            return null;
        }

        // Create a new ViewItem and add it to the map.
        ViewItem item = new ViewItem(viewId);
        instance.get().viewMap.put(viewId, item);

        // Check if we have a default ViewFactory defined. If one is defined,
        // then set it to the item.
        if (instance.get().defaultViewFactory != null) {
            item.setFactory(instance.get().defaultViewFactory);
        }
        return item;
    }

    /**
     * Add a new view to the view handler.
     * 
     * @param viewId
     *            The view's id
     * @param parent
     *            Parent view for the given view
     * @return The resulting ViewItem object
     */
    public static ViewItem addView(Object viewId, ViewContainer parent) {
        ViewItem item = addView(viewId);
        setParent(viewId, parent);
        return item;
    }

    /**
     * Add a new view. Returns the viewId. Make sure to set either the view
     * instance or the view class for the ViewItem.
     * 
     * @return The viewId
     */
    public static Object addView() {
        Object viewId = UUID.randomUUID();
        ViewItem item = new ViewItem(viewId);
        instance.get().viewMap.put(viewId, item);
        return viewId;
    }

    /**
     * Fetch the ViewItem for the given viewId. If the viewId is not found, then
     * null is returned.
     * 
     * @param viewId
     * @return The ViewItem object for the given viewId
     */
    public static ViewItem getViewItem(Object viewId) {
        // Check if the viewId exists in the map
        if (viewId != null && instance.get().viewMap.containsKey(viewId)) {
            return instance.get().viewMap.get(viewId);
        }

        return null;
    }

    /**
     * Removes the ViewItem from the handler for the given viewId.
     * 
     * @param viewId
     * @return Returns true if the viewId existed, otherwise false.
     */
    public static boolean removeView(Object viewId) {
        if (viewId != null && instance.get().viewMap.containsKey(viewId)) {
            instance.get().viewMap.remove(viewId);

            // Check if the view has an uri defined
            String uri = getUriForViewId(viewId);
            if (uri != null) {
                // An uri definition was found, remove it
                instance.get().uriMap.remove(uri);
            }
            return true;
        }

        return false;
    }

    /**
     * Search and return the uri for the given view id
     * 
     * @param viewId
     *            View id we want to get the uri for
     * @return Returns the uri if one is found, otherwise returns null
     */
    private static String getUriForViewId(Object viewId) {
        Iterator<String> it = instance.get().uriMap.keySet().iterator();
        while (it.hasNext()) {
            String uri = it.next();
            if (instance.get().uriMap.get(uri).equals(viewId)) {
                return uri;
            }
        }

        return null;
    }

    /**
     * Activate the view with the given viewId. You can specify any given amount
     * of parameters for the activation request. Each parameter is forwarded to
     * the View's activated() method.
     * 
     * @param viewId
     *            The view's viewId
     * @param params
     *            Parameters used for activating the view
     */
    public static void activateView(Object viewId, Object... params) {
        activateView(viewId, false, params);
    }

    /**
     * Activate the view with the given viewId. You can specify any given amount
     * of parameters for the activation request. Each parameter is forwarded to
     * the View's activated() method.
     * 
     * @param viewId
     *            The view's viewId
     * @param changeUriFramgent
     *            Should the uri fragment be changed if the view has one set
     * @param params
     *            Parameters used for activating the view
     */
    @SuppressWarnings("deprecation")
    public static void activateView(Object viewId, boolean changeUriFramgent,
            Object... params) {
        if (viewId != null && instance.get().viewMap.containsKey(viewId)
                && instance.get().parentMap.containsKey(viewId)) {
            // Get the ViewItem and parent for this viewId
            ViewItem item = instance.get().viewMap.get(viewId);
            ViewContainer parent = instance.get().parentMap.get(viewId);

            // Create the dispatch event object
            DispatchEvent event = new DispatchEvent(item, params);
            // Loop through all the dispatch event listeners
            try {
                for (DispatchEventListener listener : instance.get().listeners) {
                    listener.preDispatch(event);
                    listener.preActivation(event);
                }
            } catch (DispatchException e) {
                // The dispatch was canceled, stop the execution of this method.
                return;
            }

            // Tell the parent to activate the given view
            parent.activate(item.getView());

            // Tell the view that it has been activated
            item.getView().activated(params);

            String uri = getUriForViewId(viewId);
            if (changeUriFramgent && uri != null
                    && instance.get().uriFragmentUtil != null) {
                instance.get().uriFragmentUtil.setFragment(uri, false);
            }

            // View has been dispatched, send event
            for (DispatchEventListener listener : instance.get().listeners) {
                listener.postDispatch(event);
                listener.postActivation(event);
            }
        }
    }

    /**
     * Deactivate the view with the given viewId. You can specify any given
     * amount of parameters for the deactivation request. Each parameter is
     * forwarded to the View's deactivated() method.
     * 
     * @param viewId
     *            The view's viewId
     * @param params
     *            Parameters used for activating the view
     */
    public static void deactivateView(Object viewId, Object... params) {
        if (viewId != null && instance.get().viewMap.containsKey(viewId)
                && instance.get().parentMap.containsKey(viewId)) {
            // Get the ViewItem and parent for this viewId
            ViewItem item = instance.get().viewMap.get(viewId);
            ViewContainer parent = instance.get().parentMap.get(viewId);

            // Create the dispatch event object
            DispatchEvent event = new DispatchEvent(item, params);
            // Loop through all the dispatch event listeners
            try {
                for (DispatchEventListener listener : instance.get().listeners) {
                    listener.preDeactivation(event);
                }
            } catch (DispatchException e) {
                // The dispatch was canceled, stop the execution of this method.
                return;
            }

            // Tell the parent to activate the given view
            parent.deactivate(item.getView());

            // Tell the view that it has been activated
            item.getView().deactivated(params);

            // View has been dispatched, send event
            for (DispatchEventListener listener : instance.get().listeners) {
                listener.postDeactivation(event);
            }
        }
    }

    /**
     * Set the parent view for the given viewId.
     * 
     * @param viewId
     *            The viewId of the ViewItem
     * @param parent
     *            New parent for the view
     */
    public static void setParent(Object viewId, ViewContainer parent) {
        if (viewId != null && parent != null
                && instance.get().viewMap.containsKey(viewId)) {
            instance.get().parentMap.put(viewId, parent);
        }
    }

    /**
     * Add a dispatch event listener.
     * 
     * @param listener
     *            The new listener
     */
    public static void addListener(DispatchEventListener listener) {
        if (listener != null) {
            instance.get().listeners.add(listener);
        }
    }

    /**
     * Remove a dispatch event listener.
     * 
     * @param listener
     *            The listener to be removed
     */
    public static void removeListener(DispatchEventListener listener) {
        if (listener != null) {
            instance.get().listeners.remove(listener);
        }
    }

    /**
     * Set the default ViewFactory which is to be used in all <b>newly</b> added
     * views.
     * 
     * @param defaultViewFactory
     *            Default ViewFactory to be used in all new views.
     */
    public static void setDefaultViewFactory(ViewFactory defaultViewFactory) {
        instance.get().defaultViewFactory = defaultViewFactory;
    }

    /**
     * Get the current default ViewFactory.
     * 
     * @return Default ViewFactory
     */
    public static ViewFactory getDefaultViewFactory() {
        return instance.get().defaultViewFactory;
    }

    /**
     * Maps an uri to the given view id
     * 
     * @param uri
     * @param viewId
     */
    public static void addUri(String uri, Object viewId) {
        // Make sure the uri is valid
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("Uri must be defined");
        }

        // Make sure the view id is set
        if (viewId == null) {
            throw new IllegalArgumentException("View id must be defined");
        }

        if (instance.get().uriMap.containsKey(uri)) {
            throw new IllegalArgumentException(
                    "A view is already defined for this uri");
        }

        // Make sure that a view has been added for the given view id
        if (!instance.get().viewMap.containsKey(viewId)) {
            throw new IllegalArgumentException(
                    "View id not found - a valid view id must be provided");
        }

        // Add the uri to the map
        instance.get().uriMap.put(uri, viewId);
    }

    /**
     * Remove an uri which have been mapped to a specific view id
     * 
     * @param uri
     */
    public static void removeUri(String uri) {
        // Make sure the uri is valid
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("Uri must be defined");
        }

        instance.get().uriMap.remove(uri);
    }

    public static UriFragmentUtility getUriFragmentUtil() {
        // Check if an UriFragmentUtil is defined, if not, create a new one
        if (instance.get().uriFragmentUtil == null) {
            instance.get().uriFragmentUtil = new UriFragmentUtility();
            // Register this instance of the view handler as the uri fragment
            // change listener
            instance.get().uriFragmentUtil.addListener(instance.get());
        }

        return instance.get().uriFragmentUtil;
    }

    /**
     * {@inheritDoc}
     */
    public void fragmentChanged(FragmentChangedEvent source) {
        // Make sure we don't get a null for some reason
        if (source != null) {
            // Get the uri fragment
            String fragment = source.getUriFragmentUtility().getFragment();

            int i = fragment.indexOf('/');
            Object[] params = null;
            String uri;
            if (i < 0) {
                uri = fragment;
            } else {
                uri = fragment.substring(0, i);
                params = fragment.subSequence(i + 1, fragment.length())
                        .toString().split("/");
            }

            // Check if the fragment exists in the map
            if (instance.get().uriMap.containsKey(uri)) {
                // The view was found, activate it
                Object viewId = instance.get().uriMap.get(uri);
                if (params != null) {
                    activateView(viewId, params);
                } else {
                    activateView(viewId);
                }
            }
        }
    }

    /**
     * Initializes the {@link ViewHandler} for the given {@link Application}
     * 
     * @param application
     */
    public static void initialize(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("Application may not be null");
        }
        ViewHandler handler = new ViewHandler(application);
        application.getContext().addTransactionListener(handler);
    }

}
