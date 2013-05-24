/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.List;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.DelayedTask;

/**
 * Simple checkbox field. {@link ValueChangeEvent}s are fired when the checkbox
 * state is changed by the user, instead of waiting for a {@link BlurEvent}.
 */
public class CheckBox extends Field<Boolean> implements HasChangeHandlers {

  private DelayedTask alignTask = new DelayedTask() {

    @Override
    public void onExecute() {
      alignElements();
    }
  };

  /**
   * Creates a new check box.
   */
  public CheckBox() {
    this(new CheckBoxCell());
  }

  /**
   * Creates a new check box.
   * 
   * @param cell the check box cell
   */
  public CheckBox(CheckBoxCell cell) {
    super(cell);

    setValue(false, false);
  }

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
  }

  @Override
  public void clearInvalid() {
    // do nothing
  }

  /**
   * Returns the box label.
   * 
   * @return the box label
   */
  public String getBoxLabel() {
    return getCell().getBoxLabel();
  }

  @Override
  public CheckBoxCell getCell() {
    return (CheckBoxCell) super.getCell();
  }

  /**
   * The text that appears beside the checkbox (defaults to null).
   * 
   * @param boxLabel the box label
   */
  public void setBoxLabel(String boxLabel) {
    getCell().setBoxLabel(getElement(), boxLabel);
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    super.setReadOnly(readOnly);
    getCell().getInputElement(getElement()).setReadOnly(readOnly);
  }

  @Override
  public void setTabIndex(int tabIndex) {
    this.tabIndex = tabIndex;
    getCell().getInputElement(getElement()).setTabIndex(tabIndex);
  }

  @Override
  public void setValue(Boolean value) {
    // TODO if we support a default value, replace this with that
    if (value == null) {
      value = false;
    }
    setValue(value, false);
    // IE6 is losing state when detached and attached
    redraw();
  }

  @Override
  protected void markInvalid(List<EditorError> msg) {
    // do nothing
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    alignTask.delay(10);
  }

  protected void alignElements() {
    if (getBoxLabel() == null) {
      getCell().getInputElement(getElement()).<XElement> cast().center(getElement());
    }
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    alignTask.delay(10);
  }

  @Override
  protected void onRedraw() {
    super.onRedraw();
    getCell().getInputElement(getElement()).setTabIndex(getTabIndex());
  }
}
