/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.rowset.Rowset;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.dbcontrols.grid.rt.columns.model.FieldModelColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsTableModel;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.text.Collator;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class RowsetDistributedTableTest extends GridBaseTest {

    private void editSomeData(JTable tbl, Rowset rowsRowset, int size, RowsetsTableModel model, TableColumnModel columnModel) throws Exception {
        rowsRowset.absolute(rowsRowset.size()); // equivalent to rowset.last();
        rowsRowset.delete();
        assertEquals(size - 1, rowsRowset.size());
        assertEquals(rowsRowset.size(), model.getRowCount());
        assertTrue(rowsRowset.first());
        rowsRowset.delete();
        assertEquals(size - 2, rowsRowset.size());
        assertEquals(rowsRowset.size(), model.getRowCount());
        int from = Double.valueOf(Math.random() * (columnModel.getColumnCount() - 2) + 1).intValue();
        int to = Double.valueOf(Math.random() * (columnModel.getColumnCount() - 2) + 1).intValue();
        columnModel.moveColumn(from, to);
        rowsRowset.absolute(4);
        rowsRowset.deleteAll();
        assertEquals(0, rowsRowset.size());
        assertEquals(rowsRowset.size(), model.getRowCount());
        fillInRowset(rowsRowset);
        assertEquals(size, rowsRowset.size());
        assertEquals(rowsRowset.size(), model.getRowCount());
        verifyTableData(model);
    }

    protected class CellDataComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            o1 = ((CellData) o1).getData();
            o2 = ((CellData) o2).getData();
            if (o1 instanceof String && o2 instanceof String) {
                return Collator.getInstance().compare((String) o1, (String) o2);
            } else {
                return 0;
            }
        }
    }

    @Test
    public void rowsCrudTest() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    ApplicationDbModel model = new ApplicationDbModel(new DummyTestDbClient());
                    assertNotNull(model);
                    Rowset rowsRowset = initRowset();
                    ApplicationDbEntity rowsEntity = new ApplicationDbEntity(model) {

                        @Override
                        protected void refreshRowset() throws Exception {
                            // no op here because setRowset() is called
                        }

                    };
                    rowsEntity.setQuery(new DummyTestSqlQuery());
                    rowsEntity.setRowset(rowsRowset);
                    model.addEntity(rowsEntity);
                    TableColumnModel columnModel = new DefaultTableColumnModel();
                    RowsetsTableModel tableViewModel = new RowsetsTableModel(null, rowsRowset, null);
                    TableRowSorter sorter = new TableRowSorter(tableViewModel);
                    for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
                        Rowset colRowset = initRowset();
                        ApplicationDbEntity colEntity = new ApplicationDbEntity(model) {

                            @Override
                            protected void refreshRowset() throws Exception {
                                // no op here because setRowset() is called
                            }

                        };
                        model.addEntity(colEntity);
                        colEntity.setQuery(new DummyTestSqlQuery());
                        colEntity.setRowset(colRowset);
                        Relation colRelation = new Relation(rowsEntity, rowsRowset.getFields().get(1), colEntity, colRowset.getFields().get(1));
                        model.addRelation(colRelation);

                        FieldModelColumn mCol = new FieldModelColumn(colRowset, i, null, null, false, null, null, null);
                        tableViewModel.addColumn(mCol);
                        TableColumn vCol = new TableColumn(i - 1, 70);
                        vCol.setHeaderValue(colRowset.getFields().get(i).getDescription());
                        vCol.setIdentifier(mCol);
                        vCol.setCellRenderer(new CellDataRenderer());
                        columnModel.addColumn(vCol);
                    }
                    for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
                        sorter.setComparator(i - 1, new CellDataComparator());
                    }

                    model.requery();
                    JTable tbl = new JTable(new CachingTableModel(tableViewModel), columnModel);
                    tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    tbl.setRowSorter(sorter);
                    JFrame frame = new JFrame();
                    frame.getContentPane().setLayout(new BorderLayout());
                    frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
                    frame.setSize(600, 600);
                    frame.setVisible(true);
                    int size = rowsRowset.size();
                    assertEquals(tableViewModel.getColumnCount(), rowsRowset.getFields().getFieldsCount());
                    verifyTableData(tableViewModel);
                    assertEquals(rowsRowset.size(), tableViewModel.getRowCount());
                    for (int i = 0; i < 10; i++) {
                        editSomeData(tbl, rowsRowset, size, tableViewModel, columnModel);
                    }
                    frame.setVisible(false);
                } catch (Exception ex) {
                    Logger.getLogger(RowsetDistributedTableTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail(ex.getMessage());
                }
            }

        });
    }

    @Test
    public void editAllDataThroughTableModelTest() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    ApplicationDbModel model = new ApplicationDbModel(new DummyTestDbClient());
                    assertNotNull(model);
                    Rowset rowsRowset = initRowset();
                    ApplicationDbEntity rowsEntity = new ApplicationDbEntity(model) {

                        @Override
                        protected void refreshRowset() throws Exception {
                            // no op here because setRowset() is called
                        }

                    };
                    rowsEntity.setQuery(new DummyTestSqlQuery());
                    rowsEntity.setRowset(rowsRowset);
                    model.addEntity(rowsEntity);
                    TableColumnModel columnModel = new DefaultTableColumnModel();
                    RowsetsTableModel tableViewModel = new RowsetsTableModel(null, rowsRowset, null);
                    TableRowSorter sorter = new TableRowSorter(tableViewModel);
                    for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
                        Rowset colRowset = initRowset();
                        ApplicationDbEntity colEntity = new ApplicationDbEntity(model) {

                            @Override
                            protected void refreshRowset() throws Exception {
                                // no op here because setRowset() is called
                            }

                        };
                        model.addEntity(colEntity);
                        colEntity.setQuery(new DummyTestSqlQuery());
                        colEntity.setRowset(colRowset);
                        Relation<ApplicationDbEntity> colRelation = new Relation<>(rowsEntity, rowsRowset.getFields().get(1), colEntity, colRowset.getFields().get(1));
                        model.addRelation(colRelation);

                        FieldModelColumn mCol = new FieldModelColumn(colRowset, i, null, null, false, null, null, null);
                        tableViewModel.addColumn(mCol);
                        TableColumn vCol = new TableColumn(i - 1, 70);
                        vCol.setHeaderValue(colRowset.getFields().get(i).getDescription());
                        vCol.setIdentifier(mCol);
                        vCol.setCellRenderer(new CellDataRenderer());
                        columnModel.addColumn(vCol);
                    }
                    for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
                        sorter.setComparator(i - 1, new CellDataComparator());
                    }

                    model.requery();
                    JTable tbl = new JTable(new CachingTableModel(tableViewModel), columnModel);
                    tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    tbl.setRowSorter(sorter);
                    JFrame frame = new JFrame();
                    frame.getContentPane().setLayout(new BorderLayout());
                    frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
                    frame.setSize(600, 600);
                    frame.setVisible(true);
                    int size = rowsRowset.size();
                    assertEquals(tableViewModel.getColumnCount(), rowsRowset.getFields().getFieldsCount());
                    verifyTableData(tableViewModel);
                    assertEquals(rowsRowset.size(), tableViewModel.getRowCount());
                    Object value = null;
                    setValueToAllCells(tableViewModel, value);
                    verifyValuesInAllCells(tableViewModel, value);
                    frame.setVisible(false);
                } catch (Exception ex) {
                    Logger.getLogger(RowsetDistributedTableTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail(ex.getMessage());
                }
            }
        });
    }
}
