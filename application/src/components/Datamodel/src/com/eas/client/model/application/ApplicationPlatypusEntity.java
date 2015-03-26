/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.changes.Change;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.PlatypusQuery;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    protected List<Change> getChangeLog() throws Exception {
        return model.getChangeLog();
    }

    @Override
    protected void refreshRowset(final Consumer<Void> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        if (model.process != null || aOnSuccess != null) {
            Future<Void> f = new RowsetRefreshTask(aOnFailure);
            rowset.refresh(query.getParameters(), (Rowset aRowset) -> {
                if (!f.isCancelled()) {
                    assert pending == f : PENDING_ASSUMPTION_FAILED_MSG;
                    valid = true;
                    pending = null;
                    model.terminateProcess(ApplicationPlatypusEntity.this, null);
                    if (aOnSuccess != null) {
                        aOnSuccess.accept(null);
                    }
                }
            }, (Exception ex) -> {
                Logger.getLogger(ApplicationPlatypusEntity.class.getName()).log(Level.SEVERE, ex.getMessage());
                if (!f.isCancelled()) {
                    assert pending == f : PENDING_ASSUMPTION_FAILED_MSG;
                    valid = true;
                    pending = null;
                    model.terminateProcess(ApplicationPlatypusEntity.this, ex);
                    if (aOnFailure != null) {
                        aOnFailure.accept(ex);
                    }
                }
            });
            pending = f;
        } else {
            rowset.refresh(query.getParameters(), null, null);
        }
    }
    protected static final String PENDING_ASSUMPTION_FAILED_MSG = "pending assigned to null without pending.cancel() call.";

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
            prepareRowsetByQuery();
        }
    }

    @Override
    protected void prepareRowsetByQuery() throws Exception {
        Rowset oldRowset = rowset;
        if (rowset != null) {
            rowset.removeRowsetListener(this);
            rowset.setLog(null);
            rowset = null;
        }
        if (query != null) {
            rowset = query.prepareRowset();
            rowset.setLog(getChangeLog());
            rowset.addRowsetListener(this);
            locator = new Locator();
            locator.setRowset(rowset);
            changeSupport.firePropertyChange("rowset", oldRowset, rowset);
        }
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
