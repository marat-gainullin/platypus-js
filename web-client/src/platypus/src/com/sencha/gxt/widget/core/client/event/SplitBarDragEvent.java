/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.SplitBar;

/**
 * Represents the after layout event.
 */
public class SplitBarDragEvent extends GwtEvent<SplitBarDragEvent.SplitBarDragHandler> {

  /**
   * Implemented by objects that handle {@link SplitBarDragEvent}.
   */
  public interface SplitBarDragHandler extends EventHandler {
    void onDragEvent(SplitBarDragEvent event);
  }
  
  /**
   * SplitBarDragStartHandler type.
   */
  private static Type<SplitBarDragEvent.SplitBarDragHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<SplitBarDragEvent.SplitBarDragHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<SplitBarDragEvent.SplitBarDragHandler>();
    }
    return TYPE;
  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link SplitBarDragEvent} events.
   */
  public interface HasSplitBarDragHandlers {

    /**
     * Adds a {@link SplitBarDragHandler} handler for
     * {@link SplitBarDragEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addSplitBarDragHandler(SplitBarDragHandler handler);
  }

  private int size;
  private boolean start;

  public SplitBarDragEvent(boolean start, int size) {
    this.start = start;
    this.size = size;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<SplitBarDragEvent.SplitBarDragHandler> getAssociatedType() {
    return (Type) TYPE;
  }
  
  public boolean isStart() {
    return start;
  }

  public int getSize() {
    return size;
  }

  public SplitBar getSource() {
    return (SplitBar) super.getSource();
  }

  @Override
  protected void dispatch(SplitBarDragEvent.SplitBarDragHandler handler) {
    handler.onDragEvent(this);
  }

}
