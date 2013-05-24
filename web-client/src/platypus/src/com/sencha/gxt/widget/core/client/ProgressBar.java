/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.widget.core.client.cell.CellComponent;

/**
 * An manual mode updateable progress bar widget.
 * 
 * <p />
 * You are responsible for showing, updating (via {@link #updateProgress}) and
 * clearing the progress bar as needed from your own code. This method is most
 * appropriate when you want to show progress throughout an operation that has
 * predictable points of interest at which you can update the control.
 */
public class ProgressBar extends CellComponent<Double> {

  /**
   * Creates a new progress bar with the default progress bar cell.
   */
  public ProgressBar() {
    this(new ProgressBarCell());
  }

  /**
   * Creates a new progress bar with the specified progress bar cell.
   * 
   * @param cell the progress bar cell
   */
  public ProgressBar(ProgressBarCell cell) {
    super(cell);
    setWidth(300);
  }

  /**
   * Returns the bar's increment value.
   * 
   * @return the increment the increment
   */
  public int getIncrement() {
    return getCell().getIncrement();
  }

  /**
   * Resets the progress bar value to 0 and text to empty string.
   */
  public void reset() {
    setValue(0d, false, false);
    getCell().setProgressText("");
    getCell().reset(createContext(), getElement());
  }

  /**
   * The number of progress update segments to display within the progress bar
   * (defaults to 10). If the bar reaches the end and is still updating, it will
   * automatically wrap back to the beginning.
   * 
   * @param increment the new increment
   */
  public void setIncrement(int increment) {
    getCell().setIncrement(increment);
  }

  /**
   * Updates the progress bar value, and optionally its text. Any instances of
   * {0} in the given text will be substituted with the progress bar's value (0
   * - 100).If the text argument is not specified, any existing text value will
   * be unchanged. To blank out existing text, pass "". Note that even if the
   * progress bar value exceeds 1, it will never automatically reset -- you are
   * responsible for determining when the progress is complete and calling
   * {@link #reset} to clear and/or hide the control.
   * 
   * @param value A value between 0 and 1 (e.g., .5, defaults to 0)
   * @param text the string to display in the progress text element or null
   */
  public void updateProgress(double value, String text) {
    getCell().setProgressText(text);
    setValue(value, true, true);
  }

  @Override
  public ProgressBarCell getCell() {
    return (ProgressBarCell)super.getCell();
  }

  /**
   * Updates the progress bar text.
   * 
   * @param text the text to display
   */
  public void updateText(String text) {
    getCell().setProgressText(text);
    redraw(true);
  }

}
