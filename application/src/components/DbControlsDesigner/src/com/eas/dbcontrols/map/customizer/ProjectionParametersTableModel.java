/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import com.eas.dbcontrols.map.customizer.edits.ModifyParameterValueEdit;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.UndoableEditSupport;
import org.geotools.parameter.Parameter;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author pk
 */
public class ProjectionParametersTableModel extends AbstractTableModel
{
    public static final int COLUMN_COUNT = 2;
    public static final int PARAMETER_NAME_COLUMN = 0;
    public static final int PARAMETER_VALUE_COLUMN = 1;
    private ParameterValueGroup parameters;
    private final UndoableEditSupport urSupport;

    public ProjectionParametersTableModel(UndoableEditSupport aUrSupport)
    {
        super();
        urSupport = aUrSupport;
    }

    @Override
    public int getRowCount()
    {
        return parameters == null ? 0 : parameters.values().size();
    }

    @Override
    public int getColumnCount()
    {
        return COLUMN_COUNT;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (parameters == null)
            return null;
        checkRanges(rowIndex, columnIndex);
        switch (columnIndex)
        {
            case PARAMETER_NAME_COLUMN:
                return parameters.values().get(rowIndex).getDescriptor().getName();
            case PARAMETER_VALUE_COLUMN:
                return parameters.values().get(rowIndex);
            default:
                assert false : "Forgot a column";
                return null;
        }
    }

    private void checkRanges(int rowIndex, int columnIndex) throws IllegalArgumentException
    {
        checkRowRange(rowIndex);
        checkColumnRange(columnIndex);
    }

    private void checkRowRange(int rowIndex) throws IllegalArgumentException
    {
        if (rowIndex < 0 || rowIndex >= parameters.values().size())
            throw new IllegalArgumentException("rowIndex not in [0; " + parameters.values().size() + ")");
    }

    private void checkColumnRange(int columnIndex) throws IllegalArgumentException
    {
        if (columnIndex < 0 || columnIndex >= COLUMN_COUNT)
            throw new IllegalArgumentException("columnIndex not in [0; " + COLUMN_COUNT + ")");
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        if (parameters == null)
            return false;
        checkRanges(rowIndex, columnIndex);
        switch (columnIndex)
        {
            case PARAMETER_NAME_COLUMN:
                return false;
            case PARAMETER_VALUE_COLUMN:
                return true;
            default:
                assert false : "Forgot a column";
                return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        if (parameters == null)
            return;
        checkRanges(rowIndex, columnIndex);
        if (!isCellEditable(rowIndex, columnIndex))
            throw new IllegalArgumentException(String.format("Cell (%d, %d) is not editable", rowIndex, columnIndex));
        final Parameter p = (Parameter) parameters.values().get(rowIndex);
        final ModifyParameterValueEdit edit = new ModifyParameterValueEdit(this, p, p.getValue(), aValue);
        p.setValue(aValue);
        urSupport.postEdit(edit);
    }

    @Override
    public String getColumnName(int column)
    {
        checkColumnRange(column);
        switch (column)
        {
            case PARAMETER_NAME_COLUMN:
                return "Parameter";
            case PARAMETER_VALUE_COLUMN:
                return "Value";
            default:
                assert false : "Forgot a column";
                return super.getColumnName(column);
        }
    }

    public ParameterValueGroup getParameters()
    {
        return parameters;
    }

    public void setParameters(ParameterValueGroup parameters)
    {
        if (parameters != this.parameters)
        {
            this.parameters = parameters;
            fireTableDataChanged();
        }
    }

    public void parameterChanged(Parameter param)
    {
        for (int i = 0; i < parameters.values().size(); i++)
        {
            if (param == parameters.values().get(i))
                fireTableCellUpdated(i, PARAMETER_VALUE_COLUMN);
        }
    }
}