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
 * A {@link PathCommand} that represents a moving of the current point.
 */
public class MoveTo extends EndPointCommand {

  private String absoluteName = "M";
  private String relativeName = "m";

  /**
   * Creates a move {@link PathCommand}. Defaults to absolute.
   */
  public MoveTo() {
  }

  /**
   * Creates a move {@link PathCommand} using the given coordinates. Defaults to
   * absolute.
   * 
   * @param x the x-coordinate of the current point
   * @param y the y-coordinate of the current point
   */
  public MoveTo(double x, double y) {
    super(x, y);
  }

  /**
   * Creates a move {@link PathCommand} using the given coordinates.
   * 
   * @param x the x-coordinate of the current point
   * @param y the y-coordinate of the current point
   * @param relative true if the command is relative
   */
  public MoveTo(double x, double y, boolean relative) {
    super(x, y, relative);
  }

  /**
   * Creates a copy of the given move {@link PathCommand}.
   * 
   * @param command the command to be copied
   */
  public MoveTo(MoveTo command) {
    super(command);
  }

  @Override
  public MoveTo copy() {
    return new MoveTo(this);
  }

  @Override
  public boolean nearEqual(PathCommand command) {
    if (!(command instanceof MoveTo)) {
      return false;
    }
    MoveTo move = (MoveTo) command;
    if (Math.round(this.getX()) != Math.round(move.getX())) {
      return false;
    }
    if (Math.round(this.getY()) != Math.round(move.getY())) {
      return false;
    }
    return true;
  }

  @Override
  public void toAbsolute(PrecisePoint currentPoint, PrecisePoint movePoint) {
    movePoint.setX(x);
    movePoint.setY(y);

    super.toAbsolute(currentPoint, movePoint);
  }

  @Override
  public List<PathCommand> toCurve(PrecisePoint currentPoint, PrecisePoint movePoint, PrecisePoint curvePoint,
      PrecisePoint quadraticPoint) {
    quadraticPoint.setX(currentPoint.getX());
    quadraticPoint.setY(currentPoint.getY());
    movePoint.setX(this.getX());
    movePoint.setY(this.getY());
    List<PathCommand> commands = new ArrayList<PathCommand>();
    commands.add(copy());
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
    build.append(x).append(',').append(y);
    return build.toString();
  }

}
