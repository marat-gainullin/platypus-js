/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.path;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.core.client.util.PrecisePoint;

/**
 * A {@link PathCommand} that represents a vertical line.
 */
public class LineToVertical extends PathCommand {

  private double y = 0;
  private String absoluteName = "V";
  private String relativeName = "v";

  /**
   * Creates a vertical line {@link PathCommand}. Defaults to absolute.
   */
  public LineToVertical() {
  }

  /**
   * Creates a vertical line {@link PathCommand} using the given coordinates.
   * Defaults to absolute.
   * 
   * @param y the y-coordinate of the end point of the line
   */
  public LineToVertical(double y) {
    this.y = y;
  }

  /**
   * Creates a vertical line {@link PathCommand} using the given coordinates.
   * 
   * @param y the y-coordinate of the end point of the line
   * @param relative true if the command is relative
   */
  public LineToVertical(double y, boolean relative) {
    super(relative);
    this.y = y;
  }

  /**
   * Creates a copy of the given vertical line {@link PathCommand}.
   * 
   * @param vertical the command to be copied
   */
  public LineToVertical(LineToVertical vertical) {
    super(vertical);
    this.y = vertical.y;
  }

  @Override
  public LineToVertical copy() {
    return new LineToVertical(this);
  }

  /**
   * Returns the y-coordinate of the end point of the command.
   * 
   * @return the y-coordinate of the end point of the command
   */
  public double getY() {
    return y;
  }

  @Override
  public boolean nearEqual(PathCommand command) {
    if (!(command instanceof LineToVertical)) {
      return false;
    }
    LineToVertical vert = (LineToVertical) command;
    if (Math.round(this.getY()) != Math.round(vert.getY())) {
      return false;
    }

    return true;
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
      setY(getY() + currentPoint.getY());
    }
    currentPoint.setY(getY());
  }

  @Override
  public List<PathCommand> toCurve(PrecisePoint currentPoint, PrecisePoint movePoint, PrecisePoint curvePoint,
      PrecisePoint quadraticPoint) {
    quadraticPoint.setX(currentPoint.getX());
    quadraticPoint.setY(currentPoint.getY());
    List<PathCommand> commands = new ArrayList<PathCommand>();
    commands.add(new CurveTo(currentPoint.getX(), currentPoint.getY(), currentPoint.getX(), this.getY(),
        currentPoint.getX(), this.getY()));
    return commands;
  }

  @Override
  public String toString() {
    StringBuilder build = new StringBuilder();
    if (!relative) {
      build.append(absoluteName);
    } else {
      build.append(relativeName);
    }
    build.append(y);
    return build.toString();
  }

}
