/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.client.loader;

import com.google.gwt.core.client.Callback;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.writer.DataWriter;

/**
 * A {@link DataProxy} that retrieves data using a {@link RequestBuilder}
 * instance.
 * 
 * <p />
 * When using a load config object that implements <code>LoadConfig</code> or
 * <code>ModelData</code>, all properties and property values will be sent as
 * request parameters in the load request.
 * 
 * @param <C> the type of data used to configure the load from the proxy
 * 
 * @see RequestBuilder
 * @see ScriptTagProxy
 */
public class HttpProxy<C> implements DataProxy<C, String> {

  /**
   * The request builder used by this data proxy.
   */
  protected RequestBuilder builder;

  /**
   * The URL of the HTTP service, initialized from
   * {@link RequestBuilder#getUrl()} and used by {@link #load} as the base part
   * of the URL when appending encoded parameters for an HTTP GET request.
   */
  protected String initUrl;
  private DataWriter<C, String> writer;

  /**
   * Creates a new HttpProxy.
   * 
   * @param builder the request builder. The URL must be set to the URL of the
   *          service. For requests that use the HTTP GET method, the URL should
   *          be set to the base part of the URL. It will be updated
   *          automatically to include the encoded request.
   */
  public HttpProxy(RequestBuilder builder) {
    this.builder = builder;
    this.initUrl = builder.getUrl();
  }

  /**
   * Returns the data writer for this proxy. The data writer is responsible for
   * encoding the load config.
   * 
   * @return the data writer
   */
  public DataWriter<C, String> getWriter() {
    return writer;
  }

  @Override
  public void load(final C loadConfig, final Callback<String, Throwable> callback) {
    try {
      String data = null;
      if (shouldUseBody()) {
        data = generateUrl(loadConfig);
      } else {
        StringBuilder url = new StringBuilder(initUrl);
        url.append(url.indexOf("?") == -1 ? "?" : "&");
        String params = generateUrl(loadConfig);
        url.append(params);
        setUrl(builder, url.toString());
      }

      builder.sendRequest(data, new RequestCallback() {

        @Override
        public void onError(Request request, Throwable exception) {
          callback.onFailure(exception);
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
          if (response.getStatusCode() != Response.SC_OK) {
            callback.onFailure(new RuntimeException("HttpProxy: Invalid status code " + response.getStatusCode()));
            return;
          }
          callback.onSuccess(response.getText());

        }
      });
    } catch (Exception e) {
      callback.onFailure(e);
    }
  }

  /**
   * Decides if the config data should be written to the body or to the querystring. Defaults to
   * using the body in the case of {@code POST} or {@code PUT} requests, and the querystring for
   * all other requests. Can be overridden to change this behavior.
   * 
   * @return true if the outgoing request should write the config data to the body instead of using
   * writing to the querystring
   */
  protected boolean shouldUseBody() {
    return builder.getHTTPMethod().equals("POST") || builder.getHTTPMethod().equals("PUT");
  }

  /**
   * Sets the data writer for this proxy. The data writer is responsible for
   * encoding the load config.
   * 
   * @param writer the data writer
   */
  public void setWriter(DataWriter<C, String> writer) {
    this.writer = writer;
  }

  /**
   * Encodes the load config into a format that can be used for a GET query
   * string or a POST url-encoded body. Use {@link #setWriter} to set the data
   * writer responsible for encoding the load config. If it is not set the load
   * config is assumed to require no further encoding and its value is retrieved
   * using its <code>toString</code> method.
   * 
   * @param loadConfig the load config to encode
   * @return the encoded load config
   */
  protected String generateUrl(C loadConfig) {
    if (writer != null) {
      return writer.write(loadConfig);
    } else {
      if (loadConfig == null) {
        return "";
      }
      return loadConfig.toString();
    }
  }

  private native void setUrl(RequestBuilder rb, String url) /*-{
		rb.@com.google.gwt.http.client.RequestBuilder::url = url;
  }-*/;

}
