package com.eas.client.chart;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.Utils;
import com.eas.client.gxtcontrols.converters.DoubleRowValueConverter;
import com.eas.client.gxtcontrols.converters.StringRowValueConverter;
import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.chart.client.chart.series.PieSeries;
import com.sencha.gxt.chart.client.chart.series.Series.LabelPosition;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelConfig;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextBaseline;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;

public class PieChart extends AbstractChart {

	private String labelFieldName;
	private String dataFieldName;

	protected class SectorLabelProvider implements SeriesLabelProvider<Object>, LabelProvider<Object> {
		protected int colIndex = 0;
		protected StringRowValueConverter converter = new StringRowValueConverter();

		@Override
		public String getLabel(Object oItem) {
			try {
				if (oItem instanceof Row) {
					Row item = (Row) oItem;
					if (colIndex == 0)
						colIndex = item.getFields().find(labelFieldName);
					if (colIndex != 0)
						return converter.convert(item.getColumnObject(colIndex));
					else
						return null;
				} else if (oItem instanceof JavaScriptObject) {
					JsObject item = ((JavaScriptObject) oItem).cast();
					Object oValue = Utils.toJava(item.get(labelFieldName));
					if (oValue instanceof String)
						return (String) oValue;
					else
						return null;
				} else
					return null;
			} catch (Exception ex) {
				Logger.getLogger(PieChart.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
				return null;
			}
		}

		@Override
		public String getLabel(Object item, ValueProvider<? super Object, ? extends Number> valueProvider) {
			return getLabel(item);
		}

	}

	public PieChart(String aTitle, String aLabelFieldName, String aDataFieldName, Object aData) {
		super(createChartFiller(aData));
		setTitle(aTitle);
		labelFieldName = aLabelFieldName;
		dataFieldName = aDataFieldName;
		setShadowChart(true);
		setAnimated(true);

		PieSeries<Object> pieSeries = new PieSeries<Object>();
		pieSeries.setAngleField(new ValueProvider<Object, Double>() {
			protected int colIndex = 0;
			protected DoubleRowValueConverter covnerter = new DoubleRowValueConverter();

			@Override
			public Double getValue(Object oItem) {
				try {
					if (oItem instanceof Row) {
						Row item = (Row) oItem;
						if (colIndex == 0)
							colIndex = item.getFields().find(dataFieldName);
						if (colIndex != 0)
							return covnerter.convert(item.getColumnObject(colIndex));
						else
							return null;
					} else if (oItem instanceof JavaScriptObject) {
						JsObject item = ((JavaScriptObject) oItem).cast();
						Object oValue = Utils.toJava(item.get(dataFieldName));
						if (oValue instanceof Number)
							return ((Number) oValue).doubleValue();
						else
							return null;
					} else
						return null;
				} catch (Exception ex) {
					Logger.getLogger(PieChart.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					return null;
				}
			}

			@Override
			public void setValue(Object object, Double value) {
			}

			@Override
			public String getPath() {
				return null;
			}
		});
		for (Color sc : seriesColors)
			pieSeries.addColor(sc);
		SeriesLabelConfig<Object> labelConfig = new SeriesLabelConfig<Object>();
		TextSprite textConfig = new TextSprite();
		textConfig.setFont("Arial");
		textConfig.setTextBaseline(TextBaseline.MIDDLE);
		textConfig.setFontSize(AXIS_LABEL_FONTSIZE);
		textConfig.setTextAnchor(TextAnchor.MIDDLE);
		textConfig.setZIndex(15);
		labelConfig.setSpriteConfig(textConfig);
		labelConfig.setLabelPosition(LabelPosition.START);
		SectorLabelProvider labelProvider = new SectorLabelProvider();
		labelConfig.setLabelProvider(labelProvider);
		pieSeries.setLabelConfig(labelConfig);
		pieSeries.setLegendLabelProvider(labelProvider);
		pieSeries.setHighlighting(true);
		addSeries(pieSeries);
	}
}
