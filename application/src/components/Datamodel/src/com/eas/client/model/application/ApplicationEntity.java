/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.SQLUtils;
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
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
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
    protected JSObject onScrolled;
    protected JSObject onInserted;
    protected JSObject onDeleted;
    protected JSObject onRequeried;
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
            Exception ex = new InterruptedException("Canceled");
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

    public void execute(Consumer<Void> aOnSuccess) throws Exception {
        execute(aOnSuccess, null);
    }
    private static final String EXECUTE_JSDOC = ""
            + "/**\n"
            + "* Refreshes entity, only if any of its parameters has changed.\n"
            + "* @param onSuccess The handler function for refresh data on success event (optional).\n"
            + "* @param onFailure The handler function for refresh data on failure event (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = EXECUTE_JSDOC, params = {"onSuccess", "onFailure"})
    public void execute(final Consumer<Void> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        internalExecute(aOnSuccess, aOnFailure);
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
            + "* Requeries the entity's data. Forses the entity to refresh its data, no matter if its parameters has changed or not.\n"
            + "* @param onSuccess The callback function for refresh data on success event (optional).\n"
            + "* @param onFailure The callback function for refresh data on failure event (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = REQUERY_JSDOC, params = {"onSuccess", "onFailure"})
    public void requery(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        invalidate();
        internalExecute(aOnSuccess != null ? (Void v) -> {
            aOnSuccess.call(null, new Object[]{});
        } : null, aOnFailure != null ? (Exception ex) -> {
            aOnFailure.call(null, new Object[]{ex.getMessage()});
        } : null);
    }

    private static final String INSTANCE_CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* Experimental. The constructor funciton for the entity's data array elements.\n"
            + "*/";

    @ScriptFunction(jsDoc = INSTANCE_CONSTRUCTOR_JSDOC)
    public JSObject getElementClass() {
        return getFields().getInstanceConstructor();
    }

    @ScriptFunction
    public void setElementClass(JSObject aValue) {
        getFields().setInstanceConstructor(aValue);
    }

    private static final String ON_DELETED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after an entity row has been deleted.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_DELETED_JSDOC)
    @EventMethod(eventClass = EntityInstanceDeleteEvent.class)
    public JSObject getOnDeleted() {
        return onDeleted;
    }

    @ScriptFunction
    public void setOnDeleted(JSObject aValue) {
        JSObject oldValue = onDeleted;
        onDeleted = aValue;
        changeSupport.firePropertyChange("onDeleted", oldValue, aValue);
    }
    private static final String ON_INSERTED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after an entity row has been inserted.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_INSERTED_JSDOC)
    @EventMethod(eventClass = EntityInstanceInsertEvent.class)
    public JSObject getOnInserted() {
        return onInserted;
    }

    @ScriptFunction
    public void setOnInserted(JSObject aValue) {
        JSObject oldValue = onInserted;
        onInserted = aValue;
        changeSupport.firePropertyChange("onInserted", oldValue, aValue);
    }
    private static final String ON_SCROLLED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after the cursor position changed.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_SCROLLED_JSDOC)
    @EventMethod(eventClass = CursorPositionChangedEvent.class)
    public JSObject getOnScrolled() {
        return onScrolled;
    }

    @ScriptFunction
    public void setOnScrolled(JSObject aValue) {
        JSObject oldValue = onScrolled;
        onScrolled = aValue;
        changeSupport.firePropertyChange("onScrolled", oldValue, aValue);
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
    public void setPublished(JSObject aValue) {
        if (published != null && com.eas.script.ScriptUtils.isInitialized()) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    /**
     * Returns change log for this entity. In some cases, we might have several
     * change logs in one model. Several databases is the case.
     *
     * @throws java.lang.Exception
     * @return
     */
    protected abstract List<Change> getChangeLog() throws Exception;

    private static final String ON_REQUIRED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after the entity's data have been requeried.\n"
            + "*/";

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

    public abstract void enqueueUpdate() throws Exception;

    protected void internalExecute(final Consumer<Void> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        if (query == null) {
            throw new IllegalStateException("Query must present. Query name: " + queryName + "; tableName: " + getTableNameForDescription());
        }
        bindQueryParameters();
        if (isValid()) {
            if (aOnSuccess != null) {
                aOnSuccess.accept(null);
            }
        } else {
            // Requery if query parameters values have been changed while bindQueryParameters() call
            // or we are forced to refresh the data via requery() call.
            silentUnpend();
            refreshRowset(aOnSuccess, aOnFailure);
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
    
    protected void refreshRowset(final Consumer<Void> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        if (model.process != null || aOnSuccess != null) {
            Future<Void> f = new RowsetRefreshTask(aOnFailure);
            query.execute((JSObject aRowset) -> {
                if (!f.isCancelled()) {
                    // Apply aRowse as a snapshot. Be aware of change log!
                    assert pending == f : PENDING_ASSUMPTION_FAILED_MSG;
                    valid = true;
                    pending = null;
                    model.terminateProcess((E)ApplicationEntity.this, null);
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
                    model.terminateProcess((E)ApplicationEntity.this, ex);
                    if (aOnFailure != null) {
                        aOnFailure.accept(ex);
                    }
                }
            });
            pending = f;
        } else {
            JSObject jsRowset = query.execute(null, null);
            // Apply aRowse as a snapshot. Be aware of change log!
        }
    }
    
    protected boolean isQueriable() throws Exception {
        return queryName != null || (tableName != null && !tableName.isEmpty());
    }

    public void bindQueryParameters() throws Exception {
        Parameters selfParameters = getQuery().getParameters();
        // Let's correct script evil!!!
        for (int i = 1; i <= selfParameters.getFieldsCount(); i++) {
            Parameter p = selfParameters.get(i);
            boolean oldModified = p.isModified();
            p.setValue(ScriptUtils.toJava(p.getValue()));
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
                            // such case we initialize parameters values with RowsetUtils.UNDEFINED_SQL_VALUE
                            JSObject leftRowset = leftEntity.getPublished();
                            if (leftRowset != null && leftRowset.getMember(CURSOR_PROP_NAME) instanceof JSObject) {
                                JSObject jsCursor = (JSObject) leftRowset.getMember(CURSOR_PROP_NAME);
                                pValue = ScriptUtils.toJava(jsCursor.getMember(relation.getLeftField().getName()));
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
                                // Let's correct Rhino evil!!!
                                pValue = ScriptUtils.toJava(pValue);
                                if (pValue == null) {
                                    pValue = leftParameter.getDefaultValue();
                                }
                                pValue = ScriptUtils.toJava(pValue);
                            } else {
                                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, "Parameter of left query must present (Relation points to query parameter in entity: {0} [{1}], but query parameter is absent)", new Object[]{getTitle(), String.valueOf(getEntityId())});
                            }
                        }
                        Parameter selfPm = relation.getRightParameter();
                        if (selfPm != null) {
                            Object selfValue = selfPm.getValue();
                            if (!SQLUtils.isJdbcEqual(selfValue, pValue)) {
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
                return ScriptUtils.toJava(aHandler.call(getPublished(), new Object[]{aEvent.getPublished()}));
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return res;
    }
    /*
     @Override
     public void rowsetScrolled(RowsetScrollEvent aEvent) {
     resignOnCursor();
     if (aEvent.getNewRowIndex() >= 0 && aEvent.getNewRowIndex() <= rowset.size() + 1) {
     try {
     // call script method
     executeScriptEvent(onScrolled, new CursorPositionChangedEvent(this, aEvent.getOldRowIndex(), aEvent.getNewRowIndex()));
     internalExecuteChildren(false);
     } catch (Exception ex) {
     Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     }

     @Override
     public void rowInserted(final RowsetInsertEvent event) {
     resignOnCursor();
     try {
     // call script method
     executeScriptEvent(onInserted, new EntityInstanceInsertEvent(this, event.getRow()));
     internalExecuteChildren(false);
     } catch (Exception ex) {
     Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
     }
     }

     @Override
     public void rowDeleted(final RowsetDeleteEvent event) {
     resignOnCursor();
     try {
     // call script method
     executeScriptEvent(onDeleted, new EntityInstanceDeleteEvent(this, event.getRow()));
     internalExecuteChildren(false);
     } catch (Exception ex) {
     Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
     }
     }

     @Override
     public void rowsetSorted(RowsetSortEvent event) {
     try {
     resignOnCursor();
     // call script method
     executeScriptEvent(onFiltered, new PublishedSourcedEvent(this));
     internalExecuteChildren(false);
     } catch (Exception ex) {
     Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
     }
     }

     @Override
     public void beforeRequery(RowsetRequeryEvent rre) {
     }

     @Override
     public void rowsetRequeried(RowsetRequeryEvent event) {
     resignOnCursor();
     try {
     // call script method
     executeScriptEvent(onRequeried, new PublishedSourcedEvent(this));
     internalExecuteChildren(false);
     } catch (Exception ex) {
     Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
     }
     }

     @Override
     public void rowsetNextPageFetched(RowsetNextPageEvent event) {
     resignOnCursor();
     try {
     // call script method
     executeScriptEvent(onRequeried, new PublishedSourcedEvent(this));
     internalExecuteChildren(false);
     } catch (Exception ex) {
     Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
     }
     }

     @Override
     public void rowsetRolledback(RowsetRollbackEvent event) {
     resignOnCursor();
     try {
     internalExecuteChildren(false);
     } catch (Exception ex) {
     Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     */

    protected void resignOnCursor() {
        if (cursorListener != null) {
            cursorListener.remove();
            cursorListener = null;
        }
        if (published != null && published.getMember(CURSOR_PROP_NAME) instanceof JSObject) {
            JSObject jsCursor = (JSObject) published.getMember(CURSOR_PROP_NAME);
            JSObject jsReg = ScriptUtils.listen(jsCursor, "", new AbstractJSObject() {

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
                ScriptUtils.unlisten(jsReg);
            };
        }
    }
    protected static final String CURSOR_PROP_NAME = "cursor";

    @Override
    protected void assign(E appTarget) throws Exception {
        super.assign(appTarget);
        appTarget.setOnDeleted(onDeleted);
        appTarget.setOnInserted(onInserted);
        appTarget.setOnScrolled(onScrolled);
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
