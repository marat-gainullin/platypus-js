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
 * A {@link PathCommand} that represents a cubic Bézier segment.
 */
public class CurveTo extends EndPointCommand {

  protected double x1 = 0;
  protected double y1 = 0;
  protected double x2 = 0;
  protected double y2 = 0;
  private String absoluteName = "C";
  private String relativeName = "c";

  /**
   * Creates a curve {@link PathCommand}. Defaults to absolute.
   */
  public CurveTo() {
  }

  /**
   * Creates a copy of the given curve {@link PathCommand}.
   * 
   * @param curveto the curve to copy
   */
  public CurveTo(CurveTo curveto) {
    super(curveto);
    this.x1 = curveto.x1;
    this.y1 = curveto.y1;
    this.x2 = curveto.x2;
    this.y2 = curveto.y2;
  }

  /**
   * Creates a curve {@link PathCommand} with the given points. Defaults to
   * absolute.
   * 
   * @param x1 the x-coordinate of the beginning control point of the curve
   * @param y1 the y-coordinate of the beginning control point of the curve
   * @param x2 the x-coordinate of the ending control point of the curve
   * @param y2 the y-coordinate of the ending control point of the curve
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   */
  public CurveTo(double x1, double y1, double x2, double y2, double x, double y) {
    super(x, y);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  /**
   * Creates a curve {@link PathCommand} with the given points.
   * 
   * @param x1 the x-coordinate of the beginning control point of the curve
   * @param y1 the y-coordinate of the beginning control point of the curve
   * @param x2 the x-coordinate of the ending control point of the curve
   * @param y2 the y-coordinate of the ending control point of the curve
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   * @param relative true if the command is relative
   */
  public CurveTo(double x1, double y1, double x2, double y2, double x, double y, boolean relative) {
    super(x, y, relative);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  @Override
  public CurveTo copy() {
    return new CurveTo(this);
  }

  /**
   * Returns the x-coordinate of the beginning control point of the curve.
   * 
   * @return the x-coordinate of the beginning control point of the curve
   */
  public double getX1() {
    return x1;
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
   * Returns the y-coordinate of the beginning control point of the curve.
   * 
   * @return the y-coordinate of the beginning control point of the curve
   */
  public double getY1() {
    return y1;
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
    if (!(command instanceof CurveTo)) {
      return false;
    }
    CurveTo curve = (CurveTo) command;
    if (Math.round(this.getX1()) != Math.round(curve.getX1())) {
      return false;
    }
    if (Math.round(this.getY1()) != Math.round(curve.getY1())) {
      return false;
    }
    if (Math.round(this.getX2()) != Math.round(curve.getX2())) {
      return false;
    }
    if (Math.round(this.getY2()) != Math.round(curve.getY2())) {
      return false;
    }
    if (Math.round(this.getX()) != Math.round(curve.getX())) {
      return false;
    }
    if (Math.round(this.getY()) != Math.round(curve.getY())) {
      return false;
    }

    return true;
  }

  /**
   * Sets the x-coordinate of the beginning control point of the curve.
   * 
   * @param x1 the x-coordinate of the beginning control point of the curve
   */
  public void setX1(double x1) {
    this.x1 = x1;
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
   * Sets the y-coordinate of the beginning control point of the curve.
   * 
   * @param y1 the y-coordinate of the beginning control point of the curve
   */
  public void setY1(double y1) {
    this.y1 = y1;
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
      setX1(getX1() + x);
      setX2(getX2() + x);
      setY1(getY1() + y);
      setY2(getY2() + y);
    }
  }

  @Override
  public List<PathCommand> toCurve(PrecisePoint currentPoint, PrecisePoint movePoint, PrecisePoint curvePoint,
      PrecisePoint quadraticPoint) {
    quadraticPoint.setX(currentPoint.getX());
    quadraticPoint.setY(currentPoint.getY());
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
    build.append(x1).append(",").append(y1).append(" ").append(x2).append(",").append(y2).append(" ").append(x).append(
        ",").append(y);
    return build.toString();
  }

}
