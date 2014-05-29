/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.sorting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;

/**
 * Comparator of <code>Row</code> instances. It is used to sorting rowsets.
 * 
 * @author mg
 */
public class RowsComparator implements Comparator<Row> {

	protected List<SortingCriterion> criteria;

	/**
	 * Constructor, accepting a list of <code>SortingCriterion</code> instances
	 * 
	 * @param aCriteria
	 */
	public RowsComparator(List<SortingCriterion> aCriteria) {
		super();
		criteria = aCriteria;
	}

	public RowsComparator(SortingCriterion aCriterion) {
		super();
		criteria = new ArrayList<>();
		criteria.add(aCriterion);
	}

	/**
	 * The main method used to compare rows while sorting.
	 * 
	 * @param r1
	 *            The first row.
	 * @param r2
	 *            The second row.
	 * @return 1 if r1 greater then r2, -1 otherwise and 0 if r1 and r2 are
	 *         considered to be equal.
	 */
	public int compare(Row r1, Row r2) {
		assert criteria != null;
		for (int i = 0; i < criteria.size(); i++) {
			try {
				SortingCriterion cr = criteria.get(i);
				assert cr != null;
				assert cr.getColIndex() > 0;
				Object o1 = r1.getColumnObject(cr.getColIndex());
				Object o2 = r2.getColumnObject(cr.getColIndex());
				if (o1 == null && o2 != null) {
					return cr.isAscending() ? -1 : 1;
				} else if (o1 != null && o2 == null) {
					return cr.isAscending() ? 1 : -1;
				} else if (o1 == null && o2 == null) {
					continue;
				} else if (o1 instanceof Comparable && o2 instanceof Comparable) {
					Comparable c1 = (Comparable) o1;
					Comparable c2 = (Comparable) o2;
					if (c1 instanceof Number) {
						c1 = Double.valueOf(((Number) c1).doubleValue());
					}
					if (c2 instanceof Number) {
						c2 = Double.valueOf(((Number) c2).doubleValue());
					}
					int cRes = c1.compareTo(c2);
					if (cRes != 0) {
						return cr.isAscending() ? cRes : -cRes;
					}
				}
			} catch (InvalidColIndexException ex) {
				Logger.getLogger(RowsComparator.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return 0;
	}
}
