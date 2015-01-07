/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import java.util.List;
import java.math.BigDecimal;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.ordering.Orderer;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class OrderersTest extends RowsetBaseTest {

    // 1 criterion orderers tests
    protected static Object[] orderersKeys = new Object[]{true, "sample string data22", null, null, new BigDecimal(345677.9898f), BigDecimal.valueOf(748677), "true string7"};
    protected static int[][] rowsetPositions = new int[][]{
        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
        {20, 21, 22},
        {2, 3},
        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22},
        {1, 2, 3, 4, 15, 16, 17, 18, 19, 20, 21, 22},
        {13},
        {7}
    };
    // multi criteria orderer tests
    protected static Object[] ordererMultiKey = new Object[]{/*21L, */false, "sample string data22", new Date(millis + 18), null, new BigDecimal(345677.9898f), BigDecimal.valueOf(345677L), "true string22"};
    protected static int mkRowsetPosition = 21;

    @Test
    public void simpleOrderer1CriteriaTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("simpleOrdererTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            Orderer orderer = rowset.createOrderer(Arrays.asList(new Integer[]{i}));
            assertEquals(rowset.getOrderers().length, i - 1);
            List<Object> ks = new ArrayList<>();
            ks.add(orderersKeys[i - 2]);
            Collection<Row> found = orderer.get(ks);
            assertEquals(rowsetPositions[i - 2].length, found.size());
            assertEquals(testData.length, orderer.getRowset().size());

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
        Orderer[] orderers = rowset.getOrderers();
        assertEquals(orderers.length, fields.getFieldsCount() - 1);
        for (Orderer orderer : orderers) {
            rowset.removeOrderer(orderer);
        }
        orderers = rowset.getOrderers();
        assertEquals(orderers.length, 0);
    }

    @Test
    public void simpleOrderer1CriteriaWrongTypesTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("simpleOrderer1CriteriaWrongTypesTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            Orderer orderer = rowset.createOrderer(Arrays.asList(new Integer[]{i}));
            assertEquals(rowset.getOrderers().length, i - 1);
            Object keyValue = orderersKeys[i - 2];
            if (keyValue instanceof Boolean) {
                Boolean bValue = (Boolean) keyValue;
                keyValue = bValue ? Long.valueOf(1) : Long.valueOf(1);
            }
            if (keyValue instanceof Date) {
                Date dValue = (Date) keyValue;
                keyValue = dValue.getTime();
            }
            Collection<Row> found = orderer.get(keyValue);
            assertEquals(rowsetPositions[i - 2].length, found.size());
            assertEquals(testData.length, orderer.getRowset().getCurrent().size());

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
        Orderer[] orderers = rowset.getOrderers();
        assertEquals(orderers.length, fields.getFieldsCount() - 1);
        for (Orderer orderer : orderers) {
            rowset.removeOrderer(orderer);
        }
        orderers = rowset.getOrderers();
        assertEquals(orderers.length, 0);
    }

    @Test
    public void simpleOrdererMultiCriteriaTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("simpleOrdererMultiCriteriaTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        List<Integer> fieldIndicies = new ArrayList<>();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            fieldIndicies.add(i);
        }
        Orderer orderer = rowset.createOrderer(fieldIndicies);
        assertEquals(rowset.getOrderers().length, 1);
        Collection<Row> found = orderer.get(ordererMultiKey);
        assertFalse(found.isEmpty());
    }

    @Test
    public void crudOrdererTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("crudOrdererTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);

        List<Integer> fieldsIndicies = new ArrayList<>();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            fieldsIndicies.add(i);
        }
        Orderer orderer = rowset.createOrderer(fieldsIndicies);

        assertEquals(rowset.getOrderers().length, 1);
        Collection<Row> found = orderer.get(ordererMultiKey);
        assertFalse(found.isEmpty());

        rowset.absolute(10);
        rowset.insert();
        rowset.getCurrentRow().setColumnObject(2, false);
        found = orderer.get(ordererMultiKey);
        assertFalse(found.isEmpty());

        rowset.absolute(9);
        rowset.delete();
        found = orderer.get(ordererMultiKey);
        assertFalse(found.isEmpty());

        rowset.getCurrentRow().setColumnObject(6, null);
        found = orderer.get(ordererMultiKey);
        assertNotNull(found);
        assertTrue(found.isEmpty());

        assertEquals(orderer.getRowset().getCursorPos(), mkRowsetPosition);
        rowset.getCurrentRow().setColumnObject(6, 345677.9898f);
        assertEquals(orderer.getRowset().getCursorPos(), mkRowsetPosition);

        found = orderer.get(ordererMultiKey);
        assertFalse(found.isEmpty());

        /*
        // updating of non-locator field doesn't lead to invalidating of locator.
        assertTrue(loc.isValid());
        rowset.getCurrentRow().setColumnObject(1, 45);
        assertTrue(loc.isValid());
        */
    }    
}
