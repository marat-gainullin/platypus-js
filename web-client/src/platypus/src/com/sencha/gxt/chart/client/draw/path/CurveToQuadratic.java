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
 * A {@link PathCommand} that represents a quadratic Bézier segment.
 */
public class CurveToQuadratic extends EndPointCommand {

  private double x1;
  private double y1;
  private String absoluteName = "Q";
  private String relativeName = "q";

  /**
   * Creates a quadratic curve {@link PathCommand}. Defaults to absolute.
   */
  public CurveToQuadratic() {
  }

  /**
   * Creates a copy of the given quadratic curve {@link PathCommand}.
   * 
   * @param quad the curve to copy
   */
  public CurveToQuadratic(CurveToQuadratic quad) {
    super(quad);
    this.x1 = quad.x1;
    this.y1 = quad.y1;
  }

  /**
   * Creates a quadratic curve {@link PathCommand} with the given points.
   * Defaults to absolute.
   * 
   * @param x1 the x-coordinate of the control point of the curve
   * @param y1 the y-coordinate of the control point of the curve
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   */
  public CurveToQuadratic(double x1, double y1, double x, double y) {
    super(x, y);
    this.x1 = x1;
    this.y1 = y1;
  }

  /**
   * Creates a quadratic curve {@link PathCommand} with the given points.
   * 
   * @param x1 the x-coordinate of the control point of the curve
   * @param y1 the y-coordinate of the control point of the curve
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   * @param relative true if the command is relative
   */
  public CurveToQuadratic(double x1, double y1, double x, double y, boolean relative) {
    super(x, y, relative);
    this.x1 = x1;
    this.y1 = y1;
  }

  @Override
  public CurveToQuadratic copy() {
    return new CurveToQuadratic(this);
  }

  /**
   * Returns the x-coordinate of the control point of the curve.
   * 
   * @return the x-coordinate of the control point of the curve
   */
  public double getX1() {
    return x1;
  }

  /**
   * Returns the y-coordinate of the control point of the curve.
   * 
   * @return the y-coordinate of the control point of the curve
   */
  public double getY1() {
    return y1;
  }

  @Override
  public boolean nearEqual(PathCommand command) {
    if (!(command instanceof CurveToQuadratic)) {
      return false;
    }
    CurveToQuadratic quad = (CurveToQuadratic) command;
    if (Math.round(this.getX1()) != Math.round(quad.getX1())) {
      return false;
    }
    if (Math.round(this.getY1()) != Math.round(quad.getY1())) {
      return false;
    }
    if (Math.round(this.getX()) != Math.round(quad.getX())) {
      return false;
    }
    if (Math.round(this.getY()) != Math.round(quad.getY())) {
      return false;
    }

    return true;
  }

  /**
   * Sets the x-coordinate of the control point of the curve.
   * 
   * @param x1 the x-coordinate of the control point of the curve
   */
  public void setX1(double x1) {
    this.x1 = x1;
  }

  /**
   * Sets the y-coordinate of the control point of the curve.
   * 
   * @param y1 the y-coordinate of the control point of the curve
   */
  public void setY1(double y1) {
    this.y1 = y1;
  }

  @Override
  public void toAbsolute(PrecisePoint currentPoint, PrecisePoint movePoint) {
    double x = currentPoint.getX();
    double y = currentPoint.getY();
    boolean relative = isRelative();

    super.toAbsolute(currentPoint, movePoint);
    if (relative) {
      setX1(getX1() + x);
      setY1(getY1() + y);
    }
  }

  @Override
  public List<PathCommand> toCurve(PrecisePoint currentPoint, PrecisePoint movePoint, PrecisePoint curvePoint,
      PrecisePoint quadraticPoint) {
    quadraticPoint.setX(x1);
    quadraticPoint.setY(y1);
    double ax = 2.0 * x1 / 3.0;
    double ay = 2.0 * y1 / 3.0;
    List<PathCommand> commands = new ArrayList<PathCommand>();
    commands.add(new CurveTo(currentPoint.getX() / 3.0 + ax, currentPoint.getY() / 3.0 + ay, x / 3.0 + ax,
        y / 3.0 + ay, x, y));
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
    build.append(x1).append(",").append(y1).append(" ").append(x).append(",").append(y);
    return build.toString();
  }

}
