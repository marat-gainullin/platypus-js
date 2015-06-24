/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.changes.Change;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.PlatypusQuery;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.util.ArrayList;
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

    public ApplicationPlatypusEntity(String aQueryName) {
        super(aQueryName);
    }

    @Override
    public void accept(ModelVisitor<ApplicationPlatypusEntity, ApplicationPlatypusModel> visitor) {
        visitor.visit(this);
    }

    @ScriptFunction(jsDoc = ENQUEUE_UPDATE_JSDOC)
    @Override
    public void enqueueUpdate() throws Exception {
        model.getChangeLog().add(getQuery().prepareCommand());
    }

    @ScriptFunction(jsDoc = EXECUTE_UPDATE_JSDOC, params = {"onSuccess", "onFailure"})
    @Override
    public int executeUpdate(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        List<Change> localLog = new ArrayList<>();
        localLog.add(getQuery().prepareCommand());
        if (aOnSuccess != null) {
            return model.serverProxy.commit(localLog, Scripts.getSpace(), (Integer aUpdated) -> {
                aOnSuccess.call(null, new Object[]{aUpdated});
            }, (Exception ex) -> {
                if (aOnFailure != null) {
                    aOnFailure.call(null, new Object[]{ex.getMessage()});
                }
            });
        } else {
            return model.serverProxy.commit(localLog, Scripts.getSpace(), null, null);
        }
    }

    @Override
    public List<Change> getChangeLog() throws Exception {
        return model.getChangeLog();
    }

    @Override
    public void validateQuery() throws Exception {
        if (query == null) {
            if (queryName != null) {
                PlatypusQuery q = model.queries.getCachedQuery(queryName);
                if (q != null) {
                    query = q.copy();
                }
            } else {
                throw new IllegalStateException("Only managed queries are allowed in three-tier mode!");
            }
        }
    }
}
