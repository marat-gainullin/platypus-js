package com.eas.client.gxtcontrols.grid;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.grid.wrappers.PlatypusColumnConfig;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterField;
import com.google.gwt.core.client.JavaScriptObject;

public abstract class ModelGridColumn<T> {

	protected ModelGrid grid;
	protected String name;
	protected PlatypusColumnConfig<Row, T> columnConfig;
	protected PlatypusAdapterField<T> editor;
	protected JavaScriptObject published;
	protected JavaScriptObject cellFunction;
	protected JavaScriptObject selectFunction;

	public ModelGridColumn(String aName) {
		super();
		name = aName;
	}

	public ModelGrid getGrid() {
		return grid;
	}

	public void setGrid(ModelGrid aValue) {
		grid = aValue;
	}

	public JavaScriptObject getEventsThis() {
		return published;
	}

	public boolean isVisible() {
		return !columnConfig.isHidden();
	}

	public void setVisible(boolean aValue) {
		columnConfig.setHidden(!aValue);
	}

	public int getWidth() {
		return columnConfig.getWidth();
	}

	public void setWidth(int aValue) {
		columnConfig.setWidth(aValue);
	}

	public String getTitle() {
		return columnConfig.getHeader().asString();
	}

	public void setTitle(String aValue) {
		columnConfig.setHeader(aValue);
	}

	public boolean isResizable() {
		return columnConfig.isResizable();
	}

	public void setResizable(boolean aValue) {
		columnConfig.setResizable(aValue);
	}

	public boolean isReadonly() {
		return columnConfig.isReadonly();
	}

	public void setReadonly(boolean aValue) {
		columnConfig.setReadonly(aValue);
	}

	public boolean isSortable() {
		return columnConfig.isSortable();
	}

	public void setSortable(boolean aValue) {
		columnConfig.setSortable(aValue);
	}

	public JavaScriptObject getCellFunction() {
		return cellFunction != null ? cellFunction : grid.getGeneralCellFunction();
	}

	public void setCellFunction(JavaScriptObject aValue) {
		cellFunction = aValue;
	}

	public JavaScriptObject getSelectFunction() {
		return selectFunction;
	}

	public void setSelectFunction(JavaScriptObject aValue) {
		selectFunction = aValue;
		if (editor != null)
			editor.setOnSelect(selectFunction);
	}

	public String getName() {
		return name;
	}

	public PlatypusColumnConfig<Row, T> getColumnConfig() {
		return columnConfig;
	}

	public void setColumnConfig(PlatypusColumnConfig<Row, T> aValue) {
		columnConfig = aValue;
	}

	public PlatypusAdapterField<T> getEditor() {
		return editor;
	}

	public void setEditor(PlatypusAdapterField<T> aEditor) {
		editor = aEditor;
		if (editor != null)
			editor.setOnSelect(selectFunction);
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	public void setPublished(JavaScriptObject aValue) {
		published = aValue;
	}

	public abstract void publish(JavaScriptObject aInjectionTarget);
}
