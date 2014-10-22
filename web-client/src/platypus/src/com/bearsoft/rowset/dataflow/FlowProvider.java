/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.dataflow;

import java.util.List;

import com.bearsoft.rowset.Cancellable;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.google.gwt.core.client.Callback;

/**
 * This interface is intended to serve as base contract for data quering/reading/achieving and
 * than applying chages to variety of unknown and mystery sources/recipients.
 * @author mg
 */
public interface FlowProvider {

    /**
     * Returns back-end entity identifier. It might be a database table, or ORM entity.
     * @return
     * @throws Exception 
     */
    public String getEntityId();
    
    public Fields getExpectedFields();
    
    /**
     * Queries some source for data, according to the supplied parameters values.
     * Returns always new Rowset instance. If you need to include returned data in an existing Rowset instance,
     * follow such schema: call refresh method, get it's data rows with getCurrent() method
     * and than insert them into rowset needed. After all invoke currentToOriginal() method on this rowset.
     * @param aParams Parameters values, ordered with some unknown criteria. If rowset can't be achieved,
     * in some circumstances, this method must return at least an empty Rowset instance. Values from this parameter
     * collection are applied one by one in the straight order.
     * @return Rowset instance, containing data, retrieved from the source.
     * @see Parameters
     */
    public Cancellable refresh(Parameters aParams, Callback<Rowset, String> aCallback) throws Exception;

    /**
     * Retruns a change log, fronted with this flow provider.
     * @return Changes' log for collect changes in.
     */
    public List<Change> getChangeLog();

}
