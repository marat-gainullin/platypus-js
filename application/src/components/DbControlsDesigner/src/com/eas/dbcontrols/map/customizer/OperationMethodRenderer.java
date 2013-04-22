/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.opengis.referencing.operation.OperationMethod;

/**
 *
 * @author pk
 */
public class OperationMethodRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof OperationMethod)
        {
            final OperationMethod method = (OperationMethod) value;
            setText(method.getName().getCode());
        }
        return c;
    }
}
