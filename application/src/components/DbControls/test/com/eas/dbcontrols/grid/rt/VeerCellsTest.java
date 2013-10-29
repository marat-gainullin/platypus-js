/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.gui.grid.columns.TableColumnHandler;
import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.rowset.Rowset;
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
import com.eas.dbcontrols.grid.rt.veers.ColumnsRiddler;
import com.eas.dbcontrols.grid.rt.veers.ColumnsSource;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class VeerCellsTest extends GridBaseTest {

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
    public void cellsPlainTest() throws Exception {
        Rowset rowset = initRowset();
        TableColumnModel columns = new DefaultTableColumnModel();
        RowsetsTableModel data = new RowsetsTableModel(null, rowset, null, null);

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

        // Let's remove some data to reduce test data of the cells
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

        int s1ToGlueToIndex = 1;
        int s2ToGlueToIndex = 3;
        // let's setup columns sources
        Map<Rowset, List<ColumnsSource>> columnsSources = new HashMap<>();
        columnsSources.put(s1Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s1ToGlueToIndex), loc1, 2, cells1Loc, cells1Rowset, 4, new CellDataCellRendererFactory(), null, null)));
        columnsSources.put(s2Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s2ToGlueToIndex), loc2, 2, cells2Loc, cells2Rowset, 4, new CellDataCellRendererFactory(), null, null)));

        ColumnsRiddler riddler = new ColumnsRiddler(null, columns, null, data, columnsSources, null, new ArrayList(), null);
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
    }

    @Test
    public void cellsDistributedTest() throws Exception {
        Rowset rowset = initRowset();
        TableColumnModel columns = new DefaultTableColumnModel();
        RowsetsTableModel data = new RowsetsTableModel(null, rowset, null, null);

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

        // Let's remove some data to reduce test data of the cells
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

        ApplicationDbModel dm = new ApplicationDbModel(new DummyTestDbClient());
        assertNotNull(dm);
        dm.setRuntime(true);
        ApplicationDbEntity cells2Entity = dm.newGenericEntity();
        dm.addEntity(cells2Entity);
        cells2Entity.setQuery(new DummyTestSqlQuery());
        cells2Entity.setRowset(cells2Rowset);
        ApplicationDbEntity cells2ValuesEntity = dm.newGenericEntity();
        dm.addEntity(cells2ValuesEntity);
        cells2ValuesEntity.setQuery(new DummyTestSqlQuery());
        cells2ValuesEntity.setRowset(cells2ValuesRowset);
        Relation colRelation = new Relation(cells2Entity, cells2Rowset.getFields().get(1), cells2ValuesEntity, cells2ValuesRowset.getFields().get(1));
        dm.addRelation(colRelation);

        int s1ToGlueToIndex = 1;
        int s2ToGlueToIndex = 3;
        // let's setup columns sources
        Map<Rowset, List<ColumnsSource>> columnsSources = new HashMap<>();
        columnsSources.put(s1Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s1ToGlueToIndex), loc1, 2, cells1Loc, cells1Rowset, 4, new CellDataCellRendererFactory(), null, null)));
        columnsSources.put(s2Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s2ToGlueToIndex), loc2, 2, cells2Loc, cells2ValuesRowset, 4, new CellDataCellRendererFactory(), null, null)));

        ColumnsRiddler riddler = new ColumnsRiddler(null, columns, null, data, columnsSources, null, new ArrayList(), null);
        riddler.fill();

        int commonColumnsCount = rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount, columns.getColumnCount());

        JTable tbl = new JTable(new CachingTableModel(data), columns) {
            @Override
            protected synchronized void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
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

        TableModel model = tbl.getModel();
        synchronized (tbl) {
            //model to rowsets test section 1
            for (int c = 9; c <= 15; c++) {
                for (int r = 0; r <= 2; r++) {
                    Object val = model.getValueAt(r, c);
                    assertTrue(val instanceof CellData);
                    val = ((CellData) val).getData();
                    assertTrue(val instanceof String);
                    model.getValueAt(r, c);
                    String sVal = (String) val;
                    assertFalse(sVal.endsWith("_"));
                    model.setValueAt(sVal + "_", r, c);
                }
            }
        }
        // verify...1
        synchronized (tbl) {
            cells1Rowset.beforeFirst();
            while (cells1Rowset.next()) {
                Object col = cells1Rowset.getObject(2);
                Object row = cells1Rowset.getObject(3);

                String expected = col.toString() + row.toString() + "_";
                Object val = cells1Rowset.getObject(4);

                assertEquals(expected, val);
            }
        }
        // verify...2
        synchronized (tbl) {
            cells2Rowset.beforeFirst();
            while (cells2Rowset.next()) {
                Object col = cells2Rowset.getObject(2);
                Object row = cells2Rowset.getObject(3);

                String expected = col.toString() + row.toString() + "_";
                Object val = cells2ValuesRowset.getObject(4);

                assertEquals(expected, val);
            }
        }
        // rowsets to model test section 1
        synchronized (tbl) {
            cells1Rowset.beforeFirst();
            while (cells1Rowset.next()) {
                Object col = cells1Rowset.getObject(2);
                Object row = cells1Rowset.getObject(3);

                String val = col.toString() + row.toString();
                cells1Rowset.updateObject(4, val);
            }
        }
        synchronized (tbl) {
            // rowsets to model test section 2
            cells2Rowset.beforeFirst();
            while (cells2Rowset.next()) {
                Object col = cells2Rowset.getObject(2);
                Object row = cells2Rowset.getObject(3);

                String val = col.toString() + row.toString();
                cells2ValuesRowset.updateObject(4, val);
            }
        }
        synchronized (tbl) {
            // verify...
            for (int c = 9; c <= 15; c++) {
                for (int r = 0; r <= 2; r++) {
                    Object val = model.getValueAt(r, c);
                    assertTrue(val instanceof CellData);
                    val = ((CellData) val).getData();
                    assertTrue(val instanceof String);
                    String sVal = (String) val;
                    assertFalse(sVal.endsWith("_"));
                }
            }
        }
        frame.setVisible(false);
    }
}
