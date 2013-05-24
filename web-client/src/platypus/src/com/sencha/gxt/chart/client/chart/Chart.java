/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.chart.client.chart.axis.Axis;
import com.sencha.gxt.chart.client.chart.axis.CartesianAxis;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.axis.TimeAxis;
import com.sencha.gxt.chart.client.chart.series.LineSeries;
import com.sencha.gxt.chart.client.chart.series.ScatterSeries;
import com.sencha.gxt.chart.client.chart.series.Series;
import com.sencha.gxt.chart.client.draw.DrawComponent;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.fx.client.easing.Default;
import com.sencha.gxt.fx.client.easing.EasingFunction;

/**
 * Charts provide a flexible way to achieve a wide range of data visualization
 * capabilities. Each Chart gets its data directly from a {@link ListStore}, and
 * automatically updates its display whenever data in the Store changes.
 * 
 * ## Creating a Simple Chart
 * 
 * Every Chart has three key parts - a {@link ListStore} that contains the data,
 * multiple {@link Axis} types which define the boundaries of the Chart, and one
 * or more {@link Series} to handle the visual rendering of the data points.
 * 
 * ### 1. Creating a Store
 * 
 * The first step is to create a model that represents the type of data that
 * will be displayed in the Chart. Here is an example of a simple class with a
 * field for the name key and a single data value:
 * 
 * <pre>
     public class Data {
       private String name;
       private double data1;
       
       public Data(String name, double data1) {
         this.name = name;
         this.data1 = data1;
       }
       
       public double getData1() {
         return data1;
       }
       
       public String getName() {
         return name;
       }
       
       public void setData1(double data1) {
         this.data1 = data1;
       }
       
       public void setName(String name) {
         this.name = name;
       }
     }
 * </pre>
 * 
 * Next create the {@link ValueProvider} interface for the model fields:
 * 
 * <pre>
    public interface DataPropertyAccess extends PropertyAccess{@literal <}Data{@literal >} {
      ValueProvider{@literal <}Data, Double{@literal >} data1();
  
      ValueProvider{@literal <}Data, Double{@literal >} data2();
  
      ValueProvider{@literal <}Data, Double{@literal >} data3();
  
      ValueProvider{@literal <}Data, String{@literal >} name();
  
      {@literal @}Path("name")
      ModelKeyProvider{@literal <}Data{@literal >} nameKey();
    }

    private static final DataPropertyAccess dataAccess = GWT.create(DataPropertyAccess.class);
 * </pre>
 * 
 * Finally create the store itself. The data could be loaded dynamically, but
 * for sake of ease this example uses in line data:
 * 
 * <pre>
    ListStore{@literal <}Data{@literal >} store = new ListStore{@literal <}Data{@literal >}(dataAccess.nameKey());
    store.add(new Data("A", 50);
    store.add(new Data("B", 60);
    store.add(new Data("C", 70);
    store.add(new Data("D", 80);
    store.add(new Data("E", 60);
 * </pre>
 * 
 * ### 2. Creating the Chart object
 * 
 * Now that a Store has been created it can be used in a Chart:
 * 
 * <pre>
    Chart{@literal <}Data{@literal >} chart = new Chart{@literal <}Data{@literal >}(600, 500);
    chart.setStore(store);
    chart.setAnimated(true);
    chart.setShadowChart(true);
 * </pre>
 * 
 * That's all it takes to create a Chart instance that is backed by a Store.
 * However, if the above code is run in a browser, a blank screen will be
 * displayed. This is because the two pieces that are responsible for the visual
 * display, the Chart's axes and series, have not yet been defined.
 * 
 * ### 3. Configuring the Axes
 * 
 * Axes are the lines that define the boundaries of the data points that a Chart
 * can display. This example uses one of the most common Axes configurations - a
 * horizontal "x" axis, and a vertical "y" axis:
 * 
 * <pre>
    NumericAxis{@literal <}Data{@literal >} verticalAxis = new NumericAxis{@literal <}Data{@literal >}();
    verticalAxis.setPosition(Position.LEFT);
    verticalAxis.addField(dataAccess.data1());
    verticalAxis.setMinimum(0);
    verticalAxis.setMaximum(100);
    chart.addAxis(verticalAxis);
    
    CategoryAxis{@literal <}Data, String{@literal >} horizontalAxis = new CategoryAxis{@literal <}Data, String{@literal >}();
    horizontalAxis.setPosition(Position.BOTTOM);
    horizontalAxis.setField(dataAccess.name());
    chart.addAxis(horizontalAxis);
 * </pre>
 * 
 * The vertical axis is a {@link NumericAxis} and is positioned on the left edge
 * of the Chart. It represents the bounds of the data contained in the "data1"
 * field that was defined above. The minimum value for this axis is "0", and the
 * maximum is "100".
 * 
 * The horizontal axis is a {@link CategoryAxis} and is positioned on the bottom
 * edge of the Chart. It represents the "name" of each item in the store.
 * 
 * ### 4. Configuring the Series
 * 
 * The final step in creating a simple Chart is to configure one or more Series.
 * Series are responsible for the visual representation of the data points
 * contained in the Store. This example only has one Series:
 * 
 * <pre>
    LineSeries{@literal <}Data{@literal >} series = new LineSeries{@literal <}Data{@literal >}();
    series.setYAxisPosition(Position.LEFT);
    series.setYField(dataAccess.data1());
    series.setStroke(new RGB(32, 68, 186));
    series.setStrokeWidth(3);
    series.setSmooth(true);
    chart.addSeries(series);
 * </pre>
 * 
 * This Series is a {@link LineSeries}, and it uses the "data1" field from the
 * Store to plot its data points.
 * 
 * @param <M> the data type to be used by this Chart
 * 
 */
public class Chart<M> extends DrawComponent {

  /**
   * Enumeration used for items positioned on the chart.
   */
  public enum Position {
    /**
     * Positioned at the top of the chart.
     */
    TOP,
    /**
     * Positioned at the bottom of the chart.
     */
    BOTTOM,
    /**
     * Positioned at the left of the chart.
     */
    LEFT,
    /**
     * Positioned at the right of the chart.
     */
    RIGHT
  }

  private boolean animated = false;
  private boolean resizing = false;
  private boolean shadow = false;
  private double defaultInsets = 10;
  private int currentWidth;
  private int currentHeight;
  private PreciseRectangle bbox;
  private ListStore<M> store;
  private ListStore<M> substore;
  private double[] maxGutter = {0, 0};
  private List<Series<M>> series = new ArrayList<Series<M>>();
  private Map<Position, Axis<M, ?>> axes = new HashMap<Position, Axis<M, ?>>();
  private Legend<M> legend;
  private int lastLegend = -1;
  private Series<M> lastSeries;
  private Axis<M, ?> lastAxis;
  private EasingFunction animationEasing = new Default();
  private int animationDuration = 500;
  private StoreHandlers<M> storeHandlers;
  private HandlerRegistration handlerRegistration;
  private boolean deferred = false;

  /**
   * Creates a chart with default width and height.
   */
  public Chart() {
    this(1, 1);
  }

  /**
   * Creates a chart with the given width and height.
   * 
   * @param width the width of the chart
   * @param height the height of the chart
   */
  public Chart(int width, int height) {
    super(width, height);
    setPixelSize(width, height);

    storeHandlers = new StoreHandlers<M>() {

      @Override
      public void onAdd(StoreAddEvent<M> event) {
        redrawChart();
      }

      @Override
      public void onClear(StoreClearEvent<M> event) {
        redrawChart();
      }

      @Override
      public void onDataChange(StoreDataChangeEvent<M> event) {
        redrawChart();
      }

      @Override
      public void onFilter(StoreFilterEvent<M> event) {
        redrawChart();
      }

      @Override
      public void onRecordChange(StoreRecordChangeEvent<M> event) {
        redrawChart();
      }

      @Override
      public void onRemove(StoreRemoveEvent<M> event) {
        redrawChart();
      }

      @Override
      public void onSort(StoreSortEvent<M> event) {
        redrawChart();
      }

      @Override
      public void onUpdate(StoreUpdateEvent<M> event) {
        redrawChart();
      }
    };
  }

  /**
   * Adds an axis to the chart. Only one axis for each {@link Position} can be
   * added.
   * 
   * @param axis the axis to be added
   */
  public void addAxis(Axis<M, ?> axis) {
    axes.put(axis.getPosition(), axis);
    axis.setChart(this);
  }

  /**
   * Adds a {@link Series} to the chart.
   * 
   * @param series the series to be added
   */
  public void addSeries(Series<M> series) {
    this.series.add(series);
    series.setChart(this);
  }

  /**
   * Binds the store to the chart so that any store events will automatically
   * redraw the chart.
   * 
   * @param store the store to be bound
   */
  public void bindStore(ListStore<M> store) {
    if (this.store != null) {
      handlerRegistration.removeHandler();
    }
    if (store != null) {
      handlerRegistration = store.addStoreHandlers(storeHandlers);
    }
    setStore(store);
  }

  /**
   * Calculates the maximum gutter of the chart's series.
   */
  public void calculateMaximumGutter() {
    maxGutter[0] = 0;
    maxGutter[1] = 0;
    for (Series<M> series : this.series) {
      double gutter[] = series.getGutters();
      maxGutter[0] = Math.max(maxGutter[0], gutter[0]);
      maxGutter[1] = Math.max(maxGutter[1], gutter[1]);
    }
  }

  /**
   * Returns the duration of animations on the chart.
   * 
   * @return the duration of animations on the chart
   */
  public int getAnimationDuration() {
    return animationDuration;
  }

  /**
   * Returns the {@link EasingFunction} used by animations on the chart.
   * 
   * @return the easing function used by animations on the chart
   */
  public EasingFunction getAnimationEasing() {
    return animationEasing;
  }

  /**
   * Returns the map of axes currently added to the chart.
   * 
   * @return the map of axes currently added to the chart
   */
  public Map<Position, Axis<M, ?>> getAxes() {
    return axes;
  }

  /**
   * Returns the {@link Axis} at the given {@link Position}.
   * 
   * @param position the position of the axis
   * @return the axis at the given position
   */
  public Axis<M, ?> getAxis(Position position) {
    return axes.get(position);
  }

  /**
   * Returns the bounding box of the chart.
   * 
   * @return the bounding box of the chart
   */
  public PreciseRectangle getBBox() {
    return bbox;
  }

  /**
   * Returns the store currently being used by the chart.
   * 
   * @return the store currently being used by the chart
   */
  public ListStore<M> getCurrentStore() {
    if (substore != null) {
      return substore;
    } else {
      return store;
    }
  }

  /**
   * Returns the initial insets of the chart.
   * 
   * @return the initial insets of the chart
   */
  public double getDefaultInsets() {
    return defaultInsets;
  }

  /**
   * Returns the legend of the chart.
   * 
   * @return the legend of the chart
   */
  public Legend<M> getLegend() {
    return legend;
  }

  /**
   * Returns maximum gutter of the series in the chart.
   * 
   * @return maximum gutter of the series in the chart
   */
  public double[] getMaxGutter() {
    return maxGutter;
  }

  /**
   * Returns all of the series attached to the chart.
   * 
   * @return all of the series attached to the chart
   */
  public List<Series<M>> getSeries() {
    return series;
  }

  /**
   * Returns the series attached to the chart at the given index.
   * 
   * @param index the index of the series
   * @return the series attached to the chart
   */
  public Series<M> getSeries(int index) {
    return series.get(index);
  }

  /**
   * Returns the data store bound to the chart.
   * 
   * @return the data store bound to the chart
   */
  public ListStore<M> getStore() {
    return store;
  }

  /**
   * Returns whether or not the chart has shadows.
   * 
   * @return true if shadows
   */
  public boolean hasShadows() {
    return shadow;
  }
  
  /**
   * Returns whether or not the chart is animated.
   * 
   * @return true if animated
   */
  public boolean isAnimated() {
    return animated;
  }

  /**
   * Returns whether or not the chart is resizing.
   * 
   * @return true if resizing
   */
  public boolean isResizing() {
    return resizing;
  }

  /**
   * Method used when the chart is clicked.
   * 
   * @param event the mouse event
   */
  @Override
  public void onMouseDown(Event event) {
    super.onMouseDown(event);
    PrecisePoint point = getEventXY(event);
    if (legend != null && legend.getBBox().contains(point)) {
      legend.onMouseDown(point, event);
    }
    if (lastSeries != null) {
      lastSeries.onMouseDown(point, event);
    }
    if (lastAxis != null) {
      lastAxis.onMouseDown(point, event);
    }
  }

  /**
   * Method used when the mouse moves over the chart.
   * 
   * @param event the mouse event
   */
  @Override
  public void onMouseMove(Event event) {
    super.onMouseMove(event);
    PrecisePoint point = getEventXY(event);
    if (legend != null && legend.getBBox().contains(point)) {
      lastLegend = legend.onMouseMove(point, event);
    } else if (lastLegend != -1) {
      legend.onMouseOut(lastLegend, event);
      lastLegend = -1;
    }

    for (Axis<M, ?> axis : this.axes.values()) {
      int index = axis.onMouseMove(point, event);
      if (index != -1) {
        lastAxis = axis;
      }
    }

    for (int i = 0; i < series.size(); i++) {
      Series<M> s = series.get(i);
      s.calculateBBox(false);
      double selectionTolerance = 0;
      if (s instanceof ScatterSeries) {
        selectionTolerance = ((ScatterSeries<M>) s).getSelectionTolerance();
      }
      if (s.getBBox().contains(point, selectionTolerance)) {
        int index = s.onMouseMove(point, event);
        if (index != -1) {
          lastSeries = s;
        }
      } else if (s.highlightedState()) {
        s.onMouseOut(point, event);
      }
    }
  }

  /**
   * Method used when the mouse leaves the chart.
   * 
   * @param event the mouse event
   */
  @Override
  public void onMouseOut(Event event) {
    super.onMouseOut(event);
    if (!getElement().getBounds().contains(event.getClientX(), event.getClientY())) {
      PrecisePoint point = getEventXY(event);
      for (int i = 0; i < series.size(); i++) {
        series.get(i).onMouseOut(point, event);
        series.get(i).hideToolTip();
      }
    }
    if (lastLegend != -1) {
      legend.onMouseOut(lastLegend, event);
      lastLegend = -1;
    }
    if (lastAxis != null) {
      lastAxis.onMouseOut(null, event);
      lastAxis = null;
    }
  }

  @Override
  public void onResize(int width, int height) {
    this.resizing = true;
    setPixelSize(width, height);
    redrawChart();
    super.onResize(width, height);
  }

  /**
   * Redraws all elements of the chart.
   */
  public void redrawChart() {
    if (!deferred) {
      deferred = true;
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          render();
        }
      });
    }
  }

  /**
   * Redraws all elements of the chart immediatley.
   */
  public void redrawChartForced() {
    render();
  }

  /**
   * Removes the {@link Axis} from the chart.
   * 
   * @param axis the axis to remove
   * @return the removed axis
   */
  public Axis<M, ?> removeAxis(Axis<M, ?> axis) {
    if (axis != null) {
      axis.clear();
      axis.setChart(null);
      return axes.remove(axis.getPosition());
    } else {
      return null;
    }
  }

  /**
   * Removes the {@link Axis} at the given {@link Position} from the chart.
   * 
   * @param position the position of the axis to remove
   * @return the removed axis
   */
  public Axis<M, ?> removeAxis(Position position) {
    return removeAxis(axes.get(position));
  }

  /**
   * Removes the {@link Legend} currently attached legend from the chart.
   */
  public void removeLegend() {
    if (legend != null) {
      legend.clear();
      legend.setChart(null);
      legend = null;
    }
  }

  /**
   * Removes the given {@link Series} from the chart.
   * 
   * @param index the index of the series to be removed
   * @return true if the series is removed
   */
  public boolean removeSeries(int index) {
    return removeSeries(getSeries(index));
  }

  /**
   * Removes the given {@link Series} from the chart.
   * 
   * @param series the series to be removed
   * @return true if the series is removed
   */
  public boolean removeSeries(Series<M> series) {
    series.clear();
    series.setChart(null);
    return this.series.remove(series);
  }
  
  /**
   * Sets whether or not the chart is animated.
   * 
   * @param animated true if animated
   */
  public void setAnimated(boolean animated) {
    this.animated = animated;
  }

  /**
   * Sets the duration of animations on the chart.
   * 
   * @param animationDuration the duration of animations on the chart
   */
  public void setAnimationDuration(int animationDuration) {
    this.animationDuration = animationDuration;
  }

  /**
   * Sets the {@link EasingFunction} used by animations on the chart.
   * 
   * @param animationEasing the easing function used by animations on the chart
   */
  public void setAnimationEasing(EasingFunction animationEasing) {
    this.animationEasing = animationEasing;
  }

  /**
   * Sets the initial insets of the chart.
   * 
   * @param defaultInsets the initial insets of the chart
   */
  public void setDefaultInsets(double defaultInsets) {
    this.defaultInsets = defaultInsets;
  }

  /**
   * Sets the legend of the chart.
   * 
   * @param legend the legend of the chart
   */
  public void setLegend(Legend<M> legend) {
    legend.setChart(this);
    this.legend = legend;
  }

  @Override
  public void setPixelSize(int width, int height) {
    super.setPixelSize(width, height);
    if (currentWidth != width && currentHeight != height) {
      bbox = new PreciseRectangle(0, 0, width, height);
    }
    currentWidth = width;
    currentHeight = height;
  }

  /**
   * Sets whether or not the chart has shadows.
   * 
   * @param shadow true if shadows
   */
  public void setShadowChart(boolean shadow) {
    this.shadow = shadow;
  }

  /**
   * Sets the data store bound to the chart.
   * 
   * @param store the data store
   */
  public void setStore(ListStore<M> store) {
    this.store = store;
  }

  /**
   * Sets a temporary substore for the chart. Used by {@link TimeAxis}.
   * 
   * @param substore substore for the chart
   */
  public void setSubstore(ListStore<M> substore) {
    this.substore = substore;
  }

  /**
   * Adjust the dimensions and positions of each axis and the chart body area
   * after accounting for the space taken up on each side by the axes and
   * legend.
   */
  private void alignAxes() {
    Position[] edges = Position.values();
    Map<Position, Double> insets = new HashMap<Position, Double>();
    // Find the space needed by axes and legend as a positive inset from each
    // edge
    for (int i = 0; i < edges.length; i++) {
      boolean isVertical = (edges[i] == Position.LEFT || edges[i] == Position.RIGHT);
      Axis<M, ?> axis = getAxis(edges[i]);
      double inset = defaultInsets;

      // Add legend size if it's on this edge
      if (legend != null) {
        if (legend.getPosition() == edges[i]) {
          PreciseRectangle bbox = legend.getBBox();
          if (isVertical) {
            inset += bbox.getWidth() + inset;
          } else {
            inset += bbox.getHeight() + inset;
          }
        }
      }

      // Add axis size if there's one on this edge
      if (axis != null) {
        PreciseRectangle bbox = axis.getBBox();
        if (isVertical) {
          inset += bbox.getWidth();
        } else {
          inset += bbox.getHeight();
        }
      }

      insets.put(edges[i], inset);
    }
    // Build the chart bbox based on the collected inset values
    bbox.setX(insets.get(Position.LEFT));
    bbox.setY(insets.get(Position.TOP));
    bbox.setWidth(currentWidth - insets.get(Position.LEFT) - insets.get(Position.RIGHT));
    bbox.setHeight(currentHeight - insets.get(Position.TOP) - insets.get(Position.BOTTOM));

    // Go back through each axis and set its length and position based on the
    // corresponding edge of the chartBBox
    for (Axis<M, ?> ax : this.axes.values()) {
      if (ax instanceof CartesianAxis) {
        CartesianAxis<M, ?> axis = (CartesianAxis<M, ?>) ax;
        Position pos = axis.getPosition();
        boolean isVertical = (pos == Position.LEFT || pos == Position.RIGHT);
        axis.setX((pos == Position.RIGHT) ? (bbox.getX() + bbox.getWidth()) : (bbox.getX()));
        axis.setY((pos == Position.TOP) ? (bbox.getY()) : (bbox.getY() + bbox.getHeight()));
        axis.setDepth(isVertical ? bbox.getWidth() : bbox.getHeight());
        axis.setLength(isVertical ? bbox.getHeight() : bbox.getWidth());
      }
    }
  }

  /**
   * Creates and initializes the default axis.
   * 
   * @param axis the axis to be initialized
   */
  private void initializeAxis(Axis<M, ?> ax) {
    if (ax instanceof CartesianAxis) {
      CartesianAxis<M, ?> axis = (CartesianAxis<M, ?>) ax;
      double w = bbox.getWidth();
      double h = bbox.getHeight();
      double x = bbox.getX();
      double y = bbox.getY();
      switch (axis.getPosition()) {
        case TOP:
          axis.setLength(w);
          axis.setDepth(h);
          axis.setX(x);
          axis.setY(y);
          break;
        case BOTTOM:
          axis.setLength(w);
          axis.setDepth(h);
          axis.setX(x);
          axis.setY(h);
          break;
        case LEFT:
          axis.setLength(h);
          axis.setDepth(w);
          axis.setX(x);
          axis.setY(h);
          break;
        case RIGHT:
          axis.setLength(h);
          axis.setDepth(w);
          axis.setX(w);
          axis.setY(h);
          break;
      }
    }
    ax.drawAxis(true);
  }

  private void render() {
    // Instantiate Series and Axes
    if (surface.getSurfaceElement() == null) {
      surface.draw();
    }
    for (Axis<M, ?> axis : this.axes.values()) {
      initializeAxis(axis);
    }

    // Create legend if not already created
    if (legend != null) {
      legend.create();
    }

    // Place axes properly, including influence from each other
    this.alignAxes();

    // Reposition legend based on new axis alignment
    if (legend != null) {
      legend.updatePosition();
    }

    // Find the max gutter
    this.calculateMaximumGutter();

    // Draw axes and series
    for (Axis<M, ?> axis : this.axes.values()) {
      axis.drawAxis(false);
    }
    for (Series<M> series : this.series) {
      series.drawSeries();
    }

    deferred = false;
    resizing = false;
  }

}
