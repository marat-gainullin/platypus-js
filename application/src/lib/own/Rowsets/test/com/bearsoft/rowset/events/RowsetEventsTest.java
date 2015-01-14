/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetBaseTest;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class RowsetEventsTest extends RowsetBaseTest {

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
        for (EventsReciver eventsReciver : eventsRecivers) {
            aRowset.removeRowsetListener(eventsReciver);
        }
        recreateRecivers();
        for (EventsReciver eventsReciver : eventsRecivers) {
            aRowset.addRowsetListener(eventsReciver);
        }
    }

    protected void checkWillScroll(int aWillScroll) {
        for (EventsReciver eventsReciver : eventsRecivers) {
            assertEquals(aWillScroll, eventsReciver.willScroll);
        }
    }

    protected void checkScrolled(int aScrolled) {
        for (EventsReciver eventsReciver : eventsRecivers) {
            assertEquals(aScrolled, eventsReciver.scrolled);
        }
    }

    @Test
    public void eventsTestScroll1forward() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        Rowset rowset = initRowset();
        System.out.println("eventsTestScroll1forward");
        reRegisterRecivers(rowset);
        int scrolled = 0;
        rowset.setCursorPos(1);
        while (rowset.getCursorPos() < rowset.size() + 1) {
            scrolled++;
            rowset.setCursorPos(rowset.getCursorPos() + 1);
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkScrolled(scrolled + 1);
        assertFalse(rowset.setCursorPos(rowset.getCursorPos() + 1));
        assertFalse(rowset.setCursorPos(rowset.getCursorPos() + 1));
        assertTrue(rowset.getCursorPos() == rowset.size() + 1);
        assertTrue(rowset.setCursorPos(rowset.size() + 1));
        assertTrue(rowset.setCursorPos(rowset.size() + 1));
        checkScrolled(scrolled + 1);
    }

    @Test
    public void eventsTestScroll1backward() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("eventsTestScroll1backward");
        Rowset rowset = initRowset();
        rowset.setCursorPos(1);
        reRegisterRecivers(rowset);
        int scrolled = 0;
        rowset.setCursorPos(rowset.size());
        while (rowset.getCursorPos() != 0) {
            scrolled++;
            rowset.setCursorPos(rowset.getCursorPos() - 1);
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkScrolled(scrolled + 1);
        assertFalse(rowset.setCursorPos(rowset.getCursorPos() - 1));
        assertFalse(rowset.setCursorPos(rowset.getCursorPos() - 1));
        assertTrue(rowset.getCursorPos() == 0);
        assertTrue(rowset.setCursorPos(0));
        assertTrue(rowset.setCursorPos(0));
        checkScrolled(scrolled + 1);
    }

    @Test
    public void eventsTestDeniedReciverStabilityforward() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("eventsTestDeniedReciverStabilityforward");
        Rowset rowset = initRowset();
        rowset.setCursorPos(0);
        reRegisterRecivers(rowset);
        eventsRecivers[45].allowScroll = false;
        int scrolled = 0;
        for (int i = 1; i <= rowset.size(); i++) {
            rowset.setCursorPos(i);
            scrolled++;
        }
        assertEquals(scrolled, testData.length);
        // stability test
        checkWillScroll(scrolled);
        assertFalse(rowset.setCursorPos(rowset.getCursorPos() + 1)); // next returns false, but scrolls the rowset from last to after last position
        checkWillScroll(scrolled + 1);
        assertFalse(rowset.setCursorPos(rowset.getCursorPos() + 1));
        assertTrue(rowset.getCursorPos() == 0);
        assertFalse(rowset.setCursorPos(rowset.size() + 1));
        assertFalse(rowset.setCursorPos(rowset.size() + 1));
        checkWillScroll(scrolled + 4);
        assertTrue(rowset.getCursorPos() == 0);
    }

    @Test
    public void eventsTestDeniedInsert() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("eventsTestDeniedInsert");
        Rowset rowset = initRowset();
        reRegisterRecivers(rowset);
        eventsRecivers[63].allowInsert = false;
        rowset.deleteAll();
        assertTrue(rowset.isEmpty());
        fillInRowset(rowset);
        assertTrue(rowset.isEmpty());
    }

    @Test
    public void eventsTestDeniedDelete() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("eventsTestDeniedDelete");

        int reciver2Test = 84;
        Rowset rowset = initRowset();
        reRegisterRecivers(rowset);
        eventsRecivers[reciver2Test].allowDelete = false;
        rowset.deleteAll();
        checkRowsetCorrespondToTestData(rowset);

        eventsRecivers[reciver2Test].allowDelete = true;
        rowset.deleteAll();
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        checkRowsetCorrespondToTestData(rowset);
        eventsRecivers[reciver2Test].allowDelete = false;
        rowset.setCursorPos(0);
        rowset.deleteAll();
        assertTrue(rowset.getCursorPos() == 0);
        checkRowsetCorrespondToTestData(rowset);

        eventsRecivers[reciver2Test].allowDelete = true;
        rowset.deleteAll();
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        checkRowsetCorrespondToTestData(rowset);
        eventsRecivers[reciver2Test].allowDelete = false;
        rowset.setCursorPos(rowset.size() + 1);
        rowset.deleteAll();
        assertTrue(rowset.getCursorPos() == rowset.size() + 1);
        checkRowsetCorrespondToTestData(rowset);

        eventsRecivers[reciver2Test].allowDelete = true;
        rowset.deleteAll();
        fillInRowset(rowset);
        assertFalse(rowset.isEmpty());
        checkRowsetCorrespondToTestData(rowset);
        eventsRecivers[reciver2Test].allowDelete = false;
        rowset.setCursorPos(15);
        rowset.deleteAll();
        assertEquals(rowset.getCursorPos(), 15);
        checkRowsetCorrespondToTestData(rowset);
    }
}
