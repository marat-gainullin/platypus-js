/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import javax.swing.table.DefaultTableCellRenderer;
import org.geotools.parameter.Parameter;

/**
 *
 * @author pk
 */
public class ParameterCellRenderer extends DefaultTableCellRenderer
{
    @Override
    protected void setValue(Object value)
    {
        if (value instanceof Parameter)
        {
            final Parameter param = (Parameter) value;
            if (param.getValue() == null)
                super.setValue("");
            else
                super.setValue(String.format("%s %s", param.getValue().toString(), param.getDescriptor().getUnit().toString()));
        }
        else
            super.setValue(value);
    }
}
