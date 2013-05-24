/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent.BeforeCheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent.HasBeforeCheckChangeHandlers;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.HasCheckChangeHandlers;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;

/**
 * Adds a menu item that contains a checkbox by default, but can also be part of
 * a radio group.
 */
public class CheckMenuItem extends MenuItem implements HasBeforeCheckChangeHandlers<CheckMenuItem>,
    HasCheckChangeHandlers<CheckMenuItem> {

  public interface CheckMenuItemAppearance extends MenuItemAppearance {

    /**
     * @param parent The element representing the check menu item
     * @param state Whether to apply or remove the style
     */
    void applyChecked(XElement parent, boolean state);
    
    ImageResource checked();
    
    ImageResource unchecked();
    
    ImageResource radio();

  }

  private final CheckMenuItemAppearance appearance;
  private boolean checked;
  private String group, groupTitle;

  /**
   * Creates a new check menu item.
   * 
   */
  public CheckMenuItem() {
    this(GWT.<CheckMenuItemAppearance> create(CheckMenuItemAppearance.class));
  }

  public CheckMenuItem(CheckMenuItemAppearance appearance) {
    super(appearance);
    this.appearance = appearance;

    hideOnClick = true;
    canActivate = true;

    // force render the checkbox
    setChecked(false);
  }

  /**
   * Creates a new check menu item.
   * 
   * @param text the text
   */
  public CheckMenuItem(String text) {
    this();
    setText(text);

    // force render
    // TODO appearance pattern not properly applied
    // check images should come from appearance
    setChecked(false);
  }

  @Override
  public HandlerRegistration addBeforeCheckChangeHandler(BeforeCheckChangeHandler<CheckMenuItem> handler) {
    return addHandler(handler, BeforeCheckChangeEvent.getType());
  }

  @Override
  public HandlerRegistration addCheckChangeHandler(CheckChangeHandler<CheckMenuItem> handler) {
    return addHandler(handler, CheckChangeEvent.getType());
  }

  /**
   * Returns the ARIA group title.
   * 
   * @return the group title
   */
  public String getAriaGroupTitle() {
    return groupTitle;
  }

  /**
   * Returns the group name.
   * 
   * @return the name
   */
  public String getGroup() {
    return group;
  }

  /**
   * Returns true if the item is checked.
   * 
   * @return the checked state
   */
  public boolean isChecked() {
    return checked;
  }

  /**
   * Sets the title attribute on the group container element. Only applies to
   * radio check items when ARIA is enabled.
   * 
   * @param title the title
   */
  public void setAriaGroupTitle(String title) {
    this.groupTitle = title;
  }

  /**
   * Set the checked state of this item.
   * 
   * @param checked the new checked state
   */
  public void setChecked(boolean checked) {
    setChecked(checked, false);
  }

  /**
   * Set the checked state of this item.
   * 
   * @param state the new checked state
   * @param suppressEvent true to prevent the CheckChange event from firing
   */
  public void setChecked(boolean state, boolean suppressEvent) {
    if (suppressEvent || fireCancellableEvent(new BeforeCheckChangeEvent<CheckMenuItem>(this, state ? CheckState.CHECKED : CheckState.UNCHECKED))) {
      if (getGroup() == null) {
        setIcon(state ? appearance.checked() : appearance.unchecked());
        appearance.applyChecked(getElement(), state);
      } else {
        setIcon(state ? appearance.radio() : null);
      }
      checked = state;

      if (!suppressEvent) {
        fireEvent(new CheckChangeEvent<CheckMenuItem>(this, state ? CheckState.CHECKED : CheckState.UNCHECKED));
      }
    }
  }

  /**
   * All check items with the same group name will automatically be grouped into
   * a single-select radio button group (defaults to null).
   * 
   * @param group the group
   */
  public void setGroup(String group) {
    this.group = group;
    setChecked(checked, true);
  }

  protected void onClick(NativeEvent ce) {
    if (!disabled && getGroup() == null) {
      setChecked(!checked);
    }
    if (!disabled && !checked && getGroup() != null) {
      setChecked(!checked);
      onRadioClick(ce);
    }
    super.onClick(ce);
  }

  protected void onRadioClick(NativeEvent ce) {
    if (getParent() instanceof HasWidgets) {
      for (Widget w : ((HasWidgets) getParent())) {
        if (w instanceof CheckMenuItem) {
          CheckMenuItem check = (CheckMenuItem) w;
          if (check != this && check.isChecked() && Util.equalWithNull(group, check.getGroup())) {
            check.setChecked(false);
          }
        }
      }
    }
  }

}
