package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.CellRenderer;
import com.bearsoft.gwt.ui.widgets.grid.cells.StringEditorCell;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.rowset.Row;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.grid.RenderedCellContext;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelDecoratorBox;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.TextBox;

public class TextModelColumn extends ModelColumn<String> {

	public TextModelColumn(String aName) {
		super(new TreeExpandableCell<Row, String>(new StringEditorCell()), aName, null, new StringRowValueConverter());
		setEditor(new ModelDecoratorBox<String>(new TextBox()){

			@Override
            public void setJsValue(Object aValue) throws Exception {
            }

			@Override
            public Object getJsValue() {
	            return null;
            }
			
		});
		((StringEditorCell) getTargetCell()).setRenderer(new CellRenderer<String>() {

			@Override
			public boolean render(Context context, String aId, String value, SafeHtmlBuilder sb) {
				TextModelColumn column = TextModelColumn.this;
				JavaScriptObject onRender = column.getOnRender() != null ? column.getOnRender() : column.getGrid().getOnRender();
				if (onRender != null) {
					try {
						String toRender = value;
						PublishedStyle styleToRender = null;
						SafeHtmlBuilder lsb = new SafeHtmlBuilder();
						PublishedCell cellToRender = calcContextPublishedCell(column.getPublished(), column.getOnRender() != null ? column.getOnRender() : grid.getOnRender(), context,
						        field, value);
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
						String decorId = ControlsUtils.renderDecorated(lsb, aId,  styleToRender, sb);
						if (cellToRender != null) {
							if(context instanceof RenderedCellContext){
								((RenderedCellContext)context).setStyle(styleToRender);
							}
							TextModelColumn.this.bindGridDisplayCallback(decorId, cellToRender);
							if(cellToRender.getStyle() != null){
								ModelColumn.bindIconCallback(cellToRender.getStyle(), decorId);
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
	public void setEditor(ModelDecoratorBox<String> aEditor) {
		super.setEditor(aEditor);
		((StringEditorCell) getTargetCell()).setEditor(aEditor);
	}
}
