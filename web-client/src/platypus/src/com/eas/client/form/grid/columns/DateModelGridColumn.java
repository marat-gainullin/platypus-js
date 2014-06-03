package com.eas.client.form.grid.columns;

import java.util.Date;

import com.bearsoft.gwt.ui.widgets.grid.cells.CellRenderer;
import com.bearsoft.gwt.ui.widgets.grid.cells.DateEditorCell;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.rowset.Row;
import com.eas.client.application.PlatypusImageResource;
import com.eas.client.converters.DateRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelDate;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class DateModelGridColumn extends ModelGridColumn<Date> {

	public DateModelGridColumn(String aName) {
		super(new TreeExpandableCell<Row, Date>(new DateEditorCell()), aName, null, null, new DateRowValueConverter());
		setEditor(new ModelDate());
		((DateEditorCell) getTargetCell()).setRenderer(new CellRenderer<Date>() {

			@Override
			public boolean render(Context context, Date value, SafeHtmlBuilder sb) {
				DateModelGridColumn column = DateModelGridColumn.this;
				JavaScriptObject onRender = column.getOnRender() != null ? column.getOnRender() : column.getGrid().getOnRender();
				if (onRender != null) {
					try {
						SafeHtmlBuilder lsb = new SafeHtmlBuilder();
						PublishedStyle styleToRender = null;
						String toRender = value != null ? getFormat().format(value) : null;
						PublishedCell cellToRender = calcContextPublishedCell(column.getPublished(), onRender, context,
						        column.getColumnModelRef(), toRender, rowsEntity);
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
							DateModelGridColumn.this.bindDisplayCallback(decorId, cellToRender);		
							if(cellToRender.getStyle() != null && cellToRender.getStyle().getIcon() instanceof PlatypusImageResource){
								PlatypusImageResource pImage = (PlatypusImageResource)cellToRender.getStyle().getIcon();
								DateModelGridColumn.this.bindIconCallback(decorId, pImage);
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
	public void setEditor(PublishedDecoratorBox<Date> aEditor) {
		super.setEditor(aEditor);
		((DateEditorCell) getTargetCell()).setEditor(aEditor);
	}

	public DateTimeFormat getFormat() {
		return ((DateEditorCell) getTargetCell()).getFormat();
	}

	public void setFormat(DateTimeFormat aValue) {
		((DateEditorCell) getTargetCell()).setFormat(aValue);
		((ModelDate) getEditor()).setFormat(aValue.getPattern());
	}	
	
	public String getEmptyText(){
		return ((ModelDate)getEditor()).getEmptyText();
	}
	
	public void setEmptyText(String aValue) {
		((ModelDate)getEditor()).setEmptyText(aValue);
    }
}