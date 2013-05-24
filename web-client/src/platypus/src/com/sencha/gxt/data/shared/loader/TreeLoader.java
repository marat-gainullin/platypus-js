/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of the <code>TreeLoader</code> interface.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * <dd>{@link BeforeLoadEvent}</b></dd>
 * <dd>{@link LoadEvent}</b></dd>
 * <dd>{@link LoadExceptionEvent}</b></dd>
 * </dl>
 * 
 * 
 * @param <M> the model data type
 */
public class TreeLoader<M> extends Loader<M, List<M>> {

  private Set<M> children = new HashSet<M>();

  /**
   * Creates a new tree loader instance.
   * 
   * @param proxy the data reader
   */
  public TreeLoader(DataProxy<M, List<M>> proxy) {
    super(proxy);
  }

  /**
   * Creates a new tree loader instance.
   * 
   * @param proxy the data proxy
   * @param reader the data reader
   */
  public <T> TreeLoader(DataProxy<M, T> proxy, DataReader<List<M>, T> reader) {
    super(proxy, reader);
  }

  /**
   * Returns true if the model has children. This method allows tree based
   * components to determine if the expand icon should be displayed next to a
   * node.
   * 
   * @param parent the parent model
   * @return true if the model has children, otherwise false
   */
  public boolean hasChildren(M parent) {
    return false;
  }

  /**
   * Initiates a load request for the parent's children.
   * 
   * @param parent the parent
   * @return true if the load was requested
   */
  public boolean loadChildren(M parent) {
    if (children.contains(parent)) {
      return false;
    }
    children.add(parent);
    return load(parent);
  }

  @Override
  protected void onLoadFailure(M loadConfig, Throwable t) {
    children.remove(loadConfig);
    fireEvent(new LoadExceptionEvent<M>(loadConfig, t));
  }

  @Override
  protected void onLoadSuccess(M loadConfig, List<M> result) {
    children.remove(loadConfig);
    fireEvent(new LoadEvent<M, List<M>>(loadConfig, result));
  }

}
