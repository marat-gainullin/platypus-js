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
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.event.SeriesHandler;
import com.sencha.gxt.chart.client.chart.event.SeriesHandler.HasSeriesHandlers;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOutEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOutEvent.HasSeriesItemOutHandlers;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOutEvent.SeriesItemOutHandler;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent.HasSeriesItemOverHandlers;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent.SeriesItemOverHandler;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent.HasSeriesSelectionHandlers;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent.SeriesSelectionHandler;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.HSL;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.path.EndPointCommand;
import com.sencha.gxt.chart.client.draw.path.PathCommand;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteList;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.tips.ToolTip;

/**
 * Series is the abstract class containing the common logic to all chart series.
 * This class implements the logic of handling mouse events, animating, hiding,
 * showing all elements and returning the color of the series to be used as a
 * legend item.
 * 
 * @param <M> the data type of the series
 */
public abstract class Series<M> implements HasSeriesHandlers<M>, HasSeriesSelectionHandlers<M>,
    HasSeriesItemOutHandlers<M>, HasSeriesItemOverHandlers<M> {

  /**
   * Enumeration used for labels positioned on the series.
   */
  public enum LabelPosition {
    /**
     * Label positioned at the start of the series.
     */
    START,
    /**
     * Label positioned at the end of the series.
     */
    END,
    /**
     * Label positioned outside of the series.
     */
    OUTSIDE
  }

  protected Chart<M> chart;
  protected ValueProvider<? super M, ? extends Number> xField;
  protected SpriteList<Sprite> sprites = new SpriteList<Sprite>();
  protected List<SpriteList<Sprite>> shadowGroups = new ArrayList<SpriteList<Sprite>>();
  protected List<Sprite> shadowAttributes = new ArrayList<Sprite>();
  protected PreciseRectangle bbox = new PreciseRectangle();
  protected boolean shownInLegend = true;
  protected Map<Integer, String> legendNames = new HashMap<Integer, String>();
  protected boolean highlighting = false;
  protected int lastHighlighted = -1;
  protected SeriesHighlighter highlighter;
  protected SeriesRenderer<M> renderer;
  protected SeriesRenderer<M> shadowRenderer;
  protected ToolTip toolTip;
  protected SeriesToolTipConfig<M> toolTipConfig;
  protected Map<Integer, Sprite> labels = new HashMap<Integer, Sprite>();
  protected SeriesLabelConfig<M> labelConfig;
  protected HandlerManager handlerManager;
  protected List<String> legendTitles = new ArrayList<String>();
  protected Color stroke;
  protected double strokeWidth = 0;
  protected boolean shadowed = false;
  private boolean toolTipShown;

  /**
   * Creates a series.
   */
  public Series() {
  }

  public HandlerRegistration addSeriesHandler(SeriesHandler<M> handler) {
    GroupingHandlerRegistration reg = new GroupingHandlerRegistration();
    reg.add(ensureHandlers().addHandler(SeriesSelectionEvent.getType(), handler));
    reg.add(ensureHandlers().addHandler(SeriesItemOutEvent.getType(), handler));
    reg.add(ensureHandlers().addHandler(SeriesItemOverEvent.getType(), handler));
    return reg;
  }

  @Override
  public HandlerRegistration addSeriesItemOutHandler(SeriesItemOutHandler<M> handler) {
    return ensureHandlers().addHandler(SeriesItemOutEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addSeriesItemOverHandler(SeriesItemOverHandler<M> handler) {
    return ensureHandlers().addHandler(SeriesItemOverEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addSeriesSelectionHandler(SeriesSelectionHandler<M> handler) {
    return ensureHandlers().addHandler(SeriesSelectionEvent.getType(), handler);
  }

  /**
   * Calculates the bounding the box of the series and stores the result. To get
   * the result us {@link #getBBox()}.
   * 
   * @param gutter true if to use the series gutter in the calculation
   */
  public void calculateBBox(boolean gutter) {
    PreciseRectangle chartBBox = chart.getBBox();
    double gutterX = 0;
    double gutterY = 0;
    if (gutter) {
      gutterX = chart.getMaxGutter()[0];
      gutterY = chart.getMaxGutter()[1];
    }

    bbox = new PreciseRectangle();
    bbox.setX(chartBBox.getX() + gutterX);
    bbox.setY(chartBBox.getY() + gutterY);
    bbox.setWidth(chartBBox.getWidth());
    bbox.setHeight(chartBBox.getHeight());
  }

  /**
   * Removes all the sprites of the series from the surface.
   */
  public void clear() {
    lastHighlighted = -1;
    sprites.clear();
    for (Integer integer : labels.keySet()) {
      labels.get(integer).remove();
    }
    labels.clear();
    for (int i = 0; i < shadowGroups.size(); i++) {
      shadowGroups.get(i).clear();
    }
  }

  /**
   * Draws the series for the current chart.
   */
  public abstract void drawSeries();

  /**
   * Returns the bounding box of the series.
   * 
   * @return the bounding box of the series
   */
  public PreciseRectangle getBBox() {
    return bbox;
  }

  /**
   * Returns the chart that the series is attached.
   * 
   * @return the chart that the series is attached
   */
  public Chart<M> getChart() {
    return chart;
  }

  /**
   * Returns the axis insets of the series.
   * 
   * @return the axis insets of the series
   */
  public double[] getGutters() {
    double[] temp = {0, 0};
    return temp;
  }

  /**
   * Return the {@link SeriesHighlighter} used by the series.
   * 
   * @return the series highlighter
   */
  public SeriesHighlighter getHighlighter() {
    return highlighter;
  }

  /**
   * Returns the configuration for labels on the series.
   * 
   * @return the configuration for labels on the series
   */
  public SeriesLabelConfig<M> getLabelConfig() {
    return labelConfig;
  }

  /**
   * Returns the map of names used in the legend.
   * 
   * @return the map of names used in the legend
   */
  public Map<Integer, String> getLegendNames() {
    return legendNames;
  }

  /**
   * Returns the list of titles used in the legend of the series.
   * 
   * @return the list of titles used in the legend of the series
   */
  public List<String> getLegendTitles() {
    return legendTitles;
  }

  /**
   * Return the custom sprite renderer on the series.
   * 
   * @return the custom sprite renderer on the series
   */
  public SeriesRenderer<M> getRenderer() {
    return renderer;
  }

  /**
   * Return the custom shadow sprite renderer on the series.
   * 
   * @return the custom shadow sprite renderer on the series
   */
  public SeriesRenderer<M> getShadowRenderer() {
    return shadowRenderer;
  }

  /**
   * Return the stroke color of the series.
   * 
   * @return the stroke color of the series
   */
  public Color getStroke() {
    return stroke;
  }

  /**
   * Return the stroke width of the series.
   * 
   * @return the stroke width of the series
   */
  public double getStrokeWidth() {
    return strokeWidth;
  }

  /**
   * Return the generated tool tip from the tool tip config.
   * 
   * @return the generated tool tip
   */
  public ToolTip getToolTip() {
    if (toolTip == null && toolTipConfig != null) {
      toolTip = new ToolTip(null, toolTipConfig);
    }
    return toolTip;
  }

  /**
   * Returns the tooltip configuration.
   * 
   * @return the tooltip configuration
   */
  public SeriesToolTipConfig<M> getToolTipConfig() {
    return toolTipConfig;
  }

  /**
   * Attempts to get a simplified yet meaningful string from the given
   * {@link ValueProvider}.
   * 
   * @param provider the value provider
   * @param index the index to be used if no path
   * @return the name
   */
  public String getValueProviderName(ValueProvider<? super M, ? extends Number> provider, int index) {
    String name = provider.getPath();
    if (name == null || name.length() == 0) {
      if (index == 0) {
        if (chart != null) {
          name = "Series " + (chart.getSeries().indexOf(this));
        } else {
          name = "Series";
        }
      } else {
        name = "Field " + index;
      }
    }
    return name;
  }

  /**
   * Returns the value provider for the x-axis of the series.
   * 
   * @return the value provider for the x-axis of the series
   */
  public ValueProvider<? super M, ? extends Number> getXField() {
    return xField;
  }

  /**
   * Hides the given y field index from the series.
   * 
   * @param yFieldIndex the index of the y field
   */
  public abstract void hide(int yFieldIndex);

  /**
   * Immediately hides the tool tip shown in the series.
   */
  public void hideToolTip() {
    if (toolTip != null) {
      toolTip.hide();
    }
  }

  /**
   * Highlights the series at the given series index. The series index
   * represents the index of an item in the series. For example a bar in a bar
   * series.
   * 
   * @param index the series index to be highlighted
   */
  public abstract void highlight(int index);

  /**
   * Highlights all of the items in the series.
   * 
   * @param index the index of the series
   */
  public abstract void highlightAll(int index);

  /**
   * Returns whether or not the series is actively highlighted.
   * 
   * @return true if highlighted
   */
  public boolean highlightedState() {
    if (lastHighlighted != -1) {
      return true;
    }
    return false;
  }

  /**
   * Returns whether or not the series uses highlighting.
   * 
   * @return whether or not the series uses highlighting
   */
  public boolean isHighlighting() {
    return highlighting;
  }

  /**
   * Returns whether or not the series is shown in the legend.
   * 
   * @return true if the series is shown in the legend
   */
  public boolean isShownInLegend() {
    return shownInLegend;
  }

  /**
   * Method used when the series is clicked.
   * 
   * @param point the point clicked
   */
  public void onMouseDown(PrecisePoint point, Event event) {
    if (handlerManager != null) {
      int index = getIndex(point);
      if (index > -1) {
        ensureHandlers().fireEvent(
            new SeriesSelectionEvent<M>(chart.getCurrentStore().get(getStoreIndex(index)), getValueProvider(index),
                index, event));
      }
    }
  }

  /**
   * Method used when the series is moused over.
   * 
   * @param point the point moused over
   * @return the index of the moused over item
   */
  public int onMouseMove(PrecisePoint point, Event event) {
    if (toolTip != null || highlighting || handlerManager != null) {
      int index = getIndex(point);
      if (lastHighlighted > -1 && index != lastHighlighted) {
        onMouseOut(point, event);
      }
      if (index > -1) {
        ensureHandlers().fireEvent(
            new SeriesItemOverEvent<M>(chart.getCurrentStore().get(getStoreIndex(index)), getValueProvider(index),
                index, event));
        if (toolTip != null) {
          SeriesLabelProvider<M> labelProvider = toolTipConfig.getLabelProvider();
          if (labelProvider != null) {
            toolTipConfig.setBodyText(labelProvider.getLabel(chart.getCurrentStore().get(getStoreIndex(index)),
                getValueProvider(index)));
            toolTip.update(toolTipConfig);
          }
          if (!toolTipShown) {
            toolTip.showAt(
                (int) Math.round(point.getX() + chart.getAbsoluteLeft() + toolTipConfig.getMouseOffset()[0]),
                (int) Math.round(point.getY() + chart.getAbsoluteTop() + toolTipConfig.getMouseOffset()[1]));
            if (!toolTipConfig.isTrackMouse()) {
              toolTipShown = true;
            }
          }
        }
        if (highlighting) {
          highlight(index);
        }
        lastHighlighted = index;
      }

      return index;
    } else {
      return -1;
    }
  }

  /**
   * Method used when the mouse leaves the series.
   * 
   * @param point the point left
   */
  public void onMouseOut(PrecisePoint point, Event event) {
    if (toolTip != null || highlighting || handlerManager != null) {
      if (lastHighlighted != -1) {
        ensureHandlers().fireEvent(
            new SeriesItemOutEvent<M>(chart.getCurrentStore().get(getStoreIndex(lastHighlighted)),
                getValueProvider(lastHighlighted), lastHighlighted, event));
        if (highlighting) {
          unHighlight(lastHighlighted);
        }
        lastHighlighted = -1;
        if (toolTipConfig != null && !toolTipConfig.isTrackMouse()) {
          toolTipShown = false;
        }
      }
    }
  }

  /**
   * Removes the components tooltip (if one exists).
   */
  public void removeToolTip() {
    if (toolTip != null) {
      toolTip.initTarget(null);
      toolTip = null;
      toolTipConfig = null;
    }
  }

  /**
   * Sets the chart that the series is attached.
   * 
   * @param chart the chart that the series is attached
   */
  public void setChart(Chart<M> chart) {
    this.chart = chart;
  }

  /**
   * Set the {@link SeriesHighlighter} used by the series.
   * 
   * @param highlighter the series highlighter
   */
  public void setHighlighter(SeriesHighlighter highlighter) {
    this.highlighter = highlighter;
  }

  /**
   * Sets whether or not the series uses highlighting.
   * 
   * @param highlighting whether or not the series uses highlighting
   */
  public void setHighlighting(boolean highlighting) {
    this.highlighting = highlighting;
  }

  /**
   * Sets the configuration for labels on the series.
   * 
   * @param labelConfig the label configuration
   */
  public void setLabelConfig(SeriesLabelConfig<M> labelConfig) {
    if (this.labelConfig != labelConfig) {
      this.labelConfig = labelConfig;
      for (Integer integer : labels.keySet()) {
        labels.get(integer).remove();
      }
      labels.clear();
    }
  }

  /**
   * Sets a custom sprite renderer on the series.
   * 
   * @param renderer the renderer
   */
  public void setRenderer(SeriesRenderer<M> renderer) {
    this.renderer = renderer;
  }

  /**
   * Sets a custom shadow sprite renderer on the series.
   * 
   * @param shadowRenderer the renderer
   */
  public void setShadowRenderer(SeriesRenderer<M> shadowRenderer) {
    this.shadowRenderer = shadowRenderer;
  }

  /**
   * Sets whether or not the series is shown in the legend.
   * 
   * @param showInLegend true if the series is shown in the legend
   */
  public void setShownInLegend(boolean showInLegend) {
    this.shownInLegend = showInLegend;
  }

  /**
   * Sets the stroke color of the series.
   * 
   * @param stroke the stroke color of the series
   */
  public void setStroke(Color stroke) {
    this.stroke = stroke;
  }

  /**
   * Sets the stroke width of the series.
   * 
   * @param strokeWidth the stroke width of the series
   */
  public void setStrokeWidth(double strokeWidth) {
    this.strokeWidth = strokeWidth;
  }

  /**
   * Sets the tooltip configuration.
   * 
   * @param config the tooltip configuration
   */
  public void setToolTipConfig(SeriesToolTipConfig<M> config) {
    this.toolTipConfig = config;
    if (config != null) {
      if (toolTip == null) {
        toolTip = new ToolTip(null, config);
      } else {
        toolTip.update(config);
      }
    } else if (config == null) {
      removeToolTip();
    }
  }

  /**
   * Sets the value provider for the x-axis of the series.
   * 
   * @param xField the value provider for the x-axis of the series
   */
  public void setXField(ValueProvider<? super M, ? extends Number> xField) {
    this.xField = xField;
  }

  /**
   * Shows the given y field index from the series.
   * 
   * @param yFieldIndex the index of the y field
   */
  public abstract void show(int yFieldIndex);

  /**
   * Removes highlighting from the given series index. The series index
   * represents the index of an item in the series. For example a bar in a bar
   * series.
   * 
   * @param index the index to have its highlighting removed
   */
  public abstract void unHighlight(int index);

  /**
   * UnHighlights all items in the series.
   * 
   * @param index the index of the series
   */
  public abstract void unHighlightAll(int index);

  /**
   * Returns whether or not the given series index is visible in legend. The
   * series index represents the index of an item in the series. For example a
   * bar in a bar series.
   * 
   * @param index the series index to determine visible
   * @return whether or not it is visible
   */
  public abstract boolean visibleInLegend(int index);

  protected HandlerManager ensureHandlers() {
    if (handlerManager == null) {
      handlerManager = new HandlerManager(this);
    }
    return handlerManager;
  }

  /**
   * Returns the series index from the given point. The series index represents
   * the index of an item in the series. For example a bar in a bar series.
   * 
   * @param point the point get the series index
   * @return the series index
   */
  protected abstract int getIndex(PrecisePoint point);

  /**
   * Returns the end point of the given command.
   * 
   * @param command the command to get the point from
   * @return the end point of the command
   */
  protected PrecisePoint getPointFromCommand(PathCommand command) {
    if (command instanceof EndPointCommand) {
      EndPointCommand end = (EndPointCommand) command;
      return new PrecisePoint(end.getX(), end.getY());
    } else {
      return new PrecisePoint();
    }
  }

  /**
   * Returns the appropriate index on the store for the given series index. The
   * series index represents the index of an item in the series. For example a
   * bar in a bar series.
   * 
   * @param index the series index
   * @return the store index
   */
  protected abstract int getStoreIndex(int index);

  /**
   * Returns the value at the given series index. The series index represents
   * the index of an item in the series. For example a bar in a bar series.
   * 
   * @param index the series index
   * @return the value
   */
  protected abstract ValueProvider<? super M, ? extends Number> getValueProvider(int index);

  protected void hideShadows() {
    if (shadowed) {
      for (int i = 0; i < shadowGroups.size(); i++) {
        SpriteList<Sprite> shadows = shadowGroups.get(i);
        for (int j = 0; j < shadows.size(); j++) {
          shadows.get(j).setHidden(true);
          shadows.get(j).redraw();
        }
      }
      shadowed = false;
    }
  }

  protected void setLabelContrast(Sprite label, SeriesLabelConfig<M> config, Sprite sprite) {
    Color fill = config.getSpriteConfig().getFill();
    Color spriteFill = sprite.getFill();
    double labelBrightness;
    if (fill instanceof RGB && spriteFill instanceof RGB) {
      RGB rgb = (RGB) fill;
      RGB rgbSprite = (RGB) spriteFill;
      if (config.getLabelPosition() != LabelPosition.OUTSIDE) {
        labelBrightness = rgbSprite.getGrayScale() / 255.0;
      } else {
        labelBrightness = 1;
      }
      HSL hsl = new HSL(rgb);
      hsl.setLightness(labelBrightness > 0.5 ? 0.2 : 0.8);
      label.setFill(new RGB(hsl));
      label.redraw();
    }

  }

  /**
   * Generates label text for the given sprite at the given series index. The
   * series index represents the index of an item in the series. For example a
   * bar in a bar series.
   * 
   * @param sprite the sprite to be set
   * @param index the series index of the label data
   */
  protected void setLabelText(Sprite sprite, int index) {
    if (sprite instanceof TextSprite) {
      TextSprite text = (TextSprite) sprite;
      if (labelConfig.getLabelProvider() != null) {
        text.setText(labelConfig.getLabelProvider().getLabel(chart.getCurrentStore().get(getStoreIndex(index)),
            getValueProvider(index)));
      }
    }
  }

}
