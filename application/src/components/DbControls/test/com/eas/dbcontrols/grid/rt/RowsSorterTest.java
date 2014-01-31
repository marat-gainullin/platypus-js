/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.data.TreedModel;
import com.bearsoft.gui.grid.editing.InsettedTreeEditor;
import com.bearsoft.gui.grid.rendering.InsettedTreeRenderer;
import com.bearsoft.gui.grid.rendering.TreeColumnLeadingComponent;
import com.bearsoft.gui.grid.rows.TabularRowsSorter;
import com.bearsoft.gui.grid.rows.TreedRowsSorter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.dbcontrols.grid.rt.columns.model.FieldModelColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsModel;
import com.eas.dbcontrols.grid.rt.models.RowsetsTableModel;
import com.eas.dbcontrols.grid.rt.models.RowsetsTreedModel;
import com.eas.gui.CascadedStyle;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
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
public class RowsSorterTest extends GridBaseTest {

    protected static long[][] pkSequencesAsc = new long[][]{
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L},
        {6L, 15L, 16L, 17L, 18L, 19L, 3L, 4L, 2L, 7L, 1L, 5L, 20L, 21L, 22L, 8L, 9L, 10L, 11L, 12L, 13L, 14L},
        {2L, 3L, 1L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 1L, 2L, 3L, 4L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 21L, 22L, 11L, 12L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 13L},
        {13L, 1L, 10L, 11L, 12L, 14L, 15L, 16L, 17L, 18L, 19L, 2L, 20L, 21L, 22L, 3L, 4L, 5L, 6L, 7L, 8L, 9L}
    };
    protected static long[][] pkSequencesDesc = new long[][]{
        {22L, 21L, 20L, 19L, 18L, 17L, 16L, 15L, 14L, 13L, 12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L},
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {14L, 13L, 12L, 11L, 10L, 9L, 8L, 20L, 21L, 22L, 5L, 1L, 7L, 2L, 4L, 3L, 19L, 18L, 17L, 16L, 15L, 6L},
        {22L, 21L, 20L, 19L, 18L, 17L, 16L, 15L, 14L, 13L, 12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L, 4L, 1L, 2L, 3L},
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {1L, 2L, 3L, 4L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L},
        {13L, 11L, 12L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 21L, 22L},
        {9L, 8L, 7L, 6L, 5L, 4L, 3L, 22L, 21L, 20L, 2L, 19L, 18L, 17L, 16L, 15L, 14L, 12L, 11L, 10L, 1L, 13L}
    };
    protected static long[][] treedPkSequencesAsc = new long[][]{
        {1, 4, 5, 2, 3, 11, 18, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22},
        {18, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22, 1, 4, 5, 2, 3, 11},
        {18, 15, 16, 17, 19, 20, 12, 13, 14, 3, 2, 1, 4, 5, 21, 22, 11},
        {2, 3, 1, 4, 5, 11, 18, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22},
        {1, 4, 5, 2, 3, 11, 18, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22},
        {11, 1, 5, 4, 2, 3, 18, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22},
        {1, 4, 5, 2, 3, 21, 22, 11, 18, 12, 14, 15, 16, 17, 19, 20, 13},
        {1, 4, 5, 11, 18, 13, 12, 14, 15, 16, 17, 19, 20, 2, 21, 22, 3}
    };
    protected static long[][] treedPkSequencesDesc = new long[][]{
        {22, 21, 18, 20, 19, 17, 16, 15, 14, 13, 12, 11, 3, 2, 1, 5, 4},
        {1, 4, 5, 2, 3, 11, 18, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22},
        {11, 21, 22, 1, 5, 4, 2, 3, 18, 14, 13, 12, 20, 19, 17, 16, 15},
        {22, 21, 18, 20, 19, 17, 16, 15, 14, 13, 12, 11, 1, 5, 4, 2, 3},
        {1, 4, 5, 2, 3, 11, 18, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22},
        {1, 4, 5, 2, 3, 18, 15, 16, 17, 19, 20, 12, 13, 14, 21, 22, 11},
        {11, 18, 13, 12, 14, 15, 16, 17, 19, 20, 1, 4, 5, 2, 3, 21, 22},
        {3, 22, 21, 2, 18, 20, 19, 17, 16, 15, 14, 12, 13, 11, 1, 5, 4}
    };

    protected void checkPkSequence(long[] aPks, JTable aTable) throws Exception {
        assertEquals(aPks.length, aTable.getRowCount());
        for (int i = 0; i < aTable.getRowCount(); i++) {
            Object oValue = aTable.getValueAt(i, 0);
            if (oValue instanceof CellData) {
                oValue = ((CellData) oValue).getData();
            }
            assertTrue(Row.smartEquals(aPks[i], oValue));
        }
    }

    protected abstract class VisualState {

        public Rowset rowset;
        public TableColumnModel columnModel;
        public RowSorter sorter;
        public JTable table;
        protected JFrame frame;
        public RowsetsModel model;

        public VisualState() throws Exception {
            super();
            rowset = initRowset();
            columnModel = new DefaultTableColumnModel();
            createModelAndSorter();
            for (int i = 1; i <= rowset.getFields().getFieldsCount(); i++) {
                FieldModelColumn mCol = new FieldModelColumn(rowset, i, null, null, false, null, null, null);
                model.addColumn(mCol);
                TableColumn vCol = new TableColumn(i - 1, 70);
                vCol.setHeaderValue(rowset.getFields().get(i).getDescription());
                vCol.setIdentifier(mCol);
                vCol.setCellRenderer(new CellDataRenderer());
                columnModel.addColumn(vCol);
            }
            createJTable();
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRowSorter(sorter);
            frame = new JFrame();
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
            frame.setSize(600, 600);
            frame.setVisible(true);
            assertEquals(model.getColumnCount(), rowset.getFields().getFieldsCount());
        }

        protected abstract void createModelAndSorter();

        protected abstract void createJTable();

        public void end() {
            frame.setVisible(false);
        }
    }

    protected class TabularVisualState extends VisualState {

        public TabularVisualState() throws Exception {
            super();
            assertEquals(rowset.size(), ((TableModel) model).getRowCount());
            verifyTableData((TableModel) model);
        }

        @Override
        protected void createModelAndSorter() {
            model = new RowsetsTableModel(null, rowset, null, null);
            sorter = new TabularRowsSorter((TableModel) model, null);
        }

        @Override
        protected void createJTable() {
            table = new JTable(new CachingTableModel((TableModel) model), columnModel);
        }
    }

    protected class TreedVisualState extends VisualState {

        public TableFront2TreedModel<Row> front;

        public TreedVisualState() throws Exception {
            super();
        }

        @Override
        protected void createModelAndSorter() {
            model = new RowsetsTreedModel(null, rowset, rowset.getFields().getFieldsCount(), null, null);
            front = new TableFront2TreedModel<>((TreedModel<Row>) model);
            sorter = new TreedRowsSorter<>(front, null);
        }

        @Override
        protected void createJTable() {
            table = new JTable(new CachingTableModel((TableModel) front), columnModel);
            CascadedStyle style = GridBaseTest.setupTreeStyle(table);
            columnModel.getColumn(0).setCellRenderer(new InsettedTreeRenderer(columnModel.getColumn(0).getCellRenderer(), new TreeColumnLeadingComponent(front, style, false)));
            columnModel.getColumn(0).setCellEditor(new InsettedTreeEditor(columnModel.getColumn(0).getCellEditor(), new TreeColumnLeadingComponent(front, style, true)));
        }
    }

    @Test
    public void sorting1CriteriaAscTest() throws Exception {
        System.out.println("sorting1CriteriaAscTest");
        TabularVisualState state = new TabularVisualState();
        for (int i = 0; i < pkSequencesAsc.length; i++) {
            state.sorter.toggleSortOrder(i);// asc
            checkPkSequence(pkSequencesAsc[i], state.table);
            state.sorter.toggleSortOrder(i);// desc
            state.sorter.toggleSortOrder(i);// unsorted
        }
        state.end();
    }

    @Test
    public void sortingMultiCriteriaAscTest() throws Exception {
        System.out.println("sortingMultiCriteriaAscTest");
        TabularVisualState state = new TabularVisualState();
        List<SortKey> criteria = new ArrayList<>();
        criteria.add(new SortKey(3 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(5 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(7 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(2 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(8 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(4 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(6 - 1, SortOrder.ASCENDING));
        state.table.getRowSorter().setSortKeys(criteria);
        checkPkSequence(new long[]{6L, 15L, 16L, 17L, 18L, 19L, 3L, 4L, 2L, 7L, 1L, 5L, 21L, 22L, 20L, 8L, 9L, 10L, 11L, 12L, 13L, 14L}, state.table);
        state.end();
    }

    @Test
    public void sorting1CriteriaDescTest() throws Exception {
        System.out.println("sorting1CriteriaDescTest");
        TabularVisualState state = new TabularVisualState();
        for (int i = 0; i < pkSequencesAsc.length; i++) {
            state.sorter.toggleSortOrder(i); // asc
            state.sorter.toggleSortOrder(i); // desc
            checkPkSequence(pkSequencesDesc[i], state.table);
            state.sorter.toggleSortOrder(i); // unsorted
        }
        state.end();
    }

    @Test
    public void sortingMultiCriteriaAscDescTest() throws Exception {
        System.out.println("sortingMultiCriteriaAscDescTest");
        TabularVisualState state = new TabularVisualState();
        List<SortKey> criteria = new ArrayList<>();
        criteria.add(new SortKey(3 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(5 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(7 - 1, SortOrder.DESCENDING));
        criteria.add(new SortKey(2 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(8 - 1, SortOrder.DESCENDING));
        criteria.add(new SortKey(4 - 1, SortOrder.ASCENDING));
        criteria.add(new SortKey(6 - 1, SortOrder.DESCENDING));
        state.table.getRowSorter().setSortKeys(criteria);
        checkPkSequence(new long[]{6L, 15L, 16L, 17L, 18L, 19L, 3L, 4L, 2L, 7L, 1L, 5L, 20L, 22L, 21L, 8L, 9L, 10L, 11L, 12L, 13L, 14L}, state.table);
        state.end();
    }

    @Test
    public void treedSorting1CriteriaAscDescSomeExpandedTest() throws Exception {
        System.out.println("treedSorting1CriteriaAscDescSomeExpandedTest");
        TreedVisualState state = new TreedVisualState();
        state.front.expand(state.rowset.getRow(1), false);
        state.front.expand(state.rowset.getRow(18), false);
        long[] treedPkSequencesBefore = new long[state.table.getRowCount()];
        for (int i = 0; i < treedPkSequencesBefore.length; i++) {
            Object oValue = state.table.getValueAt(i, 0);
            if (oValue instanceof CellData) {
                oValue = ((CellData) oValue).getData();
            }
            treedPkSequencesBefore[i] = ((Integer) oValue).longValue();
        }
        for (int i = 0; i < treedPkSequencesAsc.length; i++) {
            state.sorter.toggleSortOrder(i);// asc
            checkPkSequence(treedPkSequencesAsc[i], state.table);
            state.sorter.toggleSortOrder(i);// desc
            checkPkSequence(treedPkSequencesDesc[i], state.table);
            state.sorter.toggleSortOrder(i);// unsorted
            checkPkSequence(treedPkSequencesBefore, state.table);
        }
        state.end();
    }
}
