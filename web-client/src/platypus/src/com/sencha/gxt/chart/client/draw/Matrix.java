/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

import com.sencha.gxt.chart.client.draw.engine.SVG;
import com.sencha.gxt.chart.client.draw.engine.VML;
import com.sencha.gxt.core.client.util.PrecisePoint;

/**
 * Matrix class representing the vector transform matrix used in {@link SVG} and
 * {@link VML}.
 * <p />
 * Many of SVG's graphics operations utilize 2x3 matrices of the form:
 * 
 * <pre>
 * [a c e]
 * [b d f]
 * </pre>
 * which, when expanded into a 3x3 matrix for the purposes of matrix arithmetic,
 * become:
 * 
 * <pre>
 * [a c e]
 * [b d f]
 * [0 0 1]
 * </pre>
 * See <a
 * href="http://www.w3.org/TR/SVG/coords.html#InterfaceSVGMatrix">Matrix</a> for
 * more information.
 */
public class Matrix {

  private double[][] matrix = new double[3][3];

  /**
   * Creates a new identity matrix.
   */
  public Matrix() {
    identity();
  }

  /**
   * Creates a new matrix using the given double values.
   * 
   * @param a the a component of the matrix
   * @param b the b component of the matrix
   * @param c the c component of the matrix
   * @param d the d component of the matrix
   * @param e the e component of the matrix
   * @param f the f component of the matrix
   */
  public Matrix(double a, double b, double c, double d, double e, double f) {
    matrix[0][0] = a;
    matrix[0][1] = b;
    matrix[0][2] = c;
    matrix[1][0] = d;
    matrix[1][1] = e;
    matrix[1][2] = f;
    matrix[2][0] = 0;
    matrix[2][1] = 0;
    matrix[2][2] = 1;
  }

  /**
   * Creates a clone of the given matrix.
   * 
   * @param clone the matrix to be cloned
   */
  public Matrix(Matrix clone) {
    if (clone != null) {
      matrix[0][0] = clone.get(0, 0);
      matrix[0][1] = clone.get(0, 1);
      matrix[0][2] = clone.get(0, 2);
      matrix[1][0] = clone.get(1, 0);
      matrix[1][1] = clone.get(1, 1);
      matrix[1][2] = clone.get(1, 2);
      matrix[2][0] = clone.get(2, 0);
      matrix[2][1] = clone.get(2, 1);
      matrix[2][2] = clone.get(2, 2);
    } else {
      this.identity();
    }
  }

  /**
   * Adds the given matrix to this matrix.
   * 
   * @param a the a component of the matrix
   * @param b the b component of the matrix
   * @param c the c component of the matrix
   * @param d the d component of the matrix
   * @param e the e component of the matrix
   * @param f the f component of the matrix
   */
  public void add(double a, double b, double c, double d, double e, double f) {
    Matrix out = new Matrix();
    Matrix matrix = new Matrix(a, c, e, b, d, f);
    double res = 0;

    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        res = 0;
        for (int z = 0; z < 3; z++) {
          res += this.matrix[x][z] * matrix.get(z, y);
        }
        out.set(x, y, res);
      }
    }
    this.matrix = out.getMatrix();
  }

  /**
   * Returns the element of the matrix at the given row and column.
   * 
   * @param row the row of the element
   * @param column the column of the element
   * @return the element of the matrix at the given row and column
   */
  public double get(int row, int column) {
    return matrix[row][column];
  }

  /**
   * Returns the array of element data.
   * 
   * @return the array of element data
   */
  public double[][] getMatrix() {
    return matrix;
  }

  /**
   * Sets this matrix to the identity matrix.
   */
  public void identity() {
    matrix[0][0] = 1;
    matrix[0][1] = 0;
    matrix[0][2] = 0;
    matrix[1][0] = 0;
    matrix[1][1] = 1;
    matrix[1][2] = 0;
    matrix[2][0] = 0;
    matrix[2][1] = 0;
    matrix[2][2] = 1;
  }

  /**
   * Returns the inverse of this matrix.
   * 
   * @return the inverse of this matrix
   */
  public Matrix invert() {
    double a = matrix[0][0];
    double b = matrix[1][0];
    double c = matrix[0][1];
    double d = matrix[1][1];
    double e = matrix[0][2];
    double f = matrix[1][2];
    return new Matrix(a, b, c, d, e, f);
  }

  /**
   * Multiplies the matrix by the given point and returns the result.
   * 
   * @param point the point to multiply
   * @return the result of the multiplication
   */
  public PrecisePoint pointMultiply(PrecisePoint point) {
    return new PrecisePoint(point.getX() * matrix[0][0] + point.getY() * matrix[0][1] + matrix[0][2], point.getX()
        * matrix[1][0] + point.getY() * matrix[1][1] + matrix[1][2]);
  }

  /**
   * Prepend this matrix with the given matrix.
   * 
   * @param a the a component of the matrix
   * @param b the b component of the matrix
   * @param c the c component of the matrix
   * @param d the d component of the matrix
   * @param e the e component of the matrix
   * @param f the f component of the matrix
   */
  public void prepend(double a, double b, double c, double d, double e, double f) {
    Matrix out = new Matrix();
    Matrix matrix = new Matrix(a, c, e, b, d, f);
    double res = 0;

    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        res = 0;
        for (int z = 0; z < 3; z++) {
          res += matrix.get(x, z) * this.matrix[z][y];
        }
        out.set(x, y, res);
      }
    }
    this.matrix = out.getMatrix();
  }

  /**
   * Rotates the matrix using the given angle and arbitrary axis.
   * 
   * @param angle the angle used to rotate the matrix
   * @param x the x coordinate of the arbitrary axis
   * @param y the y coordinate of the arbitrary axis
   */
  public void rotate(double angle, double x, double y) {
    double a = Math.toRadians(angle);
    add(Math.cos(a), Math.sin(a), -Math.sin(a), Math.cos(a), x, y);
    add(1, 0, 0, 1, -x, -y);
  }

  /**
   * Scales the matrix using given x and y scale and the given origin
   * 
   * @param x the x scale
   * @param y the y scale
   * @param cx the x-coordinate of the scale
   * @param cy the y-coordinate of the scale
   */
  public void scale(double x, double y, double cx, double cy) {
    add(1, 0, 0, 1, cx, cy);
    add(x, 0, 0, y, 0, 0);
    add(1, 0, 0, 1, -cx, -cy);
  }

  /**
   * Sets an element of the matrix using the given value at the specified row
   * and column.
   * 
   * @param row the row of the element
   * @param column the column of the element
   * @param value the new value of the element
   */
  public void set(int row, int column, double value) {
    matrix[row][column] = value;
  }

  /**
   * Sets the element data of the matrix using the given array.
   * 
   * @param matrix must be a 3x3 array
   */
  public void setMatrix(double[][] matrix) {
    this.matrix = matrix;
  }

  /**
   * Translates the matrix using the given x and y values.
   * 
   * @param x the translation on the x axis
   * @param y the translation on the y axis
   */
  public void translate(double x, double y) {
    prepend(1, 0, 0, 1, x, y);
  }

}
