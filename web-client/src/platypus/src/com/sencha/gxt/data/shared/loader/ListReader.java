/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import java.util.List;

/**
 * Simple reader to turn {@link List}s into {@link ListLoadResult}.
 * 
 * @param <M> the model data type
 */
public class ListReader<M> implements DataReader<ListLoadResult<M>, List<M>> {

  @Override
  public ListLoadResult<M> read(Object loadConfig, List<M> data) {
    return new ListLoadResultBean<M>(data);
  }

}
