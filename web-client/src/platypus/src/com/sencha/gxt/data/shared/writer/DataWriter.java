/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.writer;

/**
 * Data writers are a simple abstraction to turn logical objects into a format
 * that can more easily be sent over the wire. In many cases, such as saving
 * user changes, this simply means turning objects back into JSON or XML, the
 * format they were loaded in from the server (effectively the reverse of
 * {@link com.sencha.gxt.data.shared.loader.DataReader}). In other cases, this
 * can mean copying properties from load configs into an object format which can
 * be sent via RPC or RequestFactory.
 * 
 * In contrast with DataReader, this is not expected to have access to any other
 * context such as a load config, it operates directly on the data as-is. Custom
 * implementations can provide other context, but none is available by default.
 * 
 * @param <M> the source type (starting data format) of the model
 * @param <D> the target type (data format to be produced), usually to send the
 *          data over the wire
 */
public interface DataWriter<M, D> {
  /**
   * Converts a source model to its equivalent target type.
   * 
   * @param model the source model
   * @return the equivalent target type
   */
  D write(M model);
}
