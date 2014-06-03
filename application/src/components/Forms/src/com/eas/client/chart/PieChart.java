/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.chart;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptUtils;
import java.awt.Font;
import java.awt.Paint;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author kl, mg
 */
public class PieChart extends AbstractChart {

    private final String title;
    private final String nameField;
    private final String dataField;
    private DefaultPieDataset pieDataset;

    public PieChart(String pTitle, String pNameField, String pDataField) {
        super();
        title = pTitle;
        nameField = pNameField;
        dataField = pDataField;
    }

    @Override
    protected void createChartDataset() {
        pieDataset = new DefaultPieDataset();
    }

    protected void fillDataset() {
        pieDataset.clear();
        try {
            if (data != null) {
                Object oLength = data.getMember("length");
                if (oLength instanceof Number) {
                    int length = ((Number) oLength).intValue();
                    for (int j = 0; j < length; j++) {
                        Object oItem = data.getSlot(j);
                        if (oItem instanceof JSObject) {
                            JSObject sItem = (JSObject) oItem;
                            Object oName = ScriptUtils.toJava(sItem.getMember(nameField));
                            Object oData = ScriptUtils.toJava(sItem.getMember(dataField));
                            if (oName instanceof String && oData instanceof Number) {
                                pieDataset.setValue((String) oName, (Number) oData);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PieChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void createChartPanel() {
        chart = ChartFactory.createPieChart(title, pieDataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setDrawingSupplier(new DefaultDrawingSupplier() {
            protected int curentPaint;

            @Override
            public Paint getNextPaint() {
                if (curentPaint >= pieDataset.getItemCount()) {
                    curentPaint = 0;
                }
                return seriesColors[curentPaint++ % seriesColors.length];
            }
        });
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        plot.setAutoPopulateSectionPaint(true);
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
        fillDataset();
        chart.fireChartChanged();
        super.fireDataChanged();
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
