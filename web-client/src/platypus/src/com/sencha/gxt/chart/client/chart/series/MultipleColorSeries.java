/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.chart.client.draw.Color;

/**
 * A series that uses a list of colors.
 * 
 * @param <M> the data type of the series
 */
public abstract class MultipleColorSeries<M> extends Series<M> {

  protected ArrayList<Color> colors = new ArrayList<Color>();

  /**
   * Adds a color to the list of colors used in the series.
   * 
   * @param color the color to add
   * @return true if successful
   */
  public boolean addColor(Color color) {
    return colors.add(color);
  }

  /**
   * Adds a color to the list of colors used in the series.
   * 
   * @param index the index to add the color
   * @param color the color to be added
   */
  public void addColor(int index, Color color) {
    colors.add(index, color);
  }

  /**
   * Returns the color at the given index.
   * 
   * @param index the index of the color
   * @return the color at the given index
   */
  public Color getColor(int index) {
    if (colors.size() > 0) {
      return colors.get(index % colors.size());
    } else {
      return null;
    }
  }

  /**
   * Returns the list of colors used by the series.
   * 
   * @return the list of colors used by the series
   */
  public List<Color> getColors() {
    return colors;
  }

  /**
   * Removes the first occurrence given color.
   * 
   * @param color the color to be removed
   * @return true if successful
   */
  public boolean removeColor(Color color) {
    return colors.remove(color);
  }

  /**
   * Removes the color at the given index.
   * 
   * @param index the index of the color
   * @return the removed color
   */
  public Color removeColor(int index) {
    return colors.remove(index);
  }

  /**
   * Sets the color at the given index.
   * 
   * @param index the index in the list
   * @param color the color to set
   * @return the previous color at the index
   */
  public Color setColor(int index, Color color) {
    return colors.set(index, color);
  }

}
