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

import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.sencha.gxt.chart.client.draw.Matrix;
import com.sencha.gxt.chart.client.draw.sprite.CircleSprite;
import com.sencha.gxt.chart.client.draw.sprite.EllipseSprite;
import com.sencha.gxt.chart.client.draw.sprite.RectangleSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;

/**
 * A {@link Sprite} that represents a path. Also contains utility functions for
 * path manipulation.
 */
public class PathSprite extends Sprite {

  /**
   * Returns a new list of {@link PathCommand}s by making copies of the given
   * commands.
   * 
   * @param commands the commands to be copied
   * @return the new list
   */
  public static List<PathCommand> copyCommands(List<PathCommand> commands) {
    ArrayList<PathCommand> copy = new ArrayList<PathCommand>();
    for (int i = 0; i < commands.size(); i++) {
      copy.add(commands.get(i).copy());
    }
    return copy;
  }

  /**
   * Ensures {@link PathCommand} parity between this path and the given path.
   * 
   * @param origin the path to be compared
   * @param commands the path commands to compare against
   * @return list of the converted path and the delta between the target
   *         commands
   */
  public static List<PathSprite> findDelta(PathSprite origin, List<PathCommand> commands) {
    PathSprite me = origin.copy().toAbsolute();
    PathSprite sprite = new PathSprite();
    sprite.setCommands(commands);
    sprite = sprite.copy().toAbsolute();
    PrecisePoint myCurrentPoint = new PrecisePoint();
    PrecisePoint myMovePoint = new PrecisePoint();
    PrecisePoint myCurvePoint = new PrecisePoint();
    PrecisePoint myQuadraticPoint = new PrecisePoint();
    PrecisePoint spriteCurrentPoint = new PrecisePoint();
    PrecisePoint spriteMovePoint = new PrecisePoint();
    PrecisePoint spriteCurvePoint = new PrecisePoint();
    PrecisePoint spriteQuadraticPoint = new PrecisePoint();

    while (me.size() < sprite.size()) {
      me.addCommand(sprite.getCommand(me.size()));
    }
    while (me.size() > sprite.size()) {
      me.removeCommand(me.size() - 1);
    }
    int longestPathCount = Math.max(me.size(), sprite.size());
    for (int i = 0; i < longestPathCount; i++) {
      // Convert the command to a curve
      PathCommand myCommand;
      if (i < me.size()) {
        myCommand = me.getCommand(i);
        myCommand = myCommand.toCurve(myCurrentPoint, myMovePoint, myCurvePoint, myQuadraticPoint).get(0);
        me.setCommand(i, myCommand);
      } else {
        myCommand = new CurveTo(myCurrentPoint.getX(), myCurrentPoint.getY(), myCurrentPoint.getX(),
            myCurrentPoint.getY(), myCurrentPoint.getX(), myCurrentPoint.getY());
        me.addCommand(myCommand);
      }

      PathCommand spriteCommand;
      if (i < sprite.size()) {
        spriteCommand = sprite.getCommand(i);
        spriteCommand = spriteCommand.toCurve(spriteCurrentPoint, spriteMovePoint, spriteCurvePoint,
            spriteQuadraticPoint).get(0);
        sprite.setCommand(i, spriteCommand);
      } else {
        spriteCommand = new CurveTo(spriteCurrentPoint.getX(), spriteCurrentPoint.getY(), spriteCurrentPoint.getX(),
            spriteCurrentPoint.getY(), spriteCurrentPoint.getX(), spriteCurrentPoint.getY());
        sprite.addCommand(spriteCommand);
      }

      // ensure move parity
      if (myCommand instanceof MoveTo || spriteCommand instanceof MoveTo) {
        if (!(spriteCommand instanceof MoveTo)) {
          sprite.addCommand(i, new MoveTo(spriteCurrentPoint.getX(), spriteCurrentPoint.getY()));
          MoveTo move = (MoveTo) myCommand;
          myCurvePoint.setX(0);
          myCurvePoint.setY(0);
          myCurrentPoint.setX(move.getX());
          myCurrentPoint.setY(move.getY());
          longestPathCount = Math.max(me.size(), sprite.size());
        } else if (!(myCommand instanceof MoveTo)) {
          me.addCommand(i, new MoveTo(myCurrentPoint.getX(), myCurrentPoint.getY()));
          MoveTo move = (MoveTo) spriteCommand;
          spriteCurvePoint.setX(0);
          spriteCurvePoint.setY(0);
          spriteCurrentPoint.setX(move.getX());
          spriteCurrentPoint.setY(move.getY());
          longestPathCount = Math.max(me.size(), sprite.size());
        }
      }

      // move the current point of the path
      setCurrentPoint(myCommand, myCurrentPoint, myCurvePoint);
      setCurrentPoint(spriteCommand, spriteCurrentPoint, spriteCurvePoint);
    }

    // find the delta
    PathSprite delta = new PathSprite();
    for (int i = 0; i < me.size(); i++) {
      if (me.getCommand(i) instanceof MoveTo) {
        MoveTo move1 = (MoveTo) me.getCommand(i);
        MoveTo move2 = (MoveTo) sprite.getCommand(i);
        delta.addCommand(new MoveTo(move2.getX() - move1.getX(), move2.getY() - move1.getY()));
      } else if (me.getCommand(i) instanceof CurveTo) {
        CurveTo curve1 = (CurveTo) me.getCommand(i);
        CurveTo curve2 = (CurveTo) sprite.getCommand(i);
        delta.addCommand(new CurveTo(curve2.getX1() - curve1.getX1(), curve2.getY1() - curve1.getY1(), curve2.getX2()
            - curve1.getX2(), curve2.getY2() - curve1.getY2(), curve2.getX() - curve1.getX(), curve2.getY()
            - curve1.getY()));
      }
    }

    List<PathSprite> list = new ArrayList<PathSprite>();
    list.add(me);
    list.add(delta);
    return list;
  }

  /**
   * Updates the current point of the path using the given command.
   * 
   * @param command the current command
   * @param currentPoint the point that stores the current point
   */
  private static void setCurrentPoint(PathCommand command, PrecisePoint currentPoint, PrecisePoint curvePoint) {
    if (command instanceof CurveTo) {
      CurveTo curve = (CurveTo) command;
      currentPoint.setX(curve.getX());
      currentPoint.setY(curve.getY());
      curvePoint.setX(curve.getX2());
      curvePoint.setY(curve.getY2());
    } else if (command instanceof MoveTo) {
      MoveTo move = (MoveTo) command;
      currentPoint.setX(move.getX());
      currentPoint.setY(move.getY());
      curvePoint.setX(move.getX());
      curvePoint.setY(move.getY());
    }
  }

  private List<PathCommand> commands = new ArrayList<PathCommand>();

  private boolean pathDirty = false;

  private LineCap strokeLineCap;
  private boolean strokeLineCapDirty = false;
  private LineJoin strokeLineJoin;
  private boolean strokeLineJoinDirty = false;
  private double miterLimit = 4;
  private boolean miterLimitDirty = false;
  private boolean absolute = false;
  private boolean curved = false;

  /**
   * Creates a path with no values.
   */
  public PathSprite() {
  }

  /**
   * Creates a path sprite by converting the given {@link CircleSprite}.
   * 
   * @param sprite the circle sprite to be converted to a path
   */
  public PathSprite(CircleSprite sprite) {
    super(sprite);
    commands.addAll(ellipseCommands(sprite.getCenterX(), sprite.getCenterY(), sprite.getRadius(), sprite.getRadius()));
    pathDirty = true;
  }

  /**
   * Creates a path sprite by converting the given {@link EllipseSprite}.
   * 
   * @param sprite the ellipse sprite to be converted to a path
   */
  public PathSprite(EllipseSprite sprite) {
    super(sprite);
    commands.addAll(ellipseCommands(sprite.getCenterX(), sprite.getCenterY(), sprite.getRadiusX(), sprite.getRadiusY()));
    pathDirty = true;
  }

  /**
   * Creates a copy of the given path.
   * 
   * @param path the sprite to be copied
   */
  public PathSprite(PathSprite path) {
    super(path);
    commands = copyCommands(path.getCommands());
    pathDirty = true;
    setStrokeLineJoin(path.strokeLineJoin);
  }

  /**
   * Creates a path sprite by converting the given {@link RectangleSprite}.
   * 
   * @param sprite the rectangle sprite to be converted to a path
   */
  public PathSprite(RectangleSprite sprite) {
    super(sprite);
    if (!Double.isNaN(sprite.getStrokeWidth())) {
      setStrokeWidth(sprite.getStrokeWidth());
    } else {
      setStrokeWidth(1);
    }

    if (sprite.getStroke() != null) {
      setStroke(sprite.getStroke());
    } else {
      setStroke(sprite.getFill());
    }

    setTranslation(null);
    setScaling(null);
    setRotation(null);

    commands.addAll(rectangleCommands(sprite.toRectangle(), sprite.getRadius()));
    pathDirty = true;
  }

  /**
   * Creates a path sprite by converting the given {@link TextSprite}.
   * 
   * @param sprite the text sprite to be converted to a path
   */
  public PathSprite(TextSprite sprite) {
    super(sprite);
    PreciseRectangle bbox = sprite.getBBox();
    commands.addAll(rectangleCommands(bbox, Double.NaN));
    pathDirty = true;
  }

  /**
   * Adds a {@link PathCommand} to the path at the given index.
   * 
   * @param index the index to add the command
   * @param command the path command to add
   */
  public void addCommand(int index, PathCommand command) {
    if (!(command instanceof CurveTo)) {
      this.setCurved(false);
    }
    if (command.isRelative()) {
      this.setAbsolute(false);
    }
    commands.add(index, command);
    pathDirty = true;
  }

  /**
   * Adds a {@link PathCommand} to the path.
   * 
   * @param command the path command to add
   */
  public void addCommand(PathCommand command) {
    if (!(command instanceof CurveTo)) {
      this.setCurved(false);
    }
    if (command.isRelative()) {
      this.setAbsolute(false);
    }
    commands.add(command);
    pathDirty = true;
  }

  /**
   * Clears all {@link PathCommand} in the path.
   */
  public void clearCommands() {
    commands.clear();
    pathDirty = true;
  }

  @Override
  public void clearDirtyFlags() {
    super.clearDirtyFlags();
    pathDirty = false;
    strokeLineCapDirty = false;
    strokeLineJoinDirty = false;
    miterLimitDirty = false;
  }

  @Override
  public PathSprite copy() {
    return new PathSprite(this);
  }

  /**
   * Returns the calculated dimensions of the path.
   * 
   * @return the calculated dimensions of the path
   */
  public PreciseRectangle dimensions() {
    if (commands.size() == 0) {
      return new PreciseRectangle();
    }
    PathSprite curve = this.copy().toCurve();
    double x = 0;
    double y = 0;
    PrecisePoint min = new PrecisePoint(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    PrecisePoint max = new PrecisePoint(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

    for (PathCommand command : curve.getCommands()) {
      if (command instanceof MoveTo) {
        MoveTo moveto = (MoveTo) command;
        x = moveto.getX();
        y = moveto.getY();
        min.setX(Math.min(min.getX(), x));
        min.setY(Math.min(min.getY(), y));
        max.setX(Math.max(max.getX(), x));
        max.setY(Math.max(max.getY(), y));
      } else {
        CurveTo curveto = (CurveTo) command;
        PreciseRectangle bbox = this.curveDimensions(new PrecisePoint(x, y), curveto);
        min.setX(Math.min(min.getX(), bbox.getX()));
        min.setY(Math.min(min.getY(), bbox.getY()));
        max.setX(Math.max(max.getX(), bbox.getX() + bbox.getWidth()));
        max.setY(Math.max(max.getY(), bbox.getY() + bbox.getHeight()));
        x = curveto.getX();
        y = curveto.getY();
      }
    }

    return new PreciseRectangle(min.getX(), min.getY(), max.getX() - min.getX(), max.getY() - min.getY());
  }

  /**
   * Returns the {@link PathCommand} at the given index.
   * 
   * @param index the index of the command to return
   * @return the requested command
   */
  public PathCommand getCommand(int index) {
    return commands.get(index);
  }

  /**
   * Returns a {@link List} of all the {@link PathCommand}s in the sprite.
   * 
   * @return a list of all the commands in the sprite
   */
  public List<PathCommand> getCommands() {
    return commands;
  }

  /**
   * Returns the miter limit of the path.
   * 
   * @return the miter limit of the path
   */
  public double getMiterLimit() {
    return miterLimit;
  }

  @Override
  public PathSprite getPathSprite() {
    return new PathSprite(this);
  }

  /**
   * Returns the shape to be used at the end of open subpaths when they are
   * stroked.
   * 
   * @return the shape to be used at the end of open subpaths when they are
   *         stroked
   */
  public LineCap getStrokeLineCap() {
    return strokeLineCap;
  }

  /**
   * Returns the {@link LineJoin} of the path.
   * 
   * @return the line join of the path
   */
  public LineJoin getStrokeLineJoin() {
    return strokeLineJoin;
  }

  /**
   * Returns true if all path commands are absolute.
   * 
   * @return true if all path commands are absolute
   */
  public boolean isAbsolute() {
    return absolute;
  }

  /**
   * Returns true if all path commands are {@link MoveTo} or {@link CurveTo}
   * commands.
   * 
   * @return true if all path commands are moveto or curveto commands
   */
  public boolean isCurved() {
    return curved;
  }

  @Override
  public boolean isDirty() {
    return super.isDirty() || pathDirty || strokeLineCapDirty || strokeLineJoinDirty || miterLimitDirty;
  }

  /**
   * Returns true if the miter limit changed since the last render.
   * 
   * @return true if the miter limit changed since the last render
   */
  public boolean isMiterLimitDirty() {
    return miterLimitDirty;
  }

  /**
   * Returns true if the path changed since the last render.
   * 
   * @return true if the path changed since the last render
   */
  public boolean isPathDirty() {
    return pathDirty;
  }

  /**
   * Returns true if the line cap changed since the last render.
   * 
   * @return true if the line cap changed since the last render
   */
  public boolean isStrokeLineCapDirty() {
    return strokeLineCapDirty;
  }

  /**
   * Returns true if the line join changed since the last render.
   * 
   * @return true if the line join changed since the last render
   */
  public boolean isStrokeLineJoinDirty() {
    return strokeLineJoinDirty;
  }

  /**
   * Transforms the {@link PathSprite} by the passed {@link Matrix}.
   * 
   * @param matrix the transformation {@link Matrix}
   * @return the transformed {@link PathSprite}
   */
  public PathSprite map(Matrix matrix) {
    if (matrix == null) {
      return this;
    }

    PathSprite sprite = this.copy().toCurve();

    for (PathCommand command : sprite.getCommands()) {
      PrecisePoint point;
      if (command instanceof MoveTo) {
        MoveTo move = (MoveTo) command;
        point = matrix.pointMultiply(new PrecisePoint(move.getX(), move.getY()));
        move.setX(point.getX());
        move.setY(point.getY());
      } else if (command instanceof CurveTo) {
        CurveTo curve = (CurveTo) command;
        point = matrix.pointMultiply(new PrecisePoint(curve.getX1(), curve.getY1()));
        curve.setX1(point.getX());
        curve.setY1(point.getY());
        point = matrix.pointMultiply(new PrecisePoint(curve.getX2(), curve.getY2()));
        curve.setX2(point.getX());
        curve.setY2(point.getY());
        point = matrix.pointMultiply(new PrecisePoint(curve.getX(), curve.getY()));
        curve.setX(point.getX());
        curve.setY(point.getY());
      }
    }

    return sprite;
  }

  /**
   * Removes the {@link PathCommand} at the given index.
   * 
   * @param index the index of the command
   */
  public void removeCommand(int index) {
    commands.remove(index);
    pathDirty = true;
  }

  /**
   * Sets the {@link PathCommand} of the path at given index.
   * 
   * @param index the index of the command to be set
   * @param command the command to be set
   */
  public void setCommand(int index, PathCommand command) {
    assert command != null : "Cannot use a null path command.";
    if (!(command instanceof CurveTo)) {
      this.setCurved(false);
    }
    if (command.isRelative()) {
      this.setAbsolute(false);
    }
    this.commands.set(index, command);
    pathDirty = true;
  }

  /**
   * Replace the path's {@link PathCommand}s with the given {@link List} of
   * commands.
   * 
   * @param commands the new list of commands
   */
  public void setCommands(List<PathCommand> commands) {
    this.setCurved(false);
    this.setAbsolute(false);
    this.commands = commands;
    pathDirty = true;
  }

  /**
   * Sets miter limit of the path. Determines the limit on the ratio of the
   * miter length to the stroke width.
   * 
   * @param miterLimit the new miter limit of the path
   */
  public void setMiterLimit(double miterLimit) {
    if (Double.compare(this.miterLimit, miterLimit) != 0) {
      this.miterLimit = miterLimit;
      miterLimitDirty = true;
    }
  }

  /**
   * Sets the {@link LineCap} of the path. Determines the shape to be used at
   * the end of open subpaths.
   * 
   * @param strokeLineCap the line cap of the path
   */
  public void setStrokeLineCap(LineCap strokeLineCap) {
    if (this.strokeLineCap != strokeLineCap) {
      this.strokeLineCap = strokeLineCap;
      strokeLineCapDirty = true;
    }
  }

  /**
   * Sets the {@link LineJoin} of the path. Determines the shape to be used at
   * the corners of paths.
   * 
   * @param strokeLineJoin the line join of the path
   */
  public void setStrokeLineJoin(LineJoin strokeLineJoin) {
    if (this.strokeLineJoin != strokeLineJoin) {
      this.strokeLineJoin = strokeLineJoin;
      strokeLineJoinDirty = true;
    }
  }

  /**
   * Returns the number of {@link PathCommand}s added to the sprite.
   * 
   * @return the number of commands added to the sprite
   */
  public int size() {
    if (commands == null) {
      return 0;
    }
    return commands.size();
  }

  /**
   * Returns the path converted to only absolute {@link PathCommand}s.
   * 
   * @return the path converted to only absolute commands
   */
  public PathSprite toAbsolute() {
    if (this.size() < 1 || this.isAbsolute()) {
      return this;
    }
    PrecisePoint reference = new PrecisePoint();
    PrecisePoint move = new PrecisePoint();
    int start = 0;
    PathCommand command;

    command = this.getCommand(0);
    if (command instanceof MoveTo) {
      reference.setX(((MoveTo) command).getX());
      reference.setY(((MoveTo) command).getY());
      start++;
    }

    for (int i = start; i < this.size(); i++) {
      command = this.getCommand(i);
      command.toAbsolute(reference, move);
    }
    this.setAbsolute(true);
    pathDirty = true;
    return this;
  }

  /**
   * Returns the path converted to only {@link MoveTo} and {@link CurveTo}
   * commands.
   * 
   * @return the path converted to only move and curve commands
   */
  public PathSprite toCurve() {
    if (this.size() < 1 || this.isCurved()) {
      return this;
    }
    PathCommand command = null;
    PrecisePoint currentPoint = new PrecisePoint();
    PrecisePoint movePoint = new PrecisePoint();
    PrecisePoint curvePoint = new PrecisePoint();
    PrecisePoint quadraticPoint = new PrecisePoint();
    this.toAbsolute();
    int total = this.size();
    for (int i = 0; i < total; i++) {
      // Convert the command to a curve
      List<PathCommand> commands = this.getCommand(i).toCurve(currentPoint, movePoint, curvePoint, quadraticPoint);
      int j;
      for (j = 0; j < commands.size(); j++) {
        command = commands.get(j);
        if (j == 0) {
          this.setCommand(i, command);
        } else {
          this.addCommand(i + j, command);
        }
      }
      if (j > 0) {
        total += j - 1;
      }
      // move the current point of the path
      setCurrentPoint(command, currentPoint, curvePoint);
    }
    this.setCurved(true);
    pathDirty = true;
    return this;
  }

  /**
   * Returns the path smoothed by increasing the number of subsections. The
   * subdivisions is determined by the given subsections. The resultant path
   * will be made of {@link CurveTo} commands.
   * 
   * @param subsections the number of subdivisions used in the smoothing
   *          function; must be no less than 4
   * @return the smoothed path
   */
  public PathSprite toSmooth(int subsections) {
    PathSprite path = this.toCurve();
    PathCommand commandM;
    PathCommand commandP;
    PathCommand command = path.getCommand(0);
    MoveTo moveto;
    PrecisePoint[] points = new PrecisePoint[2];
    PathSprite newp = new PathSprite();
    newp.addCommand(command);
    double x = ((MoveTo) command).x;
    double y = ((MoveTo) command).y;
    int beg = 1;
    double mx = x;
    double my = y;

    for (int i = 1; i < path.size(); i++) {
      command = path.getCommand(i);
      commandM = path.getCommand(i - 1);
      if (i < path.size() - 1) {
        commandP = path.getCommand(i + 1);
      } else {
        commandP = null;
      }

      if (command instanceof MoveTo) {
        moveto = (MoveTo) command;
        x = mx = moveto.x;
        y = my = moveto.y;
        int j = i + 1;
        while (!(path.getCommand(j) instanceof CurveTo)) {
          j++;
        }
        newp.addCommand(new MoveTo(mx, my));
        beg = newp.size();
        continue;
      }

      PrecisePoint point = commandEnd(command);
      if (point.getX() == mx && point.getY() == my && (commandP == null || commandP instanceof MoveTo)) {
        PathCommand commandBeg = newp.getCommand(beg);
        PrecisePoint pointM = commandEnd(commandM);
        PrecisePoint pointBeg = commandEnd(commandBeg);
        points = getAnchors(new CurveTo(pointM.getX(), pointM.getY(), mx, my, pointBeg.getX(), pointBeg.getY()),
            subsections);
        commandEndSet(new PrecisePoint(points[1].getX(), points[1].getY()), commandBeg);
      } else if (commandP == null || commandP instanceof MoveTo) {
        points[0] = commandEnd(command);
        points[1] = new PrecisePoint(Double.NaN, Double.NaN);
      } else {
        point = commandEnd(command);
        PrecisePoint pointM = commandEnd(commandM);
        PrecisePoint pointP = commandEnd(commandP);
        points = getAnchors(
            new CurveTo(pointM.getX(), pointM.getY(), point.getX(), point.getY(), pointP.getX(), pointP.getY()),
            subsections);
      }
      point = commandEnd(command);
      newp.addCommand(new CurveTo(x, y, points[0].getX(), points[0].getY(), point.getX(), point.getY()));
      x = points[1].getX();
      y = points[1].getY();
    }
    pathDirty = true;
    return newp;
  }

  @Override
  public String toString() {
    StringBuilder build = new StringBuilder();
    for (PathCommand command : commands) {
      build.append(command.toString()).append(" ");
    }
    return build.toString();
  }

  /**
   * Determines the dimensions of the given {@link CurveTo} command. The
   * starting point of the curve must also be given.
   * 
   * @param start the starting point of the curve
   * @param curve the curve to have its dimensions calculated
   * @return the dimensions of the curve
   */
  protected PreciseRectangle curveDimensions(PrecisePoint start, CurveTo curve) {
    PrecisePoint min = new PrecisePoint(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    PrecisePoint max = new PrecisePoint(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    PrecisePoint dot;
    // solve the quadratic
    double a = (curve.getX2() - 2.0 * curve.getX1() + start.getX())
        - (curve.getX() - 2.0 * curve.getX2() + curve.getX1());
    double b = 2.0 * (curve.getX1() - start.getX()) - 2.0 * (curve.getX1() - curve.getX1());
    double c = start.getX() - curve.getX1();
    double t1 = (-b + Math.sqrt(b * b - 4.0 * a * c)) / 2.0 / a;
    double t2 = (-b - Math.sqrt(b * b - 4.0 * a * c)) / 2.0 / a;
    ArrayList<PrecisePoint> points = new ArrayList<PrecisePoint>();
    points.add(new PrecisePoint(start));
    points.add(new PrecisePoint(curve.getX(), curve.getY()));

    if (Math.abs(t1) > 1e12) {
      t1 = 0.5;
    }
    if (Math.abs(t2) > 1e12) {
      t2 = 0.5;
    }
    if (t1 > 0 && t1 < 1) {
      dot = this.findCurvePoint(start, curve, t1);
      points.add(dot);
    }
    if (t2 > 0 && t2 < 1) {
      dot = this.findCurvePoint(start, curve, t2);
      points.add(dot);
    }

    a = (curve.getY2() - 2.0 * curve.getY1() + start.getY()) - (curve.getY() - 2.0 * curve.getY2() + curve.getY1());
    b = 2 * (curve.getY1() - start.getY()) - 2 * (curve.getY2() - curve.getY1());
    c = start.getY() - curve.getY1();
    t1 = (-b + Math.sqrt(b * b - 4.0 * a * c)) / 2.0 / a;
    t2 = (-b - Math.sqrt(b * b - 4.0 * a * c)) / 2.0 / a;

    if (Math.abs(t1) > 1e12) {
      t1 = 0.5;
    }
    if (Math.abs(t2) > 1e12) {
      t2 = 0.5;
    }
    if (t1 > 0 && t1 < 1) {
      dot = this.findCurvePoint(start, curve, t1);
      points.add(dot);
    }
    if (t2 > 0 && t2 < 1) {
      dot = this.findCurvePoint(start, curve, t2);
      points.add(dot);
    }

    for (PrecisePoint p : points) {
      min.setX(Math.min(min.getX(), p.getX()));
      min.setY(Math.min(min.getY(), p.getY()));
      max.setX(Math.max(max.getX(), p.getX()));
      max.setY(Math.max(max.getY(), p.getY()));
    }

    return new PreciseRectangle(min.getX(), min.getY(), max.getX() - min.getX(), max.getY() - min.getY());
  }

  /**
   * Returns the ending coordinates of the given {@link PathCommand}.
   * 
   * @param command the command to have its end point found
   * @return the end point
   */
  private PrecisePoint commandEnd(PathCommand command) {
    PrecisePoint end = null;

    if (command instanceof MoveTo) {
      MoveTo move = (MoveTo) command;
      end = new PrecisePoint(move.getX(), move.getY());
    } else if (command instanceof LineTo) {
      LineTo line = (LineTo) command;
      end = new PrecisePoint(line.getX(), line.getY());
    } else if (command instanceof CurveTo) {
      CurveTo curve = (CurveTo) command;
      end = new PrecisePoint(curve.getX(), curve.getY());
    } else if (command instanceof EllipticalArc) {
      EllipticalArc arc = (EllipticalArc) command;
      end = new PrecisePoint(arc.getX(), arc.getY());
    }

    return end;
  }

  /**
   * Sets the given point to the end of the given command.
   * 
   * @param point the point to be set at the end of the command
   * @param command the command to have its end set
   */
  private void commandEndSet(PrecisePoint point, PathCommand command) {
    if (command instanceof MoveTo) {
      MoveTo move = (MoveTo) command;
      move.setX(point.getX());
      move.setY(point.getY());
    } else if (command instanceof LineTo) {
      LineTo line = (LineTo) command;
      line.setX(point.getX());
      line.setY(point.getY());
    } else if (command instanceof CurveTo) {
      CurveTo curve = (CurveTo) command;
      curve.setX(point.getX());
      curve.setY(point.getY());
    } else if (command instanceof EllipticalArc) {
      EllipticalArc arc = (EllipticalArc) command;
      arc.setX(point.getX());
      arc.setY(point.getY());
    }
  }

  /**
   * Converts an ellipse to a set of {@link EllipticalArc} commands.
   * 
   * @param x the x-coordinate of the ellipse
   * @param y the y-coordinate of the ellipse
   * @param rx the radius of the ellipse on its x-axis
   * @param ry the radius of the ellipse on its y-axis
   * @return the set of commands
   */
  private ArrayList<PathCommand> ellipseCommands(double x, double y, double rx, double ry) {
    ArrayList<PathCommand> ellipse = new ArrayList<PathCommand>();
    if (Double.isNaN(x)) {
      x = 0;
    }
    if (Double.isNaN(y)) {
      y = 0;
    }
    if (Double.isNaN(rx)) {
      rx = 0;
    }
    if (Double.isNaN(ry)) {
      ry = 0;
    }
    ellipse.add(new MoveTo(x, y));
    ellipse.add(new MoveTo(0, -ry, true));
    ellipse.add(new EllipticalArc(rx, ry, 0, 1, 1, 0, 2.0 * ry, true));
    ellipse.add(new EllipticalArc(rx, ry, 0, 1, 1, 0, -2.0 * ry, true));
    ellipse.add(new ClosePath(true));
    return ellipse;
  }

  /**
   * Finds the point on the curve given the time t using the cubic bezier
   * equation.
   * 
   * @param start the starting point of the curve
   * @param curve the curve to get the point from
   * @param t the time index of the point
   * @return the point on the curve
   */
  private PrecisePoint findCurvePoint(PrecisePoint start, CurveTo curve, double t) {
    // Math is from
    // http://en.wikipedia.org/wiki/B%C3%A9zier_curve#Cubic_B.C3.A9zier_curves
    double t1 = 1.0 - t;
    double x = Math.pow(t1, 3) * start.getX() + Math.pow(t1, 2) * 3 * t * curve.getX1() + t1 * 3 * t * t
        * curve.getX2() + Math.pow(t, 3) * curve.getX();
    double y = Math.pow(t1, 3) * start.getY() + Math.pow(t1, 2) * 3 * t * curve.getY1() + t1 * 3 * t * t
        * curve.getY2() + Math.pow(t, 3) * curve.getY();
    return new PrecisePoint(x, y);
  }

  /**
   * Calculates new anchor points for the segment given the number of
   * subsections.
   * 
   * @param curve the curve to be subdivided
   * @param subsections number of subsections in the new segment; must be no
   *          less than 4
   * @return the subdivided segment points
   */
  private PrecisePoint[] getAnchors(CurveTo curve, double subsections) {
    if (subsections < 4) subsections = 4;
    // Find the length of each control anchor line, by dividing the horizontal
    // distance
    // between points by the value parameter.
    double control1Length = (curve.getX2() - curve.getX1()) / subsections;
    double control2Length = (curve.getX() - curve.getX2()) / subsections;
    double control1Angle = 0;
    double control2Angle = 0;
    double control1X = 0;
    double control1Y = 0;
    double control2X = 0;
    double control2Y = 0;

    // Determine the angle of each control anchor line. If the middle point is a
    // vertical
    // turnaround then we force it to a flat horizontal angle to prevent the
    // curve from
    // dipping above or below the middle point. Otherwise we use an angle that
    // points
    // toward the previous/next target point.
    if ((curve.getY2() >= curve.getY1() && curve.getY2() >= curve.getY())
        || (curve.getY2() <= curve.getY1() && curve.getY2() <= curve.getY())) {
      control1Angle = control2Angle = Math.PI / 2;
    } else {
      control1Angle = Math.atan((curve.getX2() - curve.getX1()) / Math.abs(curve.getY2() - curve.getY1()));
      if (curve.getY1() < curve.getY2()) {
        control1Angle = Math.PI - control1Angle;
      }
      control2Angle = Math.atan((curve.getX() - curve.getX2()) / Math.abs(curve.getY2() - curve.getY()));
      if (curve.getY() < curve.getY2()) {
        control2Angle = Math.PI - control2Angle;
      }
    }

    // Adjust the calculated angles so they point away from each other on the
    // same line
    double alpha = (Math.PI / 2) - ((control1Angle + control2Angle) % (Math.PI * 2)) / 2;
    if (alpha > Math.PI / 2) {
      alpha -= Math.PI;
    }
    control1Angle += alpha;
    control2Angle += alpha;

    // Find the control anchor points from the angles and length
    control1X = curve.getX2() - control1Length * Math.sin(control1Angle);
    control1Y = curve.getY2() + control1Length * Math.cos(control1Angle);
    control2X = curve.getX2() + control2Length * Math.sin(control2Angle);
    control2Y = curve.getY2() + control2Length * Math.cos(control2Angle);

    // One last adjustment, make sure that no control anchor point extends
    // vertically past
    // its target prev/next point, as that results in curves dipping above or
    // below and
    // bending back strangely. If we find this happening we keep the control
    // angle but
    // reduce the length of the control line so it stays within bounds.
    if ((curve.getY2() > curve.getY1() && control1Y < curve.getY1())
        || (curve.getY2() < curve.getY1() && control1Y > curve.getY1())) {
      control1X += Math.abs(curve.getY1() - control1Y) * (control1X - curve.getX2()) / (control1Y - curve.getY2());
      control1Y = curve.getY1();
    }
    if ((curve.getY2() > curve.getY() && control2Y < curve.getY())
        || (curve.getY2() < curve.getY() && control2Y > curve.getY())) {
      control2X -= Math.abs(curve.getY() - control2Y) * (control2X - curve.getX2()) / (control2Y - curve.getY2());
      control2Y = curve.getY();
    }

    PrecisePoint[] points = {new PrecisePoint(control1X, control1Y), new PrecisePoint(control2X, control2Y)};
    return points;
  }

  /**
   * Converts the given rectangle to a set of {@link PathCommand}s.
   * 
   * @param rect the rectangle to be converted
   * @param radius the radius of the rectangles corners
   * @return the converted path commands
   */
  private ArrayList<PathCommand> rectangleCommands(PreciseRectangle rect, double radius) {
    ArrayList<PathCommand> path = new ArrayList<PathCommand>();
    if (!Double.isNaN(radius)) {
      path.add(new MoveTo(rect.getX() + radius, rect.getY()));
      path.add(new LineTo(rect.getWidth() - radius * 2, 0, true));
      path.add(new EllipticalArc(radius, radius, 0, 0, 1, radius, radius, true));
      path.add(new LineTo(0, rect.getHeight() - radius * 2, true));
      path.add(new EllipticalArc(radius, radius, 0, 0, 1, -radius, radius, true));
      path.add(new LineTo(radius * 2 - rect.getWidth(), 0, true));
      path.add(new EllipticalArc(radius, radius, 0, 0, 1, -radius, -radius, true));
      path.add(new LineTo(0, radius * 2 - rect.getHeight(), true));
      path.add(new EllipticalArc(radius, radius, 0, 0, 1, radius, -radius, true));
      path.add(new ClosePath(true));
    } else {
      path.add(new MoveTo(rect.getX(), rect.getY()));
      path.add(new LineTo(rect.getWidth(), 0, true));
      path.add(new LineTo(0, rect.getHeight(), true));
      path.add(new LineTo(-rect.getWidth(), 0, true));
      path.add(new ClosePath(true));
    }
    return path;
  }

  private void setAbsolute(boolean absolute) {
    this.absolute = absolute;
  }

  private void setCurved(boolean curved) {
    this.curved = curved;
  }
}
