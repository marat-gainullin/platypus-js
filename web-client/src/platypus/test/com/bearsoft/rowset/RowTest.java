/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import java.util.Arrays;
import java.util.Set;

import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;

/**
 *
 * @author mg
 */
public class RowTest extends RowsetBaseTest{

    static final Object[] testValues = {"sample string # 1", null, new java.util.Date(), 18.0d, 18.98d, true, false, null};
    static final Object[] nullValues = {null, null, null, null, null, null, null, null};
    static final int[] cols2Update = new int[]{1, 2, 4, 7};
    static final int[] colsUnchanged = new int[]{3, 5, 6, 8};
    static final Fields testFields = new Fields();

    static {
        testFields.add(new Field("strID", "", DataTypeInfo.VARCHAR));
        testFields.add(new Field("someData", "", DataTypeInfo.VARCHAR));
        testFields.add(new Field("moment", "", DataTypeInfo.TIMESTAMP));
        testFields.add(new Field("counter", "", DataTypeInfo.INTEGER));
        testFields.add(new Field("value", "", DataTypeInfo.FLOAT));
        testFields.add(new Field("flag1", "", DataTypeInfo.BOOLEAN));
        testFields.add(new Field("flag2", "", DataTypeInfo.BIT));
        testFields.add(new Field("someData1", "", DataTypeInfo.VARCHAR));
    }

    public RowTest() {
    }

    /**
     * Test of currentToOriginal method, of class Row.
     */
    public void testMoveCurrentToOriginal() throws Exception {
        System.out.println("moveCurrentToOriginal");
        moveCurrentToOriginalImpl();
    }

    protected void moveCurrentToOriginalImpl() throws Exception {
        Row instance = new Row(testFields);
        for (int i = 0; i < testValues.length; i++) {
            instance.setColumnObject(i + 1, testValues[i]);
        }
        Object[] current = instance.getCurrentValues();
        Object[] original = instance.getOriginalValues();
        assertTrue(current != null);
        assertTrue(original != null);
        assertTrue(current.length == original.length);
        assertFalse(Arrays.equals(current, original));
        assertTrue(instance.isUpdated());
        instance.currentToOriginal();
        current = instance.getCurrentValues();
        original = instance.getOriginalValues();
        assertTrue(current != null);
        assertTrue(original != null);
        assertTrue(current.length == original.length);
        assertTrue(Arrays.equals(current, original));
        assertFalse(instance.isUpdated());
    }

    /**
     * Test of originalToCurrent method, of class Row.
     */
    public void testMoveOriginalToCurrent() throws Exception {
        System.out.println("moveOriginalToCurrent");
        Row instance = new Row(testFields);
        for (int i = 0; i < testValues.length; i++) {
            instance.setColumnObject(i + 1, testValues[i]);
        }
        Object[] current = instance.getCurrentValues();
        Object[] original = instance.getOriginalValues();
        assertTrue(current != null);
        assertTrue(original != null);
        assertTrue(current.length == original.length);
        assertFalse(Arrays.equals(current, original));
        instance.originalToCurrent();
        current = instance.getCurrentValues();
        original = instance.getOriginalValues();
        assertTrue(current != null);
        assertTrue(original != null);
        assertTrue(current.length == original.length);
        assertTrue(Arrays.equals(current, original));
    }

    /**
     * Test of getColumnCount method, of class Row.
     */
    public void testGetColumnCount() {
        System.out.println("getColumnCount");
        Row instance = new Row(testFields);
        int expResult = testValues.length;
        int result = instance.getColumnCount();
        assertEquals(expResult, result);
    }

    public void testColIndexException() throws Exception {
        System.out.println("testColIndexException");
        Row instance = new Row(testFields);
        int expResult = testValues.length;
        int result = instance.getColumnCount();
        assertEquals(expResult, result);
        try {
            instance.setColumnObject(0, 987);
        } catch (Exception ex) {
            assertTrue(ex instanceof InvalidColIndexException);
        }
        try {
            instance.setColumnObject(testValues.length + 1, 987);
        } catch (Exception ex) {
            assertTrue(ex instanceof InvalidColIndexException);
        }
        instance.setColumnObject(1, 987);
        instance.setColumnObject(testValues.length, 987);
    }

    /**
     * Test of setColumnObject method, of class Row.
     */
    public void testSetColumnObject() throws Exception {
        System.out.println("setColumnObject");
        Row instance = new Row(testFields);
        for (int colIndex = 1; colIndex <= testValues.length; colIndex++) {
            Object colValue = testValues[colIndex - 1];
            instance.setColumnObject(colIndex, colValue);
            Object seemsToBeColValue = instance.getColumnObject(colIndex);
            assertEquals(colValue, seemsToBeColValue);
        }
    }

    /**
     * Test of getOriginalValues method, of class Row.
     */
    public void testGetOriginalValues() throws Exception {
        System.out.println("getOriginalValues");
        Row instance = new Row(testFields);
        for (int colIndex = 1; colIndex <= testValues.length; colIndex++) {
            Object colValue = testValues[colIndex - 1];
            instance.setColumnObject(colIndex, colValue);
            Object seemsToBeColValue = instance.getColumnObject(colIndex);
            assertEquals(colValue, seemsToBeColValue);
        }
        Object[] original = instance.getOriginalValues();
        assertTrue(original.length == testValues.length);
        assertTrue(Arrays.equals(original, nullValues));
        instance.currentToOriginal();
        original = instance.getOriginalValues();
        assertTrue(original.length == testValues.length);
        assertTrue(Arrays.equals(original, testValues));
    }

    /**
     * Test of getCurrentValues method, of class Row.
     */
    public void testGetCurrentValues() throws Exception {
        System.out.println("getCurrentValues");
        Row instance = new Row(testFields);
        for (int colIndex = 1; colIndex <= testValues.length; colIndex++) {
            Object colValue = testValues[colIndex - 1];
            instance.setColumnObject(colIndex, colValue);
            Object seemsToBeColValue = instance.getColumnObject(colIndex);
            assertEquals(colValue, seemsToBeColValue);
        }
        Object[] current = instance.getCurrentValues();
        assertTrue(current.length == testValues.length);
        assertTrue(Arrays.equals(current, testValues));
        instance.originalToCurrent();
        current = instance.getCurrentValues();
        assertTrue(current.length == testValues.length);
        assertTrue(Arrays.equals(current, nullValues));
    }

    public void testDeleted() throws Exception {
        System.out.println("deleted flag");
        Row instance = new Row(testFields);
        for (int colIndex = 1; colIndex <= testValues.length; colIndex++) {
            Object colValue = testValues[colIndex - 1];
            instance.setColumnObject(colIndex, colValue);
            Object seemsToBeColValue = instance.getColumnObject(colIndex);
            assertEquals(colValue, seemsToBeColValue);
        }
        instance.setDeleted();
        assertTrue(instance.isDeleted());
        instance.clearDeleted();
        assertFalse(instance.isDeleted());
        instance.setDeleted();
        assertTrue(instance.isDeleted());
        instance.clearDeleted();
        assertFalse(instance.isDeleted());
    }

    public void testUpdated() throws Exception {
        System.out.println("updated flag");
        moveCurrentToOriginalImpl();
    }

    public void testColumnUpdated() throws Exception {
        System.out.println("testColumnUpdated");
        Row instance = new Row(testFields);
        for (int i = 0; i < cols2Update.length; i++) {
            instance.setColumnObject(cols2Update[i], testValues[cols2Update[i]]);
        }
        Object[] current = instance.getCurrentValues();
        Object[] original = instance.getOriginalValues();
        assertTrue(current != null);
        assertTrue(original != null);
        assertTrue(current.length == original.length);
        assertFalse(Arrays.equals(current, original));
        assertTrue(instance.isUpdated());
        for (int i = 0; i < cols2Update.length; i++) {
            if (cols2Update[i] == 1 || cols2Update[i] == 7)// nul is updated by null, so nothing changed
            {
                assertFalse(instance.isColumnUpdated(cols2Update[i]));
            } else {
                assertTrue(instance.isColumnUpdated(cols2Update[i]));
            }
        }
        for (int i = 0; i < colsUnchanged.length; i++) {
            assertFalse(instance.isColumnUpdated(colsUnchanged[i]));
        }
        instance.currentToOriginal();
        current = instance.getCurrentValues();
        original = instance.getOriginalValues();
        assertTrue(current != null);
        assertTrue(original != null);
        assertTrue(current.length == original.length);
        assertTrue(Arrays.equals(current, original));
        assertFalse(instance.isUpdated());
        for (int i = 0; i < cols2Update.length; i++) {
            assertFalse(instance.isColumnUpdated(cols2Update[i]));
        }
        for (int i = 0; i < colsUnchanged.length; i++) {
            assertFalse(instance.isColumnUpdated(colsUnchanged[i]));
        }
    }

    public void testGetUpdatedColumns() throws Exception {
        System.out.println("testGetUpdatedColumns");
        Row instance = new Row(testFields);
        for (int i = 0; i < cols2Update.length; i++) {
            instance.setColumnObject(cols2Update[i], testValues[cols2Update[i]]);
        }
        Object[] current = instance.getCurrentValues();
        Object[] original = instance.getOriginalValues();
        assertTrue(current != null);
        assertTrue(original != null);
        assertTrue(current.length == original.length);
        assertFalse(Arrays.equals(current, original));
        assertTrue(instance.isUpdated());
        Set<Integer> updSet = instance.getUpdatedColumns();
        assertEquals(updSet.size(), cols2Update.length - 2/*1, 7 were nulls in both default vaues and new values and so, they were not updated*/);
        for (int i = 0; i < cols2Update.length; i++) {
            if (cols2Update[i] == 1 || cols2Update[i] == 7) {
                assertFalse(updSet.contains(cols2Update[i]));
            } else {
                assertTrue(updSet.contains(cols2Update[i]));
            }
        }
        instance.currentToOriginal();
        updSet = instance.getUpdatedColumns();
        assertTrue(updSet.isEmpty());
    }
}
