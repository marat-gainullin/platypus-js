/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.button;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasHTML;
import com.sencha.gxt.cell.core.client.ButtonCell;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonArrowAlign;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonScale;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.HasIcon;
import com.sencha.gxt.widget.core.client.cell.CellComponent;
import com.sencha.gxt.widget.core.client.cell.DefaultHandlerManagerContext;
import com.sencha.gxt.widget.core.client.event.ArrowSelectEvent;
import com.sencha.gxt.widget.core.client.event.ArrowSelectEvent.ArrowSelectHandler;
import com.sencha.gxt.widget.core.client.event.ArrowSelectEvent.HasArrowSelectHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent.BeforeSelectHandler;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent.HasBeforeSelectHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.HasSelectHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

public class CellButtonBase<C> extends CellComponent<C> implements HasHTML, HasIcon, HasBeforeSelectHandlers,
    HasSelectHandlers, HasArrowSelectHandlers, HasSafeHtml {

  protected ButtonCell<C> cell;

  public CellButtonBase() {
    this(new ButtonCell<C>());
  }

  public CellButtonBase(ButtonCell<C> cell) {
    this(cell, null);
  }

  public CellButtonBase(ButtonCell<C> cell, C initialValue) {
    super(cell, initialValue, null, true);
    this.cell = cell;
    
    setAllowTextSelection(false);

    sinkEvents(Event.ONCLICK);
  }

  @Override
  public HandlerRegistration addArrowSelectHandler(ArrowSelectHandler handler) {
    return addHandler(handler, ArrowSelectEvent.getType());
  }

  @Override
  public HandlerRegistration addBeforeSelectHandler(BeforeSelectHandler handler) {
    return addHandler(handler, BeforeSelectEvent.getType());
  }

  @Override
  public HandlerRegistration addSelectHandler(SelectHandler handler) {
    return addHandler(handler, SelectEvent.getType());
  }

  /**
   * Returns the button's arrow alignment.
   * 
   * @return the arrow alignment
   */
  public ButtonArrowAlign getArrowAlign() {
    return cell.getArrowAlign();
  }

  @Override
  public String getHTML() {
    return cell.getHTML();
  }

  @Override
  public ImageResource getIcon() {
    return cell.getIcon();
  }

  /**
   * Returns the button's icon alignment.
   *
   * @return the icon alignment
   */
  public IconAlign getIconAlign() {
    return cell.getIconAlign();
  }

  /**
   * Returns the button's menu (if it has one).
   *
   * @return the menu
   */
  public Menu getMenu() {
    return cell.getMenu();
  }

  /**
   * Returns the button's menu alignment.
   *
   * @return the menu alignment
   */
  public AnchorAlignment getMenuAlign() {
    return cell.getMenuAlign();
  }

  /**
   * Returns the button's minimum width.
   *
   * @return the minWidth the minimum width
   */
  public int getMinWidth() {
    return cell.getMinWidth();
  }

  /**
   * Returns false if mouse over effect is disabled.
   *
   * @return false if mouse effects disabled
   */
  public boolean getMouseEvents() {
    return cell.getMouseEvents();
  }

  /**
   * Returns the button's scale.
   * 
   * @return the button scale
   */
  public ButtonScale getScale() {
    return cell.getScale();
  }

  @Override
  public String getText() {
    return cell.getText();
  }

  /**
   * Hide this button's menu (if it has one).
   */
  public void hideMenu() {
    cell.hideMenu();
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    switch (event.getTypeInt()) {
      case Event.ONCLICK:
        onClick(event);
        break;
    }
  }

  /**
   * Sets the arrow alignment (defaults to RIGHT).
   *
   * @param arrowAlign the arrow alignment
   */
  public void setArrowAlign(ButtonArrowAlign arrowAlign) {
    cell.setArrowAlign(arrowAlign);
    redraw();
  }

  @Override
  public void setHTML(SafeHtml html) {
    setHTML(html.asString());
  }

  @Override
  public void setHTML(String html) {
    cell.setHTML(html);
    redraw();
  }

  @Override
  public void setIcon(ImageResource icon) {
    cell.setIcon(icon);
    redraw();
  }

  /**
   * Sets the icon alignment (defaults to LEFT).
   *
   * @param iconAlign the icon alignment
   */
  public void setIconAlign(IconAlign iconAlign) {
    cell.setIconAlign(iconAlign);
    redraw();
  }

  /**
   * Sets the button's menu.
   *
   * @param menu the menu
   */
  @UiChild(limit = 1, tagname = "menu")
  public void setMenu(Menu menu) {
    cell.setMenu(menu);
    redraw();
  }

  /**
   * Sets the position to align the menu to, see {@link XElement#alignTo} for
   * more details (defaults to 'tl-bl?', pre-render).
   *
   * @param menuAlign the menu alignment
   */
  public void setMenuAlign(AnchorAlignment menuAlign) {
    cell.setMenuAlign(menuAlign);
    redraw();
  }

  /**
   * Sets he minimum width for this button (used to give a set of buttons a
   * common width)
   *
   * @param minWidth the minimum width
   */
  public void setMinWidth(int minWidth) {
    cell.setMinWidth(minWidth);
    redraw();
  }

  /**
   * False to disable visual cues on mouseover, mouseout and mousedown (defaults
   * to true).
   *
   * @param handleMouseEvents false to disable mouse over changes
   */
  public void setMouseEvents(boolean handleMouseEvents) {
    cell.setMouseEvents(handleMouseEvents);
  }

  /**
   * Sets the button's scale.
   * 
   * @param scale the scale
   */
  public void setScale(ButtonScale scale) {
    cell.setScale(scale);
    redraw();
  }

  @Override
  public void setText(String text) {
    cell.setText(text);
    redraw();
  }

  /**
   * Show this button's menu (if it has one).
   */
  public void showMenu() {
    cell.showMenu(getElement());
  }
  
  @Override
  protected Context createContext() {
    return new DefaultHandlerManagerContext(0, 0, getKey(getValue()), ComponentHelper.ensureHandlers(this));
  }

  protected void onClick(Event event) {

  }

}
