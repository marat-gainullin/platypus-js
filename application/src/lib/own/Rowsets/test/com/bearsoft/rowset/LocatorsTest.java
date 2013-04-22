/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import java.util.List;
import java.math.BigDecimal;
import com.bearsoft.rowset.utils.KeySet;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import java.util.ArrayList;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class LocatorsTest extends RowsetBaseTest {

    // 1 criterion locators tests
    protected static Object[] locatorsKeys = new Object[]{true, "sample string data22", null, null, new BigDecimal(345677.9898f), BigDecimal.valueOf(748677), "true string7"};
    protected static int[][] rowsetPositions = new int[][]{
        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
        {20, 21, 22},
        {2, 3},
        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22},
        {1, 2, 3, 4, 15, 16, 17, 18, 19, 20, 21, 22},
        {13},
        {7}
    };
    // multi criteria locator tests
    protected static Object[] locatorMultiKey = new Object[]{/*21L, */false, "sample string data22", new Date(millis + 18), null, new BigDecimal(345677.9898f), BigDecimal.valueOf(345677L), "true string22"};
    protected static int mkRowsetPosition = 21;

    @Test
    public void simpleLocator1CriteriaTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("simpleLocatorTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            for (Locator locator : rowset.getLocators()) {
                locator.validate();
            }
            Locator loc = rowset.createLocator();
            assertEquals(rowset.getLocators().length, i - 1);
            loc.beginConstrainting();
            loc.addConstraint(i);
            loc.endConstrainting();
            loc.build();
            KeySet ks = new KeySet();
            ks.add(locatorsKeys[i - 2]);
            loc.find(ks);
            assertEquals(rowsetPositions[i - 2].length, loc.getSize());
            assertEquals(testData.length, loc.getAllRowsVector().size());
            checkRowsetPositions(rowsetPositions[i - 2], loc);
            checkAllLocatorsAreValid(rowset);

            SortingCriterion sc1 = new SortingCriterion(i, false);
            List<SortingCriterion> criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            checkAllLocatorsAreInvalid(rowset);

            sc1 = new SortingCriterion(1, true);
            criteria = new ArrayList<>();
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
    }

    @Test
    public void simpleLocator1CriteriaWrongTypesTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("simpleLocator1CriteriaWrongTypesTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            for (Locator locator : rowset.getLocators()) {
                locator.validate();
            }
            Locator loc = rowset.createLocator();
            assertEquals(rowset.getLocators().length, i - 1);
            loc.beginConstrainting();
            loc.addConstraint(i);
            loc.endConstrainting();
            loc.build();
            Object keyValue = locatorsKeys[i - 2];
            if (keyValue instanceof Boolean) {
                Boolean bValue = (Boolean) keyValue;
                keyValue = bValue ? Long.valueOf(1) : Long.valueOf(1);
            }
            if (keyValue instanceof Date) {
                Date dValue = (Date) keyValue;
                keyValue = Long.valueOf(dValue.getTime());
            }
            loc.find(keyValue);
            assertEquals(rowsetPositions[i - 2].length, loc.getSize());
            assertEquals(testData.length, loc.getAllRowsVector().size());
            checkRowsetPositions(rowsetPositions[i - 2], loc);
            checkAllLocatorsAreValid(rowset);

            SortingCriterion sc1 = new SortingCriterion(i, false);
            List<SortingCriterion> criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            checkAllLocatorsAreInvalid(rowset);

            sc1 = new SortingCriterion(1, true);
            criteria = new ArrayList<>();
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
    }

    @Test
    public void simpleLocatorMultiCriteriaTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("simpleLocatorMultiCriteriaTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        Locator loc = rowset.createLocator();
        loc.beginConstrainting();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            loc.addConstraint(i);
        }
        loc.endConstrainting();
        assertEquals(rowset.getLocators().length, 1);
        assertTrue(loc.find(locatorMultiKey));
        assertTrue(loc.first());
        assertEquals(loc.getRowset().getCursorPos(), mkRowsetPosition);
        assertEquals(loc.getRowsetPos(1), mkRowsetPosition);
    }

    @Test
    public void crudLocatorTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("crudLocatorTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);

        Locator loc = rowset.createLocator();
        loc.beginConstrainting();
        for (int i = 2; i <= fields.getFieldsCount(); i++) {
            loc.addConstraint(i);
        }
        loc.endConstrainting();

        assertEquals(rowset.getLocators().length, 1);
        assertTrue(loc.find(locatorMultiKey));
        assertTrue(loc.first());
        assertEquals(loc.getRowset().getCursorPos(), mkRowsetPosition);
        assertEquals(loc.getRowsetPos(1), mkRowsetPosition);

        rowset.absolute(10);
        rowset.insert();
        rowset.updateObject(2, false);
        assertFalse(loc.isValid());
        assertTrue(loc.find(locatorMultiKey));
        assertTrue(loc.isValid());
        assertTrue(loc.first());
        assertEquals(loc.getRowset().getCursorPos(), mkRowsetPosition + 1);
        assertEquals(loc.getRowsetPos(1), mkRowsetPosition + 1);

        rowset.absolute(9);
        rowset.delete();
        assertFalse(loc.isValid());
        assertTrue(loc.find(locatorMultiKey));
        assertTrue(loc.isValid());
        assertTrue(loc.first());
        assertEquals(loc.getRowset().getCursorPos(), mkRowsetPosition);
        assertEquals(loc.getRowsetPos(1), mkRowsetPosition);

        rowset.updateObject(6, null);
        assertFalse(loc.isValid());
        assertFalse(loc.find(locatorMultiKey));
        assertTrue(loc.isValid());
        assertFalse(loc.first());
        assertEquals(loc.getSize(), 0);

        assertEquals(loc.getRowset().getCursorPos(), mkRowsetPosition);
        rowset.updateObject(6, 345677.9898f);
        assertEquals(loc.getRowset().getCursorPos(), mkRowsetPosition);

        assertFalse(loc.isValid());
        assertTrue(loc.find(locatorMultiKey));
        assertTrue(loc.isValid());
        assertTrue(loc.first());
        assertEquals(loc.getRowset().getCursorPos(), mkRowsetPosition);
        assertEquals(loc.getRowsetPos(1), mkRowsetPosition);

        // updating of non-locator field doesn't lead to invalidating of locator.
        assertTrue(loc.isValid());
        rowset.updateObject(1, 45);
        assertTrue(loc.isValid());
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

    protected void checkRowsetPositions(int[] aPositions, Locator aLocator) throws IllegalStateException, InvalidCursorPositionException {
        assertEquals(aPositions.length, aLocator.getSize());
        // first locator's scroll method
        aLocator.beforeFirst();
        int i = 0;
        while (aLocator.next()) {
            assertEquals(aLocator.getRowset().getCursorPos(), aPositions[i++]);
        }
        // second locator's scroll method
        aLocator.afterLast();
        i = aPositions.length - 1;
        while (aLocator.previous()) {
            assertEquals(aLocator.getRowset().getCursorPos(), aPositions[i--]);
        }
        // third locator's scroll method
        for (i = 0; i < aLocator.getSize(); i++) {
            aLocator.getRowset().absolute(aLocator.getRowsetPos(i + 1));
            assertEquals(aLocator.getRowset().getCursorPos(), aPositions[i]);
        }
        // third backward locator's scroll method
        for (i = aLocator.getSize() - 1; i >= 0; i--) {
            aLocator.getRowset().absolute(aLocator.getRowsetPos(i + 1));
            assertEquals(aLocator.getRowset().getCursorPos(), aPositions[i]);
        }
    }
}
