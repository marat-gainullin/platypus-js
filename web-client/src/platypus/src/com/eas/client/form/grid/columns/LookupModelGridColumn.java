package com.eas.client.form.grid.columns;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.cells.CellRenderer;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.client.application.PlatypusImageResource;
import com.eas.client.converters.RowRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.combo.ValueLookup;
import com.eas.client.form.grid.cells.PlatypusLookupEditorCell;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelCombo;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class LookupModelGridColumn extends ModelGridColumn<Row> {

	protected ValueLookup lookup;

	protected ModelElementRef lookupValueRef;
	protected ModelElementRef displayValueRef;
	protected boolean list = true;

	public LookupModelGridColumn(String aName) {
		super(new TreeExpandableCell<Row, Row>(new PlatypusLookupEditorCell()), aName, null, null, new RowRowValueConverter());
		// TODO: fill the editor, test for data changes in lookup and value
		// rowsets
		setEditor(new ModelCombo());
		((ModelCombo)getEditor()).setForceRedraw(true);
		((PlatypusLookupEditorCell) getTargetCell()).setRenderer(new CellRenderer<Row>() {

			@Override
			public boolean render(Context context, Row value, SafeHtmlBuilder sb) {
				LookupModelGridColumn column = LookupModelGridColumn.this;
				JavaScriptObject onRender = column.getOnRender() != null ? column.getOnRender() : column.getGrid().getOnRender();
				if (onRender != null) {
					try {
						PublishedStyle styleToRender = null;
						SafeHtmlBuilder lsb = new SafeHtmlBuilder();
						String toRender = String.valueOf(value); // TODO: change
																 // to lookup
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
						String decorId = ControlsUtils.renderDecorated(lsb, styleToRender, sb);
						if (cellToRender != null) {
							LookupModelGridColumn.this.bindDisplayCallback(decorId, cellToRender);
							if (cellToRender.getStyle() != null && cellToRender.getStyle().getIcon() instanceof PlatypusImageResource) {
								PlatypusImageResource pImage = (PlatypusImageResource) cellToRender.getStyle().getIcon();
								LookupModelGridColumn.this.bindIconCallback(decorId, pImage);
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
	public void setEditor(PublishedDecoratorBox<Row> aEditor) {
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
		lookupValueRef = aValue;
		((ModelCombo) getEditor()).setValueElement(lookupValueRef);
	}

	public ModelElementRef getDisplayValueRef() {
		return displayValueRef;
	}

	public void setDisplayValueRef(ModelElementRef aValue) {
		displayValueRef = aValue;
		((ModelCombo) getEditor()).setDisplayElement(displayValueRef);
	}

	protected void init() {
		if (lookup == null)
			lookup = new ValueLookup(lookupValueRef);
	}

	public ValueLookup getLookup() {
		init();
		return lookup;
	}

	public boolean tryInit() {
		if (columnModelRef.entity.getRowset() != null && lookupValueRef.entity.getRowset() != null && displayValueRef.entity.getRowset() != null) {
			ValueLookup lookup = getLookup();
			return lookup != null && lookup.tryInit();
		}
		return false;
	}

	public boolean isChanged(Row aRow) {
		init();
		if (aRow != null && columnModelRef.getColIndex() > 0)
			try {
				if (rowsEntity == columnModelRef.entity) {
					return aRow.isColumnUpdated(columnModelRef.getColIndex());
				} else {
					if (rowsEntity.scrollTo(aRow) && columnModelRef.entity.getRowset() != null) {
						Row lRow = columnModelRef.entity.getRowset().getCurrentRow();
						return lRow.isColumnUpdated(columnModelRef.getColIndex());
					}
				}
			} catch (Exception e) {
				Logger.getLogger(LookupModelGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		return false;
	}

	@Override
	public Row getValue(Row aRow) {
		init();
		if (aRow != null && columnModelRef.getColIndex() > 0)
			try {
				if (rowsEntity == columnModelRef.entity) {
					Object lookupKeyValue = aRow != null ? aRow.getColumnObject(columnModelRef.getColIndex()) : null;
					return lookup.lookupRow(lookupKeyValue);
				} else {
					if (rowsEntity.scrollTo(aRow) && columnModelRef.entity.getRowset() != null) {
						Rowset columnRowset = columnModelRef.entity.getRowset();
						Row colRowsetRow = columnRowset.getCurrentRow();
						Object lookupKeyValue = colRowsetRow != null ? colRowsetRow.getColumnObject(columnModelRef.getColIndex()) : null;
						return lookup.lookupRow(lookupKeyValue);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(LookupModelGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		return null;
	}

	@Override
	public void update(int aIndex, Row aRow, Row value) {
		init();
		if (aRow != null && columnModelRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnModelRef.entity) {
					Object lookupKeyValue = value != null ? value.getColumnObject(lookup.getLookupValueRef().getColIndex()) : null;
					aRow.setColumnObject(columnModelRef.getColIndex(), lookupKeyValue);
				} else {
					if (rowsEntity.scrollTo(aRow) && columnModelRef.entity.getRowset() != null) {
						Object lookupKeyValue = value != null ? columnModelRef.entity.getRowset().getObject(lookup.getLookupValueRef().getColIndex()) : null;
						columnModelRef.entity.getRowset().updateObject(columnModelRef.getColIndex(), lookupKeyValue);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(LookupModelGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}

	public String getEmptyText(){
		return ((ModelCombo)getEditor()).getEmptyText();
	}
	
	public void setEmptyText(String aValue) {
		((ModelCombo)getEditor()).setEmptyText(aValue);
    }
}
