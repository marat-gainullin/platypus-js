/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

/**
 * Rowset scroll event. Occurs before and after the rowset is scrolled from one
 * position to another.
 * 
 * @author mg.
 * @see RowsetListener
 */
public class RowsetScrollEvent extends RowsetEvent {

	protected int oldRowIndex = -1;
	protected int newRowIndex = -1;

	/**
	 * <code>RowsetScrollEvent</code> constructor.
	 * 
	 * @param source
	 *            The rowset is scrolled.
	 * @param aOldRowIndex
	 *            Row index that was current row before rowset is scrolled. 1 - based.
	 * @param aNewRowIndex
	 *            Row index that will be current row after rowset will be
	 *            scrolled. 1 - based.
	 * @param aKind
	 *            Event kind (before or after)
	 * @see RowsetEventMoment
	 */
	public RowsetScrollEvent(Rowset source, int aOldRowIndex, int aNewRowIndex, RowsetEventMoment aKind) {
		super(source, aKind);
		oldRowIndex = aOldRowIndex;
		newRowIndex = aNewRowIndex;
	}

	/**
	 * Returns row index that will be current row after rowset will be scrolled.
	 * 
	 * @return Row index that will be current row after rowset will be scrolled. 1 - based.
	 */
	public int getNewRowIndex() {
		return newRowIndex;
	}

	/**
	 * Returns row index that was current row before rowset is scrolled.
	 * 
	 * @return Row index that was current row before rowset is scrolled. 1 - based.
	 */
	public int getOldRowIndex() {
		return oldRowIndex;
	}

}
