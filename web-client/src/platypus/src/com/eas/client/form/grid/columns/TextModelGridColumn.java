package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.CellRenderer;
import com.bearsoft.gwt.ui.widgets.grid.cells.StringEditorCell;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.TextBox;

public class TextModelGridColumn extends ModelGridColumn<String> {

	public TextModelGridColumn(String aName) {
		super(new TreeExpandableCell<Row, String>(new StringEditorCell()), aName, null, null, new StringRowValueConverter());
		setEditor(new PublishedDecoratorBox<String>(new TextBox()) {

			@Override
			public void setBinding(Field aField) throws Exception {
			}

		});
		((StringEditorCell) getTargetCell()).setRenderer(new CellRenderer<String>() {

			@Override
			public boolean render(Context context, String value, SafeHtmlBuilder sb) {
				TextModelGridColumn column = TextModelGridColumn.this;
				JavaScriptObject onRender = column.getOnRender() != null ? column.getOnRender() : column.getGrid().getOnRender();
				if (onRender != null) {
					try {
						String toRender = value;
						PublishedStyle styleToRender = null;
						SafeHtmlBuilder lsb = new SafeHtmlBuilder();
						PublishedCell cellToRender = calcContextPublishedCell(column.getPublished(), column.getOnRender() != null ? column.getOnRender() : grid.getOnRender(), context,
						        column.getColumnModelRef(), value, rowsEntity);
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
							TextModelGridColumn.this.bindContextCallback(context, cellToRender);
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
		((StringEditorCell) getTargetCell()).setEditor(aEditor);
	}
}
