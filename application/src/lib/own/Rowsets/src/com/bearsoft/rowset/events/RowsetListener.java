/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import com.eas.design.Designable;
import com.eas.design.Undesignable;

/**
 * Rowset's events listener interface.
 *
 * @author mg
 */
public interface RowsetListener {

    /**
     * Rowset before scroll event handler/validator.
     *
     * @param event - describes destination row index.
     * @return result indicating that scroll is leagal.
     */
    public boolean willScroll(RowsetScrollEvent event);

    /**
     * Rowset before filtering event handler/validator. The rowset fires this
     * event when filtering has been called. returning false will restrict
     * saving process.
     *
     * @param event of the rowset.
     * @return flag allowing filtering.
     */
    @Undesignable
    public boolean willFilter(RowsetFilterEvent event);

    /**
     * Rowset before requering event handler/validator. The rowset fires this
     * event when requery has been called. Returning false will restrict saving
     * process.
     *
     * @param event of the rowset.
     * @return flag allowing requering.
     */
    @Undesignable
    public boolean willRequery(RowsetRequeryEvent event);

    /**
     * Rowset before requering event handler. The rowset fires this
     * event when requery has to be called and nothing can prevent this.
     *
     * @param event of the rowset.
     */
    @Undesignable
    public void beforeRequery(RowsetRequeryEvent event);
    
    /**
     * Rowset before paging event handler/validator. The rowset fires this event
     * when nextPage has been called. Returning false will restrict saving
     * process.
     *
     * @param event of the rowset.
     * @return flag allowing paging.
     */
    @Undesignable
    public boolean willNextPageFetch(RowsetNextPageEvent event);

    /**
     * Rowset before insert event handler/validator.
     *
     * @param event - describes row inserting.
     * @return result indicating that inserting is leagal.
     */
    @Designable(displayName="willInsert", description="willInsert")
    public boolean willInsertRow(RowsetInsertEvent event);

    /**
     * Row before change event handler/validator.
     *
     * @param event - describes old and new field values.
     * @return result indicating that change is leagal.
     */
    @Designable(displayName="willChange", description="willChange")
    public boolean willChangeRow(RowChangeEvent event);

    /**
     * Rowset before delete event handler/validator.
     *
     * @param event - describes row deleting.
     * @return result indicating that deleting is leagal.
     */
    @Designable(displayName="willDelete", description="willDelete")
    public boolean willDeleteRow(RowsetDeleteEvent event);

    /**
     * Rowset before sort event handler/validator.
     *
     * @param event - describes sorting.
     * @return result indicating that sorting is leagal.
     */
    @Undesignable
    public boolean willSort(RowsetSortEvent event);

    /**
     * Rowset after filtered event handler.
     *
     * @param event - describing filter that has filtered the rowset.
     */
    @Designable(displayName="onFiltered", description="After filter event")
    public void rowsetFiltered(RowsetFilterEvent event);

    public void rowsetNetError(RowsetNetErrorEvent event);
    
    /**
     * Rowset after queried event handler.
     *
     * @param event of the rowset
     */
    @Designable(displayName="onRequeried", description="After requery event")
    public void rowsetRequeried(RowsetRequeryEvent event);

    /**
     * Rowset after next page fetched event handler.
     *
     * @param event of the rowset
     */
    @Undesignable
    public void rowsetNextPageFetched(RowsetNextPageEvent event);

    /**
     * Rowset after saved event handler. The rowset fires this event when accept
     * changes were commited.
     *
     * @param event of the rowset
     */
    @Undesignable
    public void rowsetSaved(RowsetSaveEvent event);

    /**
     * Rowset after transaction have been commited event handler. The rowset
     * fires this event when it's changes were rejected.
     *
     * @param event of the rowset
     */
    @Undesignable
    public void rowsetRolledback(RowsetRollbackEvent event);

    /**
     * Notifies registered listeners that a
     * <code>Rowset</code> object's cursor has moved. <P> The source of the
     * event can be retrieved with the method
     * <code>event.getSource()</code>.
     *
     * @param event a <code>RowsetEvent</code> object that contains        the <code>Rowset</code> object that is the source of the
     * event
     */
    @Designable(displayName="onScrolled", description="After cursor position change event")
    public void rowsetScrolled(RowsetScrollEvent event);

    /**
     * Notifies registered listeners that a
     * <code>Rowset</code> has been sorted <P> The source of the event can be
     * retrieved with the method
     * <code>event.getSource</code>.
     *
     * @param event a <code>RowsetEvent</code> object that contains        the <code>Rowset</code> object that is the source of the
     * event
     */
    @Undesignable
    public void rowsetSorted(RowsetSortEvent event);

    /**
     * Notifies registered listeners that a row have been inserted in the
     * <code>Rowset</code> object <P> The source of the event can be retrieved
     * with the method
     * <code>event.getSource</code>.
     *
     * @param event a <code>RowsetEvent</code> object that contains        the <code>Rowset</code> object that is the source of the
     * event
     */
    @Designable(displayName="onInserted", description="After insert event")
    public void rowInserted(RowsetInsertEvent event);

    /**
     * Notifies registered listeners that a
     * <code>Rowset</code> object have got a change in one of its rows. <P> The
     * source of the event can be retrieved with the method
     * <code>event.getSource</code>.
     *
     * @param event a <code>RowsetEvent</code> object that contains        the <code>Rowset</code> object that is the source of the
     * event
     */
    //@Designable(displayName="onChanged", description="After each object change event")
    //public void rowChanged(RowChangeEvent event);

    /**
     * Notifies registered listeners that a row has beeen deleted from the
     * <code>Rowset</code> object <P> The source of the event can be retrieved
     * with the method
     * <code>event.getSource()</code>.
     *
     * @param event a <code>RowsetEvent</code> object that contains        the <code>Rowset</code> object that is the source of the
     * event
     */
    @Designable(displayName="onDeleted", description="After delete event")
    public void rowDeleted(RowsetDeleteEvent event);
}
