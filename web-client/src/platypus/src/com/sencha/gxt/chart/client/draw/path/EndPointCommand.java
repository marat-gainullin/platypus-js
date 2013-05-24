/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.path;

import com.sencha.gxt.core.client.util.PrecisePoint;

/**
 * Abstract class representing a {@link PathCommand} with an endpoint.
 */
public abstract class EndPointCommand extends PathCommand {

  protected double x = 0;
  protected double y = 0;

  /**
   * Creates an end point command with default values.
   */
  public EndPointCommand() {
  }

  /**
   * Creates an end point command with the given end point coordinates. Defaults
   * to absolute.
   * 
   * @param x ending x-coordinate
   * @param y ending y-coordinate
   */
  public EndPointCommand(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Creates an end point command with the given end point coordinates and
   * whether or not the command is relative.
   * 
   * @param x ending x-coordinate
   * @param y ending y-coordinate
   * @param relative whether or not the command is relative
   */
  public EndPointCommand(double x, double y, boolean relative) {
    super(relative);
    this.x = x;
    this.y = y;
  }

  /**
   * Creates a copy of the given end point command.
   * 
   * @param command the command to be copied
   */
  public EndPointCommand(EndPointCommand command) {
    super(command);
    this.x = command.x;
    this.y = command.y;
  }

  /**
   * Returns the x-coordinate of the end point of the command.
   * 
   * @return the x-coordinate of the end point of the command
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y-coordinate of the end point of the command.
   * 
   * @return the y-coordinate of the end point of the command
   */
  public double getY() {
    return y;
  }

  /**
   * Sets the x-coordinate of the end point of the command.
   * 
   * @param x the x-coordinate of the end point of the command
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Sets the y-coordinate of the end point of the command.
   * 
   * @param y the y-coordinate of the end point of the command
   */
  public void setY(double y) {
    this.y = y;
  }

  @Override
  public void toAbsolute(PrecisePoint currentPoint, PrecisePoint movePoint) {
    if (isRelative()) {
      super.toAbsolute(currentPoint, movePoint);
      setX(getX() + currentPoint.getX());
      setY(getY() + currentPoint.getY());
    }

    currentPoint.setX(getX());
    currentPoint.setY(getY());
  }

}
