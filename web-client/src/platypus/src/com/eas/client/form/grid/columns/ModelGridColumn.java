package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.cellview.client.Column;

public class ModelGridColumn<O, T> extends ModelGridColumnBase {

	protected Column<O, T> gridColumn;
	protected DraggableHeader<T> header;
	protected PublishedDecoratorBox<T> editor;

	public ModelGridColumn(String aName) {
		super(aName);
	}

	public Column<O, T> getGridColumn() {
		return gridColumn;
	}

	public void setGridColumn(Column<O, T> aGridColumn) {
		gridColumn = aGridColumn;
	}

	public String getTitle() {
		return header != null ? header.getTitle() : null;
	}

	public void setTitle(String aValue) {
		if (header != null)
			header.setTitle(aValue);
	}

	@Override
	public void setOnSelect(JavaScriptObject aValue) {
		super.setOnSelect(aValue);
		if (editor != null)
			editor.setOnSelect(onSelect);
	}

	public DraggableHeader<T> getHeader() {
		return header;
	}

	public void setHeader(DraggableHeader<T> aValue) {
		header = aValue;
	}

	public PublishedDecoratorBox<T> getEditor() {
		return editor;
	}

	public void setEditor(PublishedDecoratorBox<T> aEditor) {
		editor = aEditor;
		if (editor != null)
			editor.setOnSelect(onSelect);
	}

}
