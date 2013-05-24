/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.client.editor;

import java.util.List;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.sencha.gxt.data.shared.ListStore;

/**
 * Binds a {@link ListStore} to a {@link List} property in the edited model object. When flushed,
 * the list property will contain the current visible items in the store and all of the changes
 * made to the items.
 * <p>
 * If bound to a null value, no changes will be made when flushed.
 * </p>
 * <p>
 * This will not collect subeditor paths, as the Store and whatever it is bound to are not treated
 * like sub-editors.
 * </p>
 * 
 * 
 * 
 * <pre><code>
public interface MyData {
  List&lt;Person> getPeople();
}
public class MyDataEditor implements Editor&lt;MyData>{
  private ListStore&lt;Person> peopleStore = new ListStore&lt;Person>(...);
  private Grid&lt;Person> peopleGrid;
  
  ListStoreEditor&lt;Person> people;//declare a sub editor
  
  public MyDataEditor() {
    peopleStore = new ListStore&lt;Person>(...);         //create the real store
    people = new ListStoreEditor&lt;Person>(peopleStore);//map to it with an editor
    
    grid = new Grid&lt;Person>(peopleStore, ...);
  }
}
</code></pre>
 * 
 * @param <T> the model type in the list and the store
 */
public class ListStoreEditor<T> implements ValueAwareEditor<List<T>> {
  private final ListStore<T> store;
  private List<T> model;

  /**
   * Creates an editor use with a {@link ListStore}.
   * 
   * @param store the list store that uses this editor
   */
  public ListStoreEditor(ListStore<T> store) {
    this.store = store;
  }

  @Override
  public void flush() {
    // make any modifications to the models themselves
    store.commitChanges();

    // flush out the contents of the list, so structural changes are made as
    // well
    if (model != null) {
      model.clear();
      model.addAll(store.getAll());
    } // TODO add an else here to create a list?
  }

  /**
   * Returns the list store that uses this editor.
   * 
   * @return the list store that uses this editor.
   */
  public ListStore<T> getStore() {
    return store;
  }

  @Override
  public void onPropertyChange(String... paths) {
    // as ListEditor, noop
  }

  @Override
  public void setDelegate(EditorDelegate<List<T>> delegate) {
    // ignore for now, this could be used to pass errors into the view
  }

  @Override
  public void setValue(List<T> value) {
    // replace the data in the store (maybe use a loader for this?)
    store.clear();
    if (value != null && value.size() > 0) {
      store.addAll(value);
    }

    // store a copy of the original list, so changes can be pushed to that
    this.model = value;
  }
}
