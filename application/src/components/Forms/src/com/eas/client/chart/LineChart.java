/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.chart;

import com.eas.script.ScriptUtils;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author kl
 */
public class LineChart extends AbstractLineChart {

    protected String title;
    protected String xLabel;
    protected String yLabel;

    public LineChart(String pTitle, String pXLabel, String pYLabel) {
        super();
        title = pTitle;
        xLabel = pXLabel;
        yLabel = pYLabel;
    }

    @Override
    protected void createChartDataset() {
        xyDataset = new XYSeriesCollection();
    }

    protected void fillSeries() {
        ((XYSeriesCollection) xyDataset).removeAllSeries();
        for (int i = 0; i < getSeries().size(); i++) {
            SeriesProperties curSeries = seriesByIndex(i);
            try {
                XYSeries xySeries = new XYSeries(curSeries.getTitle());
                if (data != null) {
                    Object oLength = data.getMember("length");
                    if (oLength instanceof Number) {
                        int length = ((Number) oLength).intValue();
                        for (int j = 0; j < length; j++) {
                            Object oItem = data.getSlot(j);
                            if (oItem instanceof JSObject) {
                                JSObject sItem = (JSObject) oItem;
                                Object ox = ScriptUtils.toJava(sItem.getMember(curSeries.getXAxisProperty()));
                                Object oy = ScriptUtils.toJava(sItem.getMember(curSeries.getYAxisProperty()));
                                if (ox instanceof Number && oy instanceof Number) {
                                    xySeries.add((Number) ox, (Number) oy);
                                }
                            }
                        }
                    }
                }
                ((XYSeriesCollection) xyDataset).addSeries(xySeries);
            } catch (Exception ex) {
                Logger.getLogger(LineChart.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        final XYPlot plot = chart.getXYPlot();
        for (int i = 0; i < getSeries().size(); i++) {
            plot.getRenderer().setSeriesPaint(i, seriesColors[i % seriesColors.length]);
        }
    }

    @Override
    protected void createChartPanel() {
        chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, xyDataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.white);
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chartPanel = new ChartPanel(chart) {
            @Override
            public void addNotify() {
                super.addNotify();
                fireDataChanged();
            }
        };
    }

    @Override
    protected void fireDataChanged() {
        fillSeries();
        chart.fireChartChanged();
        super.fireDataChanged();
    }
}
