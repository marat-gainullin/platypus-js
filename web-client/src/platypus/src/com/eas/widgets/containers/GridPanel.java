/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.containers;

import java.util.ArrayList;
import java.util.List;

import com.eas.core.XElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class GridPanel extends FlowPanel implements RequiresResize, ProvidesResize, IndexedPanel {

	
	protected int rows = 1;
	protected int columns = 1;
	protected Widget[][] grid = new Widget[rows][columns];
	
	protected int hgap;
	protected int vgap;
	
	public GridPanel(int aRows, int aCols) {
		super();
		rows = aRows;
		columns = aCols;
		grid = new Widget[rows][0];
		for(int r = 0; r < rows; r++){
			grid[r] = new Widget[columns];
		}
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	public int getRowCount(){
		return rows;
	}
	
	public int getColumnCount(){
		return columns;
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
	protected void onAttach() {
		super.onAttach();
		formatCells();
	}

	protected void formatCells() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				formatCell(i, j);
			}
		}
	}

	protected void formatCell(int aRow, int aColumn) {
		com.eas.ui.CommonResources.INSTANCE.commons().ensureInjected();
		Widget w = getWidget(aRow, aColumn);
		if (w != null) {
			Element we = w.getElement();
			Element wpe = we.getParentElement();

			wpe.getStyle().setPosition(Style.Position.ABSOLUTE);
			wpe.getStyle().setLeft((100f / columns) * aColumn, Style.Unit.PCT);
			wpe.getStyle().setTop((100f / rows) * aRow, Style.Unit.PCT);
			wpe.getStyle().setWidth(100f / columns, Style.Unit.PCT);
			wpe.getStyle().setHeight(100f / rows, Style.Unit.PCT);
			wpe.getStyle().setMargin(0, Style.Unit.PX);
			wpe.getStyle().setPaddingLeft(Math.floor(hgap / 2f), Style.Unit.PX);
			wpe.getStyle().setPaddingRight(Math.ceil(hgap / 2f), Style.Unit.PX);
			wpe.getStyle().setPaddingTop(Math.floor(vgap / 2f), Style.Unit.PX);
			wpe.getStyle().setPaddingBottom(Math.ceil(vgap / 2f), Style.Unit.PX);
			wpe.addClassName(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());

			we.getStyle().setPosition(Style.Position.RELATIVE);
			we.getStyle().setWidth(100, Style.Unit.PCT);
			we.getStyle().setHeight(100, Style.Unit.PCT);
			
			we.addClassName(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
		}
	}

	public boolean addToFreeCell(Widget aWidget) {
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[row].length; col++) {
				Widget w = getWidget(row, col);
				if (w == null) {
					setWidget(row, col, aWidget);
					return true;
				}
			}
		}
		return false;
	}

	public void setWidget(int row, int column, Widget widget) {
		Widget oldWidget = getWidget(row, column);
		if (oldWidget != null) {
			remove(oldWidget);
		}
		grid[row][column] = new SimplePanel(widget);
		super.add(grid[row][column]);
		formatCell(row, column);
		if (widget instanceof RequiresResize) {
			((RequiresResize) widget).onResize();
		}
	}

	public Widget getWidget(int row, int column) {
		Widget w = grid[row][column];
		return w instanceof SimplePanel ? ((SimplePanel) w).getWidget() : null;
	}

	@Override
	public boolean remove(Widget widget) {
		return super.remove(widget.getParent());
	}

	@Override
	public void onResize() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				Widget cell = grid[i][j];
				if(cell != null){
					cell.getElement().getStyle().setWidth(100 / grid[i].length, Style.Unit.PCT);
					cell.getElement().getStyle().setHeight(100 / grid.length, Style.Unit.PCT);
				}
			}
		}
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				Widget w = getWidget(i, j);
				if (w instanceof RequiresResize) {
					((RequiresResize) w).onResize();
				}
			}
		}
	}

	@Override
	public Widget getWidget(int index) {
		List<Widget> widgets = new ArrayList<>();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
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
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
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
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
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
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
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
