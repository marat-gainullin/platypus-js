package com.eas.client.form.grid.columns;

import java.text.ParseException;

import com.bearsoft.gwt.ui.widgets.ObjectFormat;
import com.bearsoft.gwt.ui.widgets.grid.cells.CellRenderer;
import com.eas.client.converters.ObjectRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.grid.cells.PlatypusFormattedObjectEditorCell;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelFormattedField;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class FormattedObjectModelGridColumn extends ModelGridColumn<Object> {

	public FormattedObjectModelGridColumn(String aName) {
		super(new PlatypusFormattedObjectEditorCell(), aName, null, null, new ObjectRowValueConverter());
		setEditor(new ModelFormattedField());
		((PlatypusFormattedObjectEditorCell) getCell()).setRenderer(new CellRenderer<Object>() {
			@Override
			public boolean render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb) {
				FormattedObjectModelGridColumn column = FormattedObjectModelGridColumn.this;
				JavaScriptObject onRender = column.getOnRender() != null ? column.getOnRender() : column.getGrid().getOnRender();
				if (onRender != null) {
					try {
						PublishedStyle styleToRender = null;
						SafeHtmlBuilder lsb = new SafeHtmlBuilder();
						String toRender = ((PlatypusFormattedObjectEditorCell) getCell()).getFormat().format(value);
						PublishedCell cellToRender = calcContextPublishedCell(column.getPublished(), onRender, context, column.getColumnModelRef(), toRender, column.getRowsEntity());
						if (cellToRender != null) {
							styleToRender = cellToRender.getStyle();
							if (cellToRender.getDisplay() != null)
								toRender = cellToRender.getDisplay();
						}
						if (toRender == null)
							lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
						else
							lsb.append(SafeHtmlUtils.fromString(toRender));
						styleToRender = column.getGrid().complementPublishedStyle(styleToRender);
						ControlsUtils.renderDecorated(lsb, styleToRender, sb);
						if (cellToRender != null) {
							FormattedObjectModelGridColumn.this.bindContextCallback(context, cellToRender);
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
		((PlatypusFormattedObjectEditorCell) getCell()).setEditor(aEditor);
	}

	public String getFormat() {
		ObjectFormat format = ((PlatypusFormattedObjectEditorCell) getCell()).getFormat();
		return format != null ? format.getPattern() : null;
	}

	public void setFormat(String aValue) throws ParseException {
		ObjectFormat format = ((PlatypusFormattedObjectEditorCell) getCell()).getFormat();
		if (format != null) {
			format.setPattern(aValue);
		}
		((ModelFormattedField) getEditor()).setFormat(aValue);
	}
	
	public void setFormatType(int aType, String aPattern) throws ParseException {
		((PlatypusFormattedObjectEditorCell) getCell()).setFormat(new ObjectFormat(aType, aPattern));
		((ModelFormattedField) getEditor()).setFormatType(aType, aPattern);
	}
}
