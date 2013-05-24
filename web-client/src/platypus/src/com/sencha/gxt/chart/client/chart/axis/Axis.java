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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.event.AxisHandler;
import com.sencha.gxt.chart.client.chart.event.AxisHandler.HasAxisHandlers;
import com.sencha.gxt.chart.client.chart.event.AxisItemOutEvent;
import com.sencha.gxt.chart.client.chart.event.AxisItemOutEvent.AxisItemOutHandler;
import com.sencha.gxt.chart.client.chart.event.AxisItemOutEvent.HasAxisItemOutHandlers;
import com.sencha.gxt.chart.client.chart.event.AxisItemOverEvent;
import com.sencha.gxt.chart.client.chart.event.AxisItemOverEvent.AxisItemOverHandler;
import com.sencha.gxt.chart.client.chart.event.AxisItemOverEvent.HasAxisItemOverHandlers;
import com.sencha.gxt.chart.client.chart.event.AxisSelectionEvent;
import com.sencha.gxt.chart.client.chart.event.AxisSelectionEvent.AxisSelectionHandler;
import com.sencha.gxt.chart.client.chart.event.AxisSelectionEvent.HasAxisSelectionHandlers;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteList;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.tips.ToolTip;

/**
 * Defines axis for charts. The axis position, type, style can be configured.
 * The axes are defined in an axes array of configuration objects where the
 * type, field, grid and other configuration options can be set. To know more
 * about how to create a Chart please check the Chart class documentation.
 * 
 * @param <M> the data type of the axis
 * @param <V> the variable type of axis
 */
public abstract class Axis<M, V> implements HasAxisHandlers, HasAxisItemOutHandlers, HasAxisItemOverHandlers,
    HasAxisSelectionHandlers {

  protected int steps = 10;
  protected boolean displayGrid = false;
  protected boolean hidden = false;
  protected double x = 0;
  protected double y = 0;
  protected double from;
  protected double to;
  protected Chart<M> chart;
  protected PreciseRectangle bbox = new PreciseRectangle();
  protected PathSprite axisConfig;
  protected SpriteList<PathSprite> lines = new SpriteList<PathSprite>();
  protected TextSprite titleSprite;
  protected TextSprite titleConfig;
  protected ToolTip toolTip;
  protected AxisToolTipConfig<M> toolTipConfig;
  protected int lastIndex = -1;
  protected TextSprite labelConfig = new TextSprite();
  protected SpriteList<TextSprite> labels = new SpriteList<TextSprite>();
  protected List<V> labelNames = new ArrayList<V>();
  protected LabelProvider<? super V> labelProvider = new StringLabelProvider<V>();
  private HandlerManager handlerManager;

  /**
   * Create an axis.
   */
  public Axis() {
    axisConfig = new PathSprite();
    axisConfig.setStroke(RGB.BLACK);
    axisConfig.setStrokeWidth(1);
    axisConfig.setZIndex(1);
  }

  public HandlerRegistration addAxisHandler(AxisHandler handler) {
    GroupingHandlerRegistration reg = new GroupingHandlerRegistration();
    reg.add(ensureHandlers().addHandler(AxisSelectionEvent.getType(), handler));
    reg.add(ensureHandlers().addHandler(AxisItemOutEvent.getType(), handler));
    reg.add(ensureHandlers().addHandler(AxisItemOverEvent.getType(), handler));
    return reg;
  }

  @Override
  public HandlerRegistration addAxisItemOutHandler(AxisItemOutHandler handler) {
    return ensureHandlers().addHandler(AxisItemOutEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addAxisItemOverHandler(AxisItemOverHandler handler) {
    return ensureHandlers().addHandler(AxisItemOverEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addAxisSelectionHandler(AxisSelectionHandler handler) {
    return ensureHandlers().addHandler(AxisSelectionEvent.getType(), handler);
  }

  /**
   * Removes all the sprites of the axis from the surface.
   */
  public void clear() {
    while (lines.size() > 0) {
      PathSprite sprite = lines.remove(lines.size() - 1);
      if (sprite != null) {
        sprite.remove();
      }
    }
    while (labels.size() > 0) {
      TextSprite sprite = labels.remove(labels.size() - 1);
      if (sprite != null) {
        sprite.remove();
      }
    }
    if (titleSprite != null) {
      titleSprite.remove();
      titleSprite = null;
    }
  }

  /**
   * Renders the axis into the screen and updates it's position.
   * 
   * @param init true if the initial draw call
   */
  public abstract void drawAxis(boolean init);

  /**
   * Renders the labels in the axes.
   */
  public abstract void drawLabels();

  /**
   * Returns the configuration for the axis lines.
   * 
   * @return the configuration for the axis lines
   */
  public PathSprite getAxisConfig() {
    return axisConfig;
  }

  /**
   * Returns the bounding box of the axis.
   * 
   * @return the bounding box of the axis
   */
  public PreciseRectangle getBBox() {
    return bbox;
  }

  /**
   * Returns the chart that the axis is currently attached.
   * 
   * @return the chart that the axis is currently attached
   */
  public Chart<M> getChart() {
    return chart;
  }

  /**
   * Returns the starting value of the axis.
   * 
   * @return the starting value of the axis
   */
  public double getFrom() {
    return from;
  }

  /**
   * Returns the configuration for labels on the axis.
   * 
   * @return the configuration for labels on the axis
   */
  public TextSprite getLabelConfig() {
    return labelConfig;
  }

  /**
   * Returns the {@link LabelProvider} used to provide labels displayed on the
   * axis.
   * 
   * @return the label provider used to provide labels displayed on the axis
   */
  public LabelProvider<? super V> getLabelProvider() {
    return labelProvider;
  }

  /**
   * Returns the {@link Position} of the axis.
   * 
   * @return the position of the axis
   */
  public abstract Position getPosition();

  /**
   * Returns the configuration of the title.
   * 
   * @return the configuration of the title
   */
  public TextSprite getTitleConfig() {
    return titleConfig;
  }

  /**
   * Returns the ending value of the axis.
   * 
   * @return the ending value of the axis
   */
  public double getTo() {
    return to;
  }

  /**
   * Returns the x-coordinate of the axis.
   * 
   * @return the x-coordinate of the axis
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y-coordinate of the axis.
   * 
   * @return the y-coordinate of the axis
   */
  public double getY() {
    return y;
  }

  /**
   * Returns whether or not the axis has a grid.
   * 
   * @return true if grid
   */
  public boolean isDisplayGrid() {
    return displayGrid;
  }

  /**
   * Returns whether or not the axis is hidden.
   * 
   * @return true if hidden
   */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Method used when the axis is clicked.
   * 
   * @param point the point clicked
   */
  public void onMouseDown(PrecisePoint point, Event event) {
    if (handlerManager != null) {
      int index = getIndex(point);
      if (index == labels.size()) {
        ensureHandlers().fireEvent(new AxisSelectionEvent(titleSprite.getText(), index, event));
      } else if (index > -1) {
        ensureHandlers().fireEvent(new AxisSelectionEvent(labels.get(index).getText(), index, event));
      }
    }
  }

  /**
   * Method used when the axis is moused over.
   * 
   * @param point the point moused over
   * @return the index of the moused over item
   */
  public int onMouseMove(PrecisePoint point, Event event) {
    if (toolTip != null || handlerManager != null) {
      int index = getIndex(point);
      if (lastIndex > -1 && index != lastIndex) {
        onMouseOut(point, event);
      }
      if (index > -1) {
        String text = null;
        if (index == labels.size()) {
          text = titleSprite.getText();
          ensureHandlers().fireEvent(new AxisItemOverEvent(titleSprite.getText(), index, event));
        } else if (index > -1) {
          text = labels.get(index).getText();
          ensureHandlers().fireEvent(new AxisItemOverEvent(labels.get(index).getText(), index, event));
        }
        if (toolTip != null) {
          LabelProvider<? super M> custom = toolTipConfig.getCustomLabelProvider();
          if (custom != null) {
            toolTipConfig.setBodyText(custom.getLabel(chart.getCurrentStore().get(index)));
          } else {
            toolTipConfig.setBodyText(text);
          }
          toolTip.update(toolTipConfig);
          toolTip.showAt((int) Math.round(point.getX() + chart.getAbsoluteLeft() + 20),
              (int) Math.round(point.getY() + chart.getAbsoluteTop() + 20));
        }
        lastIndex = index;
      }
      return index;
    }
    return -1;
  }

  /**
   * Method used when the mouse leaves the axis.
   * 
   * @param point the point left
   */
  public void onMouseOut(PrecisePoint point, Event event) {
    if (toolTip != null) {
      toolTip.hide();
    }
    if (lastIndex != -1) {
      if (lastIndex == labels.size()) {
        ensureHandlers().fireEvent(new AxisItemOutEvent(titleSprite.getText(), lastIndex, event));
      } else if (lastIndex > -1) {
        ensureHandlers().fireEvent(new AxisItemOutEvent(labels.get(lastIndex).getText(), lastIndex, event));
      }
      lastIndex = -1;
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
   * Sets the configuration for the axis lines.
   * 
   * @param axisConfig the configuration for the axis lines
   */
  public void setAxisConfig(PathSprite axisConfig) {
    if (this.axisConfig != axisConfig) {
      this.axisConfig = axisConfig;
      lines.clear();
    }
  }

  /**
   * Sets the chart that the axis is attached.
   * 
   * @param chart the chart that the axis is attached
   */
  public void setChart(Chart<M> chart) {
    this.chart = chart;
  }

  /**
   * Sets whether or not the axis has a grid.
   * 
   * @param displayGrid true if grid
   */
  public void setDisplayGrid(boolean displayGrid) {
    this.displayGrid = displayGrid;
  }

  /**
   * Sets whether or not the axis is hidden.
   * 
   * @param hidden true if hidden
   */
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
    if (hidden) {
      clear();
    }
  }

  /**
   * Sets the configuration for labels on the axis.
   * 
   * @param labelConfig the configuration for labels on the axis
   */
  public void setLabelConfig(TextSprite labelConfig) {
    if (this.labelConfig != labelConfig) {
      this.labelConfig = labelConfig;
      labels.clear();
    }
  }

  /**
   * Sets the {@link LabelProvider} used to provide labels displayed on the
   * axis.
   * 
   * @param labelProvider the label provider used to provide labels displayed on
   *          the axis
   */
  public void setLabelProvider(LabelProvider<? super V> labelProvider) {
    this.labelProvider = labelProvider;
  }

  /**
   * Sets the configuration of the title.
   * 
   * @param titleConfig the configuration of the title
   */
  public void setTitleConfig(TextSprite titleConfig) {
    if (this.titleConfig != titleConfig) {
      this.titleConfig = titleConfig;
      titleConfig.remove();
      titleConfig = null;
    }
  }

  /**
   * Sets the tooltip configuration.
   * 
   * @param config the tooltip configuration
   */
  public void setToolTipConfig(AxisToolTipConfig<M> config) {
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
   * Sets the x-coordinate of the axis.
   * 
   * @param x the x-coordinate of the axis
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Sets the y-coordinate of the axis.
   * 
   * @param y the y-coordinate of the axis
   */
  public void setY(double y) {
    this.y = y;
  }

  protected HandlerManager ensureHandlers() {
    if (handlerManager == null) {
      handlerManager = new HandlerManager(this);
    }
    return handlerManager;
  }

  private int getIndex(PrecisePoint point) {
    for (int i = 0; i < labels.size(); i++) {
      if (labels.get(i).getBBox().contains(point)) {
        return i;
      }
    }
    if (titleSprite != null && titleSprite.getBBox().contains(point)) {
      return labels.size();
    }
    return -1;
  }

}
