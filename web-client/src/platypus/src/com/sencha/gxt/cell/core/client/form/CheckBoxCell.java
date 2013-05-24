/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;

public class CheckBoxCell extends ValueBaseInputCell<Boolean> {

  public interface CheckBoxAppearance extends ValueBaseFieldAppearance {
    void render(SafeHtmlBuilder sb, Boolean value, CheckBoxCellOptions opts);

    void setBoxLabel(String boxLabel, XElement parent);
  }

  public static class CheckBoxCellOptions extends FieldAppearanceOptions {

    private String boxLabel;

    public String getBoxLabel() {
      return boxLabel;
    }

    public void setBoxLabel(String boxLabel) {
      this.boxLabel = boxLabel;
    }

  }

  protected final CheckBoxAppearance appearance;
  private String boxLabel;

  public CheckBoxCell() {
    this(GWT.<CheckBoxAppearance> create(CheckBoxAppearance.class));
  }

  public CheckBoxCell(CheckBoxAppearance appearance) {
    super(appearance);
    this.appearance = appearance;
  }

  public String getBoxLabel() {
    return boxLabel;
  }

  @Override
  public boolean isEditing(Context context, Element parent, Boolean value) {
    // A checkbox is never in "edit mode". There is no intermediate state
    // between checked and unchecked.
    return false;
  }

  @Override
  public void onBrowserEvent(Context context, Element parent, Boolean value, NativeEvent event,
      ValueUpdater<Boolean> valueUpdater) {
    Element target = event.getEventTarget().cast();
    if (!parent.isOrHasChild(target)) {
      return;
    }
    super.onBrowserEvent(context, parent, value, event, valueUpdater);

    String type = event.getType();

    if ("click".equals(type) && isReadOnly()) {
      event.preventDefault();
      event.stopPropagation();
      return;
    }
    
    if (isDisabled()) {
      event.preventDefault();
      event.stopPropagation();
      return;
    }
    
    boolean enterPressed = "keydown".equals(type) && event.getKeyCode() == KeyCodes.KEY_ENTER;

    if ("click".equals(type) || enterPressed) {
      event.stopPropagation();

      InputElement input = getInputElement(parent);
      Boolean checked = input.isChecked();

      boolean label = "LABEL".equals(target.getTagName());

      // TODO this should be changed to remove reference to known subclass
      boolean radio = this instanceof RadioCell;

      if (label || enterPressed) {
        event.preventDefault();

        if (checked & radio) {
          return;
        }

        // input will NOT have been updated for label clicks
        checked = !checked;
        input.setChecked(checked);
      } else if (radio && value) {

        // no action required if value is already true and this is a radio
        return;
      }

      if (valueUpdater != null && checked != value) {
        valueUpdater.update(checked);
      }
    }
  }

  @Override
  public void render(com.google.gwt.cell.client.Cell.Context context, Boolean value, SafeHtmlBuilder sb) {
    CheckBoxCellOptions opts = new CheckBoxCellOptions();
    opts.setName(name);

    // radios must have a name for ie6 and ie7
    if (name == null && (GXT.isIE6() || GXT.isIE7())) {
      name = XDOM.getUniqueId();
    }

    opts.setReadonly(isReadOnly());
    opts.setDisabled(isDisabled());
    opts.setBoxLabel(getBoxLabel());

    appearance.render(sb, value == null ? false : value, opts);
  }

  /**
   * The text that appears beside the checkbox (defaults to null).
   * 
   * @param boxLabel the box label
   */
  public void setBoxLabel(XElement parent, String boxLabel) {
    this.boxLabel = boxLabel;
    appearance.setBoxLabel(boxLabel, parent);
  }

  @Override
  protected void onEnterKeyDown(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value,
      NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
    // intentionally not calling super as we handle enter key
  }

}
