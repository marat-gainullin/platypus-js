/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.axis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.sencha.gxt.chart.client.chart.RoundNumberProvider;
import com.sencha.gxt.chart.client.chart.series.AreaSeries;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.chart.series.Series;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;

/**
 * An axis to handle numeric values. This axis is used for quantitative data as
 * opposed to the category axis. You can set minimum and maximum values to the
 * axis so that the values are bound to that. If no values are set, then the
 * scale will auto-adjust to the values.
 * 
 * @param <M> the data type that the axis uses
 */
public class NumericAxis<M> extends CartesianAxis<M, Number> {

  /**
   * Class that represents a tick mark step in {@link CartesianAxis}.
   */
  public class Step {

    private double value;
    private double weight;

    /**
     * Creates a tick step.
     */
    public Step() {
    }

    /**
     * Creates a tick step with given value and weight.
     * 
     * @param value the value of the step
     * @param weight the weight of the step
     */
    public Step(double value, double weight) {
      this.value = value;
      this.weight = weight;
    }

    /**
     * Returns the value of the step.
     * 
     * @return the value of the step
     */
    public double getValue() {
      return value;
    }

    /**
     * Returns the weight of the step.
     * 
     * @return the weight of the step
     */
    public double getWeight() {
      return weight;
    }

    /**
     * Sets the value of the step.
     * 
     * @param value the value of the step
     */
    public void setValue(double value) {
      this.value = value;
    }

    /**
     * Sets the weight of the step.
     * 
     * @param weight the weight of the step
     */
    public void setWeight(double weight) {
      this.weight = weight;
    }

  }

  private List<ValueProvider<? super M, ? extends Number>> fields = new ArrayList<ValueProvider<? super M, ? extends Number>>();
  protected double minimum = Double.NaN;
  protected double maximum = Double.NaN;
  protected boolean adjustMaximumByMajorUnit = false;
  protected boolean adjustMinimumByMajorUnit = false;
  protected int stepsMax = -1;
  protected double interval = -1;

  /**
   * Creates a numeric axis.
   */
  public NumericAxis() {
    labelProvider = new RoundNumberProvider<Number>();
  }

  /**
   * Adds a {@link ValueProvider} to provide data to a field of the axis.
   * 
   * @param field the value provider
   */
  public void addField(ValueProvider<? super M, ? extends Number> field) {
    fields.add(field);
  }

  /**
   * Calculate the start, end and step points.
   */
  public void calcEnds() {
    double min = Double.isNaN(minimum) ? Double.POSITIVE_INFINITY : minimum;
    double max = Double.isNaN(maximum) ? Double.NEGATIVE_INFINITY : maximum;
    ListStore<M> store = chart.getCurrentStore();
    List<Series<M>> series = chart.getSeries();
    Set<Integer> excluded = null;

    if (fields.size() == 0) {
      this.from = 0;
      this.to = 0;
      this.power = 0;
      this.step = 0;
      this.steps = 1;
      return;
    }
    for (int i = 0; i < series.size(); i++) {
      if (series.get(i) instanceof BarSeries && ((BarSeries<?>) series.get(i)).isStacked()
          && ((BarSeries<?>) series.get(i)).getYAxisPosition() == this.position) {
        excluded = ((BarSeries<?>) series.get(i)).getExcluded();
        break;
      } else if (series.get(i) instanceof AreaSeries
          && ((AreaSeries<?>) series.get(i)).getYAxisPosition() == this.position) {
        excluded = ((AreaSeries<?>) series.get(i)).getExcluded();
        break;
      }
    }

    if (excluded != null && excluded.size() != fields.size()) {
      for (int i = 0; i < store.size(); i++) {
        double value = 0;
        if (Double.isInfinite(min)) {
          min = 0;
        }
        for (int j = 0; j < fields.size(); j++) {
          if (excluded.contains(j)) {
            continue;
          }
          Number fieldValue = fields.get(j).getValue(store.get(i));
          if (fieldValue != null && !Double.isNaN(fieldValue.doubleValue())) {
            value += fieldValue.doubleValue();
          }
        }
        max = Math.max(max, value);
        min = Math.min(min, value);
      }
    } else {
      for (int i = 0; i < store.size(); i++) {
        for (ValueProvider<? super M, ? extends Number> field : fields) {
          Number object = field.getValue(store.get(i));
          double value = Double.NaN;
          if (object != null) {
            value = object.doubleValue();
          }
          if (!Double.isNaN(value)) {
            max = Math.max(max, value);
            min = Math.min(min, value);
          }
        }
      }
    }

    if (!Double.isNaN(maximum)) {
      max = Math.min(max, maximum);
    }
    if (!Double.isNaN(minimum)) {
      min = Math.max(min, minimum);
    }
    if (min == 0 && max == 0) {
      this.from = 0;
      this.to = 0;
      this.power = 0;
      this.step = 0;
      this.steps = 1;
    } else {
      if (stepsMax >= 0) {
        snapEnds(min, max, stepsMax);
      } else if (interval >= 0) {
        snapEnds(min, max, (max - min) / interval + 1);
      } else {
        snapEnds(min, max, 10);
      }
      if (this.adjustMaximumByMajorUnit) {
        this.to = Math.ceil(this.to / this.step) * this.step;
        this.steps = (int) ((this.to - this.from) / this.step);
      }
      if (this.adjustMinimumByMajorUnit) {
        this.from = Math.floor(this.from / this.step) * this.step;
        this.steps = (int) ((this.to - this.from) / this.step);
      }
    }
  }

  public List<ValueProvider<? super M, ? extends Number>> getFields() {
    return fields;
  }

  /**
   * Returns the manually set interval between tick marks.
   * 
   * @return the manually set interval between tick marks
   */
  public double getInterval() {
    return interval;
  }

  /**
   * Returns the maximum value of the axis.
   * 
   * @return the maximum value of the axis
   */
  public double getMaximum() {
    return maximum;
  }

  /**
   * Returns the minimum value of the axis.
   * 
   * @return the minimum value of the axis
   */
  public double getMinimum() {
    return minimum;
  }

  /**
   * Returns the steps of the axis.
   * 
   * @return the steps of the axis
   */
  public int getSteps() {
    return stepsMax;
  }

  /**
   * Returns true if the axis adjusts the minimum.
   * 
   * @return true if the axis adjusts the minimum
   */
  public boolean isAdjustMaximumByMajorUnit() {
    return adjustMaximumByMajorUnit;
  }

  /**
   * Returns true if the axis adjusts the maximum.
   * 
   * @return true if the axis adjusts the maximum
   */
  public boolean isAdjustMinimumByMajorUnit() {
    return adjustMinimumByMajorUnit;
  }

  public void removeField(ValueProvider<? super M, ? extends Number> field) {
    fields.remove(field);
  }

  /**
   * Sets true if the axis adjusts the maximum.
   * 
   * @param adjustMaximumByMajorUnit true if the axis adjusts the maximum
   */
  public void setAdjustMaximumByMajorUnit(boolean adjustMaximumByMajorUnit) {
    this.adjustMaximumByMajorUnit = adjustMaximumByMajorUnit;
  }

  /**
   * Sets true if the axis adjusts the minimum.
   * 
   * @param adjustMinimumByMajorUnit true if the axis adjusts the minimum
   */
  public void setAdjustMinimumByMajorUnit(boolean adjustMinimumByMajorUnit) {
    this.adjustMinimumByMajorUnit = adjustMinimumByMajorUnit;
  }

  public void setFields(List<ValueProvider<? super M, ? extends Number>> fields) {
    this.fields = fields;
  }

  /**
   * Sets the interval between tick marks. Is overridden if the number of steps
   * is also set.
   * 
   * @param interval the interval between tick marks
   */
  public void setInterval(double interval) {
    this.interval = interval;
  }

  /**
   * Sets the maximum value of the axis.
   * 
   * @param maximum the maximum value of the axis
   */
  public void setMaximum(double maximum) {
    this.maximum = maximum;
  }

  /**
   * Sets the minimum value of the axis.
   * 
   * @param minimum the minimum value of the axis
   */
  public void setMinimum(double minimum) {
    this.minimum = minimum;
  }

  /**
   * Sets the number of steps on the axis.
   * 
   * @param steps the number of steps on the axis
   */
  public void setSteps(int steps) {
    this.stepsMax = steps;
  }

  @Override
  protected void applyData() {
    calcEnds();
  }

  @Override
  protected void createLabels() {
    labelNames.clear();
    labelNames.add(from);
    for (int i = 0; i < ticks.size() - 2; i++) {
      labelNames.add(labelNames.get(labelNames.size() - 1).doubleValue() + step);
    }
    labelNames.add(to);
  }

  /**
   * Snaps the from, to and step points of the axis.
   * 
   * @param from the starting value of the axis
   * @param to the ending value of the axis
   * @param stepsMax maximum number of steps on the axis
   */
  private void snapEnds(double from, double to, double stepsMax) {
    double step = (to - from) / stepsMax;
    double logone = Math.log(step);
    double logtwo = Math.log(10);
    double level = Math.floor(logone / logtwo) + 1;
    double m = Math.pow(10, level);
    double cur = from = Math.floor(from / m) * m;
    double modulo = Math.round((step % m) * Math.pow(10, 2 - level));
    String[] interval = {"0:15", "20:4", "30:2", "40:4", "50:9", "60:4", "70:2", "80:4", "100:15"};
    List<Step> steps = new ArrayList<Step>();
    for (int i = 0; i < interval.length; i++) {
      String[] temp = interval[i].split(":");
      double b = (Double.valueOf(temp[0]) - modulo) < 0 ? Double.POSITIVE_INFINITY : (Double.valueOf(temp[0]) - modulo)
          / Double.valueOf(temp[1]);
      steps.add(new Step(Double.valueOf(temp[0]), b));
    }
    Collections.sort(steps, weightComparator());
    double stepsVal = steps.get(0).getValue();
    stepsVal = Math.floor(step * Math.pow(10, -level)) * Math.pow(10, level) + stepsVal * Math.pow(10, level - 2);
    int stepCount = 0;
    while (cur < to) {
      cur += stepsVal;
      stepCount++;
    }
    to = cur;

    this.from = from;
    this.to = to;
    this.power = level;
    this.step = stepsVal;
    this.steps = stepCount;
  }

  /**
   * Generates a {@link Comparator} for use in sorting steps by their weight.
   * 
   * @return the generated comparator
   */
  private Comparator<Step> weightComparator() {
    return new Comparator<Step>() {
      @Override
      public int compare(Step o1, Step o2) {
        return Double.compare(o1.getWeight(), o2.getWeight());
      }
    };
  }

}