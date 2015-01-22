/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.ordering.Filter;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class FilteringTest extends RowsetBaseTest {

    // 1 criterion filters tests
    protected static Object[] filtersKeys = new Object[]{true, "sample string data22", null, null, new BigDecimal(345677.9898f), BigDecimal.valueOf(748677L), "true string7"};
    protected static int[][] rowsetPks = new int[][]{
        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
        {20, 21, 22},
        {2, 3},
        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22},
        {1, 2, 3, 4, 15, 16, 17, 18, 19, 20, 21, 22},
        {13},
        {7}
    };
    // multi criteria filters tests
    // first filter in filters chain
    protected static Object[] filterMultiKey1 = new Object[]{/*21L, */false, "sample string data22", new Date(millis + 18), null, new BigDecimal(345677.9898f), BigDecimal.valueOf(345677L), "true string22"};
    protected static int mkRowsetPk1 = 21;
    // second filter in filters chain
    protected static Object[] filterMultiKey2 = new Object[]{/*13L*/false, "sample string data8", new Date(millis + 10), null, new BigDecimal(54367f), BigDecimal.valueOf(748677), "false string13"};
    protected static int mkRowsetPk2 = 13;
    // CRUD filter tests
    protected static Object[] filterCrudMultiKey = new Object[]{/*21L, */false, "sample string data22", null, new BigDecimal(345677.9898f)};
    protected static int[] crudPks = new int[]{20, 21, 22};

    @Test
    public void filter1ConditionTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("filter1ConditionTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            Filter filter = rowset.createFilter(Arrays.asList(new Integer[]{i}));
            if (rowset.getActiveFilter() != null) {
                rowset.getActiveFilter().cancel();
                assertNull(rowset.getActiveFilter());
            }
            List<Object> ks = new ArrayList<>();
            ks.add(filtersKeys[i - 2]);
            filter.apply(ks);
            assertEquals(rowsetPks[i - 2].length, rowset.size());
            assertEquals(filter.getOriginalRows().size(), testData.length);
            checkRowsetPks(rowset, rowsetPks[i - 2]);

            filter.cancel();
            assertEquals(rowset.size(), testData.length);

            filter.refilterRowset();
            assertEquals(rowset.size(), rowsetPks[i - 2].length);
            assertEquals(filter.getOriginalRows().size(), testData.length);
            checkRowsetPks(rowset, rowsetPks[i - 2]);

            SortingCriterion sc1 = new SortingCriterion(i, false);
            List<SortingCriterion> criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));

            sc1 = new SortingCriterion(1, true);
            criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
        }
        assertEquals(lreciver.willSort, (fields.getFieldsCount() - 1) * 2);
        assertEquals(lreciver.willSort, (fields.getFieldsCount() - 1) * 2);

        Filter[] filters = rowset.getFilters();
        assertEquals(filters.length, fields.getFieldsCount() - 1);
        for (Filter filter : filters) {
            rowset.removeFilter(filter);
        }
        filters = rowset.getFilters();
        assertEquals(filters.length, 0);
        //
        assertEquals(rowset.size(), testData.length);
    }

    @Test
    public void filteredCRUD1Test() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("filteredInsertTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        Filter filter = rowset.createFilter(Arrays.asList(new Integer[]{2, 3, 5, 6}));
        assertEquals(rowset.getFilters().length, 1);
        filter.apply(filterCrudMultiKey);
        assertTrue(rowset.setCursorPos(1));
        assertTrue(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(3));
        assertFalse(rowset.setCursorPos(4));
        assertTrue(rowset.setCursorPos(1));
        checkRowsetPks(rowset, crudPks);
        assertTrue(rowset.setCursorPos(1));

        rowset.insert(new Row(rowset.getFlowProvider().getEntityId(), rowset.getFields()), false);
        // lt's test pks generating capability
        assertNotNull(rowset.getCurrentRow().getColumnObject(1));
        // let's test filter's initing capability
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(2), filterCrudMultiKey[0]));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(3), filterCrudMultiKey[1]));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(5), filterCrudMultiKey[2]));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(6), filterCrudMultiKey[3]));
        assertEquals(4, rowset.size());
        /*
        // Let's see what happens when we try to change column, that doesn't belong to filter's criteria set.
        rowset.updateObject(8, "some updated string");
        // Let's see what happens when we try to change column, that belongs to filter's criteria set.
        rowset.updateObject(6, null);
        assertEquals(rowset.size(), 3);
         */
    }

    @Test
    public void filteredCRUD2Test() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("filteredUpdateAndModifiedFieldsTest");
        Rowset rowset = initRowset();
        rowset.setCursorPos(1);
        rowset.getCurrentRow().setColumnObject(7, new BigInteger("34267"));
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        Filter filter = rowset.createFilter(Arrays.asList(new Integer[]{2, 3, 5, 6}));
        assertEquals(rowset.getFilters().length, 1);
        filter.apply(filterCrudMultiKey);
        assertTrue(rowset.setCursorPos(1));
        assertTrue(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(3));
        assertFalse(rowset.setCursorPos(4));
        assertTrue(rowset.setCursorPos(1));
        checkRowsetPks(rowset, crudPks);
        assertTrue(rowset.setCursorPos(1));
        assertEquals(3, rowset.size());
    }

    protected class NewRowsIniter extends RowsetAdapter {

        public NewRowsIniter() {
            super();
        }

        @Override
        public boolean willInsertRow(RowsetInsertEvent event) {
            try {
                event.getRowset().getCurrentRow().setColumnObject(1, 765234);
                event.getRowset().getCurrentRow().setColumnObject(2, "ncvhdu");
                event.getRowset().getCurrentRow().setColumnObject(3, new Date(millis + 87));
                event.getRowset().getCurrentRow().setColumnObject(4, true);
                event.getRowset().getCurrentRow().setColumnObject(5, "ncncncncn");
                event.getRowset().getCurrentRow().setColumnObject(6, null);
                event.getRowset().getCurrentRow().setColumnObject(7, new BigInteger("38746583645"));
                event.getRowset().getCurrentRow().setColumnObject(8, 10101010);
            } catch (RowsetException ex) {
                Logger.getLogger(FilteringTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            return super.willInsertRow(event);
        }
    }

    @Test
    public void filteredCRUDInitingTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("filteredInsertInitingTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        Filter filter = rowset.createFilter(Arrays.asList(new Integer[]{2, 3, 5, 6}));
        assertEquals(rowset.getFilters().length, 1);
        filter.apply(filterCrudMultiKey);
        assertTrue(rowset.setCursorPos(1));
        assertTrue(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(3));
        assertFalse(rowset.setCursorPos(4));
        assertTrue(rowset.setCursorPos(1));
        checkRowsetPks(rowset, crudPks);
        assertTrue(rowset.setCursorPos(1));
        assertEquals(rowset.size(), 3);

        rowset.addRowsetListener(new NewRowsIniter());
        rowset.insert(new Row(rowset.getFlowProvider().getEntityId(), rowset.getFields()), false, new Object[]{1, 34L, rowset.getFields().get(4), null, rowset.getFields().get(5), "user supplied data for inserting", rowset.getFields().get(6), null, rowset.getFields().get(7), 76549076});
        // let's test pks generating capability
        assertEquals(rowset.getCurrentRow().getColumnObject(1), new BigDecimal(34L));
        // let's test filter's initing capability, overriding user supplied values, where needed
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(2), filterCrudMultiKey[0]));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(3), filterCrudMultiKey[1]));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(5), filterCrudMultiKey[2]));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(6), filterCrudMultiKey[3]));
        // let's test user supplied values initing capability
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(4), null));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(7), 76549076));
        // let's test event handler initing capability
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(8), "10101010"));// converter work illustration. There was a number, setted, but string is returned
        assertEquals(4, rowset.size());
        filter.cancel();
        assertEquals(testData.length + 1, rowset.size());
        filter.refilterRowset();
        assertEquals(4, rowset.size());
        filter.cancel();
        assertEquals(testData.length + 1, rowset.size());
        filter.refilterRowset();
        assertEquals(4, rowset.size());
        assertTrue(rowset.setCursorPos(1));
        assertTrue(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(3));
        rowset.deleteAt(3);
        assertEquals(3, rowset.size());
        filter.cancel();
        assertEquals(testData.length, rowset.size()); // it was deleted, compensating an insert
        filter.refilterRowset();
        assertEquals(3, rowset.size());
        rowset.deleteAll();
        assertEquals(0, rowset.size());
        filter.cancel();
        assertEquals(testData.length - 3, rowset.size());
        filter.refilterRowset();
        assertEquals(0, rowset.size());
        assertTrue(rowset.isEmpty());
        // let's test update for filtered rowset
        // let's start with inserting some data
        for (int i = 0; i < 10; i++) {
            rowset.insert(new Row(rowset.getFlowProvider().getEntityId(), rowset.getFields()), false, new Object[]{1, 34L, rowset.getFields().get(4), null, rowset.getFields().get(5), "user supplied data for inserting", rowset.getFields().get(6), null, rowset.getFields().get(7), 76549076});
            // let's test pks generating capability
            assertEquals(rowset.getCurrentRow().getColumnObject(1), new BigDecimal(34L));
            // let's test filter's initing capability, overriding user supplied values, where needed
            assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(2), filterCrudMultiKey[0]));
            assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(3), filterCrudMultiKey[1]));
            assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(5), filterCrudMultiKey[2]));
            assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(6), filterCrudMultiKey[3]));
            // let's test user supplied values initing capability
            assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(4), null));
            assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(7), 76549076));
            // let's test event handler initing capability
            assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(8), "10101010")); //converter work illustration
        }
        assertEquals(10, rowset.size());
        // let's update non criteria field and try to see what we get
        assertTrue(rowset.setCursorPos(7));
        Date _4Data = new Date(millis + 3453);
        String _8Data = "nonfiltering field data";
        Long _9Data = 86869595975L;
        rowset.getCurrentRow().setColumnObject(4, _4Data);
        rowset.getCurrentRow().setColumnObject(8, _8Data);
        rowset.getCurrentRow().setColumnObject(7, _9Data);
        assertEquals(10, rowset.size());
        // let's update criteria field and try to see what we get
        Boolean _2Data = true;
        String _5Data = "llllLLLLL__lll";
        BigDecimal _6Data = new BigDecimal(52367f);
        rowset.getCurrentRow().setColumnObject(2, _2Data);
        assertEquals(10 - 1, rowset.size()); // 2 field is filtering criteria, and so, row goes into another subset of rows in corresponding filter
        rowset.getCurrentRow().setColumnObject(5, _5Data);
        assertEquals(10 - 2, rowset.size()); // 5 field is filtering criteria, and so, !one more! row goes into another subset of rows in corresponding filter
        rowset.getCurrentRow().setColumnObject(6, _6Data);
        assertEquals(10 - 3, rowset.size()); // 6 field is filtering criteria, and so, !one more! row goes into another subset of rows in corresponding filter
        //rowset.getActiveFilter().refilterRowset(); refilterRowset occurs immediatly after updateObject
        //assertEquals(rowset.size(), 10-3);

        rowset.setImmediateFilter(false);
        rowset.getCurrentRow().setColumnObject(2, _2Data);
        rowset.getCurrentRow().setColumnObject(5, _5Data);
        rowset.getCurrentRow().setColumnObject(6, _6Data);
        assertEquals(7, rowset.size());
        rowset.getActiveFilter().refilterRowset();
        assertEquals(6, rowset.size());

        rowset.getCurrentRow().setColumnObject(2, _2Data);
        rowset.getCurrentRow().setColumnObject(5, _5Data);
        rowset.getCurrentRow().setColumnObject(6, _6Data);
        assertEquals(6, rowset.size());
        rowset.getActiveFilter().refilterRowset();
        assertEquals(5, rowset.size());

        rowset.getCurrentRow().setColumnObject(2, _2Data);
        rowset.getCurrentRow().setColumnObject(5, _5Data);
        rowset.getCurrentRow().setColumnObject(6, _6Data);
        assertEquals(5, rowset.size());
        rowset.getActiveFilter().refilterRowset();
        assertEquals(4, rowset.size());

        filter.apply(new Object[]{_2Data, filterCrudMultiKey[1], _5Data, _6Data});
        assertEquals(3, rowset.size());
        filter.cancel();
        assertEquals(testData.length - 3 + 10, rowset.size());
        filter.refilterRowset();
        assertEquals(3, rowset.size());
    }

    @Test
    public void filters1ConditionChainTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("filters1ConditionChainTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        List<Filter> lfilters = new ArrayList<>();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            Filter filter = rowset.createFilter(Arrays.asList(new Integer[]{i}));
            lfilters.add(filter);
            assertEquals(rowset.size(), testData.length);
        }
        for (int i = 0; i < lfilters.size(); i++) {
            if (i > 0) {
                assertTrue(rowset.getActiveFilter() == lfilters.get(i - 1));
            }
            Filter filter = lfilters.get(i);
            filter.apply(filtersKeys[i]);
            assertEquals(rowsetPks[i].length, rowset.size());
            assertEquals(testData.length, filter.getOriginalRows().size());
            checkRowsetPks(rowset, rowsetPks[i]);

            SortingCriterion sc1 = new SortingCriterion(i + 2, false);
            List<SortingCriterion> criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));

            sc1 = new SortingCriterion(1, true);
            criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
        }

        assertNotNull(rowset.getActiveFilter());
        rowset.getActiveFilter().cancel();
        assertNull(rowset.getActiveFilter());
        assertEquals(rowset.size(), testData.length);

        assertEquals(lreciver.willSort, (fields.getFieldsCount() - 1) * 2);
        assertEquals(lreciver.willSort, (fields.getFieldsCount() - 1) * 2);


        Filter[] filters = rowset.getFilters();
        assertEquals(filters.length, fields.getFieldsCount() - 1);
        for (Filter filter : filters) {
            rowset.removeFilter(filter);
        }
        filters = rowset.getFilters();
        assertEquals(filters.length, 0);
        //
        assertEquals(rowset.size(), testData.length);
    }

    @Test
    public void filterMultiCriteriaTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        List<Integer> fieldsIndicies = new ArrayList<>();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            fieldsIndicies.add(i);
        }
        Filter filter = rowset.createFilter(fieldsIndicies);
        assertEquals(rowset.getFilters().length, 1);
        filter.apply(filterMultiKey1);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk1));
    }

    @Test
    public void filterMultiCriteriaChainTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        List<Integer> filedsIndices = new ArrayList<>();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            filedsIndices.add(i);
        }
        Filter filter1 = rowset.createFilter(filedsIndices);
        assertEquals(rowset.getFilters().length, 1);
        List<Integer> filedsIndices2 = new ArrayList<>();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            filedsIndices2.add(i);
        }
        Filter filter2 = rowset.createFilter(filedsIndices2);
        assertEquals(rowset.getFilters().length, 2);

        filter1.apply(filterMultiKey1);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter1.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk1));
        assertTrue(rowset.getActiveFilter() == filter1);

        filter1.apply(filterMultiKey2);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter1.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter1);

        filter2.apply(filterMultiKey2);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter2.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter2);

        filter2.apply(filterMultiKey1);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter2.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk1));
        assertTrue(rowset.getActiveFilter() == filter2);

        filter1.apply(filterMultiKey2);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter1.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter1);

        filter2.apply(filterMultiKey1);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter2.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk1));
        assertTrue(rowset.getActiveFilter() == filter2);

        filter1.apply(filterMultiKey1);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter1.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk1));
        assertTrue(rowset.getActiveFilter() == filter1);

        filter2.apply(filterMultiKey2);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter2.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter2);

        filter1.apply(filterMultiKey2);
        assertTrue(rowset.setCursorPos(1));
        assertFalse(rowset.setCursorPos(2));
        assertTrue(rowset.setCursorPos(1));
        assertTrue(Row.smartEquals(filter1.getRowset().getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getCurrentRow().getColumnObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter1);
    }

    private void checkRowsetPks(Rowset aRowset, int[] aPks) throws InvalidCursorPositionException, InvalidColIndexException {
        for (int i = 1; i <= aRowset.size(); i++) {
            assertTrue(aRowset.setCursorPos(i));
            assertTrue(Row.smartEquals(aRowset.getCurrentRow().getColumnObject(1), aPks[i - 1]));
        }
    }
}
