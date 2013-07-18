package com.eas.client.chart;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.data.shared.LabelProvider;

public class TimeSeriesChart extends AbstractLineChart {
	public static final String TIMESTAMP_FORMAT = "dd.MM.yyyy hh:mm:ss";

	protected DateTimeFormat xAxisLabelFormat = DateTimeFormat.getFormat(TIMESTAMP_FORMAT);
	
	public TimeSeriesChart(String aTitle, String aXLabel, String aYLabel, Object aData) {
		super(aTitle, aXLabel, aYLabel, createChartFiller(aData));
	}

	public String getXLabelsFormat() {
	    return xAxisLabelFormat.getPattern();
    }
	
	public void setXLabelsFormat(String aPattern) {
	    xAxisLabelFormat = DateTimeFormat.getFormat(aPattern);
    }
	
	@Override
	protected NumericAxis<Object> createXAxis(String aTitle) {
		NumericAxis<Object> axis = createNumericAxis(aTitle, Position.BOTTOM);
		axis.setLabelProvider(new LabelProvider<Number>() {

			@Override
			public String getLabel(Number item) {
				return xAxisLabelFormat.format(new Date(item.longValue()));
			}

		});
		return axis;
	}
}
