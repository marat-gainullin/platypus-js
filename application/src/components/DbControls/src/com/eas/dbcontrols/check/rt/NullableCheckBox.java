/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.check.rt;

import com.eas.dbcontrols.IconCache;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Icon;
import javax.swing.JCheckBox;

/**
 *
 * @author mg
 */
public class NullableCheckBox extends JCheckBox {

    protected class SuperHandler implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            setValue(isSelected());
        }
    }
    protected static Icon nullIcon = IconCache.getIcon("16x16/nullCheck.gif");
    protected boolean settingValue;
    protected Boolean value;

    public NullableCheckBox() {
        super();
        setIcon(nullIcon);
        model.addItemListener(new SuperHandler());
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean aValue) {
        if (value != aValue) {
            settingValue = true;
            try {
                value = aValue;
                if (value == null) {
                    setIcon(nullIcon);
                    fireStateChanged();
                } else {
                    setIcon(null);
                    if (isSelected() != value.booleanValue()) {
                        setSelected(value);
                    } else {
                        fireStateChanged();
                    }
                }
            } finally {
                settingValue = false;
            }
        }
    }

    @Override
    protected void fireStateChanged() {
        if (settingValue) {
            super.fireStateChanged();
        }
    }
}
