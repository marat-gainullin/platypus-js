package com.eas.client.form.grid.columns;

import java.text.ParseException;

import com.bearsoft.gwt.ui.widgets.ObjectFormat;
import com.eas.client.converters.ObjectRowValueConverter;
import com.eas.client.form.grid.cells.FormattedObjectEditorCell;
import com.eas.client.form.published.widgets.model.ModelFormattedField;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;

public class FormattedObjectModelGridColumn extends ModelGridColumn<Object> {

	public FormattedObjectModelGridColumn(String aName) {
		super(new FormattedObjectEditorCell(), aName, null, null, new ObjectRowValueConverter());
		setEditor(new ModelFormattedField());
	}

	@Override
	public void setEditor(PublishedDecoratorBox<Object> aEditor) {
		super.setEditor(aEditor);
		((FormattedObjectEditorCell)getCell()).setEditor(aEditor);
	}

	public String getFormat() {
		ObjectFormat format = ((FormattedObjectEditorCell) getCell()).getFormat();
		return format != null ? format.getPattern() : null;
	}

	public void setFormat(String aValue) throws ParseException {
		ObjectFormat format = ((FormattedObjectEditorCell) getCell()).getFormat();
		if(format != null){
			format.setPattern(aValue);
		}
		((ModelFormattedField) getEditor()).setFormat(aValue);
	}
}
