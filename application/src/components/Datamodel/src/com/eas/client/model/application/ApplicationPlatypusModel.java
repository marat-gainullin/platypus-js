/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.cache.ServerDataStorage;
import com.eas.client.changes.Change;
import com.eas.client.model.Model;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.queries.QueriesProxy;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ApplicationPlatypusModel extends ApplicationModel<ApplicationPlatypusEntity, PlatypusQuery> {

    protected List<Change> changeLog = new ArrayList<>();
    protected ServerDataStorage serverProxy;

    protected ApplicationPlatypusModel(QueriesProxy<PlatypusQuery> aQueries) {
        super(aQueries);
    }

    public ApplicationPlatypusModel(ServerDataStorage aServerProxy, QueriesProxy<PlatypusQuery> aQueries) {
        this(aQueries);
        serverProxy = aServerProxy;
    }

    @Override
    public <M extends Model<ApplicationPlatypusEntity, ?>> void accept(ModelVisitor<ApplicationPlatypusEntity, M> visitor) {
        visitor.visit((M) this);
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

    @ScriptFunction(jsDoc = SAVE_JSDOC, params = {"onSuccess", "onFailure"})
    @Override
    public int save(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        return super.save(aOnSuccess, aOnFailure);
    }

    @Override
    public boolean isModified() throws Exception {
        return !changeLog.isEmpty();
    }

    @Override
    public int commit(final Consumer<Integer> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        return serverProxy.commit(changeLog, Scripts.getSpace(), aOnSuccess, aOnFailure);
    }

    @Override
    public void commited() {
        changeLog.clear();
        super.commited();
    }

    @ScriptFunction(jsDoc = REVERT_JSDOC)
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
}
