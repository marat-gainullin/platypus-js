/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.chart;

import com.eas.client.scripts.ScriptColor;
import com.eas.script.ScriptUtils;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.AbstractXYDataset;

/**
 *
 * @author kl, mg refactoring
 */
public class TimeSeriesChart extends AbstractLineChart {

    public static final String TIMESTAMP_FORMAT = "dd.MM.yyyy hh:mm:ss";
    private SimpleDateFormat xAxisLabelFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    private final String title;
    private final String xLabel;
    private final String yLabel;

    public TimeSeriesChart(String pTitle, String pXAxisLabel, String pYAxisLabel) {
        super();
        title = pTitle;
        xLabel = pXAxisLabel;
        yLabel = pYAxisLabel;
    }

    @Override
    protected void createChartDataset() {
        xyDataset = new AbstractXYDataset() {
            @Override
            public int getSeriesCount() {
                return getSeries().size();
            }

            @Override
            public Comparable getSeriesKey(int series) {
                return getSeries().get(series);
            }

            @Override
            public int getItemCount(int series) {
                try {
                    Object oLength = data.getMember("length");
                    if (oLength instanceof Number) {
                        return ((Number) oLength).intValue();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(TimeSeriesChart.class.getName()).log(Level.SEVERE, null, ex);
                }
                return 0;
            }

            @Override
            public Number getX(int aSeries, int aItem) {
                SeriesProperties curSeries = seriesByIndex(aSeries);
                if (curSeries != null) {
                    try {
                        Object oItem = data.getSlot(aItem);
                        if (oItem instanceof JSObject) {
                            JSObject sItem = (JSObject) oItem;
                            Object ox = ScriptUtils.toJava(sItem.getMember(curSeries.getXAxisProperty()));
                            if (ox instanceof Date) {
                                return ((Date) ox).getTime();
                            } else if (ox instanceof Number) {
                                return (Number) ox;
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(TimeSeriesChart.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }

            @Override
            public Number getY(int aSeries, int aItem) {
                SeriesProperties curSeries = seriesByIndex(aSeries);
                if (curSeries != null) {
                    try {
                        Object oItem = data.getSlot(aItem);
                        if (oItem instanceof JSObject) {
                            JSObject sItem = (JSObject) oItem;
                            Object oy = ScriptUtils.toJava(sItem.getMember(curSeries.getYAxisProperty()));
                            if (oy instanceof Date) {
                                return ((Date) oy).getTime();
                            } else if (oy instanceof Number) {
                                return (Number) oy;
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(TimeSeriesChart.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }
        };

    }

    @Override
    protected void createChartPanel() {
        chartPanel = new ChartPanel(chart) {
            @Override
            public void addNotify() {
                super.addNotify();
                fireDataChanged();
            }
        };
    }

    protected void createChart() {
        if (chart == null && getSeries() != null) {
            chart = ChartFactory.createTimeSeriesChart(title, xLabel, yLabel, xyDataset, false, true, true);
            ((DateAxis) chart.getXYPlot().getDomainAxis(0)).setDateFormatOverride(xAxisLabelFormat);
            chart.getXYPlot().setBackgroundPaint(ScriptColor.WHITE);
            ((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setBaseShapesVisible(true);
            ((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setBaseShapesFilled(true);
            chart.getXYPlot().getRenderer().setBaseToolTipGenerator(new StandardXYToolTipGenerator("({1})  {2}", new SimpleDateFormat(TIMESTAMP_FORMAT), new DecimalFormat("###.##")));
            for (int i = 0; i < getSeries().size(); i++) {
                chart.getXYPlot().getRenderer().setSeriesPaint(i, seriesColors[i % seriesColors.length]);
            }
            chartPanel.setChart(chart);
        }
    }

    public String getXLabelsFormat() {
        return xAxisLabelFormat.toPattern();
    }

    public void setXLabelsFormat(String aPattern) {
        xAxisLabelFormat = new SimpleDateFormat(aPattern);
        if (chart != null) {
            ((DateAxis) chart.getXYPlot().getDomainAxis(0)).setDateFormatOverride(xAxisLabelFormat);
        }
    }

    @Override
    public void setData(JSObject aValue) {
        if (getData() != aValue) {
            chart = null;
        }
        super.setData(aValue);
    }

    @Override
    protected void fireDataChanged() {
        createChart();
        chart.fireChartChanged();
        super.fireDataChanged();
    }
}
