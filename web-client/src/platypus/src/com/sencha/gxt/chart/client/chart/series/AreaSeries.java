/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.axis.Axis;
import com.sencha.gxt.chart.client.draw.DrawFx;
import com.sencha.gxt.chart.client.draw.path.EndPointCommand;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathCommand;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteList;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.data.shared.ListStore;

/**
 * Creates a Stacked Area Chart. The stacked area chart is useful when
 * displaying multiple aggregated layers of information.
 * 
 * Here is an example area configuration:
 * 
 * <pre>
    AreaSeries<Data> series = new AreaSeries<Data>();
    series.setYAxisPosition(Position.LEFT);
    series.addYField(dataAccess.data1());
    series.addYField(dataAccess.data2());
    series.addYField(dataAccess.data3());
    series.addYField(dataAccess.data4());
    series.addYField(dataAccess.data5());
    series.addYField(dataAccess.data6());
    series.addYField(dataAccess.data7());
    series.addColor(new RGB(148, 174, 10));
    series.addColor(new RGB(17, 95, 166));
    series.addColor(new RGB(166, 17, 32));
    series.addColor(new RGB(255, 136, 9));
    series.addColor(new RGB(255, 209, 62));
    series.addColor(new RGB(166, 17, 135));
    series.addColor(new RGB(36, 173, 154));
    chart.addSeries(series);
 * </pre>
 * 
 * First the series is created with its associated data type. The y-axis
 * position is set to tell the series the scale of the displayed axis. Otherwise
 * the series will use its own internal scale. Next the value provider fields
 * are added, which represent each layer of the area. A color is also added to
 * represent each field in the area. Finally the series is added to the chart
 * where it will be displayed.
 * 
 * @param <M> the data type used by this series
 */
public class AreaSeries<M> extends MultipleColorSeries<M> {

  private double xScale;
  private double yScale;
  private PrecisePoint min;
  private PrecisePoint max;
  private SpriteList<PathSprite> areas = new SpriteList<PathSprite>();
  private Set<Integer> exclude = new HashSet<Integer>();
  private List<Double> xValues = new ArrayList<Double>();
  private List<double[]> yValues = new ArrayList<double[]>();
  private Map<Integer, ArrayList<PathCommand>> areasCommands = new HashMap<Integer, ArrayList<PathCommand>>();
  private List<ValueProvider<? super M, ? extends Number>> yFields = new ArrayList<ValueProvider<? super M, ? extends Number>>();
  private PathSprite highlightLine;
  private PathSprite highlightLineConfig;
  private boolean highlightLineAttached;
  private int storeIndex;
  private PrecisePoint lineBottom;
  private PrecisePoint lineTop;
  private Map<Integer, ArrayList<PrecisePoint>> pointsUp = new HashMap<Integer, ArrayList<PrecisePoint>>();
  private Map<Integer, ArrayList<PrecisePoint>> pointsDown = new HashMap<Integer, ArrayList<PrecisePoint>>();
  private Position yAxisPosition;
  private Position xAxisPosition;

  /**
   * Creates an area {@link Series}.
   */
  public AreaSeries() {
    setHighlighter(new AreaHighlighter());
  }

  /**
   * Adds a data field for the y axis of the series.
   * 
   * @param index the index to have the yField inserted
   * @param yField the value provider for the data on the y axis
   */
  public void addYField(int index, ValueProvider<? super M, ? extends Number> yField) {
    this.yFields.add(index, yField);
  }

  /**
   * Adds a data field for the y axis of the series.
   * 
   * @param yField the value provider for the data on the y axis
   */
  public void addYField(ValueProvider<? super M, ? extends Number> yField) {
    this.yFields.add(yField);
  }

  @Override
  public void drawSeries() {
    ListStore<M> store = chart.getCurrentStore();

    if (store == null || store.size() == 0) {
      this.clear();
      return;
    }

    calculateBounds();
    calculatePaths();

    for (int i = 0; i < yFields.size(); i++) {
      if (exclude.contains(i)) {
        continue;
      }
      final PathSprite path;
      if (i < areas.size()) {
        path = areas.get(i);
        path.setHidden(false);
      } else {
        path = new PathSprite();
        path.setFill(colors.get(i));
        chart.addSprite(path);
        areas.add(path);
      }
      if (chart.isAnimated() && path.getCommands().size() > 0) {
        DrawFx.createCommandsAnimator(path, areasCommands.get(i)).run(chart.getAnimationDuration(),
            chart.getAnimationEasing());
      } else {
        path.setCommands(areasCommands.get(i));
        path.redraw();
      }
      if (stroke != null) {
        path.setStroke(stroke);
      }
      if (!Double.isNaN(strokeWidth)) {
        path.setStrokeWidth(strokeWidth);
      }
      if (renderer != null) {
        renderer.spriteRenderer(path, i, store);
      }
    }

    for (int j = store.size(); j < sprites.size(); j++) {
      sprites.get(j).setHidden(true);
    }

    drawLabels();
    if (!highlightLineAttached && highlightLine != null) {
      chart.addSprite(highlightLine);
      highlightLineAttached = true;
    }
  }

  /**
   * Returns the fields that have been hidden from the series using
   * {@link #hide(int)}.
   * 
   * @return the fields that have been hidden from the series
   */
  public Set<Integer> getExcluded() {
    return exclude;
  }

  /**
   * Returns the configuration sprite for the highlighting line.
   * 
   * @return the configuration sprite for the highlighting line
   */
  public PathSprite getHighlightLineConfig() {
    return highlightLineConfig;
  }

  @Override
  public ArrayList<String> getLegendTitles() {
    ArrayList<String> titles = new ArrayList<String>();
    for (int j = 0; j < getYFields().size(); j++) {
      if (legendTitles.size() > j) {
        titles.add(legendTitles.get(j));
      } else {
        titles.add(getValueProviderName(getYFields().get(j), j + 1));
      }
    }
    return titles;
  }

  /**
   * Returns the x axis position of the series.
   * 
   * @return the x axis position of the series
   */
  public Position getXAxisPosition() {
    return xAxisPosition;
  }

  /**
   * Returns the y axis position of the series.
   * 
   * @return the y axis position of the series
   */
  public Position getYAxisPosition() {
    return yAxisPosition;
  }

  /**
   * Returns the value provider for the y-axis of the series at the given index.
   * 
   * @param index the index of the value provider
   * @return the value provider for the y-axis of the series at the given index
   */
  public ValueProvider<? super M, ? extends Number> getYField(int index) {
    return yFields.get(index);
  }

  /**
   * Returns the list of value providers for the y-axis of the series.
   * 
   * @return the list of value providers for the y-axis of the series
   */
  public List<ValueProvider<? super M, ? extends Number>> getYFields() {
    return yFields;
  }

  @Override
  public void hide(int yFieldIndex) {
    areas.get(yFieldIndex).setHidden(true);
    areas.get(yFieldIndex).redraw();
    exclude.add(yFieldIndex);
    chart.redrawChartForced();
  }

  @Override
  public void highlight(int yFieldIndex) {
    if (highlightLine != null) {
      if (lineBottom == null) {
        lineBottom = new PrecisePoint(lineTop.getX(), bbox.getY() + bbox.getHeight());
      }
      highlightLine.clearCommands();
      highlightLine.addCommand(new MoveTo(lineTop.getX(), lineTop.getY()));
      highlightLine.addCommand(new LineTo(lineBottom.getX(), lineBottom.getY()));
      highlightLine.setHidden(false);
      highlightLine.redraw();
    } else {
      highlightAll(yFieldIndex);
    }
  }

  @Override
  public void highlightAll(int index) {
    if (areas.size() > index) {
      highlighter.highlight(areas.get(index));
    }
  }

  /**
   * Removes a data field for the y axis of the series.
   * 
   * @param index the index of the data field to remove
   * @return the removed field
   */
  public ValueProvider<? super M, ? extends Number> removeYField(int index) {
    return this.yFields.remove(index);
  }

  /**
   * Removes a data field for the y axis of the series.
   * 
   * @param yField the value provider for the data on the y axis
   * @return whether or not the field was successfully removed
   */
  public boolean removeYField(ValueProvider<? super M, ? extends Number> yField) {
    return this.yFields.remove(yField);
  }

  /**
   * Sets the configuration sprite for the highlighting line.
   * 
   * @param highlightLineConfig the configuration sprite for the highlighting
   *          line
   */
  public void setHighlightLineConfig(PathSprite highlightLineConfig) {
    this.highlightLineConfig = highlightLineConfig;
    if (highlightLine != null && highlightLineAttached) {
      highlightLine.remove();
      highlightLineAttached = false;
    }
    highlightLine = highlightLineConfig.copy();
    highlightLine.setHidden(true);
  }

  /**
   * Sets the list of labels used by the legend.
   * 
   * @param legendTitles the list of labels
   */
  public void setLegendTitles(List<String> legendTitles) {
    this.legendTitles = legendTitles;
  }

  /**
   * Sets the position of the x axis on the chart to be used by the series.
   * 
   * @param xAxisPosition the position of the x axis on the chart to be used by
   *          the series
   */
  public void setXAxisPosition(Position xAxisPosition) {
    this.xAxisPosition = xAxisPosition;
  }

  /**
   * Sets the position of the y axis on the chart to be used by the series.
   * 
   * @param yAxisPosition the position of the y axis on the chart to be used by
   *          the series
   */
  public void setYAxisPosition(Position yAxisPosition) {
    this.yAxisPosition = yAxisPosition;
  }

  @Override
  public void show(int yFieldIndex) {
    areas.get(yFieldIndex).setHidden(false);
    areas.get(yFieldIndex).redraw();
    exclude.remove(yFieldIndex);
    chart.redrawChartForced();
  }

  @Override
  public void unHighlight(int yFieldIndex) {
    if (highlightLine != null) {
      highlightLine.setHidden(true);
      highlightLine.redraw();
    } else {
      unHighlightAll(yFieldIndex);
    }
  }

  @Override
  public void unHighlightAll(int index) {
    if (areas.size() > index) {
      highlighter.unHighlight(areas.get(index));
    }
  }

  @Override
  public boolean visibleInLegend(int index) {
    if (exclude.contains(index)) {
      return false;
    }
    return true;
  }

  @Override
  protected int getIndex(PrecisePoint point) {
    for (int i = 0; i < areas.size(); i++) {
      if (exclude.contains(i)) {
        continue;
      }
      double dist = Double.POSITIVE_INFINITY;
      ArrayList<PrecisePoint> pointsUp = this.pointsUp.get(i);
      ArrayList<PrecisePoint> pointsDown = this.pointsDown.get(i);
      for (int p = 0; p < pointsUp.size(); p++) {
        PrecisePoint pointTop = pointsUp.get(p);
        boolean distChanged = false;
        boolean last = p == pointsUp.size() - 1;
        if (dist > Math.abs(point.getX() - pointTop.getX())) {
          dist = Math.abs(point.getX() - pointTop.getX());
          distChanged = true;
          if (last) {
            ++p;
          }
        }
        if (!distChanged || (distChanged && last)) {
          pointTop = pointsUp.get(p - 1);
          if (point.getY() >= pointTop.getY()) {
            if (pointsDown.size() == 0) {
              lineTop = pointTop;
              lineBottom = null;
              storeIndex = p - 1;
              return i;
            }
            PrecisePoint pointBottom = pointsDown.get(p - 1);
            if (point.getY() <= pointBottom.getY()) {
              lineTop = pointTop;
              lineBottom = pointBottom;
              storeIndex = p - 1;
              return i;
            }
          }
          break;
        }
      }
    }
    return -1;
  }

  @Override
  protected int getStoreIndex(int index) {
    return storeIndex;
  }

  @Override
  protected ValueProvider<? super M, ? extends Number> getValueProvider(int index) {
    return yFields.get(index);
  }

  /**
   * Calculates the bounds of the series.
   */
  private void calculateBounds() {
    ListStore<M> store = chart.getCurrentStore();
    min = new PrecisePoint(Double.NaN, Double.NaN);
    max = new PrecisePoint(Double.NaN, Double.NaN);
    // get bounding box dimensions
    calculateBBox(false);
    xValues = new ArrayList<Double>();
    yValues = new ArrayList<double[]>();

    Axis<M, ?> axis = chart.getAxis(yAxisPosition);
    if (axis != null) {
      if (axis.getPosition() == Position.TOP || axis.getPosition() == Position.BOTTOM) {
        min.setX(axis.getFrom());
        max.setX(axis.getTo());
      } else {
        min.setY(axis.getFrom());
        max.setY(axis.getTo());
      }
    }

    axis = chart.getAxis(xAxisPosition);
    if (axis != null) {
      if (axis.getPosition() == Position.TOP || axis.getPosition() == Position.BOTTOM) {
        min.setX(axis.getFrom());
        max.setX(axis.getTo());
      } else {
        min.setY(axis.getFrom());
        max.setY(axis.getTo());
      }
    }

    if (Double.isNaN(min.getX())) {
      min.setX(0);
      xScale = bbox.getWidth() / (chart.getCurrentStore().size() - 1 - exclude.size());
    } else {
      xScale = bbox.getWidth() / (max.getX() - min.getX());
    }

    if (Double.isNaN(min.getY())) {
      min.setY(0);
      yScale = bbox.getHeight() / (chart.getCurrentStore().size() - 1 - exclude.size());
    } else {
      yScale = bbox.getHeight() / (max.getY() - min.getY());
    }

    if (Double.isNaN(max.getX())) {
      max.setX(0);
    }

    if (Double.isNaN(max.getY())) {
      max.setY(0);
    }

    for (int i = 0; i < store.size(); i++) {
      M model = store.get(i);
      double xValue = 0;
      double yElement;
      double[] yValue = new double[yFields.size()];
      double acumY = 0;

      // Ensure a value
      if (xField == null) {
        xValue = i;
      } else {
        xValue = xField.getValue(model).doubleValue();
      }
      xValues.add(xValue);

      for (int j = 0; j < yFields.size(); j++) {
        if (exclude.contains(j)) {
          continue;
        }
        yElement = yFields.get(j).getValue(model).doubleValue();
        min.setY(Math.min(min.getY(), yElement));
        acumY += yElement;
        yValue[j] = yElement;
      }
      min.setX(Math.min(min.getX(), xValue));
      max.setX(Math.max(max.getX(), xValue));
      max.setY(Math.max(max.getY(), acumY));
      yValues.add(yValue);
    }
    xScale = bbox.getWidth() / (max.getX() - min.getX());
    yScale = bbox.getHeight() / (max.getY() - min.getY());

    if (xValues.size() > bbox.getWidth()) {
      shrink(bbox.getWidth());
    }
  }

  /**
   * Build a list of paths for the chart.
   */
  private void calculatePaths() {
    boolean first = true;
    double xValue;
    double[] yValue;
    double x = 0;
    double y = 0;
    double acumY;
    areasCommands = new HashMap<Integer, ArrayList<PathCommand>>();
    Map<Integer, ArrayList<PathCommand>> areasComponentCommands = new HashMap<Integer, ArrayList<PathCommand>>();
    ArrayList<PathCommand> commands = null;
    ArrayList<PathCommand> componentCommands = null;
    PathCommand command;

    int ln = xValues.size();

    // Start the path
    for (int i = 0; i < ln; i++) {
      componentCommands = new ArrayList<PathCommand>();

      xValue = xValues.get(i);
      yValue = yValues.get(i);
      x = bbox.getX() + (xValue - min.getX()) * xScale;
      acumY = 0;

      for (int j = 0; j < yValue.length; j++) {

        if (exclude.contains(j)) {
          continue;
        }
        if (i == 0) {
          pointsUp.put(j, new ArrayList<PrecisePoint>());
        }

        if (areasComponentCommands.get(j) == null) {
          componentCommands = new ArrayList<PathCommand>();
          areasComponentCommands.put(j, componentCommands);
        } else {
          componentCommands = areasComponentCommands.get(j);
        }
        acumY += yValue[j];
        y = bbox.getY() + bbox.getHeight() - (acumY - min.getY()) * yScale;
        if (areasCommands.get(j) == null) {
          commands = new ArrayList<PathCommand>();
          commands.add(new MoveTo(x, y));
          areasCommands.put(j, commands);
        } else {
          commands = areasCommands.get(j);
          commands.add(new LineTo(x, y));
        }
        componentCommands.add(new LineTo(x, y));
        pointsUp.get(j).add(new PrecisePoint(x, y));
      }
    }

    int prevAreaIndex = 0;
    // Close the paths
    for (int i = 0; i < yFields.size(); i++) {

      if (exclude.contains(i)) {
        continue;
      }

      pointsDown.put(i, new ArrayList<PrecisePoint>());
      commands = areasCommands.get(i);
      // Close bottom path to the axis
      if (first) {
        first = false;
        commands.add(new LineTo(x, bbox.getY() + bbox.getHeight()));
        commands.add(new LineTo(bbox.getX(), bbox.getY() + bbox.getHeight()));
      }
      // Close other paths to the one before them
      else {
        componentCommands = areasComponentCommands.get(prevAreaIndex);
        // reverse the componentCommands
        for (int j = 0; j < componentCommands.size() / 2; j++) {
          command = componentCommands.remove(j);
          componentCommands.add(componentCommands.size() - j, command);
          command = componentCommands.remove(componentCommands.size() - j - 2);
          componentCommands.add(j, command);
        }
        command = componentCommands.get(0);
        if (command instanceof MoveTo) {
          commands.add(new LineTo(x, ((MoveTo) command).getY()));
        } else if (command instanceof LineTo) {
          commands.add(new LineTo(x, ((LineTo) command).getY()));
        }

        for (int j = 0; j < ln; j++) {
          command = componentCommands.get(j);
          if (command instanceof MoveTo) {
            commands.add(new MoveTo((MoveTo) command));
          } else if (command instanceof LineTo) {
            commands.add(new LineTo((LineTo) command));
          }
          EndPointCommand point = (EndPointCommand) command;
          pointsDown.get(i).add(0, new PrecisePoint(point.getX(), point.getY()));
        }
        command = commands.get(0);
        if (command instanceof MoveTo) {
          commands.add(new LineTo(bbox.getX(), ((MoveTo) command).getY()));
        } else if (command instanceof LineTo) {
          commands.add(new LineTo(bbox.getX(), ((LineTo) command).getY()));
        }
      }
      prevAreaIndex = i;
    }
  }

  /**
   * Draw the labels on the series.
   */
  private void drawLabels() {
    if (labelConfig != null) {
      LabelPosition labelPosition = labelConfig.getLabelPosition();
      if (labelPosition == LabelPosition.OUTSIDE) {
        int top = areasCommands.size() - 1;
        for (int i = 0; i < chart.getCurrentStore().size(); i++) {
          final Sprite sprite;
          if (labels.get(i) != null) {
            sprite = labels.get(i);
          } else {
            sprite = labelConfig.getSpriteConfig().copy();
            labels.put(i, sprite);
            chart.addSprite(sprite);
          }
          setLabelText(sprite, i);
          sprite.redraw();
          double offsetY = sprite.getBBox().getHeight() / 2.0;
          PrecisePoint point = getPointFromCommand(areasCommands.get(top).get(i));
          if (chart.isAnimated() && sprite.getTranslation() != null) {
            DrawFx.createTranslationAnimator(sprite, point.getX(), point.getY() - offsetY).run(
                chart.getAnimationDuration(), chart.getAnimationEasing());
          } else {
            sprite.setTranslation(point.getX(), point.getY() - offsetY);
            sprite.redraw();
          }
        }
      } else if (labelPosition == LabelPosition.END || labelPosition == LabelPosition.START) {
        for (int i = 0; i < yValues.size(); i++) {
          double[] values = yValues.get(i);
          for (int j = 0; j < values.length; j++) {
            final Sprite sprite;
            if (labels.get(i * values.length + j) != null) {
              sprite = labels.get(i * values.length + j);
            } else {
              sprite = labelConfig.getSpriteConfig().copy();
              labels.put(i * values.length + j, sprite);
              chart.addSprite(sprite);
            }
            if (sprite instanceof TextSprite) {
              TextSprite text = (TextSprite) sprite;
              if (labelConfig.getLabelProvider() != null) {
                text.setText(labelConfig.getLabelProvider().getLabel(chart.getCurrentStore().get(j),
                    getValueProvider(j)));
              }
              text.redraw();
            }
            double offsetY = sprite.getBBox().getHeight() / 2.0;
            PrecisePoint point = getPointFromCommand(areasCommands.get(j).get(i));
            if (chart.isAnimated() && sprite.getTranslation() != null) {
              DrawFx.createTranslationAnimator(sprite, point.getX(), point.getY() - offsetY).run(
                  chart.getAnimationDuration(), chart.getAnimationEasing());
            } else {
              sprite.setTranslation(point.getX(), point.getY() - offsetY);
              sprite.redraw();
            }
          }
        }
      }
    }
  }

  /**
   * Shrinks the number of coordinates to fit the screen.
   * 
   * @param width the maximum width of the chart
   * @return the new shrunk coordinates
   */
  private void shrink(double width) {
    List<Double> xResult = new ArrayList<Double>();
    List<double[]> yResult = new ArrayList<double[]>();
    if (width < 1) {
      width = 1;
    }
    final double ratio = Math.ceil(xValues.size() / width);
    double xSum = 0;
    double[] ySum = new double[yFields.size()];

    for (int i = 0; i < xValues.size(); i++) {
      xSum += xValues.get(i);
      for (int j = 0; j < yFields.size(); j++) {
        ySum[j] += yValues.get(i)[j];
      }
      if (i % ratio == 0) {
        xResult.add(xSum / ratio);
        for (int k = 0; k < yFields.size(); k++) {
          ySum[k] /= ratio;
        }
        yResult.add(ySum);
        xSum = 0;
        ySum = new double[yFields.size()];
      }
    }
    xValues = xResult;
    yValues = yResult;
  }
}
