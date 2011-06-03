package org.vaadin.appfoundation.view;

/**
 * Interface for view dispatch events listeners.
 * 
 * @author Kim
 * 
 */
public interface DispatchEventListener {

    /**
     * Called before the {@link ViewHandler} activates a view.
     * 
     * @param event
     * @deprecated Use preActivation instead
     */
    @Deprecated
    public void preDispatch(DispatchEvent event) throws DispatchException;

    /**
     * Called before the {@link ViewHandler} activates a view.
     * 
     * @param event
     * @throws DispatchException
     *             if the activation process should be cancelled
     */

    public void preActivation(DispatchEvent event) throws DispatchException;

    /**
     * Called before the {@link ViewHandler} deactivates a view.
     * 
     * @param event
     * @throws DispatchException
     *             if the deactivation process should be cancelled
     */
    public void preDeactivation(DispatchEvent event) throws DispatchException;

    /**
     * Called after the {@link ViewHandler} have activated a view.
     * 
     * @param event
     * @deprecated Use postActivation instead
     */
    @Deprecated
    public void postDispatch(DispatchEvent event);

    /**
     * Called after the {@link ViewHandler} activated a view.
     * 
     * @param event
     */
    public void postActivation(DispatchEvent event);

    /**
     * Called after the {@link ViewHandler} deactivated a view.
     * 
     * @param event
     */
    public void postDeactivation(DispatchEvent event);

}
