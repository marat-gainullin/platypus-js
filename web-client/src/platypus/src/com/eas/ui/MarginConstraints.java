package com.eas.ui;

import com.google.gwt.dom.client.Style;

public class MarginConstraints {

	public static class Margin {

		public int value;
		public Style.Unit unit = Style.Unit.PX;

		public Margin() {
			super();
		}

		public Margin(int aValue, Style.Unit aUnit) {
			super();
			value = aValue;
			unit = aUnit;
		}

		public void setPlainValue(int aValue, int aContainerSize) {
			if (unit == Style.Unit.PX)
				value = aValue;
			else
				value = Math.round((float) aValue / (float) aContainerSize * 100);
		}

		public int getPlainValue(int aContainerSize) {
			if (unit == Style.Unit.PX)
				return value;
			else
				return Math.round((float) value / (float) 100 * (float) aContainerSize);
		}
		
		public static Margin parse(String aDefinition) {
			String definition = aDefinition.toLowerCase();
			Style.Unit unit;
			int value;
			if (definition.endsWith(Style.Unit.PX.getType())) {
				value = Integer.valueOf(aDefinition.substring(0, aDefinition.length() - 2));
				unit = Style.Unit.PX;
			} else if (definition.endsWith(Style.Unit.PCT.getType())) {
				value = Integer.valueOf(aDefinition.substring(0, aDefinition.length() - 1));
				unit = Style.Unit.PCT;
			} else {
				value = Integer.valueOf(definition);
				unit = Style.Unit.PX;
			}
			return new Margin(value, unit);
		}
	}

	private Margin left;
	private Margin top;
	private Margin right;
	private Margin bottom;
	private Margin width;
	private Margin height;

	public Margin getLeft() {
		return left;
	}

	public void setLeft(Margin aLeft) {
		left = aLeft;
	}

	public Margin getTop() {
		return top;
	}

	public void setTop(Margin aTop) {
		top = aTop;
	}

	public Margin getRight() {
		return right;
	}

	public void setRight(Margin aRight) {
		right = aRight;
	}

	public Margin getBottom() {
		return bottom;
	}

	public void setBottom(Margin aBottom) {
		bottom = aBottom;
	}

	public Margin getWidth() {
		return width;
	}

	public void setWidth(Margin aWidth) {
		width = aWidth;
	}

	public Margin getHeight() {
		return height;
	}

	public void setHeight(Margin aHeight) {
		height = aHeight;
	}
}
