/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.dom;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.sencha.gxt.core.client.util.BaseEventPreview;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Rectangle;

/**
 * Automatically scrolls an element when the mouse is near the top or bottom of the element.
 * 
 * <p /> Use {@link #start()} and {@link #stop()} enable and disable.
 */
public class AutoScrollSupport {

  private boolean autoScroll = true;
  private int scrollDelay = 400;
  private int scrollRepeatDelay = 300;
  private Rectangle bottomBounds, topBounds;
  private int scrollRegionHeight = 25;
  private XElement scrollElement;
  private boolean active;

  private DelayedTask scrollUpTask = new DelayedTask() {

    @Override
    public void onExecute() {
      onScrollUp();
    }
  };

  private DelayedTask scrollDownTask = new DelayedTask() {

    @Override
    public void onExecute() {
      onScrollDown();
    }
  };

  private BaseEventPreview preview = new BaseEventPreview() {
    protected boolean onPreview(NativePreviewEvent pe) {
      super.onPreview(pe);
      if (pe.getTypeInt() == Event.ONMOUSEMOVE) {
        onMove(pe.getNativeEvent());
      }
      return true;

    };

  };

  /**
   * Creates a new scroll support instance. The scroll element must be set, see
   * {@link #setScrollElement(XElement)}.
   */
  public AutoScrollSupport() {
    preview.setAutoHide(false);
  }

  /**
   * Creates a new scroll support instance.
   * 
   * @param scrollElement the scroll element
   */
  public AutoScrollSupport(XElement scrollElement) {
    this();
    setScrollElement(scrollElement);
  }

  /**
   * Returns the scroll delay.
   * 
   * @return the scroll delay in milliseconds
   */
  public int getScrollDelay() {
    return scrollDelay;
  }

  /**
   * Returns the scroll element.
   * 
   * @return the scroll element
   */
  public XElement getScrollElement() {
    return scrollElement;
  }

  /**
   * Returns the scroll region height.
   * 
   * @return the scroll region height
   */
  public int getScrollRegionHeight() {
    return scrollRegionHeight;
  }

  /**
   * Returns the scroll repeat delay.
   * 
   * @return the scroll repeat delay
   */
  public int getScrollRepeatDelay() {
    return scrollRepeatDelay;
  }

  /**
   * Returns true if auto scroll is enabled.
   * 
   * @return true if auto scroll is enabled, otherwise false
   */
  public boolean isAutoScroll() {
    return autoScroll;
  }

  /**
   * True to enable auto scroll (defaults to true).
   * 
   * @param autoScroll true if auto scroll is enabled
   */
  public void setAutoScroll(boolean autoScroll) {
    this.autoScroll = autoScroll;
  }

  /**
   * Sets the amount of time before auto scroll is activated (defaults to 400).
   * 
   * @param scrollDelay the scroll delay in milliseconds
   */
  public void setScrollDelay(int scrollDelay) {
    this.scrollDelay = scrollDelay;
  }

  /**
   * Sets the scroll element.
   * 
   * @param scrollElement the scroll element
   */
  public void setScrollElement(XElement scrollElement) {
    assert scrollElement != null;
    this.scrollElement = scrollElement;
  }

  /**
   * Sets the height of the scroll region (defaults to 25).
   * 
   * @param scrollRegionHeight the scroll region in pixels
   */
  public void setScrollRegionHeight(int scrollRegionHeight) {
    this.scrollRegionHeight = scrollRegionHeight;
  }

  /**
   * Sets the amount of time between scroll changes after auto scrolling is
   * activated (defaults to 300).
   * 
   * @param scrollRepeatDelay the repeat delay in milliseconds
   */
  public void setScrollRepeatDelay(int scrollRepeatDelay) {
    this.scrollRepeatDelay = scrollRepeatDelay;
  }

  /**
   * Starts monitoring for auto scroll.
   */
  public void start() {
    if (!active) {
      active = true;
      onStart();
    }
  }

  /**
   * Stops monitoring for auto scroll.
   */
  public void stop() {
    active = false;
    preview.remove();
    scrollDownTask.cancel();
    scrollUpTask.cancel();
  }

  protected void onMove(NativeEvent event) {
    Point p = new Point(event.getClientX(), event.getClientY());
    if (topBounds.contains(p)) {
      scrollUpTask.delay(scrollDelay);
      scrollDownTask.cancel();
    } else if (bottomBounds.contains(p)) {
      scrollDownTask.delay(scrollDelay);
      scrollUpTask.cancel();
    } else {
      scrollUpTask.cancel();
      scrollDownTask.cancel();
    }
  }

  protected void onScrollDown() {
    scrollElement.setScrollTop(scrollElement.getScrollTop() + scrollRegionHeight);
    scrollDownTask.delay(scrollRepeatDelay);
  }

  protected void onScrollUp() {
    scrollElement.setScrollTop(Math.max(0, scrollElement.getScrollTop() - scrollRegionHeight));
    scrollUpTask.delay(scrollRepeatDelay);
  }

  protected void onStart() {
    if (!autoScroll) return;

    topBounds = scrollElement.getBounds();
    topBounds.setHeight(20);

    bottomBounds = scrollElement.getBounds();
    bottomBounds.setY(bottomBounds.getY() + bottomBounds.getHeight() - 20);
    bottomBounds.setHeight(20);

    preview.add();
  }
}
