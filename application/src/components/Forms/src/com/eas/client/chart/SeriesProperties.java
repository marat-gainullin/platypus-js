/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.chart;

/**
 *
 * @author kl
 */
public class SeriesProperties implements Comparable<Object> {

    private String xAxisProperty = null;
    private String yAxisProperty = null;
    private String title = null;

    public SeriesProperties(String pXAxisProperty, String pYAxisProperty, String pSeriesTitle) {
        xAxisProperty = pXAxisProperty;
        yAxisProperty = pYAxisProperty;
        title = pSeriesTitle;
    }

    /**
     * @return the xAxisProperty
     */
    public String getXAxisProperty() {
        return xAxisProperty;
    }

    /**
     * @param xAxisProperty the xAxisProperty to set
     */
    public void setXAxisProperty(String pXAxisProperty) {
        xAxisProperty = pXAxisProperty;
    }

    /**
     * @return the yAxisProperty
     */
    public String getYAxisProperty() {
        return yAxisProperty;
    }

    /**
     * @param yAxisProperty the yAxisProperty to set
     */
    public void setYAxisProperty(String pYAxisProperty) {
        yAxisProperty = pYAxisProperty;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String pTitle) {
        title = pTitle;
    }

    @Override
    public int compareTo(Object o) {
        return 1;
    }
}
