/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.changes.Change;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.PlatypusQuery;
import com.eas.script.ScriptFunction;
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

    private static final String ENQUEUE_UPDATE_JSDOC = ""
            + "/**\n"
            + "* Adds the updates into the change log as a command.\n"
            + "*/";

    @ScriptFunction(jsDoc = ENQUEUE_UPDATE_JSDOC)
    @Override
    public void enqueueUpdate() throws Exception {
        model.getChangeLog().add(getQuery().prepareCommand());
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
