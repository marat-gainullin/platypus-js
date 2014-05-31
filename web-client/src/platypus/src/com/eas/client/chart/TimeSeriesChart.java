package com.eas.client.chart;

import com.google.gwt.i18n.client.DateTimeFormat;

public class TimeSeriesChart extends AbstractLineChart {
	public static final String TIMESTAMP_FORMAT = "dd.MM.yyyy hh:mm:ss";

	protected DateTimeFormat xAxisLabelFormat = DateTimeFormat.getFormat(TIMESTAMP_FORMAT);

	public TimeSeriesChart(String aTitle, String aXLabel, String aYLabel, Object aData) {
		super();
	}

	public String getXLabelsFormat() {
		return xAxisLabelFormat.getPattern();
	}

	public void setXLabelsFormat(String aPattern) {
		xAxisLabelFormat = DateTimeFormat.getFormat(aPattern);
	}
}
