package com.eas.client.chart;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.eas.client.gxtcontrols.converters.DoubleRowValueConverter;
import com.eas.client.gxtcontrols.converters.StringRowValueConverter;
import com.eas.client.model.Entity;
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
	
	private String labelField;
	private String dataField;

	protected class SectorLabelProvider implements  SeriesLabelProvider<Row>, LabelProvider<Row>
	{
		protected int colIndex = 0;
		protected StringRowValueConverter converter = new StringRowValueConverter();
		
		@Override
        public String getLabel(Row aRow) {
			try {
				if (colIndex == 0)
					colIndex = aRow.getFields().find(labelField);
				if (colIndex != 0)
					return converter.convert(aRow.getColumnObject(colIndex));
				else
					return null;
			} catch (InvalidColIndexException ex) {
				Logger.getLogger(PieChart.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
				return null;
			}
        }

		@Override
        public String getLabel(Row item, ValueProvider<? super Row, ? extends Number> valueProvider) {
	        return getLabel(item);
        }
		
	}
	
	public PieChart(String aTitle, String pNameField, String pDataField, Entity aEntity) {
		super(aEntity);
		setTitle(aTitle);
		labelField = pNameField;
		dataField = pDataField;
		setShadowChart(true);
		setAnimated(true);

		PieSeries<Row> pieSeries = new PieSeries<Row>();
		pieSeries.setAngleField(new ValueProvider<Row, Double>() {
			protected int colIndex = 0;
			protected DoubleRowValueConverter covnerter = new DoubleRowValueConverter();

			@Override
			public Double getValue(Row aRow) {
				try {
					if (colIndex == 0)
						colIndex = aRow.getFields().find(dataField);
					if (colIndex != 0)
						return covnerter.convert(aRow.getColumnObject(colIndex));
					else
						return null;
				} catch (InvalidColIndexException ex) {
					Logger.getLogger(PieChart.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					return null;
				}
			}

			@Override
			public void setValue(Row object, Double value) {
			}

			@Override
			public String getPath() {
				return null;
			}
		});
		for(Color sc : seriesColors)
			pieSeries.addColor(sc);
		SeriesLabelConfig<Row> labelConfig = new SeriesLabelConfig<Row>();
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
