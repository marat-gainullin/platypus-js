package com.eas.client.chart;

import com.sencha.gxt.chart.client.chart.axis.NumericAxis;

public class LineChart extends AbstractLineChart {
	
	public LineChart(String aTitle, String aXLabel, String aYLabel, Object aData) {
		super(aTitle, aXLabel, aYLabel, createChartFiller(aData));
	}
	
	@Override
	protected NumericAxis<Object> createXAxis(String aTitle) {
		return createNumericAxis(aTitle, Position.BOTTOM);
	}
}
