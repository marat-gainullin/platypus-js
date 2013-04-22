/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.chart;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.model.script.RowHostObject;
import com.eas.client.model.script.ScriptableRowset;
import java.awt.Font;
import java.awt.Paint;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author kl
 */
public class PieChart extends AbstractChart {

    private String title;
    private String nameField;
    private String dataField;
    private DefaultPieDataset pieDataset;

    public PieChart(String pTitle, String pNameField, String pDataField, ScriptableRowset<?> pRowset) {
        super();
        title = pTitle;
        nameField = pNameField;
        dataField = pDataField;
        setDataset(pRowset);
    }

    @Override
    protected void createChartDataset() {
        pieDataset = new DefaultPieDataset();
    }

    protected void fillDataset() {
        pieDataset.clear();
        try {
            int nameFldIndex = sRowset.getFields().find(nameField);
            int dataFldIndex = sRowset.getFields().find(dataField);
            for (int i = 0; i < sRowset.unwrap().size(); i++) {
                RowHostObject row = sRowset.getRow(i + 1);
                String name = (String) sRowset.unwrap().getConverter().convert2JdbcCompatible(row.getColumnObject(nameFldIndex), DataTypeInfo.VARCHAR);
                Number value = (Number) sRowset.unwrap().getConverter().convert2JdbcCompatible(row.getColumnObject(dataFldIndex), DataTypeInfo.DECIMAL);
                pieDataset.setValue(name, value);
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
}
