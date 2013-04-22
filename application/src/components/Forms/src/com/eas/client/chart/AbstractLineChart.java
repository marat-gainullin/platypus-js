/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.chart;

import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author kl
 */
abstract class AbstractLineChart extends AbstractChart {

    protected XYDataset xyDataset;
    private List<SeriesProperties> series = new ArrayList<>();

    public AbstractLineChart() {
        super();
    }

    public void addSeries(String pXAxisField, String pYAxisField, String pTitle) {
        series.add(new SeriesProperties(pXAxisField, pYAxisField, pTitle));
    }

    public SeriesProperties seriesByIndex(int index) {
        return series.get(index);
    }

    public List<SeriesProperties> getSeries() {
        return series;
    }
}
