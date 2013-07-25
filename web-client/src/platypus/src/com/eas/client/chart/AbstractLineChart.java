package com.eas.client.chart;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.Utils;
import com.eas.client.gxtcontrols.converters.DoubleRowValueConverter;
import com.eas.client.gxtcontrols.grid.fillers.ListStoreFiller;
import com.google.gwt.core.client.JavaScriptObject;
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

	protected static class DoubleValueProvider implements ValueProvider<Object, Double> {
		protected DoubleRowValueConverter converter = new DoubleRowValueConverter();
		protected String fieldName;
		protected int colIndex = 0;

		public DoubleValueProvider(String aFieldName) {
			super();
			fieldName = aFieldName;
		}

		@Override
		public Double getValue(Object oItem) {
			try {
				if (oItem instanceof Row) {
					Row item = (Row) oItem;
					if (colIndex == 0) {
						colIndex = item.getFields().find(fieldName);
					}
					if (colIndex != 0) {
						return converter.convert(item.getColumnObject(colIndex));
					} else
						return null;
				} else if (oItem instanceof JavaScriptObject) {
					JsObject item = ((JavaScriptObject) oItem).cast();
					Object oValue = Utils.toJava(item.get(fieldName));
					if (oValue instanceof Number)
						return ((Number) oValue).doubleValue();
					else if (oValue instanceof Date){
						return Long.valueOf(((Date) oValue).getTime()).doubleValue();
					}else
						return null;
				}
			} catch (Exception e) {
				Logger.getLogger(LineChart.class.getName()).log(Level.SEVERE, e.getMessage());
			}
			return null;
		}

		@Override
		public void setValue(Object item, Double value) {
			// nothing to do here
		}

		@Override
		public String getPath() {
			// nothing to do here
			return null;
		}
	}

	protected NumericAxis<Object> xAxis;
	protected NumericAxis<Object> yAxis;

	public AbstractLineChart(String aTitle, String aXLabel, String aYLabel, ListStoreFiller<Object> aFiller) {
		super(aFiller);
		setTitle(aTitle);
		xAxis = createXAxis(aXLabel);
		yAxis = createYAxis(aYLabel);
		addAxis(xAxis);
		addAxis(yAxis);
	}

	protected abstract NumericAxis<Object> createXAxis(String aTitle);

	protected NumericAxis<Object> createYAxis(String aTitle) {
		return createNumericAxis(aTitle, Position.LEFT);
	}

	public LineSeries<Object> addSeries(final String aXAxisFieldName, final String aYAxisFieldName, String aTitle) {
		LineSeries<Object> series = new LineSeries<Object>();
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

	protected static NumericAxis<Object> createNumericAxis(String aTitle, Position aPosition) {
		NumericAxis<Object> axis = new NumericAxis<Object>();
		axis.setPosition(aPosition);
		TextSprite axisTitle = new TextSprite(aTitle);
		axisTitle.setFontSize(AXIS_LABEL_FONTSIZE);
		axis.setTitleConfig(axisTitle);
		axis.setMinorTickSteps(1);
		axis.setDisplayGrid(true);
		return axis;
	}
}
