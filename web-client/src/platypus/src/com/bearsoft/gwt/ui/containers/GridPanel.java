/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import java.util.ArrayList;
import java.util.List;

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
    }

    public GridPanel(int aRows, int aCols) {
        super(aRows, aCols);
        setCellPadding(0);
        setCellSpacing(0);
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
        if (w != null/* && getElement().getClientWidth() > 0 && getElement().getClientHeight() > 0*/) {
            /*
            Element td = getCellFormatter().getElement(aRow, aColumn);
            double tdWidth = Math.round((double)numColumns / (double)getElement().getClientWidth() * 100);
            double tdHeight = Math.round((double)numRows / (double)getElement().getClientHeight() * 100);
            td.getStyle().setWidth(tdWidth, Style.Unit.PCT);
            td.getStyle().setHeight(tdHeight, Style.Unit.PCT);
                    */
            Element we = w.getElement();
            Element wpe = we.getParentElement();
            wpe.getStyle().setPosition(Style.Position.RELATIVE);
            wpe.getStyle().setWidth(100, Style.Unit.PCT);
            wpe.getStyle().setHeight(100, Style.Unit.PCT);
            wpe.getStyle().setPadding(0, Style.Unit.PX);
            wpe.getStyle().setMargin(0, Style.Unit.PX);

            we.getStyle().setPosition(Style.Position.ABSOLUTE);
            we.getStyle().setLeft(0, Style.Unit.PX);
            we.getStyle().setRight(0, Style.Unit.PX);
            we.getStyle().setTop(0, Style.Unit.PX);
            we.getStyle().setBottom(0, Style.Unit.PX);
            we.getStyle().setMarginLeft(aColumn > 0 ? hgap : 0, Style.Unit.PX);
            we.getStyle().setMarginTop(aRow > 0 ? vgap : 0, Style.Unit.PX);
            we.getStyle().setMarginRight(0, Style.Unit.PX);
            we.getStyle().setMarginBottom(0, Style.Unit.PX);
            checkButtonWidth(w);
        }
    }

    protected void checkButtonWidth(Widget w) {
        Element we = w.getElement();
        Element wpe = we.getParentElement();
        if ("button".equalsIgnoreCase(we.getTagName())) {
            we.getStyle().setWidth(wpe.getClientWidth() - hgap, Style.Unit.PX);
        }
    }

    @Override
    public void setWidget(int row, int column, Widget widget) {
        super.setWidget(row, column, new SimplePanel(widget));
        formatCell(row, column);
        if (widget instanceof RequiresResize) {
            ((RequiresResize) widget).onResize();
        }
    }

    @Override
    public Widget getWidget(int row, int column) {
        Widget w = super.getWidget(row, column);
        assert w instanceof SimplePanel;
        return ((SimplePanel) w).getWidget();
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
                td.getStyle().setWidth(0, Style.Unit.PCT);
                td.getStyle().setHeight(0, Style.Unit.PCT);
            }
        }
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                Widget w = getWidget(i, j);
                checkButtonWidth(w);
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
                if(w != null)
                	widgets.add(w);
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
                if(w != null)
                	count++;
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
                if(w != null)
                	widgets.add(w);
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
                if(w != null){
                	count++;
                	if(index == count - 1){
                		return remove(w);
                	}
                }
            }
        }
	    return false;
    }
}
