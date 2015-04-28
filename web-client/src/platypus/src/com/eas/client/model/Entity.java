package com.eas.client.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.CallbackAdapter;
import com.eas.client.Cancellable;
import com.eas.client.IDGenerator;
import com.eas.client.Utils;
import com.eas.client.Utils.JsObject;
import com.eas.client.application.Application;
import com.eas.client.form.published.HasPublished;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.queries.Query;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
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
	protected JavaScriptObject jsPublished;
	protected HandlerRegistration cursorListener;
	protected Cancellable pending;
	protected boolean valid;
	protected String title;
	protected String name;
	protected String entityId = String.valueOf((long) IDGenerator.genId());
	protected String queryName;
	protected Model model;
	protected Query query;
	protected Set<Relation> inRelations = new HashSet<Relation>();
	protected Set<Relation> outRelations = new HashSet<Relation>();

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

	private static native JavaScriptObject publishFacade(Entity aEntity)/*-{
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
		assert query != null : "Query must present (getQuery)";
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

	private static class CancellableContainer {
		public Cancellable future;
	}

	protected void refreshRowset(final Callback<JavaScriptObject, String> aCallback) throws Exception {
		if (model.process != null || aCallback != null) {
			final CancellableContainer f = new CancellableContainer();
			f.future = query.execute(new CallbackAdapter<JavaScriptObject, String>() {

				@Override
				public void doWork(JavaScriptObject aResult) throws Exception {
					if (pending == f.future) {
	                    // Apply aRowset as a snapshot. Be aware of change log!
						snapshotConsumer.<JsObject> cast().call(null, aResult);
						valid = true;
						pending = null;
						model.terminateProcess(Entity.this, null);
						if (aCallback != null) {
							aCallback.onSuccess(aResult);
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
	}

	public void validateQuery() throws Exception {
		if (query == null) {
			setQuery(Application.getAppQuery(queryName));
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

	public void refresh(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		refresh(new CallbackAdapter<JavaScriptObject, String>() {
			@Override
			protected void doWork(JavaScriptObject result) throws Exception {
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

	public void refresh(final Callback<JavaScriptObject, String> aCallback) throws Exception {
		if (model != null) {
			invalidate();
			internalExecute(aCallback);
		}
	}

	public void enqueueUpdate() throws Exception {
		model.getChangeLog().add(query.prepareCommand());
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

	public void execute(Callback<JavaScriptObject, String> aCallback) throws Exception {
		if (model != null) {
			internalExecute(aCallback);
		}
	}

	protected void internalExecute(final Callback<JavaScriptObject, String> aCallback) throws Exception {
		assert query != null : QUERY_REQUIRED;
		bindQueryParameters();
		if (isValid()) {
			if (aCallback != null) {
				aCallback.onSuccess(null);
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
							// RowsetUtils.UNDEFINED_SQL_VALUE
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
						Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "Relation had no left entity");
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
			/*
			 * final PropertyChangeListener cursorPropsListener = new
			 * PropertyChangeListener() { public void
			 * propertyChange(PropertyChangeEvent evt) { try {
			 * internalExecuteChildren(false); } catch (Exception ex) {
			 * Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null,
			 * ex); } } }; cursorListener = Utils.listen(jsCursor, "",
			 * cursorPropsListener);
			 */
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
			if (jsPublished != null)
				publishFacade(this);
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
