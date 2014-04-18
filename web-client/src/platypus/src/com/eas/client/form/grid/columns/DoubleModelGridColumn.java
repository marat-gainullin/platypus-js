package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.CellRenderer;
import com.bearsoft.gwt.ui.widgets.grid.cells.DoubleEditorCell;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.rowset.Row;
import com.eas.client.application.PlatypusImageResource;
import com.eas.client.converters.DoubleRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelSpin;
import com.eas.client.form.published.widgets.model.PublishedDecoratorBox;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class DoubleModelGridColumn extends ModelGridColumn<Double> {

	public DoubleModelGridColumn(String aName) {
		super(new TreeExpandableCell<Row, Double>(new DoubleEditorCell()), aName, null, null, new DoubleRowValueConverter());
		setEditor(new ModelSpin());
		((DoubleEditorCell) getTargetCell()).setRenderer(new CellRenderer<Double>() {
			@Override
			public boolean render(Context context, Double value, SafeHtmlBuilder sb) {
				DoubleModelGridColumn column = DoubleModelGridColumn.this;
				JavaScriptObject onRender = column.getOnRender() != null ? column.getOnRender() : column.getGrid().getOnRender();
				if (onRender != null) {
					try {
						PublishedStyle styleToRender = null;
						SafeHtmlBuilder lsb = new SafeHtmlBuilder();
						String toRender = value != null ? NumberFormat.getDecimalFormat().format(value) : null;
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
							DoubleModelGridColumn.this.bindDisplayCallback(decorId, cellToRender);		
							if(cellToRender.getStyle() != null && cellToRender.getStyle().getIcon() instanceof PlatypusImageResource){
								PlatypusImageResource pImage = (PlatypusImageResource)cellToRender.getStyle().getIcon();
								DoubleModelGridColumn.this.bindIconCallback(decorId, pImage);
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
	public void setEditor(PublishedDecoratorBox<Double> aEditor) {
		super.setEditor(aEditor);
		((DoubleEditorCell) getTargetCell()).setEditor(aEditor);
	}
	
	public Double getMin(){
		return ((ModelSpin)editor).getMin();
	}
	
	public void setMin(Double aValue){
		((ModelSpin)editor).setMin(aValue);
	}
	
	public Double getMax(){
		return ((ModelSpin)editor).getMax();
	}
	
	public void setMax(Double aValue){
		((ModelSpin)editor).setMax(aValue);
	}
	
	public Double getStep(){
		return ((ModelSpin)editor).getStep();
	}
	
	public void setStep(Double aValue){
		((ModelSpin)editor).setStep(aValue);
	}
	
	public String getEmptyText(){
		return ((ModelSpin)getEditor()).getEmptyText();
	}
	
	public void setEmptyText(String aValue) {
		((ModelSpin)getEditor()).setEmptyText(aValue);
    }
}
