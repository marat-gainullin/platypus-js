package com.eas.client.gxtcontrols.wrappers.component;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.grid.ModelGridColumn;
import com.eas.client.gxtcontrols.grid.wrappers.PlatypusGridInlineEditing;
import com.eas.client.gxtcontrols.grid.wrappers.Resumable;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.event.StartEditEvent;
import com.sencha.gxt.widget.core.client.event.StartEditEvent.StartEditHandler;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;

public class PlatypusAdapterCellField<T> extends PlatypusAdapterField<T> implements Resumable {
	protected ModelGridColumn<T> column;
	protected PlatypusGridInlineEditing<Row> gridEditing;
	protected HandlerRegistration startEditingRegistration;
	protected GridCell editedCell;

	public PlatypusAdapterCellField(Field<T> aTarget, ModelGridColumn<T> aColumn) {
		super(aTarget);
		column = aColumn;
	}

	@Override
	protected JavaScriptObject getEventsThis() {
		return column.getEventsThis();
	}

	public PlatypusGridInlineEditing<Row> getGridEditing() {
		return gridEditing;
	}

	public void setGridEditing(PlatypusGridInlineEditing<Row> aEditing) {
		if (gridEditing != aEditing) {
			if (startEditingRegistration != null)
				startEditingRegistration.removeHandler();
			editedCell = null;
			gridEditing = aEditing;
			if (gridEditing != null)
				gridEditing.addStartEditHandler(new StartEditHandler<Row>() {

					@Override
					public void onStartEdit(StartEditEvent<Row> event) {
						editedCell = event.getEditCell();
					}

				});
		}
	}

	public void resume() {
		if (!isAttached() && gridEditing != null && editedCell != null)
			gridEditing.startEditing(editedCell);
	}
	
	public void commit(){
		if(gridEditing.isEditing())
			gridEditing.completeEditing();
	}
}
