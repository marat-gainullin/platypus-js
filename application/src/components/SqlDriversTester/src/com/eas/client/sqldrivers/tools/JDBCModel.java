/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.tools;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author vy
 */
public class JDBCModel extends AbstractTableModel
{
    private List<Object[]> dataJDBC = new ArrayList();
    private int cntFields = 0;
    private Object [] columnLabel;
    private Object [] columnName;
    private Object [] columnType;
    private Object [] columnTypeName;
    private Object [] columnClassName;
    private Object [] scale;
    private Object [] precision;
    private Object [] displaySize;
    
    
    
    @Override
    public String getColumnName(int col) 
    {
        return "<html>"+(col==0?"":col+"-")
                +(col==0?"label":columnLabel[col-1])+"<p>"
                +(col==0?"name":columnName[col-1])+"<p>"
                +(col==0?"type":columnType[col-1]+","+columnTypeName[col-1])+"<p>"
                +(col==0?"className":columnClassName[col-1])+"<p>"
//                +(col==0?"type":columnType[col-1])+"<p>"
//                +(col==0?"typeName":columnTypeName[col-1])+"<p>"
                +(col==0?"scale":scale[col-1])+"<p>"
                +(col==0?"precision":precision[col-1])+"<p>"
                +(col==0?"displaySize":displaySize[col-1])
                +"</html>";
    }    
    public JDBCModel(ResultSet rs) throws SQLException
    {
        
        if (rs != null)
        {
            ResultSetMetaData metaData = rs.getMetaData();
            cntFields = metaData.getColumnCount();
            columnLabel = new Object[cntFields];
            columnName = new Object[cntFields];
            columnType = new Object[cntFields];
            columnTypeName = new Object[cntFields];
            columnClassName = new Object[cntFields];
            scale = new Object[cntFields];
            precision = new Object[cntFields];
            displaySize = new Object[cntFields];
            
            for (int i = 0;i<cntFields;i++)
            {
                
                columnLabel[i] = metaData.getColumnLabel(i+1);
                columnName[i] = metaData.getColumnName(i+1);
                columnType[i] = metaData.getColumnType(i+1);
                columnTypeName[i] = metaData.getColumnTypeName(i+1);
                columnClassName[i] = metaData.getColumnClassName(i+1);
                scale[i] = metaData.getScale(i+1);
                precision[i] = metaData.getPrecision(i+1);
                displaySize[i] = metaData.getColumnDisplaySize(i+1);
            }    
            
            while(rs.next())
            {
                Object [] row = new Object[cntFields];
                for (int i = 0; i < cntFields;i++)  row[i] = rs.getString(i+1);
                dataJDBC.add(row);
            }    
        }    
    }        
    

    @Override
    public int getRowCount() 
    {
        return dataJDBC.size();
    }

    @Override
    public int getColumnCount() 
    {
        return (dataJDBC.isEmpty()?1:dataJDBC.get(0).length+1);
    
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) 
    {
        if (columnIndex == 0) return rowIndex+1;
        Object[] row = dataJDBC.get(rowIndex);
        if (row != null && row.length>=columnIndex) return row[columnIndex-1];
        return null;
    }
    @Override
    public boolean isCellEditable(int row, int col) {return true;}
    
    
}
