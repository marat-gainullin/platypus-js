/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.List;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * Calculates the value of a summary column.
 * 
 * @param <N> the value type
 * @param <O> the return type
 */
public interface SummaryType<N, O extends Number> {

  public static final class AvgSummaryType<N> implements SummaryType<N, Double> {
    @Override
    public <M> Double calculate(List<? extends M> l, ValueProvider<? super M, N> valueProvider) {
      double avg = 0d;
      for (int i = 0, len = l.size(); i < len; i++) {
        N d = valueProvider.getValue(l.get(i));
        if (d instanceof Number) {
          avg += ((Number) d).doubleValue();
        }
      }
      return avg / l.size();
    }
  }

  public static final class CountSummaryType<V> implements SummaryType<V, Integer> {
    @Override
    public <M> Integer calculate(List<? extends M> l, ValueProvider<? super M, V> valueProvider) {
      return l.size();
    }
  }

  public static final class MaxSummaryType<N> implements SummaryType<N, Double> {
    @Override
    public <M> Double calculate(List<? extends M> l, ValueProvider<? super M, N> valueProvider) {
      Double max = null;
      for (int i = 0, len = l.size(); i < len; i++) {
        N d = valueProvider.getValue(l.get(i));
        if (d instanceof Number) {
          if (max == null) {
            max = ((Number) d).doubleValue();
          } else {
            max = Math.max(((Number) d).doubleValue(), max);
          }
        }
      }
      return max == null ? 0d : max;

    }
  }

  public static final class MinSummaryType<N> implements SummaryType<N, Double> {
    @Override
    public <M> Double calculate(List<? extends M> l, ValueProvider<? super M, N> valueProvider) {
      Double min = null;
      for (int i = 0, len = l.size(); i < len; i++) {
        N d = valueProvider.getValue(l.get(i));
        if (d instanceof Number) {
          if (min == null) {
            min = ((Number) d).doubleValue();
          } else {
            min = Math.min(((Number) d).doubleValue(), min);
          }
        }
      }
      return min == null ? 0d : min;
    }
  }

  public static final class SumSummaryType<N> implements SummaryType<N, Double> {
    @Override
    public <M> Double calculate(List<? extends M> l, ValueProvider<? super M, N> valueProvider) {
      Double sum = 0d;
      for (int i = 0, len = l.size(); i < len; i++) {
        N d = valueProvider.getValue(l.get(i));
        if (d instanceof Number) {
          sum += ((Number) d).doubleValue();
        }
      }
      return sum;
    }
  }

  /**
   * Returns the value for a summary calculation.
   * 
   * @param models the list of models
   * @param vp the value provider
   * @return the summary value
   */
  public abstract <M> O calculate(List<? extends M> models, ValueProvider<? super M, N> vp);

}
