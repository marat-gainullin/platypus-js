/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.gxtcontrols.wrappers.container;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;

/**
 * 
 * @author vy
 */
public class PlatypusGridLayoutContainer extends ResizeContainer {

	private static class GridConstraints {
		public int row;
		public int column;

		public GridConstraints(int aRow, int aColumn) {
			super();
			row = aRow;
			column = aColumn;
		}

		@Override
		public String toString() {
			return "[" + row + "; " + column + "]";
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (obj.getClass() != getClass())
				return false;
			GridConstraints other = (GridConstraints) obj;
			return column == other.column && row == other.row;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 89 * hash + column;
			hash = 89 * hash + row;
			return hash;
		}
	}

	private static Logger logger = Logger.getLogger(PlatypusGridLayoutContainer.class.getName());

	private int columns;
	private int rows;
	private int hGap;
	private int vGap;
	protected Map<GridConstraints, Widget> widgets = new HashMap();

	// runtime
	private int currentIndex;

	public PlatypusGridLayoutContainer() {
		super();
		setElement(DOM.createDiv());
		getContainerTarget().makePositionable(true);
	}

	public Widget getWidget(int aRow, int aColumn) {
		return widgets.get(new GridConstraints(aRow, aColumn));
	}

	public void setWidget(Widget aWidget, int aRow, int aColumn) {
		GridConstraints key = new GridConstraints(aRow, aColumn);
		if (widgets.containsKey(key)) {
			super.remove(widgets.get(key));
		}
		widgets.put(key, aWidget);
		if (aWidget != null) {
			aWidget.getElement().getStyle().setPosition(Position.ABSOLUTE);
			super.add(aWidget);
			if (isAttached())
				forceLayout();
		}
	}

	@Override
	public void add(Widget aChild) {
		if (columns > 0) {
			int row = currentIndex / columns;
			int column = currentIndex % columns;
			currentIndex++;
			setWidget(aChild, row, column);
		}
	}

	/**
	 * @return the columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * @param columns
	 *            the columns to set
	 */
	public void setColumns(int aColumns) {
		if (aColumns > 0)
			columns = aColumns;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int aValue) {
		if (aValue > 0)
			rows = aValue;
	}

	public int getHGap() {
		return hGap;
	}

	public void setHGap(int aValue) {
		hGap = aValue;
	}

	public int getVGap() {
		return vGap;
	}

	public void setVGap(int aValue) {
		vGap = aValue;
	}

	@Override
	protected void doLayout() {
		Size size = getContainerTarget().getStyleSize();
		for (GridConstraints gc : widgets.keySet()) {
			Widget widget = widgets.get(gc);
			if (widget != null) {
				int cellWidth = (size.getWidth() + hGap) / columns;
				int cellHeight = (size.getHeight() + vGap) / rows;
				int x = gc.column * cellWidth;
				int y = gc.row * cellHeight;
				int width = cellWidth - hGap;
				int height = cellHeight - vGap;
				applyLayout(widget, new Rectangle(x, y, width, height));
			}
		}
	}

	@Override
	public boolean remove(Widget child) {
		for (GridConstraints gc : widgets.keySet()) {
			Widget widget = widgets.get(gc);
			if (widget == child) {
				widgets.remove(gc);
				if (gc.row * columns + gc.column == currentIndex - 1)
					currentIndex--;
				break;
			}
		}
		return super.remove(child);
	}

	@Override
	public void clear() {
		widgets.clear();
		currentIndex = 0;
		super.clear();
	}
}
