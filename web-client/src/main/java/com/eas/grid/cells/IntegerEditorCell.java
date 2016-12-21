/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.cells;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author mg
 */
public class IntegerEditorCell extends NumberEditorCell<Integer> {

    public IntegerEditorCell() {
        super(new IntegerBox());
    }

    public IntegerEditorCell(Widget aEditor) {
        super(aEditor);
    }

	public IntegerEditorCell(Widget aEditor, NumberFormat aFormat) {
		super(aEditor, aFormat);
	}

}
