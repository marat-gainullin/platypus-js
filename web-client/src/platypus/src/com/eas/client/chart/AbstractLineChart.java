package com.eas.client.chart;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.eas.client.gxtcontrols.converters.DoubleRowValueConverter;
import com.eas.client.model.Entity;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.series.LineSeries;
import com.sencha.gxt.chart.client.chart.series.Primitives;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;

public abstract class AbstractLineChart extends AbstractChart {

	public static int MARKER_RADIUS = 4;
	public static Sprite[] markers = new Sprite[] { Primitives.diamond(0, 0, MARKER_RADIUS), Primitives.circle(0, 0, MARKER_RADIUS), Primitives.triangle(0, 0, MARKER_RADIUS),
	        Primitives.square(0, 0, MARKER_RADIUS), Primitives.cross(0, 0, MARKER_RADIUS), Primitives.plus(0, 0, MARKER_RADIUS) };
	

	protected static class DoubleValueProvider implements ValueProvider<Row, Double> {
		protected DoubleRowValueConverter converter = new DoubleRowValueConverter();
		protected String fieldName;
		protected int colIndex = 0;

		public DoubleValueProvider(String aFieldName) {
			super();
			fieldName = aFieldName;
		}

		@Override
		public Double getValue(Row item) {
			try {
				if (colIndex == 0) {
					colIndex = item.getFields().find(fieldName);
				}
				if (colIndex != 0) {
					return converter.convert(item.getColumnObject(colIndex));
				} else
					return null;
			} catch (InvalidColIndexException e) {
				Logger.getLogger(LineChart.class.getName()).log(Level.SEVERE, e.getMessage());
			}
			return null;
		}

		@Override
		public void setValue(Row item, Double value) {
			// nothing to do here
		}

		@Override
		public String getPath() {
			// nothing to do here
			return null;
		}
	}

	protected NumericAxis<Row> xAxis;
	protected NumericAxis<Row> yAxis;

	public AbstractLineChart(String aTitle, String aXLabel, String aYLabel, Entity aEntity) {
		super(aEntity);
		setTitle(aTitle);
		xAxis = createXAxis(aXLabel);
		yAxis = createYAxis(aYLabel);
		addAxis(xAxis);
		addAxis(yAxis);
	}

	protected abstract NumericAxis<Row> createXAxis(String aTitle);

	protected NumericAxis<Row> createYAxis(String aTitle) {
		return createNumericAxis(aTitle, Position.LEFT);
	}

	public LineSeries<Row> addSeries(final String aXAxisFieldName, final String aYAxisFieldName, String aTitle) {
		LineSeries<Row> series = new LineSeries<Row>();
		series.setLegendTitle(aTitle);
		series.setYAxisPosition(Position.LEFT);
		series.setYField(new DoubleValueProvider(aYAxisFieldName));
		series.setStroke(seriesColors[getSeries().size() % seriesColors.length]);
		// series.setFill(series.getStroke());// Area below a line
		Sprite marker = markers[getSeries().size() % markers.length];
		marker.setFill(series.getStroke());
		series.setShowMarkers(true);
		series.setMarkerConfig(marker);
		yAxis.addField(series.getYField());
		series.setXAxisPosition(Position.BOTTOM);
		series.setXField(new DoubleValueProvider(aXAxisFieldName));
		series.setHighlighting(true);
		xAxis.addField(series.getXField());
		addSeries(series);
		return series;
	}

	protected static NumericAxis<Row> createNumericAxis(String aTitle, Position aPosition) {
		NumericAxis<Row> axis = new NumericAxis<Row>();
		axis.setPosition(aPosition);
		TextSprite axisTitle = new TextSprite(aTitle);
		axisTitle.setFontSize(AXIS_LABEL_FONTSIZE);
		axis.setTitleConfig(axisTitle);
		axis.setMinorTickSteps(1);
		axis.setDisplayGrid(true);
		return axis;
	}
}
