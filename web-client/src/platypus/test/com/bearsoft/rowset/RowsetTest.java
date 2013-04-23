package com.bearsoft.rowset;


/**
 *
 * @author mg
 */
public class RowsetTest extends DataRowsetBaseTest{


    public RowsetTest() {
    }

    /**
     * Test of first method, of class Rowset.
     */
    public void testInsert() throws Exception {
        System.out.println("testInsert");
        Rowset rowset = new Rowset(fields);
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
        rowset.originalToCurrent();
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertFalse(rowset.isEmpty());
        rowset.currentToOriginal();
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertFalse(rowset.isEmpty());
        // test if rowset's data correspond to data from testData array.
        checkRowsetCorrespondToTestData(rowset);
    }

    public void testScroll1forward() throws Exception {
        System.out.println("testScroll1forward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        int scrolled = 0;
        rowset.first();
        while (!rowset.isAfterLast()) {
            scrolled++;
            rowset.next();
        }
        assertEquals(scrolled, testData.length);
        assertFalse(rowset.next());
        assertFalse(rowset.next());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
    }

    public void testScroll2forward() throws Exception {
        System.out.println("testScroll2forward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        int scrolled = 0;
        rowset.beforeFirst();
        while (rowset.next()) {
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        assertFalse(rowset.next());
        assertFalse(rowset.next());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
    }

    public void testScroll3forward() throws Exception {
        System.out.println("testScroll3forward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        int scrolled = 0;
        for (int i = 1; i <= rowset.size(); i++) {
            rowset.absolute(i);
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        assertFalse(rowset.next());
        assertFalse(rowset.next());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
    }

    public void testScroll1backward() throws Exception {
        System.out.println("testScroll1backward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        int scrolled = 0;
        rowset.last();
        while (!rowset.isBeforeFirst()) {
            scrolled++;
            rowset.previous();
        }
        assertEquals(scrolled, testData.length);
        assertFalse(rowset.previous());
        assertFalse(rowset.previous());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
    }

    public void testScroll2backward() throws Exception {
        System.out.println("testScroll2backward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        int scrolled = 0;
        rowset.afterLast();
        while (rowset.previous()) {
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        assertFalse(rowset.previous());
        assertFalse(rowset.previous());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
    }

    public void testScroll3backward() throws Exception {
        System.out.println("testScroll3backward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        int scrolled = 0;
        for (int i = rowset.size(); i >= 1; i--) {
            rowset.absolute(i);
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        assertFalse(rowset.previous());
        assertFalse(rowset.previous());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
    }

    public void testDeleteAllRows() throws Exception {
        System.out.println("testDeleteAllRows");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        assertEquals(rowset.size(), testData.length);
        rowset.currentToOriginal();
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

    public void testDeletePartial() throws Exception {
        System.out.println("testDeletePartial");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        assertEquals(rowset.size(), testData.length);
        rowset.currentToOriginal();
        int startPos = 10;
        int toDelete = 4;
        rowset.absolute(startPos);
        for (int i = 0; i < toDelete; i++) {
            rowset.delete();
        }
        assertEquals(rowset.size(), testData.length - toDelete);
        assertEquals(rowset.getCursorPos(), startPos);

        toDelete = 10;
        for (int i = 0; i < toDelete; i++) {
            rowset.delete();
        }
        assertEquals(rowset.getCursorPos(), startPos-2);

        rowset.delete();
        assertEquals(rowset.getCursorPos(), startPos-3);
        rowset.delete();
        assertEquals(rowset.getCursorPos(), startPos-4);
        rowset.delete();
        assertEquals(rowset.getCursorPos(), startPos-5);
        rowset.delete();
        assertEquals(rowset.getCursorPos(), startPos-6);
        rowset.delete();
        assertEquals(rowset.getCursorPos(), startPos-7);
        rowset.delete();
        assertEquals(rowset.getCursorPos(), startPos-8);
        rowset.delete();
        assertEquals(rowset.getCursorPos(), startPos-9);
        assertEquals(rowset.size(), 1);
        rowset.delete();
        assertTrue(rowset.isEmpty());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.isAfterLast());
    }

    public void testUpdate() throws Exception {
        System.out.println("testUpdate");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        // apply filling rowset
        rowset.currentToOriginal();
        // update some data
        int fieldFromUpdate = 8;
        int field2Update = 3;
        rowset.beforeFirst();
        int recIndex = 0;
        while (rowset.next()) {
            rowset.updateObject(field2Update, testData[recIndex][fieldFromUpdate - 1]);
            recIndex++;
        }
        // check if data is updated
        for (int i = 1; i <= testData.length; i++) {
            rowset.absolute(i);
            Object lValue = rowset.getObject(field2Update);
            assertEquals(testData[i-1][fieldFromUpdate - 1], lValue);
        }
        // cancel updated data
        rowset.originalToCurrent();
        checkRowsetCorrespondToTestData(rowset);

        // update some data again
        rowset.beforeFirst();
        recIndex = 0;
        while (rowset.next()) {
            rowset.updateObject(field2Update, testData[recIndex][fieldFromUpdate - 1]);
            recIndex++;
        }
        // check if data is updated
        for (int i = 1; i <= testData.length; i++) {
            rowset.absolute(i);
            Object lValue = rowset.getObject(field2Update);
            assertEquals(lValue, testData[i-1][fieldFromUpdate - 1]);
        }
        rowset.setShowOriginal(true);
        try {
            checkRowsetCorrespondToTestData(rowset);
        } finally {
            rowset.setShowOriginal(false);
        }
        // check if data is updated
        for (int i = 1; i <= testData.length; i++) {
            rowset.absolute(i);
            Object lValue = rowset.getObject(field2Update);
            assertEquals(lValue, testData[i-1][fieldFromUpdate - 1]);
        }
        // apply updated data
        rowset.currentToOriginal();
        // check if data is updated
        rowset.setShowOriginal(true);
        try {
            for (int i = 1; i <= testData.length; i++) {
                rowset.absolute(i);
                Object lValue = rowset.getObject(field2Update);
                assertEquals(lValue, testData[i-1][fieldFromUpdate - 1]);
            }
        } finally {
            rowset.setShowOriginal(false);
        }
    }

    public void unconvertableUpdateTest() throws Exception {
        System.out.println("unconvertableUpdateTest");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        // apply filling rowset
        rowset.currentToOriginal();
        // update some data
        int fieldFromUpdate = 4;
        int field2Update = 2;
        rowset.beforeFirst();
        int recIndex = 0;
        while (rowset.next()) {
            rowset.updateObject(field2Update, testData[recIndex][fieldFromUpdate - 1]);
            recIndex++;
        }
        // check if data is not updated, because of incompatible values types
        for (int i = 1; i <= testData.length; i++) {
            rowset.absolute(i);
            Object lValue = rowset.getObject(field2Update);
            Object etalonData = testData[i-1][fieldFromUpdate - 1];
            if(etalonData != null || lValue != null)
                assertNotSame(etalonData, lValue);
        }
    }
}
