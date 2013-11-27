/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.DelegatingFlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.dataflow.TransactionListener.Registration;
import com.bearsoft.rowset.events.*;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.filters.Filter;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.KeySet;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.SQLUtils;
import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.model.Entity;
import com.eas.client.model.GuiCallback;
import com.eas.client.model.Relation;
import com.eas.client.model.script.RowHostObject;
import com.eas.client.model.script.RowsetHostObject;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.client.model.visitors.ApplicationModelVisitor;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.Query;
import com.eas.script.ScriptUtils;
import com.eas.script.ScriptUtils.ScriptAction;
import com.eas.script.StoredFunction;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public abstract class ApplicationEntity<M extends ApplicationModel<E, ?, ?, Q>, Q extends Query<?>, E extends ApplicationEntity<M, Q, E>> extends Entity<M, Q, E> implements RowsetListener {

    // for runtime
    protected Function onBeforeChange;
    protected Function onAfterChange;
    protected Function onBeforeScroll;
    protected Function onAfterScroll;
    protected Function onBeforeInsert;
    protected Function onAfterInsert;
    protected Function onBeforeDelete;
    protected Function onAfterDelete;
    protected Function onRequeried;
    protected Function onFiltered;
    //
    protected Function instanceConstructor;
    protected RowsetHostObject<E> sRowsetWrap;
    protected Map<String, ScriptableObject> ormDefinitions = new HashMap<>();
    protected transient List<Integer> filterConstraints = new ArrayList<>();
    protected transient Rowset rowset;
    protected transient boolean filteredWhileAjusting;
    protected transient Filter rowsetFilter;
    protected transient boolean userFiltering;
    // to preserve relation order
    protected transient List<Relation<E>> rtInFilterRelations;
    protected transient int updatingCounter = 0;
    protected E substitute;

    public ApplicationEntity() {
        super();
    }

    public ApplicationEntity(M aModel) {
        super(aModel);
    }

    public ApplicationEntity(String aQueryId) {
        super(aQueryId);
    }

    public void putOrmDefinition(String aName, ScriptableObject aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            if (!ormDefinitions.containsKey(aName)) {
                ormDefinitions.put(aName, aDefinition);
            } else {
                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.WARNING, String.format("ORM property %s redefinition attempt on entity %s %s.", aName, name != null && !name.isEmpty() ? name : "", title != null && !title.isEmpty() ? "[" + title + "]" : ""));
            }
        }
    }

    public Map<String, ScriptableObject> getOrmDefinitions() {
        return Collections.unmodifiableMap(ormDefinitions);
    }

    public Function getInstanceConstructor() {
        return instanceConstructor;
    }

    public void setInstanceConstructor(Function aValue) {
        instanceConstructor = aValue;
    }

    @Override
    public void accept(ModelVisitor<E> visitor) {
        if (visitor instanceof ApplicationModelVisitor<?>) {
            ((ApplicationModelVisitor<E>) visitor).visit((E) this);
        }
    }

    public Function getHandler(String aHandlerName) {
        if (aHandlerName != null && !aHandlerName.isEmpty() && model != null && model.getScriptThis() != null) {
            Object oHandlers = model.getScriptThis().get(ScriptUtils.HANDLERS_PROP_NAME, model.getScriptThis());
            if (oHandlers instanceof Scriptable) {
                Scriptable sHandlers = (Scriptable) oHandlers;
                Object oHandler = sHandlers.get(aHandlerName, sHandlers);
                if (oHandler instanceof Function) {
                    return (Function) oHandler;
                }
            }
        }
        return null;
    }

    public RowsetHostObject<E> getRowsetWrap() {
        return sRowsetWrap;
    }

    /**
     * Gets cursor substitute.
     *
     * @return Cursor substitute entity.
     */
    public E getSubstitute() {
        return substitute;
    }

    /**
     * Sets cursor substitute. Use this function carefully. Circular references
     * may occur
     *
     * @param aValue Cursor substitute entity to be set.
     */
    public void setSubstitute(E aValue) {
        if (aValue != this) {
            substitute = aValue;
        }
    }

    public Object getSubstituteRowsetObject(String aFieldName) throws Exception {
        E lsubstitute = substitute;
        while (lsubstitute != null) {
            Rowset sRowset = lsubstitute.getRowset();
            if (sRowset != null && !sRowset.isBeforeFirst() && !sRowset.isAfterLast()) {
                Object value = sRowset.getObject(sRowset.getFields().find(aFieldName));
                if (value != null) {
                    return value;
                }
            }
            lsubstitute = lsubstitute.getSubstitute();
        }
        return null;
    }

    /**
     * Returns change log for this entity. In some cases, we might have several
     * change logs in one model. Several databases is the case.
     *
     * @return
     */
    protected abstract List<Change> getChangeLog() throws Exception;

    public Function getOnAfterChange() {
        return onAfterChange;
    }

    public Function getOnAfterDelete() {
        return onAfterDelete;
    }

    public Function getOnAfterInsert() {
        return onAfterInsert;
    }

    public Function getOnAfterScroll() {
        return onAfterScroll;
    }

    public Function getOnBeforeChange() {
        return onBeforeChange;
    }

    public Function getOnBeforeDelete() {
        return onBeforeDelete;
    }

    public Function getOnBeforeInsert() {
        return onBeforeInsert;
    }

    public Function getOnBeforeScroll() {
        return onBeforeScroll;
    }

    public Function getOnFiltered() {
        return onFiltered;
    }

    public Function getOnRequeried() {
        return onRequeried;
    }

    public void setOnAfterChange(Function aValue) {
        Function oldValue = onAfterChange;
        onAfterChange = aValue;
        changeSupport.firePropertyChange("onAfterChange", oldValue, aValue);
    }

    public void setOnAfterDelete(Function aValue) {
        Function oldValue = onAfterDelete;
        onAfterDelete = aValue;
        changeSupport.firePropertyChange("onAfterDelete", oldValue, aValue);
    }

    public void setOnAfterInsert(Function aValue) {
        Function oldValue = onAfterInsert;
        onAfterInsert = aValue;
        changeSupport.firePropertyChange("onAfterInsert", oldValue, aValue);
    }

    public void setOnAfterScroll(Function aValue) {
        Function oldValue = onAfterScroll;
        onAfterScroll = aValue;
        changeSupport.firePropertyChange("onAfterScroll", oldValue, aValue);
    }

    public void setOnBeforeChange(Function aValue) {
        Function oldValue = onBeforeChange;
        onBeforeChange = aValue;
        changeSupport.firePropertyChange("onBeforeChange", oldValue, aValue);
    }

    public void setOnBeforeDelete(Function aValue) {
        Function oldValue = onBeforeDelete;
        onBeforeDelete = aValue;
        changeSupport.firePropertyChange("onBeforeDelete", oldValue, aValue);
    }

    public void setOnBeforeInsert(Function aValue) {
        Function oldValue = onBeforeInsert;
        onBeforeInsert = aValue;
        changeSupport.firePropertyChange("onBeforeInsert", oldValue, aValue);
    }

    public void setOnBeforeScroll(Function aValue) {
        Function oldValue = onBeforeScroll;
        onBeforeScroll = aValue;
        changeSupport.firePropertyChange("onBeforeScroll", oldValue, aValue);
    }

    public void setOnFiltered(Function aValue) {
        Function oldValue = onFiltered;
        onFiltered = aValue;
        changeSupport.firePropertyChange("onFiltered", oldValue, aValue);
    }

    public void setOnRequeried(Function aValue) {
        Function oldValue = onRequeried;
        onRequeried = aValue;
        changeSupport.firePropertyChange("onRequeried", oldValue, aValue);
    }

    private void silentFirst() throws InvalidCursorPositionException {
        rowset.removeRowsetListener(this);
        try {
            rowset.first();
        } finally {
            rowset.addRowsetListener(this);
        }
    }

    public void beginUpdate() {
        updatingCounter++;
    }

    public void endUpdate() throws Exception {
        assert updatingCounter > 0;
        updatingCounter--;
        if (updatingCounter == 0) {
            internalExecuteChildren(false);
            model.pumpScriptEvents();
        }
    }

    public boolean isRowsetPresent() {
        return rowset != null;
    }

    public Rowset getRowset() throws Exception {
        /*
         if (rowset == null) {
         try {
         execute();
         } catch (Exception ex) {
         rowset = null;
         rowsetFilter = null;
         throw ex;
         }
         }
         */
        return rowset;
    }

    public boolean refresh() throws Exception {
        setExecutedRecursivly(false);
        boolean lexecuted = internalExecute(true);
        internalExecuteChildren(true);
        model.pumpScriptEvents();
        return lexecuted;
    }

    public boolean execute() throws Exception {
        if (model != null/* && model.isRuntime()*/) {
            setExecutedRecursivly(false);
            boolean lexecuted = internalExecute(false);
            internalExecuteChildren(false);
            model.pumpScriptEvents();
            return lexecuted;
        } else {
            return false;
        }
    }
    protected boolean executing = false;
    protected boolean executed = false;

    protected boolean internalExecute(boolean refresh) throws Exception {
        boolean res = false;
        if (!executing && model != null/* && model.isRuntime()*/
                && isAllParentsExecuted()) {
            //assert !datamodel.isAjusting() || !refresh;
            executing = true;
            try {
                if (refresh) {
                    uninstallUserFiltering();
                }
                if (model.isAjusting() && rowset != null) {
                    model.addSavedRowIndex((E) this, rowset.getCursorPos());
                }
                validateQuery();
                // try to select any data only within non manual queries
                // platypus manual queries are:
                //  - insert, update, delete queries;
                //  - stored procedures, witch changes data.
                if (!query.isManual()) {
                    // There might be entities - parameters values sources, with no data in theirs rowsets,
                    // so we can't bind query parameters to proper values. In the such case we initialize
                    // parameters values with RowsetUtils.UNDEFINED_SQL_VALUE
                    boolean parametersBinded = bindQueryParameters();
                    if (parametersBinded || refresh) {
                        // if we have no rowset yet or query parameters values have been changed ...
                        // or we are forced to refresh the data.
                        // requery ...
                        uninstallUserFiltering();
                        refreshRowset();
                        assert rowset != null;
                        // filtering will be done while processing onRequeried event in ApplicationEntity code
                    } else {
                        // if we have no onRequeried event, we call filter manually here.
                        if (rowset != null) {
                            filterRowset();
                            silentFirst();
                        }
                    }
                    res = rowset != null;
                }
            } finally {
                executing = false;
            }
            executed = true;
        }
        return res;
    }

    protected void unforwardChangeLog() {
        if (rowset != null && rowset.getFlowProvider() instanceof DelegatingFlowProvider) {
            DelegatingFlowProvider dfp = (DelegatingFlowProvider) rowset.getFlowProvider();
            rowset.setFlowProvider(dfp.getDelegate());
        }
    }

    protected void forwardChangeLog() {
        rowset.setFlowProvider(new DelegatingFlowProvider(rowset.getFlowProvider()) {
            @Override
            public List<Change> getChangeLog() {
                try {
                    return ApplicationEntity.this.getChangeLog();
                } catch (Exception ex) {
                    Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }

            @Override
            public Registration addTransactionListener(TransactionListener aListener) {
                return model.addTransactionListener(aListener);
            }
        });
    }

    protected boolean isAllParentsExecuted() {
        Set<Relation<E>> inRels = getInRelations();
        if (inRels != null && !inRels.isEmpty()) {
            for (Relation<E> rel : inRels) {
                if (rel != null && rel.getLeftEntity() != null) {
                    E ent = rel.getLeftEntity();
                    if (!ent.executed && !(ent instanceof ApplicationParametersEntity)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void setExecutedRecursivly(boolean aExecuted) {
        executed = aExecuted;
        Set<Relation<E>> rels = getOutRelations();
        if (rels != null) {
            for (Relation<E> outRel : rels) {
                if (outRel != null) {
                    E ent = outRel.getRightEntity();
                    if (ent != null) {
                        ent.setExecutedRecursivly(aExecuted);
                    }
                }
            }
        }
    }

    protected void uninstallUserFiltering() throws RowsetException {
        if (userFiltering && rowset != null && rowset.getActiveFilter() != null) {
            rowset.getActiveFilter().cancelFilter();
        }
        userFiltering = false;
    }

    public void refreshChildren() throws Exception {
        boolean oldExecuted = executed;
        try {
            setExecutedRecursivly(false);
        } finally {
            executed = oldExecuted;
        }
        internalExecuteChildren(true);
        model.pumpScriptEvents();
    }

    protected void executeChildren() throws Exception {
        boolean oldExecuted = executed;
        try {
            setExecutedRecursivly(false);
        } finally {
            executed = oldExecuted;
        }
        executed = true;
        internalExecuteChildren(false);
        model.pumpScriptEvents();
    }

    protected void internalExecuteChildren(boolean refresh) throws Exception {
        if (updatingCounter == 0) {
            Set<Relation<E>> rels = getOutRelations();
            if (rels != null) {
                Set<E> toExecute = new HashSet<>();
                for (Relation<E> outRel : rels) {
                    if (outRel != null && outRel.getRightEntity() != null) {
                        toExecute.add(outRel.getRightEntity());
                    }
                }
                for (E entity : toExecute) {
                    entity.internalExecute(refresh);
                }
                Set<E> childrenToExecute = toExecute;
                while (!childrenToExecute.isEmpty()) {
                    childrenToExecute = internalExecuteChildrenImpl(refresh, childrenToExecute);
                }
            }
        }
    }

    protected void internalExecuteChildren(boolean refresh, int aOnlyFieldIndex) throws Exception {
        if (updatingCounter == 0) {
            Set<Relation<E>> rels = getOutRelations();
            if (rels != null) {
                Field onlyField = getFields().get(aOnlyFieldIndex);
                Set<E> toExecute = new HashSet<>();
                for (Relation<E> outRel : rels) {
                    if (outRel != null) {
                        E ent = outRel.getRightEntity();
                        if (ent != null && outRel.getLeftField() == onlyField) {
                            toExecute.add(ent);
                        }
                    }
                }
                for (E entity : toExecute) {
                    entity.internalExecute(refresh);
                }
                Set<E> childrenToExecute = toExecute;
                while (!childrenToExecute.isEmpty()) {
                    childrenToExecute = internalExecuteChildrenImpl(refresh, childrenToExecute);
                }
            }
        }
    }

    public static <E extends ApplicationEntity<?, ?, E>> Set<E> internalExecuteChildrenImpl(boolean refresh, Collection<E> toExecute) throws Exception {
        Set<E> childrenToExecute = new HashSet<>();
        for (E entity : toExecute) {
            Set<Relation<E>> rels = entity.getOutRelations();
            if (rels != null) {
                for (Relation<E> outRel : rels) {
                    if (outRel != null && outRel.getRightEntity() != null) {
                        childrenToExecute.add(outRel.getRightEntity());
                    }
                }
            }
        }
        for (E entity : childrenToExecute) {
            entity.internalExecute(refresh);
        }
        return childrenToExecute;
    }

    public Scriptable defineProperties() throws Exception {
        if (model.getScriptThis() != null && model.getScriptThis() instanceof ScriptableObject) {
            if (name != null && !name.isEmpty()) {
                ScriptableRowset<E> sRowset = new ScriptableRowset<>((E) this);
                sRowsetWrap = new RowsetHostObject<>(sRowset, model.getScriptThis());
                ((ScriptableObject) model.getScriptThis()).defineProperty(name, sRowsetWrap, ScriptableObject.READONLY);
                return sRowsetWrap;
            }
        }
        return null;
    }

    @Override
    public void setQueryId(String aValue) {
        if ((queryId == null && aValue != null)
                || (queryId != null && aValue == null)
                || (queryId != null && !queryId.equals(aValue))) {
            query = null;
            rowset = null;
            rowsetFilter = null;
        }
        if (aValue != null) {
            tableName = null;
        }
        super.setQueryId(aValue);
    }

    @Override
    public void setTableName(String aValue) {
        if ((tableName == null && aValue != null)
                || (tableName != null && aValue == null)
                || (tableName != null && !tableName.equals(aValue))) {
            query = null;
            rowset = null;
            rowsetFilter = null;
        }
        if (aValue != null && !aValue.isEmpty()) {
            queryId = null;
        }
        super.setTableName(aValue);
    }

    @Override
    public void setTableSchemaName(String aValue) {
        if ((tableSchemaName == null && aValue != null)
                || (tableSchemaName != null && aValue == null)
                || (tableSchemaName != null && !tableSchemaName.equals(aValue))) {
            query = null;
            rowset = null;
            rowsetFilter = null;
        }
        super.setTableSchemaName(aValue);
    }

    /**
     * WARNING!!! This nethod is for external use only. It allows to set a
     * rowset from any environment and so it resets entitiy state e.g. executed
     * and executing flags are resetted, entity is re-signed to rowset's events
     * and fields is resetted with rowset's fields.
     *
     * @param aRowset
     */
    public void setRowset(Rowset aRowset) {
        Rowset oldRowset = rowset;
        if (rowset != null) {
            rowset.removeRowsetListener(this);
        }
        rowset = aRowset;
        fields = rowset.getFields();
        executed = true;
        executing = false;
        if (rowset != null) {
            rowset.addRowsetListener(this);
            changeSupport.firePropertyChange("rowset", oldRowset, rowset);
        }
    }

    protected abstract void refreshRowset() throws Exception;

    @Override
    public Fields getFields() {
        super.getFields();
        if (fields == null) {
            try {
                Rowset rs = getRowset();
                if (rs != null) {
                    fields = rs.getFields();
                }
            } catch (Exception ex) {
                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fields;
    }

    public boolean isUserFiltering() {
        return userFiltering;
    }

    public void setUserFiltering(boolean aUserFiltering) throws Exception {
        boolean oldUserFiltering = userFiltering;
        userFiltering = aUserFiltering;
        if (oldUserFiltering != userFiltering) {
            if (rowset.getActiveFilter() != null) {
                rowset.getActiveFilter().cancelFilter();
            }
            execute();
        }
    }

    protected boolean isFilterable() throws Exception {
        return rowset != null && !userFiltering && rtInFilterRelations != null && !rtInFilterRelations.isEmpty();
    }

    protected boolean isQueriable() throws Exception {
        return queryId != null || (tableName != null && !tableName.isEmpty());
    }

    public boolean bindQueryParameters() throws Exception {
        Parameters selfParameters = getQuery().getParameters();
        // Let's correct javascript evil!!!
        for (int i = 1; i <= selfParameters.getFieldsCount(); i++) {
            Parameter p = selfParameters.get(i);
            boolean oldModified = p.isModified();
            p.setValue(ScriptUtils.js2Java(p.getValue()));
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
                            Rowset leftRowset = leftEntity.getRowset();
                            if (leftRowset != null && !leftRowset.isEmpty() && !leftRowset.isBeforeFirst() && !leftRowset.isAfterLast()) {
                                try {
                                    pValue = leftRowset.getObject(leftRowset.getFields().find(relation.getLeftField().getName()));
                                } catch (InvalidColIndexException | InvalidCursorPositionException ex) {
                                    pValue = RowsetUtils.UNDEFINED_SQL_VALUE;
                                    Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "while assigning parameter:" + relation.getRightParameter() + " in entity: " + getTitle() + " [" + String.valueOf(getEntityId()) + "]", ex);
                                }
                            } else {
                                pValue = RowsetUtils.UNDEFINED_SQL_VALUE;
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
                                if (pValue == null) {
                                    pValue = leftParameter.getDefaultValue();
                                }
                            } else {
                                Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "Parameter of left query must present (Relation points to query parameter in entity: {0} [{1}], but query parameter is absent)", new Object[]{getTitle(), String.valueOf(getEntityId())});
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
                        Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "Relation with no left entity detected");
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
        return parametersModified;
    }

    protected void validateInFilterRelations() {
        // never build yet, so build it ...
        if (rtInFilterRelations == null) {
            rtInFilterRelations = new ArrayList<>();
            assert rowset != null;
            Set<Relation<E>> inRels = getInRelations();
            if (inRels != null) {
                for (Relation<E> rel : inRels) {
                    if (rel != null && rel.isRightField()) {
                        rtInFilterRelations.add(rel);
                    }
                }
            }
        }
    }

    protected void validateFilter() throws RowsetException {
        assert rtInFilterRelations != null;
        assert rowset != null;
        if (rowsetFilter == null && !rtInFilterRelations.isEmpty()) {
            List<Field> constraints = new ArrayList<>();
            // enumerate filtering relations ...
            for (Relation<E> rel : rtInFilterRelations) {
                assert rel != null && rel.isRightField();
                constraints.add(rel.getRightField());
            }
            if (!constraints.isEmpty()) {
                rowsetFilter = rowset.createFilter();
                rowsetFilter.beginConstrainting();
                try {
                    Fields rFields = rowset.getFields();
                    for (Field field : constraints) {
                        // entity's and rowset's fields may differ.
                        rowsetFilter.addConstraint(rFields.find(field.getName()));
                    }
                } finally {
                    rowsetFilter.endConstrainting();
                }
                rowsetFilter.build();
            }
        }
    }

    public boolean filterRowset() throws Exception {
        validateInFilterRelations();
        if (isFilterable()) {
            validateFilter();
            return applyFilter();
        } else {
            return false;
        }
    }

    public boolean applyFilter() throws Exception {
        try {
            assert !userFiltering : "Can't apply own filter while user filtering";
            assert rowset != null : "Bad requery -> filter chain";
            KeySet filterKeySet = new KeySet();
            if (!rtInFilterRelations.isEmpty()) {
                for (Relation<E> rel : rtInFilterRelations) {
                    // relation must be filtering relation ...
                    assert rel != null && rel.isRightField();
                    E leftEntity = rel.getLeftEntity();
                    assert leftEntity != null;
                    Object fValue = null;
                    if (rel.isLeftField()) {
                        Rowset leftRowset = leftEntity.getRowset();
                        if (leftRowset != null) {
                            try {
                                if (!leftRowset.isEmpty()) {
                                    if (!leftRowset.isBeforeFirst() && !leftRowset.isAfterLast()) {
                                        fValue = leftRowset.getObject(leftRowset.getFields().find(rel.getLeftField().getName()));
                                    } else {
                                        fValue = RowsetUtils.UNDEFINED_SQL_VALUE;
                                        Logger.getLogger(Entity.class.getName()).log(Level.FINE, "Failed to achieve value for filtering field:{0} in entity: {1} [{2}]. The source rowset has bad position (before first or after last).", new Object[]{rel.getRightField(), getTitle(), String.valueOf(getEntityId())});
                                    }
                                } else {
                                    fValue = RowsetUtils.UNDEFINED_SQL_VALUE;
                                    Logger.getLogger(Entity.class.getName()).log(Level.FINE, "Failed to achieve value for filtering field:{0} in entity: {1} [{2}]. The source rowset has no any rows.", new Object[]{rel.getRightField(), getTitle(), String.valueOf(getEntityId())});
                                }
                            } catch (InvalidColIndexException | InvalidCursorPositionException ex) {
                                Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "while achieving value for filtering field:" + rel.getRightField() + " in entity: " + getTitle() + " [" + String.valueOf(getEntityId()) + "]", ex);
                                throw ex;
                            }
                        } else {
                            fValue = RowsetUtils.UNDEFINED_SQL_VALUE;
                            Logger.getLogger(Entity.class.getName()).log(Level.FINE, "Failed to achieve value for filtering field:{0} in entity: {1} [{2}]. The source rowset is absent.", new Object[]{rel.getRightField(), getTitle(), String.valueOf(getEntityId())});
                        }
                    } else {
                        /*
                         Q leftQuery = leftEntity.getQuery();
                         assert leftQuery != null : "Left query must present (Relation points to query, but query is absent)";
                         Parameters leftParams = leftQuery.getParameters();
                         assert leftParams != null : "Parameters of left query must present (Relation points to query parameter, but query parameters are absent)";
                         */
                        Parameter leftParameter = rel.getLeftParameter();
                        if (leftParameter != null) {
                            fValue = leftParameter.getValue();
                            if (fValue == null) {
                                fValue = leftParameter.getDefaultValue();
                            }
                        } else {
                            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "Parameter of left query must present (Relation points to query parameter, but query parameter with specified name is absent)");
                        }
                    }
                    Converter conv = rowset.getConverter();
                    Field fieldOfValue = rowset.getFields().get(rel.getRightField().getName());
                    filterKeySet.add(conv.convert2RowsetCompatible(fValue, fieldOfValue.getTypeInfo()));
                }
            }
            Filter activeFilter = rowset.getActiveFilter();
            if (rowsetFilter != null && !rowsetFilter.isEmpty()
                    && (rowsetFilter != activeFilter || !rowsetFilter.getKeysetApplied().equals(filterKeySet))) {
                rowsetFilter.filterRowset(filterKeySet);
                return true;
            } else {
                return false;


            }
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);

            throw ex;
        }
    }

    public Object executeScriptEvent(final Function aHandler, final ScriptSourcedEvent aEvent) {
        Object res = null;
        if (aHandler != null) {
            try {
                final Scriptable scope = model.getScriptThis();
                if (scope != null) {
                    model.fireScriptEventExecuting((E) this, scope, aHandler, aEvent);
                    res = ScriptUtils.inContext(new ScriptAction() {
                        @Override
                        public Object run(Context cx) throws Exception {
                            Object[] args = new Object[]{ScriptUtils.javaToJS(aEvent, scope)};
                            return aHandler.call(cx, scope, sRowsetWrap != null ? sRowsetWrap : scope, args);
                        }
                    });
                }
            } catch (Exception ex) {
                if (!(ex instanceof IllegalStateException) || ex.getMessage() == null || !ex.getMessage().equals("break")) {
                    if (ex.getMessage() != null) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, " while executing \"" + aHandler + "\" event handler.", ex);
                        if (model.getGuiCallback() != null) {
                            model.getGuiCallback().showMessageDialog(ex.getMessage(), "Error", GuiCallback.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
        return res;
    }

    private void enqueueScriptEvent(Function aHandler, ScriptSourcedEvent aEvent) {
        model.enqueueScriptEvent((E) this, aHandler, aEvent);
    }

    public static class CursorPositionWillChangeEvent extends ScriptSourcedEvent {

        protected int newIndex;

        public CursorPositionWillChangeEvent(Scriptable aSource, int newIndex) {
            super(aSource);
            this.newIndex = newIndex;
        }

        public int getNewIndex() {
            return newIndex;
        }
    }

    public static class CursorPositionChangedEvent extends CursorPositionWillChangeEvent {

        protected int oldIndex;

        public CursorPositionChangedEvent(Scriptable aSource, int oldIndex, int newIndex) {
            super(aSource, newIndex);
            this.oldIndex = oldIndex;
        }

        public int getOldIndex() {
            return oldIndex;
        }
    }

    @Override
    public boolean willScroll(final RowsetScrollEvent aEvent) {
        boolean res = true;
        assert aEvent.getRowset() == rowset;
        if (model.isAjusting()) {
            model.addSavedRowIndex((E) this, aEvent.getOldRowIndex());
        } else {
            try {
                res = ScriptUtils.inContext(new ScriptAction() {
                    @Override
                    public Boolean run(Context cx) throws Exception {
                        // call script method
                        Object sRes = executeScriptEvent(onBeforeScroll, new CursorPositionWillChangeEvent(sRowsetWrap, aEvent.getNewRowIndex()));
                        if (sRes != null && sRes instanceof Boolean) {
                            return (Boolean) sRes;
                        } else {
                            return true;
                        }
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return res;
    }

    @Override
    public void rowsetScrolled(RowsetScrollEvent aEvent) {
        Rowset easRs = aEvent.getRowset();
        if (aEvent.getNewRowIndex() >= 0 && aEvent.getNewRowIndex() <= easRs.size() + 1) {
            try {
                if (!model.isAjusting()) {
                    // call script method
                    enqueueScriptEvent(onAfterScroll, new CursorPositionChangedEvent(sRowsetWrap, aEvent.getOldRowIndex(), aEvent.getNewRowIndex()));
                }
                if (!executing) {
                    internalExecuteChildren(false);
                    model.pumpScriptEvents();
                }
            } catch (Exception ex) {
                Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static class EntityInstanceChangeEvent extends ScriptSourcedEvent {

        protected Field field;
        protected Object oldValue;
        protected Object newValue;

        public EntityInstanceChangeEvent(Scriptable aSource, Field field, Object oldValue, Object newValue) {
            super(aSource);
            this.field = field;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public Field getField() {
            return field;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        public Scriptable getObject() {
            return source;
        }
    }

    @Override
    public boolean willChangeRow(final RowChangeEvent aEvent) {
        boolean assertres = model.isAjusting();
        assert !assertres;
        boolean res = true;
        Fields fmdv = getFields();
        if (fmdv != null) {
            final Field field = fmdv.get(aEvent.getFieldIndex());
            if (field != null) {
                try {
                    // call script method
                    res = ScriptUtils.inContext(new ScriptAction() {
                        @Override
                        public Boolean run(Context cx) throws Exception {
                            Object sRes = executeScriptEvent(onBeforeChange, new EntityInstanceChangeEvent(RowHostObject.publishRow(model.getScriptThis(), aEvent.getChangedRow(), ApplicationEntity.this), field, ScriptUtils.javaToJS(aEvent.getOldValue(), model.getScriptThis()), ScriptUtils.javaToJS(aEvent.getNewValue(), model.getScriptThis())));
                            if (sRes != null && sRes instanceof Boolean) {
                                return (Boolean) sRes;
                            } else {
                                return true;
                            }
                        }
                    });
                } catch (Exception ex) {
                    Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

    @Override
    public void rowChanged(final RowChangeEvent aEvent) {
        try {
            boolean assertres = model.isAjusting();
            assert !assertres;
            Fields lfields = getFields();
            if (lfields != null) {
                final Field field = lfields.get(aEvent.getFieldIndex());
                if (field != null) {
                    ScriptUtils.inContext(new ScriptAction() {
                        @Override
                        public Object run(Context cx) throws Exception {
                            enqueueScriptEvent(onAfterChange, new EntityInstanceChangeEvent(RowHostObject.publishRow(model.getScriptThis(), aEvent.getChangedRow(), ApplicationEntity.this), field, ScriptUtils.javaToJS(aEvent.getOldValue(), model.getScriptThis()), ScriptUtils.javaToJS(aEvent.getNewValue(), model.getScriptThis())));
                            return null;
                        }
                    });
                }
            }
            internalExecuteChildren(false, aEvent.getFieldIndex());
            model.pumpScriptEvents();
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static class EntityInstanceInsert extends ScriptSourcedEvent {

        protected RowHostObject inserted;

        public EntityInstanceInsert(Scriptable source, RowHostObject inserted) {
            super(source);
            this.inserted = inserted;
        }

        public RowHostObject getInserted() {
            return inserted;
        }

        public RowHostObject getObject() {
            return inserted;
        }
    }

    @Override
    public boolean willInsertRow(final RowsetInsertEvent event) {
        boolean res = true;
        // call script method
        assert !model.isAjusting();
        try {
            res = ScriptUtils.inContext(new ScriptAction() {
                @Override
                public Boolean run(Context cx) throws Exception {
                    Object sRes = executeScriptEvent(onBeforeInsert, new EntityInstanceInsert(sRowsetWrap, RowHostObject.publishRow(model.getScriptThis(), event.getRow(), ApplicationEntity.this)));
                    if (sRes != null && sRes instanceof Boolean) {
                        return (Boolean) sRes;
                    } else {
                        return true;
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public static class EntityInstanceDelete extends ScriptSourcedEvent {

        protected RowHostObject deleted;

        public EntityInstanceDelete(Scriptable source, RowHostObject deleted) {
            super(source);
            this.deleted = deleted;
        }

        public RowHostObject getDeleted() {
            return deleted;
        }
    }

    @Override
    public boolean willDeleteRow(final RowsetDeleteEvent event) {
        boolean res = true;
        // call script method
        assert !model.isAjusting();
        try {
            res = ScriptUtils.inContext(new ScriptAction() {
                @Override
                public Boolean run(Context cx) throws Exception {
                    Object sRes = executeScriptEvent(onBeforeDelete, new EntityInstanceDelete(sRowsetWrap, RowHostObject.publishRow(model.getScriptThis(), event.getRow(), ApplicationEntity.this)));
                    if (sRes != null && sRes instanceof Boolean) {
                        return (Boolean) sRes;
                    } else {
                        return true;
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public void rowInserted(final RowsetInsertEvent event) {
        try {
            boolean assertres = model.isAjusting();
            assert !assertres;
            // call script method
            ScriptUtils.inContext(new ScriptAction() {
                @Override
                public Object run(Context cx) throws Exception {
                    enqueueScriptEvent(onAfterInsert, new EntityInstanceInsert(sRowsetWrap, RowHostObject.publishRow(model.getScriptThis(), event.getRow(), ApplicationEntity.this)));
                    return null;
                }
            });
            internalExecuteChildren(false);
            model.pumpScriptEvents();
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowDeleted(final RowsetDeleteEvent event) {
        try {
            boolean assertres = model.isAjusting();
            assert !assertres;
            // call script method
            ScriptUtils.inContext(new ScriptAction() {
                @Override
                public Object run(Context cx) throws Exception {
                    enqueueScriptEvent(onAfterDelete, new EntityInstanceDelete(sRowsetWrap, RowHostObject.publishRow(model.getScriptThis(), event.getRow(), ApplicationEntity.this)));
                    return null;
                }
            });
            internalExecuteChildren(false);
            model.pumpScriptEvents();
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        try {
            // call script method
            if (!model.isAjusting()) {
                enqueueScriptEvent(onFiltered, new ScriptSourcedEvent(sRowsetWrap));
            }
            if (!executing) {
                internalExecuteChildren(false);
                model.pumpScriptEvents();
            }
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
        try {
            // call script method
            if (!model.isAjusting()) {
                enqueueScriptEvent(onFiltered, new ScriptSourcedEvent(sRowsetWrap));
            }
            if (!executing) {
                internalExecuteChildren(false);
                model.pumpScriptEvents();
            }
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        try {
            assert rowset != null;
            filterRowset();
            silentFirst();
            // filtering must go here, because of onRequiried script event is an endpoint of the network process. And it expects the data will be processed already before it will be called.
            // So, onFiltered script event goes before onRequeired script event.

            // call script method
            if (!model.isAjusting()) {
                enqueueScriptEvent(onRequeried, new ScriptSourcedEvent(sRowsetWrap));
            }
            if (!executing) {
                internalExecuteChildren(false);
                model.pumpScriptEvents();
            }
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        try {
            // call script method
            if (!model.isAjusting()) {
                enqueueScriptEvent(onRequeried, new ScriptSourcedEvent(sRowsetWrap));
            }
            if (!executing) {
                internalExecuteChildren(false);
                model.pumpScriptEvents();
            }
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetSaved(RowsetSaveEvent event) {
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
    }

    @Override
    public boolean willFilter(RowsetFilterEvent event) {
        return true;
    }

    @Override
    public boolean willRequery(RowsetRequeryEvent event) {
        return true;
    }

    @Override
    public boolean willSort(RowsetSortEvent event) {
        return true;
    }

    @Override
    public boolean willNextPageFetch(RowsetNextPageEvent event) {
        return true;
    }

    @Override
    protected void assign(E appTarget) throws Exception {
        super.assign(appTarget);
        appTarget.setOnAfterChange(onAfterChange);
        appTarget.setOnAfterDelete(onAfterDelete);
        appTarget.setOnAfterInsert(onAfterInsert);
        appTarget.setOnAfterScroll(onAfterScroll);
        appTarget.setOnFiltered(onFiltered);
        appTarget.setOnRequeried(onRequeried);
        appTarget.setOnBeforeChange(onBeforeChange);
        appTarget.setOnBeforeDelete(onBeforeDelete);
        appTarget.setOnBeforeInsert(onBeforeInsert);
        appTarget.setOnBeforeScroll(onBeforeScroll);
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

    protected void resolveHandlers() {
        if (onAfterChange instanceof StoredFunction) {
            onAfterChange = getHandler(((StoredFunction) onAfterChange).getName());
        }
        if (onAfterDelete instanceof StoredFunction) {
            onAfterDelete = getHandler(((StoredFunction) onAfterDelete).getName());
        }
        if (onAfterInsert instanceof StoredFunction) {
            onAfterInsert = getHandler(((StoredFunction) onAfterInsert).getName());
        }
        if (onAfterScroll instanceof StoredFunction) {
            onAfterScroll = getHandler(((StoredFunction) onAfterScroll).getName());
        }
        if (onBeforeChange instanceof StoredFunction) {
            onBeforeChange = getHandler(((StoredFunction) onBeforeChange).getName());
        }
        if (onBeforeDelete instanceof StoredFunction) {
            onBeforeDelete = getHandler(((StoredFunction) onBeforeDelete).getName());
        }
        if (onBeforeInsert instanceof StoredFunction) {
            onBeforeInsert = getHandler(((StoredFunction) onBeforeInsert).getName());
        }
        if (onBeforeScroll instanceof StoredFunction) {
            onBeforeScroll = getHandler(((StoredFunction) onBeforeScroll).getName());
        }
        if (onFiltered instanceof StoredFunction) {
            onFiltered = getHandler(((StoredFunction) onFiltered).getName());
        }
        if (onRequeried instanceof StoredFunction) {
            onRequeried = getHandler(((StoredFunction) onRequeried).getName());
        }
    }
}
