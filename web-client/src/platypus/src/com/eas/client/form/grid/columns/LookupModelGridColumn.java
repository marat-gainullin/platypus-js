package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.CellRenderer;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.events.RowsetEvent;
import com.eas.client.converters.ObjectRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.CrossUpdater;
import com.eas.client.form.grid.RenderedCellContext;
import com.eas.client.form.grid.cells.PlatypusLookupEditorCell;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelCombo;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class LookupModelGridColumn extends ModelGridColumn<Object> {

	protected ModelElementRef lookupValueRef;
	protected ModelElementRef displayValueRef;
	protected boolean list = true;
	protected CrossUpdater crossUpdater = new CrossUpdater(new Callback<RowsetEvent, RowsetEvent>() {

		@Override
		public void onFailure(RowsetEvent reason) {
		}

		@Override
		public void onSuccess(RowsetEvent result) {
			if (grid != null)
				grid.redraw();
		}

	});

	public LookupModelGridColumn(String aName) {
		super(new TreeExpandableCell<Row, Object>(new PlatypusLookupEditorCell()), aName, null, null, new ObjectRowValueConverter());
		setEditor(new ModelCombo());
		final ModelCombo editor = (ModelCombo) getEditor();
		editor.setForceRedraw(true);
		editor.setList(list);
		((PlatypusLookupEditorCell) getTargetCell()).setRenderer(new CellRenderer<Object>() {

			@Override
			public boolean render(Context context, String aId, Object value, SafeHtmlBuilder sb) {
				LookupModelGridColumn column = LookupModelGridColumn.this;
				JavaScriptObject onRender = column.getOnRender() != null ? column.getOnRender() : column.getGrid().getOnRender();
				if (onRender != null) {
					try {
						PublishedStyle styleToRender = null;
						SafeHtmlBuilder lsb = new SafeHtmlBuilder();
						Row valueRow = editor.getLookup().lookupRow(value);
						String toRender = valueRow != null ? ((ModelCombo) getEditor()).getConverter().convert(valueRow.getColumnObject(displayValueRef.getColIndex())) : "";
						PublishedCell cellToRender = calcContextPublishedCell(column.getPublished(), onRender, context, column.getColumnModelRef(), toRender, rowsEntity);
						if (cellToRender != null) {
							styleToRender = cellToRender.getStyle();
							if (cellToRender.getDisplay() != null)
								toRender = cellToRender.getDisplay();
						}
						if (toRender == null)
							lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
						else
							lsb.append(SafeHtmlUtils.fromString(toRender));
						styleToRender = grid.complementPublishedStyle(styleToRender);
						String decorId = ControlsUtils.renderDecorated(lsb, aId, styleToRender, sb);
						if (cellToRender != null) {
							if (context instanceof RenderedCellContext) {
								((RenderedCellContext) context).setStyle(styleToRender);
							}
							LookupModelGridColumn.this.bindGridDisplayCallback(decorId, cellToRender);
							if (cellToRender.getStyle() != null) {
								ModelGridColumn.bindIconCallback(cellToRender.getStyle(), decorId);
							}
						}
					} catch (Exception e) {
						sb.append(SafeHtmlUtils.fromString(e.getMessage()));
					}
					return true;
				} else
					return false;
			}
		});
	}

	@Override
	public void setEditor(PublishedDecoratorBox<Object> aEditor) {
		super.setEditor(aEditor);
		((PlatypusLookupEditorCell) getTargetCell()).setEditor(aEditor);
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean aValue) {
		if (list != aValue) {
			list = aValue;
			((ModelCombo) getEditor()).setList(list);
		}
	}

	public ModelElementRef getLookupValueRef() {
		return lookupValueRef;
	}

	public void setLookupValueRef(ModelElementRef aValue) {
		if (lookupValueRef != aValue) {
			if (lookupValueRef != null)
				crossUpdater.remove(lookupValueRef.entity);
			lookupValueRef = aValue;
			((ModelCombo) getEditor()).setValueElement(lookupValueRef);
			if (lookupValueRef != null)
				crossUpdater.add(lookupValueRef.entity);
		}
	}

	public ModelElementRef getDisplayValueRef() {
		return displayValueRef;
	}

	public void setDisplayValueRef(ModelElementRef aValue) {
		if (displayValueRef != aValue) {
			if (displayValueRef != null)
				crossUpdater.remove(displayValueRef.entity);
			displayValueRef = aValue;
			((ModelCombo) getEditor()).setDisplayElement(displayValueRef);
			if (displayValueRef != null)
				crossUpdater.add(displayValueRef.entity);
		}
	}

	@Override
	public void setOnRender(JavaScriptObject aValue) {
		super.setOnRender(aValue);
		((ModelCombo) getEditor()).setOnRender(aValue);
	}

	public String getEmptyText() {
		return ((ModelCombo) getEditor()).getEmptyText();
	}

	public void setEmptyText(String aValue) {
		((ModelCombo) getEditor()).setEmptyText(aValue);
	}
}
