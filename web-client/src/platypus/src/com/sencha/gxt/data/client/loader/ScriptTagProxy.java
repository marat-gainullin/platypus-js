/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.client.loader;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.writer.DataWriter;

/**
 * A {@link DataProxy} that reads a data from a URL which may be in a
 * domain other than the originating domain of the running page.
 * 
 * <p />
 * Note that if you are retrieving data from a page that is in a domain that is
 * NOT the same as the originating domain of the running page, you must use this
 * class, rather than HttpProxy.
 * 
 * <p />
 * When using a load config object that implements <code>LoadConfig</code> or
 * <code>ModelData</code>, all properties and property values will be sent as
 * request parameters in the load request.
 * 
 * 
 * @see HttpProxy
 */
public class ScriptTagProxy<C> implements DataProxy<C, JavaScriptObject> {

  private static int ID = 0;
  private String url;

  private DataWriter<C, String> writer;

  /**
   * Creates a script tag proxy that reads data from a URL that may be in a
   * domain other than the originating domain of the running page.
   * 
   * @param url the URL representing the data to retrieve
   */
  public ScriptTagProxy(String url) {
    this.url = url;
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

  public void load(C loadConfig, final Callback<JavaScriptObject, Throwable> callback) {
    String transId = "transId" + ID++;
    String prepend = url.indexOf("?") != -1 ? "&" : "?";
    String u = url + prepend + generateUrl(loadConfig);

    JsonpRequestBuilder b = new JsonpRequestBuilder();
    b.setPredeterminedId(transId);// needed?
    b.requestObject(u, new AsyncCallback<JavaScriptObject>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(JavaScriptObject result) {
        callback.onSuccess(result);
      }
    });
  }

  /**
   * Sets the proxy's url.
   * 
   * @param url the url
   */
  public void setUrl(String url) {
    this.url = url;
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
   * using {@link Object#toString()}.
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
}
