/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.dnd.core.client;

import java.util.List;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDragCancelEvent.DndDragCancelHandler;
import com.sencha.gxt.dnd.core.client.DndDragCancelEvent.HasDndDragCancelHandlers;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.DndDragEnterHandler;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.HasDndDragEnterHandlers;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent.DndDragLeaveHandler;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent.HasDndDragLeaveHandlers;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent.DndDragMoveHandler;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent.HasDndDragMoveHandlers;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent.HasDndDropHandlers;
import com.sencha.gxt.widget.core.client.Component;

/**
 * Enables a component to act as the target of a drag and drop operation (i.e. a
 * user can drop data on the component).
 * <p />
 * While the cursor is over a target, the target is responsible for determining
 * if the drop is valid and showing any visual indicators for the drop. The
 * {@link StatusProxy} object should be used to specify if the drop is valid,
 * and can also be used to change the values of the proxy object displayed by
 * the cursor.
 */
public class DropTarget implements HasDndDragEnterHandlers, HasDndDragLeaveHandlers, HasDndDragCancelHandlers,
    HasDndDragMoveHandlers, HasDndDropHandlers {

  protected Widget component;
  protected Feedback feedback;
  protected Operation operation;
  protected String overStyle;

  private boolean allowSelfAsSource;
  private SimpleEventBus eventBus;
  private HandlerRegistration componentRegistration;
  private AttachEvent.Handler componentHandler = new Handler() {

    @Override
    public void onAttachOrDetach(AttachEvent event) {
      if (event.isAttached()) {
        onComponentAttach();
      } else {
        onComponentDetach();
      }
    }
  };

  private boolean enabled = true;
  private String group = "";

  /**
   * Creates a new drop target.
   * 
   * @param target the target widget
   */
  public DropTarget(Widget target) {
    this.component = target;
    this.operation = Operation.MOVE;
    this.feedback = Feedback.APPEND;
    componentRegistration = component.addAttachHandler(componentHandler);

    if (component.isAttached()) {
      onComponentAttach();
    }
  }

  @Override
  public HandlerRegistration addDragCancelHandler(DndDragCancelHandler handler) {
    return ensureHandlers().addHandler(DndDragCancelEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addDragEnterHandler(DndDragEnterHandler handler) {
    return ensureHandlers().addHandler(DndDragEnterEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addDragLeaveHandler(DndDragLeaveHandler handler) {
    return ensureHandlers().addHandler(DndDragLeaveEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addDragMoveHandler(DndDragMoveHandler handler) {
    return ensureHandlers().addHandler(DndDragMoveEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addDropHandler(DndDropHandler handler) {
    return ensureHandlers().addHandler(DndDropEvent.getType(), handler);
  }

  /**
   * Disables the drag source.
   */
  public void disable() {
    enabled = false;
  }

  /**
   * Enables the drag source.
   */
  public void enable() {
    enabled = true;
  }

  /**
   * Returns the target's feedback setting.
   * 
   * @return the feedback
   */
  public Feedback getFeedback() {
    return feedback;
  }

  /**
   * Returns the target's group name.
   * 
   * @return the group name
   */
  public String getGroup() {
    return group;
  }

  /**
   * Returns the target's operation.
   * 
   * @return the operation
   */
  public Operation getOperation() {
    return operation;
  }

  /**
   * Returns the target's over style.
   * 
   * @return the over style
   */
  public String getOverStyle() {
    return overStyle;
  }

  /**
   * Returns the target's widget.
   * 
   * @return the widget
   */
  public Widget getWidget() {
    return component;
  }

  /**
   * Returns true if internal drops are allowed.
   * 
   * @return true for internal drops
   */
  public boolean isAllowSelfAsSource() {
    return allowSelfAsSource;
  }

  /**
   * Returns true if the drag source is enabled.
   * 
   * @return true for enabled
   */
  public boolean isEnabled() {
    return enabled && (component instanceof Component ? ((Component) component).isEnabled() : true);
  }

  /**
   * Unregisters the target as a drop target.
   */
  public void release() {
    componentRegistration.removeHandler();
    if (component.isAttached()) {
      onComponentDetach();
    }
  }

  /**
   * Sets whether internal drops are allowed (defaults to false).
   * 
   * @param allowSelfAsSource true to allow internal drops
   */
  public void setAllowSelfAsSource(boolean allowSelfAsSource) {
    this.allowSelfAsSource = allowSelfAsSource;
  }

  /**
   * Sets the target's feedback. Feedback determines the type of visual
   * indicators a drop target supports. Subclasses will determine range of valid
   * values.
   * 
   * @param feedback the feedback
   */
  public void setFeedback(Feedback feedback) {
    this.feedback = feedback;
  }

  /**
   * Sets the drag group. If specified, only drag sources with the same group
   * value are allowed.
   * 
   * @param group the group name
   */
  public void setGroup(String group) {
    this.group = group;
  }

  /**
   * Sets the operation for the drop target which specifies if data should be
   * moved or copied when dropped. Drag sources use this value to determine if
   * the target data should be removed from the source widget.
   * 
   * @param operation the operation
   */
  public void setOperation(Operation operation) {
    this.operation = operation;
  }

  /**
   * Sets the style name to be applied when the cursor is over the target
   * (defaults to null).
   * 
   * @param overStyle the over style
   */
  public void setOverStyle(String overStyle) {
    this.overStyle = overStyle;
  }

  protected void onComponentAttach() {
    DNDManager.get().registerDropTarget(this);
  }

  protected void onComponentDetach() {
    DNDManager.get().unregisterDropTarget(this);
  }

  /**
   * Called if the user cancels the drag operations while the mouse is over the
   * target.
   * 
   * @param event the drag cancel event
   */
  protected void onDragCancelled(DndDragCancelEvent event) {
    Insert.get().hide();
  }

  /**
   * Called when the user releases the mouse over the target widget.
   * 
   * @param event the drop event
   */
  protected void onDragDrop(DndDropEvent event) {

  }

  /**
   * Called when the cursor first enters the bounds of the drop target.
   * Subclasses or listeners can change the status of status proxy via the
   * passed event.
   * 
   * @param event the drag enter event
   */
  protected void onDragEnter(DndDragEnterEvent event) {

  }

  /**
   * Called when a drop fails.
   * 
   * @param event the drop event
   */
  protected void onDragFail(DndDropEvent event) {

  }

  /**
   * Called when the cursor leaves the target.
   * 
   * @param event the drag leave event
   */
  protected void onDragLeave(DndDragLeaveEvent event) {

  }

  /**
   * Called when the cursor is moved within the target widget. Subclasses or
   * listeners can change the status of status proxy via the passed event. If
   * either a subclass or listener sets
   * {@link CancellableEvent#setCancelled(boolean)} to true,
   * {@link #showFeedback(DndDragMoveEvent)} will be called.
   * 
   * @param event the dd event
   */
  protected void onDragMove(DndDragMoveEvent event) {

  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected List<Object> prepareDropData(Object data, boolean convertTreeStoreModel) {
    if (data instanceof List) {
      return (List) data;
    }
    // List<ModelData> models = new ArrayList<ModelData>();
    // if (data instanceof ModelData) {
    // if (convertTreeStoreModel && data instanceof TreeStoreModel) {
    // models.add(((TreeStoreModel) data).getModel());
    // } else {
    // models.add((ModelData) data);
    // }
    // } else if (data instanceof List) {
    // for (Object obj : (List) data) {
    // if (obj instanceof ModelData) {
    // if (convertTreeStoreModel && obj instanceof TreeStoreModel) {
    // models.add(((TreeStoreModel) obj).getModel());
    // } else {
    // models.add((ModelData) obj);
    // }
    // }
    // }
    // }
    return null;// models;
  }

  /**
   * Called as the mouse is moved over the target widget. The default
   * implementation does nothing.
   * 
   * @param event the dd event
   */
  protected void showFeedback(DndDragMoveEvent event) {

  }

  SimpleEventBus ensureHandlers() {
    return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
  }

  boolean handleDragEnter(DndDragMoveEvent event) {
    DndDragEnterEvent e = new DndDragEnterEvent(component, event.getDragSource(), event.getDragMoveEvent(),
        event.getStatusProxy());
    event.setCancelled(false);
    event.getStatusProxy().setStatus(true);
    onDragEnter(e);

    ensureHandlers().fireEventFromSource(e, this);

    if (e.isCancelled()) {
      event.getStatusProxy().setStatus(false);
      return false;
    }
    if (overStyle != null) {
      component.addStyleName(overStyle);
    }
    return true;
  }

  void handleDragLeave(DndDragMoveEvent event) {
    if (overStyle != null) {
      component.removeStyleName(overStyle);
    }
    event.getStatusProxy().setStatus(false);
    Insert.get().hide();

    DndDragLeaveEvent e = new DndDragLeaveEvent(component, event.getDragSource(), event.getDragMoveEvent(),
        event.getStatusProxy());

    onDragLeave(e);
    ensureHandlers().fireEventFromSource(e, this);
  }

  void handleDragMove(DndDragMoveEvent event) {
    showFeedback(event);
    onDragMove(event);
  }

  void handleDrop(DndDropEvent event) {
    Insert.get().hide();
    if (overStyle != null) {
      component.removeStyleName(overStyle);
    }
    onDragDrop(event);
  }

}
