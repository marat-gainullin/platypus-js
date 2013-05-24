/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

/**
 * Interface for objects that translate raw data into a given type. For example,
 * an XML string being returned from the server could be used to create model
 * instances.
 * 
 * @param <M> the return data type
 * @param <D> the incoming data type to be read
 */
public interface DataReader<M, D> {

  /**
   * Reads the raw data and returns the typed data.
   * 
   * @param loadConfig the load config information
   * @param data the data to read
   * @return the processed and / or converted data
   */
  public M read(Object loadConfig, D data);

}
