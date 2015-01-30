package com.eas.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.CallbackAdapter;
import com.bearsoft.rowset.Cancellable;
import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.Utils.JsObject;
import com.bearsoft.rowset.beans.PropertyChangeEvent;
import com.bearsoft.rowset.beans.PropertyChangeListener;
import com.bearsoft.rowset.beans.PropertyChangeSupport;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.events.RowsetNetErrorEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.InvalidFieldsExceptionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.ordering.Filter;
import com.bearsoft.rowset.ordering.Locator;
import com.bearsoft.rowset.ordering.Orderer;
import com.bearsoft.rowset.ordering.Subset;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.bearsoft.rowset.utils.IDGenerator;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.application.Application;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.HasPublished;
import com.eas.client.queries.Query;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 
 * @author mg
 */
public class Entity implements RowsetListener, HasPublished{

    protected static final String PENDING_ASSUMPTION_FAILED_MSG = "pending assigned to null without pending.cancel() call.";
	public static final String QUERY_REQUIRED = "All model entities must have a query";
	protected JavaScriptObject willScroll;
	protected JavaScriptObject onScrolled;
	protected JavaScriptObject willInsert;
	protected JavaScriptObject onInserted;
	protected JavaScriptObject willDelete;
	protected JavaScriptObject onDeleted;
	protected JavaScriptObject onRequeried;
	protected JavaScriptObject onFiltered;
	protected JavaScriptObject jsPublished;
	// runtime
	protected Rowset rowset;
	protected Filter filter;
	protected HandlerRegistration cursorListener;
	protected List<Integer> filterConstraints = new ArrayList<Integer>();
	// to preserve relation order
	protected List<Relation> rtInFilterRelations;
    protected Locator locator;
	//
	protected Cancellable pending;
	protected boolean valid;
    protected Map<List<Integer>, Orderer> userOrderers = new HashMap<List<Integer>, Orderer>();
	protected String title;
	protected String name;
	protected String entityId = String.valueOf((long) IDGenerator.genId());
	protected String queryName;
	protected Model model;
	protected Query query;
	protected Set<Relation> inRelations = new HashSet<Relation>();
	protected Set<Relation> outRelations = new HashSet<Relation>();
	protected PropertyChangeSupport changeSupport;

	public Entity() {
		super();
		changeSupport = new PropertyChangeSupport(this);
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
            Map<String, Fields.OrmDef> defs = rowset.getFields().getOrmScalarDefinitions();
            if (!defs.containsKey(aName)) {
                rowset.getFields().putOrmScalarDefinition(aName, aDefinition);
            } else {
                Logger.getLogger(Entity.class.getName()).log(Level.FINE, "ORM property "+aName+" redefinition attempt on entity "+name != null && !name.isEmpty() ? name : ""+" "+title != null && !title.isEmpty() ? "[" + title + "]" : ""+".");
            }
        }
    }

    public Map<String, Fields.OrmDef> getOrmScalarDefinitions() {
        return rowset.getFields().getOrmScalarDefinitions();
    }

    public void putOrmCollectionDefinition(String aName, Fields.OrmDef aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            Map<String, Fields.OrmDef> defs = rowset.getFields().getOrmCollectionsDefinitions();
            if (!defs.containsKey(aName)) {
                rowset.getFields().putOrmCollectionDefinition(aName, aDefinition);
            } else {
                Logger.getLogger(Entity.class.getName()).log(Level.FINE, "ORM property "+aName+" redefinition attempt on entity "+name != null && !name.isEmpty() ? name : ""+" "+title != null && !title.isEmpty() ? "[" + title + "]" : ""+".");
            }
        }
    }

    public Map<String, Fields.OrmDef> getOrmCollectionsDefinitions() {
        return rowset.getFields().getOrmCollectionsDefinitions();
    }

	private static native JavaScriptObject publishFacade(Entity aEntity)/*-{

        function copyProps(aObject) {
            var shadow = {};
            for (var pn in aObject) {
                var pName = pn + '';
                shadow[pName] = aObject[pName];
            }
            return shadow;
        }
        function applyProps(aShadow, aTarget) {
            for (var pn in aShadow) {
                var pName = pn + '';
                aTarget[pName] = aShadow[pName];
            }
        }
		var rowset = aEntity.@com.eas.client.model.Entity::getRowset()();
		var nFields = aEntity.@com.eas.client.model.Entity::getFields()();
		
		var published = aEntity.@com.eas.client.model.Entity::getPublished()();
		// array interface
		@com.bearsoft.rowset.Rowset::addRowsetContentJsListener(Lcom/bearsoft/rowset/Rowset;Lcom/google/gwt/core/client/JavaScriptObject;)(rowset, function(){
            var eventedRows = [];
            Array.prototype.splice.call(published, 0, published.length);
            var rLength = rowset.@com.bearsoft.rowset.Rowset::size()();
            for (var aCursorPos = 1; aCursorPos <= rLength; aCursorPos++) {
            	var nRow = rowset.@com.bearsoft.rowset.Rowset::getRow(I)(aCursorPos);    
            	var rowFacade = @com.bearsoft.rowset.Row::publishFacade(Lcom/bearsoft/rowset/Row;Lcom/google/gwt/core/client/JavaScriptObject;)(nRow, null);
                Array.prototype.push.call(published, rowFacade);
                eventedRows.push(nRow);
            }
            eventedRows.forEach(function (nEventedRow) {
            	nEventedRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeCollections()();
            	nEventedRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeScalars()();
            });
		});
        Object.defineProperty(published, "fill", {
            value: function () {
                throw '\'fill\' is unsupported in BoundArray because of it\'s distinct values requirement';
            }
        });
        Object.defineProperty(published, "pop", {
            value: function () {
                if (published.length > 0) {
            		var nRow = rowset.@com.bearsoft.rowset.Rowset::getRow(I)(published.length);
                    rowset.@com.bearsoft.rowset.Rowset::deleteAt(I)(published.length);
                    var res = Array.prototype.pop.call(published);
	            	nRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeCollections()();
	            	nRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeScalars()();
                    return res;
                }
            }
        });
        Object.defineProperty(published, "push", {
            value: function () {
                var eventedRows = [];
                var startSize = rowset.@com.bearsoft.rowset.Rowset::size()();
                for (var a = 0; a < arguments.length; a++) {
                    var shadow = copyProps(arguments[a]);// to avoid re initing by injected structure without values
                	var insertedRow = aEntity.@com.eas.client.model.Entity::jsInsertAt(IZLcom/google/gwt/core/client/JavaScriptObject;)(startSize + 1 + a, a < arguments.length - 1, arguments[a]);
                    applyProps(shadow, arguments[a]);
                    eventedRows.push(insertedRow);
                }
                var res = Array.prototype.push.apply(published, arguments);
                eventedRows.forEach(function (aEventedRow) {
                    aEventedRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeCollections();
                    aEventedRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeScalars();
                });
                return res;
            }
        });
        Object.defineProperty(published, "reverse", {
            value: function () {
                rowset.@com.bearsoft.rowset.Rowset::reverse()();
            }
        });
        Object.defineProperty(published, "shift", {
            value: function () {
                if (published.length > 0) {
            		var nRow = rowset.@com.bearsoft.rowset.Rowset::getRow(I)(1);
                    rowset.@com.bearsoft.rowset.Rowset::deleteAt(I)(1);
                    var res = Array.prototype.shift.call(published);
	            	nRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeCollections()();
	            	nRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeScalars()();
                    return res;
                }
            }
        });
        var defaultCompareFunction = function (o1, o2) {
            var s1 = (o1 + '');
            var s2 = (o2 + '');
            return s1 > s2 ? 1 : s1 < s2 ? -1 : 0;
        };
        Object.defineProperty(published, "sort", {
            value: function () {
                if (arguments.length <= 0 || !aEntity.@com.eas.client.model.Entity::jsSort(Ljava/lang/Object;)(arguments[0])) {
                    var compareFunc = defaultCompareFunction;
                    if (arguments.length > 0 && typeof arguments[0] === 'function') {
                        compareFunc = arguments[0];
                    }
                    Array.prototype.sort.call(published, compareFunc);
                }
                return published;
            }
        });
        Object.defineProperty(published, "splice", {
            value: function () {
               	var eventedRows = [];
                if (arguments.length > 0) {
                    var beginToDeleteAt = arguments[0];
                    var howManyToDelete = published.length;
                    if (arguments.length > 1) {
                        howManyToDelete = arguments[1];
                    }
                    var needToAdd = arguments.length > 2;
                    var deleted = 0;
                    while (!rowset.@com.bearsoft.rowset.Rowset::isEmpty()() && deleted++ < howManyToDelete) {
                    	var deletedRow = rowset.@com.bearsoft.rowset.Rowset::getRow(I)(beginToDeleteAt + 1);
                        rowset.@com.bearsoft.rowset.Rowset::deleteAt(IZ)(beginToDeleteAt + 1, needToAdd);
                        eventedRows.push(deletedRow);
                    }
                    var insertAt = beginToDeleteAt;
                    for (var a = 2; a < arguments.length; a++) {
                    	var shadow = copyProps(arguments[a]);// to avoid re initing by injected structure without values
                        var insertedRow = aEntity.@com.eas.client.model.Entity::jsInsertAt(IZLcom/google/gwt/core/client/JavaScriptObject;)(insertAt + 1, a < arguments.length - 1, arguments[a]);
                        applyProps(shadow, arguments[a]);
                        eventedRows.push(insertedRow);
                        insertAt++;
                    }
                }
                var res = Array.prototype.splice.apply(published, arguments);
                eventedRows.forEach(function (aEventedRow) {
                    aEventedRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeCollections();
                    aEventedRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeScalars();
                });
                return res;
            }
        });

        Object.defineProperty(published, "unshift", {
            value: function () {
                var eventedRows = [];
                for (var a = 0; a < arguments.length; a++) {
                  	var shadow = copyProps(arguments[a]);// to avoid re initing by injected structure without values
                    var insertedRow = aEntity.@com.eas.client.model.Entity::jsInsertAt(IZLcom/google/gwt/core/client/JavaScriptObject;)(a + 1, a < arguments.length - 1, arguments[a]);
                    applyProps(shadow, arguments[a]);
                    eventedRows.push(insertedRow);
                }
                var res = Array.prototype.unshift.apply(published, arguments);
                eventedRows.forEach(function (aEventedRow) {
                    aEventedRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeCollections();
                    aEventedRow.@com.bearsoft.rowset.Row::fireChangesOfOppositeScalars();
                });
                return res;
            }
        });		
		// cursor interface 
		Object.defineProperty(published, "scrollTo", {
			value : function(aRowWrapper) {
				return aRowWrapper && aRowWrapper.unwrap ? aEntity.@com.eas.client.model.Entity::scrollTo(Lcom/bearsoft/rowset/Row;)(aRowWrapper.unwrap()) : false;
			}
		});
		// find interface
		Object.defineProperty(published, "find", {
			value : function(aCriteria) {
				return aEntity.@com.eas.client.model.Entity::jsFind(Lcom/google/gwt/core/client/JavaScriptObject;)(aCriteria);
			}
		});
		Object.defineProperty(published, "findByKey", {
			value : function(aValue) {
				return aEntity.@com.eas.client.model.Entity::jsFindById(Ljava/lang/Object;)($wnd.P.boxAsJava(aValue));
			}
		});
		published.findById = function(){
			$wnd.P.Logger.warning("Deprecated \"findById\" call detected. Please, use findByKey instead.");
			return published.findByKey.apply(published, arguments);
		};
		//
		Object.defineProperty(published, "enqueueUpdate", {
			value : function() {
				aEntity.@com.eas.client.model.Entity::enqueueUpdate()();
			}
		});
		Object.defineProperty(published, "execute", {
			value : function(onSuccess, onFailure) {
				aEntity.@com.eas.client.model.Entity::execute(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
			}
		});
		Object.defineProperty(published, "requery", {
			value : function(onSuccess, onFailure) {
				aEntity.@com.eas.client.model.Entity::refresh(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
			}
		});
		Object.defineProperty(published, "createFilter", {
			value : function(aConstraints) {
				return aEntity.@com.eas.client.model.Entity::jsCreateFilter(Lcom/google/gwt/core/client/JavaScriptObject;)(aConstraints);
			}
		});
		Object.defineProperty(published, "activeFilter", {
			get : function() {
				var nFilter = rowset.@com.bearsoft.rowset.Rowset::getActiveFilter()();
				return nFilter != null ? @com.bearsoft.rowset.ordering.Filter::publishFacade(Lcom/bearsoft/rowset/ordering/Filter;)(nFilter) : null;
			}
		});
		
		Object.defineProperty(published, "createSorting", {
			value : function(aCriteria) {
				return aEntity.@com.eas.client.model.Entity::jsCreateSorting(Lcom/google/gwt/core/client/JavaScriptObject;)(aCriteria);
			}
		});
		Object.defineProperty(published, "removeAll", {
			value : function() {
				rowset.@com.bearsoft.rowset.Rowset::deleteAll()();
			}
		});
		Object.defineProperty(published, "remove", {
			value : function(aRow) {
				if(aRow){
					if(aRow.unwrap){
						rowset.@com.bearsoft.rowset.Rowset::delete(Lcom/bearsoft/rowset/Row;)(aRow.unwrap());
					}else{
						rowset.@com.bearsoft.rowset.Rowset::deleteAt(I)(aRow);
					}
				}
			}
		});
		Object.defineProperty(published, "unwrap", {
			value : function() {
				return aEntity;
			}
		});
		// properties
		Object.defineProperty(published, "cursorPos",    { get : function(){ return rowset.@com.bearsoft.rowset.Rowset::getCursorPos()();}, set : function(aValue){ rowset.@com.bearsoft.rowset.Rowset::setCursorPos(I)(+aValue);}});
		Object.defineProperty(published, "elementClass", { get : function(){ return aEntity.@com.eas.client.model.Entity::getElementClass()()}, set : function(aValue){ aEntity.@com.eas.client.model.Entity::setElementClass(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue)}});
		Object.defineProperty(published, "cursor",       {
			get : function(){
				var nRow = rowset.@com.bearsoft.rowset.Rowset::getCurrentRow()();
			    return nRow != null ? @com.bearsoft.rowset.Row::publishFacade(Lcom/bearsoft/rowset/Row;Lcom/google/gwt/core/client/JavaScriptObject;)(nRow, null) : null;
			}
		});	
		var nEntityTitle = aEntity.@com.eas.client.model.Entity::getTitle()();
		var nEntityDesc = aEntity.@com.eas.client.model.Entity::getName()() + (nEntityTitle ? " [" + nEntityTitle + "]" : "");	    
		var pFields = @com.bearsoft.rowset.metadata.Fields::publishFacade(Lcom/bearsoft/rowset/metadata/Fields;Ljava/lang/String;)(nFields, nEntityDesc);
		Object.defineProperty(published, "schema",         { get : function(){ return pFields; }});
		// entity.params
		var nativeQuery = aEntity.@com.eas.client.model.Entity::getQuery()();
		var nativeParams = nativeQuery.@com.eas.client.queries.Query::getParameters()();
		var paramsCount = nativeParams.@com.bearsoft.rowset.metadata.Parameters::getParametersCount()();
		var publishedParams = {};  
		var publishedParamsSchema = @com.bearsoft.rowset.metadata.Fields::publishFacade(Lcom/bearsoft/rowset/metadata/Fields;Ljava/lang/String;)(nativeParams, "Params of " + nEntityDesc);
		for(var i = 0; i < paramsCount; i++){
			(function(){
				var _i = i;
				var propDesc = {
					 get : function(){ return publishedParamsSchema[_i].value; },
					 set : function(aValue){ publishedParamsSchema[_i].value = aValue; }
				};
				Object.defineProperty(publishedParams, publishedParamsSchema[_i].name, propDesc);
				Object.defineProperty(publishedParams, _i, propDesc);
			})();
		}			
		if(!publishedParams.schema)
			Object.defineProperty(publishedParams, "schema", { get : function(){ return publishedParamsSchema; }});
		Object.defineProperty(published, "params", {
			get : function(){
				return publishedParams;
			}
		});
		// events		
//		Object.defineProperty(published, "willDelete", {
//			get : function(){
//				return aEntity.@com.eas.client.model.Entity::getWillDelete()();
//			},
//			set : function(aValue){
//				aEntity.@com.eas.client.model.Entity::setWillDelete(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
//			}
//		});
//		Object.defineProperty(published, "willInsert", {
//			get : function(){
//				return aEntity.@com.eas.client.model.Entity::getWillInsert()();
//			},
//			set : function(aValue){
//				aEntity.@com.eas.client.model.Entity::setWillInsert(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
//			}
//		});
//		Object.defineProperty(published, "willScroll", {
//			get : function(){
//				return aEntity.@com.eas.client.model.Entity::getWillScroll()();
//			},
//			set : function(aValue){
//				aEntity.@com.eas.client.model.Entity::setWillScroll(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
//			}
//		});
		Object.defineProperty(published, "onDeleted", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnDeleted()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnDeleted(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onFiltered", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnFiltered()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnFiltered(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onInserted", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnInserted()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnInserted(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onRequeried", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnRequeried()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnRequeried(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onScrolled", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnScrolled()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnScrolled(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
	}-*/;

	public PropertyChangeSupport getChangeSupport() {
		return changeSupport;
	}

	public Fields getFields() {
		return rowset.getFields();
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
			rowset = null;
			filter = null;
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
		if(!(aRelation instanceof ReferenceRelation))
			return outRelations.add(aRelation);
		else
			return false;
	}

	public boolean addInRelation(Relation aRelation) {
		if(!(aRelation instanceof ReferenceRelation))
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

	private static class CancellableContainer{
		public Cancellable future;
	}
	
	protected void refreshRowset(final Callback<Rowset, String> aCallback) throws Exception {
		if (query != null && rowset != null) {
	        if (model.process != null || aCallback != null) {
	        	final CancellableContainer f = new CancellableContainer();
	        	f.future = rowset.refresh(query.getParameters(), new CallbackAdapter<Rowset, String>() {
	
					@Override
					public void doWork(Rowset aResult) throws Exception {
	                    if(pending == f.future){
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
	                    if(pending == f.future){
		                    valid = true;
		                    pending = null;
	                        model.terminateProcess(Entity.this, aMessage);
							if(aCallback != null){
								aCallback.onFailure(aMessage);
							}
	                    }
					}
					
				});
	        	pending = f.future;
	        }else{
	            rowset.refresh(query.getParameters(), null);
	        }
		}
	}

	public void validateQuery() throws Exception {
		if (query == null) {
			setQuery(Application.getAppQuery(queryName));
            prepareRowsetByQuery();
		}
	}
	
	protected void prepareRowsetByQuery() throws InvalidFieldsExceptionException{
		if(query != null){
			Rowset oldRowset = rowset;
			if(rowset != null){
				rowset.removeRowsetListener(this);
				rowset.setLog(null);
				rowset = null;
			}
			rowset = query.prepareRowset();
			rowset.setLog(model.getChangeLog());
			rowset.addRowsetListener(this);
            locator = new Locator();
            locator.setRowset(rowset);
			changeSupport.firePropertyChange("rowset", oldRowset, rowset);
		}
	}

	public Entity copy() throws Exception {
		assert model != null : "Entities can't exist without a model";
		Entity copied = new Entity(model);
		assign(copied);
		return copied;
	}

	public JavaScriptObject getOnDeleted() {
		return onDeleted;
	}

	public JavaScriptObject getOnInserted() {
		return onInserted;
	}

	public JavaScriptObject getOnScrolled() {
		return onScrolled;
	}

	public JavaScriptObject getWillDelete() {
		return willDelete;
	}

	public JavaScriptObject getWillInsert() {
		return willInsert;
	}

	public JavaScriptObject getWillScroll() {
		return willScroll;
	}

	public JavaScriptObject getOnFiltered() {
		return onFiltered;
	}

	public JavaScriptObject getOnRequeried() {
		return onRequeried;
	}

	public void setOnDeleted(JavaScriptObject aValue) {
		onDeleted = aValue;
	}

	public void setOnInserted(JavaScriptObject aValue) {
		onInserted = aValue;
	}

	public void setOnScrolled(JavaScriptObject aValue) {
		onScrolled = aValue;
	}

	public void setWillDelete(JavaScriptObject aValue) {
		willDelete = aValue;
	}

	public void setWillInsert(JavaScriptObject aValue) {
		willInsert = aValue;
	}

	public void setWillScroll(JavaScriptObject aValue) {
		willScroll = aValue;
	}

	public void setOnFiltered(JavaScriptObject aValue) {
		onFiltered = aValue;
	}

	public void setOnRequeried(JavaScriptObject aValue) {
		onRequeried = aValue;
	}

	public boolean isPending() {
		return pending != null;
	}

	public void refresh(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		refresh(new CallbackAdapter<Rowset, String>() {
			@Override
			protected void doWork(Rowset result) throws Exception {
				if (onSuccess != null)
					Utils.invokeJsFunction(onSuccess);
			}
			
			@Override
			public void onFailure(String reason) {
				if(onFailure != null){
					try{
						Utils.executeScriptEventVoid(jsPublished, onFailure, reason);
					}catch(Exception ex){
						Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			
		});
	}

	public void refresh(final Callback<Rowset, String> aCallback) throws Exception {
		if (model != null) {
			invalidate();
			internalExecute(aCallback);
		}
	}
	
	public void enqueueUpdate() throws Exception {
		model.getChangeLog().add(query.prepareCommand());	
	}
	
	public void execute(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
		execute(new CallbackAdapter<Rowset, String>() {
			
			@Override
			protected void doWork(Rowset aResult) throws Exception {
				if (onSuccess != null)
					Utils.invokeJsFunction(onSuccess);
			}
			
			@Override
			public void onFailure(String reason) {
				if(onFailure != null){
					try{
						Utils.executeScriptEventVoid(jsPublished, onFailure, reason);
					}catch(Exception ex){
						Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}			
			
		});
	}

	public void execute(Callback<Rowset, String> aCallback) throws Exception {
		if (model != null) {
			internalExecute(aCallback);
		}
	}

	protected void internalExecute(final Callback<Rowset, String> aCallback) throws Exception {
		assert query != null : QUERY_REQUIRED;
		bindQueryParameters();
		if (isValid()) {
			// There might be a case of only rowset filtering
			assert rowset != null;
			assert pending == null;
			filterRowset();
			rowset.silentFirst();
			if(aCallback != null){
				aCallback.onSuccess(rowset);
			}
		} else {
            // Requery if query parameters values have been changed while bindQueryParameters() call
            // or we are forced to refresh the data via requery() call.
			silentUnpend();
			refreshRowset(aCallback);
            assert rowset != null;
            assert pending != null || (aCallback == null && model.process == null);
            // filtering will be done while processing onRequeried event in ApplicationEntity code
		}
	}

    public void unpend() {
        if (pending != null) {
            pending.cancel();
            pending = null;
        }
    }

	protected void silentUnpend(){
		Model.RequeryProcess lprocess = model.process;
		model.process = null;
		try{
			unpend();
		}finally{
			model.process = lprocess;
		}
	}
	
    protected void internalExecuteChildren(boolean refresh) throws Exception {
        Set<Relation> rels = getOutRelations();
        if (rels != null) {
            Set<Entity> toExecute = new HashSet<>();
            for(Relation outRel : rels){
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
            for(Relation outRel : rels){
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
/*
	public void setRowset(Rowset aRowset) {
        Rowset oldRowset = rowset;
        if (rowset != null) {
            rowset.removeRowsetListener(this);
        }
        rowset = aRowset;
        valid = true;
        if (rowset != null) {
            rowset.addRowsetListener(this);
            changeSupport.firePropertyChange("rowset", oldRowset, rowset);
        }
	}
*/
	protected boolean isFilterable() throws Exception {
		return rowset != null && rtInFilterRelations != null && !rtInFilterRelations.isEmpty();
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
							Rowset leftRowset = leftEntity.getRowset();
							// There might be entities - parameters values sources, with no
							// data in theirs rowsets, so we can't bind query parameters to proper values. In the
							// such case we initialize parameters values with RowsetUtils.UNDEFINED_SQL_VALUE
							if (leftRowset != null && leftRowset.getCurrentRow() != null) {
								try {
									pValue = leftRowset.getCurrentRow().getColumnObject(leftRowset.getFields().find(relation.getLeftField().getName()));
								} catch (Exception ex) {
									pValue = RowsetUtils.UNDEFINED_SQL_VALUE;
									Logger.getLogger(Entity.class.getName()).log(Level.SEVERE,
									        "while assigning parameter:" + relation.getRightParameter() + " in entity: " + getTitle() + " [" + String.valueOf(getEntityId()) + "]", ex);
								}
							} else {
								pValue = RowsetUtils.UNDEFINED_SQL_VALUE;
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
		if(parametersModified){
			invalidate();
		}
	}

	protected void validateInFilterRelations() {
		// never build yet, so build it ...
		if (rtInFilterRelations == null) {
			rtInFilterRelations = new ArrayList<Relation>();
			assert rowset != null;
			Set<Relation> inRels = getInRelations();
			if (inRels != null) {
				for (Relation rel : inRels) {
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
        if (filter == null && !rtInFilterRelations.isEmpty()) {
            List<Integer> filterConstraints = new ArrayList<>();
            Fields rFields = rowset.getFields();
            // enumerate filtering relations ...
            for(Relation rel : rtInFilterRelations){
                assert rel != null && rel.isRightField();
                filterConstraints.add(rFields.find(rel.getRightField().getName()));
            };
            filter = rowset.createFilter(filterConstraints);
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
		try{
	        assert rowset != null : "Bad requery -> filter chain";
	        List<Object> filterKeys = new ArrayList<>();
			if (!rtInFilterRelations.isEmpty()) {
				for (Relation rel : rtInFilterRelations) {
					// relation must be filtering relation ...
					assert rel != null && rel.isRightField();
					Entity leftEntity = rel.getLeftEntity();
					assert leftEntity != null;
					Object fValue = null;
					if (rel.isLeftField()) {
						Rowset leftRowset = leftEntity.getRowset();
						if (leftRowset != null) {
							if (!leftRowset.isEmpty()) {
								if (leftRowset.getCurrentRow() != null) {
									fValue = leftRowset.getCurrentRow().getColumnObject(leftRowset.getFields().find(rel.getLeftField().getName()));
								} else {
									fValue = RowsetUtils.UNDEFINED_SQL_VALUE;
									Logger.getLogger(Entity.class.getName()).log(
									        Level.FINE,
									        "Failed to achieve value for filtering field:" + rel.getRightField() + " in entity: " + getTitle() + " [" + String.valueOf(getEntityId())
									                + "]. The source rowset has bad position (before first or after last).");
								}
							} else {
								fValue = RowsetUtils.UNDEFINED_SQL_VALUE;
								Logger.getLogger(Entity.class.getName()).log(
								        Level.FINE,
								        "Failed to achieve value for filtering field:" + rel.getRightField() + " in entity: " + getTitle() + " [" + String.valueOf(getEntityId())
								                + "]. The source rowset has no any rows.");
							}
						} else {
							fValue = RowsetUtils.UNDEFINED_SQL_VALUE;
							Logger.getLogger(Entity.class.getName()).log(
							        Level.FINE,
							        "Failed to achieve value for filtering field:" + rel.getRightField() + " in entity: " + getTitle() + " [" + String.valueOf(getEntityId())
							                + "]. The source rowset is absent.");
						}
					} else {
						Parameter leftParameter = rel.getLeftParameter();
						if (leftParameter != null) {
							fValue = leftParameter.getValue();
							if (fValue == null) {
								fValue = leftParameter.getDefaultValue();
							}
						} else {
							Logger.getLogger(Entity.class.getName()).log(Level.SEVERE,
							        "Parameter of left query must present (Relation points to query parameter, but query parameter with specified name is absent)");
						}
					}
					Field fieldOfValue = rowset.getFields().get(rel.getRightField().getName());
					filterKeys.add(Converter.convert2RowsetCompatible(fValue, fieldOfValue.getTypeInfo()));
				}
			}
			if (filter != null && !filter.isEmpty() && (filter != rowset.getActiveFilter() || !filter.getAppliedKeys().equals(filterKeys))) {
				filter.apply(filterKeys);			
				return true;
			} else {
				return false;
			}
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
	}

	@Override
	public boolean willScroll(RowsetScrollEvent aEvent) {
		assert aEvent.getRowset() == rowset;
		// call script method
		try {
			Boolean sRes = Utils.executeScriptEventBoolean(jsPublished, willScroll, JsEvents.publishCursorPositionWillChangeEvent(jsPublished, aEvent.getOldRowIndex(), aEvent.getNewRowIndex()));
			if (sRes != null) {
				return sRes;
			}
		} catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
		return true;
	}

	@Override
	public void rowsetScrolled(RowsetScrollEvent aEvent) {
        resignOnCursor();
		Rowset eventRowset = aEvent.getRowset();
		assert eventRowset == rowset;
		if (aEvent.getNewRowIndex() >= 0 && aEvent.getNewRowIndex() <= eventRowset.size() + 1) {
			try {
				internalExecuteChildren(false);
				// call script method
				Utils.executeScriptEventVoid(jsPublished, onScrolled, JsEvents.publishCursorPositionChangedEvent(jsPublished, aEvent.getOldRowIndex(), aEvent.getNewRowIndex()));
			} catch (Exception ex) {
				Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	@Override
	public boolean willInsertRow(RowsetInsertEvent aEvent) {
		// call script method
		try {
			JavaScriptObject publishedRow = Row.publishFacade(aEvent.getRow(), null);
			Boolean sRes = Utils.executeScriptEventBoolean(jsPublished, willInsert, JsEvents.publishEntityInstanceInsertEvent(jsPublished, publishedRow));
			if (sRes != null) {
				return sRes;
			}
		} catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
		return true;
	}

	@Override
	public boolean willDeleteRow(RowsetDeleteEvent aEvent) {
		// call script method
		try {
			JavaScriptObject publishedRow = Row.publishFacade(aEvent.getRow(), null);
			Boolean sRes = Utils.executeScriptEventBoolean(jsPublished, willDelete, JsEvents.publishEntityInstanceDeleteEvent(jsPublished, publishedRow));
			if (sRes != null) {
				return sRes;
			}
		} catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
		return true;
	}

	@Override
	public void rowInserted(RowsetInsertEvent aEvent) {
        resignOnCursor();
		try {
			internalExecuteChildren(false);
			// call script method
			JavaScriptObject publishedRow = Row.publishFacade(aEvent.getRow(), null);
			Utils.executeScriptEventVoid(jsPublished, onInserted, JsEvents.publishEntityInstanceInsertEvent(jsPublished, publishedRow));
		} catch (Exception ex) {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void rowDeleted(RowsetDeleteEvent aEvent) {
        resignOnCursor();
		try {
			internalExecuteChildren(false);
			// call script method
			JavaScriptObject publishedRow = Row.publishFacade(aEvent.getRow(), null);
			Utils.executeScriptEventVoid(jsPublished, onDeleted, JsEvents.publishEntityInstanceDeleteEvent(jsPublished, publishedRow));
		} catch (Exception ex) {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void rowsetSorted(RowsetSortEvent event) {
        resignOnCursor();
		try {
			internalExecuteChildren(false);
			// call script method
			JavaScriptObject publishedEvent = JsEvents.publishSourcedEvent(jsPublished);
			Utils.executeScriptEventVoid(jsPublished, onFiltered, publishedEvent);
		} catch (Exception ex) {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void rowsetRequeried(RowsetRequeryEvent event) {
        resignOnCursor();
		try {
			filterRowset();
			JavaScriptObject publishedEvent = JsEvents.publishSourcedEvent(jsPublished);
			Utils.executeScriptEventVoid(jsPublished, onRequeried, publishedEvent);
			internalExecuteChildren(false);
		} catch (Exception ex) {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void rowsetNetError(RowsetNetErrorEvent event) {
	}

	@Override
	public void rowsetFiltered(RowsetFilterEvent event) {
        resignOnCursor();
		try {
			internalExecuteChildren(false);
			// call script method
			JavaScriptObject publishedEvent = JsEvents.publishSourcedEvent(jsPublished);
			Utils.executeScriptEventVoid(jsPublished, onFiltered, publishedEvent);
		} catch (Exception ex) {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void rowsetSaved(RowsetSaveEvent event) {
	}

	@Override
	public void rowsetRolledback(RowsetRollbackEvent event) {
        resignOnCursor();
        try {
            filterRowset();
            internalExecuteChildren(false);
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
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
	public void beforeRequery(RowsetRequeryEvent event) {
	}

	@Override
	public boolean willSort(RowsetSortEvent event) {
		return true;
	}

    protected void resignOnCursor() {
        if (cursorListener != null) {
            cursorListener.removeHandler();
            cursorListener = null;
        }
        final Row cursor = rowset.getCurrentRow();
        if (cursor != null) {
            final PropertyChangeListener cursorPropsListener = new PropertyChangeListener(){
            	public void propertyChange(PropertyChangeEvent evt) {
                    try {
                        internalExecuteChildren(false);
                    } catch (Exception ex) {
                        Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
                    }
            	}
            };
            cursor.addPropertyChangeListener(cursorPropsListener);
            cursorListener = new HandlerRegistration(){
            	@Override
            	public void removeHandler() {
                    cursor.removePropertyChangeListener(cursorPropsListener);
            	}
            };
        }
    }

	protected void assign(Entity appTarget) throws Exception {
		appTarget.setEntityId(entityId);
		appTarget.setQueryName(queryName);
		appTarget.setTitle(title);
		appTarget.setName(name);
		appTarget.setOnDeleted(onDeleted);
		appTarget.setOnInserted(onInserted);
		appTarget.setOnScrolled(onScrolled);
		appTarget.setOnFiltered(onFiltered);
		appTarget.setOnRequeried(onRequeried);
		appTarget.setWillDelete(willDelete);
		appTarget.setWillInsert(willInsert);
		appTarget.setWillScroll(willScroll);
	}

	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}

	public boolean isValid(){
		return valid;
	}
	
	public void invalidate(){
		valid = false;
	}
	
	public Rowset getRowset() {
		return rowset;
	}

    private Orderer checkUserOrderer(List<Integer> aConstraints) throws IllegalStateException {
        Orderer orderer = userOrderers.get(aConstraints);
        if (orderer == null) {
            orderer = rowset.createOrderer(aConstraints);
            userOrderers.put(aConstraints, orderer);
        }
        return orderer;
    }

	public boolean scrollTo(Row aRow) throws Exception {
        if (aRow != null) {
            int idx = locator.indexOf(aRow);
            if (idx != -1) {
                return rowset.setCursorPos(idx + 1);
            }
        }
        return false;
	}

	/**
	 * Finds a single row and wraps it in appropriate js object.
	 * 
	 * @param aValue
	 *            Search key value.
	 * @return Wrapped row if it have been found and null otherwise.
	 */
	public JavaScriptObject jsFindById(Object aValue) throws Exception {
		Row result = findById(aValue);
		return result != null ? Row.publishFacade(result, null) : null;
	}
	
    public static final String BAD_PRIMARY_KEYS_MSG = "Bad primary keys detected. Required one and only one primary key field, but found: ";
    
	public Row findById(Object aValue) throws Exception {
        Fields fields = rowset.getFields();
        List<Integer> pks = fields.getPrimaryKeysIndicies();
        if (pks.size() == 1) {
            List<Object> keyValues = new ArrayList<>();
            keyValues.add(Utils.toJava(aValue));
            Orderer loc = checkUserOrderer(pks);
            Collection<Row> res = loc.get(keyValues);
            if (res != null && !res.isEmpty()) {
                return res.iterator().next();
            } else {
                return null;
            }
        } else {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, BAD_PRIMARY_KEYS_MSG + pks.size());
        }
        return null;
	}

	public static final String BAD_FIND_AGRUMENTS_MSG = "Bad find agruments";
	public static final String BAD_FIND_ARGUMENT_MSG = "Argument at index %d must be a rowset's field.";

	public JavaScriptObject jsFind(JavaScriptObject aCriteria) throws Exception {
		if (aCriteria != null) {
            Fields fields = rowset.getFields();
            List<Integer> constraints = new ArrayList<>();
            List<Object> keyValues = new ArrayList<>();
            JsArrayString jsKeys = aCriteria.<JsObject>cast().keys();
            for (int i = 0; i < jsKeys.length();i++) {
            	String key = jsKeys.get(i);
                int fieldIndex = fields.find(key);
                if (fieldIndex != -1) {
                    Field field = fields.get(key);
                    constraints.add(fieldIndex);
                    Object javaValue = aCriteria.<JsObject>cast().getJava(key);
                    Object convertedValue = Converter.convert2RowsetCompatible(javaValue, field.getTypeInfo());
                    keyValues.add(convertedValue);
                }
            }
            if (!constraints.isEmpty() && constraints.size() == keyValues.size()) {
                Orderer loc = checkUserOrderer(constraints);
                Subset res = loc.get(keyValues);
                if (res != null) {
                	if(res.getPublished() == null){
	                    JsObject jsRes = JavaScriptObject.createArray(res.size()).cast();
	                    int i = 0;
	                    for(Row r : res){
	                        JavaScriptObject jsRow = Row.publishFacade(r, null);
	                        jsRes.setSlot(i++, jsRow);
	                    }
	                    res.setPublished(jsRes);
                	}
                    return res.getPublished();
                }
            } else {
                Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, BAD_FIND_AGRUMENTS_MSG);
            }
		} else {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, BAD_FIND_AGRUMENTS_MSG);
		}
        return JavaScriptObject.createArray();
	}

	public JavaScriptObject jsCreateFilter(JavaScriptObject aConstraints) throws Exception {
		JsArrayMixed constraints = aConstraints.<JsArrayMixed> cast();
		Fields fields = rowset.getFields();
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < constraints.length(); i++) {
			JavaScriptObject jsConstraint = constraints.getObject(i);
			int colIndex = 0;
			try{
				Field field = RowsetUtils.unwrapField(jsConstraint);
				colIndex = fields.find(field.getName());
			}catch(Exception ex){
				// field name
				String possibleFieldName = constraints.getString(i);
				Field field = fields.get(possibleFieldName);
				if(field != null){
					colIndex = fields.find(possibleFieldName);
				}else{
					// field col index
					colIndex = Double.valueOf(constraints.getNumber(i)).intValue();
				}
			}
			indices.add(colIndex);
		}
		Filter lfilter = rowset.createFilter(indices);
		return Filter.publishFacade(lfilter);
	}

	public RowsComparator jsCreateSorting(JavaScriptObject aConstraints) {
		JsArrayMixed constraints = aConstraints.<JsArrayMixed> cast();
		Fields fields = getFields();
		List<SortingCriterion> criteria = new ArrayList<SortingCriterion>();
		if(aConstraints.<JsObject>cast().isArray()){
			for (int i = 0; i < constraints.length(); i += 2) {
				JavaScriptObject fieldValue = constraints.getObject(i);
				int colIndex = 0;
				try {
					// field instance
					Field field = RowsetUtils.unwrapField(fieldValue);
					colIndex = fields.find(field.getName());
				} catch (Exception ex) {
					// field name
					String possibleFieldName = constraints.getString(i);
					Field field = fields.get(possibleFieldName);
					if(field != null){
						colIndex = fields.find(possibleFieldName);
					}else{
						// field col index
						colIndex = Double.valueOf(constraints.getNumber(i)).intValue();
					}
				}
				// sorting direction
				boolean direction = true;
				if (i < constraints.length() - 1)
					direction = constraints.getBoolean(i + 1);
				criteria.add(new SortingCriterion(colIndex, direction));
			}
		}else{
			JsObject jsConstraints = aConstraints.<JsObject>cast();
			JsArrayString jsKeys = jsConstraints.keys();
			for(int i=0;i<jsKeys.length();i++){
				String jsKey = jsKeys.get(i);
				// field col index
				int colIndex = fields.find(jsKey);
				boolean direction = jsConstraints.getBoolean(jsKey);
				criteria.add(new SortingCriterion(colIndex, direction));
			}
		}
		RowsComparator comparator = new RowsComparator(criteria);
		return comparator;
	}

	public void sort(RowsComparator aComparator) throws Exception {
		RowsComparator comparator = (RowsComparator) aComparator;
		rowset.sort(comparator);
	}

	public void sort(Object aComparator) throws Exception {
		if(aComparator instanceof RowsComparator){
			sort((RowsComparator)aComparator);
		}else{
			assert aComparator instanceof JavaScriptObject; 
			sort((JavaScriptObject)aComparator);
		}		
	}
	
	public void sort(final JavaScriptObject aComparatorFunc) throws Exception {
		Comparator<Row> comparator = new Comparator<Row>() {
			
			protected native int invokeComparatorFunc(JavaScriptObject aComparatorFun, JavaScriptObject row1, JavaScriptObject row2) throws Exception/*-{
				return aComparatorFun(row1, row2);
			}-*/;
			
			@Override
			public int compare(Row row1, Row row2) {
				try {
					return invokeComparatorFunc(aComparatorFunc, Row.publishFacade(row1, null), Row.publishFacade(row2, null));
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}

		};
		rowset.sort(comparator);
	}

	public Row jsInsertAt(int aIndex, boolean aAjusting, JavaScriptObject aTarget) throws Exception {
		Row inserted = new Row(rowset.getFlowProvider().getEntityId(), rowset.getFields());
		Row.publishFacade(inserted, aTarget);
		rowset.insertAt(inserted, aAjusting, aIndex, null);
		return inserted;
	}

	public boolean jsSort(Object aSorting) throws InvalidCursorPositionException{
		if(aSorting instanceof RowsComparator){
			rowset.sort((RowsComparator)aSorting);
			return true;
		}else{
			return false;
		}
	}
	
	public void setPublished(JavaScriptObject aPublished) {
		if(jsPublished != aPublished){
			jsPublished = aPublished;
			if(jsPublished != null)
				publishFacade(this);
		}
	}

	@Override
	public JavaScriptObject getPublished() {
		return jsPublished;
	}
	
	public JavaScriptObject getElementClass() {
	    return rowset.getFields().getInstanceConstructor();
    }
	
	public void setElementClass(JavaScriptObject aValue) {
		rowset.getFields().setInstanceConstructor(aValue);
    }
}
