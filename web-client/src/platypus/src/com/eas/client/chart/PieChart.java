package com.eas.client.chart;

public class PieChart extends AbstractChart {

	private String labelFieldName;
	private String dataFieldName;

	protected class SectorLabelProvider {
		
		public String getLabel(Object oItem) {
				return null;
		}

	}

	public PieChart(String aTitle, String aLabelFieldName, String aDataFieldName, Object aData) {
		super();
	}
}
