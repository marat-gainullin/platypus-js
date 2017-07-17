package com.eas.client.model.application;

import com.eas.client.changes.Change;
import com.eas.client.changes.CommandRequest;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.PlatypusQuery;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.util.Collections;
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

    @ScriptFunction(jsDoc = ENQUEUE_UPDATE_JSDOC, params = {"params"})
    @Override
    public void enqueueUpdate(JSObject aParams) throws Exception {
        if (aParams != null) {
            PlatypusQuery copied = query.copy();
            aParams.keySet().forEach((String pName) -> {
                Parameter p = copied.getParameters().get(pName);
                if (p != null) {
                    Object jsValue = aParams.getMember(pName);
                    p.setValue(jsValue);// .toJava is inside prepreCommand()
                }
            });
            model.getChangeLog().add(copied.prepareCommandRequest());
        } else {
            model.getChangeLog().add(query.prepareCommandRequest());
        }
    }

    @ScriptFunction(jsDoc = EXECUTE_UPDATE_JSDOC, params = {"onSuccess", "onFailure"})
    @Override
    public int executeUpdate(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        CommandRequest commandRequest = query.prepareCommandRequest();
        if (aOnSuccess != null) {
            return model.serverProxy.commit(Collections.singletonList(commandRequest), Scripts.getSpace(), (Integer aUpdated) -> {
                aOnSuccess.call(null, new Object[]{aUpdated});
            }, (Exception ex) -> {
                if (aOnFailure != null) {
                    aOnFailure.call(null, new Object[]{ex.getMessage()});
                }
            });
        } else {
            return model.serverProxy.commit(Collections.singletonList(commandRequest), Scripts.getSpace(), null, null);
        }
    }

    @ScriptFunction(jsDoc = UPDATE_JSDOC, params = {"params", "onSuccess", "onFailure"})
    @Override
    public int update(JSObject aParams, JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        PlatypusQuery copied = query.copy();
        aParams.keySet().forEach((String pName) -> {
            Parameter p = copied.getParameters().get(pName);
            if (p != null) {
                Object jsValue = aParams.getMember(pName);
                p.setValue(Scripts.getSpace().toJava(jsValue));
            }
        });
        CommandRequest commandRequest = copied.prepareCommandRequest();
        if (aOnSuccess != null) {
            return model.serverProxy.commit(Collections.singletonList(commandRequest), Scripts.getSpace(), (Integer aUpdated) -> {
                aOnSuccess.call(null, new Object[]{aUpdated});
            }, (Exception ex) -> {
                if (aOnFailure != null) {
                    aOnFailure.call(null, new Object[]{ex.getMessage()});
                }
            });
        } else {
            return model.serverProxy.commit(Collections.singletonList(commandRequest), Scripts.getSpace(), null, null);
        }
    }

    /**
     * Returns change log for this entity. In some cases, we might have several
     * change logs in one model. Several databases is the case.
     *
     * @throws java.lang.Exception
     * @return 
     */
    public List<Change.Transferable> getChangeLog() throws Exception {
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
