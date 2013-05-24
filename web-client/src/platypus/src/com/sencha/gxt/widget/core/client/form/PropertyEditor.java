/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.io.IOException;
import java.text.ParseException;

import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;

/**
 * Abstract base class for property editors. Property editors are generally
 * responsible for string / value conversion and other operations.
 * 
 * @param <T> the field's data type
 */
public abstract class PropertyEditor<T> implements Renderer<T>, Parser<T> {

  /**
   * A default property editor that renders itself using it's
   * {@link Object#toString()} method and parses itself as itself.
   */
  public static final PropertyEditor<?> DEFAULT = new PropertyEditor<Object>() {

    @Override
    public Object parse(CharSequence text) throws ParseException {
      return text;
    }

    @Override
    public String render(Object object) {
      return object == null ? "" : object.toString();
    }
  };

  public void render(T object, Appendable appendable) throws IOException {
    appendable.append(render(object));
  }

}
