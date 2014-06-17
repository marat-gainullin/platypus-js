/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.spin.rt;

import java.text.ParseException;
import javax.swing.JComponent;
import javax.swing.JSpinner;

/**
 *
 * @author mg
 */
public class NullableSpinner extends JSpinner {

    public NullableSpinner(NullableSpinnerNumberModel aModel) {
        super();
        Object oldValue = aModel.getValue();
        aModel.setValue(0.0d);
        setModel(aModel);
        aModel.setValue(oldValue);
    }

    @Override
    public void commitEdit() throws ParseException {
        if (getModel().getValue() != null) {
            super.commitEdit();
        }
    }

    @Override
    public void setOpaque(boolean aValue) {
        super.setOpaque(aValue);
        JComponent editor = getEditor();
        editor.setOpaque(aValue);
        if (editor instanceof NumberEditor) {
            ((NumberEditor) editor).getTextField().setOpaque(aValue);
        }
    }

}
