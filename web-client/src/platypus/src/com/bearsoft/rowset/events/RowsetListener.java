/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

/**
 * Rowset's events listener interface.
 * 
 * @author mg
 */
public interface RowsetListener {

	/**
	 * Rowset before scroll event handler/validator.
	 * 
	 * @param event
	 *            - describes destination row index.
	 * @return result indicating that scroll is leagal.
	 */
	public boolean willScroll(RowsetScrollEvent event);

	/**
	 * Rowset before filtering event handler/validator. The rowset fires this
	 * event when filtering has been called. returning false will restrict
	 * saving process.
	 * 
	 * @param event
	 *            of the rowset.
	 * @return flag allowing filtering.
	 */
	public boolean willFilter(RowsetFilterEvent event);

	/**
	 * Rowset before requering event handler/validator. The rowset fires this
	 * event when requery has been called. Returning false will restrict saving
	 * process.
	 * 
	 * @param event
	 *            of the rowset.
	 * @return flag allowing requering.
	 */
	public boolean willRequery(RowsetRequeryEvent event);

	/**
	 * Rowset before requering event handler. The rowset fires this event when
	 * requery has been called and after all willRequery() handlers have been
	 * called.
	 * 
	 * @param event
	 *            of the rowset.
	 */
	public void beforeRequery(RowsetRequeryEvent event);

	/**
	 * Rowset before insert event handler/validator.
	 * 
	 * @param event
	 *            - describes row inserting.
	 * @return result indicating that inserting is leagal.
	 */
	public boolean willInsertRow(RowsetInsertEvent event);

	/**
	 * Rowset before delete event handler/validator.
	 * 
	 * @param event
	 *            - describes row deleting.
	 * @return result indicating that deleting is leagal.
	 */
	public boolean willDeleteRow(RowsetDeleteEvent event);

	/**
	 * Rowset before sort event handler/validator.
	 * 
	 * @param event
	 *            - describes sorting.
	 * @return result indicating that sorting is leagal.
	 */
	public boolean willSort(RowsetSortEvent event);

	/**
	 * Rowset after filtered event handler.
	 * 
	 * @param event
	 *            - describing filter that has filtered the rowset.
	 */
	public void rowsetFiltered(RowsetFilterEvent event);

	/**
	 * Rowset after queried event handler.
	 * 
	 * @param event
	 *            of the rowset
	 */
	public void rowsetRequeried(RowsetRequeryEvent event);

	/**
	 * Rowset after net error event handler.
	 * 
	 * @param event
	 *            of the rowset
	 */
	public void rowsetNetError(RowsetNetErrorEvent event);

	/**
	 * Rowset after saved event handler. The rowset fires this event when accept
	 * changes was succesful.
	 * 
	 * @param event
	 *            of the rowset
	 */
	public void rowsetSaved(RowsetSaveEvent event);

	/**
	 * Rowset after saved event handler. The rowset fires this event when accept
	 * changes was succesful.
	 * 
	 * @param event
	 *            of the rowset
	 */
	public void rowsetRolledback(RowsetRollbackEvent event);

	/**
	 * Notifies registered listeners that a <code>Rowset</code> object's cursor
	 * has moved.
	 * <P>
	 * The source of the event can be retrieved with the method
	 * <code>event.getSource()</code>.
	 * 
	 * @param event
	 *            a <code>RowsetEvent</code> object that contains the
	 *            <code>Rowset</code> object that is the source of the event
	 */
	public void rowsetScrolled(RowsetScrollEvent event);

	/**
	 * Notifies registered listeners that a <code>Rowset</code> has been sorted
	 * <P>
	 * The source of the event can be retrieved with the method
	 * <code>event.getSource</code>.
	 * 
	 * @param event
	 *            a <code>RowsetEvent</code> object that contains the
	 *            <code>Rowset</code> object that is the source of the event
	 */
	public void rowsetSorted(RowsetSortEvent event);

	/**
	 * Notifies registered listeners that a row have been inserted in the
	 * <code>Rowset</code> object
	 * <P>
	 * The source of the event can be retrieved with the method
	 * <code>event.getSource</code>.
	 * 
	 * @param event
	 *            a <code>RowsetEvent</code> object that contains the
	 *            <code>Rowset</code> object that is the source of the event
	 */
	public void rowInserted(RowsetInsertEvent event);

	/**
	 * Notifies registered listeners that a row has beeen deleted from the
	 * <code>Rowset</code> object
	 * <P>
	 * The source of the event can be retrieved with the method
	 * <code>event.getSource()</code>.
	 * 
	 * @param event
	 *            a <code>RowsetEvent</code> object that contains the
	 *            <code>Rowset</code> object that is the source of the event
	 */
	public void rowDeleted(RowsetDeleteEvent event);
}
