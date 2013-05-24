/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.sencha.gxt.core.client.dom.CompositeElement;

/**
 * Specialized <code>EventPreview</code>. Provides auto hide support and the
 * ability to add elements which should be ignored when auto hide is enabled.
 */
public class BaseEventPreview implements NativePreviewHandler {

  private static int lastX, lastY;

  /**
   * Returns the last client x value when a base event preview is on top of the
   * preview stack.
   * 
   * @return the last client y value
   */
  public static int getLastClientX() {
    return lastX;
  }

  /**
   * Returns the last client y value when a base event preview is on top of the
   * preview stack.
   * 
   * @return the last client y value
   */
  public static int getLastClientY() {
    return lastY;
  }

  /**
   * Returns the last xy value when a base event preview is on top of the
   * preview stack.
   * 
   * @return the last client x and client y
   */
  public static Point getLastXY() {
    return new Point(lastX, lastY);
  }

  private CompositeElement ignoreList = new CompositeElement();
  private boolean autoHide = true;
  private boolean autoHideAllowEvent;
  private HandlerRegistration handler;
  private int keyEvent;

  public BaseEventPreview() {
    keyEvent = KeyNav.getKeyEvent();
  }

  /**
   * Adds this instance to the event preview stack.
   */
  public void add() {
    if (handler == null) {
      handler = Event.addNativePreviewHandler(this);
      onAdd();
    }
  }

  /**
   * Returns the ignore list.
   * 
   * @return this list
   */
  public CompositeElement getIgnoreList() {
    return ignoreList;
  }

  /**
   * Returns true if auto hide is enabled.
   * 
   * @return the auto hide state
   */
  public boolean isAutoHide() {
    return autoHide;
  }

  /**
   * Returns true if the auto hide event is cancelled.
   * 
   * @return the auto hide event
   */
  public boolean isAutoHideAllowEvent() {
    return autoHideAllowEvent;
  }

  public void onPreviewNativeEvent(NativePreviewEvent event) {
    Event e = event.getNativeEvent().cast();

    lastX = e.getClientX();
    lastY = e.getClientY();

    if (autoHide && onAutoHidePreview(event)) {
      if (autoHideAllowEvent) {
        event.cancel();
      }
      remove();
    }

    if (!onPreview(event)) {
      event.cancel();
    }
  }

  /**
   * Pushes the event preview to the stop of the stack.
   */
  public void push() {
    if (handler != null) {
      remove();
      add();
    }
  }

  /**
   * Removes event preview.
   */
  public void remove() {
    if (handler != null) {
      handler.removeHandler();
      handler = null;
      onRemove();
    }
  }

  /**
   * True to remove the event preview when the user clicks on an element not it
   * the ignore list (default to true).
   * 
   * @param autoHide the auto hide state
   */
  public void setAutoHide(boolean autoHide) {
    this.autoHide = autoHide;
  }

  /**
   * Sets if the event that removes event preview is cancelled (default to
   * true). Only applies when {@link #setAutoHide(boolean)} is true.
   * 
   * @param autoHideAllowEvent true to cancel the event
   */
  public void setAutoHideCancelEvent(boolean autoHideAllowEvent) {
    this.autoHideAllowEvent = autoHideAllowEvent;
  }

  /**
   * List of elements to be ignored when autoHide is enabled. An example of
   * usage would be a menu item that displays a sub menu. When the sub menu is
   * displayed, the menu item is added to the ignore list so that the sub menu
   * will not close when the mousing over the item.
   * 
   * @param ignoreList the ignore list
   */
  public void setIgnoreList(CompositeElement ignoreList) {
    this.ignoreList = ignoreList;
  }

  /**
   * Sets the key event type used to determine key presses for
   * {@link #onPreview(Event.NativePreviewEvent)}. By default, the key press
   * event is determined using {@link KeyNav#getKeyEvent()}.
   * 
   * @param type the key event type
   */
  public void setKeyEvent(int type) {
    keyEvent = type;
  }

  protected void onAdd() {

  }

  /**
   * Called right before event preview will be removed from auto hide.
   * 
   * @param pe the preview event
   * @return true to allow auto hide, false to cancel
   */
  protected boolean onAutoHide(NativePreviewEvent pe) {
    return true;
  }

  /**
   * Called when a preview event is received and {@link #autoHide} is enabled.
   * 
   * @param pe the preview event
   * @return true to remove event preview
   */
  protected boolean onAutoHidePreview(NativePreviewEvent pe) {
    int type = pe.getTypeInt();

    switch (type) {
      case Event.ONMOUSEDOWN:
      case Event.ONMOUSEUP:
      case Event.ONCLICK:
      case Event.ONDBLCLICK: {
        boolean ignore = getIgnoreList().is((Element) pe.getNativeEvent().getEventTarget().cast());
        if (!ignore && onAutoHide(pe)) {
          return true;
        }
      }
    }
    return false;
  }

  protected void onClick(NativePreviewEvent pe) {

  }

  /**
   * Called when a preview event is received.
   * 
   * @param pe the preview event
   * @return true to allow the event
   */
  protected boolean onPreview(NativePreviewEvent pe) {
    if (pe.getTypeInt() == keyEvent) {
      onPreviewKeyPress(pe);
      return true;
    }
    switch (pe.getTypeInt()) {
      case Event.ONCLICK:
        onClick(pe);
    }
    return true;
  }

  /**
   * Called when a preview key press event is received.
   * 
   * @param pe the preview event
   */
  protected void onPreviewKeyPress(NativePreviewEvent pe) {

  }

  protected void onRemove() {

  }

}
