/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

import java.util.ArrayList;
import java.util.List;

/**
 * A color representing a linear gradient.
 */
public class Gradient extends Color {

  private String id = "";
  private double angle = 0;
  private List<Stop> stops = new ArrayList<Stop>();

  /**
   * Creates a gradient using the given id and angle.
   * 
   * @param id the gradient id
   * @param angle the angle of the gradient
   */
  public Gradient(String id, double angle) {
    super("url(#" + id + ")");
    this.id = id;
    this.angle = angle;
  }

  /**
   * Adds a stop to the gradient using the given offset and color.
   * 
   * @param offset the offset of the stop
   * @param color the color of the stop
   */
  public void addStop(int offset, Color color) {
    stops.add(new Stop(offset, color));
  }

  /**
   * Adds the given stop to the gradient.
   * 
   * @param stop the stop to be added
   */
  public void addStop(Stop stop) {
    stops.add(stop);
  }

  /**
   * Removes all stops from the gradient.
   */
  public void clearStops() {
    stops.clear();
  }

  /**
   * Returns the angle of the gradient.
   * 
   * @return the angle of the gradient
   */
  public double getAngle() {
    return angle;
  }

  /**
   * Returns the id of the gradient.
   * 
   * @return the id of the gradient
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the stops of the gradient.
   * 
   * @return the stops of the gradient
   */
  public List<Stop> getStops() {
    return stops;
  }

  /**
   * Sets the angle of the gradient.
   * 
   * @param angle the new angle of the gradient
   */
  public void setAngle(double angle) {
    this.angle = angle;
  }

  /**
   * Sets the id of the gradient. Updates the color string with the new id.
   * 
   * @param id the new id of the gradient
   */
  public void setId(String id) {
    this.id = id;
    setColor("url(#" + id + ")");
  }

}
