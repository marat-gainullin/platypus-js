/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * Marker Interface implemented by types that provide access to properties of 
 * bean-like models. Methods can be of type {@link ValueProvider},
 * {@link ModelKeyProvider}, or {@link LabelProvider}, and in all cases, the type
 * T should match the generic type for PropertyAccess&lt;T>.
 * <p>
 * Method names should map to existing model properties, except for the get- and
 * set- prefix that is used to change those properties. Much as with the GWT
 * Editor functionality, nested properties or properties with names unrelated
 * to their method may also be defined using the {@link Path} annotation.
 * </p>
 * <p>
 * {@code PropertyAccess} instances are created by invoking {@link GWT#create(Class)}.
 * </p>
 * <p>
 * In the following example, an interface is declared with several getters and setters - it doesn't
 * matter to {@code PropertyAccess} how these are implemented - and generic access to these methods
 * are generated in the {@code MyDataProperties} implementation automatically.
 * </p>
 * <pre><code>
public interface MyData {
  String getName();
  void setName(String name);
  
  String getId();
  void setId(String id);
  
  MyData getMoreData();
  void setMoreData(MyData moreData);
}
public interface MyDataProperties extends PropertyAccess&lt;MyData> {
  ValueProvider&lt;MyData, String> name();// declaring a way to read/write the name property
  
  {@literal @}Path("name")
  LabelProvider&lt;MyData> nameLabel();   // if name() didn't exist, this could be called name and would
                                       // not need {@literal @}Path("name")
  ModelKeyProvider&lt;MyData> id();
  
  {@literal @}Path("moreData.name")
  ValueProvider&lt;MyData,String> moreDataName(); // provides access to .getMoreData().getName()
}
</code></pre>
 * 
 * @param <T> the target type
 */
public interface PropertyAccess<T> {

}
