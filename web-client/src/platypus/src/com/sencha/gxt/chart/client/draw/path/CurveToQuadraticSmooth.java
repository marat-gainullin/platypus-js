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
 * A {@link PathCommand} that represents a shorthand quadratic Bézier segment.
 * The control point is assumed to be the reflection of the control point on the
 * previous command relative to the current point.
 */
public class CurveToQuadraticSmooth extends EndPointCommand {

  private String absoluteName = "T";
  private String relativeName = "t";

  /**
   * Creates a shorthand quadratic curve {@link PathCommand} with the given
   * points. Defaults to absolute.
   */
  public CurveToQuadraticSmooth() {
  }

  /**
   * Creates a copy of the given shorthand quadratic curve {@link PathCommand}
   * with the given points.
   * 
   * @param smoothQuadratic the shorthand quadratic curve to copy
   */
  public CurveToQuadraticSmooth(CurveToQuadraticSmooth smoothQuadratic) {
    super(smoothQuadratic);
  }

  /**
   * Creates a shorthand quadratic curve {@link PathCommand} with the given
   * points. Defaults to absolute.
   * 
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   */
  public CurveToQuadraticSmooth(double x, double y) {
    super(x, y);
  }

  /**
   * Creates a shorthand quadratic curve {@link PathCommand} with the given
   * points.
   * 
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   * @param relative true if the command is relative
   */
  public CurveToQuadraticSmooth(double x, double y, boolean relative) {
    super(x, y, relative);
  }

  @Override
  public CurveToQuadraticSmooth copy() {
    return new CurveToQuadraticSmooth(this);
  }

  @Override
  public boolean nearEqual(PathCommand command) {
    if (!(command instanceof LineTo)) {
      return false;
    }
    LineTo line = (LineTo) command;
    if (Math.round(this.getX()) != Math.round(line.getX())) {
      return false;
    }
    if (Math.round(this.getY()) != Math.round(line.getY())) {
      return false;
    }

    return true;
  }

  @Override
  public List<PathCommand> toCurve(PrecisePoint currentPoint, PrecisePoint movePoint, PrecisePoint curvePoint,
      PrecisePoint quadraticPoint) {
    double x = currentPoint.getX();
    double y = currentPoint.getY();
    double qx = x + (x - quadraticPoint.getX());
    double qy = y + (y - quadraticPoint.getY());
    quadraticPoint.setX(qx);
    quadraticPoint.setY(qy);
    double ax = 2.0 * qx / 3.0;
    double ay = 2.0 * qy / 3.0;
    List<PathCommand> commands = new ArrayList<PathCommand>();
    commands.add(new CurveTo(x / 3.0 + ax, y / 3.0 + ay, this.x / 3.0 + ax, this.y / 3.0 + ay, this.x, this.y));
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
    build.append(x).append(",").append(y);
    return build.toString();
  }
}
