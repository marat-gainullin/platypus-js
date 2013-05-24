/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.sencha.gxt.cell.core.client.AutoProgressBarCell;

/**
 * An auto mode progress bar widget.
 * 
 * <p />
 * You simply call {@link #auto} and let the progress bar run indefinitely, only
 * clearing it once the operation is complete. You can optionally have the
 * progress bar wait for a specific amount of time and then clear itself.
 * Automatic mode is most appropriate for timed operations or asynchronous
 * operations in which you have no need for indicating intermediate progress.
 */
public class AutoProgressBar extends ProgressBar {

  protected final AutoProgressBarCell cell;
  
  /**
   * Creates a new progress bar with the default automatic progress bar cell.
   */
  public AutoProgressBar() {
    this(new AutoProgressBarCell());
  }

  /**
   * Creates a new progress bar with the specified automatic progress bar cell.
   * 
   * @param cell the automatic progress bar cell
   */
  public AutoProgressBar(AutoProgressBarCell cell) {
    super(cell);
    this.cell = cell;
  }

  /**
   * Initiates an auto-updating progress bar using the current duration,
   * increment, and interval.
   */
  public void auto() {
    cell.auto(createContext(), getElement());
  }

  /**
   * Returns the duration.
   * 
   * @return the duration
   */
  public int getDuration() {
    return cell.getDuration();
  }

  /**
   * Returns the bar's interval value.
   * 
   * @return the interval in milliseconds
   */
  public int getInterval() {
    return cell.getInterval();
  }

  /**
   * Returns true if the progress bar is currently in a {@link #auto} operation.
   * 
   * @return true if waiting, else false
   */
  public boolean isRunning() {
    return cell.isRunning();
  }

  /**
   * The length of time in milliseconds that the progress bar should run before
   * resetting itself (defaults to DEFAULT, in which case it will run
   * indefinitely until reset is called).
   * 
   * @param duration the duration in milliseconds
   */
  public void setDuration(int duration) {
    cell.setDuration(duration);
  }

  /**
   * Sets the length of time in milliseconds between each progress update
   * (defaults to 300 ms).
   * 
   * @param interval the interval to set
   */
  public void setInterval(int interval) {
    cell.setInterval(interval);
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    if (isRunning()) {
      reset();
    }
  }

}
