/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * A base class for HTML layout containers. Provides behavior common to all HTML
 * layout containers, including the attachment of of the widget element to the
 * container's parent element. For a code snippet that illustrates the use of
 * this class, see {@link HtmlLayoutContainer}.
 */
public abstract class AbstractHtmlLayoutContainer extends Container {

  /**
   * Specifies HTML layout parameters, such as the mapping of each child to a
   * corresponding selector in the HTML template. For a code snippet that
   * illustrates the use of this class, see {@link HtmlLayoutContainer}.
   */
  public static class HtmlData extends MarginData {

    private String selector;

    /**
     * Creates an HTML layout parameter with the specified selector value.
     * 
     * @param selector identifies the element in the HTML template to which the
     *          associated widget is attached. If more than one element matches
     *          the selector, the first matching element is selected.
     */
    public HtmlData(String selector) {
      this.selector = selector;
    }

    /**
     * Returns the selector that identifies the element in the HTML template to
     * which the associated widget is attached.
     * 
     * @return the selector for the widget
     */
    public String getSelector() {
      return selector;
    }

    /**
     * Sets the selector that identifies the element in the HTML template to
     * which the associated widget is attached.
     * 
     * @param selector identifies the element in the HTML template to which the
     *          associated widget is attached. If more than one element matches
     *          the selector, the first matching element is selected.
     */
    public void setSelector(String selector) {
      this.selector = selector;
    }
  }

  private SafeHtml html;

  protected AbstractHtmlLayoutContainer() {
    setElement(DOM.createDiv());
  }

  /**
   * Adds a widget to the HTML layout container with the specified layout
   * parameters.
   * 
   * @param child the widget to add to the layout container
   * @param layoutData the parameters that describe how to lay out the widget
   */
  @UiChild(tagname = "child")
  public void add(IsWidget child, HtmlData layoutData) {
    if (child != null) {
      child.asWidget().setLayoutData(layoutData);
    }
    super.add(child);
  }

  @Override
  protected void doPhysicalAttach(Widget child, int beforeIndex) {
    Object layoutData = child.getLayoutData();
    if (layoutData instanceof HtmlData) {
      String selector = ((HtmlData) layoutData).getSelector();
      XElement c = getContainerTarget().child(selector);
      if (c != null) {
        c.appendChild(child.getElement());
        return;
      }
    }
    super.doPhysicalAttach(child, beforeIndex);
  }

  protected SafeHtml getHTML() {
    return html;
  }
  
  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    if (GXT.isIE8()) {
      getElement().repaint();
    }
  }

  protected void setHTML(SafeHtml html) {
    this.html = html;
    for (Widget w : this) {
      doPhysicalDetach(w);
    }
    getContainerTarget().setInnerHTML(html == null ? "" : html.asString());

    int i = 0;
    for (Widget w : this) {
      doPhysicalAttach(w, i++);
    }
  }

}
