/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.model.store.ApplicationModel2XmlDom;
import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.Client;
import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ModelScriptEventsListener;
import com.eas.client.model.ModelScriptEventsSupport;
import com.eas.client.model.Relation;
import com.eas.client.model.script.ApplicationModelHostObject;
import com.eas.client.model.script.ScriptEvent;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.client.model.visitors.ApplicationModelVisitor;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.Query;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import com.eas.script.ScriptUtils.ScriptAction;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public abstract class ApplicationModel<E extends ApplicationEntity<?, Q, E>, P extends E, C extends Client, Q extends Query<C>> extends Model<E, P, C, Q> {

    protected Set<ReferenceRelation<E>> referenceRelations = new HashSet<>();
    protected Set<Long> savedRowIndexEntities = new HashSet<>();
    protected List<Entry<E, Integer>> savedEntitiesRowIndexes = new ArrayList<>();
    protected List<ScriptEvent<E>> scriptEventsQueue = new ArrayList<>();
    private boolean pumpingScriptEvents = false;
    protected ModelScriptEventsSupport<E> scriptEventsSupport = new ModelScriptEventsSupport<>();
    protected Set<TransactionListener> transactionListeners = new HashSet<>();

    public TransactionListener.Registration addTransactionListener(final TransactionListener aListener) {
        transactionListeners.add(aListener);
        return new TransactionListener.Registration() {
            @Override
            public void remove() {
                transactionListeners.remove(aListener);
            }
        };
    }

    @Override
    public void setScriptThis(Scriptable aScriptScope) throws Exception {
        Scriptable oldValue = scriptThis;
        super.setScriptThis(aScriptScope);
        if (scriptThis != null && scriptThis instanceof ScriptableObject) {
            ((ScriptableObject) scriptThis).defineProperty(Entity.MODEL_PROPERTY, new ApplicationModelHostObject(aScriptScope, this), ScriptableObject.READONLY);
            for (E ent : entities.values()) {
                if (ent != null) {
                    ent.defineProperties();
                }
            }
            if (parametersEntity != null) {
                parametersEntity.defineProperties();
            }
            //
            for (ReferenceRelation<E> aRelation : referenceRelations) {
                String scalarPropertyName = aRelation.getScalarPropertyName();
                if (scalarPropertyName == null || scalarPropertyName.isEmpty()) {
                    scalarPropertyName = aRelation.getRightEntity().getName();
                }
                if (scalarPropertyName != null && !scalarPropertyName.isEmpty()) {
                    aRelation.getLeftEntity().putOrmDefinition(
                            scalarPropertyName,
                            ScriptUtils.scalarPropertyDefinition(
                            aRelation.getRightEntity().getRowsetWrap(),
                            aRelation.getRightField().getName(),
                            aRelation.getLeftField().getName()));
                }
                String collectionPropertyName = aRelation.getCollectionPropertyName();
                if (collectionPropertyName == null || collectionPropertyName.isEmpty()) {
                    collectionPropertyName = aRelation.getLeftEntity().getName();
                }
                if (collectionPropertyName != null && !collectionPropertyName.isEmpty()) {
                    aRelation.getRightEntity().putOrmDefinition(
                            collectionPropertyName,
                            ScriptUtils.collectionPropertyDefinition(
                            aRelation.getLeftEntity().getRowsetWrap(),
                            aRelation.getRightField().getName(),
                            aRelation.getLeftField().getName()));
                }
            }
            //////////////////
        }
        changeSupport.firePropertyChange("scriptScope", oldValue, scriptThis);
    }
    
    public Function getHandler(String aHandlerName) {
        if (aHandlerName != null && !aHandlerName.isEmpty() && getScriptThis() != null) {
            Object oHandlers = getScriptThis().get(ScriptUtils.HANDLERS_PROP_NAME, getScriptThis());
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

    @Override
    public boolean validate() throws Exception {
        boolean res = super.validate();
        if (res) {
            for (Relation<E> rel : referenceRelations) {
                resolveRelation(rel, this);
            }
            checkReferenceRelationsIntegrity();
        }
        return res;
    }

    @Override
    protected void resolveRelation(Relation<E> aRelation, Model<E, P, C, Q> aModel) throws Exception {
        super.resolveRelation(aRelation, aModel);
        if (aRelation instanceof ReferenceRelation<?>) {
            if (aRelation.getLeftField() != null && !aRelation.getLeftField().isFk()) {
                aRelation.setLeftField(null);
            }
            if (aRelation.getRightField() != null && !aRelation.getRightField().isPk()) {
                aRelation.setRightField(null);
            }
            if (aRelation.getLeftField() != null
                    && aRelation.getLeftField().isFk()
                    && aRelation.getRightField() != null
                    && aRelation.getRightField().isPk()) {
                String leftTableName = aRelation.getLeftField().getFk().getReferee().getTable();
                String leftFieldName = aRelation.getLeftField().getFk().getReferee().getField();
                String rightTableName = aRelation.getRightField().getTableName();
                String rightFieldName = aRelation.getRightField().getOriginalName();
                boolean tablesSame = (leftTableName == null ? rightTableName == null : leftTableName.equalsIgnoreCase(rightTableName));
                boolean fieldsSame = (leftFieldName == null ? rightFieldName == null : leftFieldName.equalsIgnoreCase(rightFieldName));
                if (!tablesSame || !fieldsSame) {
                    aRelation.setLeftField(null);
                    aRelation.setRightField(null);
                }
            }
        }
    }

    public void resolveHandlers() {
        if (scriptThis != null) {
            for (E ent : entities.values()) {
                if (ent != null) {
                    ent.resolveHandlers();
                }
            }
            if (parametersEntity != null) {
                parametersEntity.resolveHandlers();
            }
            for (ScriptEvent event : scriptEventsQueue) {
                event.resolveHandler();
            }
        }
    }

    @Override
    public Model<E, P, C, Q> copy() throws Exception {
        Model<E, P, C, Q> copied = super.copy();
        for (ReferenceRelation<E> relation : referenceRelations) {
            ReferenceRelation<E> rcopied = (ReferenceRelation<E>) relation.copy();
            resolveRelation(rcopied, copied);
            ((ApplicationModel<E, P, C, Q>) copied).getReferenceRelations().add(rcopied);
        }
        ((ApplicationModel<E, P, C, Q>) copied).checkReferenceRelationsIntegrity();
        return copied;
    }

    @Override
    public void checkRelationsIntegrity() {
        super.checkRelationsIntegrity();
        checkReferenceRelationsIntegrity();
    }

    protected void checkReferenceRelationsIntegrity() {
        List<ReferenceRelation<E>> toDel = new ArrayList<>();
        for (ReferenceRelation<E> rel : referenceRelations) {
            if (rel.getLeftEntity() == null || (rel.getLeftField() == null && rel.getLeftParameter() == null)
                    || rel.getRightEntity() == null || (rel.getRightField() == null && rel.getRightParameter() == null)) {
                toDel.add(rel);
            }
        }
        for (ReferenceRelation<E> rel : toDel) {
            removeReferenceRelation(rel);
        }
    }

    @Override
    public void accept(ModelVisitor<E> visitor) {
        if (visitor instanceof ApplicationModelVisitor<?>) {
            ((ApplicationModelVisitor<E>) visitor).visit(this);
        }
    }

    @Override
    public Document toXML() {
        return ApplicationModel2XmlDom.transform(this);
    }

    /**
     * Pumps all enqueued script events. At this moment, this method does
     * nothing, because enqueueScriptEvent is not wherever called. Thus,
     * postponed events are disabled because of browser client compatibility.
     */
    public void pumpScriptEvents() {
        if (!pumpingScriptEvents && runtime) {
            pumpingScriptEvents = true;
            try {
                // while executing events, the scriptEventsQueue may be filled again!
                while (!scriptEventsQueue.isEmpty()) {
                    // create new vector to preserve concurrent modification
                    // filling the scriptEventsQueue while executing events is legal!
                    ArrayList<ScriptEvent<E>> lQueue = new ArrayList<>();
                    lQueue.addAll(scriptEventsQueue);
                    // while executing events, the scriptEventsQueue may be filled again!
                    scriptEventsQueue.clear();
                    // invoke events in occurance order
                    for (int i = 0; i < lQueue.size(); i++) {
                        ScriptEvent<E> entry = lQueue.get(i);
                        assert entry != null;
                        try {
                            entry.invoke();
                        } catch (Exception ex) {
                            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                assert scriptEventsQueue.isEmpty();
            } finally {
                pumpingScriptEvents = false;
            }
        }
    }

    public void enqueueScriptEvent(E aEntity, Function aHandler, ScriptSourcedEvent aEvent) {
        if (aHandler != null) {
            ScriptEvent<E> entry = new ScriptEvent<>(aEntity, aHandler, aEvent);
            scriptEventsSupport.fireScriptEventEnqueueing(aEntity, aHandler, aEvent);
            scriptEventsQueue.add(entry);
        }
    }

    public void addScriptEventsListener(ModelScriptEventsListener<E> l) {
        scriptEventsSupport.addListener(l);
    }

    public void removeScriptEventsListener(ModelScriptEventsListener<E> l) {
        scriptEventsSupport.removeListener(l);
    }

    public void fireScriptEventEnqueueing(E aEntity, Function aHandler, ScriptSourcedEvent aEvent) {
        scriptEventsSupport.fireScriptEventEnqueueing(aEntity, aHandler, aEvent);
    }

    public void fireScriptEventExecuting(E aEntity, Scriptable aScope, Function aHandler, ScriptSourcedEvent aEvent) {
        scriptEventsSupport.fireScriptEventExecuting(aEntity, aScope, aHandler, aEvent);
    }

    public void beginSavingCurrentRowIndexes() {
        boolean res = isAjusting();
        assert res;
        if (ajustingCounter == 1) {
            savedRowIndexEntities.clear();
            savedEntitiesRowIndexes.clear();
        }
    }

    public boolean isModified() throws Exception {
        if (entities != null) {
            for (E ent : entities.values()) {
                if (ent != null && ent.getRowset() != null) {
                    if (ent.getRowset().isModified()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Stub for compliance with asynchronous model within browser client.
     *
     * @return Allways false. Because of it is a stub.
     */
    public boolean isPending() {
        return false;
    }

    public final boolean save() throws Exception {
        return save(null);
    }
    private static final String SAVE_JSDOC = ""
            + "/**\n"
            + "* Saves model data changes.\n"
            + "* If model can't apply the changed data, than exception is thrown. In this case, application can call model.save() another time to save the changes.\n"
            + "* If an application needs to abort futher attempts and discard model data changes, use <code>model.revert()</code>.\n"
            + "* @param callback the function to be envoked after the data changes saved (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = SAVE_JSDOC, params = {"callback"})
    public boolean save(final Function aCallback) throws Exception {
        if (commitable) {
            try {
                commit();
                saved();
                if (aCallback != null) {
                    ScriptUtils.inContext(new ScriptAction() {
                        @Override
                        public Object run(Context cx) throws Exception {
                            aCallback.call(cx, scriptThis, scriptThis, new Object[]{});
                            return null;
                        }
                    });
                }
            } catch (Exception ex) {
                rolledback();
                throw ex;
            }
        }
        return true;
    }
    private static final String COMMIT_JSDOC = ""
            + "/**\n"
            + "* Commits model data changes.\n"
            + "*/";

    @ScriptFunction(jsDoc = COMMIT_JSDOC)
    public abstract int commit() throws Exception;
    private static final String REVERT_JSDOC = ""
            + "/**\n"
            + "* Reverts model data changes.\n"
            + "* After this method call, no data changes are avaliable for <code>model.save()</code> method, but the model still attempts to commit.\n"
            + "* Call <code>model.save()</code> on commitable and unchanged model nevertheless leads to a commit.\n"
            + "*/";

    @ScriptFunction(jsDoc = REVERT_JSDOC)
    public abstract void revert() throws Exception;
    private static final String SAVED_JSDOC = ""
            + "/**\n"
            + "* Notifies the model what it is saved.\n"
            + "*/";

    @ScriptFunction(jsDoc = SAVED_JSDOC)
    public abstract void saved() throws Exception;

    public abstract void rolledback() throws Exception;

    protected void fireCommited() throws Exception {
        for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
            l.commited();
        }
    }

    protected void fireReverted() throws Exception {
        for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
            l.rolledback();
        }
    }

    public final void requery() throws Exception {
        requery(null, null);
    }
    private static final String REQUERY_JSDOC = ""
            + "/**\n"
            + "* Requeries the model data. Forses the model data refresh, no matter if its parameters has changed or not.\n"
            + "* @param onSuccessCallback the handler function for refresh data on success event (optional).\n"
            + "* @param onFailureCallback the handler function for refresh data on failure event (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = REQUERY_JSDOC, params = {"onSuccessCallback", "onFailureCallback"})
    public void requery(final Function aOnSuccess, final Function aOnFailure) throws Exception {
        try {
            executeRootEntities(true);
            if (aOnSuccess != null) {
                ScriptUtils.inContext(new ScriptAction() {
                    @Override
                    public Object run(Context cx) throws Exception {
                        aOnSuccess.call(cx, scriptThis, scriptThis, new Object[]{});
                        return null;
                    }
                });
            }
        } catch (final Exception ex) {
            if (aOnFailure != null) {
                ScriptUtils.inContext(new ScriptAction() {
                    @Override
                    public Object run(Context cx) throws Exception {
                        aOnFailure.call(cx, scriptThis, scriptThis, new Object[]{ex.getMessage()});
                        return null;
                    }
                });
            } else {
                throw ex;
            }
        }
    }

    public void execute() throws Exception {
        execute(null, null);
    }

    public void execute(Function aOnSuccess) throws Exception {
        execute(aOnSuccess, null);
    }
    private static final String EXECUTE_JSDOC = ""
            + "/**\n"
            + "* Refreshes the model, only if any of its parameters has changed.\n"
            + "* @param onSuccessCallback the handler function for refresh data on success event (optional).\n"
            + "* @param onFailureCallback the handler function for refresh data on failure event (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = EXECUTE_JSDOC, params = {"onSuccessCallback", "onFailureCallback"})
    public void execute(final Function aOnSuccess, final Function aOnFailure) throws Exception {
        try {
            executeRootEntities(false);
            if (aOnSuccess != null) {
                ScriptUtils.inContext(new ScriptAction() {
                    @Override
                    public Object run(Context cx) throws Exception {
                        aOnSuccess.call(cx, scriptThis, scriptThis, new Object[]{});
                        return null;
                    }
                });
            }
        } catch (final Exception ex) {
            if (aOnFailure != null) {
                ScriptUtils.inContext(new ScriptAction() {
                    @Override
                    public Object run(Context cx) throws Exception {
                        aOnFailure.call(cx, scriptThis, scriptThis, new Object[]{ex.getMessage()});
                        return null;
                    }
                });
            } else {
                throw ex;
            }
        }
    }

    private void executeRootEntities(boolean refresh) throws Exception {
        Set<E> toExecute = new HashSet<>();
        for (E entity : entities.values()) {
            Set<Relation<E>> dependanceRels = new HashSet<>();
            for (Relation<E> inRel : entity.getInRelations()) {
                if (!(inRel.getLeftEntity() instanceof ApplicationParametersEntity)) {
                    dependanceRels.add(inRel);
                }
            }
            if (dependanceRels.isEmpty()) {
                toExecute.add(entity);
            }
        }
        for (E entity : toExecute) {
            entity.internalExecute(refresh);
        }
        Set<E> childrenToExecute = toExecute;
        while (!childrenToExecute.isEmpty()) {
            childrenToExecute = ApplicationEntity.internalExecuteChildrenImpl(refresh, childrenToExecute);
        }
        pumpScriptEvents();
    }

    public boolean isEntityRowIndexStateSaved(E entity) {
        return savedRowIndexEntities.contains(entity.getEntityId());
    }

    public void addSavedRowIndex(E aEntity, int aIndex) {
        boolean res = isAjusting();
        assert res;
        assert (aEntity != null);
        if (!isEntityRowIndexStateSaved(aEntity)) {
            Entry<E, Integer> entry = new SimpleEntry<>(aEntity, aIndex);
            savedEntitiesRowIndexes.add(entry);
            savedRowIndexEntities.add(aEntity.getEntityId());
        }
    }

    public void restoreRowIndexes() {
        boolean res = isAjusting();
        assert res;
        if (ajustingCounter == 1 && savedEntitiesRowIndexes != null && savedRowIndexEntities != null) {
            for (Entry<E, Integer> entr : savedEntitiesRowIndexes) {
                if (entr != null) {
                    try {
                        E ent = entr.getKey();
                        if (ent != null && ent.getRowset() != null) {
                            ent.getRowset().absolute(entr.getValue());
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            savedRowIndexEntities.clear();
            savedEntitiesRowIndexes.clear();
        }
    }

    @Override
    public void setRuntime(boolean aValue) throws Exception {
        if (runtime != aValue) {
            boolean oldValue = runtime;
            runtime = aValue;
            if (!oldValue && runtime) {
                executeRootEntities(true);
            }
            PropertyChangeEvent evt = new PropertyChangeEvent(this, "runtime", oldValue, runtime);
            for (PropertyChangeListener l : changeSupport.getPropertyChangeListeners()) {
                try {
                    l.propertyChange(evt);
                } catch (Exception ex) {
                    Logger.getLogger(ApplicationModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    protected static final String USER_DATASOURCE_NAME = "userQuery";

    public synchronized Scriptable createQuery(String aQueryId) throws Exception {
        Logger.getLogger(ApplicationModel.class.getName()).log(Level.WARNING, "createQuery deprecated call detected. Use loadEntity() instead.");
        return loadEntity(aQueryId);
    }
    private static final String LOAD_ENTITY_JSDOC = ""
            + "/**\n"
            + "* Creates new entity of model, based on application query.\n"
            + "* @param queryId the query application element ID.\n"
            + "* @return a new entity.\n"
            + "*/";

    @ScriptFunction(jsDoc = LOAD_ENTITY_JSDOC, params = {"queryId"})
    public synchronized Scriptable loadEntity(String aQueryId) throws Exception {
        if (client == null) {
            throw new NullPointerException("Null client detected while creating an entity");
        }
        E entity = newGenericEntity();
        entity.setName(USER_DATASOURCE_NAME);
        entity.setQueryId(aQueryId);
        entity.validateQuery();
        //addEntity(entity); To avoid memory leaks you should not add the entity in the model!
        return entity.defineProperties();
    }

    public synchronized Scriptable createQuery(ScriptableRowset<E> aLeftScriptableRowset, Field aLeftField, String aRightQueryId, Field aRightField) throws Exception {
        if (client == null) {
            throw new NullPointerException("Null client detected while creating a query");
        }
        E rightEntity = newGenericEntity();
        rightEntity.setName(USER_DATASOURCE_NAME);
        rightEntity.setQueryId(aRightQueryId);
        addEntity(rightEntity);
        // filter relation
        Relation<E> rel = new Relation<>(aLeftScriptableRowset.getEntity(), aLeftField, rightEntity, aRightField);
        addRelation(rel);
        // parameters bypass relations
        Parameters params = aLeftScriptableRowset.getEntity().getQuery().getParameters();
        assert params != null;
        for (int i = 1; i <= params.getParametersCount(); i++) {
            Parameter p = (Parameter) params.get(i);
            Relation<E> pRel = new Relation<>(aLeftScriptableRowset.getEntity(), p, rightEntity, rightEntity.getQuery().getParameters().get(p.getName()));
            addRelation(pRel);
        }
        return rightEntity.defineProperties();
    }

    public synchronized void deleteQuery(Object oQuery) {
        if (oQuery != null && oQuery instanceof ScriptableRowset<?>) {
            ScriptableRowset<E> sRowset = (ScriptableRowset<E>) oQuery;
            E entity2Delete = sRowset.getEntity();
            Set<Relation<E>> rels = entity2Delete.getInOutRelations();
            if (rels != null) {
                for (Relation<E> rel : rels) {
                    removeRelation(rel);
                }
            }
            removeEntity((E) entity2Delete);
        }
    }

    public void addReferenceRelation(ReferenceRelation<E> aRelation) {
        referenceRelations.add(aRelation);
        fireRelationAdded(aRelation);
    }

    public void removeReferenceRelation(ReferenceRelation<E> aRelation) {
        referenceRelations.remove(aRelation);
        fireRelationRemoved(aRelation);
    }

    public Set<ReferenceRelation<E>> getReferenceRelations() {
        return referenceRelations;
    }

    public Set<ReferenceRelation<E>> getReferenceRelationsByEntity(E aEntity) {
        Set<ReferenceRelation<E>> res = new HashSet<>();
        for (ReferenceRelation<E> rel : referenceRelations) {
            if (rel.getLeftEntity() == aEntity || rel.getRightEntity() == aEntity) {
                res.add(rel);
            }
        }
        return res;
    }

    public CompactBlob loadBlobFromFile(File aFile) throws IOException {
        if (aFile != null && !aFile.isDirectory()) {
            return _loadBlobFromFile(aFile.toString());
        }
        return null;
    }

    public CompactBlob loadBlobFromFile(String aFilePath) throws IOException {
        return _loadBlobFromFile(aFilePath);
    }

    public static CompactBlob _loadBlobFromFile(String aFilePath) throws IOException {
        if (aFilePath != null && !aFilePath.isEmpty()) {
            File f = new File(aFilePath);
            if (f.canRead() && f.isFile()) {
                try (FileInputStream fs = new FileInputStream(f)) {
                    byte[] data = new byte[(int) f.length()];
                    fs.read(data, 0, data.length);
                    return new CompactBlob(data);
                }
            }
        }
        return null;
    }

    public CompactClob loadClobFromFile(File aFile, String charsetName) throws IOException {
        if (aFile != null && !aFile.isDirectory()) {
            return _loadClobFromFile(aFile.toString(), charsetName);
        }
        return null;
    }

    public CompactClob loadClobFromFile(String aFilePath, String charsetName) throws IOException {
        return _loadClobFromFile(aFilePath, charsetName);
    }

    public static CompactClob _loadClobFromFile(String aFilePath, String charsetName) throws IOException {
        if (aFilePath != null && !aFilePath.isEmpty()) {
            File f = new File(aFilePath);
            if (f.canRead() && f.isFile()) {
                try (InputStream fs = new FileInputStream(f)) {
                    byte[] data = new byte[(int) f.length()];
                    fs.read(data, 0, data.length);
                    return new CompactClob(new String(data, charsetName));
                }
            }
        }
        return null;
    }

    public void saveBlobToFile(File aFile, Object oLob) throws IOException {
        if (aFile != null && !aFile.isDirectory()) {
            _saveBlobToFile(aFile.toString(), oLob);
        }
    }

    public void saveBlobToFile(String aFilePath, Object oLob) throws IOException {
        _saveBlobToFile(aFilePath, oLob);
    }

    public static void _saveBlobToFile(String aFilePath, Object oLob) throws IOException {
        if (aFilePath != null && !aFilePath.isEmpty() && oLob instanceof CompactBlob) {
            CompactBlob b = (CompactBlob) oLob;
            File f = new File(aFilePath);
            if (!f.isDirectory()) {
                if (!f.exists()) {
                    f.createNewFile();
                } else {
                    f.delete();
                    f.createNewFile();
                }
                if (f.canWrite()) {
                    try (FileOutputStream fs = new FileOutputStream(f)) {
                        byte[] data = b.getData();
                        fs.write(data, 0, data.length);
                    }
                }
            }
        } else {
            throw new IOException("not a blob value");
        }
    }

    public void saveClobToFile(File aFile, Object oLob, String charsetName) throws IOException {
        if (aFile != null && !aFile.isDirectory()) {
            _saveClobToFile(aFile.toString(), oLob, charsetName);
        }
    }

    public void saveClobToFile(String aFilePath, Object oLob, String charsetName) throws IOException {
        _saveClobToFile(aFilePath, oLob, charsetName);
    }

    public static void _saveClobToFile(String aFilePath, Object oLob, String charsetName) throws IOException {
        if (aFilePath != null && !aFilePath.isEmpty() && oLob instanceof CompactClob) {
            CompactClob c = (CompactClob) oLob;
            File f = new File(aFilePath);
            if (!f.isDirectory()) {
                if (!f.exists()) {
                    f.createNewFile();
                } else {
                    f.delete();
                    f.createNewFile();
                }

                if (f.canWrite()) {
                    try (OutputStream fs = new FileOutputStream(f)) {
                        byte[] data;
                        String sData = c.getData();
                        if (sData != null && !sData.isEmpty()) {
                            data = sData.getBytes(charsetName);
                        } else {
                            data = new byte[0];
                        }
                        fs.write(data, 0, data.length);
                    }
                }
            }
        } else {
            throw new IOException("not a clob value");
        }
    }
}
