/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.List;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * A combo box that creates and manages a {@link ListStore} of {@code <T>}
 * instances. Values are added to the list store using {@link #add} and removed
 * from the list store using {@link #remove(Object)}.
 * <p/>
 * If the selection list is already in a list store for some other purpose, you
 * may find it easier to use {@link ComboBox} directly.
 * 
 * @param <T> the combo box type
 */
public class SimpleComboBox<T> extends ComboBox<T> {

  /**
   * Creates an empty combo box in preparation for values to be added to the
   * selection list using {@link #add}.
   * 
   * @param labelProvider the label provider that implements the interface to
   *          the data model associated with this combo box and is responsible
   *          for returning the value displayed to the user
   */
  public SimpleComboBox(LabelProvider<? super T> labelProvider) {
    super(new ListStore<T>(new ModelKeyProvider<T>() {
      @Override
      public String getKey(T item) {
        return item.toString();
      }
    }), labelProvider);
  }

  /**
   * Adds the values to the list of items displayed in the drop down.
   * 
   * @param values the values to add
   */
  public void add(List<T> values) {
    getStore().addAll(values);
  }

  /**
   * Adds the value to the list of items displayed in the drop down.
   * 
   * @param value the value to add
   */
  public void add(T value) {
    getStore().add(value);
  }

  /**
   * Returns the selected index.
   * 
   * @return the index or -1 if no selection
   */
  public int getSelectedIndex() {
    T c = getValue();
    if (c != null) {
      return getStore().indexOf(c);
    }
    return -1;
  }

  /**
   * Removes the item from the list of items displayed in the drop down.
   * 
   * @param remove the value to remove
   */
  public void remove(T remove) {
    getStore().remove(remove);
  }

}
