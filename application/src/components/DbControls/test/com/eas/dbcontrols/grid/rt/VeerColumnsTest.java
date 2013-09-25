/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.gui.grid.columns.TableColumnHandler;
import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.dbcontrols.grid.rt.columns.model.FieldModelColumn;
import com.eas.dbcontrols.grid.rt.columns.model.RowModelColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsTableModel;
import com.eas.dbcontrols.grid.rt.veers.ColumnsRiddler;
import com.eas.dbcontrols.grid.rt.veers.ColumnsSource;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Gala
 */
public class VeerColumnsTest extends GridBaseTest {

    protected static final Fields fields1 = new Fields();

    static {
        fields1.add(new Field("s1id", "Идентификатор первой серии столбцов"));
        fields1.add(new Field("s1Title", "Заголовок первой серии столбцов"));
        fields1.add(new Field("s1Data", "Данные (текст) первой серии столбцов"));
    }
    protected static final Fields fields2 = new Fields();

    static {
        fields2.add(new Field("s2id", "Идентификатор второй серии столбцов"));
        fields2.add(new Field("s2Title", "Заголовок второй серии столбцов"));
        fields2.add(new Field("s2Data", "Данные (текст) второй серии столбцов"));
    }
    protected static final Fields fields3 = new Fields();

    static {
        fields3.add(new Field("s3id", "Идентификатор третьей серии столбцов"));
        fields3.add(new Field("s3Title", "Заголовок третьей серии столбцов"));
        fields3.add(new Field("s3Data", "Данные (текст) третьей серии столбцов"));
    }
    protected static Object[][] columnsSeries1TestData = new Object[][]{
        {1, "series1c1", "sample text1"},
        {2, "series1c2", "sample text2"},
        {3, "series1c3", "sample text3"},
        {4, "series1c4", "sample text4"},
        {5, "series1c5", "sample text5"},
        {6, "series1c6", "sample text6"},
        {7, "series1c7", "sample text7"},
        {8, "series1c8", "sample text8"},
        {9, "series1c9", "sample text9"},
        {10, "series1c10", "sample text10"}
    };
    protected static Object[][] columnsSeries2TestData = new Object[][]{
        {1, "series2c1", "sample text1"},
        {2, "series2c2", "sample text2"},
        {3, "series2c3", "sample text3"},
        {4, "series2c4", "sample text4"},
        {5, "series2c5", "sample text5"},
        {6, "series2c6", "sample text6"},
        {7, "series2c7", "sample text7"},
        {8, "series2c8", "sample text8"},
        {9, "series2c9", "sample text9"},
        {10, "series2c10", "sample text10"}
    };
    protected static Object[][] columnsSeries3TestData = new Object[][]{
        {1, "series3c1", "sample text1"},
        {2, "series3c2", "sample text2"},
        {3, "series3c3", "sample text3"},
        {4, "series3c4", "sample text4"},
        {5, "series3c5", "sample text5"},
        {6, "series3c6", "sample text6"},
        {7, "series3c7", "sample text7"},
        {8, "series3c8", "sample text8"},
        {9, "series3c9", "sample text9"},
        {10, "series3c10", "sample text10"}
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

    private void shuffleColumnModel(TableColumnModel aColumns) {
        Random rnd = new Random();
        for (int i = 0; i < aColumns.getColumnCount(); i++) {
            int from = rnd.nextInt(aColumns.getColumnCount());
            int to = rnd.nextInt(aColumns.getColumnCount());
            aColumns.moveColumn(from, to);
        }
    }

    private int getIndexOfColumn(TableColumnModel columns, TableColumn aColumn) {
        for (int i = 0; i < columns.getColumnCount(); i++) {
            if (columns.getColumn(i) == aColumn) {
                return i;
            }
        }
        return -1;
    }

    protected class CellDataCellRendererFactory implements TableColumnHandler {

        @Override
        public void handle(TableColumn aColumn) {
            aColumn.setCellRenderer(new CellDataRenderer());
        }
    }

    @Test
    public void columnsFillCrudTest() throws Exception {
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

        Rowset s1Rowset = new Rowset(fields1);
        fillInRowset(s1Rowset, columnsSeries1TestData);
        Locator loc1 = createColumnsLocator(s1Rowset);

        Rowset s2Rowset = new Rowset(fields2);
        fillInRowset(s2Rowset, columnsSeries2TestData);
        Locator loc2 = createColumnsLocator(s2Rowset);

        Rowset s3Rowset = new Rowset(fields3);
        fillInRowset(s3Rowset, columnsSeries3TestData);
        Locator loc3 = createColumnsLocator(s3Rowset);

        int s1ToGlueToIndex = 1;
        int s2ToGlueToIndex = 3;
        int s3ToGlueToIndex = 5;
        // let's setup columns sources
        Map<Rowset, List<ColumnsSource>> columnsSources = new HashMap<>();
        columnsSources.put(s1Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s1ToGlueToIndex), loc1, 2, null, null, 0, new CellDataCellRendererFactory(), null, null)));
        columnsSources.put(s2Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s2ToGlueToIndex), loc2, 2, null, null, 0, new CellDataCellRendererFactory(), null, null)));
        columnsSources.put(s3Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s3ToGlueToIndex), loc3, 2, null, null, 0, new CellDataCellRendererFactory(), null, null)));

        ColumnsRiddler riddler = new ColumnsRiddler(null, columns, null, data, columnsSources, null, new ArrayList(), null);
        riddler.fill();

        int commonColumnsCount = rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size() + s3Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size() + s3Rowset.size(), columns.getColumnCount());

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
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + s1Rowset.size() + s2Rowset.size() + 1, 2);

        // delete rows section
        int deleted = 0;

        s1Rowset.absolute(3);
        s1Rowset.delete();
        s1Rowset.absolute(5);
        s1Rowset.delete();
        deleted += 2;
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount - deleted, data.getColumnCount());
        verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);

        s2Rowset.absolute(3);
        s2Rowset.delete();
        s2Rowset.absolute(5);
        s2Rowset.delete();
        deleted += 2;
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount - deleted, data.getColumnCount());
        verifyColumns(columns, s2Rowset, s2ToGlueToIndex + 1 + s1Rowset.size(), 2);

        s3Rowset.absolute(3);
        s3Rowset.delete();
        s3Rowset.absolute(5);
        s3Rowset.delete();
        deleted += 2;
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount - deleted, data.getColumnCount());
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + 1 + s1Rowset.size() + s2Rowset.size(), 2);
        int afterDeleteColumnsCount = rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size() + s3Rowset.size();

        // insert rows section
        int inserted = 0;

        s1Rowset.absolute(2);
        s1Rowset.insert();
        s1Rowset.absolute(5);
        s1Rowset.insert();
        inserted += 2;
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(afterDeleteColumnsCount + inserted, data.getColumnCount());
        verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);

        s2Rowset.absolute(2);
        s2Rowset.insert();
        s2Rowset.absolute(5);
        s2Rowset.insert();
        inserted += 2;
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(afterDeleteColumnsCount + inserted, data.getColumnCount());
        verifyColumns(columns, s2Rowset, s2ToGlueToIndex + 1 + s1Rowset.size(), 2);

        s3Rowset.absolute(2);
        s3Rowset.insert();
        s3Rowset.absolute(5);
        s3Rowset.insert();
        inserted += 2;
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(afterDeleteColumnsCount + inserted, data.getColumnCount());
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + 1 + s1Rowset.size() + s2Rowset.size(), 2);

        assertEquals(afterDeleteColumnsCount + inserted, commonColumnsCount);
        // +1 is needed, because of 1, 3, 5 is column's indices of columns, columns of interest are glued to.
        // and columns of interest have indicies one greater than 1, 3, 5. 
        verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);
        verifyColumns(columns, s2Rowset, s2ToGlueToIndex + s1Rowset.size() + 1, 2);
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + s1Rowset.size() + s2Rowset.size() + 1, 2);
        // update rows section
        //
        frame.setVisible(false);
    }

    @Test
    public void columnsSideEffectsCrudTest() throws Exception {
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

        Rowset s1Rowset = new Rowset(fields1);
        fillInRowset(s1Rowset, columnsSeries1TestData);
        Locator loc1 = createColumnsLocator(s1Rowset);

        Rowset s2Rowset = new Rowset(fields2);
        fillInRowset(s2Rowset, columnsSeries2TestData);
        Locator loc2 = createColumnsLocator(s2Rowset);

        Rowset s3Rowset = new Rowset(fields3);
        fillInRowset(s3Rowset, columnsSeries3TestData);
        Locator loc3 = createColumnsLocator(s3Rowset);

        int s1ToGlueToIndex = 1;
        int s2ToGlueToIndex = 3;
        int s3ToGlueToIndex = 5;
        // let's setup columns sources
        Map<Rowset, List<ColumnsSource>> columnsSources = new HashMap<>();
        columnsSources.put(s1Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s1ToGlueToIndex), loc1, 2, null, null, 0, new CellDataCellRendererFactory(), null, null)));
        columnsSources.put(s2Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s2ToGlueToIndex), loc2, 2, null, null, 0, new CellDataCellRendererFactory(), null, null)));
        columnsSources.put(s3Rowset, Collections.singletonList(new ColumnsSource(null, columns.getColumn(s3ToGlueToIndex), loc3, 2, null, null, 0, new CellDataCellRendererFactory(), null, null)));

        ColumnsRiddler riddler = new ColumnsRiddler(null, columns, null, data, columnsSources, null, new ArrayList(), null);
        riddler.fill();

        int commonColumnsCount = rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size() + s3Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size() + s3Rowset.size(), columns.getColumnCount());

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
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + s1Rowset.size() + s2Rowset.size() + 1, 2);

        // delete rows section. Side effect is in deleting all rows in the columns rowsets.
        int deleted = 0;

        deleted += s1Rowset.size();
        s1Rowset.deleteAll();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount - deleted, data.getColumnCount());
        verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);

        deleted += s2Rowset.size();
        s2Rowset.deleteAll();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount - deleted, data.getColumnCount());
        verifyColumns(columns, s2Rowset, s2ToGlueToIndex + 1 + s1Rowset.size(), 2);

        deleted += s3Rowset.size();
        s3Rowset.deleteAll();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount - deleted, data.getColumnCount());
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + 1 + s1Rowset.size() + s2Rowset.size(), 2);
        int afterDeleteColumnsCount = rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size() + s3Rowset.size();

        // Insert rows section. Side effect is in inserting a row in empty rowsets.
        int inserted = 0;

        fillInRowset(s1Rowset, columnsSeries1TestData);
        inserted += s1Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(afterDeleteColumnsCount + inserted, data.getColumnCount());
        verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);

        fillInRowset(s2Rowset, columnsSeries2TestData);
        inserted += s2Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(afterDeleteColumnsCount + inserted, data.getColumnCount());
        verifyColumns(columns, s2Rowset, s2ToGlueToIndex + 1 + s1Rowset.size(), 2);

        fillInRowset(s3Rowset, columnsSeries3TestData);
        inserted += s3Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(afterDeleteColumnsCount + inserted, data.getColumnCount());
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + 1 + s1Rowset.size() + s2Rowset.size(), 2);

        assertEquals(afterDeleteColumnsCount + inserted, commonColumnsCount);
        // +1 is needed, because of 1, 3, 5 is column's indices of columns, columns of interest are glued to.
        // and columns of interest have indicies one greater than 1, 3, 5. 
        verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);
        verifyColumns(columns, s2Rowset, s2ToGlueToIndex + s1Rowset.size() + 1, 2);
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + s1Rowset.size() + s2Rowset.size() + 1, 2);
        // update rows section
        //
        frame.setVisible(false);
    }

    @Test
    public void columnsShuffleFullCrudTest() throws Exception {
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

        Rowset s1Rowset = new Rowset(fields1);
        fillInRowset(s1Rowset, columnsSeries1TestData);
        Locator loc1 = createColumnsLocator(s1Rowset);

        Rowset s2Rowset = new Rowset(fields2);
        fillInRowset(s2Rowset, columnsSeries2TestData);
        Locator loc2 = createColumnsLocator(s2Rowset);

        Rowset s3Rowset = new Rowset(fields3);
        fillInRowset(s3Rowset, columnsSeries3TestData);
        Locator loc3 = createColumnsLocator(s3Rowset);

        int s1ToGlueToIndex = 1;
        int s2ToGlueToIndex = 3;
        int s3ToGlueToIndex = 5;
        TableColumn col1ToGlueTo = columns.getColumn(s1ToGlueToIndex);
        TableColumn col2ToGlueTo = columns.getColumn(s2ToGlueToIndex);
        TableColumn col3ToGlueTo = columns.getColumn(s3ToGlueToIndex);
        // let's setup columns sources
        Map<Rowset, List<ColumnsSource>> columnsSources = new HashMap<>();
        columnsSources.put(s1Rowset, Collections.singletonList(new ColumnsSource(null, col1ToGlueTo, loc1, 2, null, null, 0, new CellDataCellRendererFactory(), null, null)));
        columnsSources.put(s2Rowset, Collections.singletonList(new ColumnsSource(null, col2ToGlueTo, loc2, 2, null, null, 0, new CellDataCellRendererFactory(), null, null)));
        columnsSources.put(s3Rowset, Collections.singletonList(new ColumnsSource(null, col3ToGlueTo, loc3, 2, null, null, 0, new CellDataCellRendererFactory(), null, null)));

        ColumnsRiddler riddler = new ColumnsRiddler(null, columns, null, data, columnsSources, null, new ArrayList(), null);
        riddler.fill();

        int commonColumnsCount = rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size() + s3Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size() + s3Rowset.size(), columns.getColumnCount());

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
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + s1Rowset.size() + s2Rowset.size() + 1, 2);

        // Let's shuffle the whole columns model and see what it will lead to.
        shuffleColumnModel(columns);
        // delete rows section. Side effect is in deleting all rows in the columns rowsets.
        int deleted = 0;

        deleted += s1Rowset.size();
        s1Rowset.deleteAll();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount - deleted, data.getColumnCount());
        // The following call does nothing becauseof s3Rowset is empty
        verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);
        //\ retained here only for logical strength of tested code

        deleted += s2Rowset.size();
        s2Rowset.deleteAll();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount - deleted, data.getColumnCount());
        // The following call does nothing becauseof s3Rowset is empty
        verifyColumns(columns, s2Rowset, s2ToGlueToIndex + 1 + s1Rowset.size(), 2);
        //\ retained here only for logical strength of tested code

        deleted += s3Rowset.size();
        s3Rowset.deleteAll();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(commonColumnsCount - deleted, data.getColumnCount());
        // The following section does nothing becauseof s3Rowset is empty
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + 1 + s1Rowset.size() + s2Rowset.size(), 2);
        int afterDeleteColumnsCount = rowset.getFields().getFieldsCount() + s1Rowset.size() + s2Rowset.size() + s3Rowset.size();
        //\ retained here only for logical strength of tested code

        // Insert and update rows section. Side effect is in inserting a row in empty rowsets.
        int inserted = 0;

        fillInRowset(s1Rowset, columnsSeries1TestData);
        inserted += s1Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(afterDeleteColumnsCount + inserted, data.getColumnCount());
        s1ToGlueToIndex = getIndexOfColumn(columns, col1ToGlueTo);
        verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);

        fillInRowset(s2Rowset, columnsSeries2TestData);
        inserted += s2Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(afterDeleteColumnsCount + inserted, data.getColumnCount());
        s2ToGlueToIndex = getIndexOfColumn(columns, col2ToGlueTo);
        verifyColumns(columns, s2Rowset, s2ToGlueToIndex + 1, 2);

        fillInRowset(s3Rowset, columnsSeries3TestData);
        inserted += s3Rowset.size();
        assertEquals(columns.getColumnCount(), data.getColumnCount());
        assertEquals(afterDeleteColumnsCount + inserted, data.getColumnCount());
        s3ToGlueToIndex = getIndexOfColumn(columns, col3ToGlueTo);
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + 1, 2);

        assertEquals(afterDeleteColumnsCount + inserted, commonColumnsCount);
        // +1 is needed, because of 1, 3, 5 is column's indices of columns, columns of interest are glued to.
        // and columns of interest have indicies one greater than 1, 3, 5. 
        s1ToGlueToIndex = getIndexOfColumn(columns, col1ToGlueTo);
        s2ToGlueToIndex = getIndexOfColumn(columns, col2ToGlueTo);
        s3ToGlueToIndex = getIndexOfColumn(columns, col3ToGlueTo);
        verifyColumns(columns, s1Rowset, s1ToGlueToIndex + 1, 2);
        verifyColumns(columns, s2Rowset, s2ToGlueToIndex + 1, 2);
        verifyColumns(columns, s3Rowset, s3ToGlueToIndex + 1, 2);
        //
        frame.setVisible(false);
    }
}
