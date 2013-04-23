/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import java.math.BigDecimal;

import com.bearsoft.rowset.DataRowsetBaseTest;
import com.bearsoft.rowset.Rowset;

/**
 *
 * @author mg
 */
public class RowsetEventsTest extends DataRowsetBaseTest {

    protected static final int eventsReciversCount = 100;
    protected static EventsReciver[] eventsRecivers = new EventsReciver[eventsReciversCount];

    static {
        recreateRecivers();
    }

    private static void recreateRecivers() {
        for (int i = 0; i < eventsRecivers.length; i++) {
            eventsRecivers[i] = new EventsReciver();
        }
    }

    protected void reRegisterRecivers(Rowset aRowset) {
        for (int i = 0; i < eventsRecivers.length; i++) {
            aRowset.removeRowsetListener(eventsRecivers[i]);
        }
        recreateRecivers();
        for (int i = 0; i < eventsRecivers.length; i++) {
            aRowset.addRowsetListener(eventsRecivers[i]);
        }
    }

    protected void checkWillScroll(int aWillScroll) {
        for (int i = 0; i < eventsRecivers.length; i++) {
            assertEquals(aWillScroll, eventsRecivers[i].willScroll);
        }
    }

    protected void checkScrolled(int aScrolled) {
        for (int i = 0; i < eventsRecivers.length; i++) {
            assertEquals(aScrolled, eventsRecivers[i].scrolled);
        }
    }

    public void testEventsTestScroll1forward() throws Exception {
        Rowset rowset = new Rowset(fields);
        System.out.println("eventsTestScroll1forward");
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        reRegisterRecivers(rowset);
        int scrolled = 0;
        rowset.first();
        while (!rowset.isAfterLast()) {
            scrolled++;
            rowset.next();
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkScrolled(scrolled + 1);
        assertFalse(rowset.next());
        assertFalse(rowset.next());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
        checkScrolled(scrolled + 1);
    }

    public void testEventsTestScroll2forward() throws Exception {
        System.out.println("eventsTestScroll2forward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        reRegisterRecivers(rowset);
        int scrolled = 0;
        rowset.beforeFirst();
        while (rowset.next()) {
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkScrolled(scrolled + 2);
        assertFalse(rowset.next());
        assertFalse(rowset.next());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
        checkScrolled(scrolled + 2);
    }

    public void testEventsTestScroll3forward() throws Exception {
        System.out.println("eventsTestScroll3forward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        reRegisterRecivers(rowset);
        int scrolled = 0;
        for (int i = 1; i <= rowset.size(); i++) {
            rowset.absolute(i);
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkScrolled(scrolled);
        assertFalse(rowset.next()); // next returns false, but scrolls the rowset from last to after last position
        checkScrolled(scrolled + 1);
        assertFalse(rowset.next());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.isAfterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
        assertTrue(rowset.afterLast());
        checkScrolled(scrolled + 1);
    }

    public void testEventsTestScroll1backward() throws Exception {
        System.out.println("eventsTestScroll1backward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.first();
        reRegisterRecivers(rowset);
        int scrolled = 0;
        rowset.last();
        while (!rowset.isBeforeFirst()) {
            scrolled++;
            rowset.previous();
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkScrolled(scrolled + 1);
        assertFalse(rowset.previous());
        assertFalse(rowset.previous());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
        checkScrolled(scrolled + 1);
    }

    public void testEventsTestScroll2backward() throws Exception {
        System.out.println("eventsTestScroll2backward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        reRegisterRecivers(rowset);
        int scrolled = 0;
        rowset.afterLast();
        while (rowset.previous()) {
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkScrolled(scrolled + 2);
        assertFalse(rowset.previous());
        assertFalse(rowset.previous());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
        checkScrolled(scrolled + 2);
    }

    public void testEventsTestScroll3backward() throws Exception {
        System.out.println("eventsTestScroll3backward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.beforeFirst();
        reRegisterRecivers(rowset);
        int scrolled = 0;
        for (int i = rowset.size(); i >= 1; i--) {
            rowset.absolute(i);
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkScrolled(scrolled);
        assertFalse(rowset.previous());
        checkScrolled(scrolled + 1);
        assertFalse(rowset.previous());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
        assertTrue(rowset.beforeFirst());
        checkScrolled(scrolled + 1);
    }

    public void testEventsTestDeniedReciverStabilityforward() throws Exception {
        System.out.println("eventsTestDeniedReciverStabilityforward");
        Rowset rowset = new Rowset(fields);
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.originalToCurrent();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        rowset.beforeFirst();
        reRegisterRecivers(rowset);
        eventsRecivers[45].allowScroll = false;
        int scrolled = 0;
        for (int i = 1; i <= rowset.size(); i++) {
            rowset.absolute(i);
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkWillScroll(scrolled);
        assertFalse(rowset.next()); // next returns false, but scrolls the rowset from last to after last position
        checkWillScroll(scrolled + 1);
        assertFalse(rowset.next());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.isBeforeFirst());
        assertTrue(!rowset.afterLast());
        assertTrue(!rowset.afterLast());
        assertTrue(!rowset.afterLast());
        checkWillScroll(scrolled + 5);
        assertTrue(rowset.isBeforeFirst());
        assertTrue(rowset.isBeforeFirst());
    }

    public void testEventsTestDeniedInsert() throws Exception {
        System.out.println("eventsTestDeniedInsert");
        Rowset rowset = new Rowset(fields);
        reRegisterRecivers(rowset);
        eventsRecivers[63].allowInsert = false;
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertTrue(rowset.isEmpty());
    }

    public void testEventsTestDeniedUpdate() throws Exception {
        System.out.println("eventsTestDeniedUpdate");
        Rowset rowset = new Rowset(fields);
        reRegisterRecivers(rowset);
        eventsRecivers[14].allowChange = false;
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        for (int i = 1; i <= rowset.size(); i++) {
            rowset.absolute(i);
            for (int j = 2; j <= fields.getFieldsCount(); j++) {
                assertEquals(rowset.getObject(j), null);
            }
        }
        rowset.absolute(10);
        Object oldPkValue = rowset.getObject(1);
        rowset.updateObject(1, 444444);
        assertEquals(oldPkValue, rowset.getObject(1));
        eventsRecivers[14].allowChange = true;
        rowset.deleteAll();
        fillInRowset(rowset);
        checkRowsetCorrespondToTestData(rowset);
        rowset.absolute(10);
        oldPkValue = rowset.getObject(1);
        rowset.updateObject(1, 444444);
        assertEquals(new Double(444444), rowset.getObject(1));
    }

    public void testEventsTestDeniedDelete() throws Exception {
        System.out.println("eventsTestDeniedDelete");

        int reciver2Test = 84;
        Rowset rowset = new Rowset(fields);
        reRegisterRecivers(rowset);
        eventsRecivers[reciver2Test].allowDelete = false;
        // initial filling tests
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        checkRowsetCorrespondToTestData(rowset);
        rowset.deleteAll();
        checkRowsetCorrespondToTestData(rowset);

        eventsRecivers[reciver2Test].allowDelete = true;
        rowset.deleteAll();
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        checkRowsetCorrespondToTestData(rowset);
        eventsRecivers[reciver2Test].allowDelete = false;
        rowset.beforeFirst();
        rowset.deleteAll();
        assertTrue(rowset.isBeforeFirst());
        checkRowsetCorrespondToTestData(rowset);

        eventsRecivers[reciver2Test].allowDelete = true;
        rowset.deleteAll();
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        checkRowsetCorrespondToTestData(rowset);
        eventsRecivers[reciver2Test].allowDelete = false;
        rowset.afterLast();
        rowset.deleteAll();
        assertTrue(rowset.isAfterLast());
        checkRowsetCorrespondToTestData(rowset);

        eventsRecivers[reciver2Test].allowDelete = true;
        rowset.deleteAll();
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        checkRowsetCorrespondToTestData(rowset);
        eventsRecivers[reciver2Test].allowDelete = false;
        rowset.absolute(15);
        rowset.deleteAll();
        assertEquals(rowset.getCursorPos(), 15);
        checkRowsetCorrespondToTestData(rowset);
    }
}
