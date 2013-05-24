/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import com.sencha.gxt.chart.client.draw.path.ClosePath;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.CircleSprite;
import com.sencha.gxt.chart.client.draw.sprite.RectangleSprite;

/**
 * Collection of methods to generate primitive shapes. Used in series to
 * generate markers.
 * 
 */
public class Primitives {

  /**
   * Generates an arrow using the given parameters.
   * 
   * @param cx the center x-coordinate of the primitive
   * @param cy the center x-coordinate of the primitive
   * @param radius the size of the primitive
   * @return the generated primitive
   */
  public static PathSprite arrow(double cx, double cy, double radius) {
    PathSprite arrow = new PathSprite();
    arrow.addCommand(new MoveTo(cx - radius * 0.7, cy - radius * 0.4));
    arrow.addCommand(new LineTo(radius * 0.6, 0, true));
    arrow.addCommand(new LineTo(0, -radius * 0.4, true));
    arrow.addCommand(new LineTo(radius, radius * 0.8, true));
    arrow.addCommand(new LineTo(-radius, radius * 0.8, true));
    arrow.addCommand(new LineTo(0, -radius * 0.4, true));
    arrow.addCommand(new LineTo(-radius * 0.6, 0, true));
    arrow.addCommand(new ClosePath());
    return arrow;
  }

  /**
   * Generates a circle using the given parameters.
   * 
   * @param cx the center x-coordinate of the primitive
   * @param cy the center x-coordinate of the primitive
   * @param radius the size of the primitive
   * @return the generated primitive
   */
  public static CircleSprite circle(double cx, double cy, double radius) {
    CircleSprite circle = new CircleSprite();
    circle.setCenterX(cx);
    circle.setCenterY(cy);
    circle.setRadius(radius);
    return circle;
  }

  /**
   * Generates a cross using the given parameters.
   * 
   * @param cx the center x-coordinate of the primitive
   * @param cy the center x-coordinate of the primitive
   * @param radius the size of the primitive
   * @return the generated primitive
   */
  public static PathSprite cross(double cx, double cy, double radius) {
    PathSprite cross = new PathSprite();
    radius /= 2.5;
    cross.addCommand(new MoveTo(cx - radius, cy));
    cross.addCommand(new LineTo(-radius, -radius, true));
    cross.addCommand(new LineTo(radius, -radius, true));
    cross.addCommand(new LineTo(radius, radius, true));
    cross.addCommand(new LineTo(radius, -radius, true));
    cross.addCommand(new LineTo(radius, radius, true));
    cross.addCommand(new LineTo(-radius, radius, true));
    cross.addCommand(new LineTo(radius, radius, true));
    cross.addCommand(new LineTo(-radius, radius, true));
    cross.addCommand(new LineTo(-radius, -radius, true));
    cross.addCommand(new LineTo(-radius, radius, true));
    cross.addCommand(new LineTo(-radius, -radius, true));
    cross.addCommand(new ClosePath());
    return cross;
  }

  /**
   * Generates a diamond using the given parameters.
   * 
   * @param cx the center x-coordinate of the primitive
   * @param cy the center x-coordinate of the primitive
   * @param radius the size of the primitive
   * @return the generated primitive
   */
  public static PathSprite diamond(double cx, double cy, double radius) {
    PathSprite diamond = new PathSprite();
    diamond.addCommand(new MoveTo(cx, cy - radius));
    diamond.addCommand(new LineTo(radius, radius, true));
    diamond.addCommand(new LineTo(-radius, radius, true));
    diamond.addCommand(new LineTo(-radius, -radius, true));
    diamond.addCommand(new LineTo(radius, -radius, true));
    diamond.addCommand(new ClosePath());
    return diamond;
  }

  /**
   * Generates a line using the given parameters.
   * 
   * @param cx the center x-coordinate of the primitive
   * @param cy the center x-coordinate of the primitive
   * @param radius the size of the primitive
   * @return the generated primitive
   */
  public static RectangleSprite line(double cx, double cy, double radius) {
    RectangleSprite line = new RectangleSprite();
    line.setX(cx - radius);
    line.setY(cy - radius);
    line.setHeight(2 * radius);
    line.setWidth(2 * radius / 5);
    return line;
  }

  /**
   * Generates a plus using the given parameters.
   * 
   * @param cx the center x-coordinate of the primitive
   * @param cy the center x-coordinate of the primitive
   * @param radius the size of the primitive
   * @return the generated primitive
   */
  public static PathSprite plus(double cx, double cy, double radius) {
    PathSprite plus = new PathSprite();
    radius /= 2;
    plus.addCommand(new MoveTo(cx - radius / 2, cy - radius / 2));
    plus.addCommand(new LineTo(0, -radius, true));
    plus.addCommand(new LineTo(radius, 0, true));
    plus.addCommand(new LineTo(0, radius, true));
    plus.addCommand(new LineTo(radius, 0, true));
    plus.addCommand(new LineTo(0, radius, true));
    plus.addCommand(new LineTo(-radius, 0, true));
    plus.addCommand(new LineTo(0, radius, true));
    plus.addCommand(new LineTo(-radius, 0, true));
    plus.addCommand(new LineTo(0, -radius, true));
    plus.addCommand(new LineTo(-radius, 0, true));
    plus.addCommand(new LineTo(0, -radius, true));
    plus.addCommand(new ClosePath());
    return plus;
  }

  /**
   * Generates a square using the given parameters.
   * 
   * @param cx the center x-coordinate of the primitive
   * @param cy the center x-coordinate of the primitive
   * @param radius the size of the primitive
   * @return the generated primitive
   */
  public static RectangleSprite square(double cx, double cy, double radius) {
    RectangleSprite square = new RectangleSprite();
    square.setX(cx - radius);
    square.setY(cy - radius);
    square.setHeight(2 * radius);
    square.setWidth(2 * radius);
    return square;
  }

  /**
   * Generates a triangle using the given parameters.
   * 
   * @param cx the center x-coordinate of the primitive
   * @param cy the center x-coordinate of the primitive
   * @param radius the size of the primitive
   * @return the generated primitive
   */
  public static PathSprite triangle(double cx, double cy, double radius) {
    PathSprite triangle = new PathSprite();
    radius *= 1.75;
    triangle.addCommand(new MoveTo(cx, cy));
    triangle.addCommand(new MoveTo(0, -radius * 0.58, true));
    triangle.addCommand(new LineTo(radius * 0.5, radius * 0.87, true));
    triangle.addCommand(new LineTo(-radius, 0, true));
    triangle.addCommand(new ClosePath());
    return triangle;
  }

}
