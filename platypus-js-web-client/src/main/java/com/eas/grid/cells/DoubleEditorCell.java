/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.cells;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class DoubleEditorCell extends NumberEditorCell<Double> {

	public DoubleEditorCell() {
		super(new DoubleBox());
	}

	public DoubleEditorCell(Widget aEditor) {
		super(aEditor);
	}

	public DoubleEditorCell(Widget aEditor, NumberFormat aFormat) {
		super(aEditor, aFormat);
	}

}
