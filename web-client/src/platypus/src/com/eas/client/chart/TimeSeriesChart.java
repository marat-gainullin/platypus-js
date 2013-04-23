package com.eas.client.chart;

import java.util.Date;

import com.bearsoft.rowset.Row;
import com.eas.client.model.Entity;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.data.shared.LabelProvider;

public class TimeSeriesChart extends AbstractLineChart {
	public static final String TIMESTAMP_FORMAT = "dd.MM.yyyy hh:mm:ss";

	protected DateTimeFormat xAxisLabelFormat = DateTimeFormat.getFormat(TIMESTAMP_FORMAT);
	
	public TimeSeriesChart(String aTitle, String aXLabel, String aYLabel, Entity aEntity) {
		super(aTitle, aXLabel, aYLabel, aEntity);
	}

	public String getXLabelsFormat() {
	    return xAxisLabelFormat.getPattern();
    }
	
	public void setXLabelsFormat(String aPattern) {
	    xAxisLabelFormat = DateTimeFormat.getFormat(aPattern);
    }
	
	@Override
	protected NumericAxis<Row> createXAxis(String aTitle) {
		NumericAxis<Row> axis = createNumericAxis(aTitle, Position.BOTTOM);
		axis.setLabelProvider(new LabelProvider<Number>() {

			@Override
			public String getLabel(Number item) {
				return xAxisLabelFormat.format(new Date(item.longValue()));
			}

		});
		return axis;
	}
}
