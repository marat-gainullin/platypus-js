/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid;

import com.bearsoft.gui.grid.insets.LinearInset;
import com.bearsoft.gui.grid.constraints.LinearConstraint;
import java.util.Arrays;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Gala
 */
public class BaseTableTest {

    protected final static long millis = System.currentTimeMillis();
    protected final static long delta = 60000;
    protected final static Object[] columnNames = {"id", "consumed", "when", "desc", "age", "reviewed"};
    protected static Object[][] sampleData = {
        {1l, true, new Date(millis + 10 * delta), "sample1", 67, false},
        {2l, true, new Date(millis + 20 * delta), "sample2", 68, false},
        {3l, true, new Date(millis + 30 * delta), "sample3", 69, false},
        {4l, false, new Date(millis + 40 * delta), "sample4", 70, false},
        {5l, false, new Date(millis + 50 * delta), "sample5", 71, false},
        {6l, true, new Date(millis + 60 * delta), "sample6", 72, true},
        {7l, false, new Date(millis + 70 * delta), "sample7", 73, false}
    };
    protected static Object[][] sampleDataSaveStorage = new Object[sampleData.length][sampleData[0].length];

    protected class SampleTableModel extends AbstractTableModel {

        public SampleTableModel() {
            super();
        }

        @Override
        public String getColumnName(int column) {
            if (column >= 0 && column < columnNames.length) {
                return columnNames[column].toString();
            } else {
                return "";
            }
        }

        public int getRowCount() {
            return sampleData.length;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            return sampleData[row][col];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return true;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            sampleData[row][col] = value;
            fireTableCellUpdated(row, col);
        }

        public void deleteRow(int row) {
            Object[][] head = Arrays.copyOfRange(sampleData, 0, row);
            Object[][] tail = Arrays.copyOfRange(sampleData, row+1, sampleData.length);
            sampleData = new Object[sampleData.length-1][columnNames.length];
            int j=0;
            for(int i=0;i<head.length;i++)
            {
                sampleData[j++] = head[i];
            }
            for(int i=0;i<tail.length;i++)
            {
                sampleData[j++] = tail[i];
            }
            fireTableRowsDeleted(row, row);
        }

        public void insertRow(int row) {
            Object[][] head = Arrays.copyOfRange(sampleData, 0, row);
            Object[][] tail = Arrays.copyOfRange(sampleData, row, sampleData.length);
            sampleData = new Object[sampleData.length+1][columnNames.length];
            int j=0;
            for(int i=0;i<head.length;i++)
            {
                sampleData[j++] = head[i];
            }
            sampleData[j++] = new Object[columnNames.length];
            for(int i=0;i<tail.length;i++)
            {
                sampleData[j++] = tail[i];
            }
            fireTableRowsInserted(row, row);
        }
    }

    protected class SampleColumnModel extends DefaultTableColumnModel {

        public SampleColumnModel(LinearConstraint aConstraint) {
            super();
            for (int i = aConstraint.getMin(); i <= Math.min(columnNames.length - 1, aConstraint.getMax()); i++) {
                TableColumn tc = new TableColumn(i - aConstraint.getMin(), 60);
                tc.setHeaderValue(columnNames[i]);
                addColumn(tc);
            }
        }

        public SampleColumnModel(LinearInset aInset) {
            super();
            for (int i = 0; i < aInset.getPreFirst(); i++) {
                TableColumn tc = new TableColumn(i, 60);
                tc.setHeaderValue(String.valueOf(i - aInset.getPreFirst()));
                addColumn(tc);
            }
            for (int i = 0; i < columnNames.length; i++) {
                TableColumn tc = new TableColumn(i + aInset.getPreFirst(), 60);
                tc.setHeaderValue(columnNames[i]);
                addColumn(tc);
            }
            for (int i = 0; i < aInset.getAfterLast(); i++) {
                TableColumn tc = new TableColumn(i + aInset.getPreFirst() + columnNames.length, 60);
                tc.setHeaderValue("+" + String.valueOf(i + 1));
                addColumn(tc);
            }
        }
    }

    @Test
    public void dummyTest() {
    }

    @Before
    public void copySampleData2SaveStorage() {
        sampleDataSaveStorage = Arrays.copyOf(sampleData, sampleData.length);
    }

    protected void restoreSampleData() {
        sampleData = Arrays.copyOf(sampleDataSaveStorage, sampleDataSaveStorage.length);
    }
}
