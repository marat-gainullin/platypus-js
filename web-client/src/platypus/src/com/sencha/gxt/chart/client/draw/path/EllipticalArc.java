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
 * A {@link PathCommand} that represents a segment of an ellipse.
 */
public class EllipticalArc extends EndPointCommand {

  private double radiusX = 0;
  private double radiusY = 0;
  private double xAxisRotation = 0;
  private int largeArcFlag = 0;
  private int sweepFlag = 0;
  private String absoluteName = "A";
  private String relativeName = "a";

  /**
   * Creates an elliptical arc {@link PathCommand}. Defaults to absolute.
   */
  public EllipticalArc() {
  }

  /**
   * Creates an elliptical arc {@link PathCommand} using the given values.
   * Defaults to absolute.
   * 
   * @param radiusX the radius of the ellipse on its x-axis
   * @param radiusY the radius of the ellipse on its y-axis
   * @param xAxisRotation the rotation of the ellipse in relation to the current
   *          coordinate system
   * @param largeArcFlag if 0 then the smaller arc will be used, if 1 then the
   *          larger
   * @param sweepFlag if 0 then the arc will be in a negative angle, if 1 then
   *          in a positive angle
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   */
  public EllipticalArc(double radiusX, double radiusY, double xAxisRotation, int largeArcFlag, int sweepFlag, double x,
      double y) {
    super(x, y);
    this.radiusX = radiusX;
    this.radiusY = radiusY;
    this.xAxisRotation = xAxisRotation;
    this.largeArcFlag = largeArcFlag;
    this.sweepFlag = sweepFlag;
  }

  /**
   * Creates an elliptical arc {@link PathCommand} using the given values.
   * 
   * @param radiusX the radius of the ellipse on its x-axis
   * @param radiusY the radius of the ellipse on its y-axis
   * @param xAxisRotation the rotation of the ellipse in relation to the current
   *          coordinate system
   * @param largeArcFlag if 0 then the smaller arc will be used, if 1 then the
   *          larger
   * @param sweepFlag if 0 then the arc will be in a negative angle, if 1 then
   *          in a positive angle
   * @param x the x-coordinate of the end of the segment
   * @param y the y-coordinate of the end of the segment
   * @param relative true if the command is relative
   */
  public EllipticalArc(double radiusX, double radiusY, double xAxisRotation, int largeArcFlag, int sweepFlag, double x,
      double y, boolean relative) {
    super(x, y, relative);
    this.radiusX = radiusX;
    this.radiusY = radiusY;
    this.xAxisRotation = xAxisRotation;
    this.largeArcFlag = largeArcFlag;
    this.sweepFlag = sweepFlag;
  }

  /**
   * Creates a copy of the given elliptical arc.
   * 
   * @param command the arc to be copied
   */
  public EllipticalArc(EllipticalArc command) {
    super(command);
    this.radiusX = command.radiusX;
    this.radiusY = command.radiusY;
    this.xAxisRotation = command.xAxisRotation;
    this.largeArcFlag = command.largeArcFlag;
    this.sweepFlag = command.sweepFlag;
  }

  @Override
  public EllipticalArc copy() {
    return new EllipticalArc(this);
  }

  /**
   * Returns the large arc flag of the arc.
   * 
   * @return the large arc flag of the arc
   */
  public int getLargeArcFlag() {
    return largeArcFlag;
  }

  /**
   * Returns the radius of the ellipse on its x-axis.
   * 
   * @return the radius of the ellipse on its x-axis
   */
  public double getRadiusX() {
    return radiusX;
  }

  /**
   * Returns the radius of the ellipse on its y-axis.
   * 
   * @return the radius of the ellipse on its y-axis
   */
  public double getRadiusY() {
    return radiusY;
  }

  /**
   * Returns the sweep flag of the arc.
   * 
   * @return the sweep flag of the arc
   */
  public int getSweepFlag() {
    return sweepFlag;
  }

  /**
   * Returns the rotation of the ellipse in relation to the current coordinate
   * system.
   * 
   * @return the rotation of the ellipse in relation to the current coordinate
   *         system
   */
  public double getxAxisRotation() {
    return xAxisRotation;
  }

  @Override
  public boolean nearEqual(PathCommand command) {
    if (!(command instanceof EllipticalArc)) {
      return false;
    }
    EllipticalArc arc = (EllipticalArc) command;

    if (Math.round(this.getRadiusX()) != Math.round(arc.getRadiusX())) {
      return false;
    }
    if (Math.round(this.getRadiusY()) != Math.round(arc.getRadiusY())) {
      return false;
    }
    if (Math.round(this.getxAxisRotation()) != Math.round(arc.getxAxisRotation())) {
      return false;
    }
    if (Math.round(this.getLargeArcFlag()) != Math.round(arc.getLargeArcFlag())) {
      return false;
    }
    if (Math.round(this.getSweepFlag()) != Math.round(arc.getSweepFlag())) {
      return false;
    }
    if (Math.round(this.getX()) != Math.round(arc.getX())) {
      return false;
    }
    if (Math.round(this.getY()) != Math.round(arc.getY())) {
      return false;
    }
    return true;
  }

  /**
   * Sets the large arc flag of the arc.
   * 
   * @param largeArcFlag if 0 then the smaller arc will be used, if 1 then the
   *          larger
   */
  public void setLargeArcFlag(int largeArcFlag) {
    this.largeArcFlag = largeArcFlag;
  }

  /**
   * Sets the radius of the ellipse on its x-axis.
   * 
   * @param radiusX the radius of the ellipse on its x-axis
   */
  public void setRadiusX(double radiusX) {
    this.radiusX = radiusX;
  }

  /**
   * Sets the radius of the ellipse on its y-axis.
   * 
   * @param radiusY the radius of the ellipse on its y-axis
   */
  public void setRadiusY(double radiusY) {
    this.radiusY = radiusY;
  }

  /**
   * Sets the sweep flag of the arc.
   * 
   * @param sweepFlag if 0 then the arc will be in a negative angle, if 1 then
   *          in a positive angle
   */
  public void setSweepFlag(int sweepFlag) {
    this.sweepFlag = sweepFlag;
  }

  /**
   * Sets the rotation of the ellipse in relation to the current coordinate
   * system
   * 
   * @param xAxisRotation the rotation of the ellipse in relation to the current
   *          coordinate system
   */
  public void setxAxisRotation(double xAxisRotation) {
    this.xAxisRotation = xAxisRotation;
  }

  @Override
  public List<PathCommand> toCurve(PrecisePoint currentPoint, PrecisePoint movePoint, PrecisePoint curvePoint,
      PrecisePoint quadraticPoint) {
    quadraticPoint.setX(currentPoint.getX());
    quadraticPoint.setY(currentPoint.getY());
    return arc2curveRecursive(currentPoint.getX(), currentPoint.getY(), radiusX, radiusY, xAxisRotation, largeArcFlag,
        sweepFlag, x, y, null);
  }

  @Override
  public String toString() {
    StringBuilder build = new StringBuilder();
    if (!relative) {
      build.append(absoluteName);
    } else {
      build.append(relativeName);
    }
    build.append(radiusX).append(",").append(radiusY).append(",").append(xAxisRotation).append(",").append(largeArcFlag).append(
        ",").append(sweepFlag).append(",").append(x).append(",").append(y);
    return build.toString();
  }

  private List<PathCommand> arc2curveRecursive(double x1, double y1, double rx, double ry, double angle,
      int largeArcFlag, int sweepFlag, double x2, double y2, double[] recursive) {
    double cx, cy, f1, f2;
    double rad = Math.toRadians(angle);
    List<PathCommand> res = new ArrayList<PathCommand>();
    if (recursive == null) {
      PrecisePoint xy = rotate(x1, y1, -rad);
      x1 = xy.getX();
      y1 = xy.getY();
      xy = rotate(x2, y2, -rad);
      x2 = xy.getX();
      y2 = xy.getY();
      double x = (x1 - x2) / 2.0;
      double y = (y1 - y2) / 2.0;
      double h = (x * x) / (rx * rx) + (y * y) / (ry * ry);
      if (h > 1) {
        h = Math.sqrt(h);
        rx = h * rx;
        ry = h * ry;
      }
      double rx2 = rx * rx;
      double ry2 = ry * ry;
      double k = (largeArcFlag == sweepFlag ? -1 : 1)
          * Math.sqrt(Math.abs((rx2 * ry2 - rx2 * y * y - ry2 * x * x) / (rx2 * y * y + ry2 * x * x)));
      cx = k * rx * y / ry + (x1 + x2) / 2;
      cy = k * -ry * x / rx + (y1 + y2) / 2;
      f1 = Math.asin((y1 - cy) / ry);
      f2 = Math.asin((y2 - cy) / ry);

      f1 = x1 < cx ? Math.PI - f1 : f1;
      f2 = x2 < cx ? Math.PI - f2 : f2;
      if (f1 < 0) {
        f1 = Math.PI * 2 + f1;
      }
      if (f2 < 0) {
        f2 = Math.PI * 2 + f2;
      }
      if (sweepFlag == 1 && f1 > f2) {
        f1 = f1 - Math.PI * 2;
      }
      if (sweepFlag == 0 && f2 > f1) {
        f2 = f2 - Math.PI * 2;
      }
    } else {
      f1 = recursive[0];
      f2 = recursive[1];
      cx = recursive[2];
      cy = recursive[3];
    }
    double constant = Math.PI * 120 / 180;
    if (Math.abs(f2 - f1) > constant) {
      double f2Old = f2;
      double x2Old = x2;
      double y2Old = y2;
      f2 = f1 + constant * (sweepFlag == 1 && f2 > f1 ? 1 : -1);
      x2 = cx + rx * Math.cos(f2);
      y2 = cy + ry * Math.sin(f2);
      res = arc2curveRecursive(x2, y2, rx, ry, angle, 0, sweepFlag, x2Old, y2Old, new double[] {f2, f2Old, cx, cy});
    }
    double c1 = Math.cos(f1);
    double s1 = Math.sin(f1);
    double c2 = Math.cos(f2);
    double s2 = Math.sin(f2);
    double df = f2 - f1;
    double t = Math.tan(df / 4.0);
    double hx = 4.0 / 3.0 * rx * t;
    double hy = 4.0 / 3.0 * ry * t;
    PrecisePoint m1 = new PrecisePoint(x1, y1);
    PrecisePoint m2 = new PrecisePoint(x1 + hx * s1, y1 - hy * c1);
    PrecisePoint m3 = new PrecisePoint(x2 + hx * s2, y2 - hy * c2);
    PrecisePoint m4 = new PrecisePoint(x2, y2);
    m2.setX(2 * m1.getX() - m2.getX());
    m2.setY(2 * m1.getY() - m2.getY());
    if (res.size() > 0) {
      res.add(0, new CurveTo(m2.getX(), m2.getY(), m3.getX(), m3.getY(), m4.getX(), m4.getY()));
    } else {
      res.add(new CurveTo(m2.getX(), m2.getY(), m3.getX(), m3.getY(), m4.getX(), m4.getY()));
    }
    if (recursive == null) {
      for (int k = 0; k < res.size(); k++) {
        CurveTo curve = (CurveTo) res.get(k);
        PrecisePoint current = rotate(curve.getX1(), curve.getY1(), rad);
        curve.setX1(current.getX());
        curve.setY1(current.getY());
        current = rotate(curve.getX2(), curve.getY2(), rad);
        curve.setX2(current.getX());
        curve.setY2(current.getY());
        current = rotate(curve.getX(), curve.getY(), rad);
        curve.setX(current.getX());
        curve.setY(current.getY());
      }
    }
    return res;
  }

  private PrecisePoint rotate(double x, double y, double rad) {
    double cos = Math.cos(rad);
    double sin = Math.sin(rad);
    return new PrecisePoint(x * cos - y * sin, x * sin + y * cos);
  }

}
