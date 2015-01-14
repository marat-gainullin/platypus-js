/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import com.bearsoft.rowset.dataflow.FlowBaseTest.JdbcFlowProviderAdapter;
import com.bearsoft.rowset.dataflow.FlowBaseTest.ObservingResourcesProvider;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.events.RowsetNetErrorEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class RowsetBaseTest {

    protected static final long millis = System.currentTimeMillis();
    protected static Object[][] testData = new Object[][]{
        {1L, true, "sample string data20", new Date(millis), null, new BigDecimal(345677.9898f), new BigInteger("345677"), "true string1"},
        {2L, true, "sample string data17", null, null, new BigDecimal(345677.9898f), new BigInteger("345677"), "true string2"},
        {3L, true, "sample string data15", null, null, new BigDecimal(345677.9898f), new BigInteger("345677"), "true string3"},
        {4L, true, "sample string data16", new Date(millis + 1), null, new BigDecimal(345677.9898f), new BigInteger("345677"), "true string4"},
        {5L, true, "sample string data21", new Date(millis + 2), null, new BigDecimal(54367f), new BigInteger("345677"), "true string5"},
        {6L, true, "sample string data1", new Date(millis + 3), null, new BigDecimal(54367f), new BigInteger("345677"), "true string6"},
        {7L, true, "sample string data2", new Date(millis + 4), null, new BigDecimal(54367f), new BigInteger("345677"), "true string7"},
        {8L, true, "sample string data3", new Date(millis + 5), null, new BigDecimal(54367f), new BigInteger("345677"), "true string8"},
        {9L, true, "sample string data4", new Date(millis + 6), null, new BigDecimal(54367f), new BigInteger("345677"), "true string9"},
        {10L, true, "sample string data5", new Date(millis + 7), null, new BigDecimal(54367f), new BigInteger("345677"), "true string10"},
        {11L, true, "sample string data6", new Date(millis + 8), null, new BigDecimal(54367f), new BigInteger("745677"), "true string11"},
        {12L, false, "sample string data7", new Date(millis + 9), null, new BigDecimal(54367f), new BigInteger("745677"), "true string12"},
        {13L, false, "sample string data8", new Date(millis + 10), null, new BigDecimal(54367f), new BigInteger("748677"), "false string13"},
        {14L, false, "sample string data9", new Date(millis + 11), null, new BigDecimal(54367f), new BigInteger("745677"), "true string14"},
        {15L, false, "sample string data10", new Date(millis + 12), null, new BigDecimal(345677.9898f), new BigInteger("745677"), "true string15"},
        {16L, false, "sample string data11", new Date(millis + 13), null, new BigDecimal(345677.9898f), new BigInteger("745677"), "true string16"},
        {17L, false, "sample string data12", new Date(millis + 14), null, new BigDecimal(345677.9898f), new BigInteger("745677"), "true string17"},
        {18L, false, "sample string data13", new Date(millis + 15), null, new BigDecimal(345677.9898f), new BigInteger("745677"), "true string18"},
        {19L, false, "sample string data14", new Date(millis + 16), null, new BigDecimal(345677.9898f), new BigInteger("745677"), "true string19"},
        {20L, false, "sample string data22", new Date(millis + 17), null, new BigDecimal(345677.9898f), new BigInteger("745677"), "true string21"},
        {21L, false, "sample string data22", new Date(millis + 18), null, new BigDecimal(345677.9898f), new BigInteger("345677"), "true string22"},
        {22L, false, "sample string data22", new Date(millis + 19), null, new BigDecimal(345677.9898f), new BigInteger("345677"), "true string23"}
    };
    protected static Fields fields;
    protected static String TEST_TABLE_NAME = "TEST_TABLE";
    protected static String TEST_TABLE_DESCRIPTION = "Тестовая таблица";
    protected static String TEST_FIELD_PREFIX = "TT_";

    protected void checkRowsetCorrespondToTestData(Rowset rowset) throws InvalidColIndexException, InvalidCursorPositionException {
        for (int i = 1; i <= rowset.size(); i++) {
            rowset.setCursorPos(i);
            for (int colIndex = 1; colIndex <= rowset.getFields().getFieldsCount(); colIndex++) {
                Object tstData = testData[i - 1][colIndex - 1];
                // check of converter work
                if ((rowset.getFields().get(colIndex).getTypeInfo().getSqlType() == java.sql.Types.NUMERIC
                        || rowset.getFields().get(colIndex).getTypeInfo().getSqlType() == java.sql.Types.DECIMAL)
                        && ((tstData instanceof Long) || (tstData instanceof BigInteger))) {
                    if (tstData instanceof Long) {
                        tstData = new BigDecimal((Long) tstData);
                    } else if (tstData instanceof BigInteger) {
                        tstData = new BigDecimal((BigInteger) tstData);
                    }
                }
                //
                assertEquals(rowset.getCurrentRow().getColumnObject(colIndex), tstData);
            }
        }
    }

    protected void fillInRowset(Rowset rowset) throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        for (int i = 0; i < testData.length; i++) {
            int oldSize = rowset.size();
            rowset.insert(new Row(rowset.getFlowProvider().getEntityId(), rowset.getFields()), false);
            if (oldSize < rowset.size()) {
                assertTrue(rowset.getRow(rowset.getCursorPos()) == rowset.getCurrentRow());
                assertTrue(rowset.getCurrentRow().isInserted());
                for (int colIndex = 0; colIndex < fields.getFieldsCount(); colIndex++) {
                    rowset.getCurrentRow().setColumnObject(colIndex + 1, testData[i][colIndex]);
                }
            }
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        fields = new Fields();
        fields.setTableDescription(TEST_TABLE_DESCRIPTION);
        Field f1 = new Field(TEST_FIELD_PREFIX + "ID", "Идентификатор");

        f1.getTypeInfo().setSqlType(java.sql.Types.NUMERIC);
        f1.getTypeInfo().setJavaClassName(Long.class.getName());
        f1.setPk(true);
        f1.setNullable(false);
        fields.add(f1);
        Field f2 = new Field(TEST_FIELD_PREFIX + "CONFIRMED", "Подтверждено");
        f2.getTypeInfo().setSqlType(java.sql.Types.BOOLEAN);
        f2.getTypeInfo().setJavaClassName(Boolean.class.getName());
        fields.add(f2);
        Field f3 = new Field(TEST_FIELD_PREFIX + "CAPTION", "Заголовок");
        f3.getTypeInfo().setSqlType(java.sql.Types.VARCHAR);
        f3.getTypeInfo().setJavaClassName(String.class.getName());
        fields.add(f3);
        Field f4 = new Field(TEST_FIELD_PREFIX + "TIME", "Время создания");
        f4.getTypeInfo().setSqlType(java.sql.Types.DATE);
        f4.getTypeInfo().setJavaClassName(java.util.Date.class.getName());
        fields.add(f4);
        Field f5 = new Field(TEST_FIELD_PREFIX + "BLOCK", "Кусок данных");
        f5.getTypeInfo().setSqlType(java.sql.Types.BLOB);
        f5.getTypeInfo().setJavaClassName(SerialBlob.class.getName());
        fields.add(f5);
        Field f6 = new Field(TEST_FIELD_PREFIX + "ORDER", "Порядок");
        f6.getTypeInfo().setSqlType(java.sql.Types.DECIMAL);
        f6.getTypeInfo().setJavaClassName(BigDecimal.class.getName());
        fields.add(f6);
        Field f7 = new Field(TEST_FIELD_PREFIX + "AMOUNT", "Количество");
        f7.getTypeInfo().setSqlType(java.sql.Types.NUMERIC);
        f7.getTypeInfo().setJavaClassName(BigInteger.class.getName());
        f7.setNullable(false);
        fields.add(f7);
        Field f8 = new Field(TEST_FIELD_PREFIX + "DESCRIPTION", "Описание");
        f8.getTypeInfo().setSqlType(java.sql.Types.NVARCHAR);
        f8.getTypeInfo().setJavaClassName(String.class.getName());
        f8.setNullable(false);
        fields.add(f8);
    }

    protected Rowset initRowset() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        Rowset rowset = new Rowset(fields);
        DataSource ds = new ObservingResourcesProvider("", null, 0, 0, 0);
        rowset.setFlowProvider(new JdbcFlowProviderAdapter("test-source", ds, new RowsetConverter(), "test-clause"));
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.currentToOriginal();
        assertFalse(rowset.isEmpty());
        checkRowsetCorrespondToTestData(rowset);
        return rowset;
    }

    @Test
    public void dummyTest() {
    }

    public static class EventsReciver implements RowsetListener {

        public boolean allowScroll = true;
        public boolean allowSort = true;
        public boolean allowDelete = true;
        public boolean allowInsert = true;
        public boolean allowFilter = true;
        public boolean allowRequery = true;
        public boolean allowPaging = true;
        public boolean allowSave = true;
        public int willScroll = 0;
        public int willSort = 0;
        public int willDelete = 0;
        public int willInsert = 0;
        public int willChange = 0;
        public int willFilter = 0;
        public int willRequery = 0;
        public int willNextPageFetch = 0;
        public int scrolled = 0;
        public int sorted = 0;
        public int deleted = 0;
        public int inserted = 0;
        public int changed = 0;
        public int filtered = 0;
        public int requeried = 0;
        public int nextPageFetched = 0;
        public int saved = 0;
        public int rolledback = 0;

        @Override
        public boolean willSort(RowsetSortEvent event) {
            ++willSort;
            return allowSort;
        }

        @Override
        public boolean willScroll(RowsetScrollEvent event) {
            ++willScroll;
            return allowScroll;
        }

        @Override
        public boolean willFilter(RowsetFilterEvent event) {
            ++willFilter;
            return allowFilter;
        }

        @Override
        public boolean willRequery(RowsetRequeryEvent event) {
            ++willRequery;
            return allowRequery;
        }

        @Override
        public boolean willNextPageFetch(RowsetNextPageEvent event) {
            ++willNextPageFetch;
            return allowPaging;
        }

        @Override
        public boolean willInsertRow(RowsetInsertEvent event) {
            ++willInsert;
            return allowInsert;
        }

        @Override
        public boolean willDeleteRow(RowsetDeleteEvent event) {
            ++willDelete;
            return allowDelete;
        }

        @Override
        public void rowsetFiltered(RowsetFilterEvent event) {
            ++filtered;
        }

        @Override
        public void rowsetRequeried(RowsetRequeryEvent event) {
            ++requeried;
        }

        @Override
        public void rowsetNextPageFetched(RowsetNextPageEvent event) {
            ++nextPageFetched;
        }

        @Override
        public void rowsetSaved(RowsetSaveEvent event) {
            ++saved;
        }

        @Override
        public void rowsetRolledback(RowsetRollbackEvent event) {
            ++rolledback;
        }

        @Override
        public void rowsetScrolled(RowsetScrollEvent event) {
            ++scrolled;
        }

        @Override
        public void rowInserted(RowsetInsertEvent event) {
            assertNotNull(event.getRowset());
            assertNotNull(event.getRow());
            ++inserted;
        }

        /*
        @Override
        public void rowChanged(RowChangeEvent event) {
            ++changed;
        }
        */

        @Override
        public void rowDeleted(RowsetDeleteEvent event) {
            ++deleted;
        }

        @Override
        public void rowsetSorted(RowsetSortEvent event) {
            ++sorted;
        }

        @Override
        public void beforeRequery(RowsetRequeryEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void rowsetNetError(RowsetNetErrorEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
