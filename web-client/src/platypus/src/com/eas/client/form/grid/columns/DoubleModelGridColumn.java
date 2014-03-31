package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.DoubleEditorCell;
import com.eas.client.converters.DoubleRowValueConverter;
import com.eas.client.form.published.widgets.model.ModelSpin;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;

public class DoubleModelGridColumn extends ModelGridColumn<Double> {

	public DoubleModelGridColumn(String aName) {
		super(new DoubleEditorCell(), aName, null, null, new DoubleRowValueConverter());
		setEditor(new ModelSpin());
	}

	@Override
	public void setEditor(PublishedDecoratorBox<Double> aEditor) {
		super.setEditor(aEditor);
		((DoubleEditorCell)getCell()).setEditor(aEditor);
	}
}
