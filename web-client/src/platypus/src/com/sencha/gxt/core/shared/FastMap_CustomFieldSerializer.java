/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.core.java.util.Map_CustomFieldSerializerBase;

public class FastMap_CustomFieldSerializer {

  @SuppressWarnings("rawtypes")
  public static void deserialize(SerializationStreamReader streamReader, FastMap instance)
      throws SerializationException {
    Map_CustomFieldSerializerBase.deserialize(streamReader, instance);
  }

  @SuppressWarnings("rawtypes")
  public static void serialize(SerializationStreamWriter streamWriter, FastMap instance) throws SerializationException {
    Map_CustomFieldSerializerBase.serialize(streamWriter, instance);
  }
}
