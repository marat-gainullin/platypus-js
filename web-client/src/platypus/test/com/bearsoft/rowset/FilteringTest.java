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
import com.bearsoft.rowset.filters.Filter;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.bearsoft.rowset.utils.KeySet;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class FilteringTest extends DataRowsetBaseTest {

    // 1 criterion filters tests
    protected static Object[] filtersKeys = new Object[]{true, "sample string data22", null, null, 345677.9898d, 748677.0d, "true string7"};
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
    protected static Object[] filterMultiKey1 = new Object[]{/*21L, */false, "sample string data22", new Date(millis + 18), null, 345677.9898d, 345677d, "true string22"};
    protected static int mkRowsetPk1 = 21;
    // second filter in filters chain
    protected static Object[] filterMultiKey2 = new Object[]{/*13L*/false, "sample string data8", new Date(millis + 10), null, 54367.0d, 748677d, "false string13"};
    protected static int mkRowsetPk2 = 13;
    // CRUD filter tests
    protected static Object[] filterCrudMultiKey = new Object[]{/*21L, */false, "sample string data22", null, 345677.9898d};
    protected static int[] crudPks = new int[]{20, 21, 22};

    public void testFilter1ConditionTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("filter1ConditionTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            for (Locator locator : rowset.getLocators()) {
                locator.validate();
            }
            Locator loc = rowset.createLocator();
            loc.beginConstrainting();
            loc.addConstraint(i);
            loc.endConstrainting();
            loc.validate();

            assertEquals(rowset.getLocators().length, i - 1);
            Filter hf = rowset.createFilter();
            hf.beginConstrainting();
            hf.addConstraint(i);
            hf.endConstrainting();
            checkAllLocatorsAreValid(rowset);
            if (rowset.getActiveFilter() != null) {
                rowset.getActiveFilter().cancelFilter();
                assertNull(rowset.getActiveFilter());
            }
            hf.build();
            KeySet ks = new KeySet();
            ks.add(filtersKeys[i - 2]);
            hf.filterRowset(ks);
            checkAllLocatorsAreInvalid(rowset);
            assertEquals(rowsetPks[i - 2].length, rowset.size());
            assertEquals(hf.getOriginalRows().size(), testData.length);
            checkRowsetPks(rowset, rowsetPks[i - 2]);

            hf.cancelFilter();
            assertEquals(rowset.size(), testData.length);

            hf.refilterRowset();
            checkAllLocatorsAreInvalid(rowset);
            assertEquals(rowset.size(), rowsetPks[i - 2].length);
            assertEquals(hf.getOriginalRows().size(), testData.length);
            checkRowsetPks(rowset, rowsetPks[i - 2]);

            SortingCriterion sc1 = new SortingCriterion(i, false);
            List<SortingCriterion> criteria = new ArrayList();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            checkAllLocatorsAreInvalid(rowset);

            sc1 = new SortingCriterion(1, true);
            criteria = new ArrayList();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            checkAllLocatorsAreInvalid(rowset);
        }
        assertEquals(lreciver.willSort, (fields.getFieldsCount() - 1) * 2);
        assertEquals(lreciver.willSort, (fields.getFieldsCount() - 1) * 2);

        Locator[] locators = rowset.getLocators();
        assertEquals(locators.length, fields.getFieldsCount() - 1);
        for (int i = 0; i < locators.length; i++) {
            rowset.removeLocator(locators[i]);
        }
        locators = rowset.getLocators();
        assertEquals(locators.length, 0);

        Filter[] filters = rowset.getFilters();
        assertEquals(filters.length, fields.getFieldsCount() - 1);
        for (int i = 0; i < filters.length; i++) {
            rowset.removeFilter(filters[i]);
        }
        filters = rowset.getFilters();
        assertEquals(filters.length, 0);

        assertEquals(rowset.size(), testData.length);
    }

    public void testFilteredCRUD1Test() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("filteredInsertTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        Filter filter = rowset.createFilter();
        filter.beginConstrainting();
        filter.addConstraint(2);
        filter.addConstraint(3);
        filter.addConstraint(5);
        filter.addConstraint(6);
        filter.endConstrainting();
        filter.build();
        assertEquals(rowset.getFilters().length, 1);
        filter.filterRowset(filterCrudMultiKey);
        assertTrue(rowset.first());
        assertTrue(rowset.next());
        assertTrue(rowset.next());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        checkRowsetPks(rowset, crudPks);
        assertTrue(rowset.first());

        rowset.insert();
        // lt's test pks generating capability
        assertNotNull(rowset.getObject(1));
        // let's test filter's initing capability
        assertTrue(Row.smartEquals(rowset.getObject(2), filterCrudMultiKey[0]));
        assertTrue(Row.smartEquals(rowset.getObject(3), filterCrudMultiKey[1]));
        assertTrue(Row.smartEquals(rowset.getObject(5), filterCrudMultiKey[2]));
        assertTrue(Row.smartEquals(rowset.getObject(6), filterCrudMultiKey[3]));
        assertEquals(rowset.size(), 4);
        /*
        // Let's see what happens when we try to change column, that doesn't belong to filter's criteria set.
        rowset.updateObject(8, "some updated string");
        // Let's see what happens when we try to change column, that belongs to filter's criteria set.
        rowset.updateObject(6, null);
        assertEquals(rowset.size(), 3);
         */
    }

    protected class NewRowsIniter extends RowsetAdapter {

        public NewRowsIniter() {
            super();
        }

        @Override
        public boolean willInsertRow(RowsetInsertEvent event) {
            try {
                event.getRowset().updateObject(1, 765234);
                event.getRowset().updateObject(2, "ncvhdu");
                event.getRowset().updateObject(3, new Date(millis + 87));
                event.getRowset().updateObject(4, true);
                event.getRowset().updateObject(5, "ncncncncn");
                event.getRowset().updateObject(6, null);
                event.getRowset().updateObject(7, new BigInteger("38746583645"));
                event.getRowset().updateObject(8, 10101010);
            } catch (RowsetException ex) {
                Logger.getLogger(FilteringTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            return super.willInsertRow(event);
        }
    }

    public void testFilteredCRUDInitingTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("filteredInsertInitingTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        Filter filter = rowset.createFilter();
        filter.beginConstrainting();
        filter.addConstraint(2);
        filter.addConstraint(3);
        filter.addConstraint(5);
        filter.addConstraint(6);
        filter.endConstrainting();
        filter.build();
        assertEquals(rowset.getFilters().length, 1);
        filter.filterRowset(filterCrudMultiKey);
        assertTrue(rowset.first());
        assertTrue(rowset.next());
        assertTrue(rowset.next());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        checkRowsetPks(rowset, crudPks);
        assertTrue(rowset.first());
        assertEquals(rowset.size(), 3);

        rowset.addRowsetListener(new NewRowsIniter());
        rowset.insert(new Object[]{1, 34L, rowset.getFields().get(4), null, rowset.getFields().get(5), "user supplied data for inserting", rowset.getFields().get(6), null, rowset.getFields().get(7), 76549076});
        // let's test pks generating capability
        assertEquals(rowset.getObject(1), 34d);
        // let's test filter's initing capability, overriding user supplied values, where needed
        assertTrue(Row.smartEquals(rowset.getObject(2), filterCrudMultiKey[0]));
        assertTrue(Row.smartEquals(rowset.getObject(3), filterCrudMultiKey[1]));
        assertTrue(Row.smartEquals(rowset.getObject(5), filterCrudMultiKey[2]));
        assertTrue(Row.smartEquals(rowset.getObject(6), filterCrudMultiKey[3]));
        // let's test user supplied values initing capability
        assertTrue(Row.smartEquals(rowset.getObject(4), null));
        assertTrue(Row.smartEquals(rowset.getObject(7), 76549076));
        // let's test event handler initing capability
        assertTrue(Row.smartEquals(rowset.getObject(8), "10101010"));// converter work illustration. There was a number, setted, but string is returned
        assertEquals(rowset.size(), 4);
        filter.cancelFilter();
        assertEquals(rowset.size(), testData.length + 1);
        filter.refilterRowset();
        assertEquals(rowset.size(), 4);
        filter.cancelFilter();
        assertEquals(rowset.size(), testData.length + 1);
        filter.refilterRowset();
        assertEquals(rowset.size(), 4);
        assertTrue(rowset.first());
        assertTrue(rowset.next());
        assertTrue(rowset.next());
        rowset.delete();
        assertEquals(rowset.size(), 3);
        filter.cancelFilter();
        assertEquals(rowset.size(), testData.length); // it was deleted, compensating an insert
        filter.refilterRowset();
        assertEquals(rowset.size(), 3);
        rowset.deleteAll();
        assertEquals(rowset.size(), 0);
        filter.cancelFilter();
        assertEquals(rowset.size(), testData.length - 3);
        filter.refilterRowset();
        assertEquals(rowset.size(), 0);
        assertTrue(rowset.isEmpty());
        // let's test update for filtered rowset
        // let's start with inserting some data
        for (int i = 0; i < 10; i++) {
            rowset.insert(new Object[]{1, 34L, rowset.getFields().get(4), null, rowset.getFields().get(5), "user supplied data for inserting", rowset.getFields().get(6), null, rowset.getFields().get(7), 76549076});
            // let's test pks generating capability
            assertEquals(rowset.getObject(1), 34d);
            // let's test filter's initing capability, overriding user supplied values, where needed
            assertTrue(Row.smartEquals(rowset.getObject(2), filterCrudMultiKey[0]));
            assertTrue(Row.smartEquals(rowset.getObject(3), filterCrudMultiKey[1]));
            assertTrue(Row.smartEquals(rowset.getObject(5), filterCrudMultiKey[2]));
            assertTrue(Row.smartEquals(rowset.getObject(6), filterCrudMultiKey[3]));
            // let's test user supplied values initing capability
            assertTrue(Row.smartEquals(rowset.getObject(4), null));
            assertTrue(Row.smartEquals(rowset.getObject(7), 76549076));
            // let's test event handler initing capability
            assertTrue(Row.smartEquals(rowset.getObject(8), "10101010")); //converter work illustration
        }
        assertEquals(rowset.size(), 10);
        // let's update non criteria field and try to see what we get
        assertTrue(rowset.absolute(7));
        Date _4Data = new Date(millis + 3453);
        String _8Data = "nonfiltering field data";
        Long _9Data = 86869595975L;
        rowset.updateObject(4, _4Data);
        rowset.updateObject(8, _8Data);
        rowset.updateObject(7, _9Data);
        assertEquals(rowset.size(), 10);
        // let's update criteria field and try to see what we get
        Boolean _2Data = true;
        String _5Data = "llllLLLLL__lll";
        Double _6Data = new Double(52367.0d);
        rowset.updateObject(2, _2Data);
        assertEquals(rowset.size(), 10 - 1); // 2 field is filtering criteria, and so, row goes into another subset of rows in corresponding filter
        rowset.updateObject(5, _5Data);
        assertEquals(rowset.size(), 10 - 2); // 5 field is filtering criteria, and so, !one more! row goes into another subset of rows in corresponding filter
        rowset.updateObject(6, _6Data);
        assertEquals(rowset.size(), 10 - 3); // 6 field is filtering criteria, and so, !one more! row goes into another subset of rows in corresponding filter
        //rowset.getActiveFilter().refilterRowset(); refilterRowset occurs immediatly after updateObject
        //assertEquals(rowset.size(), 10-3);

        rowset.setImmediateFilter(false);
        rowset.updateObject(2, _2Data);
        rowset.updateObject(5, _5Data);
        rowset.updateObject(6, _6Data);
        assertEquals(rowset.size(), 7);
        rowset.getActiveFilter().refilterRowset();
        assertEquals(rowset.size(), 6);

        rowset.updateObject(2, _2Data);
        rowset.updateObject(5, _5Data);
        rowset.updateObject(6, _6Data);
        assertEquals(rowset.size(), 6);
        rowset.getActiveFilter().refilterRowset();
        assertEquals(rowset.size(), 5);

        rowset.updateObject(2, _2Data);
        rowset.updateObject(5, _5Data);
        rowset.updateObject(6, _6Data);
        assertEquals(rowset.size(), 5);
        rowset.getActiveFilter().refilterRowset();
        assertEquals(rowset.size(), 4);

        filter.filterRowset(new Object[]{_2Data, filterCrudMultiKey[1], _5Data, _6Data});
        assertEquals(rowset.size(), 3);
        filter.cancelFilter();
        assertEquals(rowset.size(), testData.length - 3 + 10);
        filter.refilterRowset();
        assertEquals(rowset.size(), 3);
    }

    public void testFilters1ConditionChainTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("filtersChainTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        List<Filter> lfilters = new ArrayList();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            Filter hf = rowset.createFilter();
            hf.beginConstrainting();
            hf.addConstraint(i);
            hf.endConstrainting();
            lfilters.add(hf);
            hf.validate();
            assertEquals(rowset.size(), testData.length);
        }

        for (int i = 0; i < lfilters.size(); i++) {
            for (Locator locator : rowset.getLocators()) {
                locator.validate();
            }
            Locator loc = rowset.createLocator();
            loc.beginConstrainting();
            loc.addConstraint(i);
            loc.endConstrainting();
            loc.validate();

            assertEquals(rowset.getLocators().length, i + 1);
            if (i > 0) {
                assertTrue(rowset.getActiveFilter() == lfilters.get(i - 1));
            }
            Filter hf = lfilters.get(i);
            KeySet ks = new KeySet();
            ks.add(filtersKeys[i]);
            checkAllLocatorsAreValid(rowset);
            hf.filterRowset(ks);
            checkAllLocatorsAreInvalid(rowset);
            assertEquals(rowsetPks[i].length, rowset.size());
            assertEquals(testData.length, hf.getOriginalRows().size());
            checkRowsetPks(rowset, rowsetPks[i]);

            SortingCriterion sc1 = new SortingCriterion(i + 2, false);
            List<SortingCriterion> criteria = new ArrayList();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            checkAllLocatorsAreInvalid(rowset);

            sc1 = new SortingCriterion(1, true);
            criteria = new ArrayList();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            checkAllLocatorsAreInvalid(rowset);
        }

        assertNotNull(rowset.getActiveFilter());
        rowset.getActiveFilter().cancelFilter();
        assertNull(rowset.getActiveFilter());
        assertEquals(rowset.size(), testData.length);
        checkAllLocatorsAreInvalid(rowset);

        assertEquals(lreciver.willSort, (fields.getFieldsCount() - 1) * 2);
        assertEquals(lreciver.willSort, (fields.getFieldsCount() - 1) * 2);


        Locator[] locators = rowset.getLocators();
        assertEquals(locators.length, fields.getFieldsCount() - 1);
        for (int i = 0; i < locators.length; i++) {
            rowset.removeLocator(locators[i]);
        }
        locators = rowset.getLocators();
        assertEquals(locators.length, 0);

        Filter[] filters = rowset.getFilters();
        assertEquals(filters.length, fields.getFieldsCount() - 1);
        for (int i = 0; i < filters.length; i++) {
            rowset.removeFilter(filters[i]);
        }
        filters = rowset.getFilters();
        assertEquals(filters.length, 0);

        assertEquals(rowset.size(), testData.length);
    }

    public void testFilterMultiCriteriaTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        Filter filter = rowset.createFilter();
        filter.beginConstrainting();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            filter.addConstraint(i);
        }
        filter.endConstrainting();
        filter.build();
        assertEquals(rowset.getFilters().length, 1);
        filter.filterRowset(filterMultiKey1);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter.getRowset().getObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk1));
    }

    public void testFilterMultiCriteriaChainTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        Filter filter1 = rowset.createFilter();
        filter1.beginConstrainting();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            filter1.addConstraint(i);
        }
        filter1.endConstrainting();
        filter1.validate();
        assertEquals(rowset.getFilters().length, 1);
        Filter filter2 = rowset.createFilter();
        filter2.beginConstrainting();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            filter2.addConstraint(i);
        }
        filter2.endConstrainting();
        filter2.validate();
        assertEquals(rowset.getFilters().length, 2);

        filter1.filterRowset(filterMultiKey1);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter1.getRowset().getObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk1));
        assertTrue(rowset.getActiveFilter() == filter1);

        filter1.filterRowset(filterMultiKey2);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter1.getRowset().getObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter1);

        filter2.filterRowset(filterMultiKey2);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter2.getRowset().getObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter2);

        filter2.filterRowset(filterMultiKey1);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter2.getRowset().getObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk1));
        assertTrue(rowset.getActiveFilter() == filter2);

        filter1.filterRowset(filterMultiKey2);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter1.getRowset().getObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter1);

        filter2.filterRowset(filterMultiKey1);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter2.getRowset().getObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk1));
        assertTrue(rowset.getActiveFilter() == filter2);

        filter1.filterRowset(filterMultiKey1);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter1.getRowset().getObject(1), mkRowsetPk1));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk1));
        assertTrue(rowset.getActiveFilter() == filter1);

        filter2.filterRowset(filterMultiKey2);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter2.getRowset().getObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter2);

        filter1.filterRowset(filterMultiKey2);
        assertTrue(rowset.first());
        assertFalse(rowset.next());
        assertTrue(rowset.first());
        assertTrue(Row.smartEquals(filter1.getRowset().getObject(1), mkRowsetPk2));
        assertTrue(Row.smartEquals(rowset.getObject(1), mkRowsetPk2));
        assertTrue(rowset.getActiveFilter() == filter1);
    }

    private void checkAllLocatorsAreInvalid(Rowset aRowset) {
        Locator[] locators = aRowset.getLocators();
        for (int i = 0; i < locators.length; i++) {
            assertFalse(locators[i].isValid());
        }
    }

    private void checkAllLocatorsAreValid(Rowset aRowset) {
        Locator[] locators = aRowset.getLocators();
        for (int i = 0; i < locators.length; i++) {
            assertTrue(locators[i].isValid());
        }
    }

    private void checkRowsetPks(Rowset aRowset, int[] aPks) throws InvalidCursorPositionException, InvalidColIndexException {
        for (int i = 1; i <= aRowset.size(); i++) {
            assertTrue(aRowset.absolute(i));
            assertTrue(Row.smartEquals(aRowset.getObject(1), aPks[i - 1]));
        }
    }
}
