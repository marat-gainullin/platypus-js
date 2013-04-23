/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.tools;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author vy
 */
public class SqlFieldModel extends AbstractTableModel
{
    private List<Object[]> data = new ArrayList();
    private int cntFields = 0;
    

    public SqlFieldModel(ResultSetMetaData metaData) throws SQLException 
    {
        if (metaData != null)
        {
            cntFields = metaData.getColumnCount();

            for (int i = 0;i<cntFields;i++)
            {
                data.add(new Object[]
                {
                    metaData.getColumnLabel(i+1),
                    metaData.getColumnName(i+1),
                    metaData.getColumnType(i+1),
                    metaData.getColumnTypeName(i+1),
                    metaData.getColumnClassName(i+1),
                    metaData.getPrecision(i+1),
                    metaData.getScale(i+1),
                    metaData.getColumnDisplaySize(i+1)
                });   
            }   
        }    
        
    }

    @Override
    public String getColumnName(int col) 
    {
        switch (col)
        {
            case 1 : return "columnLabel";
            case 2 : return "columnName";
            case 3 : return "columnType";
            case 4 : return "columnTypeName";
            case 5 : return "columnClassName";
            case 6 : return "precision";
            case 7 : return "scale";
            case 8 : return "columnDisplaySize";
        }    
        return "";
    }    
    
    @Override
    public int getRowCount() 
    {
        return data.size();
    }


    @Override
    public int getColumnCount() {
        return 9;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0)
        {
            return rowIndex+1;
        }    
        else
        {
            Object[] row = data.get(rowIndex);
            return row[columnIndex-1];
        }    
    }
    @Override
    public boolean isCellEditable(int row, int col) {return true;}
    
    
}
