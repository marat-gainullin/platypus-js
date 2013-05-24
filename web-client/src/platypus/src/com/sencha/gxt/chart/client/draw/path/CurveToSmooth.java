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
 * A {@link PathCommand} that represents a shorthand cubic Bézier segment. The
 * first control point is assumed to be the reflection of the second control
 * point on the previous command relative to the current point.
 */
public class CurveToSmooth extends EndPointCommand {

  private double x2;
  private double y2;
  private String absoluteName = "S";
  private String relativeName = "s";

  /**
   * Creates a shorthand curve {@link PathCommand}. Defaults to absolute.
   */
  public CurveToSmooth() {
  }

  /**
   * Creates a copy of the given shorthand curve {@link PathCommand}.
   * 
   * @param curveto the shorthand curve to copy
   */
  public CurveToSmooth(CurveToSmooth curveto) {
    super(curveto);
    this.x2 = curveto.x2;
    this.y2 = curveto.y2;
  }

  /**
   * Creates a shorthand curve {@link PathCommand} with the given points.
   * Defaults to absolute.
   * 
   * @param x2 the x-coordinate of the ending control point of the curve
   * @param y2 the y-coordinate of the ending control point of the curve
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   */
  public CurveToSmooth(double x2, double y2, double x, double y) {
    super(x, y);
    this.x2 = x2;
    this.y2 = y2;
  }

  /**
   * Creates a curve {@link PathCommand} with the given points. Defaults to
   * absolute.
   * 
   * @param x2 the x-coordinate of the ending control point of the curve
   * @param y2 the y-coordinate of the ending control point of the curve
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   * @param relative true if the command is relative
   */
  public CurveToSmooth(double x2, double y2, double x, double y, boolean relative) {
    super(x, y, relative);
    this.x2 = x2;
    this.y2 = y2;
  }

  @Override
  public CurveToSmooth copy() {
    return new CurveToSmooth(this);
  }

  /**
   * Returns the x-coordinate of the ending control point of the curve
   * 
   * @return the x-coordinate of the ending control point of the curve
   */
  public double getX2() {
    return x2;
  }

  /**
   * Returns the y-coordinate of the ending control point of the curve
   * 
   * @return the y-coordinate of the ending control point of the curve
   */
  public double getY2() {
    return y2;
  }

  @Override
  public boolean nearEqual(PathCommand command) {
    if (!(command instanceof CurveToSmooth)) {
      return false;
    }
    CurveToSmooth curveto = (CurveToSmooth) command;
    if (Math.round(this.getX2()) != Math.round(curveto.getX2())) {
      return false;
    }
    if (Math.round(this.getY2()) != Math.round(curveto.getY2())) {
      return false;
    }
    if (Math.round(this.getX()) != Math.round(curveto.getX())) {
      return false;
    }
    if (Math.round(this.getY()) != Math.round(curveto.getY())) {
      return false;
    }

    return true;
  }

  /**
   * Sets the x-coordinate of the ending control point of the curve.
   * 
   * @param x2 the x-coordinate of the ending control point of the curve
   */
  public void setX2(double x2) {
    this.x2 = x2;
  }

  /**
   * Sets the y-coordinate of the ending control point of the curve.
   * 
   * @param y2 the y-coordinate of the ending control point of the curve
   */
  public void setY2(double y2) {
    this.y2 = y2;
  }

  @Override
  public void toAbsolute(PrecisePoint currentPoint, PrecisePoint movePoint) {
    double x = currentPoint.getX();
    double y = currentPoint.getY();
    boolean relative = isRelative();

    super.toAbsolute(currentPoint, movePoint);
    if (relative) {
      setX2(getX2() + x);
      setY2(getY2() + y);
    }
  }

  @Override
  public List<PathCommand> toCurve(PrecisePoint currentPoint, PrecisePoint movePoint, PrecisePoint curvePoint,
      PrecisePoint quadraticPoint) {
    quadraticPoint.setX(currentPoint.getX());
    quadraticPoint.setY(currentPoint.getY());
    double x = currentPoint.getX();
    double y = currentPoint.getY();
    double bx = curvePoint.getX();
    double by = curvePoint.getY();
    List<PathCommand> commands = new ArrayList<PathCommand>();
    commands.add(new CurveTo(x + (x - bx), y + (y - by), x2, y2, this.x, this.y));
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
    build.append(x2).append(",").append(y2).append(" ").append(x).append(",").append(y);
    return build.toString();
  }

}
