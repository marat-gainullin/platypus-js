/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.state.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractRpcProvider extends Provider {

  @Override
  public void getValue(String name, final Callback<String, Throwable> callback) {
    getValue(name, new AsyncCallback<String>() {
      @Override
      public void onSuccess(String result) {
        callback.onSuccess(result);
      }

      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }
    });
  }

  public abstract void getValue(String name, AsyncCallback<String> callback);

}
