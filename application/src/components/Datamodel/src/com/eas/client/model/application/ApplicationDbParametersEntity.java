/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.SqlQuery;
import com.eas.client.model.ParametersRowset;
import com.eas.client.model.visitors.ApplicationModelVisitor;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.script.NoPublisherException;
import java.util.ArrayList;
import java.util.List;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ApplicationDbParametersEntity extends ApplicationDbEntity implements ApplicationParametersEntity {

    protected List<Change> changeLog = new ArrayList<>();// dummy change log. No entries expected here

    public ApplicationDbParametersEntity(ApplicationDbModel aModel) {
        super();
        model = aModel;
        entityId = ApplicationModel.PARAMETERS_ENTITY_ID;
    }

    @Override
    public List<Change> getChangeLog() throws Exception {
        return changeLog;
    }

    @Override
    public boolean isRowsetPresent() {
        return true;
    }

    @Override
    public Rowset getRowset() throws Exception {
        execute();
        return super.getRowset();
    }

    @Override
    public Fields getFields() {
        return model.getParameters();
    }

    @Override
    public void validateQuery() throws Exception {
        execute();
    }

    @Override
    protected boolean isTagValid(String aTagName) {
        return true;
    }

    @Override
    public ApplicationDbParametersEntity copy() throws Exception {
        ApplicationDbParametersEntity copied = new ApplicationDbParametersEntity(model);
        assign(copied);
        return copied;
    }

    @Override
    public void accept(ModelVisitor<ApplicationDbEntity> visitor) {
        if (visitor instanceof ApplicationModelVisitor<?>) {
            ((ApplicationModelVisitor<?>) visitor).visit(this);
        }
    }

    @Override
    public String getQueryName() {
        return null;
    }

    @Override
    public SqlQuery getQuery() {
        return null;
    }

    @Override
    public String getTableDatasourceName() {
        return null;
    }

    @Override
    public void setTableDatasourceName(String tableDbId) {
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public void setTableName(String aTableName) {
    }

    @Override
    public Long getEntityId() {
        return ApplicationModel.PARAMETERS_ENTITY_ID;
    }

    @Override
    public boolean execute() {
        if (rowset == null) {
            rowset = new ParametersRowset((Parameters) model.getParameters());
            rowset.addRowsetListener(this);
        }
        return rowset != null;
    }

    @Override
    protected void internalExecute(boolean refresh) {
    }

    @Override
    protected void refreshRowset() throws Exception {
        // no op
    }
    
    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;
    
    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    } 

}
