package com.eas.client.gxtcontrols;

import com.google.gwt.dom.client.Style.Unit;

public class LayoutPanelPlacement {
	private static String SUFFIX_PX = "px";
	private static String SUFFIX_PCT = "%";

	private Double size = null;
	private Unit unit = null;

	public LayoutPanelPlacement(String aSizeValue) {
		if (aSizeValue != null) {
			if (aSizeValue.endsWith(SUFFIX_PX)) {
				unit = Unit.PX;
				size = Double.parseDouble(aSizeValue.substring(0,
						aSizeValue.indexOf(SUFFIX_PX)));
			} else if (aSizeValue.endsWith(SUFFIX_PCT)) {
				unit = Unit.PCT;
				size = Double.parseDouble(aSizeValue.substring(0,
						aSizeValue.indexOf(SUFFIX_PCT)));
			}
		}
	}

	public Double getSize() {
		return size;
	}

	public Unit getUnit() {
		return unit;
	}

}
