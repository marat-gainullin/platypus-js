/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

import java.util.List;

import com.eas.client.changes.Change;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameters;
import com.eas.core.Cancellable;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;

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
    public String getEntityName();
    
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
    public Cancellable refresh(Parameters aParams, Callback<JavaScriptObject, String> aCallback) throws Exception;

    /**
     * Retruns a change log, fronted with this flow provider.
     * @return Changes' log for collect changes in.
     */
    public List<Change> getChangeLog();

}
