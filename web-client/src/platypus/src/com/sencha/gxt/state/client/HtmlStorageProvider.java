/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.state.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.storage.client.Storage;

/**
 * Simple HTML5 Storage implementation of the state provider.
 */
public class HtmlStorageProvider extends Provider {
  private final Storage storage;

  public HtmlStorageProvider(Storage storage) {
    this.storage = storage;
  }

  @Override
  public void clear(String name) {
    storage.removeItem(name);
  }

  @Override
  public void getValue(String name, Callback<String, Throwable> callback) {
    callback.onSuccess(storage.getItem(name));
  }

  @Override
  public void setValue(String name, String value) {
    storage.setItem(name, value);
  }

}
