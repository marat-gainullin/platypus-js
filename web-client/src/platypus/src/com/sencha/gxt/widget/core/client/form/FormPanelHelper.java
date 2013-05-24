/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * Utility methods for form panels.
 */
public class FormPanelHelper {

  /**
   * Returns all of the container's child field labels. Field labels in nested
   * containers are included in the returned list.
   * 
   * @param container the container
   * @return the fields
   */
  public static List<FieldLabel> getFieldLabels(HasWidgets container) {
    List<FieldLabel> fields = new ArrayList<FieldLabel>();
    getLabels(fields, container);
    return fields;
  }

  /**
   * Returns all of the container's child fields. Fields in nested containers
   * are included in the returned list.
   * 
   * @param container the container
   * @return the fields
   */
  public static List<IsField<?>> getFields(HasWidgets container) {
    List<IsField<?>> fields = new ArrayList<IsField<?>>();
    getFields(fields, container);
    return fields;
  }

  /**
   * Returns true if the form is invalid.
   * 
   * @return true if all fields are valid
   */
  public static boolean isValid(HasWidgets container) {
    return isValid(container, false);
  }

  /**
   * Returns the form's valid state by querying all child fields.
   * 
   * @param preventMark true for silent validation (no invalid event and field
   *          is not marked invalid)
   * 
   * @return true if all fields are valid
   */
  public static boolean isValid(HasWidgets container, boolean preventMark) {
    boolean valid = true;
    for (IsField<?> f : getFields(container)) {
      if (!f.isValid(preventMark)) {
        valid = false;
      }
    }
    return valid;
  }

  /**
   * Resets all field values.
   */
  public static void reset(HasWidgets container) {
    for (IsField<?> f : getFields(container)) {
      f.reset();
    }
  }

  private static void getFields(List<IsField<?>> widgets, HasWidgets container) {
    Iterator<Widget> it = container.iterator();
    while (it.hasNext()) {
      Widget w = it.next();

      if (w instanceof IsField<?>) {
        widgets.add((IsField<?>) w);
      }

      if (w instanceof HasWidgets) {
        getFields(widgets, (HasWidgets) w);
      }
    }
  }

  private static void getLabels(List<FieldLabel> widgets, HasWidgets container) {
    Iterator<Widget> it = container.iterator();
    while (it.hasNext()) {
      Widget w = it.next();

      if (w instanceof FieldLabel) {
        widgets.add((FieldLabel) w);
        continue;
      }

      if (w instanceof HasWidgets) {
        getLabels(widgets, (HasWidgets) w);
      }
    }
  }
}
