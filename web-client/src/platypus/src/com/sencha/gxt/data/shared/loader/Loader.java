/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent.LoadExceptionHandler;
import com.sencha.gxt.data.shared.loader.LoaderHandler.HasLoaderHandlers;

/**
 * Abstract base class for objects that can load remote data.
 * 
 * <p />
 * The optional input object passed with the load request is logically called a
 * "load config" with the object returned by the loader called the
 * "load result".
 * 
 * <p />
 * Typically, loaders work with {@link DataProxy} and {@link DataReader} to help
 * perform the load operations. The <code>DataProxy</code> is responsible for
 * obtaining the remote data. The <code>DataReader</code> is responsible for
 * "processing" the remote data and converting the data to the expected format.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * <dd>{@link BeforeLoadEvent}</b></dd>
 * <dd>{@link LoadEvent}</b></dd>
 * <dd>{@link LoadExceptionEvent}</b></dd>
 * </dl>
 * 
 * @param <C> the type of the data used to configure
 * @param <M> the type of data being returned by the loader
 */
public class Loader<C, M> implements HasLoaderHandlers<C, M> {

  private static class WrapperProxy<M, C, T> implements DataProxy<C, M> {
    private final DataProxy<C, T> proxy;
    private final DataReader<M, T> reader;

    public WrapperProxy(DataProxy<C, T> proxy, DataReader<M, T> reader) {
      this.proxy = proxy;
      this.reader = reader;
    }

    public void load(final C loadConfig, final Callback<M, Throwable> callback) {
      proxy.load(loadConfig, new Callback<T, Throwable>() {
        @Override
        public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }

        public void onSuccess(T result) {
          if (GWT.isProdMode()) {
            callback.onSuccess(reader.read(loadConfig, result));
          } else {
            try {
              callback.onSuccess(reader.read(loadConfig, result));
            } catch (ClassCastException ex) {
              GWT.log("Improper cast in " + reader.getClass()
                  + ", cannot convert the incoming data to the correct return type. "
                  + "Please provide an implementation of createReturnData()", ex);
              throw ex;
            }
          }
        }
      });
    }
  }

  private final DataProxy<C, M> proxy;
  private C lastLoadConfig;
  private boolean reuseLoadConfig;

  private SimpleEventBus eventBus;

  /**
   * Creates a new base loader instance. Since a data reader is not used, the
   * data returned by the data proxy will not be read and converted by a reader.
   * 
   * @param proxy the data proxy
   */
  public Loader(DataProxy<C, M> proxy) {
    this.proxy = proxy;
  }

  /**
   * Creates a new loader with the given proxy and reader.
   * 
   * @param proxy the data proxy
   * @param reader the data reader
   */
  public <T> Loader(DataProxy<C, T> proxy, DataReader<M, T> reader) {
    this(new WrapperProxy<M, C, T>(proxy, reader));
  }

  @Override
  public HandlerRegistration addBeforeLoadHandler(BeforeLoadHandler<C> handler) {
    return ensureHandler().addHandler(BeforeLoadEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addLoaderHandler(LoaderHandler<C, M> handler) {
    GroupingHandlerRegistration group = new GroupingHandlerRegistration();
    group.add(ensureHandler().addHandler(BeforeLoadEvent.getType(), handler));
    group.add(ensureHandler().addHandler(LoadEvent.getType(), handler));
    group.add(ensureHandler().addHandler(LoadExceptionEvent.getType(), handler));
    return group;
  }

  @Override
  public HandlerRegistration addLoadExceptionHandler(LoadExceptionHandler<C> handler) {
    return ensureHandler().addHandler(LoadExceptionEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addLoadHandler(LoadHandler<C, M> handler) {
    return ensureHandler().addHandler(LoadEvent.getType(), handler);
  }

  /**
   * Returns the last load config.
   * 
   * @return the last load config
   */
  public C getLastLoadConfig() {
    return lastLoadConfig;
  }

  /**
   * Returns the loader's data proxy.
   * 
   * @return the data proxy
   */
  public DataProxy<?, ?> getProxy() {
    return proxy;
  }

  /**
   * Returns true if the load config is being reused.
   * 
   * @return the reuse load config state
   */
  public boolean isReuseLoadConfig() {
    return reuseLoadConfig;
  }

  /**
   * Loads the data using the current configuration. Fires the
   * {@link BeforeLoadEvent} before the request, then the {@link LoadEvent}
   * after the load operation.
   * 
   * @return true if the load was requested, false if cancelled
   */
  public boolean load() {
    C config = (reuseLoadConfig && lastLoadConfig != null) ? lastLoadConfig : newLoadConfig();
    config = prepareLoadConfig(config);
    return load(config);
  }

  /**
   * Loads the data using the given load configuration. Fires the
   * {@link BeforeLoadEvent} before the request, then the {@link LoadEvent}
   * after the load operation. The current load configuration object can be
   * retrieved using {@link #getLastLoadConfig()}.
   * 
   * @param loadConfig the load config
   * @return true if the load was requested, false if cancelled
   */
  public boolean load(C loadConfig) {
    if (fireEvent(new BeforeLoadEvent<C>(loadConfig))) {
      lastLoadConfig = loadConfig;
      loadData(loadConfig);
      return true;
    }
    return false;
  }

  /**
   * Sets whether the same load config instance should be used for load
   * operations (defaults to false).
   * 
   * @param reuseLoadConfig true to reuse
   */
  public void setReuseLoadConfig(boolean reuseLoadConfig) {
    this.reuseLoadConfig = reuseLoadConfig;
  }

  /**
   * Fires the given event.
   * 
   * @param event the event to fire
   * @return true if the event was not cancelled, false if the event was
   *         cancelled
   */
  protected boolean fireEvent(GwtEvent<?> event) {
    ensureHandler().fireEvent(event);
    if (event instanceof CancellableEvent) {
      return !((CancellableEvent) event).isCancelled();
    }
    return true;
  }

  /**
   * Load data, delegating to {@link DataProxy} if configured, or forwarding to
   * {@link #loadData(Object)} if not.
   * 
   * @param config the load config
   */
  protected void loadData(final C config) {
    Callback<M, Throwable> callback = new Callback<M, Throwable>() {
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        onLoadFailure(config, caught);
      }

      public void onSuccess(M result) {
        onLoadSuccess(config, result);
      }
    };
    if (proxy == null) {
      loadData(config, callback);
      return;
    }
    proxy.load(config, callback);
  }

  /**
   * Called by {@link #loadData(Object)} when a data proxy is not being used.
   * 
   * @param config the load config
   * @param callback the callback
   */
  protected void loadData(C config, Callback<M, Throwable> callback) {

  }

  /**
   * Template method to allow custom BaseLoader subclasses to provide their own
   * implementation of LoadConfig. This implementation return null.
   */
  protected C newLoadConfig() {
    return null;
  }

  /**
   * Called when a load operation fails.
   * 
   * @param loadConfig the load config
   * @param t the exception
   */
  protected void onLoadFailure(C loadConfig, Throwable t) {
    fireEvent(new LoadExceptionEvent<C>(loadConfig, t));
  }

  /**
   * Called when the remote data has been received.
   * 
   * @param loadConfig the load config
   * @param data data
   */
  protected void onLoadSuccess(C loadConfig, M data) {
    fireEvent(new LoadEvent<C, M>(loadConfig, data));
  }

  /**
   * Template method to allow custom subclasses to prepare the load config prior
   * to loading data
   */
  protected C prepareLoadConfig(C config) {
    return config;
  }

  /**
   * Sets the most recent load config.
   * 
   * @param lastLoadConfig the most recent load config to set
   */
  protected void setLastLoadConfig(C lastLoadConfig) {
    this.lastLoadConfig = lastLoadConfig;
  }

  SimpleEventBus ensureHandler() {
    return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
  }

}
