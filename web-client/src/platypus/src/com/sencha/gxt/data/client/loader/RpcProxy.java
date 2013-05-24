/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.client.loader;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.DataProxy;

/**
 * <code>DataProxy</code> implementation that retrieves data using GWT RPC.
 * 
 * @param <C> the type of data used to configure the load from the proxy
 * @param <D> the type of data being returned by the data proxy
 */
public abstract class RpcProxy<C, D> implements DataProxy<C, D> {

  /**
   * Retrieves data using GWT RPC.
   * 
   * @param loadConfig the load config describing the data to retrieve
   * @param callback the callback to invoke on success or failure
   */
  public abstract void load(C loadConfig, AsyncCallback<D> callback);

  @Override
  public final void load(C loadConfig, final Callback<D, Throwable> callback) {
    load(loadConfig, new AsyncCallback<D>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(D result) {
        callback.onSuccess(result);
      }
    });
  }
}
