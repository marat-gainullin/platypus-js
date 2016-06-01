/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.changes.Change;
import com.eas.client.events.PublishedSourcedEvent;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.queries.Query;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.EventMethod;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import com.eas.util.ListenerRegistration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 * @param <M>
 * @param <Q>
 * @param <E>
 */
public abstract class ApplicationEntity<M extends ApplicationModel<E, Q>, Q extends Query, E extends ApplicationEntity<M, Q, E>> extends Entity<M, Q, E> implements HasPublished {

    public static final String BAD_FIELD_NAME_MSG = "Bad field name %s";
    public static final String BAD_FIND_AGRUMENTS_MSG = "Bad find agruments";
    public static final String BAD_FIND_ARGUMENT_MSG = "Argument at index %d must be a rowset's field.";
    public static final String BAD_PRIMARY_KEYS_MSG = "Bad primary keys detected. Required one and only one primary key field, but %d found.";
    public static final String CANT_CONVERT_TO_MSG = "Can't convert to %s, substituting with null.";
    // for runtime
    protected JSObject onRequeried;
    //
    protected JSObject lastSnapshot = Scripts.getSpace() != null ? Scripts.getSpace().makeArray() : null;
    protected JSObject snapshotConsumer;
    protected JSObject snapshotProducer;
    //
    protected JSObject published;
    protected ListenerRegistration cursorListener;
    protected boolean valid;
    protected Future<Void> pending;

    public ApplicationEntity() {
        super();
    }

    public ApplicationEntity(M aModel) {
        super(aModel);
    }

    public ApplicationEntity(String aEntityId) {
        super(aEntityId);
    }

    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        valid = false;
    }

    protected class RowsetRefreshTask implements Future<Void> {

        protected boolean cancelled;
        protected Consumer<Exception> onCancel;

        public RowsetRefreshTask(Consumer<Exception> aOnCancel) {
            super();
            onCancel = aOnCancel;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            valid = true;
            pending = null;
            String msg = "Canceled query on " + (name != null && !name.isEmpty() ? name : "") + (title != null && !title.isEmpty() ? "[" + title + "]" : "");
            Exception ex = new InterruptedException(msg);
            model.terminateProcess((E) ApplicationEntity.this, ex);
            if (onCancel != null) {
                onCancel.accept(ex);
            }
            return cancelled;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }

    public boolean isPending() {
        return pending != null;
    }

    public void unpend() {
        if (pending != null) {
            pending.cancel(true);
            pending = null;
        }
    }

    protected void silentUnpend() {
        ApplicationModel.RequeryProcess<E, Q> p = model.process;
        model.process = null;
        try {
            unpend();
        } finally {
            model.process = p;
        }
    }

    public void execute() throws Exception {
        execute(null, null);
    }

    public void execute(JSObject aOnSuccess) throws Exception {
        execute(aOnSuccess, null);
    }
    private static final String EXECUTE_JSDOC = ""
            + "/**\n"
            + " * Requeries the entity, only if any of its parameters has changed.\n"
            + " * @param onSuccess The handler function for refresh data on success event (optional).\n"
            + " * @param onFailure The handler function for refresh data on failure event (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = EXECUTE_JSDOC, params = {"onSuccess", "onFailure"})
    public void execute(final JSObject aOnSuccess, final JSObject aOnFailure) throws Exception {
        if (aOnSuccess != null) {
            if (getOutRelations().isEmpty()) {
                internalExecute((JSObject v) -> {
                    aOnSuccess.call(null, new Object[]{published});
                }, aOnFailure != null ? (Exception ex) -> {
                    aOnFailure.call(null, new Object[]{ex.getMessage()});
                } : null);
            } else {
                model.inProcess(() -> {
                    internalExecute(null, null);
                    return null;
                }, (Void v) -> {
                    aOnSuccess.call(null, new Object[]{published});
                }, aOnFailure != null ? (Exception ex) -> {
                    aOnFailure.call(null, new Object[]{ex.getMessage()});
                } : null);
            }
        } else {
            internalExecute(null, null);
        }
    }

    // Requery interface
    public void requery() throws Exception {
        requery(null, null);
    }

    public void requery(JSObject aOnSuccess) throws Exception {
        requery(aOnSuccess, null);
    }
    private static final String REQUERY_JSDOC = ""
            + "/**\n"
            + " * Requeries the entity's data. Forses the entity to refresh its data, no matter if its parameters has changed or not.\n"
            + " * @param onSuccess The callback function for refreshed data on success event (optional).\n"
            + " * @param onFailure The callback function for refreshed data on failure event (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = REQUERY_JSDOC, params = {"onSuccess", "onFailure"})
    public void requery(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        invalidate();
        execute(aOnSuccess, aOnFailure);
    }

    private static final String QUERY_JSDOC = ""
            + "/**\n"
            + " * Queries the entity's data. Data will be fresh copy. A call to query() will be independent from other calls.\n"
            + " * Subsequent calls will not cancel requests made within previous calls.\n"
            + " * @param params The params object with parameters' values of query. These values will not be written to entity's parameters.\n"
            + " * @param onSuccess The callback function for fresh data on success event (optional).\n"
            + " * @param onFailure The callback function for fresh data on failure event (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = QUERY_JSDOC, params = {"params", "onSuccess", "onFailure"})
    public JSObject query(JSObject aParams, JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        Query copied = query.copy();
        aParams.keySet().forEach((String pName) -> {
            Parameter p = copied.getParameters().get(pName);
            if (p != null) {
                Object jsValue = aParams.getMember(pName);
                p.setValue(Scripts.getSpace().toJava(jsValue));
            }
        });
        return copied.execute(Scripts.getSpace(), aOnSuccess != null ? (JSObject v) -> {
            aOnSuccess.call(null, new Object[]{v});
        } : null, aOnFailure != null ? (Exception ex) -> {
            aOnFailure.call(null, new Object[]{ex.getMessage()});
        } : null);
    }

    private static final String APPEND_JSDOC = ""
            + "/**\n"
            + " * Append data to the entity's data.\n"
            + " * Appended data will be managed by model, but appending itself will not be included in data changelog.\n"
            + " * @param data The plain js objects array to be appended.\n"
            + " */";

    @ScriptFunction(jsDoc = APPEND_JSDOC, params = {"data"})
    public void append(JSObject aData) {
        if (snapshotConsumer != null) {
            snapshotConsumer.call(null, new Object[]{aData, false});
        }
    }

    private static final String INSTANCE_CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * The constructor funciton for the entity's data array elements.\n"
            + " */";

    @ScriptFunction(jsDoc = INSTANCE_CONSTRUCTOR_JSDOC)
    public JSObject getElementClass() {
        return getFields().getInstanceConstructor();
    }

    @ScriptFunction
    public void setElementClass(JSObject aValue) {
        getFields().setInstanceConstructor(aValue);
    }

    public void putOrmScalarDefinition(String aName, Fields.OrmDef aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            Map<String, Fields.OrmDef> defs = getFields().getOrmScalarDefinitions();
            if (!defs.containsKey(aName)) {
                getFields().putOrmScalarDefinition(aName, aDefinition);
            } else {
                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.FINE, String.format("ORM property %s redefinition attempt on entity %s %s.", aName, name != null && !name.isEmpty() ? name : "", title != null && !title.isEmpty() ? "[" + title + "]" : ""));
            }
        }
    }

    public Map<String, Fields.OrmDef> getOrmScalarDefinitions() {
        return getFields().getOrmScalarDefinitions();
    }

    public void putOrmCollectionDefinition(String aName, Fields.OrmDef aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            Map<String, Fields.OrmDef> defs = getFields().getOrmCollectionsDefinitions();
            if (!defs.containsKey(aName)) {
                getFields().putOrmCollectionDefinition(aName, aDefinition);
            } else {
                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.FINE, String.format("ORM property %s redefinition attempt on entity %s %s.", aName, name != null && !name.isEmpty() ? name : "", title != null && !title.isEmpty() ? "[" + title + "]" : ""));
            }
        }
    }

    public Map<String, Fields.OrmDef> getOrmCollectionsDefinitions() {
        return getFields().getOrmCollectionsDefinitions();
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null && Scripts.isInitialized()) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
        if (Scripts.isInitialized()) {
            Scripts.getSpace().listen(published, "cursor", new AbstractJSObject() {

                @Override
                public boolean isFunction() {
                    return true;
                }

                @Override
                public Object call(Object thiz, Object... args) {
                    try {
                        resignOnCursor();
                        internalExecuteChildren(false);
                    } catch (Exception ex) {
                        Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return null;
                }

            });
        }
    }

    /**
     * Returns change log for this entity. In some cases, we might have several
     * change logs in one model. Several databases is the case.
     *
     * @throws java.lang.Exception
     * @return
     */
    public abstract List<Change> getChangeLog() throws Exception;

    private static final String ON_REQUIRED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the event occured after the entity's data have been requeried.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_REQUIRED_JSDOC)
    @EventMethod(eventClass = PublishedSourcedEvent.class)
    public JSObject getOnRequeried() {
        return onRequeried;
    }

    @ScriptFunction
    public void setOnRequeried(JSObject aValue) {
        JSObject oldValue = onRequeried;
        onRequeried = aValue;
        changeSupport.firePropertyChange("onRequeried", oldValue, aValue);
    }

    public JSObject getSnapshotConsumer() {
        return snapshotConsumer;
    }

    public void setSnapshotConsumer(JSObject aValue) {
        snapshotConsumer = aValue;
    }

    public JSObject getSnapshotProducer() {
        return snapshotProducer;
    }

    public void setSnapshotProducer(JSObject aValue) {
        snapshotProducer = aValue;
    }

    protected static final String ENQUEUE_UPDATE_JSDOC = ""
            + "/**\n"
            + " * Adds the updates into the change log as a command.\n"
            + " * @param params Params object literal. Optional. If absent, entity's parameters' values will be taken.\n"
            + " */";

    public abstract void enqueueUpdate(JSObject params) throws Exception;

    protected static final String UPDATE_JSDOC = ""
            + "/**\n"
            + " * Applies the updates into the database and commits the transaction.\n"
            + " * @param params Params object literal.\n"
            + " * @param onSuccess Success callback. It has an argument, - updates rows count.\n"
            + " * @param onFailure Failure callback. It has an argument, - exception occured while applying updates into the database.\n"
            + " */";

    public abstract int update(JSObject params, JSObject onSuccess, JSObject onFailure) throws Exception;

    protected static final String EXECUTE_UPDATE_JSDOC = ""
            + "/**\n"
            + " * Applies the updates into the database and commits the transaction.\n"
            + " * @param onSuccess Success callback. It has an argument, - updates rows count.\n"
            + " * @param onFailure Failure callback. It has an argument, - exception occured while applying updates into the database.\n"
            + " */";

    public abstract int executeUpdate(JSObject onSuccess, JSObject onFailure) throws Exception;

    protected void internalExecute(final Consumer<JSObject> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        if (query == null) {
            throw new IllegalStateException("Query must present. Query name: " + queryName + "; tableName: " + getTableNameForDescription());
        }
        bindQueryParameters();
        if (isValid()) {
            if (aOnSuccess != null) {
                Scripts.getSpace().process(() -> {
                    aOnSuccess.accept(null);
                });
            }
        } else {
            // Requery if query parameters values have been changed while bindQueryParameters() call
            // or we are forced to refresh the data via requery() call.
            silentUnpend();
            JSObject jsRowset = refreshRowset(aOnSuccess, aOnFailure);
            assert pending != null || (aOnSuccess == null && model.process == null);
        }
    }

    protected void internalExecuteChildren(boolean refresh) throws Exception {
        Set<Relation<E>> rels = getOutRelations();
        if (rels != null) {
            Set<E> toExecute = new HashSet<>();
            rels.forEach((Relation<E> outRel) -> {
                if (outRel != null) {
                    E rEntity = outRel.getRightEntity();
                    if (rEntity != null) {
                        toExecute.add(rEntity);
                    }
                }
            });
            model.executeEntities(refresh, toExecute);
        }
    }

    protected void internalExecuteChildren(boolean refresh, int aOnlyFieldIndex) throws Exception {
        Set<Relation<E>> rels = getOutRelations();
        if (rels != null) {
            Field onlyField = getFields().get(aOnlyFieldIndex);
            Set<E> toExecute = new HashSet<>();
            rels.forEach((Relation<E> outRel) -> {
                if (outRel != null) {
                    E rEntity = outRel.getRightEntity();
                    if (rEntity != null && outRel.getLeftField() == onlyField) {
                        toExecute.add(rEntity);
                    }
                }
            });
            model.executeEntities(refresh, toExecute);
        }
    }

    protected static final String PENDING_ASSUMPTION_FAILED_MSG = "pending assigned to null without pending.cancel() call.";

    protected JSObject refreshRowset(final Consumer<JSObject> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        if (model.process != null || aOnSuccess != null) {
            Future<Void> f = new RowsetRefreshTask(aOnFailure);
            query.execute(Scripts.getSpace(), (JSObject aRowset) -> {
                if (!f.isCancelled()) {
                    applySnapshot(aRowset);
                    assert pending == f : PENDING_ASSUMPTION_FAILED_MSG;
                    valid = true;
                    pending = null;
                    model.terminateProcess((E) ApplicationEntity.this, null);
                    fireRequeried();
                    if (aOnSuccess != null) {
                        aOnSuccess.accept(aRowset);
                    }
                }
            }, (Exception ex) -> {
                Logger.getLogger(ApplicationPlatypusEntity.class.getName()).log(Level.SEVERE, ex.getMessage());
                if (!f.isCancelled()) {
                    assert pending == f : PENDING_ASSUMPTION_FAILED_MSG;
                    valid = true;
                    pending = null;
                    model.terminateProcess((E) ApplicationEntity.this, ex);
                    if (aOnFailure != null) {
                        aOnFailure.accept(ex);
                    }
                }
            });
            pending = f;
            return null;
        } else {
            JSObject jsRowset = query.execute(Scripts.getSpace(), null, null);
            applySnapshot(jsRowset);
            fireRequeried();
            return jsRowset;
        }
    }

    public void takeSnapshot() {
        if (snapshotProducer != null) {
            lastSnapshot = (JSObject) snapshotProducer.call(null, new Object[]{});
        }
    }

    public void applyLastSnapshot() {
        applySnapshot(lastSnapshot);
    }

    public void applySnapshot(JSObject aValue) {
        lastSnapshot = aValue;
        // Apply aRowset as a snapshot. Be aware of change log!
        if (snapshotConsumer != null) {// snapshotConsumer is null in designer
            snapshotConsumer.call(null, new Object[]{aValue, true});
        }
    }

    protected void fireRequeried() {
        if (onRequeried != null) {
            try {
                JSObject event = Scripts.getSpace().makeObj();
                event.setMember("source", published);
                onRequeried.call(published, new Object[]{event});
            } catch (Exception ex) {
                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected boolean isQueriable() throws Exception {
        return queryName != null || (tableName != null && !tableName.isEmpty());
    }

    public void bindQueryParameters() throws Exception {
        Parameters selfParameters = getQuery().getParameters();
        for (int i = 1; i <= selfParameters.getFieldsCount(); i++) {
            Parameter p = selfParameters.get(i);
            boolean oldModified = p.isModified();
            // Let's correct script evil!!!
            p.setValue(Scripts.getSpace().toJava(p.getValue()));
            p.setModified(oldModified);
        }
        //
        boolean parametersModified = false;
        Set<Relation<E>> inRels = getInRelations();
        if (inRels != null && !inRels.isEmpty()) {
            for (Relation<E> relation : inRels) {
                if (relation != null && relation.isRightParameter()) {
                    E leftEntity = relation.getLeftEntity();
                    if (leftEntity != null) {
                        Object pValue = null;
                        if (relation.isLeftField()) {
                            // There might be entities - parameters values sources, with no
                            // data in theirs rowsets, so we can't bind query parameters to proper values. In the
                            // such case we initialize parameters values with null
                            JSObject leftRowset = leftEntity.getPublished();
                            if (leftRowset != null && leftRowset.getMember(CURSOR_PROP_NAME) instanceof JSObject) {
                                JSObject jsCursor = (JSObject) leftRowset.getMember(CURSOR_PROP_NAME);
                                pValue = Scripts.getSpace().toJava(jsCursor.getMember(relation.getLeftField().getName()));
                            } else {
                                pValue = null;
                            }
                        } else {
                            /*
                             Query<?> leftQuery = leftEntity.getQuery();
                             assert leftQuery != null : "Left query must present (Relation points to query, but query is absent)";
                             Parameters leftParams = leftQuery.getParameters();
                             assert leftParams != null : "Parameters of left query must present (Relation points to query parameter, but query parameters are absent)";
                             */
                            Parameter leftParameter = relation.getLeftParameter();
                            if (leftParameter != null) {
                                pValue = leftParameter.getValue();
                                // Let's correct nashorn evil!!!
                                pValue = Scripts.getSpace().toJava(pValue);
                                if (pValue == null) {
                                    pValue = leftParameter.getDefaultValue();
                                }
                                pValue = Scripts.getSpace().toJava(pValue);
                            } else {
                                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, "Parameter of left query must present (Relation points to query parameter in entity: {0} [{1}], but query parameter is absent)", new Object[]{getTitle(), String.valueOf(getEntityId())});
                            }
                        }
                        Parameter selfPm = relation.getRightParameter();
                        if (selfPm != null) {
                            Object selfValue = selfPm.getValue();
                            if (!Objects.equals(selfValue, pValue)) {
                                selfPm.setValue(pValue);
                            }
                        }
                    } else {
                        Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, "Relation with no left entity detected");
                    }
                }
            }
        }
        for (int i = 1; i <= selfParameters.getFieldsCount(); i++) {
            Parameter param = (Parameter) selfParameters.get(i);
            if (param.isModified()) {
                parametersModified = true;
                param.setModified(false);
            }
        }
        if (parametersModified) {
            invalidate();
        }
    }

    public Object executeScriptEvent(final JSObject aHandler, final PublishedSourcedEvent aEvent) {
        Object res = null;
        if (aHandler != null) {
            try {
                return Scripts.getSpace().toJava(aHandler.call(getPublished(), new Object[]{aEvent.getPublished()}));
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return res;
    }

    protected void resignOnCursor() {
        if (cursorListener != null) {
            cursorListener.remove();
            cursorListener = null;
        }
        if (published != null && published.getMember(CURSOR_PROP_NAME) instanceof JSObject) {
            JSObject jsCursor = (JSObject) published.getMember(CURSOR_PROP_NAME);
            JSObject jsReg = Scripts.getSpace().listen(jsCursor, "", new AbstractJSObject() {

                @Override
                public boolean isFunction() {
                    return true;
                }

                @Override
                public Object call(Object thiz, Object... args) {
                    try {
                        internalExecuteChildren(false);
                    } catch (Exception ex) {
                        Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return null;
                }

            });
            cursorListener = () -> {
                Scripts.unlisten(jsReg);
            };
        }
    }
    protected static final String CURSOR_PROP_NAME = "cursor";

    @Override
    protected void assign(E appTarget) throws Exception {
        super.assign(appTarget);
        appTarget.setOnRequeried(onRequeried);
    }

    @Override
    public boolean addInRelation(Relation<E> aRelation) {
        if (aRelation instanceof ReferenceRelation<?>) {
            return false;
        } else {
            return super.addInRelation(aRelation);
        }
    }

    @Override
    public boolean addOutRelation(Relation<E> aRelation) {
        if (aRelation instanceof ReferenceRelation<?>) {
            return false;
        } else {
            return super.addOutRelation(aRelation);
        }
    }
}
