/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;

public class PropertyDisplayCell<C> extends AbstractCell<C> {
  private final PropertyEditor<C> propertyEditor;

  public PropertyDisplayCell(PropertyEditor<C> propertyEditor, Set<String> consumedEvents) {
    super(consumedEvents);
    this.propertyEditor = propertyEditor;
  }

  public PropertyDisplayCell(PropertyEditor<C> propertyEditor, String... consumedEvents) {
    super(consumedEvents);
    this.propertyEditor = propertyEditor;
  }

  public void render(Cell.Context context, C value, SafeHtmlBuilder sb) {
    sb.appendEscaped(propertyEditor.render(value));
  }
}
