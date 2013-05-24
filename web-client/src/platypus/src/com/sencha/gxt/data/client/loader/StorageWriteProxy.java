/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.client.loader;

import com.google.gwt.core.client.Callback;
import com.google.gwt.storage.client.Storage;
import com.sencha.gxt.data.client.loader.StorageWriteProxy.Entry;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.writer.DataWriter;

/**
 * Proxy to push key/value string pairs into local html5 browser storage. Both
 * key and value must be populated prior to
 * 
 */
public class StorageWriteProxy<K, V> implements DataProxy<Entry<K, V>, Void> {

  /**
   * Defines a key / value pair.
   */
  public static class Entry<K, V> {
    private final K key;
    private final V value;

    public Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }
  }

  private final Storage storage;
  private DataWriter<K, String> keyWriter;
  private DataWriter<V, String> valueWriter;

  /**
   * Creates a storage write proxy that saves a key and value to HTML5 browser
   * based storage.
   * 
   * @param session true to use session storage, false to use local storage
   */
  public StorageWriteProxy(boolean session) {
    this(session ? Storage.getSessionStorageIfSupported() : Storage.getLocalStorageIfSupported());
  }

  /**
   * Creates a storage write proxy that saves a key and value to the given HTML5
   * browser based storage.
   * 
   * @param storage the browser based storage
   */
  public StorageWriteProxy(Storage storage) {
    assert storage != null : "Storage may not be null";
    this.storage = storage;
  }

  /**
   * Returns the current {@link DataWriter} used for converting key instances
   * into Strings
   * 
   * @return the current data writer that converts key instances into strings
   */
  public DataWriter<K, String> getKeyWriter() {
    return keyWriter;
  }

  /**
   * Returns the current {@link DataWriter} used for converting value instances
   * into Strings
   * 
   * @return the current data writer that converts value instances into strings
   */
  public DataWriter<V, String> getValueWriter() {
    return valueWriter;
  }

  @Override
  public void load(Entry<K, V> data, Callback<Void, Throwable> callback) {
    storage.setItem(getEncodedKey(data.getKey()), getEncodedValue(data.getValue()));
    callback.onSuccess(null);
  }

  /**
   * Sets a writer to use for converting key objects into a string to use when
   * storing values.
   * 
   * @param keyWriter
   */
  public void setKeyWriter(DataWriter<K, String> keyWriter) {
    this.keyWriter = keyWriter;
  }

  /**
   * Sets a writer to use for converting value objects into a string to be
   * stored.
   * 
   * @param valueWriter
   */
  public void setValueWriter(DataWriter<V, String> valueWriter) {
    this.valueWriter = valueWriter;
  }

  protected String getEncodedKey(K key) {
    if (keyWriter == null) {
      return key.toString();
    } else {
      return keyWriter.write(key);
    }
  }

  protected String getEncodedValue(V value) {
    if (valueWriter == null) {
      return value.toString();
    } else {
      return valueWriter.write(value);
    }
  }
}
