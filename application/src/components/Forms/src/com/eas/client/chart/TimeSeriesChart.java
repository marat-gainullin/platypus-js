/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.chart;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.model.script.RowHostObject;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.client.scripts.ScriptColor;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.AbstractXYDataset;

/**
 *
 * @author kl
 */
public class TimeSeriesChart extends AbstractLineChart {

    public static final String TIMESTAMP_FORMAT = "dd.MM.yyyy hh:mm:ss";
    private SimpleDateFormat xAxisLabelFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    private String title;
    private String xLabel;
    private String yLabel;

    public TimeSeriesChart(String pTitle, String pXAxisLabel, String pYAxisLabel, ScriptableRowset<?> pRowset) {
        this(pTitle, pXAxisLabel, pYAxisLabel);
        setDataset(pRowset);
    }

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
                    return sRowset.getSize();
                } catch (Exception ex) {
                    Logger.getLogger(TimeSeriesChart.class.getName()).log(Level.SEVERE, null, ex);
                }
                return 0;
            }

            @Override
            public Number getX(int aSeries, int aItem) {
                SeriesProperties curSeries = seriesByIndex(aSeries);
                if (curSeries != null) {
                    RowHostObject aRow;
                    try {
                        aRow = sRowset.getRow(aItem + 1);
                        int fldIndex = sRowset.getFields().find(curSeries.getXAxisProperty());
                        Date date = (Date) sRowset.unwrap().getConverter().convert2JdbcCompatible(aRow.getColumnObject(fldIndex), DataTypeInfo.TIMESTAMP);
                        return date.getTime();
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
                    RowHostObject aRow;
                    try {
                        aRow = sRowset.getRow(aItem + 1);
                        int fldIndex = sRowset.getFields().find(curSeries.getYAxisProperty());
                        return (Number) sRowset.unwrap().getConverter().convert2JdbcCompatible(aRow.getColumnObject(fldIndex), DataTypeInfo.DECIMAL);
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
    
    protected void createChart()
    {
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
    protected void fireDataChanged() {
        createChart();
        chart.fireChartChanged();
        super.fireDataChanged();
    }
}
