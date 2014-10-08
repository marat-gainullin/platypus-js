/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.changes.Change;
import com.eas.client.cache.ServerDataStorage;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.queries.QueriesProxy;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ApplicationPlatypusModel extends ApplicationModel<ApplicationPlatypusEntity, ApplicationPlatypusParametersEntity, PlatypusQuery> {

    protected List<Change> changeLog = new ArrayList<>();
    protected ServerDataStorage serverProxy;

    protected ApplicationPlatypusModel(QueriesProxy<PlatypusQuery> aQueries) {
        super(aQueries);
        parametersEntity = new ApplicationPlatypusParametersEntity(this);
    }

    public ApplicationPlatypusModel(ServerDataStorage aServerProxy, QueriesProxy<PlatypusQuery> aQueries) {
        this(aQueries);
        serverProxy = aServerProxy;
    }

    public ServerDataStorage getServerProxy() {
        return serverProxy;
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
            + " * Saves model data changes. Calls onSuccess when done.\n"
            + " * If model can't apply the changed, than exception is thrown.\n"
            + " * In this case, application can call model.save() another time to save the changes.\n"
            + " * If an application need to abort futher attempts and discard model data changes, than it can call model.revert().\n"
            + " * @param onSuccess Success callback.\n"
            + " * @param onFailure Failure callback.\n")
    @Override
    public void save(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        serverProxy.getChangeLog().addAll(changeLog);
        super.save(aOnSuccess, aOnFailure);
    }

    @Override
    public int commit(final Consumer<Integer> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        return serverProxy.commit(aOnSuccess, aOnFailure);
    }

    @Override
    public void commited() {
        changeLog.clear();
        super.commited();
    }

    @Override
    public void revert() {
        changeLog.clear();
        super.revert();
    }

    @ScriptFunction(jsDoc = REQUERY_JSDOC, params = {"onSuccess", "onFailure"})
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
