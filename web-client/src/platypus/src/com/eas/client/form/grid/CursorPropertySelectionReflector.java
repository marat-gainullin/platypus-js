package com.eas.client.form.grid;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.form.grid.selection.HasSelectionLead;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;

public class CursorPropertySelectionReflector implements SelectionChangeEvent.Handler {

	protected SelectionModel<JavaScriptObject> selectionModel;
	protected JavaScriptObject rowsSource;

	public CursorPropertySelectionReflector(JavaScriptObject aRowsSource, SelectionModel<JavaScriptObject> aSelectionModel) {
		super();
		selectionModel = aSelectionModel;
		rowsSource = aRowsSource;
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		if (rowsSource != null && selectionModel instanceof HasSelectionLead<?>) {
			try {
				JavaScriptObject lead = ((HasSelectionLead<JavaScriptObject>) selectionModel).getLead();
				if (lead != null){// To avoid assignment of null value to "cursor"
					scrollTo(rowsSource, lead);
				}
			} catch (Exception e) {
				Logger.getLogger(CursorPropertySelectionReflector.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}

	protected static native void scrollTo(JavaScriptObject aThis, JavaScriptObject aTarget)/*-{
		if (typeof aThis.cursor != 'undefined')
			aThis.cursor = aTarget;
	}-*/;
}
