/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.changes.Change;
import com.eas.client.AppClient;
import com.eas.client.queries.PlatypusQuery;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean save() throws Exception {
        client.getChangeLog().addAll(changeLog);
        return super.save();
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

    @Override
    public void requery() throws Exception {
        changeLog.clear();
        super.requery();
    }

    public List<Change> getChangeLog() {
        return changeLog;
    }
}
