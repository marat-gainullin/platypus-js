/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.containers;

import java.util.ArrayList;
import java.util.List;

import com.eas.ui.XElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class GridPanel extends Grid implements RequiresResize, ProvidesResize, IndexedPanel {

	protected int hgap;
	protected int vgap;

	public GridPanel() {
		super();
		setCellPadding(0);
		setCellSpacing(0);
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	public GridPanel(int aRows, int aCols) {
		super(aRows, aCols);
		setCellPadding(0);
		setCellSpacing(0);
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	public int getHgap() {
		return hgap;
	}

	public void setHgap(int aValue) {
		hgap = aValue;
		if (isAttached()) {
			formatCells();
		}
	}

	public int getVgap() {
		return vgap;
	}

	public void setVgap(int aValue) {
		vgap = aValue;
		if (isAttached()) {
			formatCells();
		}
	}

	@Override
	public void resizeColumns(int columns) {
		super.resizeColumns(columns);
		if (isAttached()) {
			formatCells();
		}
	}

	@Override
	public void resizeRows(int rows) {
		super.resizeRows(rows);
		if (isAttached()) {
			formatCells();
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		formatCells();
	}

	protected void formatCells() {
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				formatCell(i, j);
			}
		}
	}

	protected void formatCell(int aRow, int aColumn) {
		Widget w = getWidget(aRow, aColumn);
		if (w != null) {
			Element we = w.getElement();
			Element wpe = we.getParentElement();
			Element tde = wpe.getParentElement();
			tde.getStyle().setPosition(Style.Position.RELATIVE);

			wpe.getStyle().setPosition(Style.Position.ABSOLUTE);
			wpe.getStyle().setLeft(hgap / 2, Style.Unit.PX);
			wpe.getStyle().setRight(hgap / 2, Style.Unit.PX);
			wpe.getStyle().setTop(vgap / 2, Style.Unit.PX);
			wpe.getStyle().setBottom(vgap / 2, Style.Unit.PX);
			wpe.getStyle().setPadding(0, Style.Unit.PX);
			wpe.getStyle().setMargin(0, Style.Unit.PX);

			we.getStyle().setPosition(Style.Position.ABSOLUTE);
			we.getStyle().setLeft(0, Style.Unit.PX);
			we.getStyle().setTop(0, Style.Unit.PX);
			we.getStyle().clearRight();
			we.getStyle().clearBottom();
			we.getStyle().setWidth(100, Style.Unit.PCT);
			we.getStyle().setHeight(100, Style.Unit.PCT);
			com.eas.ui.CommonResources.INSTANCE.commons().ensureInjected();
			we.addClassName(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
		}
	}

	public boolean addToFreeCell(Widget aWidget) {
		for (int row = 0; row < getRowCount(); row++) {
			for (int col = 0; col < getColumnCount(); col++) {
				Widget w = getWidget(row, col);
				if (w == null) {
					setWidget(row, col, aWidget);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void setWidget(int row, int column, Widget widget) {
		Widget oldWidget = getWidget(row, column);
		if (oldWidget != null) {
			remove(oldWidget);
		}
		super.setWidget(row, column, new SimplePanel(widget));
		formatCell(row, column);
		if (widget instanceof RequiresResize) {
			((RequiresResize) widget).onResize();
		}
	}

	@Override
	public Widget getWidget(int row, int column) {
		Widget w = super.getWidget(row, column);
		return w instanceof SimplePanel ? ((SimplePanel) w).getWidget() : null;
	}

	@Override
	public boolean remove(Widget widget) {
		return super.remove(widget.getParent());
	}

	@Override
	public void onResize() {
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				Element td = getCellFormatter().getElement(i, j);
				td.getStyle().setWidth(100 / numColumns, Style.Unit.PCT);
				td.getStyle().setHeight(100 / numRows, Style.Unit.PCT);
			}
		}
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				Widget w = getWidget(i, j);
				// checkFocusWidgetWidthHeight(w);
				if (w instanceof RequiresResize) {
					((RequiresResize) w).onResize();
				}
			}
		}
	}

	@Override
	public Widget getWidget(int index) {
		List<Widget> widgets = new ArrayList<>();
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				Widget w = getWidget(i, j);
				if (w != null) {
					widgets.add(w);
				}
			}
		}
		return index >= 0 && index < widgets.size() ? widgets.get(index) : null;
	}

	@Override
	public int getWidgetCount() {
		int count = 0;
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				Widget w = getWidget(i, j);
				if (w != null) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public int getWidgetIndex(Widget child) {
		List<Widget> widgets = new ArrayList<>();
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				Widget w = getWidget(i, j);
				if (w != null) {
					widgets.add(w);
				}
			}
		}
		return widgets.indexOf(child);
	}

	@Override
	public boolean remove(int index) {
		int count = 0;
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				Widget w = getWidget(i, j);
				if (w != null) {
					count++;
					if (index == count - 1) {
						return remove(w);
					}
				}
			}
		}
		return false;
	}
}
