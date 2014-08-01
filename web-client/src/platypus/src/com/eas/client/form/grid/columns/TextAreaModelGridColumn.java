package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.CellRenderer;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.rowset.Row;
import com.eas.client.application.PlatypusImageResource;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.grid.RenderedCellContext;
import com.eas.client.form.grid.cells.PlatypusTextEditorCell;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelTextArea;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class TextAreaModelGridColumn extends ModelGridColumn<String> {

	public TextAreaModelGridColumn(String aName) {
		super(new TreeExpandableCell<Row, String>(new PlatypusTextEditorCell()), aName, null, null, new StringRowValueConverter());
		setEditor(new ModelTextArea());
		((PlatypusTextEditorCell) getTargetCell()).setRenderer(new CellRenderer<String>() {
			@Override
			public boolean render(Cell.Context context, String aId, String value, SafeHtmlBuilder sb) {
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
						String decorId = ControlsUtils.renderDecorated(lsb, aId,  styleToRender, sb);
						if (cellToRender != null) {
							if(context instanceof RenderedCellContext){
								((RenderedCellContext)context).setStyle(styleToRender);
							}
							TextAreaModelGridColumn.this.bindGridDisplayCallback(decorId, cellToRender);		
							if(cellToRender.getStyle() != null && cellToRender.getStyle().getIcon() instanceof PlatypusImageResource){
								PlatypusImageResource pImage = (PlatypusImageResource)cellToRender.getStyle().getIcon();
								TextAreaModelGridColumn.this.bindIconCallback(decorId, pImage);
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
	public void setEditor(PublishedDecoratorBox<String> aEditor) {
		super.setEditor(aEditor);
		((PlatypusTextEditorCell) getTargetCell()).setEditor(aEditor);
	}
	
	public String getEmptyText(){
		return ((ModelTextArea)getEditor()).getEmptyText();
	}
	
	public void setEmptyText(String aValue) {
		((ModelTextArea)getEditor()).setEmptyText(aValue);
    }
}
