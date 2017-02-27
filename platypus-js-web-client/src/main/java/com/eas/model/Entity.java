package com.eas.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.CallbackAdapter;
import com.eas.client.IdGenerator;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.queries.Query;
import com.eas.core.Callable;
import com.eas.core.Cancellable;
import com.eas.core.HasPublished;
import com.eas.core.Utils;
import com.eas.core.Utils.JsObject;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 
 * @author mg
 */
public class Entity implements HasPublished {

	protected static final String PENDING_ASSUMPTION_FAILED_MSG = "pending assigned to null without pending.cancel() call.";
	public static final String QUERY_REQUIRED = "All model entities must have a query";
	// runtime
	protected JavaScriptObject onRequeried;
	protected JavaScriptObject snapshotConsumer;
	protected JavaScriptObject snapshotProducer;
	protected JavaScriptObject lastSnapshot = JavaScriptObject.createArray();
	protected JavaScriptObject jsPublished;
	protected HandlerRegistration cursorListener;
	protected Cancellable pending;
	protected boolean valid;
	protected String title;
	protected String name;
	protected String entityId = String.valueOf((long) IdGenerator.genId());
	protected String queryName;
	protected Model model;
	protected Query query;
	protected Set<Relation> inRelations = new HashSet<>();
	protected Set<Relation> outRelations = new HashSet<>();

	public Entity() {
		super();
	}

	public Entity(Model aModel) {
		this();
		model = aModel;
	}

	public Entity(String aQueryName) {
		this();
		queryName = aQueryName;
	}

	public void putOrmScalarDefinition(String aName, Fields.OrmDef aDefinition) {
		if (aName != null && !aName.isEmpty() && aDefinition != null) {
			Map<String, Fields.OrmDef> defs = getFields().getOrmScalarDefinitions();
			if (!defs.containsKey(aName)) {
				getFields().putOrmScalarDefinition(aName, aDefinition);
			} else {
				Logger.getLogger(Entity.class.getName()).log(
				        Level.FINE,
				        "ORM property " + aName + " redefinition attempt on entity " + name != null && !name.isEmpty() ? name : "" + " " + title != null && !title.isEmpty() ? "[" + title + "]" : ""
				                + ".");
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
				Logger.getLogger(Entity.class.getName()).log(
				        Level.FINE,
				        "ORM property " + aName + " redefinition attempt on entity " + name != null && !name.isEmpty() ? name : "" + " " + title != null && !title.isEmpty() ? "[" + title + "]" : ""
				                + ".");
			}
		}
	}

	public Map<String, Fields.OrmDef> getOrmCollectionsDefinitions() {
		return getFields().getOrmCollectionsDefinitions();
	}

	private static native JavaScriptObject publishFacade(Entity nEntity, JavaScriptObject aTarget)/*-{
		Object.defineProperty(aTarget, 'elementClass', {
			get : function() {
				return nEntity.@com.eas.model.Entity::getElementClass()();
			},
			set : function(aValue) {
				nEntity.@com.eas.model.Entity::setElementClass(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aTarget, 'onRequeried', {
			get : function() {
				return nEntity.@com.eas.model.Entity::getOnRequeried()();
			},
			set : function(aValue) {
				nEntity.@com.eas.model.Entity::setOnRequeried(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aTarget, 'enqueueUpdate', {
			value : function(aParams) {
				nEntity.@com.eas.model.Entity::enqueueUpdate(Lcom/google/gwt/core/client/JavaScriptObject;)(aParams);
			}
		});
		Object.defineProperty(aTarget, 'executeUpdate', {
			value : function(onSuccess, onFailure) {
				nEntity.@com.eas.model.Entity::executeUpdate(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
			}
		});
		Object.defineProperty(aTarget, 'execute', {
			value : function(onSuccess, onFailure) {
				nEntity.@com.eas.model.Entity::execute(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
			}
		});
		Object.defineProperty(aTarget, 'query', {
                        value : function(params, onSuccess, onFailure) {
                                nEntity.@com.eas.model.Entity::query(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(params, onSuccess, onFailure);
                        }
                });
		Object.defineProperty(aTarget, 'requery', {
			value : function(onSuccess, onFailure) {
				nEntity.@com.eas.model.Entity::requery(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
			}
		});
		Object.defineProperty(aTarget, 'append', {
			value : function(aData) {
				nEntity.@com.eas.model.Entity::append(Lcom/google/gwt/core/client/JavaScriptObject;)(aData);
			}
		});
		Object.defineProperty(aTarget, 'update', {
                        value : function(params, onSuccess, onFailure) {
                                nEntity.@com.eas.model.Entity::update(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(params, onSuccess, onFailure);
                        }
                });
	}-*/;

	public Fields getFields() {
		if (query != null) {
			return query.getFields();
		} else {
			return null;
		}
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model aValue) {
		model = aValue;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String aValue) {
		entityId = aValue;
	}

	public String getTitle() {
		String ltitle = title;
		if (ltitle == null || ltitle.isEmpty()) {
			try {
				Query lquery = getQuery();
				ltitle = lquery.getTitle();
			} catch (Exception ex) {
				Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
				ltitle = "";
			}
			setTitle(ltitle);
		}
		return ltitle;
	}

	public void setTitle(String aValue) {
		title = aValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String aValue) {
		name = aValue;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String aValue) {
		if (queryName == null ? aValue != null : !queryName.equals(aValue)) {
			setQuery(null);
		}
		queryName = aValue;
	}

	public Query getQuery() throws Exception {
		assert query != null : "Missing definition of entity '" + queryName + "'";
		return query;
	}

	public void setQuery(Query aValue) {
		query = aValue;
	}

	public boolean removeOutRelation(Relation aRelation) {
		return outRelations.remove(aRelation);
	}

	public boolean removeInRelation(Relation aRelation) {
		return inRelations.remove(aRelation);
	}

	public boolean addOutRelation(Relation aRelation) {
		if (!(aRelation instanceof ReferenceRelation))
			return outRelations.add(aRelation);
		else
			return false;
	}

	public boolean addInRelation(Relation aRelation) {
		if (!(aRelation instanceof ReferenceRelation))
			return inRelations.add(aRelation);
		else
			return false;
	}

	public Set<Relation> getInRelations() {
		return inRelations;
	}

	public Set<Relation> getOutRelations() {
		return outRelations;
	}

	public Set<Relation> getInOutRelations() {
		Set<Relation> lInOutRelations = new HashSet<Relation>();
		lInOutRelations.addAll(inRelations);
		lInOutRelations.addAll(outRelations);
		return lInOutRelations;
	}

	protected boolean isTagValid(String aTagName) {
		return true;
	}

	public JavaScriptObject getSnapshotConsumer() {
		return snapshotConsumer;
	}

	public void setSnapshotConsumer(JavaScriptObject aValue) {
		snapshotConsumer = aValue;
	}

	public JavaScriptObject getSnapshotProducer() {
		return snapshotProducer;
	}

	public void setSnapshotProducer(JavaScriptObject aValue) {
		snapshotProducer = aValue;
	}

	public void takeSnapshot() {
		if (snapshotProducer != null) {
			lastSnapshot = (JavaScriptObject) snapshotProducer.<Utils.JsObject> cast().call(null, null);
		}
	}

	public void applyLastSnapshot() {
		applySnapshot(lastSnapshot);
	}

	public void applySnapshot(JavaScriptObject aValue) {
		lastSnapshot = aValue;
		// Apply aValue as a snapshot. Be aware of change log!
		if (snapshotConsumer != null) {// snapshotConsumer is null in designer
			snapshotConsumer.<Utils.JsObject> cast().call(null, aValue, true);
		}
	}

	private static class CancellableContainer {
		public Cancellable future;
	}

	protected void refreshRowset(final Callback<JavaScriptObject, String> aCallback) throws Exception {
		final CancellableContainer f = new CancellableContainer();
		f.future = query.execute(new CallbackAdapter<JavaScriptObject, String>() {

			@Override
			public void doWork(JavaScriptObject aResult) throws Exception {
				if (pending == f.future) {
					// Apply aRowset as a snapshot. Be aware of change log!
					applySnapshot(aResult);
					valid = true;
					pending = null;
					model.terminateProcess(Entity.this, null);
					if (onRequeried != null) {
						try {
							Utils.JsObject event = JavaScriptObject.createObject().cast();
							event.setJs("source", jsPublished);
							onRequeried.<Utils.JsObject> cast().call(jsPublished, event);
						} catch (Exception ex) {
							Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					if (aCallback != null) {
						aCallback.onSuccess(jsPublished);
					}
				}
			}

			@Override
			public void onFailure(String aMessage) {
				if (pending == f.future) {
					valid = true;
					pending = null;
					model.terminateProcess(Entity.this, aMessage);
					if (aCallback != null) {
						aCallback.onFailure(aMessage);
					}
				}
			}

		});
		pending = f.future;
	}

	public void validateQuery() throws Exception {
		if (query == null) {
			setQuery(model.client.getCachedAppQuery(queryName));
		}
	}

	public Entity copy() throws Exception {
		assert model != null : "Entities can't exist without a model";
		Entity copied = new Entity(model);
		assign(copied);
		return copied;
	}

	public JavaScriptObject getOnRequeried() {
		return onRequeried;
	}

	public void setOnRequeried(JavaScriptObject aValue) {
		onRequeried = aValue;
	}

	public boolean isPending() {
		return pending != null;
	}

	public void requery(final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
		requery(new CallbackAdapter<JavaScriptObject, String>() {
			@Override
			protected void doWork(JavaScriptObject result) throws Exception {
				if (aOnSuccess != null) {
					aOnSuccess.<Utils.JsObject> cast().apply(jsPublished, JavaScriptObject.createArray());
				}
			}

			@Override
			public void onFailure(String reason) {
				if (aOnFailure != null) {
					aOnFailure.<Utils.JsObject> cast().call(jsPublished, reason);
				}
			}

		});
	}

	public void query(JavaScriptObject aParams, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
		Query copied = query.copy();
		if (aParams != null) {
			Utils.JsObject params = aParams.cast();
			JsArrayString keys = params.keys();
			for (int i = 0; i < keys.length(); i++) {
				String key = keys.get(i);
				Object pValue = params.getJava(key);
				Parameter p = copied.getParameters().get(key);
				if (p != null) {
					p.setValue(Utils.toJava(pValue));
				}
			}
		}
		copied.execute(new CallbackAdapter<JavaScriptObject, String>() {
			@Override
			protected void doWork(JavaScriptObject result) throws Exception {
				if (aOnSuccess != null) {
					aOnSuccess.<Utils.JsObject> cast().call(jsPublished, result);
				}
			}

			@Override
			public void onFailure(String reason) {
				if (aOnFailure != null) {
					aOnFailure.<Utils.JsObject> cast().call(jsPublished, reason);
				}
			}

		});
	}

	public void requery(final Callback<JavaScriptObject, String> aCallback) throws Exception {
		if (model != null) {
			invalidate();
			execute(aCallback);
		}
	}

	public void append(JavaScriptObject aData) throws Exception {
		if (snapshotConsumer != null) {
			snapshotConsumer.<Utils.JsObject> cast().call(null, aData, false);
		}
	}

	public void enqueueUpdate(JavaScriptObject aParams) throws Exception {
		Utils.JsObject changeLog = model.getChangeLog().<Utils.JsObject> cast();
		Query copied = query.copy();
		if (aParams != null) {
			Utils.JsObject params = aParams.cast();
			JsArrayString keys = params.keys();
			for (int i = 0; i < keys.length(); i++) {
				String key = keys.get(i);
				Object pValue = params.getJava(key);
				Parameter p = copied.getParameters().get(key);
				if (p != null) {
					p.setValue(Utils.toJava(pValue));
				}
			}
		}
		changeLog.setSlot(changeLog.length(), copied.prepareCommand());
	}

	/**
	 * Legacy method.
	 * @param onSuccess
	 * @param onFailure
	 * @throws Exception
	 */
	public void executeUpdate(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		Utils.JsObject changeLog = JavaScriptObject.createArray(1).cast();
		changeLog.setSlot(0, query.prepareCommand());
		model.client.requestCommit(changeLog, new CallbackAdapter<Void, String>() {

			@Override
			protected void doWork(Void aVoid) throws Exception {
				if (onSuccess != null)
					Utils.invokeJsFunction(onSuccess);
			}

			@Override
			public void onFailure(String aReason) {
				try {
					if (onFailure != null)
						Utils.executeScriptEventVoid(jsPublished, onFailure, aReason);
				} catch (Exception ex) {
					Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		});
	}
	
	/**
	 * Executes a dml query of the entity. Uses first argument JavaScript object as parameters.
	 * @param aOnSuccess
	 * @param aOnFailure
	 * @throws Exception
	 */
	public void update(final JavaScriptObject aParams, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
		Query copied = query.copy();
		if (aParams != null) {
			Utils.JsObject params = aParams.cast();
			JsArrayString keys = params.keys();
			for (int i = 0; i < keys.length(); i++) {
				String key = keys.get(i);
				Object pValue = params.getJava(key);
				Parameter p = copied.getParameters().get(key);
				if (p != null) {
					p.setValue(Utils.toJava(pValue));
				}
			}
		}
		Utils.JsObject changeLog = JavaScriptObject.createArray(1).cast();
		changeLog.setSlot(0, copied.prepareCommand());
		model.client.requestCommit(changeLog, new CallbackAdapter<Void, String>() {

			@Override
			protected void doWork(Void aVoid) throws Exception {
				if (aOnSuccess != null)
					Utils.invokeJsFunction(aOnSuccess);
			}

			@Override
			public void onFailure(String aReason) {
				try {
					if (aOnFailure != null)
						Utils.executeScriptEventVoid(jsPublished, aOnFailure, aReason);
				} catch (Exception ex) {
					Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		});
	}

	public JavaScriptObject getChangeLog() {
		return model.getChangeLog();
	}

	public void execute(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		execute(new CallbackAdapter<JavaScriptObject, String>() {

			@Override
			protected void doWork(JavaScriptObject aResult) throws Exception {
				if (onSuccess != null)
					Utils.invokeJsFunction(onSuccess);
			}

			@Override
			public void onFailure(String reason) {
				if (onFailure != null) {
					try {
						Utils.executeScriptEventVoid(jsPublished, onFailure, reason);
					} catch (Exception ex) {
						Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

		});
	}

	public void execute(final Callback<JavaScriptObject, String> aCallback) throws Exception {
		if (model != null) {
			if (getOutRelations().isEmpty()) {
				internalExecute(aCallback);
			} else {
				model.inNewProcess(new Callable() {
					@Override
					public void call() throws Exception {
						internalExecute(null);
					}
				}, new Callback<JavaScriptObject, String>() {
					@Override
					public void onSuccess(JavaScriptObject result) {
						if(aCallback != null){
							aCallback.onSuccess(jsPublished);
						}
					}

					@Override
					public void onFailure(String reason) {
						if(aCallback != null){
							aCallback.onFailure(reason);
						}
					}
				});
			}
		}
	}

	protected void internalExecute(final Callback<JavaScriptObject, String> aCallback) throws Exception {
		assert query != null : QUERY_REQUIRED;
		bindQueryParameters();
		if (isValid()) {
			if (aCallback != null) {
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

					@Override
					public void execute() {
						aCallback.onSuccess(null);
					}
				});
			}
		} else {
			// Requery if query parameters values have been changed while
			// bindQueryParameters() call
			// or we are forced to refresh the data via requery() call.
			silentUnpend();
			refreshRowset(aCallback);
			assert pending != null || (aCallback == null && model.process == null);
			// filtering will be done while processing onRequeried event in
			// ApplicationEntity code
		}
	}

	public void unpend() {
		if (pending != null) {
			pending.cancel();
			pending = null;
		}
	}

	protected void silentUnpend() {
		Model.RequeryProcess lprocess = model.process;
		model.process = null;
		try {
			unpend();
		} finally {
			model.process = lprocess;
		}
	}

	protected void internalExecuteChildren(boolean refresh) throws Exception {
		Set<Relation> rels = getOutRelations();
		if (rels != null) {
			Set<Entity> toExecute = new HashSet<>();
			for (Relation outRel : rels) {
				if (outRel != null) {
					Entity rEntity = outRel.getRightEntity();
					if (rEntity != null) {
						toExecute.add(rEntity);
					}
				}
			}
			model.executeEntities(refresh, toExecute);
		}
	}

	protected void internalExecuteChildren(boolean refresh, int aOnlyFieldIndex) throws Exception {
		Set<Relation> rels = getOutRelations();
		if (rels != null) {
			Field onlyField = getFields().get(aOnlyFieldIndex);
			Set<Entity> toExecute = new HashSet<>();
			for (Relation outRel : rels) {
				if (outRel != null) {
					Entity rEntity = outRel.getRightEntity();
					if (rEntity != null && outRel.getLeftField() == onlyField) {
						toExecute.add(rEntity);
					}
				}
			}
			model.executeEntities(refresh, toExecute);
		}
	}

	public void bindQueryParameters() throws Exception {
		Query selfQuery = getQuery();
		Parameters selfParameters = selfQuery.getParameters();
		boolean parametersModified = false;
		Set<Relation> inRels = getInRelations();
		if (inRels != null && !inRels.isEmpty()) {
			for (Relation relation : inRels) {
				if (relation != null && relation.isRightParameter()) {
					Entity leftEntity = relation.getLeftEntity();
					if (leftEntity != null) {
						Object pValue = null;
						if (relation.isLeftField()) {
							// There might be entities - parameters values
							// sources, with no
							// data in theirs rowsets, so we can't bind query
							// parameters to proper values. In the
							// such case we initialize parameters values with
							// null
							JavaScriptObject leftRowset = leftEntity.getPublished();
							if (leftRowset != null && leftRowset.<JsObject> cast().getJs("cursor") != null) {
								JavaScriptObject jsCursor = leftRowset.<JsObject> cast().getJs("cursor");
								pValue = jsCursor.<JsObject> cast().getJava(relation.getLeftField().getName());
							} else {
								pValue = null;
							}
						} else {
							Parameter leftParameter = relation.getLeftParameter();
							if (leftParameter != null) {
								pValue = leftParameter.getValue();
								if (pValue == null) {
									pValue = leftParameter.getDefaultValue();
								}
							} else {
								Logger.getLogger(Entity.class.getName()).log(
								        Level.SEVERE,
								        "Parameter of left query must present (Relation points to query parameter in entity: " + getTitle() + " [" + getEntityId()
								                + "], but query parameter is absent)");
							}
						}
						Parameter selfPm = relation.getRightParameter();
						if (selfPm != null) {
							Object selfValue = selfPm.getValue();
							if ((selfValue == null && pValue != null) || (selfValue != null && !selfValue.equals(pValue))) {
								selfPm.setValue(pValue);
							}
						}
					} else {
						Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "Relation has no left entity");
					}
				}
			}
		}
		for (int i = 1; i <= selfParameters.getFieldsCount(); i++) {
			Parameter param = selfParameters.get(i);
			if (param.isModified()) {
				parametersModified = true;
				param.setModified(false);
			}
		}
		if (parametersModified) {
			invalidate();
		}
	}

	protected void resignOnCursor() {
		if (cursorListener != null) {
			cursorListener.removeHandler();
			cursorListener = null;
		}
		JavaScriptObject jsCursor = jsPublished.<JsObject> cast().getJs("cursor");
		if (jsCursor != null) {
			cursorListener = Utils.listenPath(jsCursor, "", new Utils.OnChangeHandler() {

				@Override
				public void onChange(JavaScriptObject anEvent) {
					try {
						internalExecuteChildren(false);
					} catch (Exception ex) {
						Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			});
		}
	}

	protected void assign(Entity appTarget) throws Exception {
		appTarget.setEntityId(entityId);
		appTarget.setQueryName(queryName);
		appTarget.setTitle(title);
		appTarget.setName(name);
		appTarget.setOnRequeried(onRequeried);
	}

	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}

	public boolean isValid() {
		return valid;
	}

	public void invalidate() {
		valid = false;
	}

	public void setPublished(JavaScriptObject aPublished) {
		if (jsPublished != aPublished) {
			jsPublished = aPublished;
			if (jsPublished != null) {
				publishFacade(this, jsPublished);
				Utils.listenPath(jsPublished, "cursor", new Utils.OnChangeHandler() {

					@Override
					public void onChange(JavaScriptObject anEvent) {
						try {
							resignOnCursor();
							internalExecuteChildren(false);
						} catch (Exception ex) {
							Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				});
			}
		}
	}

	@Override
	public JavaScriptObject getPublished() {
		return jsPublished;
	}

	public JavaScriptObject getElementClass() {
		return getFields().getInstanceConstructor();
	}

	public void setElementClass(JavaScriptObject aValue) {
		getFields().setInstanceConstructor(aValue);
	}
}
