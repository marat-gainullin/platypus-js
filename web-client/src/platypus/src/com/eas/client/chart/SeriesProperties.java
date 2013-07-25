package com.eas.client.chart;

public class SeriesProperties {

	private String xAxisProperty;
	private String yAxisProperty;
	private String title;

	public SeriesProperties(String pXAxisProperty, String pYAxisProperty, String pSeriesTitle) {
		super();
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
	 * @param xAxisProperty
	 *            the xAxisProperty to set
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
	 * @param yAxisProperty
	 *            the yAxisProperty to set
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
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String pTitle) {
		title = pTitle;
	}
}
