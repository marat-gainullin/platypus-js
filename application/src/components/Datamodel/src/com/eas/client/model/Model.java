/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.Client;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.Query;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import java.beans.PropertyChangeSupport;
import java.util.*;
import org.w3c.dom.Document;

/**
 * @param <E> Entity generic type.
 * @param <P> Parameters entity generic type.
 * @param <C> Client generic type.
 * @param <Q> Query generic type.
 * @author mg
 */
public abstract class Model<E extends Entity<?, Q, E>, P extends E, C extends Client, Q extends Query<C>> {

    public static final long PARAMETERS_ENTITY_ID = -1L;
    public static final String PARAMETERS_SCRIPT_NAME = "params";
    public static final String DATASOURCE_METADATA_SCRIPT_NAME = "schema";
    public static final String DATASOURCE_NAME_TAG_NAME = "Name";
    public static final String DATASOURCE_TITLE_TAG_NAME = "Title";
    protected StoredQueryFactory queryFactory;
    protected C client;
    protected Set<Relation<E>> relations = new HashSet<>();
    protected Map<Long, E> entities = new HashMap<>();
    protected P parametersEntity;
    protected Parameters parameters = new Parameters();
    protected int ajustingCounter;
    protected Runnable resolver;
    protected GuiCallback guiCallback;
    protected ModelEditingSupport<E> editingSupport = new ModelEditingSupport<>();
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected boolean relationsAgressiveCheck;

    public Model<E, P, C, Q> copy() throws Exception {
        Model<E, P, C, Q> copied = getClass().newInstance();
        copied.setClient(client);
        for (E entity : entities.values()) {
            copied.addEntity((E) entity.copy());
        }
        if (parameters != null) {
            copied.setParameters(parameters.copy());
        }
        if (getParametersEntity() != null) {
            copied.setParametersEntity((P) getParametersEntity().copy());
        }
        for (Relation<E> relation : relations) {
            Relation<E> rcopied = relation.copy();
            resolveRelation(rcopied, copied);
            copied.addRelation(rcopied);
        }
        return copied;
    }

    /**
     * Base model constructor.
     */
    protected Model() {
        super();
    }

    /**
     * Constructor of datamodel. Used in designers.
     *
     * @param aClient C instance all queries to be sent to.
     * @see C
     */
    public Model(C aClient) {
        this();
        setClient(aClient);
    }

    public void setResolver(Runnable aResolver) {
        resolver = aResolver;
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public void setClient(C aValue) {
        client = aValue;
        if (client != null && resolver != null) {
            resolver.run();
            resolver = null;
        }
        if (client == null) {
            for (E e : entities.values()) {
                e.clearFields();
            }
        }
    }

    public void clearRelations() {
        if (relations != null) {
            relations.clear();
            for (E e : entities.values()) {
                e.getInRelations().clear();
                e.getOutRelations().clear();
            }
        }
    }

    /**
     * Tests if entities' internal data is actual, updating it if necessary.
     *
     * @return True if any change occur and false is all is actual.
     */
    public synchronized boolean validate() throws Exception {
        if (validateEntities()) {
            validateRelations();
            checkRelationsIntegrity();
            return true;
        } else {
            return false;
        }
    }

    protected void validateRelations() throws Exception {
        for (Relation<E> rel : relations) {
            resolveRelation(rel, this);
        }
    }

    protected boolean validateEntities() throws Exception {
        boolean res = false;
        for (E e : entities.values()) {
            if (e.validate()) {
                res = true;
            }
        }
        return res;
    }

    public void fireAllQueriesChanged() throws Exception {
        for (E e : getEntities().values()) {
            boolean queryExists;
            try {
                queryExists = e.getQuery() != null;
            } catch (Exception ex) {
                queryExists = false;
            }
            e.getChangeSupport().firePropertyChange(Entity.QUERY_VALID_PROPERTY, !queryExists, queryExists);
        }
    }

    public GuiCallback getGuiCallback() {
        return guiCallback;
    }

    public void setGuiCallback(GuiCallback aValue) {
        guiCallback = aValue;
        editingSupport.setGuiCallback(aValue);
    }

    /**
     * Finds entity by table name. Takes into account only table name, ignoring
     * schema, even it's exist.
     *
     * @param aTableName
     * @return Entity found;
     */
    public E getEntityByTableName(String aTableName) {
        if (aTableName != null && !aTableName.isEmpty() && entities != null) {
            for (E ent : entities.values()) {
                String tableName = ent.getTableName();
                if (tableName != null && !tableName.isEmpty()) {
                    if (tableName.toLowerCase().equals(aTableName.toLowerCase())) {
                        return ent;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds entity by full table name. Takes into account schema name.
     *
     * @param aTableName
     * @return Entity found;
     */
    public E getEntityByFullTableName(String aTableName) {
        if (aTableName != null && !aTableName.isEmpty() && entities != null) {
            for (E ent : entities.values()) {
                String tableName = ent.getTableName();
                if (tableName != null && !tableName.isEmpty()) {
                    if (ent.getTableSchemaName() != null && !ent.getTableSchemaName().isEmpty()) {
                        tableName = ent.getTableSchemaName() + "." + tableName;
                    }
                    if (tableName.toLowerCase().equals(aTableName.toLowerCase())) {
                        return ent;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds entity by name.
     *
     * @param aName
     * @return Entity found;
     */
    public E getEntityByName(String aName) {
        for (E appEntity : getEntities().values()) {
            if (appEntity.getName() != null && appEntity.getName().equalsIgnoreCase(aName)) {
                return appEntity;
            }
        }
        if (parametersEntity != null && parametersEntity.getName() != null && parametersEntity.getName().equalsIgnoreCase(aName)) {
            return parametersEntity;
        }
        return null;
    }

    public abstract E newGenericEntity();

    public abstract void accept(ModelVisitor<E> visitor);

    public abstract Document toXML();

    /**
     * Registers an <code>DatamodelEditingValidator</code>. The validator is
     * notified whenever an datamodel edit action will occur.
     *
     * @param v an <code>DatamodelEditingValidator</code> object
     * @see #removeDatamodelEditingValidator
     */
    public synchronized void addEditingValidator(ModelEditingValidator<E> v) {
        editingSupport.addValidator(v);
    }

    /**
     * Removes an <code>DatamodelEditingValidator</code>.
     *
     * @param v the <code>DatamodelEditingValidator</code> object to be removed
     * @see #addDatamodelEditingValidator
     */
    public synchronized void removeEditingValidator(ModelEditingValidator<E> v) {
        editingSupport.removeValidator(v);
    }

    public boolean checkRelationAddingValid(Relation<E> aRelation) {
        return editingSupport.checkRelationAddingValid(aRelation);
    }

    public boolean checkRelationRemovingValid(Relation<E> aRelation) {
        return editingSupport.checkRelationRemovingValid(aRelation);
    }

    public boolean checkEntityAddingValid(E aEntity) {
        return editingSupport.checkEntityAddingValid(aEntity);
    }

    public boolean checkEntityRemovingValid(E aEntity) {
        return editingSupport.checkEntityRemovingValid(aEntity);
    }

    public void addEditingListener(ModelEditingListener<E> l) {
        editingSupport.addListener(l);
    }

    public void removeEditingListener(ModelEditingListener<E> l) {
        editingSupport.removeListener(l);
    }

    private void fireEntityAdded(E ent) {
        editingSupport.fireEntityAdded(ent);
    }

    private void fireEntityRemoved(E ent) {
        editingSupport.fireEntityRemoved(ent);
    }

    public void fireRelationAdded(Relation<E> aRel) {
        editingSupport.fireRelationAdded(aRel);
    }

    public void fireRelationRemoved(Relation<E> aRel) {
        editingSupport.fireRelationRemoved(aRel);
    }

    public P getParametersEntity() {
        return parametersEntity;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void addRelation(Relation<E> aRel) {
        relations.add(aRel);
        E lEntity = aRel.getLeftEntity();
        E rEntity = aRel.getRightEntity();
        if (lEntity != null && rEntity != null) {
            lEntity.addOutRelation(aRel);
            rEntity.addInRelation(aRel);
        }
        fireRelationAdded(aRel);
    }

    public Set<Relation<E>> collectRelationsByEntity(E aEntity) {
        return aEntity.getInOutRelations();
    }

    public int getAjustingCounter() {
        return ajustingCounter;
    }

    public boolean isAjusting() {
        return ajustingCounter > 0;
    }

    public void beginAjusting() {
        ajustingCounter++;
    }

    public void endAjusting() {
        ajustingCounter--;
        assert (ajustingCounter >= 0);
    }

    public C getClient() {
        return client;
    }

    public void removeRelation(Relation<E> aRelation) {
        relations.remove(aRelation);
        E lEntity = aRelation.getLeftEntity();
        E rEntity = aRelation.getRightEntity();
        if (lEntity != null && rEntity != null) {
            lEntity.removeOutRelation(aRelation);
            rEntity.removeInRelation(aRelation);
        }
        fireRelationRemoved(aRelation);
    }

    public void addEntity(E aEntity) {
        entities.put(aEntity.getEntityId(), aEntity);
        fireEntityAdded(aEntity);
    }

    public boolean removeEntity(E aEnt) {
        return aEnt != null && removeEntity(aEnt.getEntityId()) != null;
    }

    public E removeEntity(Long aEntId) {
        E lent = entities.get(aEntId);
        if (lent != null) {
            entities.remove(aEntId);
            fireEntityRemoved(lent);
        }
        return lent;
    }

    public Map<Long, E> getEntities() {
        return entities;
    }

    /**
     * Sane as getEntities, but returns also parameters entity if it exists.
     *
     * @return
     */
    public Map<Long, E> getAllEntities() {
        Map<Long, E> allEntities = new HashMap<>();
        allEntities.putAll(entities);
        if (parametersEntity != null) {
            allEntities.put(parametersEntity.getEntityId(), parametersEntity);
        }
        return allEntities;
    }

    public E getEntityById(Long aId) {
        if (aId != null && PARAMETERS_ENTITY_ID == aId) {
            return parametersEntity;
        } else {
            return entities.get(aId);
        }
    }

    public void setEntities(Map<Long, E> aValue) {
        entities = aValue;
    }

    public Set<Relation<E>> getRelations() {
        return relations;
    }

    public void setParameters(Parameters aParams) {
        parameters = aParams;
    }

    public void setParametersEntity(P aParamsEntity) {
        parametersEntity = aParamsEntity;
    }

    public void setRelations(Set<Relation<E>> aRelations) {
        relations = aRelations;
    }

    public boolean isFieldInRelations(E aEntity, Set<Relation<E>> aRelations, Field aField) {
        for (Relation<E> lrel : aRelations) {
            if (lrel.getLeftField() == aField || lrel.getLeftParameter() == aField
                    || lrel.getRightField() == aField || lrel.getRightParameter() == aField) {
                return true;
            }
        }
        return false;
    }

    public abstract boolean isTypeSupported(int type) throws Exception;

    public boolean isNamePresent(String aName, E toExclude, Field field2Exclude) {
        if (entities != null && aName != null) {
            for (E ent : entities.values()) {
                if (ent != null && ent != toExclude) {
                    String lName = ent.getName();
                    if (lName != null && aName.equals(lName)) {
                        return true;
                    }
                    Fields mayBeParams = ent.getFields();
                    if (mayBeParams != null && mayBeParams instanceof Parameters && mayBeParams.isNameAlreadyPresent(aName, field2Exclude)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isRelationsAgressiveCheck() {
        return relationsAgressiveCheck;
    }

    public void setRelationsAgressiveCheck(boolean aValue) {
        relationsAgressiveCheck = aValue;
    }

    protected void resolveRelation(Relation<E> aRelation, Model<E, P, C, Q> aModel) throws Exception {
        if (aRelation.getLeftEntity() != null) {
            aRelation.setLeftEntity(aModel.getEntityById(aRelation.getLeftEntity().getEntityId()));
        }
        if (aRelation.getRightEntity() != null) {
            aRelation.setRightEntity(aModel.getEntityById(aRelation.getRightEntity().getEntityId()));
        }
        if (aRelation.getLeftField() != null) {
            String targetName = aRelation.getLeftField().getName();
            if (aRelation.getLeftEntity() != null) {
                if (aRelation.isLeftParameter() && aRelation.getLeftEntity().getQueryId() != null) {
                    Q lQuery = aRelation.getLeftEntity().getQuery();
                    if (lQuery != null) {
                        aRelation.setLeftField(lQuery.getParameters().get(targetName));
                    } else if (relationsAgressiveCheck) {
                        aRelation.setLeftField(null);
                    } else {
                        aRelation.setLeftField(new Parameter(targetName));
                    }
                } else {
                    Fields lFields = aRelation.getLeftEntity().getFields();
                    if (lFields != null) {
                        aRelation.setLeftField(lFields.get(targetName));
                    } else if (relationsAgressiveCheck) {
                        aRelation.setLeftField(null);
                    } else {
                        aRelation.setLeftField(new Field(targetName));
                    }
                }
            } else if (relationsAgressiveCheck) {
                aRelation.setLeftField(null);
            } else {
                aRelation.setLeftField(new Field(targetName));
            }
        }
        if (aRelation.getRightField() != null) {
            String targetName = aRelation.getRightField().getName();
            if (aRelation.getRightEntity() != null) {
                if (aRelation.isRightParameter() && aRelation.getRightEntity().getQueryId() != null) {
                    Q rQuery = aRelation.getRightEntity().getQuery();
                    if (rQuery != null) {
                        aRelation.setRightField(rQuery.getParameters().get(targetName));
                    } else if (relationsAgressiveCheck) {
                        aRelation.setRightField(null);
                    } else {
                        aRelation.setRightField(new Parameter(targetName));
                    }
                } else {
                    Fields rFields = aRelation.getRightEntity().getFields();
                    if (rFields != null) {
                        aRelation.setRightField(rFields.get(aRelation.getRightField().getName()));
                    } else if (relationsAgressiveCheck) {
                        aRelation.setRightField(null);
                    } else {
                        aRelation.setRightField(new Field(targetName));
                    }
                }
            } else if (relationsAgressiveCheck) {
                aRelation.setRightField(null);
            } else {
                aRelation.setRightField(new Field(targetName));
            }
        }
    }

    public void checkRelationsIntegrity() {
        List<Relation<E>> toDel = new ArrayList<>();
        for (Relation<E> rel : relations) {
            if (rel.getLeftEntity() == null || (rel.getLeftField() == null && rel.getLeftParameter() == null)
                    || rel.getRightEntity() == null || (rel.getRightField() == null && rel.getRightParameter() == null)) {
                toDel.add(rel);
            }
        }
        toDel.stream().forEach((rel) -> {
            removeRelation(rel);
        });
    }
}
