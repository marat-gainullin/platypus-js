package com.eas.client.form.grid.columns;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.cells.CellRenderer;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.factories.GridFactory;
import com.eas.client.form.grid.cells.PlatypusTextEditorCell;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelTextArea;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class TextAreaModelGridColumn extends ModelGridColumn<String> {

	public TextAreaModelGridColumn(String aName) {
		super(new PlatypusTextEditorCell(), aName, null, null, new StringRowValueConverter());
		setEditor(new ModelTextArea());
		((PlatypusTextEditorCell) getCell()).setRenderer(new CellRenderer<String>() {
			@Override
			public boolean render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				TextAreaModelGridColumn column = TextAreaModelGridColumn.this;
				JavaScriptObject onRender = column.getOnRender() != null ? column.getOnRender() : column.getGrid().getOnRender();
				if (onRender != null) {
					try {
						PublishedStyle styleToRender = null;
						SafeHtmlBuilder lsb = new SafeHtmlBuilder();
						String toRender = value;
						PublishedCell cellToRender = calcContextPublishedCell(column.getPublished(), onRender, context, column.getColumnModelRef(), value, rowsEntity);
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
						ControlsUtils.renderDecorated(lsb, styleToRender, sb);
						if (cellToRender != null) {
							TextAreaModelGridColumn.this.bindContextCallback(context, cellToRender);
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
	public void setEditor(PublishedDecoratorBox<String> aEditor) {
		super.setEditor(aEditor);
		((PlatypusTextEditorCell) getCell()).setEditor(aEditor);
	}
}
