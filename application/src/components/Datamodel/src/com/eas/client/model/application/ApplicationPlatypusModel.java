/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.changes.Change;
import com.eas.client.AppClient;
import com.eas.client.queries.PlatypusQuery;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Function;

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

    @Override
    public boolean isTypeSupported(int aType) throws Exception {
        return true;
    }

    @ScriptFunction(jsDocText = "Saves model data changes. Calls aCallback when done."
    + "If model can't apply the changed, than exception is thrown. "
    + "In this case, application can call model.save() another time to save the changes. "
    + "If an application need to abort futher attempts and discard model data changes, "
    + "than it can call model.revert().")
    @Override
    public boolean save(Function aCallback) throws Exception {
        client.getChangeLog().addAll(changeLog);
        return super.save(aCallback);
    }

    @Override
    public int commit() throws Exception {
        if (commitable) {
            return client.commit();
        } else {
            return 0;
        }
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

    @ScriptFunction(jsDocText = "Requeries model data with callback.")
    @Override
    public void requery(Function aCallback) throws Exception {
        changeLog.clear();
        super.requery(aCallback);
    }

    public List<Change> getChangeLog() {
        return changeLog;
    }
}
