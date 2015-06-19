/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

import com.eas.client.metadata.Parameters;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This interface is intended to serve as base contract for data
 * quering/fetching/reading and than applying changes to variety of unknown and
 * mystery sources/recipients.
 *
 * @author mg
 */
public interface FlowProvider extends AutoCloseable{

    public static final int NO_PAGING_PAGE_SIZE = -1;

    /**
     * Returns back-end entity identifier. It might be a database table, or ORM
     * entity.
     *
     * @return Back-end entity identifier. It might be a database table, or ORM
     * entity
     */
    public String getEntityName();

    /**
     * Queries some source for data, according to the supplied parameters
     * values. Returns allways new Rowset instance. If you need to include
     * returned data in an existing Rowset instance, follow such schema: call
     * refresh method, get it's data rows with getCurrent() method and than
     * insert them into rowset needed. After all invoke currentToOriginal()
     * method on this rowset.
     *
     * @param aParams Parameters values, ordered with some unknown criteria. If
     * rowset can't be achieved, in some circumstances, this method must return
     * at least an empty Rowset instance. Values from this parameter collection
     * are applied one by one in the straight order.
     * @param onSuccess
     * @param onFailure
     * @return Rowset instance, containing data, retrieved from the source.
     * @throws java.lang.Exception
     * @see Parameters
     */
    public Collection<Map<String, Object>> refresh(Parameters aParams, Consumer<Collection<Map<String, Object>>> onSuccess, Consumer<Exception> onFailure) throws Exception;

    /**
     * Fetches a next page of data from an abstract data source.
     *
     * @param onSuccess
     * @param onFailure
     * @return Rowset instance, containing data, retrieved from the source while
     * fetching a page.
     * @throws Exception
     * @see FlowProviderNotPagedException
     */
    public Collection<Map<String, Object>> nextPage(Consumer<Collection<Map<String, Object>>> onSuccess, Consumer<Exception> onFailure) throws Exception;

    /**
     * Returns page size for paged flow providers.
     *
     * @return Page size for paged flow providers. Value less or equal to zero
     * means that there is no paging.
     */
    public int getPageSize();

    /**
     * Sets page size for paged flow providers.
     *
     * @param aPageSize Page size for paged flow providers.
     */
    public void setPageSize(int aPageSize);

    public boolean isProcedure();

    public void setProcedure(boolean aProcedure);
    
}
