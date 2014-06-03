/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.changes.Change;
import com.eas.client.AppClient;
import com.eas.client.queries.PlatypusQuery;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ApplicationPlatypusModel extends ApplicationModel<ApplicationPlatypusEntity, ApplicationPlatypusParametersEntity, AppClient, PlatypusQuery> {

    protected List<Change> changeLog = new ArrayList<>();

    public ApplicationPlatypusModel() {
        super();
        parametersEntity = new ApplicationPlatypusParametersEntity(this);
    }

    public ApplicationPlatypusModel(AppClient aClient) {
        this();
        setClient(aClient);
    }

    @Override
    public ApplicationPlatypusEntity newGenericEntity() {
        return new ApplicationPlatypusEntity(this);
    }

    @Override
    public void addEntity(ApplicationPlatypusEntity aEntity) {
        aEntity.setModel(this);
        super.addEntity(aEntity);
    }

    @Override
    public void setParametersEntity(ApplicationPlatypusParametersEntity aParamsEntity) {
        super.setParametersEntity(aParamsEntity);
        parametersEntity.setModel(this);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Saves model data changes. Calls aCallback when done.\n"
            + " * If model can't apply the changed, than exception is thrown.\n"
            + " * In this case, application can call model.save() another time to save the changes.\n"
            + " * If an application need to abort futher attempts and discard model data changes, than it can call model.revert().\n")
    @Override
    public boolean save(JSObject aCallback) throws Exception {
        client.getChangeLog().addAll(changeLog);
        return super.save(aCallback);
    }

    @Override
    public int commit() throws Exception {
        return client.commit();
    }

    @Override
    public void saved() throws Exception {
        changeLog.clear();
        fireCommited();
    }

    @Override
    public void revert() throws Exception {
        changeLog.clear();
        fireReverted();
    }

    @Override
    public void rolledback() throws Exception {
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Requeries model data with callback.\n"
            + " */")
    public void requery(JSObject aOnSuccess) throws Exception {
        requery(aOnSuccess, null);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Requeries model data with callback.\n"
            + " */")
    @Override
    public void requery(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        changeLog.clear();
        super.requery(aOnSuccess, aOnFailure);
    }

    public List<Change> getChangeLog() {
        return changeLog;
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
