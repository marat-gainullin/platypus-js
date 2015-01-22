package com.bearsoft.rowset;

import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Марат
 */
public class RowsetTest extends RowsetBaseTest{


    public RowsetTest() {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testInsert() throws Exception {
        System.out.println("testInsert");
        Rowset rowset = initRowset();
        assertFalse(rowset.isEmpty());
        // test if rowset's data correspond to data from testData array.
        checkRowsetCorrespondToTestData(rowset);
    }

    @Test
    public void testScroll1forward() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("testScroll1forward");
        Rowset rowset = initRowset();
        assertFalse(rowset.isEmpty());
        int scrolled = 0;
        rowset.setCursorPos(1);
        while (rowset.getCursorPos() < rowset.size() + 1) {
            scrolled++;
            rowset.setCursorPos(rowset.getCursorPos()+1);
        }
        assertEquals(scrolled, testData.length);
        assertFalse(rowset.setCursorPos(rowset.getCursorPos()+1));
        assertFalse(rowset.setCursorPos(rowset.getCursorPos()+1));
        assertTrue(rowset.getCursorPos() == rowset.size() + 1);
        assertTrue(rowset.setCursorPos(rowset.size() + 1));
    }

    @Test
    public void testScroll1backward() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("testScroll1backward");
        Rowset rowset = initRowset();
        assertFalse(rowset.isEmpty());
        int scrolled = 0;
        rowset.setCursorPos(rowset.size());
        while (rowset.getCursorPos() > 0) {
            scrolled++;
            rowset.setCursorPos(rowset.getCursorPos() - 1);
        }
        assertEquals(scrolled, testData.length);
        assertFalse(rowset.setCursorPos(rowset.getCursorPos() - 1));
        assertFalse(rowset.setCursorPos(rowset.getCursorPos() - 1));
        assertTrue(rowset.getCursorPos() == 0);
        assertTrue(rowset.setCursorPos(0));
    }
    
    @Test
    public void testDeleteAllRows() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("testDeleteAllRows");
        Rowset rowset = initRowset();
        assertFalse(rowset.isEmpty());
        assertEquals(rowset.size(), testData.length);
        rowset.deleteAll();
        assertTrue(rowset.isEmpty());
        rowset.originalToCurrent();
        assertFalse(rowset.isEmpty());
        rowset.deleteAll();
        assertTrue(rowset.isEmpty());
        rowset.currentToOriginal();
        assertTrue(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
    }

    @Test
    public void testDeletePartial() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("testDeletePartial");
        Rowset rowset = initRowset();
        assertFalse(rowset.isEmpty());
        assertEquals(rowset.size(), testData.length);
        int startPos = 10;
        int toDelete = 4;
        rowset.setCursorPos(startPos);
        for (int i = 0; i < toDelete; i++) {
            rowset.deleteAt(rowset.getCursorPos());
        }
        assertEquals(rowset.size(), testData.length - toDelete);
        assertEquals(rowset.getCursorPos(), startPos);

        toDelete = 10;
        for (int i = 0; i < toDelete; i++) {
            rowset.deleteAt(rowset.getCursorPos());
        }
        assertEquals(rowset.getCursorPos(), startPos-2);

        rowset.deleteAt(rowset.getCursorPos());
        assertEquals(rowset.getCursorPos(), startPos-3);
        rowset.deleteAt(rowset.getCursorPos());
        assertEquals(rowset.getCursorPos(), startPos-4);
        rowset.deleteAt(rowset.getCursorPos());
        assertEquals(rowset.getCursorPos(), startPos-5);
        rowset.deleteAt(rowset.getCursorPos());
        assertEquals(rowset.getCursorPos(), startPos-6);
        rowset.deleteAt(rowset.getCursorPos());
        assertEquals(rowset.getCursorPos(), startPos-7);
        rowset.deleteAt(rowset.getCursorPos());
        assertEquals(rowset.getCursorPos(), startPos-8);
        rowset.deleteAt(rowset.getCursorPos());
        assertEquals(rowset.getCursorPos(), startPos-9);
        assertEquals(rowset.size(), 1);
        rowset.deleteAt(rowset.getCursorPos());
        assertTrue(rowset.isEmpty());
        assertTrue(rowset.getCursorPos() == 0);
    }

    @Test
    public void testUpdate() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("testUpdate");
        Rowset rowset = initRowset();
        assertFalse(rowset.isEmpty());
        // update some data
        int fieldFromUpdate = 8;
        int field2Update = 3;
        rowset.setCursorPos(0);
        int recIndex = 0;
        while (rowset.setCursorPos(rowset.getCursorPos() + 1)) {
            rowset.getCurrentRow().setColumnObject(field2Update, testData[recIndex][fieldFromUpdate - 1]);
            recIndex++;
        }
        // check if data is updated
        for (int i = 1; i <= testData.length; i++) {
            rowset.setCursorPos(i);
            Object lValue = rowset.getCurrentRow().getColumnObject(field2Update);
            assertEquals(testData[i-1][fieldFromUpdate - 1], lValue);
        }
        // cancel updated data
        rowset.originalToCurrent();
        checkRowsetCorrespondToTestData(rowset);

        // update some data again
        rowset.setCursorPos(0);
        recIndex = 0;
        while (rowset.setCursorPos(rowset.getCursorPos() + 1)) {
            rowset.getCurrentRow().setColumnObject(field2Update, testData[recIndex][fieldFromUpdate - 1]);
            recIndex++;
        }
        // check if data is updated
        for (int i = 1; i <= testData.length; i++) {
            rowset.setCursorPos(i);
            Object lValue = rowset.getCurrentRow().getColumnObject(field2Update);
            assertEquals(lValue, testData[i-1][fieldFromUpdate - 1]);
        }
        // check if data is updated
        for (int i = 1; i <= testData.length; i++) {
            rowset.setCursorPos(i);
            Object lValue = rowset.getCurrentRow().getColumnObject(field2Update);
            assertEquals(lValue, testData[i-1][fieldFromUpdate - 1]);
        }
    }

    @Test
    public void unconvertableUpdateTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("unconvertableUpdateTest");
        Rowset rowset = initRowset();
        assertFalse(rowset.isEmpty());
        // update some data
        int fieldFromUpdate = 4;
        int field2Update = 2;
        rowset.setCursorPos(0);
        int recIndex = 0;
        while (rowset.setCursorPos(rowset.getCursorPos() + 1)) {
            rowset.getCurrentRow().setColumnObject(field2Update, testData[recIndex][fieldFromUpdate - 1]);
            recIndex++;
        }
        // check if data is not updated, because of incompatible values types
        for (int i = 1; i <= testData.length; i++) {
            rowset.setCursorPos(i);
            Object lValue = rowset.getCurrentRow().getColumnObject(field2Update);
            Object etalonData = testData[i-1][fieldFromUpdate - 1];
            if(etalonData != null || lValue != null)
                assertNotSame(etalonData, lValue);
        }
    }
}
