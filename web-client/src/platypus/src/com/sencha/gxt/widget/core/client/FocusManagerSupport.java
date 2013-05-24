/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

/**
 * Component support class for Focus Manager.
 */
public class FocusManagerSupport {

  private boolean ignore;
  private String nextId;
  private String previousId;
  private Component c;

  FocusManagerSupport(Component c) {

  }

  /**
   * Returns the target widget.
   * 
   * @return the target widget
   */
  public Component getComponent() {
    return c;
  }

  /**
   * Returns the next widget id.
   * 
   * @return the next widget id
   */
  public String getNextId() {
    return nextId;
  }

  /**
   * Returns the previous widget id.
   * 
   * @return the previous widget id
   */
  public String getPreviousId() {
    return previousId;
  }

  /**
   * Returns true if the widget will be ignored by the ARIA and FocusManager
   * API.
   * 
   * @return true if widget is being ignored
   */
  public boolean isIgnore() {
    return ignore;
  }

  /**
   * True to mark this widget to be ignored by the ARIA and FocusManager API
   * (defaults to false). Typically set to true for any containers that should
   * not be navigable to.
   * 
   * @param ignore true to ignore
   */
  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  /**
   * The id of the widget to navigate to when TAB is pressed (defaults to
   * null). When set, the focus manager will override its default behavior to
   * determine the next focusable widget.
   * 
   * @param nextId the next widget id
   */
  public void setNextId(String nextId) {
    this.nextId = nextId;
  }

  /**
   * The id of the widget to navigate to when SHIFT-TAB is pressed (defaults
   * to null). When set, the focus manager will override its default behavior to
   * determine the previous focusable widget.
   * 
   * @param previousId the previous widget id
   */
  public void setPreviousId(String previousId) {
    this.previousId = previousId;
  }
}
