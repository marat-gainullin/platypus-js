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
 * A {@link PathCommand} that represents the end of the current subpath.
 */
public class ClosePath extends PathCommand {

  private String absoluteName = "Z";
  private String relativeName = "z";

  /**
   * Creates a close {@link PathCommand}. Defaults to absolute.
   */
  public ClosePath() {
  }

  /**
   * Creates a close {@link PathCommand} with the given boolean as to whether or
   * not it is relative.
   * 
   * @param relative true if the command is relative
   */
  public ClosePath(boolean relative) {
    super(relative);
  }

  /**
   * Creates a copy of the given close {@link PathCommand}.
   * 
   * @param command the command to be copied
   */
  public ClosePath(ClosePath command) {
    super(command);
  }

  @Override
  public ClosePath copy() {
    return new ClosePath(this);
  }

  @Override
  public boolean nearEqual(PathCommand command) {
    if (!(command instanceof ClosePath)) {
      return false;
    }
    return true;
  }

  @Override
  public void toAbsolute(PrecisePoint currentPoint, PrecisePoint movePoint) {
    currentPoint.setX(movePoint.getX());
    currentPoint.setY(movePoint.getY());
  }

  @Override
  public List<PathCommand> toCurve(PrecisePoint currentPoint, PrecisePoint movePoint, PrecisePoint curvePoint,
      PrecisePoint quadraticPoint) {
    quadraticPoint.setX(currentPoint.getX());
    quadraticPoint.setY(currentPoint.getY());
    List<PathCommand> commands = new ArrayList<PathCommand>();
    commands.add(new CurveTo(currentPoint.getX(), currentPoint.getY(), movePoint.getX(), movePoint.getY(),
        movePoint.getX(), movePoint.getY()));
    return commands;
  }

  @Override
  public String toString() {
    if (!relative) {
      return absoluteName;
    } else {
      return relativeName;
    }
  }

}
