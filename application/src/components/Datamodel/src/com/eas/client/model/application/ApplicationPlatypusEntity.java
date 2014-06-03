/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.exceptions.InvalidFieldsExceptionException;
import com.eas.client.queries.PlatypusQuery;
import com.eas.script.NoPublisherException;
import java.util.List;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ApplicationPlatypusEntity extends ApplicationEntity<ApplicationPlatypusModel, PlatypusQuery, ApplicationPlatypusEntity> {

    public ApplicationPlatypusEntity() {
        super();
    }

    public ApplicationPlatypusEntity(ApplicationPlatypusModel aModel) {
        super(aModel);
    }

    public ApplicationPlatypusEntity(String aQueryId) {
        super(aQueryId);
    }

    @Override
    public void enqueueUpdate() throws Exception {
        model.getClient().enqueueUpdate(getQueryId(), getQuery().getParameters());
    }

    @Override
    public int executeUpdate() throws Exception {
        model.getClient().enqueueUpdate(getQueryId(), getQuery().getParameters());
        return 0;
    }

    @Override
    protected List<Change> getChangeLog() throws Exception {
        return model.getChangeLog();
    }

    @Override
    protected void refreshRowset() throws Exception {
        if (query != null) {
            rowset.refresh(query.getParameters());
        }
    }

    @Override
    public void validateQuery() throws Exception {
        if (query == null) {
            if (queryId != null) {
                query = model.getClient().getAppQuery(queryId);
            } else {
                throw new IllegalStateException("In three-tier mode only managed queries are allowed!");
            }
            prepareRowsetByQuery();
        }
    }

    protected void prepareRowsetByQuery() throws InvalidFieldsExceptionException {
        Rowset oldRowset = rowset;
        if (rowset != null) {
            rowset.removeRowsetListener(this);
            unforwardChangeLog();
            rowset = null;
        }
        if (query != null) {
            rowset = query.prepareRowset();
            forwardChangeLog();
            rowset.addRowsetListener(this);
            changeSupport.firePropertyChange("rowset", oldRowset, rowset);
        }
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
