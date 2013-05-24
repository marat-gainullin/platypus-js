/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.cell.core.client.SliderCell;
import com.sencha.gxt.cell.core.client.SliderCell.HorizontalSliderAppearance;
import com.sencha.gxt.cell.core.client.SliderCell.VerticalSliderAppearance;
import com.sencha.gxt.widget.core.client.form.Field;

/**
 * Lets the user select a value by sliding an indicator within a bounded range.
 */
public class Slider extends Field<Integer> {

  protected final SliderCell cell;

  /**
   * Creates a slider with the default slider cell.
   */
  public Slider() {
    super(new SliderCell());
    cell = (SliderCell) getCell();
    setAllowTextSelection(false);
    redraw();
  }

  /**
   * Creates a slider with the specified orientation.
   * 
   * @param vertical true to create a vertical slider
   */
  public Slider(boolean vertical) {
    super(new SliderCell(vertical ? GWT.<VerticalSliderAppearance> create(VerticalSliderAppearance.class)
        : GWT.<HorizontalSliderAppearance> create(HorizontalSliderAppearance.class)));
    cell = (SliderCell) getCell();
    redraw();
  }

  /**
   * Creates a slider with the specified slider cell.
   * 
   * @param cell the cell for this slider
   */
  public Slider(SliderCell cell) {
    super(cell);
    this.cell = cell;
    redraw();
  }

  /**
   * Returns the increment.
   * 
   * @return the increment
   */
  public int getIncrement() {
    return cell.getIncrement();
  }

  /**
   * Returns the max value (defaults to 100).
   * 
   * @return the max value
   */
  public int getMaxValue() {
    return cell.getMaxValue();
  }

  /**
   * Returns the tool tip message.
   * 
   * @return the tool tip message
   */
  public String getMessage() {
    return cell.getMessage();
  }

  /**
   * Returns the minimum value (defaults to 0).
   * 
   * @return the minimum value
   */
  public int getMinValue() {
    return cell.getMinValue();
  }

  /**
   * How many units to change the slider when adjusting by drag and drop. Use
   * this option to enable 'snapping' (default to 10).
   * 
   * @param increment the increment
   */
  public void setIncrement(int increment) {
    cell.setIncrement(increment);
  }

  /**
   * Sets the max value (defaults to 100).
   * 
   * @param maxValue the max value
   */
  public void setMaxValue(int maxValue) {
    cell.setMaxValue(maxValue);
  }

  /**
   * Sets the tool tip message (defaults to '{0}'). "{0} will be substituted
   * with the current slider value.
   * 
   * @param message the tool tip message
   */
  public void setMessage(String message) {
    cell.setMessage(message);
  }

  /**
   * Sets the minimum value (defaults to 0).
   * 
   * @param minValue the minimum value
   */
  public void setMinValue(int minValue) {
    cell.setMinValue(minValue);
  }

}
