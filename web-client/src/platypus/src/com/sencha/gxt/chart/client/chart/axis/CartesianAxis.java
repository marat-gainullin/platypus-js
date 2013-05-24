/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.axis;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.Rotation;
import com.sencha.gxt.chart.client.draw.Surface;
import com.sencha.gxt.chart.client.draw.path.ClosePath;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathCommand;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextBaseline;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;

/**
 * An axis in Cartesian coordinates.
 * 
 * @param <M> the data type of the axis
 * @param <V> the variable type of axis
 */
public abstract class CartesianAxis<M, V> extends Axis<M, V> {

  private int textLabelPadding = 10;
  private int dashSize = 3;
  protected Position position = Position.BOTTOM;
  /**
   * The number of small ticks between two major ticks. Default is zero.
   */
  protected int minorTickSteps = 0;
  private double labelTolerance = 0;
  private boolean labelOverlapHiding = false;
  private int labelStepRatio = 1;
  private PreciseRectangle axisBBox = new PreciseRectangle();

  private PathSprite gridLines;
  private PathSprite defaultGridConfig = new PathSprite();
  private PathSprite gridOdd;
  private PathSprite gridOddConfig;
  private PathSprite gridEven;
  private PathSprite gridEvenConfig;

  protected double power;
  protected double step = Double.NaN;

  protected List<PrecisePoint> ticks = new ArrayList<PrecisePoint>();
  private double depth = 0;
  private double length = 0;
  private double width = 0;
  private double height = 0;

  /**
   * Create a Cartesian axis.
   */
  public CartesianAxis() {
    defaultGridConfig.setStroke(new RGB(204, 204, 204));
    defaultGridConfig.setZIndex(1);
    labelConfig.setTextAnchor(TextAnchor.MIDDLE);
    labelConfig.setTextBaseline(TextBaseline.MIDDLE);
  }

  @Override
  public void clear() {
    super.clear();
    if (gridLines != null) {
      gridLines.remove();
      gridLines = null;
    }
    if (gridEven != null) {
      gridEven.remove();
      gridEven = null;
    }
    if (gridOdd != null) {
      gridOdd.remove();
      gridOdd = null;
    }
  }

  @Override
  public void drawAxis(boolean init) {
    double currentX = 0;
    double currentY = 0;
    double dashesX = 0;
    double dashesY = 0;
    double gutterX = chart.getMaxGutter()[0];
    double gutterY = chart.getMaxGutter()[1];

    List<PathCommand> commands = new ArrayList<PathCommand>();
    double trueLength;
    double delta;
    ticks = new ArrayList<PrecisePoint>();
    applyData();

    // If not steps are specified then don't draw the axis.
    // This generally happens when the store is empty.
    if (hidden || chart.getCurrentStore().size() == 0 || from == to) {
      return;
    }

    if (position == Position.LEFT || position == Position.RIGHT) {
      currentX = Math.floor(this.x) + 0.5;
      commands.add(new MoveTo(currentX, y));
      commands.add(new LineTo(0, -length, true));
      trueLength = length - (gutterY * 2);
    } else {
      currentY = Math.floor(this.y) + 0.5;
      commands.add(new MoveTo(x, currentY));
      commands.add(new LineTo(length, 0, true));
      trueLength = length - (gutterX * 2);
    }

    delta = trueLength / steps;
    dashesX = Math.max(minorTickSteps + 1, 0);
    dashesY = Math.max(minorTickSteps + 1, 0);

    if (position == Position.LEFT || position == Position.RIGHT) {
      currentY = y - gutterY;
      if (position == Position.LEFT) {
        currentX = x - dashSize * 2;
      } else {
        currentX = x;
      }
      while (currentY >= y - gutterY - trueLength) {
        commands.add(new MoveTo(currentX, Math.floor(currentY) + 0.5));
        commands.add(new LineTo(dashSize * 2 + 1, 0, true));
        if (currentY != y - gutterY) {
          for (int i = 1; i < dashesY; i++) {
            commands.add(new MoveTo(currentX + dashSize, Math.floor(currentY + delta * i / dashesY) + 0.5));
            commands.add(new LineTo(dashSize + 1, 0, true));
          }
        }
        ticks.add(new PrecisePoint(Math.floor(x), Math.floor(currentY)));
        currentY -= delta;
        if (delta == 0) {
          break;
        }
      }
      if (Math.round(currentY + delta - (y - gutterY - trueLength)) != 0) {
        commands.add(new MoveTo(currentX, Math.floor(y - length + gutterY) + 0.5));
        commands.add(new LineTo(dashSize * 2 + 1, 0, true));
        for (int i = 1; i < dashesY; i++) {
          commands.add(new MoveTo(currentX + dashSize, Math.floor(currentY + delta * i / dashesY) + 0.5));
          commands.add(new LineTo(dashSize + 1, 0, true));
        }
        ticks.add(new PrecisePoint(Math.floor(x), Math.floor(currentY)));
      }
    } else {
      currentX = x + gutterX;
      if (position == Position.BOTTOM) {
        currentY = y - dashSize * 2;
      } else {
        currentY = y - dashSize * 4;
      }
      while (currentX <= x + gutterX + trueLength) {
        commands.add(new MoveTo(Math.floor(currentX) + 0.5, currentY + 6));
        commands.add(new LineTo(0, dashSize * 2 + 1, true));
        if (currentX != x + gutterX) {
          for (int i = 1; i < dashesX; i++) {
            commands.add(new MoveTo(Math.floor(currentX - delta * i / dashesX) + 0.5, currentY + 6));
            commands.add(new LineTo(0, dashSize + 1, true));
          }
        }
        ticks.add(new PrecisePoint(Math.floor(currentX), Math.floor(y)));
        currentX += delta;
        if (delta == 0) {
          break;
        }
      }
      if (Math.round(currentX - delta - (x + gutterX + trueLength)) != 0) {
        commands.add(new MoveTo(Math.floor(x + length - gutterX) + 0.5, currentY + 6));
        commands.add(new LineTo(0, dashSize * 2 + 1, true));
        for (int i = 1; i < dashesX; i++) {
          commands.add(new MoveTo(Math.floor(x + length - gutterX - delta * i / dashesX) + 0.5, currentY + 6));
          commands.add(new LineTo(0, dashSize + 1, true));
        }
        ticks.add(new PrecisePoint(Math.floor(currentX), Math.floor(y)));
      }
    }

    createLabels();

    final PathSprite axis;
    if (lines.size() == 0) {
      axis = axisConfig.copy();
      lines.add(axis);
      chart.addSprite(axis);
    } else {
      axis = lines.get(0);
    }
    axis.setCommands(commands);
    axis.redraw();

    // drawGrid
    if (!init && displayGrid) {
      drawGrid();
    }
    axisBBox = axis.getBBox();
    drawLabels();
  }

  @Override
  public void drawLabels() {
    double maxWidth = 0;
    double maxHeight = 0;
    if (position == Position.LEFT || position == Position.RIGHT) {
      if (position == Position.LEFT) {
        labelConfig.setTextAnchor(TextAnchor.END);
      } else {
        labelConfig.setTextAnchor(TextAnchor.START);
      }
      maxWidth = drawVerticalLabels();
    } else {
      labelConfig.setTextBaseline(TextBaseline.TOP);
      maxHeight = drawHorizontalLabels();
    }

    // Hide unused labels
    int ln = labels.size();
    int i = (int) Math.ceil(ticks.size() / (double) labelStepRatio);
    if ((ticks.size() - 1) % labelStepRatio != 0) {
      i++;
    }
    for (; i < ln; i++) {
      labels.get(i).setHidden(true);
      labels.get(i).redraw();
    }

    bbox = new PreciseRectangle();
    bbox.setX(axisBBox.getX());
    bbox.setY(axisBBox.getY());
    bbox.setHeight(maxHeight);
    bbox.setWidth(maxWidth);

    if (titleConfig != null) {
      drawTitle(maxWidth, maxHeight);
    }
  }

  /**
   * Returns the dash size of the axis.
   * 
   * @return the dash size of the axis
   */
  public int getDashSize() {
    return dashSize;
  }

  /**
   * Returns the depth of the axis.
   * 
   * @return the depth of the axis
   */
  public double getDepth() {
    return depth;
  }

  /**
   * Returns the config for the default axis grid.
   * 
   * @return the config for the default axis grid
   */
  public PathSprite getGridDefaultConfig() {
    return defaultGridConfig;
  }

  /**
   * Returns the config for the grid on the even indices.
   * 
   * @return the config for the grid on the even indices
   */
  public PathSprite getGridEvenConfig() {
    return gridEvenConfig;
  }

  /**
   * Returns the config for the grid on the odd indices.
   * 
   * @return the config for the grid on the odd indices
   */
  public PathSprite getGridOddConfig() {
    return gridOddConfig;
  }

  /**
   * Returns the height of the axis.
   * 
   * @return the height of the axis
   */
  public double getHeight() {
    return height;
  }

  /**
   * Returns the number of tick marks per label.
   * 
   * @return the number of tick marks per label
   */
  public int getLabelStepRatio() {
    return labelStepRatio;
  }

  /**
   * Returns the intersection tolerance of labels on the axis.
   * 
   * @return the intersection tolerance of labels on the axis
   */
  public double getLabelTolerance() {
    return labelTolerance;
  }

  /**
   * Returns the length of the axis.
   * 
   * @return the length of the axis
   */
  public double getLength() {
    return length;
  }

  /**
   * Returns the number of ticks between major ticks.
   * 
   * @return the number of ticks between major ticks
   */
  public int getMinorTickSteps() {
    return minorTickSteps;
  }

  @Override
  public Position getPosition() {
    return position;
  }

  /**
   * Returns the padding between labels on the axis.
   * 
   * @return the padding between labels on the axis
   */
  public int getTextLabelPadding() {
    return textLabelPadding;
  }

  /**
   * Returns the width of the axis.
   * 
   * @return the width of the axis
   */
  public double getWidth() {
    return width;
  }

  /**
   * Returns whether or not the axis uses label hiding.
   * 
   * @return whether or not the axis uses label hiding
   */
  public boolean isLabelOverlapHiding() {
    return labelOverlapHiding;
  }

  /**
   * Sets the dash size of the axis.
   * 
   * @param dashSize the dash size of the axis
   */
  public void setDashSize(int dashSize) {
    this.dashSize = dashSize;
  }

  /**
   * Sets the depth of the axis.
   * 
   * @param depth the depth of the axis
   */
  public void setDepth(double depth) {
    this.depth = depth;
  }

  /**
   * Sets the config for the default axis grid.
   * 
   * @param defaultGridConfig the config for the default axis grid
   */
  public void setGridDefaultConfig(PathSprite defaultGridConfig) {
    if (this.defaultGridConfig != defaultGridConfig) {
      this.defaultGridConfig = defaultGridConfig;
      if (gridLines != null) {
        gridLines.remove();
        gridLines = null;
      }
    }
  }

  /**
   * Sets the config for the grid on the even indices. Overrides the default
   * grid.
   * 
   * @param gridEvenConfig the config for the grid on the even indices
   */
  public void setGridEvenConfig(PathSprite gridEvenConfig) {
    if (this.gridEvenConfig != gridEvenConfig) {
      this.gridEvenConfig = gridEvenConfig;
      if (gridEven != null) {
        gridEven.remove();
        gridEven = null;
      }
    }
  }

  /**
   * Sets the config for the grid on the odd indices. Overrides the default
   * grid.
   * 
   * @param gridOddConfig the config for the grid on the odd indices
   */
  public void setGridOddConfig(PathSprite gridOddConfig) {
    if (this.gridOddConfig != gridOddConfig) {
      this.gridOddConfig = gridOddConfig;
      if (gridOdd != null) {
        gridOdd.remove();
        gridOdd = null;
      }
    }
  }

  /**
   * Sets the height of the axis.
   * 
   * @param height the height of the axis
   */
  public void setHeight(double height) {
    this.height = height;
  }

  /**
   * Sets whether or not the axis uses label hiding. Defaults to false.
   * 
   * @param labelOverlapHiding whether or not the axis uses label hiding
   */
  public void setLabelOverlapHiding(boolean labelOverlapHiding) {
    this.labelOverlapHiding = labelOverlapHiding;
  }

  /**
   * Sets the padding between labels on the axis.
   * 
   * @param textLabelPadding the padding between labels on the axis
   */
  public void setLabelPadding(int textLabelPadding) {
    this.textLabelPadding = textLabelPadding;
  }

  /**
   * Sets the number of tick marks per label. Ratio needs to be greater than 0.
   * The last step label of an axis will always be drawn even the label step
   * ratio is not a divisor of the last array index.
   * 
   * @param labelStepRatio the number of tick marks per label
   */
  public void setLabelStepRatio(int labelStepRatio) {
    assert labelStepRatio > 0 : "Label step ratio must be greater than zero.";
    this.labelStepRatio = labelStepRatio;
  }

  /**
   * Sets the intersection tolerance of labels on the axis.
   * 
   * @param labelTolerance the intersection tolerance of labels on the axis
   */
  public void setLabelTolerance(double labelTolerance) {
    this.labelTolerance = labelTolerance;
  }

  /**
   * Sets the length of the axis.
   * 
   * @param length the length of the axis
   */
  public void setLength(double length) {
    this.length = length;
  }

  /**
   * Sets the number of ticks between major ticks.
   * 
   * @param minorTickSteps the number of ticks between major ticks
   */
  public void setMinorTickSteps(int minorTickSteps) {
    this.minorTickSteps = minorTickSteps;
  }

  /**
   * Sets the {@link Position} of the axis.
   * 
   * @param position the position of the axis
   */
  public void setPosition(Position position) {
    this.position = position;
  }

  /**
   * Sets the width of the axis.
   * 
   * @param width the width of the axis
   */
  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * Applies the field data to the labels of the axis.
   */
  protected abstract void applyData();

  /**
   * Creates the list of labels to display.
   */
  protected abstract void createLabels();

  /**
   * Renders an horizontal and/or vertical grid into the {@link Surface}.
   */
  private void drawGrid() {
    double[] gutters = chart.getMaxGutter();
    int ln = ticks.size();
    int i = 1;
    double width = this.depth - 2;
    double lineWidth;
    List<PathCommand> commands = new ArrayList<PathCommand>();
    List<PathCommand> oddCommands = new ArrayList<PathCommand>();
    List<PathCommand> evenCommands = new ArrayList<PathCommand>();
    Sprite styles;

    if ((gutters[1] != 0 && (position == Position.LEFT || position == Position.RIGHT))
        || (gutters[0] != 0 && (position == Position.TOP || position == Position.BOTTOM))) {
      i = 0;
    }
    if (gridOddConfig != null || gridEvenConfig != null) {
      i = 1;
    }

    for (; i < ln; i++) {
      PrecisePoint point = ticks.get(i);
      PrecisePoint prevPoint;
      if (i > 0) {
        prevPoint = ticks.get(i - 1);
      } else {
        prevPoint = new PrecisePoint();
      }
      if (gridOddConfig != null || gridEvenConfig != null) {
        styles = ((i % 2 != 0) ? gridOddConfig : gridEvenConfig);
        if (styles != null) {
          lineWidth = styles.getStrokeWidth() / 2;
        } else {
          lineWidth = 0;
        }
        commands = (i % 2 == 0 ? evenCommands : oddCommands);
        if (position == Position.LEFT) {
          commands.add(new MoveTo(prevPoint.getX() + 1 + lineWidth, prevPoint.getY() + 0.5 - lineWidth));
          commands.add(new LineTo(prevPoint.getX() + 1 + width - lineWidth, prevPoint.getY() + 0.5 - lineWidth));
          commands.add(new LineTo(point.getX() + 1 + width - lineWidth, point.getY() + 0.5 + lineWidth));
          commands.add(new LineTo(point.getX() + 1 + lineWidth, point.getY() + 0.5 + lineWidth));
          commands.add(new ClosePath());

        } else if (position == Position.RIGHT) {
          commands.add(new MoveTo(prevPoint.getX() - lineWidth, prevPoint.getY() + 0.5 - lineWidth));
          commands.add(new LineTo(prevPoint.getX() - width + lineWidth, prevPoint.getY() + 0.5 - lineWidth));
          commands.add(new LineTo(point.getX() - width + lineWidth, point.getY() + 0.5 + lineWidth));
          commands.add(new LineTo(point.getX() - lineWidth, point.getY() + 0.5 + lineWidth));
          commands.add(new ClosePath());
        } else if (position == Position.TOP) {
          commands.add(new MoveTo(prevPoint.getX() + 0.5 + lineWidth, prevPoint.getY() + 0.5 - lineWidth));
          commands.add(new LineTo(prevPoint.getX() + 0.5 + lineWidth, prevPoint.getY() + 1 + width - lineWidth));
          commands.add(new LineTo(point.getX() + 0.5 - lineWidth, point.getY() + 1 + width - lineWidth));
          commands.add(new LineTo(point.getX() + 0.5 - lineWidth, point.getY() + 1 + lineWidth));
          commands.add(new ClosePath());
        } else {
          commands.add(new MoveTo(prevPoint.getX() + 0.5 + lineWidth, prevPoint.getY() + 1 + lineWidth));
          commands.add(new LineTo(prevPoint.getX() + 0.5 + lineWidth, prevPoint.getY() - width + lineWidth));
          commands.add(new LineTo(point.getX() + 0.5 - lineWidth, point.getY() - width + lineWidth));
          commands.add(new LineTo(point.getX() + 0.5 - lineWidth, point.getY() - lineWidth));
          commands.add(new ClosePath());
        }
      } else if (defaultGridConfig != null) {
        if (position == Position.LEFT) {
          commands.add(new MoveTo(point.getX() + 0.5, point.getY() + 0.5));
          commands.add(new LineTo(width, 0, true));
        } else if (position == Position.RIGHT) {
          commands.add(new MoveTo(point.getX() + 0.5, point.getY() + 0.5));
          commands.add(new LineTo(-width, 0, true));
        } else if (position == Position.TOP) {
          commands.add(new MoveTo(point.getX() + 0.5, point.getY() + 0.5));
          commands.add(new LineTo(0, width, true));
        } else {
          commands.add(new MoveTo(point.getX() + 0.5, point.getY() - 0.5));
          commands.add(new LineTo(0, -width, true));
        }
      }
    }
    // odd path
    if (oddCommands.size() > 0 && gridOddConfig != null) {
      if (gridOdd == null) {
        gridOdd = gridOddConfig.copy();
        chart.addSprite(gridOdd);
      }
      gridOdd.setCommands(oddCommands);
      if (gridOdd.getFill() == null) gridOdd.setFill(Color.NONE);
      gridOdd.redraw();
    }
    // even path
    else if (evenCommands.size() > 0 && gridEvenConfig != null) {
      if (gridEven == null) {
        gridEven = gridEvenConfig.copy();
        chart.addSprite(gridEven);
      }
      gridEven.setCommands(evenCommands);
      if (gridEven.getFill() == null) gridEven.setFill(Color.NONE);
      gridEven.redraw();
    }
    // standard grid
    else if (commands.size() > 0 && defaultGridConfig != null) {
      if (gridLines == null) {
        gridLines = defaultGridConfig.copy();
        chart.addSprite(gridLines);
      }
      gridLines.setCommands(commands);
      gridLines.redraw();
    }
  }

  /**
   * Renders labels on a horizontal axis and returns the height
   * 
   * @return the height
   */
  private double drawHorizontalLabels() {
    PrecisePoint point;
    PreciseRectangle bbox = new PreciseRectangle();
    PreciseRectangle previousBBox = null;
    TextSprite textLabel = null;
    double x = 0;
    double y = 0;
    double maxHeight = height;
    boolean vertical = false;
    double furthestLabel = 0;
    int horizontalLabelPadding = textLabelPadding;
    Rotation labelRotation = labelConfig.getRotation();

    if (labelRotation != null) {
      vertical = (Math.floor((labelRotation.getDegrees() - 90) % 180) == 0);
    }

    int numberOfLabels = calculateNumberOfLabels();
    for (int i = 0; i < numberOfLabels; i++) {
      point = i * labelStepRatio < ticks.size() ? ticks.get(i * labelStepRatio) : ticks.get(ticks.size() - 1);
      textLabel = getOrCreateLabel(i);

      double height = textLabel.getFontSize();

      if (labelRotation != null) {
        textLabel.redraw();
        bbox = textLabel.getBBox();
        height = bbox.getHeight();
        if (!vertical) {
          x = Math.floor(point.getX() - (height / 2.0));
        } else {
          x = Math.floor(point.getX() - (bbox.getWidth() / 2.0));
        }
      } else {
        x = point.getX();
      }

      maxHeight = Math.max(maxHeight, height + dashSize + horizontalLabelPadding);

      if (position == Position.TOP) {
        y = Math.floor(point.getY() - dashSize - horizontalLabelPadding - height);
      } else {
        y = Math.floor(point.getY() + dashSize + horizontalLabelPadding);
        if (labelRotation != null) {
          y += height;
        }
      }

      textLabel.setHidden(false);
      textLabel.setX(x);
      textLabel.setY(y);
      if (labelRotation != null) {
        textLabel.setRotation(new Rotation(x, y, labelRotation.getDegrees()));
      }
      textLabel.redraw();

      // Skip label if there isn't available minimum space
      if (labelOverlapHiding) {
        bbox = textLabel.getBBox();
        if (previousBBox != null) {
          furthestLabel = Math.max(furthestLabel, previousBBox.getX() + previousBBox.getWidth());
          if (bbox.getX() <= furthestLabel - labelTolerance) {
            textLabel.setHidden(true);
            textLabel.redraw();
            continue;
          }
        } else {
          furthestLabel = bbox.getX();
        }
        previousBBox = bbox;
      }
    }
    return maxHeight;
  }

  /**
   * Renders the title of the axis.
   */
  private void drawTitle(double maxWidth, double maxHeight) {
    double x = this.x;
    double y = this.y;
    PreciseRectangle bbox = new PreciseRectangle();
    boolean rotate = (position == Position.LEFT || position == Position.RIGHT);
    if (titleSprite == null) {
      titleSprite = titleConfig.copy();
      if (rotate) {
        titleSprite.setRotation(270);
      }
      chart.addSprite(titleSprite);
    }
    titleSprite.redraw();
    bbox = titleSprite.getBBox();

    int pad = dashSize + Math.abs(textLabelPadding);
    if (rotate) {
      y -= this.length / 2.0 - bbox.getHeight() / 2.0;
      if (position == Position.LEFT) {
        x -= (maxWidth + textLabelPadding + bbox.getWidth());
      } else {
        x += (maxWidth + textLabelPadding - (bbox.getWidth() / 2.0));
      }
      this.bbox.setWidth(this.bbox.getWidth() + bbox.getWidth());
      this.bbox.setHeight(chart.getBBox().getHeight());
    } else {
      x += (this.length / 2.0) - (bbox.getWidth() / 2.0);
      if (position == Position.TOP) {
        y -= (maxHeight + pad + bbox.getHeight());
      } else {
        y += (maxHeight + pad - (bbox.getHeight() / 2.0));
      }
      this.bbox.setHeight(this.bbox.getHeight() + bbox.getHeight());
      this.bbox.setWidth(chart.getBBox().getWidth());
    }
    titleSprite.setTranslation(x, y);
    titleSprite.redraw();
  }

  /**
   * Renders labels on a vertical axis and returns the width.
   * 
   * @return the width
   */
  private double drawVerticalLabels() {
    PrecisePoint point;
    PreciseRectangle bbox = new PreciseRectangle();
    PreciseRectangle previousBBox = null;
    TextSprite textLabel = null;
    double x = 0;
    double y = 0;
    double maxWidth = width;
    double furthestLabel = chart.getBBox().getHeight();

    int numberOfLabels = calculateNumberOfLabels();
    for (int i = 0; i < numberOfLabels; i++) {
      point = i * labelStepRatio < ticks.size() ? ticks.get(i * labelStepRatio) : ticks.get(ticks.size() - 1);
      textLabel = getOrCreateLabel(i);

      textLabel.redraw();
      bbox = textLabel.getBBox();
      maxWidth = Math.max(maxWidth, bbox.getWidth() + dashSize + textLabelPadding);

      y = point.getY();
      if (position == Position.LEFT) {
        x = point.getX() - dashSize - textLabelPadding;
      } else {
        x = point.getX() + dashSize + textLabelPadding;
      }

      textLabel.setHidden(false);
      textLabel.setX(x);
      textLabel.setY(y);
      textLabel.redraw();

      // Skip label if there isn't available minimum space
      if (labelOverlapHiding) {
        bbox = textLabel.getBBox();
        if (previousBBox != null) {
          furthestLabel = Math.min(furthestLabel, previousBBox.getY());
          if (bbox.getY() + bbox.getHeight() >= furthestLabel + labelTolerance) {
            textLabel.setHidden(true);
            textLabel.redraw();
            continue;
          }
        } else {
          furthestLabel = bbox.getY();
        }
        previousBBox = bbox;
      }
    }
    return maxWidth;
  }

  /**
   * Determines the number of labels to be drawn. When labelStepRatio is used
   * this method guarantees that enough labels will be drawn to encompass the
   * last tick.
   * 
   * @return the number of labels to be used
   */
  private int calculateNumberOfLabels() {
    return (ticks.size() - 2 + labelStepRatio) / labelStepRatio + 1;
  }

  /**
   * Returns the label at the given index. If none exists a new one is
   * generated.
   * 
   * @param index the index of the label
   * @return the label at the index
   */
  private TextSprite getOrCreateLabel(int index) {
    final TextSprite textLabel;

    // Re-use existing textLabel or create a new one
    if (index < labels.size()) {
      textLabel = labels.get(index);
    } else {
      textLabel = labelConfig.copy();
      labels.add(textLabel);
      chart.addSprite(textLabel);
    }
    textLabel.setX(0);
    textLabel.setY(0);
    int labelNameIndex;
    if (index * labelStepRatio < labelNames.size()) {
      labelNameIndex = index * labelStepRatio;
    } else {
      labelNameIndex = labelNames.size() - 1;
    }
    textLabel.setText(labelProvider.getLabel(labelNames.get(labelNameIndex)));

    return textLabel;
  }

}
