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

/**
 *
 * @author vy
 */
public class SqlsTableModel extends AbstractTableModel {

    private List<String> sqls;
    private boolean[] choices;
    private String[] results;

    public SqlsTableModel(List<String> aSqls) {
        setSqls(aSqls);
    }

    @Override
    public int getRowCount() {
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
                return choices[ rowIndex];
            case 1:
                return sqls.get(rowIndex);
            case 2:
                return results[rowIndex];
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            choices[row] = ((boolean) value);
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
        switch (column) {
            case 0:
                return "Run";
            case 1:
                return "Script";
            case 2:
                return "Result";
        }
        return "";
    }

    public List<String> getSqls() {
        return sqls;
    }

    private void setSqls(List<String> aSqls) {
        sqls = aSqls;
        if (aSqls != null) {
            int size = sqls.size();
            choices = new boolean[size];
            Arrays.fill(choices, true);
            results = new String[size];
        } else {
            choices = null;
            results = null;
        }
    }

    public boolean[] getChoices() {
        return choices;
    }

    public String[] getResults() {
        return results;
    }
}
