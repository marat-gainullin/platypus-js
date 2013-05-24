/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.Collapsible;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent.BeforeCollapseHandler;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent.HasBeforeCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent.BeforeExpandHandler;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent.HasBeforeExpandHandlers;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.HasCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.HasExpandHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A simple container that wraps its content in a HTML fieldset. FieldSet
 * support collapsing which can be enabled using
 * {@link #setCollapsible(boolean)}.
 */
public class FieldSet extends SimpleContainer implements HasBeforeExpandHandlers, HasExpandHandlers,
    HasBeforeCollapseHandlers, HasCollapseHandlers, Collapsible {

  public interface FieldSetAppearance {
    XElement getContainerTarget(XElement parent);

    XElement getTextElement(XElement parent);

    XElement getToolElement(XElement parent);

    void onCollapse(XElement parent, boolean collapse);

    void render(SafeHtmlBuilder sb);
  }

  private final FieldSetAppearance appearance;
  private boolean collapsed, collapsible;
  private ToolButton collapseButton;

  /**
   * Creates a new field set.
   */
  public FieldSet() {
    this(GWT.<FieldSetAppearance> create(FieldSetAppearance.class));
  }

  /**
   * Creates a new field set.
   * 
   * @param appearance the field set appearance
   */
  public FieldSet(FieldSetAppearance appearance) {
    super(true);

    this.appearance = appearance;

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    this.appearance.render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));
  }

  @Override
  public HandlerRegistration addBeforeCollapseHandler(BeforeCollapseHandler handler) {
    return addHandler(handler, BeforeCollapseEvent.getType());
  }

  @Override
  public HandlerRegistration addBeforeExpandHandler(BeforeExpandHandler handler) {
    return addHandler(handler, BeforeExpandEvent.getType());
  }

  @Override
  public HandlerRegistration addCollapseHandler(CollapseHandler handler) {
    return addHandler(handler, CollapseEvent.getType());
  }

  @Override
  public HandlerRegistration addExpandHandler(ExpandHandler handler) {
    return addHandler(handler, ExpandEvent.getType());
  }

  @Override
  public void collapse() {
    assert collapsible : "Calling collapse on non-collapsible field set";
    if (collapsible) {
      if (fireCancellableEvent(new BeforeCollapseEvent())) {
        this.collapsed = true;
        appearance.onCollapse(getElement(), true);
        getCollapseButton().changeStyle(ToolButton.DOWN);
        fireEvent(new CollapseEvent());
      }
    }
  }

  @Override
  public void expand() {
    assert collapsible : "Calling expand on non-collapsible field set";
    if (collapsible) {
      if (fireCancellableEvent(new BeforeCollapseEvent())) {
        this.collapsed = false;
        appearance.onCollapse(getElement(), false);
        getCollapseButton().changeStyle(ToolButton.UP);
        fireEvent(new ExpandEvent());
      }
    }
  }

  /**
   * Returns the collapse button.
   * 
   * @return the collapse button or null if field set not collapsible
   */
  public ToolButton getCollapseButton() {
    if (collapseButton == null) {
      collapseButton = new ToolButton(ToolButton.UP);
      collapseButton.addSelectHandler(new SelectHandler() {

        @Override
        public void onSelect(SelectEvent event) {
          setExpanded(!isExpanded());
        }
      });
      if (isAttached()) {
        ComponentHelper.doAttach(collapseButton);
      }
    }
    return collapseButton;
  }

  /**
   * Returns the heading.
   * 
   * @return the heading HTML
   */
  public String getHeadingHtml() {
    return appearance.getTextElement(getElement()).getInnerHTML();
  }

  /**
   * Returns the heading.
   * 
   * @return the heading text
   */
  public String getHeadingText() {
    return appearance.getTextElement(getElement()).getInnerText();
  }

  /**
   * Returns true if the fieldset is collapsible.
   * 
   * @return true if collapsible
   */
  public boolean isCollapsible() {
    return collapsible;
  }

  @Override
  public boolean isExpanded() {
    return !collapsed;
  }

  /**
   * Sets whether the fieldset is collapsible (defaults to false, pre-render). This method
   * only configures the field set to be collapsible and does not change the
   * expand / collapse state. Use {@link #setExpanded(boolean)},
   * {@link #expand()}, and {@link #collapse()} to expand and collapse the field
   * set.
   * 
   * @param collapsible true to enable collapsing
   */
  public void setCollapsible(boolean collapsible) {
    assertPreRender();
    this.collapsible = collapsible;
  }

  /**
   * Convenience method to expand / collapse the field set by invoking
   * {@link #expand()} or {@link #collapse()}.
   * 
   * @param expand true to expand the field set, otherwise collapse
   */
  public void setExpanded(boolean expand) {
    if (expand) {
      expand();
    } else {
      collapse();
    }
  }

  /**
   * Sets the heading.
   * 
   * @param heading the heading HTML
   */
  public void setHeadingHtml(String heading) {
    appearance.getTextElement(getElement()).setInnerHTML(heading);
  }

  /**
   * Sets the heading.
   * 
   * @param heading the heading text
   */
  public void setHeadingText(String heading) {
    appearance.getTextElement(getElement()).setInnerText(heading);
  }

  @Override
  protected Size adjustSize(Size size) {
    int width = size.getWidth();
    if (width != -1 && width > 50) {
      width -= getContainerTarget().getMargins(Side.LEFT, Side.RIGHT);
      size.setWidth(width);
    }
    return size;
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(collapseButton);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(collapseButton);
  }

  @Override
  protected XElement getContainerTarget() {
    return appearance.getContainerTarget(getElement());
  }

  @Override
  protected void notifyHide() {
    if (!collapsed) {
      super.notifyHide();
    }
  }

  @Override
  protected void notifyShow() {
    if (!collapsed) {
      super.notifyShow();
    }
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    if (collapsible) {
      // make sure we create
      getCollapseButton();
      appearance.getToolElement(getElement()).appendChild(collapseButton.getElement());
    }
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    getContainerTarget().setWidth(width - getElement().getFrameWidth(Side.LEFT, Side.RIGHT), true);
  }

}
