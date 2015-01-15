/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.sorting;

/**
 * Part of sorting criteria used in comparing rows process.
 * 
 * @author mg
 */
public class SortingCriterion {

	protected int colIndex = 0;
	protected boolean ascending = true;

	/**
	 * Criterion constructor.
	 * 
	 * @param aColIndex
	 *            Column(Field) index in rowset's <code>Fields</code>
	 * @param aAscending
	 *            Setting, defining the comparing direction.
	 */
	public SortingCriterion(int aColIndex, boolean aAscending) {
		super();
		colIndex = aColIndex;
		ascending = aAscending;
	}

	/**
	 * Returns column(Field) index.
	 * 
	 * @return Column(Field) index.
	 * @see #SortingCriterion(int aColIndex, boolean aAscending)
	 * @see #setColIndex(int colIndex)
	 */
	public int getColIndex() {
		return colIndex;
	}

	/**
	 * Sets column(Field) index.
	 * 
	 * @param colIndex
	 *            Column(Field) index.
	 * @see #SortingCriterion(int aColIndex, boolean aAscending)
	 * @see #getColIndex()
	 */
	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}

	/**
	 * Returns ascending setting for this criterion.
	 * 
	 * @return Ascending setting for this criterion.
	 * @see #SortingCriterion(int aColIndex, boolean aAscending)
	 * @see #setAscending(boolean ascending)
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * Sets ascending setting for this criterion.
	 * 
	 * @param ascending
	 *            Ascending setting for this criterion.
	 * @see #SortingCriterion(int aColIndex, boolean aAscending)
	 * @see #isAscending()
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
}