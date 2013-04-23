package com.eas.client.gxtcontrols.wrappers.component;

import java.util.Collections;
import java.util.Set;

import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.widget.core.client.ProgressBar;

public class PlatypusProgressBar extends ProgressBar {

	private int shift = 0;
	private double scale = 0.01;
	private int minimum = 0;
	private int maximum = 100;
	

	public PlatypusProgressBar()
	{
		super(new ProgressBarCell(){
			@Override
			public Set<String> getConsumedEvents() {// To fix NullPointerException in CellComponent, working with ProgressBar
				Set<String> consumed = super.getConsumedEvents();
				if(consumed == null)
					consumed = Collections.<String>emptySet();
				return consumed;
			}
			
		});
	}
	
	public void setRange(int minValue, int maxValue) {
		minimum = minValue;
		maximum = maxValue;
		assert minimum != maximum;
		shift = minimum;
		scale = 1.0 / (maximum - minimum);
	}

	public double calcValue(int aValue) {
		return scale * (aValue - shift);
	}

	public void updateProgress(int aValue, String text) {
		super.updateProgress(calcValue(aValue), text);
	}

	public void setValue(int aValue, boolean fireEvents, boolean redraw) {
		super.setValue(calcValue(aValue), fireEvents, redraw);
	}

	public void setValue(int aValue, boolean fireEvents) {
		setValue(aValue, fireEvents, false);
	}

	public void setValue(int aValue) {
		setValue(aValue, false, false);
	}

	public int getShift() {
		return shift;
	}

	public double getScale() {
		return scale;
	}
	
	public int getMinimum() {
		return minimum;
	}

	public int getMaximum() {
		return maximum;
	}
	
	public void setMinimum(int aValue) {
		setRange(aValue, maximum);
	}

	public void setMaximum(int aValue) {
		setRange(minimum,aValue);
	}
	
	public int getIntValue() {
		Double value = getValue();
		if (value == null) {
			return 0;
		}
		assert scale != 0;
		value = value/scale + shift;
		return value.intValue();
	}


}
