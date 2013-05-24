/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import java.util.List;

import com.sencha.gxt.widget.core.client.event.CheckChangedEvent.HasCheckChangedHandlers;

/**
 * Interface for objects that provide check state.
 * 
 * @param <M> the model type
 */
public interface CheckProvider<M> extends HasCheckChangedHandlers<M> {

  /**
   * Returns the current checked selection.
   * 
   * @return the checked selection
   */
  public List<M> getCheckedSelection();

  /**
   * Returns true if the model is checked.
   * 
   * @param model the model
   * @return the check state
   */
  public boolean isChecked(M model);

  /**
   * Sets the current checked selection.
   * 
   * @param selection the checked selection
   */
  public void setCheckedSelection(List<M> selection);

}
