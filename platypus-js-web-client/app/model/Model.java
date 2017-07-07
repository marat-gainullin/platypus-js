package com.eas.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.eas.core.Logger;
import com.eas.client.AppClient;
import com.eas.client.CallbackAdapter;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.core.Callable;
import com.eas.core.HasPublished;
import com.eas.core.Utils;
import com.eas.core.Utils.JsObject;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;

/**
 * @author mg
 */
public class Model implements HasPublished {

    protected AppClient client;
    //
    protected JavaScriptObject jsPublished;

    public static class SimpleEntry implements Entry<Entity, Integer> {

        protected Entity key;
        protected Integer value;

        private SimpleEntry(Entity aKey, Integer aValue) {
            super();
            key = aKey;
            value = aValue;
        }

        @Override
        public Entity getKey() {
            return key;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public Integer setValue(Integer aValue) {
            Integer oldValue = value;
            value = aValue;
            return oldValue;
        }
    }

    public Model copy() throws Exception {
        Model copied = new Model(client);
        for (Entity entity : entities.values()) {
            copied.addEntity(entity.copy());
        }
        for (Relation relation : relations) {
            Relation rcopied = relation.copy();
            resolveCopiedRelation(rcopied, copied);
            copied.addRelation(rcopied);
        }
        for (ReferenceRelation relation : referenceRelations) {
            ReferenceRelation rcopied = (ReferenceRelation) relation.copy();
            resolveCopiedRelation(rcopied, copied);
            copied.addRelation(rcopied);
        }
        return copied;
    }

    protected void resolveCopiedRelation(Relation aRelation, Model aModel) throws Exception {
        if (aRelation.getLeftEntity() != null) {
            aRelation.setLeftEntity(aModel.getEntityById(aRelation.getLeftEntity().getEntityId()));
        }
        if (aRelation.getRightEntity() != null) {
            aRelation.setRightEntity(aModel.getEntityById(aRelation.getRightEntity().getEntityId()));
        }
        if (aRelation.getLeftField() != null) {
            if (aRelation.getLeftEntity() != null) {
                if (aRelation.isLeftParameter() && aRelation.getLeftEntity().getQueryName() != null) {
                    aRelation.setLeftField(aRelation.getLeftEntity().getQuery().getParameters().get(aRelation.getLeftField().getName()));
                } else {
                    aRelation.setLeftField(aRelation.getLeftEntity().getFields().get(aRelation.getLeftField().getName()));
                }
            } else {
                aRelation.setLeftField(null);
            }
        }
        if (aRelation.getRightField() != null) {
            if (aRelation.getRightEntity() != null) {
                if (aRelation.isRightParameter() && aRelation.getRightEntity().getQueryName() != null) {
                    aRelation.setRightField(aRelation.getRightEntity().getQuery().getParameters().get(aRelation.getRightField().getName()));
                } else {
                    aRelation.setRightField(aRelation.getRightEntity().getFields().get(aRelation.getRightField().getName()));
                }
            } else {
                aRelation.setRightField(null);
            }
        }
    }

    public void checkRelationsIntegrity() {
        List<Relation> toDel = new ArrayList<Relation>();
        for (Relation rel : relations) {
            if (rel.getLeftEntity() == null || (rel.getLeftField() == null && rel.getLeftParameter() == null) || rel.getRightEntity() == null
                    || (rel.getRightField() == null && rel.getRightParameter() == null)) {
                toDel.add(rel);
            }
        }
        for (Relation rel : toDel) {
            removeRelation(rel);
        }
        checkReferenceRelationsIntegrity();
    }

    protected void checkReferenceRelationsIntegrity() {
        List<ReferenceRelation> toDel = new ArrayList<ReferenceRelation>();
        for (ReferenceRelation rel : referenceRelations) {
            if (rel.getLeftEntity() == null || (rel.getLeftField() == null && rel.getLeftParameter() == null) || rel.getRightEntity() == null
                    || (rel.getRightField() == null && rel.getRightParameter() == null)) {
                toDel.add(rel);
            }
        }
        for (ReferenceRelation rel : toDel) {
            referenceRelations.remove(rel);
        }
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
     * @see AppClient
     */
    public Model(AppClient aClient) {
        this();
        client = aClient;
    }

    public AppClient getClient() {
        return client;
    }

    public void setClient(AppClient aValue) {
        client = aValue;
    }

    public Set<ReferenceRelation> getReferenceRelations() {
        return Collections.unmodifiableSet(referenceRelations);
    }

    public boolean isPending() {
        for (Entity entity : entities.values()) {
            if (entity.isPending()) {
                return true;
            }
        }
        return false;
    }

    public void addRelation(Relation aRel) {
        if (aRel instanceof ReferenceRelation) {
            referenceRelations.add((ReferenceRelation) aRel);
        } else {
            relations.add(aRel);
            Entity lEntity = aRel.getLeftEntity();
            Entity rEntity = aRel.getRightEntity();
            if (lEntity != null && rEntity != null) {
                lEntity.addOutRelation(aRel);
                rEntity.addInRelation(aRel);
            }
        }
    }

    public Set<Relation> collectRelationsByEntity(Entity aEntity) {
        return aEntity.getInOutRelations();
    }

    @Override
    public JavaScriptObject getPublished() {
        return jsPublished;
    }

    @Override
    public void setPublished(JavaScriptObject aValue) {
        if (jsPublished != aValue) {
            jsPublished = aValue;
            publish();
        }
    }

    private void publish() {
        try {
            publishTopLevelFacade(jsPublished, this);
            publishEntities();
        } catch (Exception ex) {
            Logger.severe(ex);
        }
    }

    private void publishEntities() throws Exception {
        assert jsPublished != null : "JavaScript facade object has to be already installed while publishing rowsets facades.";
        validateQueries();
        //
        for (Entity entity : entities.values()) {
            if (entity.getName() != null && !entity.getName().isEmpty()) {
                JavaScriptObject publishedEntity = publishEntity(entity);
                entity.setPublished(publishedEntity);
                jsPublished.<JsObject>cast().inject(entity.getName(), publishedEntity, true, false);
            }
        }
        //
        for (ReferenceRelation aRelation : referenceRelations) {
            String scalarPropertyName = aRelation.getScalarPropertyName();
            String collectionPropertyName = aRelation.getCollectionPropertyName();
            if (scalarPropertyName != null && !scalarPropertyName.isEmpty()) {
                aRelation.getLeftEntity().putOrmScalarDefinition(
                        scalarPropertyName,
                        new Fields.OrmDef(aRelation.getLeftField().getName(), scalarPropertyName, collectionPropertyName, ormPropertiesDefiner.scalar(
                                aRelation.getRightEntity().getPublished(),
                                aRelation.getRightField().getName(),
                                aRelation.getLeftField().getName())));
            }
            if (collectionPropertyName != null && !collectionPropertyName.isEmpty()) {
                aRelation.getRightEntity().putOrmCollectionDefinition(
                        collectionPropertyName,
                        new Fields.OrmDef(collectionPropertyName, scalarPropertyName, ormPropertiesDefiner.collection(
                                aRelation.getLeftEntity().getPublished(),
                                aRelation.getRightField().getName(),
                                aRelation.getLeftField().getName())));
            }
        }
    }

    private static DefinitionsContainer ormPropertiesDefiner = DefinitionsContainer.init();

    private static final class DefinitionsContainer extends JavaScriptObject {

        protected DefinitionsContainer() {
        }

        public native static DefinitionsContainer init()/*-{
            var Logger = @com.eas.core.Predefine::logger;
            var M = @com.eas.model.JsModel::managed;
            return {
                scalarDef : function(targetEntity, targetFieldName, sourceFieldName) {
                    var _self = this;
                    _self.enumerable = false;
                    _self.configurable = true;
                    _self.get = function() {
                        var criterion = {};
                        criterion[targetFieldName] = this[sourceFieldName];
                        var found = targetEntity.find(criterion);
                        return found && found.length == 1 ? found[0] : null;
                    };
                    _self.set = function(aValue) {
                        this[sourceFieldName] = aValue ? aValue[targetFieldName] : null;
                    };
                },
                collectionDef : function(sourceEntity, targetFieldName, sourceFieldName) {
                    var _self = this;
                    _self.enumerable = false;
                    _self.configurable = true;
                    _self.get = function() {
                        var criterion = {};
                        var targetKey = this[targetFieldName];
                                            criterion[sourceFieldName] = targetKey;
                                            var found = sourceEntity.find(criterion);
                        M.manageArray(found, {
                            spliced: function (added, deleted) {
                                added.forEach(function (item) {
                                    item[sourceFieldName] = targetKey;
                                });
                                deleted.forEach(function (item) {
                                    item[sourceFieldName] = null;
                                });
                                @com.eas.core.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(found, {source: found, propertyName: 'length'});
                            },
                            scrolled: function (aSubject, oldCursor, newCursor) {
                                @com.eas.core.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(found, {source: found, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
                            }
                        });
                        @com.eas.core.Utils::listenable(Lcom/google/gwt/core/client/JavaScriptObject;)(found);
                        return found;
                    };
                }
            }
        }-*/;

        public native JavaScriptObject scalar(JavaScriptObject targetEntity, String targetFieldName, String sourceFieldName)/*-{
			var constr = this.scalarDef;
			return new constr(targetEntity, targetFieldName, sourceFieldName);
		}-*/;

        public native JavaScriptObject collection(JavaScriptObject sourceEntity, String targetFieldName, String sourceFieldName)/*-{
			var constr = this.collectionDef;
			return new constr(sourceEntity, targetFieldName, sourceFieldName);
		}-*/;
    }

    public native static void publishTopLevelFacade(JavaScriptObject aTarget, Model aModel) throws Exception/*-{
        var Logger = @com.eas.core.Predefine::logger;
        var publishedModel = aTarget;
        Object.defineProperty(publishedModel, "createQuery", {
            get : function() {
                return function(aQueryId) {
                    Logger.warning("createQuery deprecated call detected. Use loadEntity() instead.");
                    return aModel.@com.eas.model.Model::jsLoadEntity(Ljava/lang/String;)(aQueryId);
                }
            }
        });
        Object.defineProperty(publishedModel, "loadEntity", {
            get : function() {
                return function(aQueryId) {
                    return aModel.@com.eas.model.Model::jsLoadEntity(Ljava/lang/String;)(aQueryId);
                }
            }
        });
        Object.defineProperty(publishedModel, "save", {
            get : function() {
                return function(onScuccess, onFailure) {
                    aModel.@com.eas.model.Model::save(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onScuccess, onFailure);
                }
            }
        });
        Object.defineProperty(publishedModel, "revert", {
            get : function() {
                return function() {
                    aModel.@com.eas.model.Model::revert()();
                }
            }
        });
        Object.defineProperty(publishedModel, "requery", {
            get : function() {
                return function(onSuccess, onFailure) {
                    aModel.@com.eas.model.Model::requery(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
                }
            }
        });
        Object.defineProperty(publishedModel, "execute", {
            get : function() {
                return function(onSuccess, onFailure) {
                    aModel.@com.eas.model.Model::execute(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
                }
            }
        });
        Object.defineProperty(publishedModel, "unwrap", {
            get : function() {
                return function() {
                    return aModel;
                }
            }
        });
        Object.defineProperty(publishedModel, "modified", {
            get : function() {
                return aModel.@com.eas.model.Model::isModified()();
            }
        });
        Object.defineProperty(publishedModel, "pending", {
            get : function() {
                return aModel.@com.eas.model.Model::isPending()();
            }
        });
    }-*/;

    public void removeRelation(Relation aRelation) {
        relations.remove(aRelation);
        Entity lEntity = aRelation.getLeftEntity();
        Entity rEntity = aRelation.getRightEntity();
        if (lEntity != null && rEntity != null) {
            lEntity.removeOutRelation(aRelation);
            rEntity.removeInRelation(aRelation);
        }
    }

    public void addEntity(Entity aEntity) {
        entities.put(aEntity.getEntityId(), aEntity);
        aEntity.setModel(this);
    }

    public boolean removeEntity(Entity aEnt) {
        if (aEnt != null) {
            return (removeEntity(aEnt.getEntityId()) != null);
        }
        return false;
    }

    public Entity removeEntity(String aEntId) {
        Entity lent = entities.get(aEntId);
        if (lent != null) {
            entities.remove(aEntId);
        }
        return lent;
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public Entity getEntityById(String aId) {
        return entities.get(aId);
    }

    public void setEntities(Map<String, Entity> aValue) {
        entities = aValue;
    }

    public Set<Relation> getRelations() {
        return relations;
    }

    public void setRelations(Set<Relation> aRelations) {
        relations = aRelations;
    }

    public void executeEntities(boolean refresh, Set<Entity> toExecute) throws Exception {
        if (refresh) {
            for (Entity entity : toExecute) {
                entity.invalidate();
            }
        }
        for (Entity entity : toExecute) {
            entity.internalExecute(null);
        }
    }

    private Set<Entity> rootEntities() {
        final Set<Entity> rootEntities = new Set();
        for (Entity entity : entities.values()) {
            if (entity.getInRelations().isEmpty()) {
                rootEntities.add(entity);
            }
        }
        return rootEntities;
    }

    public RequeryProcess getProcess() {
        return process;
    }

    public void setProcess(RequeryProcess aValue) {
        process = aValue;
    }

    public void terminateProcess(Entity aSource, String aErrorMessage) {
        if (process != null) {
            if (aErrorMessage != null) {
                process.errors.put(aSource, aErrorMessage);
            }
            if (!isPending()) {
                RequeryProcess pr = process;
                process = null;
                pr.end();
            }
        }
    }

    public boolean isTypeSupported(int type) throws Exception {
        return true;
    }

    public boolean isNamePresent(String aName, Collection<Entity> aEntities, Entity toExclude, Field field2Exclude) {
        if (aEntities != null && aName != null) {
            for (Entity ent : aEntities) {
                if (ent != null && ent != toExclude) {
                    String lName = ent.getName();
                    if (lName != null && aName.equals(lName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public JavaScriptObject getChangeLog() {
        return changeLog;
    }

    public void accept(ModelVisitor visitor) {
        visitor.visit(this);
    }

    public boolean isModified() throws Exception {
        return changeLog.length() > 0;
    }

    public void save(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            // Scheduling is needed because of asynchronous nature of Object.observe's callback calling process.
            @Override
            public void execute() {
                try {
                    client.requestCommit(changeLog, new CallbackAdapter<Void, String>() {

                        @Override
                        protected void doWork(Void aVoid) throws Exception {
                            commited();
                            if (onSuccess != null) {
                                Utils.invokeJsFunction(onSuccess);
                            }
                        }

                        @Override
                        public void onFailure(String aReason) {
                            try {
                                rolledback();
                                if (onFailure != null) {
                                    Utils.executeScriptEventVoid(jsPublished, onFailure, aReason);
                                }
                            } catch (Exception ex) {
                                Logger.severe(ex);
                            }
                        }

                    });
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
            }

        });
    }

    public void revert() throws Exception {
        changeLog.splice(0, changeLog.length());
        for (Entity e : entities.values()) {
            e.applyLastSnapshot();
        }
    }

    public void commited() throws Exception {
        changeLog.splice(0, changeLog.length());
        for (Entity e : entities.values()) {
            e.takeSnapshot();
        }
    }

    public void rolledback() throws Exception {
        Logger.severe("Model changes are rolled back");
    }

    public void requery(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
        requery(new CallbackAdapter<JavaScriptObject, String>() {
            @Override
            protected void doWork(JavaScriptObject aRowset) throws Exception {
                if (onSuccess != null) {
                    Utils.invokeJsFunction(onSuccess);
                }
            }

            @Override
            public void onFailure(String reason) {
                if (onFailure != null) {
                    try {
                        Utils.executeScriptEventVoid(jsPublished, onFailure, reason);
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }
        });
    }

    public void requery(Callback<JavaScriptObject, String> aCallback) throws Exception {
        inNewProcess(new Callable() {
            @Override
            public void call() throws Exception {
                revert();
                executeEntities(true, rootEntities());
            }

        }, aCallback);
    }

    public void execute(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
        execute(new CallbackAdapter<JavaScriptObject, String>() {
            @Override
            protected void doWork(JavaScriptObject aRowset) throws Exception {
                if (onSuccess != null) {
                    Utils.invokeJsFunction(onSuccess);
                }
            }

            @Override
            public void onFailure(String reason) {
                if (onFailure != null) {
                    try {
                        Utils.executeScriptEventVoid(jsPublished, onFailure, reason);
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }
        });
    }

    public void execute(Callback<JavaScriptObject, String> aCallback) throws Exception {
        inNewProcess(new Callable() {
            @Override
            public void call() throws Exception {
                executeEntities(false, rootEntities());
            }
        }, aCallback);
    }

    public void inNewProcess(Callable aAction, Callback<JavaScriptObject, String> aCallback) throws Exception {
        if (process != null) {
            process.cancel();
            process = null;
        }
        if (aCallback != null) {
            process = new RequeryProcess(aCallback);
        }
        aAction.call();
        if (!isPending() && process != null) {
            process.end();
            process = null;
        }
    }

    public void validateQueries() throws Exception {
        for (Entity entity : entities.values()) {
            entity.validateQuery();
        }
    }

    protected static final String USER_DATASOURCE_NAME = "userEntity";

    public Object jsLoadEntity(String aQueryName) throws Exception {
        if (client == null) {
            throw new NullPointerException("Null client detected while creating an entity");
        }
        Entity entity = new Entity(this);
        entity.setName(USER_DATASOURCE_NAME);
        entity.setQueryName(aQueryName);
        entity.validateQuery();
        // addEntity(entity); To avoid memory leaks you should not add the
        // entity to the model!
        return publishEntity(entity);
    }
}
