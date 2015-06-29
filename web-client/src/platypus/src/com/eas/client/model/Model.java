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

import com.eas.client.CallbackAdapter;
import com.eas.client.Utils;
import com.eas.client.Utils.JsObject;
import com.eas.client.application.AppClient;
import com.eas.client.form.published.HasPublished;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * @author mg
 */
public class Model implements HasPublished {

	protected AppClient client;
	protected Set<Relation> relations = new HashSet<Relation>();
	protected Set<ReferenceRelation> referenceRelations = new HashSet<ReferenceRelation>();
	protected Map<String, Entity> entities = new HashMap<String, Entity>();
	protected JsObject changeLog = JavaScriptObject.createArray().cast();
	//
	protected RequeryProcess process;
	protected JavaScriptObject jsPublished;

	public static class RequeryProcess {
		public Collection<Entity> entities;
		public Map<Entity, String> errors = new HashMap<Entity, String>();
		public Callback<JavaScriptObject, String> callback;

		public RequeryProcess(Collection<Entity> aEntities, Callback<JavaScriptObject, String> aCallback) {
			super();
			entities = aEntities;
			callback = aCallback;
			assert callback != null : "aCallback argument is required.";
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
			callback.onFailure("Canceled");
		}

		public void success() {
			callback.onSuccess(null);
		}

		public void failure() {
			callback.onFailure(assembleErrors());
		}

		public void end() {
			if (errors.isEmpty()) {
				success();
			} else {
				failure();
			}
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
	 * @param aClient
	 *            C instance all queries to be sent to.
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
			if (entity.isPending())
				return true;
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
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
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
				jsPublished.<JsObject> cast().inject(entity.getName(), publishedEntity, true, false);
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

	protected native JavaScriptObject publishEntity(Entity nEntity)/*-{
		function Insert(aEntityName){
			this.kind = 'insert';
			this.entity = aEntityName;
			this.data = {};
		}
		function Delete(aEntityName){
			this.kind = 'delete';
			this.entity = aEntityName;
			this.keys = {};
		}
		function Update(aEntityName){
			this.kind = 'update';
			this.entity = aEntityName;
			this.keys = {};
			this.data = {};
		}
		
	    function fireSelfScalarsOppositeCollectionsChanges(aSubject, aChange, nFields) {
            var expandingsOldValues = aChange.beforeState.selfScalarsOldValues;
	        var ormDefs = nFields.@com.eas.client.metadata.Fields::forEachOrmScalarExpandings(Ljava/lang/String;Lcom/eas/client/Utils$JsObject;)(aChange.propertyName, function(ormDef){
            	var ormDefName = ormDef.@com.eas.client.metadata.Fields.OrmDef::getName()(); 
                if (ormDefName) {
                	var ormDefOppositeName = ormDef.@com.eas.client.metadata.Fields.OrmDef::getOppositeName()();
                    var expandingOldValue = expandingsOldValues[ormDefName];
                    var expandingNewValue = aSubject[ormDefName];
                    @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aSubject, {source: aChange.source, propertyName: ormDefName, oldValue: expandingOldValue, newValue: expandingNewValue});
                    if (ormDefOppositeName) {
                        if (expandingOldValue) {
                            @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(expandingOldValue, {source: expandingOldValue, propertyName: ormDefOppositeName});
                        }
                        if (expandingNewValue) {
                            @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(expandingNewValue, {source: expandingNewValue, propertyName: ormDefOppositeName});
                        }
                    }
                }
	        });
	    }
	
	    function prepareSelfScalarsChanges(aSubject, aChange, nFields) {
	        var oldScalarValues = [];
	    	nFields.@com.eas.client.metadata.Fields::forEachOrmScalarExpandings(Ljava/lang/String;Lcom/eas/client/Utils$JsObject;)(aChange.propertyName, function(ormDef){
            	var ormDefName = ormDef.@com.eas.client.metadata.Fields.OrmDef::getName()(); 
                if (ormDef && ormDefName) {
                    oldScalarValues[ormDefName] = aSubject[ormDefName];
                }
	    	});
	        return oldScalarValues;
	    }
	
	    function fireOppositeScalarsSelfCollectionsChanges(aSubject, aChange, nFields) {
	        var oppositeScalarsFirerers = aChange.beforeState.oppositeScalarsFirerers;
	        if (oppositeScalarsFirerers) {
	            oppositeScalarsFirerers.forEach(function (aFirerer) {
	                aFirerer();
	            });
	        }
	        var collectionsDefs = nFields.@com.eas.client.metadata.Fields::getOrmCollectionsDefinitions()().@java.util.Map::entrySet()();
	        if (collectionsDefs) {
	        	var collectionsDefsIt = collectionsDefs.@java.util.Set::iterator()();
	        	while(collectionsDefsIt.@java.util.Iterator::hasNext()()){
	        		var aEntry = collectionsDefsIt.@java.util.Iterator::next()(); 
	                var collectionName = aEntry.@java.util.Map.Entry::getKey()();
	                var ormDef = aEntry.@java.util.Map.Entry::getValue()();
	                var collection = aSubject[collectionName];
	                collection.forEach(function (item) {
	                    @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(item, {source: item, propertyName: ormDef.@com.eas.client.metadata.Fields.OrmDef::getOppositeName()()});
	                });
	            };
	            collectionsDefsIt = collectionsDefs.@java.util.Set::iterator()();
	            while(collectionsDefsIt.@java.util.Iterator::hasNext()()) {
	            	aEntry = collectionsDefsIt.@java.util.Iterator::next()();
	                var collectionName = aEntry.@java.util.Map.Entry::getKey()();
	                @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aSubject, {source: aSubject, propertyName: collectionName});
	            };
	        }
	    }
	
	    function prepareOppositeScalarsChanges(aSubject, nFields) {
	        var firerers = [];
	        var collectionsDefs = nFields.@com.eas.client.metadata.Fields::getOrmCollectionsDefinitions()().@java.util.Map::entrySet()();
        	var collectionsDefsIt = collectionsDefs.@java.util.Set::iterator()();
        	while(collectionsDefsIt.@java.util.Iterator::hasNext()()){
        		var aEntry = collectionsDefsIt.@java.util.Iterator::next()(); 
	            var collectionName = aEntry.@java.util.Map.Entry::getKey()();
	            var ormDef = aEntry.@java.util.Map.Entry::getValue()();
	            var collection = aSubject[collectionName];
	            collection.forEach(function (item) {
	               	var ormDefOppositeName = ormDef.@com.eas.client.metadata.Fields.OrmDef::getOppositeName()();
	                if (ormDefOppositeName) {
	                    firerers.push(function () {
	                        @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(item, {source: item, propertyName: ormDefOppositeName});
	                    });
	                }
	            });
	        };
	        return firerers;
	    }
	
	    function fireOppositeScalarsChanges(aSubject, nFields) {
	        var collected = prepareOppositeScalarsChanges(aSubject, nFields);
	        collected.forEach(function (aFirerer) {
	            aFirerer();
	        });
	    }
	
	    function fireOppositeCollectionsChanges(aSubject, nFields) {
	        var scalarsDefs = nFields.@com.eas.client.metadata.Fields::getOrmScalarDefinitions()().@java.util.Map::entrySet()();
        	var scalarsDefsIt = scalarsDefs.@java.util.Set::iterator()();
        	while(scalarsDefsIt.@java.util.Iterator::hasNext()()){
        		var aEntry = scalarsDefsIt.@java.util.Iterator::next()(); 
	            var scalarName = aEntry.@java.util.Map.Entry::getKey()();
	            if (scalarName) {
	                var ormDef = aEntry.@java.util.Map.Entry::getValue()();
	                var scalar = aSubject[scalarName];
	               	var ormDefOppositeName = ormDef.@com.eas.client.metadata.Fields.OrmDef::getOppositeName()();
	                if (scalar && ormDefOppositeName) {
	                    @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(scalar, {source: scalar, propertyName: ormDefOppositeName});
	                }
	            }
	        };
	    }
	
	    function generateChangeLogKeys(nKeys, nFields, propName, aSubject, oldValue) {
	        if (nFields) {
	            for (var i = 1; i <= nFields.@com.eas.client.metadata.Fields::getFieldsCount()(); i++) {
	                var nField = nFields.@com.eas.client.metadata.Fields::get(I)(i);
	                if (nField.@com.eas.client.metadata.Field::isPk()()) {
	                    var fieldName = nField.@com.eas.client.metadata.Field::getName()();
	                    var value = aSubject[fieldName];
	                    // Some tricky processing of primary keys modification case ...
	                    if (fieldName == propName) {
	                        value = oldValue;
	                    }
	                    nKeys[fieldName] = value;
	                }
	            }
	        }
	    }

        var justInserted = null;
        var justInsertedChange = null;
        var orderers = {};
        var published = [];

		var changeLog = nEntity.@com.eas.client.model.Entity::getChangeLog()();
		
        function managedOnChange(aSubject, aChange) {
            var nField = noFields[aChange.propertyName];
            if (!tryToComplementInsert(aSubject, aChange)) {
                var updateChange = new Update(nEntity.@com.eas.client.model.Entity::getQueryName()());
                generateChangeLogKeys(updateChange.keys, nFields, aChange.propertyName, aSubject, aChange.oldValue);
                updateChange.data[aChange.propertyName] = aChange.newValue;
                changeLog.push(updateChange);
            }
            Object.keys(orderers).forEach(function (aOrdererKey) {
                var aOrderer = orderers[aOrdererKey];
                if (aOrderer.inKeys(aChange.propertyName)) {
                    aOrderer.add(aChange.source);
                }
            });
            @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aSubject, aChange);
            fireSelfScalarsOppositeCollectionsChanges(aSubject, aChange, nFields);// Expanding change
            if (nField && nField.@com.eas.client.metadata.Field::isPk()()) {
                fireOppositeScalarsSelfCollectionsChanges(aSubject, aChange, nFields);
            }
        }
        function managedBeforeChange(aSubject, aChange) {
            var oldScalars = prepareSelfScalarsChanges(aSubject, aChange, nFields);
            var oppositeScalarsFirerers = prepareOppositeScalarsChanges(aSubject, nFields);
            Object.keys(orderers).forEach(function (aOrdererKey) {
                var aOrderer = orderers[aOrdererKey];
                if (aOrderer.inKeys(aChange.propertyName)) {
                    aOrderer['delete'](aChange.source);
                }
            });
            return {selfScalarsOldValues: oldScalars, oppositeScalarsFirerers: oppositeScalarsFirerers};
        }
        function tryToComplementInsert(aSubject, aChange) {
            var complemented = false;
            var nField = noFields[aChange.propertyName];
            if (aSubject === justInserted && !nField.@com.eas.client.metadata.Field::isNullable()()) {
                var met = false;
                var iData = justInsertedChange.data;
                for (var d in iData) {
                    var iv = iData[d];
                    if (d == aChange.propertyName) {
                        met = true;
                        break;
                    }
                }
                if (!met) {
                    iData[aChange.propertyName] = aChange.newValue;
                    complemented = true;
                }
            }
            return complemented;
        }
        function acceptInstance(aSubject) {
            Object.keys(noFields).forEach(function(aFieldName){
                if(typeof aSubject[aFieldName] === 'undefined')
                    aSubject[aFieldName] = null;
            });
            $wnd.P.manageObject(aSubject, managedOnChange, managedBeforeChange);
            @com.eas.client.Utils::listenable(Lcom/google/gwt/core/client/JavaScriptObject;)(aSubject);
            // ORM mutable scalar and collection properties
            var define = function (aOrmDefs) {
            	var aOrmDefsIt = aOrmDefs.@java.util.Map::entrySet()().@java.util.Set::iterator()();
                while(aOrmDefsIt.@java.util.Iterator::hasNext()()) {
                	var defsEntry = aOrmDefsIt.@java.util.Iterator::next()();
                    var ormDef = defsEntry.@java.util.Map.Entry::getValue()();
                    var jsDef = ormDef.@com.eas.client.metadata.Fields.OrmDef::getJsDef()();
                    Object.defineProperty(aSubject, defsEntry.@java.util.Map.Entry::getKey()(), jsDef);
                }
            };
            define(nFields.@com.eas.client.metadata.Fields::getOrmScalarDefinitions()());
            define(nFields.@com.eas.client.metadata.Fields::getOrmCollectionsDefinitions()());
        }

        var _onInserted = null;
        var _onDeleted = null;
        var _onScrolled = null;
        $wnd.P.manageArray(published, {
            spliced: function (added, deleted) {
                added.forEach(function (aAdded) {
                    justInserted = aAdded;
                    justInsertedChange = new Insert(nEntity.@com.eas.client.model.Entity::getQueryName()());
                    for (var nf = 0; nf < nnFields.@java.util.List::size()(); nf++) {
                        var nField = nnFields.@java.util.List::get(I)(nf);
                        var nFieldName = nField.@com.eas.client.metadata.Field::getName()();
                        if (!aAdded[nFieldName] && nField.@com.eas.client.metadata.Field::isPk()()) {
                        	var nTypeInfo = nField.@com.eas.client.metadata.Field::getTypeInfo()();
                            aAdded[nFieldName] = $wnd.P.boxAsJs(nTypeInfo.@com.eas.client.metadata.DataTypeInfo::generateValue()());
                        }
                    }
                    for (var na in aAdded) {
                        var nField = noFields[na];
                        if (nField) {
                            var v = aAdded[na];
                            justInsertedChange.data[na] = v;
                        }
                    }
                    changeLog.push(justInsertedChange);
                    for (var aOrdererKey in orderers) {
                        var aOrderer = orderers[aOrdererKey];
                        aOrderer.add(aAdded);
                    }
                    acceptInstance(aAdded);
                    fireOppositeScalarsChanges(aAdded, nFields);
                    fireOppositeCollectionsChanges(aAdded, nFields);
                });
                deleted.forEach(function (aDeleted) {
                    if (aDeleted === justInserted) {
                        justInserted = null;
                        justInsertedChange = null;
                    }
                    var deleteChange = new Delete(nEntity.@com.eas.client.model.Entity::getQueryName()());
                    generateChangeLogKeys(deleteChange.keys, nFields, null, aDeleted, null);
                    changeLog.push(deleteChange);
                    for (var aOrdererKey in orderers) {
                        var aOrderer = orderers[aOrdererKey];
                        aOrderer['delete'](aDeleted);
                    }
                    fireOppositeScalarsChanges(aDeleted, nFields);
                    fireOppositeCollectionsChanges(aDeleted, nFields);
                    @com.eas.client.Utils::unlistenable(Lcom/google/gwt/core/client/JavaScriptObject;)(aDeleted);
                    $wnd.P.unmanageObject(aDeleted);
                });
                if (_onInserted && added.length > 0) {
                    try {
                        _onInserted({source: published, items: added});
                    } catch (e) {
                        Logger.severe(e);
                    }
                }
                if (_onDeleted && deleted.length > 0) {
                    try {
                        _onDeleted({source: published, items: deleted});
                    } catch (e) {
                        Logger.severe(e);
                    }
                }
            	@com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(published, {source: published, propertyName: 'length'});
            },
            scrolled: function (aSubject, oldCursor, newCursor) {
                if (_onScrolled) {
                    try {
                        _onScrolled({source: published, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
                    } catch (e) {
                        Logger.severe(e);
                    }
                }
                @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(published, {source: published, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
            }
        });
        var pSchema = {};
        Object.defineProperty(published, "schema", {
            value: pSchema
        });
        var pkFieldName = '';
        var nFields = nEntity.@com.eas.client.model.Entity::getFields()();
        var nnFields = nFields.@com.eas.client.metadata.Fields::toCollection()();
        var noFields = {};
        // schema
        for (var n = 0; n < nnFields.@java.util.List::size()(); n++) {
            (function () {
                var nField = nnFields.@java.util.List::get(I)(n);
                var nFieldName = nField.@com.eas.client.metadata.Field::getName()();
                noFields[nFieldName] = nField;
                if (nField.@com.eas.client.metadata.Field::isPk()())
                    pkFieldName = nFieldName;
                    
				var pField = @com.eas.client.metadata.Field::publishFacade(Lcom/eas/client/metadata/Field;)(nField);
                var schemaDesc = {
                    value: pField
                };
                if (!pSchema[nFieldName]) {
                    Object.defineProperty(pSchema, nFieldName, schemaDesc);
                } else {                	
                    var eTitle = nEntity.@com.eas.client.model.Entity::getTitle()() ? " [" + nEntity.@com.eas.client.model.Entity::getTitle()() + "]" : "";
                    throw "Duplicated field name found: " + nFieldName + " in entity " + nEntity.@com.eas.client.model.Entity::getName()() + eTitle;
                }
                Object.defineProperty(pSchema, n, schemaDesc);
            })();
        }
        // entity.params.p1 syntax
        var nParameters = nEntity.@com.eas.client.model.Entity::getQuery()().@com.eas.client.queries.Query::getParameters()();
        var ncParameters = nParameters.@com.eas.client.metadata.Parameters::toCollection()();
        var pParams = {};
        for (var p = 0; p < ncParameters.@java.util.List::size()(); p++) {
            (function () {
                var nParameter = ncParameters.@java.util.List::get(I)(p);
                var pDesc = {
                    get: function () {
                        return $wnd.P.boxAsJs(nParameter.@com.eas.client.metadata.Parameter::getJsValue()());
                    },
                    set: function (aValue) {
                        nParameter.@com.eas.client.metadata.Parameter::setJsValue(Ljava/lang/Object;)($wnd.P.boxAsJava(aValue));
                    }
                };
                Object.defineProperty(pParams, nParameter.@com.eas.client.metadata.Parameter::getName()(), pDesc);
                Object.defineProperty(pParams, p, pDesc);
            })();
        }
        Object.defineProperty(published, 'params', {value: pParams});
        // entity.params.schema.p1 syntax
        var pParamsSchema = @com.eas.client.metadata.Fields::publishFacade(Lcom/eas/client/metadata/Fields;)(nParameters);
        if (!pParams.schema)
            Object.defineProperty(pParams, 'schema', {value: pParamsSchema});
        Object.defineProperty(published, 'find', {value: function (aCriteria) {
                var keys = Object.keys(aCriteria);
                keys = keys.sort();
                var ordererKey = keys.join(' | ');
                var orderer = orderers[ordererKey];
                if (!orderer) {
                    orderer = new $wnd.P.Orderer(keys);
                    published.forEach(function (item) {
                        orderer.add(item);
                    });
                    orderers[ordererKey] = orderer;
                }
                var found = orderer.find(aCriteria);
                return found;
            }});
        Object.defineProperty(published, 'findByKey', {value: function (aKeyValue) {
                var criteria = {};
                criteria[pkFieldName] = aKeyValue;
                var found = published.find(criteria);
                return found.length > 0 ? found[0] : null;
            }});
        Object.defineProperty(published, 'findById', {value: function (aKeyValue) {
                $wnd.P.Logger.warning('findById() is deprecated. Use findByKey() instead.');
                return published.findByKey(aKeyValue);
            }});
        var toBeDeletedMark = '-platypus-to-be-deleted-mark';
        Object.defineProperty(published, 'remove', {value: function (toBeDeleted) {
                toBeDeleted = toBeDeleted.forEach ? toBeDeleted : [toBeDeleted];
                toBeDeleted.forEach(function (anInstance) {
                    anInstance[toBeDeletedMark] = true;
                });
                for (var d = published.length - 1; d >= 0; d--) {
                    if (published[d][toBeDeletedMark]) {
                        published.splice(d, 1);
                    }
                }
                toBeDeleted.forEach(function (anInstance) {
                    delete anInstance[toBeDeletedMark];
                });
            }});
        Object.defineProperty(published, 'onScrolled', {
            get: function () {
                return _onScrolled;
            },
            set: function (aValue) {
                _onScrolled = aValue;
            }
        });
        Object.defineProperty(published, 'onInserted', {
            get: function () {
                return _onInserted;
            },
            set: function (aValue) {
                _onInserted = aValue;
            }
        });
        Object.defineProperty(published, 'onDeleted', {
            get: function () {
                return _onDeleted;
            },
            set: function (aValue) {
                _onDeleted = aValue;
            }
        });
        nEntity.@com.eas.client.model.Entity::setSnapshotConsumer(Lcom/google/gwt/core/client/JavaScriptObject;)(function (aSnapshot, aFreshData) {
            if(aFreshData){
            	Array.prototype.splice.call(published, 0, published.length);
            }
            var instanceCtor = nEntity.@com.eas.client.model.Entity::getElementClass()();
            for (var s = 0; s < aSnapshot.length; s++) {
                var snapshotInstance = aSnapshot[s];
                var accepted;
	            if (instanceCtor) {
                    accepted = new instanceCtor();
	            } else {
	            	accepted = {};
	            }
                for (var sp in snapshotInstance) {
                    accepted[sp] = snapshotInstance[sp];
                }
                Array.prototype.push.call(published, accepted);
                acceptInstance(accepted);
            }
            orderers = {};
            published.cursor = published.length > 0 ? published[0] : null;
            @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(published, {source: published, propertyName: 'length'});
            published.forEach(function(aItem){
	            fireOppositeScalarsChanges(aItem, nFields);
	            fireOppositeCollectionsChanges(aItem, nFields);
            });
        });
        nEntity.@com.eas.client.model.Entity::setSnapshotProducer(Lcom/google/gwt/core/client/JavaScriptObject;)(function(){
            var snapshot = [];
            var snapshotFields = Object.keys(noFields);
            published.forEach(function (aItem) {
                var cloned = {};
                snapshotFields.forEach(function (aFieldName) {
                    var typeOfField = typeof aItem[aFieldName];
                    if (typeOfField === 'undefined' || typeOfField === 'function')
                        cloned[aFieldName] = null;
                    else
                        cloned[aFieldName] = aItem[aFieldName];
                });
                snapshot.push(cloned);
            });
            return snapshot;
        });
        @com.eas.client.Utils::listenable(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
        return published;
	}-*/; 
	
	private static DefinitionsContainer ormPropertiesDefiner = DefinitionsContainer.init();

	private static final class DefinitionsContainer extends JavaScriptObject {

		protected DefinitionsContainer() {
		}

		public native static DefinitionsContainer init()/*-{
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
	                    $wnd.P.manageArray(found, {
	                        spliced: function (added, deleted) {
	                            added.forEach(function (item) {
	                                item[sourceFieldName] = targetKey;
	                            });
	                            deleted.forEach(function (item) {
	                                item[sourceFieldName] = null;
	                            });
	                            @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(found, {source: found, propertyName: 'length'});
	                        },
	                        scrolled: function (aSubject, oldCursor, newCursor) {
	                            @com.eas.client.Utils::fire(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(found, {source: found, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
	                        }
	                    });
	                    @com.eas.client.Utils::listenable(Lcom/google/gwt/core/client/JavaScriptObject;)(found);
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
		var publishedModel = aTarget;
		Object.defineProperty(publishedModel, "createQuery", {
			get : function() {
				return function(aQueryId) {
					$wnd.P.Logger.warning("createQuery deprecated call detected. Use loadEntity() instead.");
					return aModel.@com.eas.client.model.Model::jsLoadEntity(Ljava/lang/String;)(aQueryId);
				}
			}
		});
		Object.defineProperty(publishedModel, "loadEntity", {
			get : function() {
				return function(aQueryId) {
					return aModel.@com.eas.client.model.Model::jsLoadEntity(Ljava/lang/String;)(aQueryId);
				}
			}
		});
		Object.defineProperty(publishedModel, "save", {
			get : function() {
				return function(onScuccess, onFailure) {
					aModel.@com.eas.client.model.Model::save(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onScuccess, onFailure);
				}
			}
		});
		Object.defineProperty(publishedModel, "revert", {
			get : function() {
				return function() {
					aModel.@com.eas.client.model.Model::revert()();
				}
			}
		});
		Object.defineProperty(publishedModel, "requery", {
			get : function() {
				return function(onSuccess, onFailure) {
					aModel.@com.eas.client.model.Model::requery(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
				}
			}
		});
		Object.defineProperty(publishedModel, "execute", {
			get : function() {
				return function(onSuccess, onFailure) {
					aModel.@com.eas.client.model.Model::execute(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
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
				return aModel.@com.eas.client.model.Model::isModified()();
			}
		});
		Object.defineProperty(publishedModel, "pending", {
			get : function() {
				return aModel.@com.eas.client.model.Model::isPending()();
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
			if (!entity.getQuery().isManual()) {
                if (process == null) {
                    entity.internalExecute(new CallbackAdapter<JavaScriptObject, String>() {
            			@Override
            			protected void doWork(JavaScriptObject result) throws Exception {
            				// no op
            			}

            			@Override
            			public void onFailure(String reason) {
                            Logger.getLogger(Model.class.getName()).log(Level.WARNING, reason);
            			}

                    });
                } else {
    				entity.internalExecute(null);
                }
			}
		}
	}

	private Set<Entity> rootEntities() {
		final Set<Entity> rootEntities = new HashSet<>();
		for (Entity entity : entities.values()) {
			if (entity.getInRelations().isEmpty())
				rootEntities.add(entity);
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
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			// Scheduling is needed because of asynchronous nature of Object.observe's callback calling process.
			@Override
			public void execute() {
				try {
					client.requestCommit(changeLog, new CallbackAdapter<Void, String>() {

						@Override
						protected void doWork(Void aVoid) throws Exception {
							commited();
							if (onSuccess != null)
								Utils.invokeJsFunction(onSuccess);
						}

						@Override
						public void onFailure(String aReason) {
							try {
								rolledback();
								if (onFailure != null)
									Utils.executeScriptEventVoid(jsPublished, onFailure, aReason);
							} catch (Exception ex) {
								Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
							}
						}

					});
				} catch (Exception ex) {
					Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		});
	}

	public void revert() throws Exception {
		changeLog.splice(0, changeLog.length());
		for(Entity e : entities.values()){
            e.applyLastSnapshot();
		}
	}

	public void commited() throws Exception {
		changeLog.splice(0, changeLog.length());
		for(Entity e : entities.values()){
            e.takeSnapshot();
		}
	}

	public void rolledback() throws Exception {
		Logger.getLogger(Model.class.getName()).log(Level.SEVERE, "rolled back");
	}

	public void requery(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		requery(new CallbackAdapter<JavaScriptObject, String>() {
			@Override
			protected void doWork(JavaScriptObject aRowset) throws Exception {
				if (onSuccess != null)
					Utils.invokeJsFunction(onSuccess);
			}

			@Override
			public void onFailure(String reason) {
				if (onFailure != null) {
					try {
						Utils.executeScriptEventVoid(jsPublished, onFailure, reason);
					} catch (Exception ex) {
						Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		});
	}

	public void requery(Callback<JavaScriptObject, String> aCallback) throws Exception {
		changeLog.splice(0, changeLog.length());
		if (process != null) {
			process.cancel();
		}
		if (aCallback != null) {
			process = new RequeryProcess(entities.values(), aCallback);
		}
		revert();
		executeEntities(true, rootEntities());
		if (!isPending() && process != null) {
			process.end();
			process = null;
		}
	}

	public void execute(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		execute(new CallbackAdapter<JavaScriptObject, String>() {
			@Override
			protected void doWork(JavaScriptObject aRowset) throws Exception {
				if (onSuccess != null)
					Utils.invokeJsFunction(onSuccess);
			}

			@Override
			public void onFailure(String reason) {
				if (onFailure != null) {
					try {
						Utils.executeScriptEventVoid(jsPublished, onFailure, reason);
					} catch (Exception ex) {
						Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		});
	}

	public void execute(Callback<JavaScriptObject, String> aCallback) throws Exception {
		if (process != null) {
			process.cancel();
		}
		if (aCallback != null) {
			process = new RequeryProcess(entities.values(), aCallback);
		}
		executeEntities(false, rootEntities());
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
