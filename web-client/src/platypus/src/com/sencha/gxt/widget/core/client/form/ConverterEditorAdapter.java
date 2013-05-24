/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.editor.client.CompositeEditor;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.LeafValueEditor;
import com.sencha.gxt.data.shared.Converter;

/**
 * Adapter to allow an Editor to have a subeditor that acts on a {@link Converter}-modified instance
 * of the data.
 * <p>
 * As an example, consider an {@code Order} object that has a property {@code vendorId}, which holds
 * the {@Vendor id} property of a {@code Vendor} object. But to draw the field, the full 
 * {@code Vendor} must be used in a {@code ComboBox&lt;Vendor>}:
 * </p>
 * <pre><code>
public class Order {
  public String getVendorId() { ... }
}
public class Vendor {
  public String getName() { ... }
  public String getId() { ... }
}
interface VendorProperties extends ValueProvider&lt;Vendor> {
  ModelKeyProvider&lt;Vendor> id();
  LabelProvider&lt;Vendor> name();
}
public class OrderEditor implements Editor&lt;Order> {

  // Won't work as a subeditor, since we can't refer to the String vendorId as a Vendor
  //ComboBox&lt;Vendor> vendorId;
  
  // Instead, we create a way to map from String -> Vendor
  public static class VendorIdConverter implements Converter&lt;String, Vendor> {
    private final Store&ltVendor> store;
    public VendorIdConverter(Store&lt;Vendor> store) {
      this.store = store;
    }
    public String convertFieldValue(Vendor object) {
      return object.getId();
    }
    public Vendor convertModelValue(String object) {
      return store.findModelWithKey(object);
    }
  }

  // We create a ComboBox to display, but tell the editor to ignore it
  {@literal @}Ignore
  ComboBox&lt;Vendor> vendor;

  // And we wire the vendorId property to the vendor ComboBox, using the Converter
  // This is an editor, so it must be public, protected, or default
  ConverterEditorAdapter&lt;String, Vendor, ComboBox&lt;Vendor>> vendorId;

  public OrderEditor() {
    VendorProperties props = GWT.create(VendorProperties.class);

    ListStore&ltVendor> vendorList = new ListStore&lt;Vendor>(props.id());
    //possible vendors should be added to this list, might be an RPC call?
    vendorList.add(...);

    //this will make the combo box draw the vendor name - easier to read than the id
    vendor = new ComboBox&lt;Vendor>(props.name());

    //and finally, actually wire them together
    vendorId = new ConverterEditorAdapter&lt;String, Vendor, ComboBox&lt;Vendor>>
        (vendor, new VendorIdConverter(vendorList));
  }
}
</code></pre>
 *
 * @param <T> original sub-property type, referred to as the model type by the Converter
 * @param <U> type editable by E, referred to as the field type by the Converter
 * @param <E> editor type used to display, modify U
 */
public class ConverterEditorAdapter<T, U, E extends Editor<U>> implements CompositeEditor<T, U, E>, LeafValueEditor<T> {

  private EditorChain<U, E> chain;
  private T value;
  private final E editor;
  private final Converter<T, U> converter;

  /**
   * Creates a new editor adapter using the given converter to translate values to and from what
   * can be used by the given editor.
   * @param editor
   * @param converter
   */
  public ConverterEditorAdapter(E editor, Converter<T, U> converter) {
    this.editor = editor;
    this.converter = converter;
  }

  @Override
  public void flush() {
    value = getConverter().convertFieldValue(chain.getValue(editor));
  }

  @Override
  public void onPropertyChange(String... paths) {
  }

  @Override
  public void setDelegate(EditorDelegate<T> delegate) {
  }

  @Override
  public void setValue(T value) {
    chain.attach(getConverter().convertModelValue(value), editor);
  }

  @Override
  public T getValue() {
    return value;
  }

  @Override
  public E createEditorForTraversal() {
    return editor;
  }

  @Override
  public String getPathElement(E subEditor) {
    return "";
  }

  @Override
  public void setEditorChain(CompositeEditor.EditorChain<U, E> chain) {
    this.chain = chain;
  }
  
  /**
   * @return the converter instance used by this editor adapter to modify values
   */
  public Converter<T, U> getConverter() {
    return converter;
  }
}
