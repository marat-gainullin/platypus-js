package com.eas.client.chart;

import com.bearsoft.rowset.Row;
import com.eas.client.model.Entity;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;

public class LineChart extends AbstractLineChart {
	
	public LineChart(String aTitle, String aXLabel, String aYLabel, Entity aEntity) {
		super(aTitle, aXLabel, aYLabel, aEntity);
	}
	
	@Override
	protected NumericAxis<Row> createXAxis(String aTitle) {
		return createNumericAxis(aTitle, Position.BOTTOM);
	}
}
