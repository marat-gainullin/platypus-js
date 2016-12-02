/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.Model;
import com.eas.client.model.Relation;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.Query;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 * @param <E>
 * @param <Q>
 */
public abstract class ApplicationModel<E extends ApplicationEntity<?, Q, E>, Q extends Query> extends Model<E, Q> implements HasPublished {

    protected JSObject published;
    protected Set<ReferenceRelation<E>> referenceRelations = new HashSet<>();
    protected QueriesProxy<Q> queries;
    protected RequeryProcess<E, Q> process;

    public static class RequeryProcess<E extends ApplicationEntity<?, Q, E>, Q extends Query> {

        public Map<E, Exception> errors = new HashMap<>();
        public Consumer<Void> onSuccess;
        public Consumer<Exception> onFailure;

        public RequeryProcess(Consumer<Void> aOnSuccess, Consumer<Exception> aOnFailure) {
            super();
            onSuccess = aOnSuccess;
            assert onSuccess != null : "aOnSuccess argument is required.";
            onFailure = aOnFailure;
        }

        protected String assembleErrors() {
            if (errors != null && !errors.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                errors.entrySet().stream().forEach((Map.Entry<E, Exception> entry) -> {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(entry.getValue().getMessage()).append(" (").append(entry.getKey().getName()).append("[ ").append(entry.getKey().getTitle()).append("])");
                });
                return sb.toString();
            }
            return null;
        }

        public void cancel() throws Exception {
            if (onFailure != null) {
                onFailure.accept(new InterruptedException("Canceled"));
            }
        }

        public void success() {
            onSuccess.accept(null);
        }

        public void failure() {
            if (onFailure != null) {
                onFailure.accept(new Exception(assembleErrors()));
            }
        }

        public void end() {
            if (errors.isEmpty()) {
                success();
            } else {
                failure();
            }
        }
    }

    public ApplicationModel() {
        super();
    }

    public ApplicationModel(QueriesProxy<Q> aQueries) {
        super();
        queries = aQueries;
    }

    public QueriesProxy<Q> getQueries() {
        return queries;
    }

    public void terminateProcess(E aSource, Exception aErrorMessage) {
        if (process != null) {
            if (aErrorMessage != null) {
                process.errors.put(aSource, aErrorMessage);
            }
            if (!isPending()) {
                RequeryProcess<E, Q> pr = process;
                process = null;
                pr.end();
            }
        }
    }

    public boolean isPending() {
        return entities.values().stream().anyMatch((entity) -> (entity.isPending()));
    }

    public void executeEntities(boolean refresh, Set<E> toExecute) throws Exception {
        if (refresh) {
            toExecute.stream().forEach((entity) -> {
                entity.invalidate();
            });
        }
        for (E entity : toExecute) {
            entity.internalExecute(null, null);
        }
    }

    private Set<E> rootEntities() {
        final Set<E> rootEntities = new HashSet<>();
        entities.values().stream().forEach((E entity) -> {
            if (entity.getInRelations().isEmpty()) {
                rootEntities.add(entity);
            }
        });
        return rootEntities;
    }

    /**
     * Validates queries in force way. Such case is used in designer ONLY!
     *
     * @return
     * @throws Exception
     */
    @Override
    protected boolean validateEntities() throws Exception {
        for (E e : entities.values()) {
            queries.getQuery(e.getQueryName(), Scripts.getSpace(), null, null);
        }
        return super.validateEntities();
    }

    public void validateQueries() throws Exception {
        for (E entity : entities.values()) {
            entity.validateQuery();
        }
    }

    public Collection<E> entities() {
        return entities.values();
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
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    public void createORMDefinitions() {
        referenceRelations.stream().forEach((ReferenceRelation<E> aRelation) -> {
            String scalarPropertyName = aRelation.getScalarPropertyName();
            String collectionPropertyName = aRelation.getCollectionPropertyName();
            if (scalarPropertyName != null && !scalarPropertyName.isEmpty()) {
                aRelation.getLeftEntity().putOrmScalarDefinition(scalarPropertyName,
                        new Fields.OrmDef(aRelation.getLeftField().getName(), scalarPropertyName, collectionPropertyName, Scripts.getSpace().scalarPropertyDefinition(
                                        (JSObject) aRelation.getRightEntity().getPublished(),
                                        aRelation.getRightField().getName(),
                                        aRelation.getLeftField().getName())));
            }
            if (collectionPropertyName != null && !collectionPropertyName.isEmpty()) {
                aRelation.getRightEntity().putOrmCollectionDefinition(collectionPropertyName,
                        new Fields.OrmDef(collectionPropertyName, scalarPropertyName, Scripts.getSpace().collectionPropertyDefinition(
                                        (JSObject) aRelation.getLeftEntity().getPublished(),
                                        aRelation.getRightField().getName(),
                                        aRelation.getLeftField().getName())));
            }
        });
    }

    @Override
    public Model<E, Q> copy() throws Exception {
        Model<E, Q> copied = super.copy();
        for (ReferenceRelation<E> relation : referenceRelations) {
            ReferenceRelation<E> rcopied = (ReferenceRelation<E>) relation.copy();
            copied.resolveRelation(rcopied);
            ((ApplicationModel<E, Q>) copied).getReferenceRelations().add(rcopied);
        }
        ((ApplicationModel<E, Q>) copied).checkReferenceRelationsIntegrity();
        return copied;
    }

    @Override
    protected void validateRelations() throws Exception {
        super.validateRelations();
        for (Relation<E> rel : referenceRelations) {
            resolveRelation(rel);
        }
    }

    @Override
    public void checkRelationsIntegrity() {
        super.checkRelationsIntegrity();
        checkReferenceRelationsIntegrity();
    }

    protected void checkReferenceRelationsIntegrity() {
        List<ReferenceRelation<E>> toDel = new ArrayList<>();
        referenceRelations.stream().forEach((ReferenceRelation<E> rel) -> {
            if (rel.getLeftEntity() == null || (rel.getLeftField() == null && rel.getLeftParameter() == null)
                    || rel.getRightEntity() == null || (rel.getRightField() == null && rel.getRightParameter() == null)) {
                toDel.add(rel);
            }
        });
        toDel.stream().forEach((rel) -> {
            removeReferenceRelation(rel);
        });
    }

    public abstract boolean isModified() throws Exception;

    protected static final String SAVE_JSDOC = ""
            + "/**\n"
            + " * Saves model data changes.\n"
            + " * If model can't apply the changed data, than exception is thrown. In this case, application can call model.save() another time to save the changes.\n"
            + " * If an application needs to abort further attempts and discard model data changes, use <code>model.revert()</code>.\n"
            + " * Note, that a <code>model.save()</code> call on unchanged model nevertheless leads to a commit.\n"
            + " * @param onSuccess The function to be invoked after the data changes saved (optional).\n"
            + " * @param onFailure The function to be invoked when exception raised while commit process (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = SAVE_JSDOC, params = {"onSuccess", "onFailure"})
    public int save(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        if (aOnSuccess != null) {
            commit((Integer aResult) -> {
                commited();
                aOnSuccess.call(null, new Object[]{aResult});
            }, (Exception ex) -> {
                rolledback(ex);
                if (aOnFailure != null) {
                    aOnFailure.call(null, new Object[]{ex.getMessage()});
                }
            });
            return 0;
        } else {
            try {
                int result = commit(null, null);
                commited();
                return result;
            } catch (Exception ex) {
                rolledback(ex);
                throw ex;
            }
        }
    }

    protected void rolledback(Exception ex) {
        Logger.getLogger(ApplicationModel.class.getName()).log(Level.SEVERE, ex.toString());
    }

    public void save(JSObject aOnSuccess) throws Exception {
        save(aOnSuccess, null);
    }

    public void save() throws Exception {
        save(null, null);
    }

    protected static final String REVERT_JSDOC = ""
            + "/**\n"
            + " * Reverts model data changes.\n"
            + " * After this method call, no data changes are avaliable for <code>model.save()</code> method.\n"
            + " */";

    @ScriptFunction(jsDoc = REVERT_JSDOC)
    public void revert() {
        entities.values().stream().forEach((E aEntity) -> {
            aEntity.applyLastSnapshot();
        });
    }

    public abstract int commit(Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception;

    public void commited() {
        entities.values().stream().forEach((E aEntity) -> {
            aEntity.takeSnapshot();
        });
    }

    public final void requery() throws Exception {
        requery(null, null);
    }

    public void requery(JSObject onSuccess) throws Exception {
        requery(onSuccess, null);
    }

    protected static final String REQUERY_JSDOC = ""
            + "/**\n"
            + " * Requeries the model data. Forces the model data refresh, no matter if its parameters has changed or not.\n"
            + " * @param onSuccess The handler function for refresh data on success event (optional).\n"
            + " * @param onFailure The handler function for refresh data on failure event (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = REQUERY_JSDOC, params = {"onSuccess", "onFailure"})
    public void requery(JSObject onSuccess, JSObject onFailure) throws Exception {
        inProcess(() -> {
            revert();
            executeEntities(true, rootEntities());
            return null;
        }, onSuccess != null ? (Void v) -> {
            onSuccess.call(null, new Object[]{});
        } : null, onFailure != null ? (Exception ex) -> {
            onFailure.call(null, new Object[]{ex.getMessage()});
        } : null);
    }

    public void execute() throws Exception {
        execute(null, null);
    }

    public void execute(Consumer<Void> onSuccess) throws Exception {
        execute(onSuccess, null);
    }
    private static final String EXECUTE_JSDOC = ""
            + "/**\n"
            + " * Refreshes the model, only if any of its parameters has changed.\n"
            + " * @param onSuccess The handler function for refresh data on success event (optional).\n"
            + " * @param onFailure The handler function for refresh data on failure event (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = EXECUTE_JSDOC, params = {"onSuccess", "onFailure"})
    public void execute(Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        inProcess(() -> {
            executeEntities(false, rootEntities());
            return null;
        }, onSuccess, onFailure);
    }

    public void inProcess(Callable<Void> aAction, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (process != null) {
            process.cancel();
            process = null;
        }
        if (onSuccess != null) {
            process = new RequeryProcess<>(onSuccess, onFailure);
        }
        aAction.call();
        if (!isPending() && process != null) {
            process.end();
            process = null;
        }
    }

    protected static final String USER_DATASOURCE_NAME = "userQuery";

    public E createQuery(String aQueryName) throws Exception {
        Logger.getLogger(ApplicationModel.class.getName()).log(Level.WARNING, "createQuery deprecated call detected. Use loadEntity() instead.");
        return loadEntity(aQueryName);
    }
    private static final String LOAD_ENTITY_JSDOC = ""
            + "/**\n"
            + " * Creates new entity of model, based on application query.\n"
            + " * @param queryName the query application element name.\n"
            + " * @return a new entity.\n"
            + " */";

    @ScriptFunction(jsDoc = LOAD_ENTITY_JSDOC, params = {"queryName"})
    public E loadEntity(String aQueryName) throws Exception {
        E entity = newGenericEntity();
        entity.setName(USER_DATASOURCE_NAME);
        entity.setQueryName(aQueryName);
        entity.validateQuery();
        return entity;
    }

    public E createQuery(E aLeftEntity, Field aLeftField, String aRightQueryId, Field aRightField) throws Exception {
        E rightEntity = newGenericEntity();
        rightEntity.setName(USER_DATASOURCE_NAME);
        rightEntity.setQueryName(aRightQueryId);
        addEntity(rightEntity);
        // filter relation
        Relation<E> rel = new Relation<>(aLeftEntity, aLeftField, rightEntity, aRightField);
        addRelation(rel);
        // parameters bypass relations
        Parameters params = aLeftEntity.getQuery().getParameters();
        assert params != null;
        for (int i = 1; i <= params.getParametersCount(); i++) {
            Parameter p = (Parameter) params.get(i);
            Relation<E> pRel = new Relation<>(aLeftEntity, p, rightEntity, rightEntity.getQuery().getParameters().get(p.getName()));
            addRelation(pRel);
        }
        return rightEntity;
    }

    public void deleteQuery(E entity2Delete) {
        if (entity2Delete != null) {
            Set<Relation<E>> rels = entity2Delete.getInOutRelations();
            if (rels != null) {
                rels.stream().forEach((rel) -> {
                    removeRelation(rel);
                });
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
        referenceRelations.stream().forEach((ReferenceRelation<E> rel) -> {
            if (rel.getLeftEntity() == aEntity || rel.getRightEntity() == aEntity) {
                res.add(rel);
            }
        });
        return res;
    }
}
