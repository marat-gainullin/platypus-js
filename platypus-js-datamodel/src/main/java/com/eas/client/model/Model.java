package com.eas.client.model;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.Query;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * @param <E> Entity generic type.
 * @param <Q> Query generic type.
 * @author mg
 */
public abstract class Model<E extends Entity<?, Q, E>, Q extends Query> {

    public static final String DATASOURCE_METADATA_SCRIPT_NAME = "schema";
    protected Set<Relation<E>> relations = new HashSet<>();
    protected Map<Long, E> entities = new HashMap<>();
    protected ModelEditingSupport<E> editingSupport = new ModelEditingSupport<>();
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected boolean relationsAgressiveCheck;

    public Model<E, Q> copy() throws Exception {
        Model<E, Q> copied = getClass().newInstance();
        for (E entity : entities.values()) {
            copied.addEntity((E) entity.copy());
        }
        for (Relation<E> relation : relations) {
            Relation<E> rcopied = relation.copy();
            copied.resolveRelation(rcopied);
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

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public void clearRelations() {
        if (relations != null) {
            relations.clear();
            entities.values().stream().forEach((e) -> {
                e.getInRelations().clear();
                e.getOutRelations().clear();
            });
        }
    }

    /**
     * Tests if entities' internal data is actual, updating it if necessary.
     *
     * @return True if any change occur and false is all is actual.
     * @throws java.lang.Exception
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
            resolveRelation(rel);
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

    public ModelEditingSupport<E> getEditingSupport() {
        return editingSupport;
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
        return null;
    }

    public abstract E newGenericEntity();

    public abstract <M extends Model<E, ?>> void accept(ModelVisitor<E, M> visitor);

    /**
     * Registers an <code>DatamodelEditingValidator</code>. The validator is
     * notified whenever an datamodel edit action will occur.
     *
     * @param v an <code>DatamodelEditingValidator</code> object
     * @see #removeEditingValidator(com.eas.client.model.ModelEditingValidator) 
     */
    public synchronized void addEditingValidator(ModelEditingValidator<E> v) {
        editingSupport.addValidator(v);
    }

    /**
     * Removes an <code>DatamodelEditingValidator</code>.
     *
     * @param v the <code>DatamodelEditingValidator</code> object to be removed
     * @see #addEditingValidator(com.eas.client.model.ModelEditingValidator) 
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

    public E getEntityById(Long aId) {
        return entities.get(aId);
    }

    public void setEntities(Map<Long, E> aValue) {
        entities = aValue;
    }

    public Set<Relation<E>> getRelations() {
        return relations;
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

    public void resolveRelation(Relation<E> aRelation) throws Exception {
        resolveRelationEntities(aRelation);
        resolveRelationFields(aRelation);
    }

    protected void resolveRelationFields(Relation<E> aRelation) {
        if (aRelation.getLeftField() != null) {
            String targetName = aRelation.getLeftField().getName();
            if (aRelation.getLeftEntity() != null) {
                Q lQuery = aRelation.getLeftEntity().getQuery();
                if (aRelation.isLeftParameter() && aRelation.getLeftEntity().getQueryName() != null) {
                    if (lQuery != null) {
                        aRelation.setLeftField(lQuery.getParameters().get(targetName));
                    } else if (relationsAgressiveCheck) {
                        aRelation.setLeftField(null);
                    } else {
                        aRelation.setLeftField(new Parameter(targetName));
                    }
                } else {
                    Fields lFields = aRelation.getLeftEntity().getFields();
                    if (lFields != null && lQuery != null && lQuery.isMetadataAccessible()) {
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
                Q rQuery = aRelation.getRightEntity().getQuery();
                if (aRelation.isRightParameter() && aRelation.getRightEntity().getQueryName() != null) {
                    if (rQuery != null) {
                        aRelation.setRightField(rQuery.getParameters().get(targetName));
                    } else if (relationsAgressiveCheck) {
                        aRelation.setRightField(null);
                    } else {
                        aRelation.setRightField(new Parameter(targetName));
                    }
                } else {
                    Fields rFields = aRelation.getRightEntity().getFields();
                    if (rFields != null && rQuery != null && rQuery.isMetadataAccessible()) {
                        aRelation.setRightField(rFields.get(targetName));
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

    protected void resolveRelationEntities(Relation<E> aRelation) {
        if (aRelation.getLeftEntity() != null) {
            aRelation.setLeftEntity(getEntityById(aRelation.getLeftEntity().getEntityId()));
        }
        if (aRelation.getRightEntity() != null) {
            aRelation.setRightEntity(getEntityById(aRelation.getRightEntity().getEntityId()));
        }
    }

    public void checkRelationsIntegrity() {
        List<Relation<E>> toDel = new ArrayList<>();
        relations.stream().forEach((rel) -> {
            if (rel.getLeftEntity() == null || (rel.getLeftField() == null && rel.getLeftParameter() == null)
                    || rel.getRightEntity() == null || (rel.getRightField() == null && rel.getRightParameter() == null)) {
                toDel.add(rel);
            }
        });
        toDel.stream().forEach((rel) -> {
            removeRelation(rel);
        });
    }
}
