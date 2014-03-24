/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.containers.GridPanel;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author mg
 */
public class GridPane extends GridPanel implements HasPublished{

	protected JavaScriptObject published;
	
	public GridPane(int aRows, int aCols) {
		super(aRows, aCols);
	}
	
	public GridPane(int aRows, int aCols, int aVGap, int aHGap) {
		this(aRows, aCols);
		setHgap(aHGap);
		setVgap(aVGap);
	}
	
	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		published.add = function(toAdd, aRow, aCol){
			if(toAdd && toAdd.unwrap && aRow && aCol){
				aComponent.@com.eas.client.form.published.containers.GridPane::setWidget(IILcom/google/gwt/user/client/ui/Widget;)(aRow, aCol, toAdd.unwrap());
			}
		}
		published.remove = function(aChild) {
			if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {
				aComponent.@com.eas.client.form.published.containers.GridPane::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
			}
		};
		published.cell = function(aRow, aCol) {
			var widget;
			if (aCol != undefined && aCol != null) {
				widget = aComponent.@com.eas.client.form.published.containers.GridPane::getWidget(II)(aRow, aCol);
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			}else
				return null;
		};
	}-*/;
}
