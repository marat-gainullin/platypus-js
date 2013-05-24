/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form.error;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.user.client.DOM;

/**
 * A <code>FieldError</code> instance that calls setInnerHTML on a target
 * element. The target element can be specified directly or via a element id.
 */
public class ElementErrorHandler implements ErrorHandler {

  protected String elementId;
  protected Element element;

  public ElementErrorHandler(String elementId) {
    this.elementId = elementId;
  }

  public ElementErrorHandler(Element element) {
    this.element = element;
  }

  public Element getElement() {
    return element;
  }

  public void setElement(Element element) {
    this.element = element;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  @Override
  public void clearInvalid() {
    Element elem = element;
    if (elem == null) {
      elem = DOM.getElementById(elementId);
    }
    if (elem != null) {
      elem.setInnerHTML("");
    }
  }

  @Override
  public void markInvalid(List<EditorError> errors) {
    Element elem = element;
    if (elem == null) {
      elem = DOM.getElementById(elementId);
    }
    if (elem != null) {
      elem.setInnerHTML(errors.get(0).getMessage());
    }
  }

}