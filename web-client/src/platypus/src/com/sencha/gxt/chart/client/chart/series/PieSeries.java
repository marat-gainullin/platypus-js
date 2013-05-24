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

import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOutEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.DrawFx;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.Rotation;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathCommand;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.RectangleSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteList;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.data.shared.ListStore;

/**
 * Creates a Pie Chart. A Pie Chart is a useful visualization technique to
 * display quantitative information for different categories that also have a
 * meaning as a whole.
 * 
 * Here is an example pie configuration:
 * 
 * <pre>
    PieSeries<Data> series = new PieSeries<Data>();
    series.setAngleField(dataAccess.data1());
    series.addColor(slice1);
    series.addColor(slice2);
    series.addColor(slice3);
    series.addColor(slice4);
    series.addColor(slice5);
    series.addColor(slice6);
    chart.addSeries(series);
 * </pre>
 * 
 * First the series is created with its associated data type. Next the value
 * provider field is set, which provides the value of the pie slices. A color is
 * also added to represent each slice in the pie. Finally the series is added to
 * the chart where it will be displayed.
 * 
 * @param <M> data type used by this series
 */
public class PieSeries<M> extends AbstractPieSeries<M> {

  private Set<Integer> exclude = new HashSet<Integer>();
  private ArrayList<ValueProvider<? super M, ? extends Number>> lengthField = new ArrayList<ValueProvider<? super M, ? extends Number>>();
  private Map<Integer, Slice> slices = new HashMap<Integer, Slice>();
  private Map<Integer, PrecisePoint> labelPoints = new HashMap<Integer, PrecisePoint>();
  private Map<Integer, PrecisePoint> middlePoints = new HashMap<Integer, PrecisePoint>();
  private Map<Integer, PathSprite> calloutLines = new HashMap<Integer, PathSprite>();
  private Map<Integer, RectangleSprite> calloutBoxes = new HashMap<Integer, RectangleSprite>();
  private double firstAngle;
  private double popOutMargin = 20;

  /**
   * Creates a pie series.
   */
  public PieSeries() {
    // setup shadow attributes
    shadowAttributes = new ArrayList<Sprite>();
    PathSprite config = new PathSprite();
    config.setStrokeWidth(6);
    config.setStrokeOpacity(1);
    config.setStroke(new RGB(200, 200, 200));
    config.setTranslation(1.2, 2);
    config.setStrokeLineJoin(LineJoin.ROUND);
    shadowAttributes.add(config);
    config = new PathSprite();
    config.setStrokeWidth(4);
    config.setStrokeOpacity(1);
    config.setStroke(new RGB(150, 150, 150));
    config.setTranslation(0.9, 1.5);
    config.setStrokeLineJoin(LineJoin.ROUND);
    shadowAttributes.add(config);
    config = new PathSprite();
    config.setStrokeWidth(2);
    config.setStrokeOpacity(1);
    config.setStroke(new RGB(100, 100, 100));
    config.setTranslation(0.6, 1);
    config.setStrokeLineJoin(LineJoin.ROUND);
    shadowAttributes.add(config);

    // initialize the shadow groups
    if (shadowGroups.size() == 0) {
      for (int i = 0; i < shadowAttributes.size(); i++) {
        shadowGroups.add(new SpriteList<Sprite>());
      }
    }
  }

  /**
   * Adds a {@link ValueProvider} that represents the radius of a pie slice.
   * 
   * @param lengthField the value provider
   */
  public void addLengthField(ValueProvider<? super M, ? extends Number> lengthField) {
    this.lengthField.add(lengthField);
  }

  @Override
  public void clear() {
    super.clear();
    for (int i = 0; i < shadowGroups.size(); i++) {
      shadowGroups.get(i).clear();
    }
  }

  @Override
  public void drawSeries() {
    ListStore<M> store = chart.getCurrentStore();
    boolean first = true;
    int layers = lengthField.size() > 0 ? lengthField.size() : 1;
    PreciseRectangle chartBBox = chart.getBBox();
    Map<Integer, Slice> oldSlices = slices;
    slices = new HashMap<Integer, Slice>();
    Map<Integer, Double> layerTotals = new HashMap<Integer, Double>();
    Slice slice;
    double totalField = 0;
    double totalLength = 0;
    double maxLength = 0;
    List<PathCommand> commands = new ArrayList<PathCommand>();
    M model;

    double value = 0;
    double angle = 0;
    double middleAngle = 0;
    double endAngle = 0;
    double rhoAcum = 0;

    if (store == null || store.size() == 0) {
      this.clear();
      return;
    }

    center.setX(chartBBox.getX() + (chartBBox.getWidth() / 2));
    center.setY(chartBBox.getY() + (chartBBox.getHeight() / 2));
    radius = Math.min(center.getX() - chartBBox.getX(), center.getY() - chartBBox.getY());

    for (int i = 0; i < store.size(); i++) {
      if (exclude.contains(i)) {
        continue;
      }
      model = store.get(i);
      totalField += angleField.getValue(model).doubleValue();
      if (lengthField.size() > 0) {
        totalLength = 0;
        for (int j = 0; j < layers; j++) {
          totalLength += lengthField.get(j).getValue(model).doubleValue();
        }
        layerTotals.put(i, totalLength);
        maxLength = Math.max(maxLength, totalLength);
      }
    }

    for (int i = 0; i < store.size(); i++) {
      if (exclude.contains(i)) {
        continue;
      }
      model = store.get(i);
      value = angleField.getValue(model).doubleValue();
      middleAngle = angle - 360 * value / totalField / 2;

      // First Slice
      if (first) {
        angle = 360 - middleAngle;
        firstAngle = angle;
        middleAngle = angle - 360 * value / totalField / 2;
        first = false;
      }
      endAngle = angle - 360 * value / totalField;
      slice = new Slice(value, angle, endAngle, radius);
      if (angle % 360 == endAngle % 360) {
        slice.setStartAngle(angle - 0.0001);
      }
      if (lengthField.size() > 0) {
        slice.setRho(radius * (layerTotals.get(i) / maxLength));
      }
      if (slices.get(i) == null) {
        slices.put(i, slice);
      }
      angle = endAngle;
    }

    // do all shadows first.
    if (chart.hasShadows()) {
      for (int i = 0; i < store.size(); i++) {
        if (exclude.contains(i)) {
          continue;
        }
        slice = slices.get(i);
        rhoAcum = 0;
        for (int j = 0; j < layers; j++) {
          double deltaRho = 0;
          if (lengthField.size() > 0) {
            deltaRho = lengthField.get(j).getValue(store.get(i)).doubleValue() / layerTotals.get(i) * slice.getRho();
          } else {
            deltaRho = slice.getRho();
          }
          slice.setMargin(margin);
          slice.setStartRho(rhoAcum + (deltaRho * donut / 100));
          slice.setEndRho(rhoAcum + deltaRho);
          // create shadows
          for (int shindex = 0; shindex < shadowGroups.size(); shindex++) {
            Sprite shadowAttr = shadowAttributes.get(shindex);
            SpriteList<Sprite> shadows = shadowGroups.get(shindex);
            final PathSprite shadow;

            commands = calculateSegment(slice);
            if (i < shadows.size()) {
              shadow = (PathSprite) shadows.get(i);
              shadow.setHidden(false);
            } else {
              shadow = (PathSprite) shadowAttr.copy();
              shadow.setFill(Color.NONE);
              chart.addSprite(shadow);
              shadows.add(shadow);
            }

            if (chart.isAnimated() && oldSlices.get(i) != null) {
              createSegmentAnimator(shadow, oldSlices.get(i), slice).run(chart.getAnimationDuration(),
                  chart.getAnimationEasing());
            } else {
              shadow.setCommands(commands);
              shadow.redraw();
            }
            if (shadowRenderer != null) {
              shadowRenderer.spriteRenderer(shadow, i, chart.getCurrentStore());
            }
          }
        }
      }
      shadowed = true;
    } else {
      hideShadows();
    }

    // do pie slices after
    for (int i = 0; i < store.size(); i++) {
      if (exclude.contains(i)) {
        continue;
      }
      slice = slices.get(i);
      rhoAcum = 0;
      for (int j = 0; j < layers; j++) {
        double deltaRho = 0;
        if (lengthField.size() > 0) {
          deltaRho = lengthField.get(j).getValue(store.get(i)).doubleValue() / layerTotals.get(i) * slice.getRho();
        } else {
          deltaRho = slice.getRho();
        }
        final PathSprite sprite;
        int index = i * layers + j;
        if (sprites.get(index) != null) {
          sprite = (PathSprite) sprites.get(index);
          sprite.setHidden(false);
        } else {
          // Create a new sprite if needed (no height)
          sprite = new PathSprite();
          sprite.setFill(getColor(i));
          sprite.setZIndex(10);
          sprites.add(sprite);
          chart.addSprite(sprite);
        }
        if (stroke != null) {
          sprite.setStroke(stroke);
        }
        if (!Double.isNaN(strokeWidth)) {
          sprite.setStrokeWidth(strokeWidth);
        }

        slice.setMargin(margin);
        slice.setStartRho(rhoAcum + (deltaRho * donut / 100));
        slice.setEndRho(rhoAcum + deltaRho);

        if (labelConfig != null) {
          calculateMiddle(slice, i);
        }

        if (chart.isAnimated() && oldSlices.get(i) != null) {
          createSegmentAnimator(sprite, oldSlices.get(i), slice).run(chart.getAnimationDuration(),
              chart.getAnimationEasing());
        } else {
          sprite.setCommands(calculateSegment(slice));
          sprite.redraw();
        }
        if (renderer != null) {
          renderer.spriteRenderer(sprite, i, store);
        }
        rhoAcum += deltaRho;
      }
    }
    for (int j = (slices.size() + exclude.size()) * layers; j < sprites.size(); j++) {
      sprites.get(j).setHidden(true);
    }
    if (chart.hasShadows()) {
      for (int j = 0; j < shadowGroups.size(); j++) {
        SpriteList<Sprite> shadows = shadowGroups.get(j);
        for (int k = slices.size() + exclude.size(); k < shadows.size(); k++) {
          Sprite unusedSprite = shadows.get(k);
          unusedSprite.setHidden(true);
          unusedSprite.redraw();
        }
      }
    }
    for (int j = slices.size() + exclude.size(); j < labels.size(); j++) {
      Sprite unusedSprite = labels.get(j);
      unusedSprite.setHidden(true);
      unusedSprite.redraw();
    }
    for (int j = slices.size() + exclude.size(); j < calloutLines.size(); j++) {
      Sprite unusedSprite = calloutLines.get(j);
      unusedSprite.setHidden(true);
      unusedSprite.redraw();
    }
    for (int j = slices.size() + exclude.size(); j < calloutBoxes.size(); j++) {
      Sprite unusedSprite = calloutBoxes.get(j);
      unusedSprite.setHidden(true);
      unusedSprite.redraw();
    }
    drawLabels();
  }

  /**
   * Returns the list of value providers that represent the radius of pie
   * slices.
   * 
   * @return the list
   */
  public ArrayList<ValueProvider<? super M, ? extends Number>> getLengthFields() {
    return lengthField;
  }

  /**
   * Returns the margin that the slices pop out.
   * 
   * @return the margin that the slices pop out
   */
  public double getPopOutMargin() {
    return popOutMargin;
  }

  @Override
  public void hide(int yFieldIndex) {
    sprites.get(yFieldIndex).setHidden(true);
    sprites.get(yFieldIndex).redraw();
    for (int i = 0; i < shadowGroups.size(); i++) {
      SpriteList<Sprite> shadows = shadowGroups.get(i);
      if (shadows.get(yFieldIndex) != null) {
        shadows.get(yFieldIndex).setHidden(true);
        shadows.get(yFieldIndex).redraw();
      }
    }
    if (labelConfig != null) {
      labels.get(yFieldIndex).setHidden(true);
      labels.get(yFieldIndex).redraw();
      if (labelConfig.getLabelPosition() == LabelPosition.OUTSIDE) {
        calloutLines.get(yFieldIndex).setHidden(true);
        calloutLines.get(yFieldIndex).redraw();
        calloutBoxes.get(yFieldIndex).setHidden(true);
        calloutBoxes.get(yFieldIndex).redraw();
      }
    }
    exclude.add(yFieldIndex);
    drawSeries();
  }

  @Override
  public void highlight(int yFieldIndex) {
    int layers = lengthField.size() > 0 ? lengthField.size() : 1;
    int spriteIndex = (yFieldIndex * layers) + (layers - 1);
    if (popOutMargin > 0) {
      Slice slice = new Slice(slices.get(yFieldIndex));
      slice.setMargin(popOutMargin);
      double labelX = 0;
      double labelY = 0;
      if (labels.size() > yFieldIndex && calloutBoxes.size() == 0) {
        double middle = Math.toRadians((slice.startAngle + slice.endAngle) / 2.0);
        PrecisePoint trans = labelPoints.get(yFieldIndex);
        labelX = popOutMargin * Math.cos(middle) + trans.getX();
        labelY = popOutMargin * Math.sin(middle) + trans.getY();
        if (Math.abs(labelX) < 1e-10) {
          labelX = 0;
        }
        if (Math.abs(labelY) < 1e-10) {
          labelY = 0;
        }
        if (chart.isAnimated()) {
          DrawFx.createTranslationAnimator(labels.get(yFieldIndex), labelX, labelY).run(chart.getAnimationDuration(),
              chart.getAnimationEasing());
        } else {
          labels.get(yFieldIndex).setTranslation(labelX, labelY);
          labels.get(yFieldIndex).redraw();
        }
      }
      if (chart.isAnimated()) {
        createSegmentAnimator((PathSprite) sprites.get(spriteIndex), slices.get(yFieldIndex), slice).run(
            chart.getAnimationDuration(), chart.getAnimationEasing());
        for (int i = 0; i < shadowGroups.size(); i++) {
          SpriteList<Sprite> shadows = shadowGroups.get(i);
          if (shadows.get(yFieldIndex) != null) {
            createSegmentAnimator((PathSprite) shadows.get(yFieldIndex), slices.get(yFieldIndex), slice).run(
                chart.getAnimationDuration(), chart.getAnimationEasing());
          }
        }
      } else {
        List<PathCommand> commands = calculateSegment(slice);
        ((PathSprite) sprites.get(spriteIndex)).setCommands(commands);
        sprites.get(spriteIndex).redraw();
        for (int i = 0; i < shadowGroups.size(); i++) {
          SpriteList<Sprite> shadows = shadowGroups.get(i);
          if (shadows.get(yFieldIndex) != null) {
            PathSprite shadow = (PathSprite) shadows.get(yFieldIndex);
            shadow.setCommands(commands);
            shadow.redraw();
          }
        }
      }
    }
    if (highlighter != null) {
      highlighter.highlight(sprites.get(spriteIndex));
      for (int i = 0; i < shadowGroups.size(); i++) {
        SpriteList<Sprite> shadows = shadowGroups.get(i);
        if (shadows.get(yFieldIndex) != null) {
          highlighter.highlight(shadows.get(yFieldIndex));
        }
      }
    }
  }

  @Override
  public void highlightAll(int index) {
    highlight(index);
  }

  @Override
  public int onMouseMove(PrecisePoint point, Event event) {
    if (handlerManager != null || highlighting) {
      if (lastHighlighted == -1) {
        int length = sprites.size();
        for (int i = 0; i < length; i++) {
          if (exclude.contains(i)) {
            continue;
          }
          Slice slice = slices.get(i);
          if (slice != null) {
            double dx = Math.abs(point.getX() - center.getX());
            double dy = Math.abs(point.getY() - center.getY());
            double rho = Math.sqrt(dx * dx + dy * dy);
            double angle = Math.toDegrees(Math.atan2(point.getY() - center.getY(), point.getX() - center.getX())) + 360;
            if (angle > firstAngle) {
              angle -= 360;
            }
            if (angle <= slice.getStartAngle() && angle > slice.getEndAngle() && rho >= slice.getStartRho()
                && rho <= slice.getEndRho()) {
              ensureHandlers().fireEvent(
                  new SeriesItemOverEvent<M>(chart.getCurrentStore().get(i), getValueProvider(i), i, event));
              if (toolTip != null) {
                if (toolTipConfig.getLabelProvider() != null) {
                  toolTipConfig.setBodyText(toolTipConfig.getLabelProvider().getLabel(chart.getCurrentStore().get(i),
                      getValueProvider(i)));
                }
                toolTip.update(toolTipConfig);
                toolTip.showAt(
                    (int) Math.round(point.getX() + chart.getAbsoluteLeft() + toolTipConfig.getMouseOffset()[0]),
                    (int) Math.round(point.getY() + chart.getAbsoluteTop() + toolTipConfig.getMouseOffset()[1]));
              }
              if (highlighting) {
                highlight(i);
                lastHighlighted = i;
              }
              return i;
            }
          }
        }
      } else {
        Slice slice = slices.get(lastHighlighted);
        if (slice != null) {
          double dx = Math.abs(point.getX() - center.getX());
          double dy = Math.abs(point.getY() - center.getY());
          double rho = Math.sqrt(dx * dx + dy * dy);
          double angle = Math.toDegrees(Math.atan2(point.getY() - center.getY(), point.getX() - center.getX())) + 360;
          if (angle > firstAngle) {
            angle -= 360;
          }
          if (!(angle <= slice.getStartAngle() && angle > slice.getEndAngle() && rho >= slice.getStartRho() && rho <= slice.getEndRho())) {
            ensureHandlers().fireEvent(
                new SeriesItemOutEvent<M>(chart.getCurrentStore().get(getStoreIndex(lastHighlighted)),
                    getValueProvider(lastHighlighted), lastHighlighted, event));
            if (highlighting) {
              unHighlight(lastHighlighted);
              lastHighlighted = -1;
            }
          }
        }
      }
    }
    return -1;
  }

  /**
   * Sets the margin that the slices pop out.
   * 
   * @param popOutMargin the margin that the slices pop out
   */
  public void setPopOutMargin(double popOutMargin) {
    this.popOutMargin = popOutMargin;
  }

  @Override
  public void show(int yFieldIndex) {
    sprites.get(yFieldIndex).setHidden(false);
    sprites.get(yFieldIndex).redraw();
    if (chart.hasShadows()) {
      for (int i = 0; i < shadowGroups.size(); i++) {
        SpriteList<Sprite> shadows = shadowGroups.get(i);
        if (shadows.get(yFieldIndex) != null) {
          shadows.get(yFieldIndex).setHidden(false);
          shadows.get(yFieldIndex).redraw();
        }
      }
    }
    exclude.remove(yFieldIndex);
    if (labelConfig != null) {
      labels.get(yFieldIndex).setHidden(false);
      labels.get(yFieldIndex).redraw();
      if (labelConfig.getLabelPosition() == LabelPosition.OUTSIDE) {
        calloutLines.get(yFieldIndex).setHidden(false);
        calloutLines.get(yFieldIndex).redraw();
        calloutBoxes.get(yFieldIndex).setHidden(false);
        calloutBoxes.get(yFieldIndex).redraw();
      }
    }
    drawSeries();
  }

  @Override
  public void unHighlight(int yFieldIndex) {
    int layers = lengthField.size() > 0 ? lengthField.size() : 1;
    int spriteIndex = (yFieldIndex * layers) + (layers - 1);
    if (popOutMargin > 0) {
      Slice slice = new Slice(slices.get(yFieldIndex));
      slice.setMargin(margin);
      double labelX = 0;
      double labelY = 0;
      if (labels.size() > yFieldIndex && calloutBoxes.size() == 0) {
        double middle = Math.toRadians((slice.startAngle + slice.endAngle) / 2.0);
        labelX = margin * Math.cos(middle) + labelPoints.get(yFieldIndex).getX();
        labelY = margin * Math.sin(middle) + labelPoints.get(yFieldIndex).getY();
        if (Math.abs(labelX) < 1e-10) {
          labelX = 0;
        }
        if (Math.abs(labelY) < 1e-10) {
          labelY = 0;
        }
        if (chart.isAnimated()) {
          DrawFx.createTranslationAnimator(labels.get(yFieldIndex), labelX, labelY).run(chart.getAnimationDuration(),
              chart.getAnimationEasing());
        } else {
          labels.get(yFieldIndex).setTranslation(labelX, labelY);
          labels.get(yFieldIndex).redraw();
        }
      }
      if (chart.isAnimated()) {
        Slice startSlice = new Slice(slices.get(yFieldIndex));
        startSlice.setMargin(popOutMargin);
        createSegmentAnimator((PathSprite) sprites.get(spriteIndex), startSlice, slice).run(
            chart.getAnimationDuration(), chart.getAnimationEasing());
        for (int i = 0; i < shadowGroups.size(); i++) {
          SpriteList<Sprite> shadows = shadowGroups.get(i);
          if (shadows.get(yFieldIndex) != null) {
            createSegmentAnimator((PathSprite) shadows.get(yFieldIndex), startSlice, slice).run(
                chart.getAnimationDuration(), chart.getAnimationEasing());
          }
        }
      } else {
        List<PathCommand> commands = calculateSegment(slices.get(yFieldIndex));
        ((PathSprite) sprites.get(spriteIndex)).setCommands(commands);
        sprites.get(spriteIndex).redraw();
        for (int i = 0; i < shadowGroups.size(); i++) {
          SpriteList<Sprite> shadows = shadowGroups.get(i);
          if (shadows.get(yFieldIndex) != null) {
            PathSprite shadow = (PathSprite) shadows.get(yFieldIndex);
            shadow.setCommands(commands);
            shadow.redraw();
          }
        }
      }
    }
    if (highlighter != null) {
      highlighter.unHighlight(sprites.get(spriteIndex));
    }
  }

  @Override
  public void unHighlightAll(int index) {
    unHighlight(index);
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
    for (int i = 0; i < slices.size(); i++) {
      if (exclude.contains(i)) {
        continue;
      }
      Slice slice = slices.get(i);
      if (slice != null) {
        double dx = Math.abs(point.getX() - center.getX());
        double dy = Math.abs(point.getY() - center.getY());
        double rho = Math.sqrt(dx * dx + dy * dy);
        double angle = Math.toDegrees(Math.atan2(point.getY() - center.getY(), point.getX() - center.getX())) + 360;
        if (angle > firstAngle) {
          angle -= 360;
        }
        if (angle <= slice.getStartAngle() && angle > slice.getEndAngle() && rho >= slice.getStartRho()
            && rho <= slice.getEndRho()) {
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  protected ValueProvider<? super M, ? extends Number> getValueProvider(int index) {
    return angleField;
  }

  /**
   * Utility function to calculate the middle point of a pie slice.
   * 
   * @param slice the pie slice
   */
  private void calculateMiddle(Slice slice, int index) {
    double a1 = Math.toRadians(Math.min(slice.getStartAngle(), slice.getEndAngle()));
    double a2 = Math.toRadians(Math.max(slice.getStartAngle(), slice.getEndAngle()));
    double midAngle = -(a1 + (a2 - a1) / 2.0);
    double xm = center.getX() + (slice.getEndRho() + slice.getStartRho()) / 2.0 * Math.cos(midAngle);
    double ym = center.getY() - (slice.getEndRho() + slice.getStartRho()) / 2.0 * Math.sin(midAngle);
    middlePoints.put(index, new PrecisePoint(xm, ym));
  }

  /**
   * Draws the labels of the series.
   */
  private void drawLabels() {
    if (labelConfig != null) {
      double previousDegrees = Double.NaN;
      for (int i = 0; i < chart.getCurrentStore().size(); i++) {
        if (exclude.contains(i)) {
          continue;
        }

        final Sprite sprite;
        if (labels.get(i) != null) {
          sprite = labels.get(i);
          sprite.setHidden(false);
        } else {
          sprite = labelConfig.getSpriteConfig().copy();
          labels.put(i, sprite);
          chart.addSprite(sprite);
        }
        setLabelText(sprite, i);
        PrecisePoint middle = middlePoints.get(i);
        double x = middle.getX() - center.getX();
        double y = middle.getY() - center.getY();
        double rho = 1;
        double theta = Math.atan2(y, x == 0 ? 1 : x);
        double degrees = Math.toDegrees(theta);
        LabelPosition labelPosition = labelConfig.getLabelPosition();
        if (labelPosition == LabelPosition.OUTSIDE) {
          Slice slice = slices.get(i);
          rho = slice.getEndRho() + 20;
          double rhoCenter = (slice.endRho + slice.startRho) / 2.0 + (slice.endRho - slice.startRho) / 3.0;
          PrecisePoint calloutPoint = new PrecisePoint(rho * Math.cos(theta) + center.getX(), rho * Math.sin(theta)
              + center.getY());
          x = rhoCenter * Math.cos(theta);
          y = rhoCenter * Math.sin(theta);

          final PathSprite line;
          if (calloutLines.get(i) != null) {
            line = calloutLines.get(i);
            line.setHidden(false);
          } else {
            line = new PathSprite();
            line.setStrokeWidth(1);
            line.setStroke(RGB.BLACK);
            line.setFill(Color.NONE);
            calloutLines.put(i, line);
            chart.addSprite(line);
          }

          final RectangleSprite box;
          if (calloutBoxes.get(i) != null) {
            box = calloutBoxes.get(i);
            box.setHidden(false);
          } else {
            box = new RectangleSprite();
            box.setStroke(RGB.BLACK);
            box.setStrokeWidth(1);
            box.setFill(Color.NONE);
            calloutBoxes.put(i, box);
            chart.addSprite(box);
          }

          sprite.redraw();
          PreciseRectangle bbox = sprite.getBBox();

          List<PathCommand> commands = new ArrayList<PathCommand>();
          commands.add(new MoveTo(x + center.getX(), y + center.getY()));
          commands.add(new LineTo(calloutPoint.getX(), calloutPoint.getY()));
          commands.add(new LineTo(x > 0 ? 10 : -10, 0, true));

          PreciseRectangle rect = new PreciseRectangle(calloutPoint.getX() + (x > 0 ? 10 : -(bbox.getWidth() + 30)),
              calloutPoint.getY() + (y > 0 ? (-(bbox.getHeight() - 5)) : (-(bbox.getHeight() - 5))),
              bbox.getWidth() + 20, bbox.getHeight() + 20);

          PrecisePoint labelPoint = new PrecisePoint(calloutPoint.getX() + (x > 0 ? 20 : -(20 + bbox.getWidth())),
              calloutPoint.getY() + (y > 0 ? -bbox.getHeight() / 4.0 : -bbox.getHeight() / 4.0));

          if (chart.isAnimated() && line.size() > 0 && !Double.isNaN(box.getX()) && sprite.getTranslation() != null) {
            DrawFx.createCommandsAnimator(line, commands).run(chart.getAnimationDuration(), chart.getAnimationEasing());
            DrawFx.createRectangleAnimator(box, rect).run(chart.getAnimationDuration(), chart.getAnimationEasing());
            DrawFx.createTranslationAnimator(sprite, labelPoint.getX(), labelPoint.getY()).run(
                chart.getAnimationDuration(), chart.getAnimationEasing());
          } else {
            line.setCommands(commands);
            line.redraw();
            box.setX(rect.getX());
            box.setY(rect.getY());
            box.setWidth(rect.getWidth());
            box.setHeight(rect.getHeight());
            box.redraw();
            sprite.setTranslation(labelPoint.getX(), labelPoint.getY());
            sprite.redraw();
          }
        } else if (labelPosition == LabelPosition.END) {
          rho = Math.sqrt(x * x + y * y) * 2.0;
          x = rho * Math.cos(theta) + center.getX();
          y = rho * Math.sin(theta) + center.getY();
          if (chart.isAnimated() && sprite.getTranslation() != null) {
            DrawFx.createTranslationAnimator(sprite, x, y).run(chart.getAnimationDuration(), chart.getAnimationEasing());
          } else {
            sprite.setTranslation(x, y);
          }
          labelPoints.put(i, new PrecisePoint(x, y));
        } else if (labelPosition == LabelPosition.START) {
          degrees = fixAngle(degrees);
          if (degrees > 90 && degrees < 270) {
            degrees += 180;
          }
          if (!Double.isNaN(previousDegrees) && Math.abs(previousDegrees - degrees) > 180) {
            if (degrees > previousDegrees) {
              degrees -= 360;
            } else {
              degrees += 360;
            }
            degrees %= 360;
          } else {
            degrees = fixAngle(degrees);
          }
          if (labelConfig.isLabelContrast()) {
            final Sprite back = sprites.get(i);
            if (chart.isAnimated()) {
              Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                  setLabelContrast(sprite, labelConfig, back);
                }
              });
            } else {
              setLabelContrast(sprite, labelConfig, back);
            }
          }
          labelPoints.put(i, new PrecisePoint(middle));
          if (chart.isAnimated() && sprite.getTranslation() != null) {
            DrawFx.createTranslationAnimator(sprite, middle.getX(), middle.getY()).run(chart.getAnimationDuration(),
                chart.getAnimationEasing());
            DrawFx.createRotationAnimator(sprite, 0, 0, degrees).run(chart.getAnimationDuration(),
                chart.getAnimationEasing());
          } else {
            sprite.setTranslation(middle.getX(), middle.getY());
            sprite.setRotation(new Rotation(degrees));
          }
          previousDegrees = degrees;
        }
        sprite.redraw();
      }
    }
  }

  /**
   * Fixes the given angle 360 degrees.
   * 
   * @param angle the angle to be fixed
   * @return the fixed angle
   */
  private double fixAngle(double angle) {
    if (angle < 0) {
      angle += 360;
    }
    return Math.round(angle) % 360;
  }
}
