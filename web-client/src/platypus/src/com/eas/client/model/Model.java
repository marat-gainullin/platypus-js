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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.Callback;
import com.eas.client.Cancellable;
import com.eas.client.CancellableCallback;
import com.eas.client.CancellableCallbackAdapter;
import com.eas.client.Utils;
import com.eas.client.Utils.JsObject;
import com.eas.client.application.AppClient;
import com.eas.client.beans.PropertyChangeSupport;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author mg
 */
public class Model {

	public static final String SCRIPT_MODEL_NAME = "model";
	public static final String PARAMETERS_SCRIPT_NAME = "params";
	public static final String DATASOURCE_METADATA_SCRIPT_NAME = "schema";
	public static final String DATASOURCE_NAME_TAG_NAME = "Name";
	public static final String DATASOURCE_TITLE_TAG_NAME = "Title";
	public static final String DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME = "onBeforeChange";
	public static final String DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME = "onAfterChange";
	public static final String DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME = "onBeforeScroll";
	public static final String DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME = "onAfterScroll";
	public static final String DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME = "onBeforeInsert";
	public static final String DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME = "onAfterInsert";
	public static final String DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME = "onBeforeDelete";
	public static final String DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME = "onAfterDelete";
	public static final String DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME = "onRequeried";
	public static final String DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME = "onFiltered";
	protected Set<String> savedRowIndexEntities = new HashSet<String>();
	protected List<Entry<Entity, Integer>> savedEntitiesRowIndexes = new ArrayList<Entry<Entity, Integer>>();
	protected AppClient client = null;
	protected Set<Relation> relations = new HashSet<Relation>();
	protected Set<ReferenceRelation> referenceRelations = new HashSet<ReferenceRelation>();
	protected Map<String, Entity> entities = new HashMap<String, Entity>();
	protected ParametersEntity parametersEntity;
	protected Parameters parameters = new Parameters();
	protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	protected boolean runtime = false;
	protected boolean commitable = true;
	protected List<Change> changeLog = new ArrayList<Change>();
	protected Set<TransactionListener> transactionListeners = new HashSet<TransactionListener>();
	//
	protected NetworkProcess process;
	protected int ajustingCounter = 0;
	protected JavaScriptObject module;
	protected Runnable handlersResolver;

	public static class NetworkProcess {
		public Map<Entity, String> errors = new HashMap<Entity, String>();
		public CancellableCallback onSuccess;
		public Callback<String> onFailure;

		public NetworkProcess(CancellableCallback aOnSuccess, Callback<String> aOnFailure) {
			onSuccess = aOnSuccess;
			onFailure = aOnFailure;
		}

		protected String assembleErrors() {
			if (errors != null && !errors.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (Entity entity : errors.keySet()) {
					if (sb.length() > 0)
						sb.append("\n");
					sb.append(errors.get(entity)).append(" (").append(entity.getName()).append("[ ").append(entity.getTitle()).append("])");
				}
				return sb.toString();
			}
			return null;
		}

		public void cancel() throws Exception {
			if (onFailure != null)
				onFailure.run("Canceled");
		}

		public void success() throws Exception {
			if (onSuccess != null)
				onSuccess.run();
		}

		public void failure() throws Exception {
			if (onFailure != null)
				onFailure.run(assembleErrors());
		}

		public void end() throws Exception {
			if (errors.isEmpty())
				success();
			else
				failure();
		}
	}

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
		if (parameters != null) {
			copied.setParameters(parameters.copy());
		}
		if (getParametersEntity() != null) {
			Entity pEntity = copied.getEntityById(getParametersEntity().getEntityId());
			assert pEntity instanceof ParametersEntity;
			copied.setParametersEntity((ParametersEntity) pEntity);
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
				if (aRelation.isLeftParameter() && aRelation.getLeftEntity().getQueryId() != null) {
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
				if (aRelation.isRightParameter() && aRelation.getRightEntity().getQueryId() != null) {
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
            if (rel.getLeftEntity() == null || (rel.getLeftField() == null && rel.getLeftParameter() == null)
                    || rel.getRightEntity() == null || (rel.getRightField() == null && rel.getRightParameter() == null)) {
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
            if (rel.getLeftEntity() == null || (rel.getLeftField() == null && rel.getLeftParameter() == null)
                    || rel.getRightEntity() == null || (rel.getRightField() == null && rel.getRightParameter() == null)) {
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
	public Model() {
		super();
		parametersEntity = new ParametersEntity();
		parametersEntity.setModel(this);
		entities.put(parametersEntity.getEntityId(), parametersEntity);
	}

	/**
	 * Constructor of datamodel. Used in designers.
	 * 
	 * @param aClient
	 *            C instance all queries to be sent to.
	 * @see AppClient
	 */
	public Model(AppClient aClient) {
		this();
		client = aClient;
	}

	public Model(AppClient aClient, Runnable aHandlersResolver) {
		this(aClient);
		handlersResolver = aHandlersResolver;
	}

	public PropertyChangeSupport getChangeSupport() {
		return changeSupport;
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
			if (entity.isPending())
				return true;
		}
		return false;
	}

	public ParametersEntity getParametersEntity() {
		return parametersEntity;
	}

	public Parameters getParameters() {
		return parameters;
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

	public JavaScriptObject getModule() {
		return module;
	}

	public void publish(JavaScriptObject aModule) throws Exception {
		try {
			module = aModule;
			// deprecated
			ParametersEntity parametersEntity = getParametersEntity();
			Entity.publish(module, parametersEntity);
			if (!"params".equals(parametersEntity.getName())) {
				String oldName = parametersEntity.getName();
				try {
					parametersEntity.setName("params");
					Entity.publish(module, parametersEntity);
				} finally {
					parametersEntity.setName(oldName);
				}
			}
			Entity.publishRows(parametersEntity.getPublished());
			//
			publishTopLevelFacade(aModule, this);
			publishRowsets();
		} catch (Exception ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			throw ex;
		}
	}

	public void publishRowsets() throws Exception {
		assert module != null : "Module has to be already installed while publishing rowsets facades.";
		validateQueries();
		for (Entity entity : entities.values()) {
			if (entity instanceof ParametersEntity) {
				String oldName = entity.getName();
				try {
					if (!"params".equals(oldName)) {
						entity.setName("params");
						Entity.publish(module.<JsObject> cast().getJs("model"), entity);
						Entity.publishRows(entity.getPublished());
					}
				} finally {
					entity.setName(oldName);
				}
			} else {
				Entity.publish(module, entity);/* deprecated */
				Entity.publish(module.<JsObject> cast().getJs("model"), entity);
			}
		}
		//
		for (ReferenceRelation aRelation : referenceRelations) {
			String scalarPropertyName = aRelation.getScalarPropertyName();
			if (scalarPropertyName == null || scalarPropertyName.isEmpty()) {
				scalarPropertyName = aRelation.getRightEntity().getName();
			}
			if (scalarPropertyName != null && !scalarPropertyName.isEmpty()) {
				aRelation.getLeftEntity().putOrmDefinition(scalarPropertyName,
				        ormPropertiedDefiner.scalar(aRelation.getRightEntity().getPublished(), aRelation.getRightField().getName(), aRelation.getLeftField().getName()));
			}
			String collectionPropertyName = aRelation.getCollectionPropertyName();
			if (collectionPropertyName == null || collectionPropertyName.isEmpty()) {
				collectionPropertyName = aRelation.getLeftEntity().getName();
			}
			if (collectionPropertyName != null && !collectionPropertyName.isEmpty()) {
				aRelation.getRightEntity().putOrmDefinition(collectionPropertyName,
				        ormPropertiedDefiner.collection(aRelation.getLeftEntity().getPublished(), aRelation.getRightField().getName(), aRelation.getLeftField().getName()));
			}
		}
		// ////////////////
	}

	private static DefinitionsContainer ormPropertiedDefiner = DefinitionsContainer.init();

	private static final class DefinitionsContainer extends JavaScriptObject {

		protected DefinitionsContainer() {
		}

		public native static DefinitionsContainer init()/*-{
			return {
				scalarDef : function(targetEntity, targetFieldName, sourceFieldName) {
					var _self = this;
					_self.enumerable = true;
					_self.configurable = false;
					_self.get = function() {
						var found = targetEntity.find(targetEntity.schema[targetFieldName], this[sourceFieldName]);
						return found.length == 0 ? null : (found.length == 1 ? found[0] : found);
					};
					_self.set = function(aValue) {
						this[sourceFieldName] = aValue ? aValue[targetFieldName] : null;
					};
				},
				collectionDef : function(sourceEntity, targetFieldName, sourceFieldName) {
					var _self = this;
					_self.enumerable = true;
					_self.configurable = false;
					_self.get = function() {
						var res = sourceEntity.find(sourceEntity.schema[sourceFieldName], this[targetFieldName]);
						if (res && res.length > 0) {
							return res;
						} else {
							var emptyCollectionPropName = '-x-empty-collection-' + sourceFieldName;
							var emptyCollection = this[emptyCollectionPropName];
							if (!emptyCollection) {
								emptyCollection = [];
								this[emptyCollectionPropName] = emptyCollection;
							}
							return emptyCollection;
						}
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

	public native static void publishTopLevelFacade(JavaScriptObject aModule, Model aModel) throws Exception/*-{
		var publishedModel = {
			createQuery : function(aQueryId) {
				$wnd.Logger.warning("createQuery deprecated call detected. Use loadEntity() instead.");
				return aModel.@com.eas.client.model.Model::jsLoadEntity(Ljava/lang/String;)(aQueryId);
			},
			loadEntity : function(aQueryId) {
				return aModel.@com.eas.client.model.Model::jsLoadEntity(Ljava/lang/String;)(aQueryId);
			},
			save : function(onScuccess, onFailure) {
				aModel.@com.eas.client.model.Model::save(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onScuccess, onFailure);
			},
			revert : function() {
				aModel.@com.eas.client.model.Model::revert()();
			},
			requery : function(onSuccess, onFailure) {
				aModel.@com.eas.client.model.Model::requery(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
			},
			unwrap : function() {
				return aModel;
			}
		};
		Object.defineProperty(publishedModel, "modified", {
			get : function() {
				return aModel.@com.eas.client.model.Model::isModified()();
			}
		});
		Object.defineProperty(publishedModel, "runtime", {
			configurable : true,
			set : function(aValue) {
				aModel.@com.eas.client.model.Model::setRuntime(Z)(aValue);
				delete publishedModel.runtime;
			}
		});
		Object.defineProperty(publishedModel, "commitable", {
			get : function() {
				return aModel.@com.eas.client.model.Model::isCommitable()();
			},
			set : function(aValue) {
				aModel.@com.eas.client.model.Model::setCommitable(Z)(aValue);
			}
		});
		Object.defineProperty(publishedModel, "pending", {
			get : function() {
				return aModel.@com.eas.client.model.Model::isPending()();
			}
		});
		Object.defineProperty(aModule, "model", {
			get : function() {
				return publishedModel;
			}
		});
		// deprecated
		Object.defineProperty(aModule, "schema", {
			get : function() {
				return aModule.params.schema;
			}
		});
		for ( var i = 0; i < aModule.schema.length; i++) {
			(function() {
				var param = aModule.schema[i];
				Object.defineProperty(aModule, param.name, {
					get : function() {
						return aModule.params[param.name];
					},
					set : function(aValue) {
						aModule.params[param.name] = aValue;
					}
				});
			})();
		}
		//
	}-*/;

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

	public void setParameters(Parameters aParams) {
		parameters = aParams;
	}

	public void setParametersEntity(ParametersEntity aParamsEntity) {
		parametersEntity = aParamsEntity;
		if (parametersEntity != null)
			parametersEntity.setModel(this);
	}

	public void setRelations(Set<Relation> aRelations) {
		relations = aRelations;
	}

	public boolean isRuntime() {
		return runtime;
	}

	public boolean isCommitable() {
		return commitable;
	}

	public void setCommitable(boolean aValue) {
		commitable = aValue;
	}

	public NetworkProcess getProcess() {
		return process;
	}

	public void setProcess(NetworkProcess aValue) {
		process = aValue;
	}

	public void terminateProcess(Entity aSource, String aErrorMessage) throws Exception {
		if (process != null) {
			if (aErrorMessage != null) {
				process.errors.put(aSource, aErrorMessage);
			}
			if (!isPending()) {
				NetworkProcess pr = process;
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
					if (ent instanceof ParametersEntity) {
						Fields params = ent.getFields();
						if (params != null && params.isNameAlreadyPresent(aName, field2Exclude)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public List<com.bearsoft.rowset.changes.Change> getChangeLog() {
		return changeLog;
	}

	public TransactionListener.Registration addTransactionListener(final TransactionListener aListener) {
		transactionListeners.add(aListener);
		return new TransactionListener.Registration() {
			@Override
			public void remove() {
				transactionListeners.remove(aListener);
			}
		};
	}

	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
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
			for (Entity ent : entities.values()) {
				if (ent != null && ent.getRowset() != null) {
					if (ent.getRowset().isModified()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Cancellable save(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		client.getChangeLog().addAll(changeLog);
		if (commitable) {
			return client.commit(new CancellableCallbackAdapter() {

				@Override
				protected void doWork() throws Exception {
					saved();
					if (onSuccess != null)
						Utils.invokeJsFunction(onSuccess);
				}
			}, new Callback<String>() {

				@Override
				public void run(String aResult) throws Exception {
					rolledback();
					if (onFailure != null)
						Utils.executeScriptEventVoid(module, onFailure, aResult);
				}

				@Override
				public void cancel() {
				}
			});
		} else
			return null;
	}

	public void saved() throws Exception {
		changeLog.clear();
		for (TransactionListener l : transactionListeners.toArray(new TransactionListener[] {})) {
			try {
				l.commited();
			} catch (Exception ex) {
				Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public void revert() throws Exception {
		changeLog.clear();
		for (TransactionListener l : transactionListeners.toArray(new TransactionListener[] {})) {
			try {
				l.rolledback();
			} catch (Exception ex) {
				Logger.getLogger(AppClient.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public void rolledback() throws Exception {
	}

	public void requery(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		requery(new CancellableCallbackAdapter() {
			@Override
			protected void doWork() throws Exception {
				if (onSuccess != null)
					Utils.invokeJsFunction(onSuccess);
			}
		}, new Callback<String>() {
			@Override
			public void run(String aResult) throws Exception {
				if (onFailure != null)
					Utils.executeScriptEventVoid(module, onFailure, aResult);
			}

			@Override
			public void cancel() {
			}
		});
	}

	public void requery(CancellableCallback onSuccess, Callback<String> onFailure) throws Exception {
		revert();
		executeRootEntities(true, onSuccess, onFailure);
	}

	public void execute(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		execute(new CancellableCallbackAdapter() {
			@Override
			protected void doWork() throws Exception {
				if (onSuccess != null)
					Utils.invokeJsFunction(onSuccess);
			}
		}, new Callback<String>() {
			@Override
			public void run(String aResult) throws Exception {
				if (onFailure != null)
					Utils.executeScriptEventVoid(module, onFailure, aResult);
			}

			@Override
			public void cancel() {
			}
		});
	}

	public void execute(CancellableCallback onSuccess, Callback<String> onFailure) throws Exception {
		executeRootEntities(false, onSuccess, onFailure);
	}

	public static Set<Entity> gatherNextLayer(Collection<Entity> aLayer) throws Exception {
		Set<Entity> nextLayer = new HashSet<Entity>();
		for (Entity entity : aLayer) {
			Set<Relation> rels = entity.getOutRelations();
			if (rels != null) {
				for (Relation outRel : rels) {
					if (outRel != null) {
						Entity ent = outRel.getRightEntity();
						if (ent != null) {
							nextLayer.add(ent);
						}
					}
				}
			}
		}
		return nextLayer;
	}

	public void executeEntities(Set<Entity> toExecute, CancellableCallback onSuccess, Callback<String> onFailure) throws Exception {
		for (Entity entity : toExecute) {
			entity.internalExecute(null, null);
		}
	}

	private void executeRootEntities(boolean refresh, CancellableCallback onSuccess, Callback<String> onFailure) throws Exception {
		final Set<Entity> toExecute = new HashSet<Entity>();
		for (Entity entity : entities.values()) {
			if (!(entity instanceof ParametersEntity)) {// ParametersEntity is
				                                        // in the entities, so
				                                        // we have to filter it
				                                        // out
				if (refresh)
					entity.invalidate();
				Set<Relation> dependanceRels = new HashSet<Relation>();
				for (Relation inRel : entity.getInRelations()) {
					if (!(inRel.getLeftEntity() instanceof ParametersEntity)) {
						dependanceRels.add(inRel);
					}
				}
				if (dependanceRels.isEmpty()) {
					toExecute.add(entity);
				}
			}
		}
		if (process != null)
			process.cancel();
		process = new NetworkProcess(onSuccess, onFailure);
		executeEntities(toExecute, onSuccess, onFailure);
	}

	public void validateQueries() throws Exception {
		for (Entity entity : entities.values()) {
			entity.validateQuery();
		}
	}

	public boolean isEntityRowIndexStateSaved(Entity entity) {
		return savedRowIndexEntities.contains(entity.getEntityId());
	}

	public void addSavedRowIndex(Entity entity, int aIndex) {
		boolean res = isAjusting();
		assert res;
		assert (entity != null);
		if (!isEntityRowIndexStateSaved(entity)) {
			Entry<Entity, Integer> entry = new SimpleEntry(entity, aIndex);
			savedEntitiesRowIndexes.add(entry);
			savedRowIndexEntities.add(entity.getEntityId());
		}
	}

	public void restoreRowIndexes() {
		boolean res = isAjusting();
		assert res;
		if (ajustingCounter == 1 && savedEntitiesRowIndexes != null && savedRowIndexEntities != null) {
			for (Entry<Entity, Integer> entr : savedEntitiesRowIndexes) {
				if (entr != null) {
					try {
						Entity ent = entr.getKey();
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

	public void setRuntime(boolean aValue) throws Exception {
		boolean oldValue = runtime;
		runtime = aValue;
		if (!oldValue && runtime) {
			resolveHandlers();
			if (module != null)
				module.<Utils.JsModule> cast().clearFunctionsContainer();
			validateQueries();
			executeRootEntities(true, null, null);
		}
	}

	protected void resolveHandlers() {
		if (handlersResolver != null) {
			handlersResolver.run();
			handlersResolver = null;
		}
	}

	protected static final String USER_DATASOURCE_NAME = "userEntity";

	public synchronized Object jsLoadEntity(String aQueryId) throws Exception {
		if (client == null) {
			throw new NullPointerException("Null client detected while creating an entity");
		}
		Entity entity = new Entity(this);
		entity.setName(USER_DATASOURCE_NAME);
		entity.setQueryId(aQueryId);
		entity.validateQuery();
		// addEntity(entity); To avoid memory leaks you should not add the
		// entity in the model!
		return Entity.publishEntityFacade(entity);
	}
}
