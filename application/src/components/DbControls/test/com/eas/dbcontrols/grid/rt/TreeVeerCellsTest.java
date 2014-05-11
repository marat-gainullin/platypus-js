/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.gui.grid.columns.TableColumnHandler;
import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.editing.InsettedTreeEditor;
import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.bearsoft.gui.grid.rendering.InsettedTreeRenderer;
import com.bearsoft.gui.grid.rendering.TreeColumnLeadingComponent;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.dbcontrols.grid.rt.columns.model.FieldModelColumn;
import com.eas.dbcontrols.grid.rt.columns.model.RowModelColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsTableModel;
import com.eas.dbcontrols.grid.rt.models.RowsetsTreedModel;
import com.eas.dbcontrols.grid.rt.veers.ColumnsRiddler;
import com.eas.dbcontrols.grid.rt.veers.ColumnsSource;
import com.eas.gui.CascadedStyle;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Gala
 */
public class TreeVeerCellsTest extends GridBaseTest {

    protected static final Fields fields1 = new Fields();

    static {
        fields1.add(new Field("s1id", "Идентификатор первой серии столбцов"));
        fields1.get("s1id").setPk(true);
        fields1.get("s1id").setTypeInfo(DataTypeInfo.INTEGER);
        fields1.add(new Field("s1Title", "Заголовок первой серии столбцов"));
        fields1.add(new Field("s1Data", "Данные (текст) первой серии столбцов"));
    }
    protected static final Fields fields2 = new Fields();

    static {
        fields2.add(new Field("s2id", "Идентификатор второй серии столбцов"));
        fields2.get("s2id").setPk(true);
        fields2.get("s2id").setTypeInfo(DataTypeInfo.INTEGER);
        fields2.add(new Field("s2Title", "Заголовок второй серии столбцов"));
        fields2.add(new Field("s2Data", "Данные (текст) второй серии столбцов"));
    }
    protected static final Fields cells1Fields = new Fields();

    static {
        cells1Fields.add(new Field("cellID", ""));
        cells1Fields.add(new Field("colKey", ""));
        cells1Fields.add(new Field("rowKey", ""));
        cells1Fields.add(new Field("cellData", ""));
    }
    protected static final Fields cells2Fields = new Fields();

    static {
        cells2Fields.add(new Field("cellID1", ""));
        cells2Fields.add(new Field("colKey1", ""));
        cells2Fields.add(new Field("rowKey1", ""));
        cells2Fields.add(new Field("cellData1", ""));
    }
    protected static Object[][] columnsSeries1TestData = new Object[][]{
        {1, "series1c1", "sample text1"},
        {2, "series1c2", "sample text2"},
        {3, "series1c3", "sample text3"}
    };
    protected static Object[][] columnsSeries2TestData = new Object[][]{
        {1, "series2c1", "sample text1"},
        {2, "series2c2", "sample text2"},
        {3, "series2c3", "sample text3"},
        {4, "series2c4", "sample text4"}
    };
    protected static Object[][] cells1TestData = new Object[][]{
        {1, 1, 1, "11"},
        {2, 2, 1, "21"},
        {3, 3, 1, "31"},
        {4, 1, 2, "12"},
        {5, 2, 2, "22"},
        {6, 3, 2, "32"},
        {7, 1, 3, "13"},
        {8, 2, 3, "23"},
        {9, 3, 3, "33"}
    };
    protected static Object[][] cells2TestData = new Object[][]{
        {1, 1, 1, "11"},
        {2, 2, 1, "21"},
        {3, 3, 1, "31"},
        {4, 4, 1, "41"},
        {5, 1, 2, "12"},
        {6, 2, 2, "22"},
        {7, 3, 2, "32"},
        {8, 4, 2, "42"},
        {9, 1, 3, "13"},
        {10, 2, 3, "23"},
        {11, 3, 3, "33"},
        {12, 4, 3, "43"}
    };

    private Locator createColumnsLocator(Rowset s1Rowset) throws IllegalStateException {
        Locator loc1 = s1Rowset.createLocator();
        loc1.beginConstrainting();
        try {
            loc1.addConstraint(1);
        } finally {
            loc1.endConstrainting();
        }
        return loc1;
    }

    protected class CellDataCellRendererFactory implements TableColumnHandler {

        @Override
        public void handle(TableColumn aColumn) {
            aColumn.setCellRenderer(new CellDataRenderer());
        }
    }

    private void verifyColumns(TableColumnModel aColumns, Rowset aRowset, int startFrom, int aTitleColumnIndex) throws Exception {
        int colsIndex = startFrom;
        for (int i = 1; i <= aRowset.size(); i++) {
            TableColumn vCol = aColumns.getColumn(colsIndex);
            aRowset.absolute(i);
            String expectedColumnTitle = aRowset.getString(aTitleColumnIndex);
            assertEquals(expectedColumnTitle, vCol.getHeaderValue());
            assertTrue(vCol.getIdentifier() instanceof RowModelColumn);
            RowModelColumn mCol = (RowModelColumn) vCol.getIdentifier();
            assertEquals(aTitleColumnIndex, mCol.getColTitleFieldIndex());
            assertSame(aRowset.getCurrentRow(), mCol.getRow());
            assertSame(aRowset, mCol.getRowset());
            colsIndex++;
        }
    }

    @Test
    public void rowsCrudCollapsedTest() throws Exception {
        TreeTestState state = new CollapsedTreeTestState();
        state.test();
    }

    @Test
    public void rowsCrudExpandedTest() throws Exception {
        TreeTestState state = new ExpandedTreeTestState();
        state.test();
    }

    protected abstract class TreeTestState {

        Rowset rowsRowset;
        RowsetsTreedModel model;
        TableFront2TreedModel<Row> front;
        Locator loc;
        JTable tbl;
        JFrame frame;

        public TreeTestState() throws Exception {
            rowsRowset = initRowset();
            model = new RowsetsTreedModel(null, rowsRowset, 9, null);
            front = new TableFront2TreedModel(model);
            TableColumnModel columns = new DefaultTableColumnModel();
            tbl = new JTable(new CachingTableModel(front), columns);
            CascadedStyle style = setupTreeStyle(tbl);

            for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
                FieldModelColumn mCol = new FieldModelColumn(rowsRowset, i, null, null, false, null, null, null);
                model.addColumn(mCol);
                TableColumn vCol = new TableColumn(i - 1, 70);
                vCol.setHeaderValue(rowsRowset.getFields().get(i).getDescription());
                vCol.setIdentifier(mCol);
                columns.addColumn(vCol);
                if (i > 1) {
                    vCol.setCellRenderer(new CellDataRenderer());
                } else {
                    vCol.setCellRenderer(new InsettedTreeRenderer(new CellDataRenderer(), new TreeColumnLeadingComponent(front, style, false)));
                    vCol.setCellEditor(new InsettedTreeEditor(tbl.getDefaultEditor(String.class), new TreeColumnLeadingComponent(front, style, true)));
                }
            }

            assertEquals(model.getColumnCount(), rowsRowset.getFields().getFieldsCount());

            Rowset s1Rowset = new Rowset(fields1);
            fillInRowset(s1Rowset, columnsSeries1TestData);
            Locator loc1 = createColumnsLocator(s1Rowset);

            Rowset s2Rowset = new Rowset(fields2);
            fillInRowset(s2Rowset, columnsSeries2TestData);
            Locator loc2 = createColumnsLocator(s2Rowset);

            Rowset cells1Rowset = new Rowset(cells1Fields);
            fillInRowset(cells1Rowset, cells1TestData);
            Locator cells1Loc = cells1Rowset.createLocator();
            cells1Loc.beginConstrainting();
            cells1Loc.addConstraint(3);//row key
            cells1Loc.addConstraint(2);//col key
            cells1Loc.endConstrainting();

            Rowset cells2Rowset = new Rowset(cells2Fields);
            fillInRowset(cells2Rowset, cells2TestData);
            Locator cells2Loc = cells2Rowset.createLocator();
            cells2Loc.beginConstrainting();
            cells2Loc.addConstraint(3);//row key
            cells2Loc.addConstraint(2);//col key
            cells2Loc.endConstrainting();

            int s1ToGlueToIndex = 1;
            int s2ToGlueToIndex = 3;
            // let's setup columns sources
            TableColumn s1ToGlue = columns.getColumn(s1ToGlueToIndex);
            GridColumnsGroup s1ToglueGroup = new GridColumnsGroup(s1ToGlue);
            TableColumn s2ToGlue = columns.getColumn(s2ToGlueToIndex);
            GridColumnsGroup s2ToglueGroup = new GridColumnsGroup(s2ToGlue);

            Map<Rowset, List<ColumnsSource>> columnsSources = new HashMap<>();

            columnsSources.put(s1Rowset, Collections.singletonList(new ColumnsSource(s1ToglueGroup, s1ToGlue, loc1, 2, cells1Loc, cells1Rowset, 4, new CellDataCellRendererFactory(), null, null)));
            columnsSources.put(s2Rowset, Collections.singletonList(new ColumnsSource(s2ToglueGroup, s2ToGlue, loc2, 2, cells2Loc, cells2Rowset, 4, new CellDataCellRendererFactory(), null, null)));

            ColumnsRiddler riddler = new ColumnsRiddler(null, columns, null, model, columnsSources, null, new ArrayList());
            riddler.fill();

            int commonColumnsCount = rowsRowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size();
            assertEquals(columns.getColumnCount(), model.getColumnCount());
            assertEquals(commonColumnsCount, columns.getColumnCount());

            tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            frame = new JFrame();
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
            frame.setSize(600, 600);
            frame.setVisible(true);

            // +1 is needed, because of 1, 3, 5 is column's indices of columns, columns of interest are glued to.
            // and columns of interest have indicies one greater than 1, 3, 5. 
            verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);
            verifyColumns(columns, s2Rowset, s2ToGlueToIndex + s1Rowset.size() + 1, 2);

            assertEquals(7, tbl.getRowCount());

            loc = rowsRowset.createLocator();
            loc.beginConstrainting();
            try {
                loc.addConstraint(1);
            } finally {
                loc.endConstrainting();
            }
        }

        protected abstract void deletesSection() throws RowsetException;

        protected abstract void insertsSection() throws RowsetException;

        public abstract void test() throws RowsetException;
    }

    protected class CollapsedTreeTestState extends TreeTestState {

        public CollapsedTreeTestState() throws Exception {
            super();
        }

        @Override
        protected void deletesSection() throws RowsetException {
            // Deleting section. Tests delete and parentless capability
            assertTrue(loc.find(new Object[]{11}));
            assertTrue(loc.first());
            rowsRowset.delete();
            assertEquals(6, tbl.getRowCount());
            assertTrue(loc.find(new Object[]{18}));
            assertTrue(loc.first());
            rowsRowset.delete();
            assertEquals(13, tbl.getRowCount());
            assertTrue(loc.find(new Object[]{4}));
            assertTrue(loc.first());
            rowsRowset.delete();
            assertEquals(16, tbl.getRowCount());
        }

        @Override
        protected void insertsSection() throws RowsetException {
            // Inserting section. Tests if parent field changing leads to structure changes.
            // The aim of the section is completly revert deleted rows
            int key2InsertAfter = 3;
            assertTrue(loc.find(new Object[]{key2InsertAfter}));
            assertTrue(loc.first());
            rowsRowset.insert();
            assertEquals(17, tbl.getRowCount());
            // let's make actual structure change
            rowsRowset.updateObject(1, testData[key2InsertAfter][1 - 1]);
            assertEquals(14, tbl.getRowCount());
            // we take into account, that we initialize only [1;8] fields
            // so i < - i legal.
            for (int i = 2; i < testData[key2InsertAfter].length; i++) {
                rowsRowset.updateObject(i, testData[key2InsertAfter][i - 1]);
            }
            // let's make actual structure change
            rowsRowset.updateObject(testData[key2InsertAfter].length, testData[key2InsertAfter][testData[key2InsertAfter].length - 1]);
            assertEquals(13, tbl.getRowCount());
            key2InsertAfter = 10;
            assertTrue(loc.find(new Object[]{key2InsertAfter}));
            assertTrue(loc.first());
            rowsRowset.insert();
            assertEquals(14, tbl.getRowCount());
            // let's make actual structure change
            rowsRowset.updateObject(1, testData[key2InsertAfter][1 - 1]);
            // we take into account, that we initialize only [2;8] fields
            // so i < - i legal.
            for (int i = 2; i < testData[key2InsertAfter].length; i++) {
                rowsRowset.updateObject(i, testData[key2InsertAfter][i - 1]);
            }
            // let's make actual structure change
            rowsRowset.updateObject(testData[key2InsertAfter].length, testData[key2InsertAfter][testData[key2InsertAfter].length - 1]);
            assertEquals(14, tbl.getRowCount());
            key2InsertAfter = 17;
            assertTrue(loc.find(new Object[]{key2InsertAfter}));
            assertTrue(loc.first());
            rowsRowset.insert();
            assertEquals(15, tbl.getRowCount());
            // let's make actual structure change
            rowsRowset.updateObject(1, testData[key2InsertAfter][1 - 1]);
            assertEquals(7, tbl.getRowCount());
            // we take into account, that we initialize only [1;8] fields
            // so i < - i legal.
            for (int i = 2; i < testData[key2InsertAfter].length; i++) {
                rowsRowset.updateObject(i, testData[key2InsertAfter][i - 1]);
            }
            // let's make actual structure change
            rowsRowset.updateObject(testData[key2InsertAfter].length, testData[key2InsertAfter][testData[key2InsertAfter].length - 1]);
            assertEquals(7, tbl.getRowCount());
            assertEquals(testData.length, rowsRowset.size());
        }

        @Override
        public void test() throws RowsetException {
            deletesSection();
            insertsSection();
            frame.setVisible(false);
        }
    }

    protected class ExpandedTreeTestState extends TreeTestState {

        public ExpandedTreeTestState() throws Exception {
            super();
            expand();
        }

        private void expand() throws RowsetException {
            assertTrue(loc.find(new Object[]{1}));
            front.expand(loc.getRow(0), false);
            assertTrue(loc.find(new Object[]{4}));
            front.expand(loc.getRow(0), false);
            assertTrue(loc.find(new Object[]{18}));
            front.expand(loc.getRow(0), false);
            assertTrue(loc.find(new Object[]{2}));
            front.expand(loc.getRow(0), false);
        }

        @Override
        public void deletesSection() throws RowsetException {
            // Deleting section. Tests delete and parentless capability
            assertEquals(22, tbl.getRowCount());
            assertTrue(loc.find(new Object[]{11}));
            assertTrue(loc.first());
            rowsRowset.delete();
            assertEquals(21, tbl.getRowCount());
            assertTrue(loc.find(new Object[]{18}));
            assertTrue(loc.first());
            rowsRowset.delete();
            assertEquals(20, tbl.getRowCount());
            assertTrue(loc.find(new Object[]{4}));
            assertTrue(loc.first());
            rowsRowset.delete();
            assertEquals(19, tbl.getRowCount());
        }

        @Override
        public void insertsSection() throws RowsetException {
            // Inserting section. Tests if parent field changing leads to structure changes.
            // The aim of the section is completly revert deleted rows
            int key2InsertAfter = 3;
            assertTrue(loc.find(new Object[]{key2InsertAfter}));
            assertTrue(loc.first());
            rowsRowset.insert();
            assertEquals(20, tbl.getRowCount());
            // let's make actual structure change
            rowsRowset.updateObject(1, testData[key2InsertAfter][1 - 1]);
            assertEquals(17, tbl.getRowCount());
            // we take into account, that we initialize only [1;8] fields
            // so i < - i legal.
            for (int i = 2; i < testData[key2InsertAfter].length; i++) {
                rowsRowset.updateObject(i, testData[key2InsertAfter][i - 1]);
            }
            // let's make actual structure change
            rowsRowset.updateObject(testData[key2InsertAfter].length, testData[key2InsertAfter][testData[key2InsertAfter].length - 1]);
            assertEquals(17, tbl.getRowCount());
            key2InsertAfter = 10;
            assertTrue(loc.find(new Object[]{key2InsertAfter}));
            assertTrue(loc.first());
            rowsRowset.insert();
            assertEquals(18, tbl.getRowCount());
            // let's make actual structure change
            rowsRowset.updateObject(1, testData[key2InsertAfter][1 - 1]);
            assertEquals(18, tbl.getRowCount());
            // we take into account, that we initialize only [2;8] fields
            // so i < - i legal.
            for (int i = 2; i < testData[key2InsertAfter].length; i++) {
                rowsRowset.updateObject(i, testData[key2InsertAfter][i - 1]);
            }
            assertEquals(18, tbl.getRowCount());
            // let's make actual structure change
            rowsRowset.updateObject(testData[key2InsertAfter].length, testData[key2InsertAfter][testData[key2InsertAfter].length - 1]);
            assertEquals(18, tbl.getRowCount());
            key2InsertAfter = 17;
            assertTrue(loc.find(new Object[]{key2InsertAfter}));
            assertTrue(loc.first());
            rowsRowset.insert();
            assertEquals(19, tbl.getRowCount());
            // let's make actual structure change
            rowsRowset.updateObject(1, testData[key2InsertAfter][1 - 1]);
            assertEquals(11, tbl.getRowCount());
            // we take into account, that we initialize only [1;8] fields
            // so i < - i legal.
            for (int i = 2; i < testData[key2InsertAfter].length; i++) {
                rowsRowset.updateObject(i, testData[key2InsertAfter][i - 1]);
            }
            assertEquals(11, tbl.getRowCount());
            // let's make actual structure change
            rowsRowset.updateObject(testData[key2InsertAfter].length, testData[key2InsertAfter][testData[key2InsertAfter].length - 1]);
            assertEquals(11, tbl.getRowCount());
            assertEquals(testData.length, rowsRowset.size());
        }

        @Override
        public void test() throws RowsetException {
            deletesSection();
            insertsSection();
            frame.setVisible(false);
        }
    }

    @Test
    public void rowsRowsetFilteredTest() throws Exception {
        // TODO: see and record situation with rows rowset filtering.
    }

    @Test
    public void cellsPlainTest() throws Exception {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Rowset rowset = initRowset();
                    TableColumnModel columns = new DefaultTableColumnModel();
                    RowsetsTreedModel data = new RowsetsTreedModel(null, rowset, 9, null);

                    for (int i = 1; i <= rowset.getFields().getFieldsCount(); i++) {
                        FieldModelColumn mCol = new FieldModelColumn(rowset, i, null, null, false, null, null, null);
                        data.addColumn(mCol);
                        TableColumn vCol = new TableColumn(i - 1, 70);
                        vCol.setHeaderValue(rowset.getFields().get(i).getDescription());
                        vCol.setIdentifier(mCol);
                        vCol.setCellRenderer(new CellDataRenderer());
                        columns.addColumn(vCol);
                    }

                    assertEquals(data.getColumnCount(), rowset.getFields().getFieldsCount());

                    Rowset s1Rowset = new Rowset(fields1);
                    fillInRowset(s1Rowset, columnsSeries1TestData);
                    Locator loc1 = createColumnsLocator(s1Rowset);

                    Rowset s2Rowset = new Rowset(fields2);
                    fillInRowset(s2Rowset, columnsSeries2TestData);
                    Locator loc2 = createColumnsLocator(s2Rowset);

                    Rowset cells1Rowset = new Rowset(cells1Fields);
                    fillInRowset(cells1Rowset, cells1TestData);
                    Locator cells1Loc = cells1Rowset.createLocator();
                    cells1Loc.beginConstrainting();
                    cells1Loc.addConstraint(3);//row key
                    cells1Loc.addConstraint(2);//col key
                    cells1Loc.endConstrainting();

                    Rowset cells2Rowset = new Rowset(cells2Fields);
                    fillInRowset(cells2Rowset, cells2TestData);
                    Locator cells2Loc = cells2Rowset.createLocator();
                    cells2Loc.beginConstrainting();
                    cells2Loc.addConstraint(3);//row key
                    cells2Loc.addConstraint(2);//col key
                    cells2Loc.endConstrainting();

                    int s1ToGlueToIndex = 1;
                    int s2ToGlueToIndex = 3;
                    // let's setup columns sources
                    Map<Rowset, List<ColumnsSource>> columnsSources = new HashMap<>();
                    columnsSources.put(s1Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s1ToGlueToIndex), loc1, 2, cells1Loc, cells1Rowset, 4, new CellDataCellRendererFactory(), null, null)));
                    columnsSources.put(s2Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s2ToGlueToIndex), loc2, 2, cells2Loc, cells2Rowset, 4, new CellDataCellRendererFactory(), null, null)));

                    ColumnsRiddler riddler = new ColumnsRiddler(null, columns, null, data, columnsSources, null, new ArrayList());
                    riddler.fill();

                    int commonColumnsCount = rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size();
                    assertEquals(columns.getColumnCount(), data.getColumnCount());
                    assertEquals(commonColumnsCount, columns.getColumnCount());

                    //JTable tbl = new JTable(new CachingTableModel(new TableFront2TreedModel(model)), columns);
                    JTable tbl = new JTable(new TableFront2TreedModel(data), columns);
                    tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    JFrame frame = new JFrame();
                    frame.getContentPane().setLayout(new BorderLayout());
                    frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
                    frame.setSize(600, 600);
                    frame.setVisible(true);

        // +1 is needed, because of 1, 3, 5 is column's indices of columns, columns of interest are glued to.
                    // and columns of interest have indicies one greater than 1, 3, 5. 
                    verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);
                    verifyColumns(columns, s2Rowset, s2ToGlueToIndex + s1Rowset.size() + 1, 2);

        // TODO: make some nodes expanded
                    //model to rowsets test section 1
                    TableModel model = tbl.getModel();
                    for (int c = 9; c <= 15; c++) {
                        for (int r = 0; r <= 2; r++) {
                            Object val = model.getValueAt(r, c);
                            assertTrue(val instanceof CellData);
                            val = ((CellData) val).getData();
                            assertTrue(val instanceof String);
                            String sVal = (String) val;
                            assertFalse(sVal.endsWith("_"));
                            model.setValueAt(sVal + "_", r, c);
                        }
                    }
                    // verify...1
                    cells1Rowset.beforeFirst();
                    while (cells1Rowset.next()) {
                        Object col = cells1Rowset.getObject(2);
                        Object row = cells1Rowset.getObject(3);

                        String expected = col.toString() + row.toString() + "_";
                        Object val = cells1Rowset.getObject(4);

                        assertEquals(expected, val);
                    }
                    // verify...2
                    cells2Rowset.beforeFirst();
                    while (cells2Rowset.next()) {
                        Object col = cells2Rowset.getObject(2);
                        Object row = cells2Rowset.getObject(3);

                        String expected = col.toString() + row.toString() + "_";
                        Object val = cells2Rowset.getObject(4);

                        assertEquals(expected, val);
                    }

                    // rowsets to model test section 1
                    cells1Rowset.beforeFirst();
                    while (cells1Rowset.next()) {
                        Object col = cells1Rowset.getObject(2);
                        Object row = cells1Rowset.getObject(3);

                        String val = col.toString() + row.toString();
                        cells1Rowset.updateObject(4, val);
                    }
                    // rowsets to model test section 2
                    cells2Rowset.beforeFirst();
                    while (cells2Rowset.next()) {
                        Object col = cells2Rowset.getObject(2);
                        Object row = cells2Rowset.getObject(3);

                        String val = col.toString() + row.toString();
                        cells2Rowset.updateObject(4, val);
                    }
                    // verify...
                    for (int c = 9; c <= 15; c++) {
                        for (int r = 0; r <= 2; r++) {
                            Object val = model.getValueAt(r, c);
                            assertTrue(val instanceof CellData);
                            val = ((CellData) val).getData();
                            assertTrue(val instanceof String);
                            String sVal = (String) val;
                            assertTrue(!sVal.endsWith("_"));
                        }
                    }
                    frame.setVisible(false);
                } catch (Exception ex) {
                    Logger.getLogger(TreeVeerCellsTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail(ex.getMessage());
                }
            }
        });
    }

    @Test
    public void cellsDistributedTest() throws Exception {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Rowset rowset = initRowset();
                    TableColumnModel columns = new DefaultTableColumnModel();
                    RowsetsTableModel data = new RowsetsTableModel(null, rowset, null);

                    for (int i = 1; i <= rowset.getFields().getFieldsCount(); i++) {
                        FieldModelColumn mCol = new FieldModelColumn(rowset, i, null, null, false, null, null, null);
                        data.addColumn(mCol);
                        TableColumn vCol = new TableColumn(i - 1, 70);
                        vCol.setHeaderValue(rowset.getFields().get(i).getDescription());
                        vCol.setIdentifier(mCol);
                        vCol.setCellRenderer(new CellDataRenderer());
                        columns.addColumn(vCol);
                    }

                    assertEquals(data.getColumnCount(), rowset.getFields().getFieldsCount());

                    assertEquals(rowset.size(), data.getRowCount());
                    verifyTableData(data);

                    // Let's remove some model to reduce test model of the cells
                    while (rowset.size() > 3) {
                        rowset.absolute(4);
                        rowset.delete();
                    }

                    Rowset s1Rowset = new Rowset(fields1);
                    fillInRowset(s1Rowset, columnsSeries1TestData);
                    Locator loc1 = createColumnsLocator(s1Rowset);

                    Rowset s2Rowset = new Rowset(fields2);
                    fillInRowset(s2Rowset, columnsSeries2TestData);
                    Locator loc2 = createColumnsLocator(s2Rowset);

                    Rowset cells1Rowset = new Rowset(cells1Fields);
                    fillInRowset(cells1Rowset, cells1TestData);
                    Locator cells1Loc = cells1Rowset.createLocator();
                    cells1Loc.beginConstrainting();
                    cells1Loc.addConstraint(3);//row key
                    cells1Loc.addConstraint(2);//col key
                    cells1Loc.endConstrainting();

                    Rowset cells2Rowset = new Rowset(cells2Fields);
                    fillInRowset(cells2Rowset, cells2TestData);
                    Locator cells2Loc = cells2Rowset.createLocator();
                    cells2Loc.beginConstrainting();
                    cells2Loc.addConstraint(3);//row key
                    cells2Loc.addConstraint(2);//col key
                    cells2Loc.endConstrainting();

                    Rowset cells2ValuesRowset = new Rowset(cells2Fields);
                    fillInRowset(cells2ValuesRowset, cells2TestData);

                    ApplicationDbModel model = new ApplicationDbModel(new DummyTestDbClient());
                    assertNotNull(model);
                    model.requery();
                    ApplicationDbEntity cells2Entity = model.newGenericEntity();
                    model.addEntity(cells2Entity);
                    cells2Entity.setQuery(new DummyTestSqlQuery());
                    cells2Entity.setRowset(cells2Rowset);
                    ApplicationDbEntity cells2ValuesEntity = model.newGenericEntity();
                    model.addEntity(cells2ValuesEntity);
                    cells2ValuesEntity.setQuery(new DummyTestSqlQuery());
                    cells2ValuesEntity.setRowset(cells2ValuesRowset);
                    Relation colRelation = new Relation(cells2Entity, cells2Rowset.getFields().get(1), cells2ValuesEntity, cells2ValuesRowset.getFields().get(1));
                    model.addRelation(colRelation);

                    int s1ToGlueToIndex = 1;
                    int s2ToGlueToIndex = 3;
                    // let's setup columns sources
                    Map<Rowset, List<ColumnsSource>> columnsSources = new HashMap<>();
                    columnsSources.put(s1Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s1ToGlueToIndex), loc1, 2, cells1Loc, cells1Rowset, 4, new CellDataCellRendererFactory(), null, null)));
                    columnsSources.put(s2Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s2ToGlueToIndex), loc2, 2, cells2Loc, cells2ValuesRowset, 4, new CellDataCellRendererFactory(), null, null)));

                    ColumnsRiddler riddler = new ColumnsRiddler(null, columns, null, data, columnsSources, null, new ArrayList());
                    riddler.fill();

                    int commonColumnsCount = rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size();
                    assertEquals(columns.getColumnCount(), data.getColumnCount());
                    assertEquals(commonColumnsCount, columns.getColumnCount());

                    JTable tbl = new JTable(new CachingTableModel(data), columns);
                    tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    JFrame frame = new JFrame();
                    frame.getContentPane().setLayout(new BorderLayout());
                    frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
                    frame.setSize(600, 600);
                    frame.setVisible(true);

                    // +1 is needed, because of 1, 3, 5 is column's indices of columns, columns of interest are glued to.
                    // and columns of interest have indicies one greater than 1, 3, 5. 
                    verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);
                    verifyColumns(columns, s2Rowset, s2ToGlueToIndex + s1Rowset.size() + 1, 2);

                    // TODO: make some nodes expanded
                    TableModel rtModel = tbl.getModel();
                    //model to rowsets test section 1
                    for (int c = 9; c <= 15; c++) {
                        for (int r = 0; r <= 2; r++) {
                            Object val = rtModel.getValueAt(r, c);
                            assertTrue(val instanceof CellData);
                            val = ((CellData) val).getData();
                            assertTrue(val instanceof String);
                            String sVal = (String) val;
                            assertFalse(sVal.endsWith("_"));
                            rtModel.setValueAt(sVal + "_", r, c);
                        }
                    }
                    // verify...1
                    cells1Rowset.beforeFirst();
                    while (cells1Rowset.next()) {
                        Object col = cells1Rowset.getObject(2);
                        Object row = cells1Rowset.getObject(3);

                        String expected = col.toString() + row.toString() + "_";
                        Object val = cells1Rowset.getObject(4);

                        assertEquals(expected, val);
                    }
                    // verify...2
                    cells2Rowset.beforeFirst();
                    while (cells2Rowset.next()) {
                        Object col = cells2Rowset.getObject(2);
                        Object row = cells2Rowset.getObject(3);

                        String expected = col.toString() + row.toString() + "_";
                        Object val = cells2ValuesRowset.getObject(4);

                        assertEquals(expected, val);
                    }
                    // rowsets to model test section 1
                    cells1Rowset.beforeFirst();
                    while (cells1Rowset.next()) {
                        Object col = cells1Rowset.getObject(2);
                        Object row = cells1Rowset.getObject(3);

                        String val = col.toString() + row.toString();
                        cells1Rowset.updateObject(4, val);
                    }
                    // rowsets to model test section 2
                    cells2Rowset.beforeFirst();
                    while (cells2Rowset.next()) {
                        Object col = cells2Rowset.getObject(2);
                        Object row = cells2Rowset.getObject(3);

                        String val = col.toString() + row.toString();
                        cells2ValuesRowset.updateObject(4, val);
                    }
                    // verify...
                    for (int c = 9; c <= 15; c++) {
                        for (int r = 0; r <= 2; r++) {
                            Object val = rtModel.getValueAt(r, c);
                            assertTrue(val instanceof CellData);
                            val = ((CellData) val).getData();
                            assertTrue(val instanceof String);
                            String sVal = (String) val;
                            assertFalse(sVal.endsWith("_"));
                        }
                    }
                    frame.setVisible(false);
                } catch (Exception ex) {
                    Logger.getLogger(TreeVeerCellsTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail(ex.getMessage());
                }
            }
        });
    }
}
