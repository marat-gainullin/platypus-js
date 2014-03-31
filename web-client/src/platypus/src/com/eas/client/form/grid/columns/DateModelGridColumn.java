package com.eas.client.form.grid.columns;

import java.util.Date;

import com.bearsoft.gwt.ui.widgets.grid.cells.DateEditorCell;
import com.eas.client.converters.DateRowValueConverter;
import com.eas.client.form.published.widgets.model.ModelDate;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DateModelGridColumn extends ModelGridColumn<Date> {

	public DateModelGridColumn(String aName) {
		super(new DateEditorCell(), aName, null, null, new DateRowValueConverter());
		setEditor(new ModelDate());
	}

	@Override
	public void setEditor(PublishedDecoratorBox<Date> aEditor) {
		super.setEditor(aEditor);
		((DateEditorCell)getCell()).setEditor(aEditor);
	}

	public DateTimeFormat getFormat() {
		return ((DateEditorCell) getCell()).getFormat();
	}

	public void setFormat(DateTimeFormat aValue) {
		((DateEditorCell) getCell()).setFormat(aValue);
		((ModelDate) getEditor()).setFormat(aValue.getPattern());
	}
}
