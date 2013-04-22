/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import org.geotools.parameter.Parameter;
import org.opengis.parameter.InvalidParameterValueException;

/**
 *
 * @author pk
 */
public class ParameterCellEditor extends AbstractCellEditor implements ActionListener, TableCellEditor
{
    private final JTextField editor = new JTextField();
    private Object currentValue;
    private Parameter currentParameter;

    public ParameterCellEditor()
    {
        editor.addActionListener(this);
    }

    @Override
    public boolean isCellEditable(EventObject e)
    {
        return super.isCellEditable(e);
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent)
    {
        return super.shouldSelectCell(anEvent);
    }

    @Override
    public boolean stopCellEditing()
    {
        final String text = editor.getText();
        if (text == null || text.isEmpty())
        {
            try
            {
                if (currentParameter != null)
                    Parameter.ensureValidValue(currentParameter.getDescriptor(), null);
                currentValue = null;
                fireEditingStopped();
                return true;
            } catch (InvalidParameterValueException ex)
            {
                return false;
            }
        }
        else
        {
            try
            {
                final Object castedValue = castStringToRequiredType(text, currentParameter);
                try
                {
                    if (currentParameter != null)
                        Parameter.ensureValidValue(currentParameter.getDescriptor(), castedValue);
                    currentValue = castedValue;
                    fireEditingStopped();
                    return true;
                } catch (InvalidParameterValueException ex)
                {
                    JOptionPane.showMessageDialog(editor, ex.getMessage(), "Invalid value", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(editor, String.format("Not a number: %s" + text), "Invalid value", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    public Object getCellEditorValue()
    {
        return currentValue;
    }

    private Object castStringToRequiredType(String string, Parameter parameter) throws NumberFormatException
    {
        if (parameter == null)
            return string;
        final Class requiredClass = parameter.getDescriptor().getValueClass();
        if (Double.class.equals(requiredClass))
        {
            return Double.parseDouble(string);
        }
        else if (Float.class.equals(requiredClass))
        {
            return Float.parseFloat(string);
        }
        else if (Long.class.equals(requiredClass))
        {
            return Long.parseLong(string);
        }
        else if (Integer.class.equals(requiredClass))
        {
            return Integer.parseInt(string);
        }
        else if (Short.class.equals(requiredClass))
        {
            return Short.parseShort(string);
        }
        else if (Byte.class.equals(requiredClass))
        {
            return Byte.parseByte(string);
        }
        else if (Boolean.class.equals(requiredClass))
        {
            return Boolean.parseBoolean(string);
        }
        else
            // Let's hope for the best.
            return string;
    }

    public void actionPerformed(ActionEvent e)
    {
        stopCellEditing();
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        if (value == null)
        {
            editor.setText("");
            currentParameter = null;
            currentValue = null;
        }
        else if (value instanceof Parameter)
        {
            final Parameter param = (Parameter) value;
            if (param.getValue() != null)
                editor.setText(param.getValue().toString());
            else
                editor.setText("");
            currentParameter = param;
            currentValue = param.getValue();
        }
        else
        {
            editor.setText(value.toString());
            currentParameter = null;
            currentValue = value;
        }
        return editor;
    }
}
