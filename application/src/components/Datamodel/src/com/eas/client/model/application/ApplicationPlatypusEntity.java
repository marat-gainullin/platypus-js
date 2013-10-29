/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.exceptions.InvalidFieldsExceptionException;
import com.eas.client.queries.PlatypusQuery;
import java.util.List;

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
}
