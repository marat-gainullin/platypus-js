/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

public class SqlsTableModel extends AbstractTableModel {

    private List<String> sqls;
    private boolean[] choices;
    private String[] results;
    private String [] columnNames;

    public SqlsTableModel(String [] aColumnNames) {
        columnNames =aColumnNames;
    }

    @Override
    synchronized public int getRowCount() {
        if (sqls != null) {
            return sqls.size();
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return getChoice(rowIndex);
            case 1:
                return getSql(rowIndex);
            case 2:
                return getResult(rowIndex);
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            setChoice(row, (boolean) value);
        }
    }

    @Override
    public Class getColumnClass(int col) {
        if (col == 0) {
            try {
                return Class.forName("java.lang.Boolean");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SqlsTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public String getColumnName(int column) {
        if (columnNames != null && columnNames.length > column) {
            return columnNames[column];
        }
        return "";
    }

    public List<String> getSqls() {
        return sqls;
    }

    synchronized public void setSqls(List<String> aSqls) {
        sqls = aSqls;
        if (aSqls != null) {
            int size = sqls.size();
            choices = new boolean[size];
            results = new String[size];
            setAllChoices(true);
        } else {
            choices = null;
            results = null;
        }
        fireTableStructureChanged();
    }

    synchronized public boolean getChoice(int aIndex) {
        return (choices != null && choices.length > aIndex ? choices[aIndex] : false);
    }

    synchronized public String getSql(int aIndex) {
        return (sqls != null && !sqls.isEmpty() ? sqls.get(aIndex) : null);
    }
    
    synchronized public String getResult(int aIndex) {
        return (results != null && results.length > aIndex ? results[aIndex] : null);
    }
    
    synchronized public void setChoice(int aIndex, boolean aValue) {
        if (choices != null && choices.length > aIndex) {
            choices[aIndex] = aValue;
        }
    }

    synchronized public void setSql(int aIndex, String aValue) {
        if (sqls != null && sqls.size() > aIndex) {
            sqls.add(aIndex, aValue);
        }
    }
    
    synchronized public void setResult(int aIndex, String aValue) {
        if (results != null && results.length > aIndex) {
            results[aIndex] = aValue;
        }
    }
    
    synchronized public void setAllChoices(boolean aValue) {
        if (choices != null) {
            Arrays.fill(choices, aValue);
        }
    }
    
    synchronized public void setAllResults(String aValue) {
        if (results != null) {
            Arrays.fill(results, aValue);
        }
    }
}    
