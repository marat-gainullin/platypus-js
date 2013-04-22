/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import java.util.List;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class SortingTest extends RowsetBaseTest {

    protected static long[][] pkSequencesAsc = new long[][]{
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L},
        {6L, 15L, 16L, 17L, 18L, 19L, 3L, 4L, 2L, 7L, 1L, 5L, 20L, 21L, 22L, 8L, 9L, 10L, 11L, 12L, 13L, 14L},
        {2L, 3L, 1L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 1L, 2L, 3L, 4L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 21L, 22L, 11L, 12L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 13L},
        {13L, 1L, 10L, 11L, 12L, 14L, 15L, 16L, 17L, 18L, 19L, 2L, 20L, 21L, 22L, 3L, 4L, 5L, 6L, 7L, 8L, 9L}
    };
    protected static long[][] pkSequencesDesc = new long[][]{
        {22L, 21L, 20L, 19L, 18L, 17L, 16L, 15L, 14L, 13L, 12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L},
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {14L, 13L, 12L, 11L, 10L, 9L, 8L, 20L, 21L, 22L, 5L, 1L, 7L, 2L, 4L, 3L, 19L, 18L, 17L, 16L, 15L, 6L},
        {22L, 21L, 20L, 19L, 18L, 17L, 16L, 15L, 14L, 13L, 12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L, 4L, 1L, 2L, 3L},
        {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L},
        {1L, 2L, 3L, 4L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L},
        {13L, 11L, 12L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 21L, 22L},
        {9L, 8L, 7L, 6L, 5L, 4L, 3L, 22L, 21L, 20L, 2L, 19L, 18L, 17L, 16L, 15L, 14L, 12L, 11L, 10L, 1L, 13L}
    };

    protected void checkPkSequence(long[] aPks, Rowset aRowset) throws InvalidCursorPositionException, InvalidColIndexException {
        assertEquals(aPks.length, aRowset.size());
        for (int i = 1; i <= aRowset.size(); i++) {
            aRowset.absolute(i);
            assertTrue(Row.smartEquals(aRowset.getObject(1), aPks[i - 1]));
        }
    }

    @Test
    public void sorting1CriteriaAscTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("sorting1CriteriaAscTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        for (int i = 1; i <= fields.getFieldsCount(); i++) {
            // to avoid sorting stability problems...
            SortingCriterion sc0 = new SortingCriterion(1, true);
            List<SortingCriterion> criteria0 = new ArrayList<>();
            criteria0.add(sc0);
            rowset.sort(new RowsComparator(criteria0));
            checkPkSequence(pkSequencesAsc[0], rowset);

            SortingCriterion sc1 = new SortingCriterion(i, true);
            List<SortingCriterion> criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            assertEquals(lreciver.willSort, i * 2);
            assertEquals(lreciver.sorted, i * 2);
            checkPkSequence(pkSequencesAsc[i - 1], rowset);
        }
    }

    @Test
    public void sortingMultiCriteriaAscTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("sortingMultiCriteriaAscTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        List<SortingCriterion> criteria = new ArrayList<>();
        SortingCriterion sc1 = new SortingCriterion(3, true);
        criteria.add(sc1);
        SortingCriterion sc2 = new SortingCriterion(5, true);
        criteria.add(sc2);
        SortingCriterion sc3 = new SortingCriterion(7, true);
        criteria.add(sc3);
        SortingCriterion sc4 = new SortingCriterion(2, true);
        criteria.add(sc4);
        SortingCriterion sc5 = new SortingCriterion(8, true);
        criteria.add(sc5);
        SortingCriterion sc7 = new SortingCriterion(4, true);
        criteria.add(sc7);
        SortingCriterion sc8 = new SortingCriterion(6, true);
        criteria.add(sc8);
        rowset.sort(new RowsComparator(criteria));
        assertEquals(lreciver.willSort, 1);
        assertEquals(lreciver.sorted, 1);
        checkPkSequence(new long[]{6L, 15L, 16L, 17L, 18L, 19L, 3L, 4L, 2L, 7L, 1L, 5L, 21L, 22L, 20L, 8L, 9L, 10L, 11L, 12L, 13L, 14L}, rowset);
    }

    @Test
    public void sorting1CriteriaDescTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("sorting1CriteriaDescTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        for (int i = 1; i <= fields.getFieldsCount(); i++) {
            // to avoid sorting stability problems...
            SortingCriterion sc0 = new SortingCriterion(1, true);
            List<SortingCriterion> criteria0 = new ArrayList<>();
            criteria0.add(sc0);
            rowset.sort(new RowsComparator(criteria0));
            checkPkSequence(pkSequencesAsc[0], rowset);

            SortingCriterion sc1 = new SortingCriterion(i, false);
            List<SortingCriterion> criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            assertEquals(lreciver.willSort, i * 2);
            assertEquals(lreciver.sorted, i * 2);
            checkPkSequence(pkSequencesDesc[i - 1], rowset);
        }
    }

    @Test
    public void sorting1CriteriaDenyEventsTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("sorting1CriteriaDenyEventsTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        lreciver.allowSort = false;
        for (int i = 1; i <= fields.getFieldsCount(); i++) {
            // to avoid sorting stability problems...
            SortingCriterion sc0 = new SortingCriterion(1, true);
            List<SortingCriterion> criteria0 = new ArrayList<>();
            criteria0.add(sc0);
            rowset.sort(new RowsComparator(criteria0));
            checkPkSequence(pkSequencesAsc[0], rowset);

            SortingCriterion sc1 = new SortingCriterion(i, false);
            List<SortingCriterion> criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            assertEquals(lreciver.willSort, i * 2);
            assertEquals(lreciver.sorted, 0);
            checkPkSequence(pkSequencesAsc[0], rowset);
        }
        lreciver.allowSort = true;
        for (int i = 1; i <= fields.getFieldsCount(); i++) {
            // to avoid sorting stability problems...
            SortingCriterion sc0 = new SortingCriterion(1, true);
            List<SortingCriterion> criteria0 = new ArrayList<>();
            criteria0.add(sc0);
            rowset.sort(new RowsComparator(criteria0));
            checkPkSequence(pkSequencesAsc[0], rowset);

            SortingCriterion sc1 = new SortingCriterion(i, false);
            List<SortingCriterion> criteria = new ArrayList<>();
            criteria.add(sc1);
            rowset.sort(new RowsComparator(criteria));
            assertEquals(lreciver.willSort, i * 2 + fields.getFieldsCount() * 2);
            assertEquals(lreciver.sorted, i * 2);
            checkPkSequence(pkSequencesDesc[i - 1], rowset);
        }
    }

    @Test
    public void sortingMultiCriteriaAscDescTest() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
        System.out.println("sortingMultiCriteriaAscDescTest");
        Rowset rowset = initRowset();
        EventsReciver lreciver = new EventsReciver();
        rowset.addRowsetListener(lreciver);
        List<SortingCriterion> criteria = new ArrayList<>();
        SortingCriterion sc1 = new SortingCriterion(3, true);
        criteria.add(sc1);
        SortingCriterion sc2 = new SortingCriterion(5, true);
        criteria.add(sc2);
        SortingCriterion sc3 = new SortingCriterion(7, false);
        criteria.add(sc3);
        SortingCriterion sc4 = new SortingCriterion(2, true);
        criteria.add(sc4);
        SortingCriterion sc5 = new SortingCriterion(8, false);
        criteria.add(sc5);
        SortingCriterion sc7 = new SortingCriterion(4, true);
        criteria.add(sc7);
        SortingCriterion sc8 = new SortingCriterion(6, false);
        criteria.add(sc8);
        rowset.sort(new RowsComparator(criteria));
        assertEquals(lreciver.willSort, 1);
        assertEquals(lreciver.sorted, 1);
        checkPkSequence(new long[]{6L, 15L, 16L, 17L, 18L, 19L, 3L, 4L, 2L, 7L, 1L, 5L, 20L, 22L, 21L, 8L, 9L, 10L, 11L, 12L, 13L, 14L}, rowset);
    }
}
