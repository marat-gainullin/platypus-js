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
import java.util.Collections;
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
import com.bearsoft.rowset.beans.PropertyChangeSupport;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.DelegatingFlowProvider;
import com.bearsoft.rowset.events.RowChangeEvent;
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
import com.bearsoft.rowset.exceptions.InvalidFieldsExceptionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.filters.Filter;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.locators.RowWrap;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.ordering.HashOrderer;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.bearsoft.rowset.utils.IDGenerator;
import com.bearsoft.rowset.utils.KeySet;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.application.Application;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.HasPublished;
import com.eas.client.queries.Query;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;

/**
 * 
 * @author mg
 */
public class Entity implements RowsetListener, HasPublished{

    protected static final String PENDING_ASSUMPTION_FAILED_MSG = "pending assigned to null without pending.cancel() call.";
	public static final String QUERY_REQUIRED = "All model entities must have a query";
	protected JavaScriptObject onBeforeChange;
	protected JavaScriptObject onAfterChange;
	protected JavaScriptObject onBeforeScroll;
	protected JavaScriptObject onAfterScroll;
	protected JavaScriptObject onBeforeInsert;
	protected JavaScriptObject onAfterInsert;
	protected JavaScriptObject onBeforeDelete;
	protected JavaScriptObject onAfterDelete;
	protected JavaScriptObject onRequeried;
	protected JavaScriptObject onFiltered;
	protected JavaScriptObject jsPublished;
	protected JavaScriptObject jsElementClass;
	// runtime
	protected List<Integer> filterConstraints = new ArrayList<Integer>();
	protected Cancellable pending;
	protected boolean valid;
	protected Rowset rowset;
	protected Filter filter;
	protected boolean userFiltering;
	protected Map<List<Integer>, Locator> userLocators = new HashMap<List<Integer>, Locator>();
	// to preserve relation order
	protected List<Relation> rtInFilterRelations;
	protected int updatingCounter;
	protected String title;
	protected String name;
	protected String entityId = String.valueOf((long) IDGenerator.genId());
	protected String queryName;
	protected Model model;
	protected Query query;
	protected Set<Relation> inRelations = new HashSet<Relation>();
	protected Set<Relation> outRelations = new HashSet<Relation>();
	protected Fields fields;
	protected PropertyChangeSupport changeSupport;
	protected Map<String, JavaScriptObject> ormDefinitions = new HashMap<String, JavaScriptObject>();

	public Entity() {
		super();
		changeSupport = new PropertyChangeSupport(this);
	}

	public Entity(Model aModel) {
		this();
		model = aModel;
	}

	public Entity(String aQueryId) {
		this();
		queryName = aQueryId;
	}

    public void putOrmDefinition(String aName, JavaScriptObject aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            if (!ormDefinitions.containsKey(aName)) {
                ormDefinitions.put(aName, aDefinition);
            }else{
                Logger.getLogger(Entity.class.getName()).log(Level.FINE, "ORM property "+aName+" redefinition attempt on entity "+(name != null && !name.isEmpty() ? name : "")+" "+(title != null && !title.isEmpty() ? "[" + title + "]" : "")+".");
            }
        }
    }

    public Map<String, JavaScriptObject> getOrmDefinitions() {
        return Collections.unmodifiableMap(ormDefinitions);
    }

	private static native JavaScriptObject publishEntityFacade(Entity aEntity)/*-{

		var rowset = aEntity.@com.eas.client.model.Entity::getRowset()();
		
		function propsToArray(aObj){
			var linearProps = [];
			for(var pName in aObj){
				if(isNaN(pName) && pName != "length"){
					linearProps.push(pName);
					linearProps.push(aObj[pName]);
				}
			}
			return linearProps;
		}
		var published = aEntity.@com.eas.client.model.Entity::getPublished()();
		// array mutator methods
		Object.defineProperty(published, "pop", { 
			value : function(){
                if(rowset != null){
                    var size = rowset.@com.bearsoft.rowset.Rowset::size()();
                    var deleted = rowset.@com.bearsoft.rowset.Rowset::getRow(I)(size);
                    rowset.@com.bearsoft.rowset.Rowset::deleteAt(I)(size);
                    return @com.eas.client.model.Entity::publishRowFacade(Lcom/bearsoft/rowset/Row;Lcom/eas/client/model/Entity;Lcom/google/gwt/core/client/JavaScriptObject;)(deleted, aEntity, null);
                }
			}
        });
        Object.defineProperty(published, "shift", {
        	value : function(){
                if(rowset != null){
                    var deleted = rowset.@com.bearsoft.rowset.Rowset::getRow(I)(1);
                    rowset.@com.bearsoft.rowset.Rowset::deleteAt(I)(1);
                    return @com.eas.client.model.Entity::publishRowFacade(Lcom/bearsoft/rowset/Row;Lcom/eas/client/model/Entity;Lcom/google/gwt/core/client/JavaScriptObject;)(deleted, aEntity, null);
                }
        	}
        });
        Object.defineProperty(published, "push", {
        	value : function(){
                if(rowset != null){
                    for(var i = 0; i < arguments.length; i++){
                        var cSize = rowset.@com.bearsoft.rowset.Rowset::size()();
                        var propsAsArray = propsToArray(arguments[i]);
                        aEntity.@com.eas.client.model.Entity::insertAt(ILcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(cSize + 1, propsAsArray, arguments[i]);
                    }
                    return rowset.@com.bearsoft.rowset.Rowset::size()();
                }else{
                    return 0;
                }
        	}
        });
        Object.defineProperty(published, "unshift", {
        	value : function(){
                if(rowset != null){
                    for(var i = arguments.length - 1; i >= 0; i--){
                        var propsAsArray = propsToArray(arguments[i]);
                        aEntity.@com.eas.client.model.Entity::insertAt(ILcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(1, propsAsArray, arguments[i]);
                    }
                    return rowset.@com.bearsoft.rowset.Rowset::size()();
                }else
                    return 0;
        	}
        });
        Object.defineProperty(published, "reverse", {
        	value : function(){
                if(rowset != null){
                    rowset.@com.bearsoft.rowset.Rowset::reverse()();
                }
        	}
        });
        Object.defineProperty(published, "splice", {
        	value : function(){
                if(arguments.length > 0){
                    if(rowset != null){
                        var size = rowset.@com.bearsoft.rowset.Rowset::size()();
                        var startAt = arguments[0];
                        if(startAt < 0)
                            startAt = size + startAt;
                        if(startAt < 0)
                            throw "Bad first argument 'index'. It should be less than or equal array's length by absolute value"; 
                        var howMany = arguments.length > 1 ? arguments[1] : size;
                        if(howMany < 0)
                            throw "Bad second argument 'howMany'. It should greater or equal to zero"; 
                        var toAdd = arguments.length > 2 ? $wnd.Array.prototype.slice.call(arguments, 2) : [];
                        var removed = [];
                        while(startAt < size && removed.length < howMany){
                            var deleted = rowset.@com.bearsoft.rowset.Rowset::getRow(I)(startAt + 1);
                            rowset.@com.bearsoft.rowset.Rowset::deleteAt(I)(startAt + 1);
                            var deletedFacade = @com.eas.client.model.Entity::publishRowFacade(Lcom/bearsoft/rowset/Row;Lcom/eas/client/model/Entity;Lcom/google/gwt/core/client/JavaScriptObject;)(deleted, aEntity, null);
                            removed.push(deletedFacade);
                            size = rowset.@com.bearsoft.rowset.Rowset::size()();
                        }
                        for(var l = arguments.length - 1; l >= 2; l--){						
                            var propsAsArray = propsToArray(arguments[l]);
                            aEntity.@com.eas.client.model.Entity::insertAt(ILcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(startAt + 1, propsAsArray, arguments[l]);
                        }
                        return removed;
                    }else
                        return [];
                }else
                    throw "Bad arguments. There are must at least one argument";
        	}
        });
        Object.defineProperty(published, "sort", {
        	value : function(aComparator) {
                if(aComparator){
                    aEntity.@com.eas.client.model.Entity::sort(Ljava/lang/Object;)(aComparator);
                }else
                    throw "A comparing function or comparator object must be specified."; 
        	}
        });
		// properties
//		Object.defineProperty(published, "getQueryId", {
//			value : function() {
//				return aEntity.@com.eas.client.model.Entity::getQueryName()();
//			}
//		});
//		Object.defineProperty(published, "isModified", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::isModified()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "isEmpty", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::isEmpty()();
//				else
//					return true;
//			}
//		});
//		Object.defineProperty(published, "isInserting", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::isInserting()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "getSize", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::size()();
//				else
//					return 0;
//			}
//		});
//		Object.defineProperty(published, "getRowIndex", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::getCursorPos()();
//				else
//					return -1;
//			}
//		});
//		Object.defineProperty(published, "setRowIndex", {
//			value : function(aRowIndex) {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::absolute(I)(aRowIndex);
//			}
//			
//		});
//		Object.defineProperty(published, "getSubstitute", {
//			value : function() {
//				var sEntity = aEntity.@com.eas.client.model.Entity::getSubstitute()();
//				if(sEntity != null)
//					return sEntity.@com.eas.client.model.Entity::getPublished()();
//				else
//					return null;
//			}
//		});
//		Object.defineProperty(published, "setSubstitute", {
//			value : function(aSubstitute) {
//				aEntity.@com.eas.client.model.Entity::setSubstitute(Lcom/eas/client/model/Entity;)(aSubstitute != null ? aSubstitute.unwrap() : null);
//			}
//		});
		// cursor interface 
		Object.defineProperty(published, "scrollTo", {
			value : function(aRow) {
				return aEntity.@com.eas.client.model.Entity::scrollTo(Lcom/google/gwt/core/client/JavaScriptObject;)(aRow);
			}
		});
//		Object.defineProperty(published, "beforeFirst", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::beforeFirst()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "afterLast", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::afterLast()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "bof", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::isBeforeFirst()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "eof", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::isAfterLast()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "first", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::first()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "next", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::next()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "prev", {
//			 value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::previous()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "last", {
//			value : function() {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::last()();
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "pos", {
//			value : function(aIndex) {
//				if(rowset != null)
//					return rowset.@com.bearsoft.rowset.Rowset::absolute(I)(aIndex);
//				else
//					return false;
//			}
//		});
//		Object.defineProperty(published, "getRow", {
//			value : function(aIndex) {
//				if(rowset != null)
//					return @com.eas.client.model.Entity::publishRowFacade(Lcom/bearsoft/rowset/Row;Lcom/eas/client/model/Entity;)(rowset.@com.bearsoft.rowset.Rowset::getRow(I)(aIndex), aEntity);
//				else
//					return null;
//			}
//		});
		// find interface
		Object.defineProperty(published, "find", {
			value : function() {
				var args;
				if(arguments.length == 1){
					args = arguments[0];
				}else{
					args = [];
					for(var a=0;a<arguments.length;a++){
						args[args.length] = arguments[a]; 
					}
				}
				return aEntity.@com.eas.client.model.Entity::find(Lcom/google/gwt/core/client/JavaScriptObject;)(args);
			}
		});
		Object.defineProperty(published, "findById", {
			value : function(aValue) {
				return aEntity.@com.eas.client.model.Entity::findById(Ljava/lang/Object;)($wnd.P.boxAsJava(aValue));
			}
		});
		// relations interface
		Object.defineProperty(published, "beginUpdate", {
			value : function() {
				aEntity.@com.eas.client.model.Entity::beginUpdate()();
			}
		});
		Object.defineProperty(published, "endUpdate", {
			value : function() {
				aEntity.@com.eas.client.model.Entity::endUpdate()();
			}
		});
		//
		Object.defineProperty(published, "enqueueUpdate", {
			value : function() {
				aEntity.@com.eas.client.model.Entity::enqueueUpdate()();
			}
		});
		Object.defineProperty(published, "execute", {
			value : function(onSuccess, onFailure) {
				var oldManual = published.manual;
				try{
					published.manual = false;
					aEntity.@com.eas.client.model.Entity::execute(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
				}finally{
					published.manual = oldManual;
				}
			}
		});
		Object.defineProperty(published, "requery", {
			value : function(onSuccess, onFailure) {
				var oldManual = published.manual;
				try{
					published.manual = false;
					aEntity.@com.eas.client.model.Entity::refresh(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(onSuccess, onFailure);
				}finally{
					published.manual = oldManual;
				}
			}
		});
		// processing interface
		//Object.defineProperty(published, "createLocator", {
		//	value : function() {
		//		return aEntity.@com.eas.client.model.Entity::createLocator(Lcom/google/gwt/core/client/JavaScriptObject;)(arguments.length == 1 && Array.isArray(arguments[0]) ? arguments[0] : arguments);
		//	}
		//});
		Object.defineProperty(published, "createFilter", {
			value : function() {
				return aEntity.@com.eas.client.model.Entity::createFilter(Lcom/google/gwt/core/client/JavaScriptObject;)(arguments.length == 1 && Array.isArray(arguments[0]) ? arguments[0] : arguments);
			}
		});
		Object.defineProperty(published, "activeFilter", {
			get : function() {
				var nFilter = rowset.@com.bearsoft.rowset.Rowset::getActiveFilter()();
				return @com.eas.client.model.Entity::publishFilterFacade(Lcom/bearsoft/rowset/filters/Filter;Lcom/eas/client/model/Entity;)(nFilter, aEntity);
			}
		});
		
		Object.defineProperty(published, "createSorting", {
			value : function() {
				var args;
				if(arguments.length == 1){
					args = arguments[0];
				}else{
					args = [];
					for(var a=0;a<arguments.length;a++){
						args[args.length] = arguments[a]; 
					}
				}
				return aEntity.@com.eas.client.model.Entity::createSorting(Lcom/google/gwt/core/client/JavaScriptObject;)(args);
			}
		});
		// data at cursor interface
//		Object.defineProperty(published, "getObject", {
//			value : function(aColIndex) {
//				var rValue = null;
//				if(rowset != null){
//					rValue = rowset.@com.bearsoft.rowset.Rowset::getJsObject(Ljava/lang/String;)(published.schema[aColIndex - 1].name);
//				}
//				if(rValue == null){
//					rValue = aEntity.@com.eas.client.model.Entity::getSubstituteRowsetJsObject(I)(aColIndex);
//				}
//				return $wnd.P.boxAsJs(rValue);
//			}
//		});
//		// modify interface
//		Object.defineProperty(published, "updateObject", {
//			value : function(aColIndex, aValue) {
//				if(rowset != null)
//					rowset.@com.bearsoft.rowset.Rowset::updateJsObject(Ljava/lang/String;Ljava/lang/Object;)(published.schema[aColIndex-1].name, $wnd.P.boxAsJava(aValue));
//			}
//		});
//		Object.defineProperty(published, "insert", {
//			value : function() {
//				aEntity.@com.eas.client.model.Entity::insert(Lcom/google/gwt/core/client/JavaScriptObject;)(arguments);
//			}
//		});
//		Object.defineProperty(published, "insertAt", {
//			value : function() {
//				aEntity.@com.eas.client.model.Entity::insertAt(ILcom/google/gwt/core/client/JavaScriptObject;)($wnd.P.boxAsJava(arguments[0]), $wnd.Array.prototype.slice.call(arguments, 1));
//			}
//		});
		Object.defineProperty(published, "removeAll", {
			value : function() {
				if(rowset != null){
					rowset.@com.bearsoft.rowset.Rowset::deleteAll()();
				}
			}
		});
//		Object.defineProperty(published, "deleteAll", {
//			value : function() {
//				if(rowset != null)
//					rowset.@com.bearsoft.rowset.Rowset::deleteAll()();
//			}
//		});
		Object.defineProperty(published, "remove", {
			value : function(aRow) {
				if(rowset != null){
					if(aRow){
						if(aRow.unwrap){
							rowset.@com.bearsoft.rowset.Rowset::deleteRow(Lcom/bearsoft/rowset/Row;)(aRow.unwrap());
						}else{
							rowset.@com.bearsoft.rowset.Rowset::deleteAt(I)(aRow);
						}
					}else{
						rowset.@com.bearsoft.rowset.Rowset::delete()();
					}
				}
			}
		});
//		Object.defineProperty(published, "deleteRow", {
//			value : function(aRow) {
//				if(rowset != null){
//					if(aRow){
//						if(aRow.unwrap)
//							rowset.@com.bearsoft.rowset.Rowset::deleteRow(Lcom/bearsoft/rowset/Row;)(aRow.unwrap());
//						else
//							rowset.@com.bearsoft.rowset.Rowset::deleteAt(I)(aRow);
//					}else
//						rowset.@com.bearsoft.rowset.Rowset::delete()();
//				}
//			}
//		});
		Object.defineProperty(published, "unwrap", {
			value : function() {
				return aEntity;
			}
		});
		// properties
//		Object.defineProperty(published, "queryId",      { get : function(){ return published.getQueryId()}});
//		Object.defineProperty(published, "manual",       { get : function(){ return aEntity.@com.eas.client.model.Entity::isManual()()}, set : function(aValue){ aEntity.@com.eas.client.model.Entity::setManual(Z)(!!aValue)}});
//		Object.defineProperty(published, "modified",     { get : function(){ return published.isModified()}});
//		Object.defineProperty(published, "empty",        { get : function(){ return published.isEmpty()}});
//		Object.defineProperty(published, "inserting",    { get : function(){ return published.isInserting()}});
//		Object.defineProperty(published, "size",         { get : function(){ return published.getSize(); }});
		Object.defineProperty(published, "cursorPos",    { get : function(){ return rowset.@com.bearsoft.rowset.Rowset::getCursorPos()();}, set : function(aValue){ rowset.@com.bearsoft.rowset.Rowset::absolute(I)(+aValue);}});
//		Object.defineProperty(published, "substitute",   { get : function(){ return published.getSubstitute()}, set : function(aValue){ published.setSubstitute(aValue)}});
		Object.defineProperty(published, "elementClass", { get : function(){ return aEntity.@com.eas.client.model.Entity::getElementClass()()}, set : function(aValue){ aEntity.@com.eas.client.model.Entity::setElementClass(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue)}});
		Object.defineProperty(published, "cursor",       {
			get : function(){
				var nRow = rowset.@com.bearsoft.rowset.Rowset::getCurrentRow()();
			    return @com.eas.client.model.Entity::publishRowFacade(Lcom/bearsoft/rowset/Row;Lcom/eas/client/model/Entity;Lcom/google/gwt/core/client/JavaScriptObject;)(nRow, aEntity, null);
			}
		});
		    
		Object.defineProperty(published, "schema",         { get : function(){ return @com.eas.client.model.Entity::publishFieldsFacade(Lcom/bearsoft/rowset/metadata/Fields;Lcom/eas/client/model/Entity;)(aEntity.@com.eas.client.model.Entity::getFields()(), aEntity) }});
		// entity.params
		var nativeQuery = aEntity.@com.eas.client.model.Entity::getQuery()();
		var nativeParams = nativeQuery.@com.eas.client.queries.Query::getParameters()();
		var paramsCount = nativeParams.@com.bearsoft.rowset.metadata.Parameters::getParametersCount()();
		var publishedParams = {};  
		var publishedParamsSchema = @com.eas.client.model.Entity::publishFieldsFacade(Lcom/bearsoft/rowset/metadata/Fields;Lcom/eas/client/model/Entity;)(nativeParams, aEntity);
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
		Object.defineProperty(published, "willChange", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnBeforeChange()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnBeforeChange(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "willDelete", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnBeforeDelete()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnBeforeDelete(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "willInsert", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnBeforeInsert()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnBeforeInsert(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "willScroll", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnBeforeScroll()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnBeforeScroll(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onChanged", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnAfterChange()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnAfterChange(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onDeleted", {
			get : function(){
				return aEntity.@com.eas.client.model.Entity::getOnAfterDelete()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnAfterDelete(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
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
				return aEntity.@com.eas.client.model.Entity::getOnAfterInsert()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnAfterInsert(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
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
				return aEntity.@com.eas.client.model.Entity::getOnAfterScroll()();
			},
			set : function(aValue){
				aEntity.@com.eas.client.model.Entity::setOnAfterScroll(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
	}-*/;

	public static native void publishRows(JavaScriptObject aPublished) throws Exception/*-{
		var nEntity = aPublished.unwrap();
		var rowset = nEntity.@com.eas.client.model.Entity::getRowset()();
		var rowsCount = rowset.@com.bearsoft.rowset.Rowset::size()(); 
        $wnd.Array.prototype.splice.call(aPublished, 0, aPublished.length);
		for (var j = 1; j <= rowsCount; j++) {
			var nRow = rowset.@com.bearsoft.rowset.Rowset::getRow(I)(j);
			$wnd.Array.prototype.push.call(aPublished, @com.eas.client.model.Entity::publishRowFacade(Lcom/bearsoft/rowset/Row;Lcom/eas/client/model/Entity;Lcom/google/gwt/core/client/JavaScriptObject;)(nRow, nEntity, null));
		}
	}-*/;

	public static native JavaScriptObject publishRowFacade(Row aRow, Entity aEntity, JavaScriptObject aTarget) throws Exception/*-{
		if(aRow != null){
			var published = aRow.@com.bearsoft.rowset.Row::getPublished()();
			if(published == null){
				if(aTarget){
					published = aTarget;
				}else{
					var elClass = aEntity.@com.eas.client.model.Entity::getElementClass()();
					if(elClass != null && typeof elClass == "function")
						published = new elClass();
					else
						published = {};
				} 
				Object.defineProperty(published, "unwrap", { get : function(){
					return function() {
						return aRow;
					}
				}});
				var nativeFields = aEntity.@com.eas.client.model.Entity::getFields()();
				var fieldsCount = nativeFields.@com.bearsoft.rowset.metadata.Fields::getFieldsCount()();
				var schema = @com.eas.client.model.Entity::publishFieldsFacade(Lcom/bearsoft/rowset/metadata/Fields;Lcom/eas/client/model/Entity;)(nativeFields, aEntity);
				for(var i = 0; i < fieldsCount; i++){
					(function(){
						var _i = i;
						var propDesc = {
							 get : function(){ return $wnd.P.boxAsJs(aRow.@com.bearsoft.rowset.Row::getFieldObject(Ljava/lang/String;)(schema[_i].name)); },
							 set : function(aValue){ aRow.@com.bearsoft.rowset.Row::setFieldObject(Ljava/lang/String;Ljava/lang/Object;)(schema[_i].name, $wnd.P.boxAsJava(aValue)); }
						};
						Object.defineProperty(published, schema[_i].name, propDesc);
						Object.defineProperty(published, (_i+""),     propDesc);
					})();
				}
				aRow.@com.bearsoft.rowset.Row::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				aEntity.@com.eas.client.model.Entity::publishOrmProps(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		}else
			return null;
	}-*/;

	public void publishOrmProps(JavaScriptObject aTarget){
		for(Map.Entry<String, JavaScriptObject> entry : ormDefinitions.entrySet()){
			aTarget.<JsObject>cast().defineProperty(entry.getKey(), entry.getValue());
		}
	}
	
	public static native JavaScriptObject publishFieldsFacade(Fields aFields, Entity aEntity) throws Exception/*-{
		if(aFields != null){
			var published = aFields.@com.bearsoft.rowset.metadata.Fields::getPublished()();
			if(published == null){
				published = {
					getFieldsCount : function() {
						return aFields.@com.bearsoft.rowset.metadata.Fields::getFieldsCount()();
					},
					isEmpty : function() {
						return aFields.@com.bearsoft.rowset.metadata.Fields::isEmpty()();
					},
					get : function(aFieldIndex) {
						return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aFields.@com.bearsoft.rowset.metadata.Fields::get(I)(aFieldIndex));
					},
					getTableDescription : function() {
						return aFields.@com.bearsoft.rowset.metadata.Fields::getTableDescription()();
					},
					unwrap : function()
					{
						return aFields;
					}
				};
				
				Object.defineProperty(published, "empty", { get : function(){ return published.isEmpty()}});
				Object.defineProperty(published, "tableDescription", { get : function(){ return published.getTableDescription()}});
				var fieldsCount = published.getFieldsCount();
				
				for(var i = 0; i < fieldsCount; i++)
				{
					(function(){
						var _i = i;
						Object.defineProperty(published, (_i+""), { get : function(){ return published.get(_i+1) }});
						Object.defineProperty(published, published.get(_i+1).name, { get : function(){ return published.get(_i+1) }});
					})();
				}
				aFields.@com.bearsoft.rowset.metadata.Fields::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		}else
			return null;
	}-*/;

	public static native JavaScriptObject publishFieldFacade(Field aField) throws Exception/*-{
		if(aField != null)
		{
			var published = aField.@com.bearsoft.rowset.metadata.Field::getPublished()();
			if (published == null) {
				published = {
					getName : function() {
						return aField.@com.bearsoft.rowset.metadata.Field::getName()();
					},
					getDescription : function() {
						return aField.@com.bearsoft.rowset.metadata.Field::getDescription()();
					},
					getSize : function() {
						return aField.@com.bearsoft.rowset.metadata.Field::getSize()();
					},
					isPk : function() {
						return aField.@com.bearsoft.rowset.metadata.Field::isPk()();
					},
					setPk : function(aValue) {
						aField.@com.bearsoft.rowset.metadata.Field::setPk(Z)(aValue);
					},
					isStrong4Insert : function() {
						return aField.@com.bearsoft.rowset.metadata.Field::isStrong4Insert()();
					},
					setStrong4Insert : function(aValue) {
						aField.@com.bearsoft.rowset.metadata.Field::setStrong4Insert(Z)(aValue);
					},
					isNullable : function() {
						return aField.@com.bearsoft.rowset.metadata.Field::isNullable()();
					},
					isReadonly : function() {
						return aField.@com.bearsoft.rowset.metadata.Field::isReadonly()();
					},
					unwrap : function() {
						return aField;
					}
				};
				Object.defineProperty(published, "name", {
					get : function() {
						return published.getName();
					}
				});
				Object.defineProperty(published, "description", {
					get : function() {
						return published.getDescription();
					}
				});
				Object.defineProperty(published, "size", {
					get : function() {
						return published.getSize();
					}
				});
				Object.defineProperty(published, "pk", {
					get : function() {
						return published.isPk();
					},
					set : function(aValue) {
						published.setPk(aValue);
					}
				});
				Object.defineProperty(published, "strong4Insert", {
					get : function() {
						return published.isStrong4Insert();
					},
					set : function(aValue) {
						published.setStrong4Insert(aValue);
					}
				});
				Object.defineProperty(published, "nullable", {
					get : function() {
						return published.isNullable();
					}
				});
				Object.defineProperty(published, "readonly", {
					get : function() {
						return published.isReadonly();
					}
				});
				if(@com.eas.client.model.Entity::isParameter(Lcom/bearsoft/rowset/metadata/Field;)(aField))
				{
					Object.defineProperty(published, "modified", {
						get : function() {
							return aField.@com.bearsoft.rowset.metadata.Parameter::isModified()();
						}
					});
					Object.defineProperty(published, "value", {
						get : function() {
							return $wnd.P.boxAsJs(aField.@com.bearsoft.rowset.metadata.Parameter::getJsValue()());
						},
						set : function(aValue) {
							aField.@com.bearsoft.rowset.metadata.Parameter::setJsValue(Ljava/lang/Object;)($wnd.P.boxAsJava(aValue));
						}
					});
				}
				aField.@com.bearsoft.rowset.metadata.Field::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		} else
			return null;
	}-*/;

	public static native JavaScriptObject publishLocatorFacade(Locator loc, Entity aEntity) throws Exception/*-{
		if(loc != null)
		{
			var published = loc.@com.bearsoft.rowset.locators.Locator::getPublished()();
			if(published == null){
				published = {
					first : function() {
						return loc.@com.bearsoft.rowset.locators.Locator::first()();
					},
					last : function() {
						return loc.@com.bearsoft.rowset.locators.Locator::last()();
					},
					next : function() {
						return loc.@com.bearsoft.rowset.locators.Locator::next()();
					},
					prev : function() {
						return loc.@com.bearsoft.rowset.locators.Locator::previous()();
					},
					isBeforeFirst : function() {
						return loc.@com.bearsoft.rowset.locators.Locator::isBeforeFirst()();
					},
					isAfterLast : function() {
						return loc.@com.bearsoft.rowset.locators.Locator::isAfterLast()();
					},
					find : function() {
						return loc.@com.bearsoft.rowset.locators.Locator::find(Lcom/google/gwt/core/client/JavaScriptObject;)(arguments);
					},
					getRow : function(aIndex) {
						return @com.eas.client.model.Entity::publishRowFacade(Lcom/bearsoft/rowset/Row;Lcom/eas/client/model/Entity;Lcom/google/gwt/core/client/JavaScriptObject;)(loc.@com.bearsoft.rowset.locators.Locator::getRow(I)(aIndex), aEntity, null);
					},
					getSize : function() {
						return loc.@com.bearsoft.rowset.locators.Locator::getSize()();
					},
					unwrap : function() {
						return loc;
					}
				}
				loc.@com.bearsoft.rowset.locators.Locator::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		} else
			return null;
	}-*/;

	public static native JavaScriptObject publishFilterFacade(Filter aFilter, Entity aEntity) throws Exception/*-{
		if (aFilter != null) {
			var published = aFilter.@com.bearsoft.rowset.filters.Filter::getPublished()();
			if (published == null) {
				published = {
					apply : function() {
						aEntity.@com.eas.client.model.Entity::setUserFiltering(Z)(true);
						aFilter.@com.bearsoft.rowset.filters.Filter::apply(Lcom/google/gwt/core/client/JavaScriptObject;)(arguments);
					},
					isApplied : function() {
						return aFilter.@com.bearsoft.rowset.filters.Filter::isApplied()();
					},
					cancel : function() {
						aFilter.@com.bearsoft.rowset.filters.Filter::cancelFilter()();
					},
					unwrap : function() {
						return aFilter;
					}
				}
				aFilter.@com.bearsoft.rowset.filters.Filter::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		} else
			return null;
	}-*/;

	public PropertyChangeSupport getChangeSupport() {
		return changeSupport;
	}

	public Fields getFields() {
		if (fields == null) {
			try {
				assert query != null : "Query must present (getFields)";
				if (query != null) {
					fields = query.getFields();
					if (fields == null) {
						if (rowset != null) {
							fields = rowset.getFields();
						}
					}
					assert fields != null;
				}
			} catch (Exception ex) {
				fields = null;
				Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		if (fields != null)
			fields.setOwner(this);
		return fields;
	}

	public void clearFields() {
		if (fields != null)
			fields.setOwner(null);
		fields = null;
		setQuery(null);
	}

	public Model getModel() {
		return model;
	}

	public void regenerateId() {
		entityId = String.valueOf(IDGenerator.genId());
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
		if (query != null)
			query.getParameters().setOwner(null);
		query = aValue;
		if (query != null)
			query.getParameters().setOwner(this);
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
	                    assert pending == f.future : PENDING_ASSUMPTION_FAILED_MSG;
	                    valid = true;
	                    pending = null;
	                    model.terminateProcess(Entity.this, null);
	                    if (aCallback != null) {
	                    	aCallback.onSuccess(aResult);
	                    }
					}
					
					@Override
					public void onFailure(String aMessage) {
	                    assert pending == f.future : PENDING_ASSUMPTION_FAILED_MSG;
	                    valid = true;
	                    pending = null;
                        model.terminateProcess(Entity.this, aMessage);
						if(aCallback != null){
							aCallback.onFailure(aMessage);
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
				unforwardChangeLog();
				rowset = null;
			}
			rowset = query.prepareRowset();
			forwardChangeLog();
			rowset.addRowsetListener(this);
			changeSupport.firePropertyChange("rowset", oldRowset, rowset);
		}
	}

	protected void unforwardChangeLog(){
		if(rowset.getFlowProvider() instanceof DelegatingFlowProvider){
			DelegatingFlowProvider dfp = (DelegatingFlowProvider)rowset.getFlowProvider();
			rowset.setFlowProvider(dfp.getDelegate());
		}
	}
	
	protected void forwardChangeLog(){
		rowset.setFlowProvider(new DelegatingFlowProvider(rowset.getFlowProvider()) {
			public List<Change> getChangeLog() {
				return model.getChangeLog();
			};
		});
	}
	
	public Entity copy() throws Exception {
		assert model != null : "Entities can't exist without a model";
		Entity copied = new Entity(model);
		assign(copied);
		return copied;
	}

	public JavaScriptObject getOnAfterChange() {
		return onAfterChange;
	}

	public JavaScriptObject getOnAfterDelete() {
		return onAfterDelete;
	}

	public JavaScriptObject getOnAfterInsert() {
		return onAfterInsert;
	}

	public JavaScriptObject getOnAfterScroll() {
		return onAfterScroll;
	}

	public JavaScriptObject getOnBeforeChange() {
		return onBeforeChange;
	}

	public JavaScriptObject getOnBeforeDelete() {
		return onBeforeDelete;
	}

	public JavaScriptObject getOnBeforeInsert() {
		return onBeforeInsert;
	}

	public JavaScriptObject getOnBeforeScroll() {
		return onBeforeScroll;
	}

	public JavaScriptObject getOnFiltered() {
		return onFiltered;
	}

	public JavaScriptObject getOnRequeried() {
		return onRequeried;
	}

	public void setOnAfterChange(JavaScriptObject aValue) {
		onAfterChange = aValue;
	}

	public void setOnAfterDelete(JavaScriptObject aValue) {
		onAfterDelete = aValue;
	}

	public void setOnAfterInsert(JavaScriptObject aValue) {
		onAfterInsert = aValue;
	}

	public void setOnAfterScroll(JavaScriptObject aValue) {
		onAfterScroll = aValue;
	}

	public void setOnBeforeChange(JavaScriptObject aValue) {
		onBeforeChange = aValue;
	}

	public void setOnBeforeDelete(JavaScriptObject aValue) {
		onBeforeDelete = aValue;
	}

	public void setOnBeforeInsert(JavaScriptObject aValue) {
		onBeforeInsert = aValue;
	}

	public void setOnBeforeScroll(JavaScriptObject aValue) {
		onBeforeScroll = aValue;
	}

	public void setOnFiltered(JavaScriptObject aValue) {
		onFiltered = aValue;
	}

	public void setOnRequeried(JavaScriptObject aValue) {
		onRequeried = aValue;
	}

	/*
	 * private void silentFirst() throws InvalidCursorPositionException {
	 * rowset.removeRowsetListener(this); try { rowset.first(); } finally {
	 * rowset.addRowsetListener(this); } }
	 */

	public void beginUpdate() {
		updatingCounter++;
	}

	public void endUpdate() throws Exception {
		assert updatingCounter > 0;
		updatingCounter--;
		if (updatingCounter == 0) {
			internalExecuteChildren(false);
		}
	}

	public boolean isManual() {
		assert query != null : "Query must present (isManual)";
		return query.isManual();
	}

	public void setManual(boolean aValue) {
		assert query != null : "Query must present (setManual)";
		query.setManual(aValue);
	}

	public boolean isPending() {
		return pending != null;
	}

	public boolean isRowsetPresent() {
		return rowset != null;
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
			uninstallUserFiltering();
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
	
	protected void uninstallUserFiltering() throws RowsetException {
		if (userFiltering && rowset != null && rowset.getActiveFilter() != null) {
			rowset.getActiveFilter().cancelFilter();
		}
		userFiltering = false;
	}
	
    protected void internalExecuteChildren(boolean refresh) throws Exception {
        if (updatingCounter == 0) {
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
    }

    protected void internalExecuteChildren(boolean refresh, int aOnlyFieldIndex) throws Exception {
        if (updatingCounter == 0) {
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
    }

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

	public boolean isUserFiltering() {
		return userFiltering;
	}

	public void setUserFiltering(boolean aUserFiltering) throws Exception {
		boolean oldUserFiltering = userFiltering;
		userFiltering = aUserFiltering;
		if (oldUserFiltering != userFiltering) {
			if (rowset.getActiveFilter() != null) {
				rowset.getActiveFilter().cancelFilter();
			}
			execute(null);
		}
	}

	protected boolean isFilterable() throws Exception {
		return rowset != null && !userFiltering && rtInFilterRelations != null && !rtInFilterRelations.isEmpty();
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
							if (leftRowset != null && !leftRowset.isEmpty() && !leftRowset.isBeforeFirst() && !leftRowset.isAfterLast()) {
								try {
									pValue = leftRowset.getObject(leftRowset.getFields().find(relation.getLeftField().getName()));
								} catch (Exception ex) {
									pValue = RowsetUtils.UNDEFINED_SQL_VALUE;
									Logger.getLogger(Entity.class.getName()).log(Level.SEVERE,
									        "while assigning parameter:" + relation.getRightParameter() + " in entity: " + getTitle() + " [" + String.valueOf(getEntityId()) + "]", ex);
								}
							} else {
								pValue = RowsetUtils.UNDEFINED_SQL_VALUE;
							}
						} else {
							/*
							 * Query leftQuery = leftEntity.getQuery();
							 * assert leftQuery != null :
							 * "Left query must present (Relation points to query, but query is absent)"
							 * ; Parameters leftParams =
							 * leftQuery.getParameters(); assert leftParams
							 * != null :
							 * "Parameters of left query must present (Relation points to query parameter, but query parameters are absent)"
							 * ; Parameter leftParameter =
							 * leftParams.get(relation.getLeftParameter());
							 * if (leftParameter != null) { pValue =
							 * leftParameter.getValue(); if (pValue == null)
							 * { pValue = leftParameter.getDefaultValue(); }
							 * } else {
							 * Logger.getLogger(Entity.class.getName()).log(
							 * Level.SEVERE,
							 * "Parameter of left query must present (Relation points to query parameter "
							 * + relation.getRightParameter() +
							 * " in entity: " + getTitle() + " [" +
							 * String.valueOf(getEntityId()) +
							 * "], but query parameter with specified name is absent)"
							 * ); }
							 */
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
			List<Field> constraints = new ArrayList<Field>();
			// enumerate filtering relations ...
			for (Relation rel : rtInFilterRelations) {
				assert rel != null && rel.isRightField();
				constraints.add(rel.getRightField());
			}
			if (!constraints.isEmpty()) {
				filter = rowset.createFilter();
				filter.beginConstrainting();
				try {
					Fields rFields = rowset.getFields();
					for (Field field : constraints) {
						filter.addConstraint(rFields.find(field.getName()));
					}
				} finally {
					filter.endConstrainting();
				}
				filter.build();
			}
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
		assert !userFiltering : "Can't apply own filter while user filtering";
		assert rowset != null : "Bad requery -> filter chain";
		KeySet filterKeySet = new KeySet();
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
							if (!leftRowset.isBeforeFirst() && !leftRowset.isAfterLast()) {
								fValue = leftRowset.getObject(leftRowset.getFields().find(rel.getLeftField().getName()));
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
					/*
					 * Query leftQuery = leftEntity.getQuery(); assert leftQuery
					 * != null :
					 * "Left query must present (Relation points to query, but query is absent)"
					 * ; Parameters leftParams = leftQuery.getParameters();
					 * assert leftParams != null :
					 * "Parameters of left query must present (Relation points to query parameter, but query parameters are absent)"
					 * ; Parameter leftParameter =
					 * leftParams.get(rel.getLeftParameter()); if (leftParameter
					 * != null) { fValue = leftParameter.getValue(); if (fValue
					 * == null) { fValue = leftParameter.getDefaultValue(); } }
					 * else {
					 * Logger.getLogger(Entity.class.getName()).log(Level.
					 * SEVERE,
					 * "Parameter of left query must present (Relation points to query parameter "
					 * + rel.getLeftParameter() +
					 * ", but query parameter with specified name is absent)");
					 * }
					 */
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
				filterKeySet.add(Converter.convert2RowsetCompatible(fValue, fieldOfValue.getTypeInfo()));
			}
		}
		if (filter != null && !filter.isEmpty() && (filter != rowset.getActiveFilter() || !filter.getKeysetApplied().equals(filterKeySet))) {
			filter.filterRowset(filterKeySet);			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean willScroll(RowsetScrollEvent aEvent) {
		assert aEvent.getRowset() == rowset;
		// call script method
		Boolean sRes = null;
		try {
			sRes = Utils.executeScriptEventBoolean(jsPublished, onBeforeScroll, JsEvents.publishCursorPositionWillChangeEvent(jsPublished, aEvent.getOldRowIndex(), aEvent.getNewRowIndex()));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (sRes != null) {
			return sRes;
		}
		return true;
	}

	@Override
	public void rowsetScrolled(RowsetScrollEvent aEvent) {
		Rowset eventRowset = aEvent.getRowset();
		assert eventRowset == rowset;
		if (aEvent.getNewRowIndex() >= 0 && aEvent.getNewRowIndex() <= eventRowset.size() + 1) {
			try {
				internalExecuteChildren(false);
				// call script method
				Utils.executeScriptEventVoid(jsPublished, onAfterScroll, JsEvents.publishCursorPositionChangedEvent(jsPublished, aEvent.getOldRowIndex(), aEvent.getNewRowIndex()));
			} catch (Exception ex) {
				Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	@Override
	public boolean willChangeRow(RowChangeEvent aEvent) {
		Fields fmdv = getFields();
		if (fmdv != null) {
			Field fmd = fmdv.get(aEvent.getFieldIndex());
			if (fmd != null) {
				// call script method
				Boolean sRes = null;
				try {
					JavaScriptObject publishedRow = publishRowFacade(aEvent.getChangedRow(), this, null);
					sRes = Utils.executeScriptEventBoolean(jsPublished, onBeforeChange,
					        JsEvents.publishEntityInstanceChangeEvent(publishedRow, publishFieldFacade(fmd), Utils.toJs(aEvent.getOldValue()), Utils.toJs(aEvent.getNewValue())));
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
				if (sRes != null) {
					return sRes;
				}
			}
		}
		return true;
	}

	@Override
	public void rowChanged(RowChangeEvent aEvent) {
		try {
			internalExecuteChildren(false, aEvent.getFieldIndex());
			Fields fmdv = getFields();
			if (fmdv != null) {
				Field fmd = fmdv.get(aEvent.getFieldIndex());
				if (fmd != null) {
					// call script method
					JavaScriptObject publishedRow = publishRowFacade(aEvent.getChangedRow(), this, null);
					Utils.executeScriptEventVoid(jsPublished, onAfterChange,
					        JsEvents.publishEntityInstanceChangeEvent(publishedRow, publishFieldFacade(fmd), Utils.toJs(aEvent.getOldValue()), Utils.toJs(aEvent.getNewValue())));
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public boolean willInsertRow(RowsetInsertEvent aEvent) {
		// call script method
		Boolean sRes = null;
		try {
			JavaScriptObject publishedRow = publishRowFacade(aEvent.getRow(), this, null);
			sRes = Utils.executeScriptEventBoolean(jsPublished, onBeforeInsert, JsEvents.publishEntityInstanceInsertEvent(jsPublished, publishedRow));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (sRes != null) {
			return sRes;
		}
		return true;
	}

	@Override
	public boolean willDeleteRow(RowsetDeleteEvent aEvent) {
		// call script method
		Boolean sRes = null;
		try {
			JavaScriptObject publishedRow = publishRowFacade(aEvent.getRow(), this, null);
			sRes = Utils.executeScriptEventBoolean(jsPublished, onBeforeDelete, JsEvents.publishEntityInstanceDeleteEvent(jsPublished, publishedRow));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (sRes != null) {
			return sRes;
		}
		return true;
	}

	@Override
	public void rowInserted(RowsetInsertEvent aEvent) {
		try {
			if (jsPublished != null)
				publishRows(jsPublished);
			internalExecuteChildren(false);
			// call script method
			JavaScriptObject publishedRow = publishRowFacade(aEvent.getRow(), this, null);
			Utils.executeScriptEventVoid(jsPublished, onAfterInsert, JsEvents.publishEntityInstanceInsertEvent(jsPublished, publishedRow));
		} catch (Exception ex) {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void rowDeleted(RowsetDeleteEvent aEvent) {
		try {
			if (jsPublished != null)
				publishRows(jsPublished);
			internalExecuteChildren(false);
			// call script method
			JavaScriptObject publishedRow = publishRowFacade(aEvent.getRow(), this, null);
			Utils.executeScriptEventVoid(jsPublished, onAfterDelete, JsEvents.publishEntityInstanceDeleteEvent(jsPublished, publishedRow));
		} catch (Exception ex) {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void rowsetSorted(RowsetSortEvent event) {
		try {
			if (jsPublished != null)
				publishRows(jsPublished);
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
		try {
            assert rowset != null;
			filterRowset();
			if (jsPublished != null)
				publishRows(jsPublished);
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
		try {
			if (jsPublished != null)
				publishRows(jsPublished);
			if ((!rowset.isBeforeFirst() && !rowset.isAfterLast()) || !rowset.first())
				internalExecuteChildren(false);
			// call script method
			JavaScriptObject publishedEvent = JsEvents.publishSourcedEvent(jsPublished);
			/*
			 * if (model.isPending()) model.enqueueEvent(new
			 * ScriptEvent(jsPublished, this, onFiltered, publishedEvent));
			 * else
			 */
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

	protected void assign(Entity appTarget) throws Exception {
		appTarget.setEntityId(entityId);
		appTarget.setQueryName(queryName);
		appTarget.setTitle(title);
		appTarget.setName(name);
		appTarget.setOnAfterChange(onAfterChange);
		appTarget.setOnAfterDelete(onAfterDelete);
		appTarget.setOnAfterInsert(onAfterInsert);
		appTarget.setOnAfterScroll(onAfterScroll);
		appTarget.setOnFiltered(onFiltered);
		appTarget.setOnRequeried(onRequeried);
		appTarget.setOnBeforeChange(onBeforeChange);
		appTarget.setOnBeforeDelete(onBeforeDelete);
		appTarget.setOnBeforeInsert(onBeforeInsert);
		appTarget.setOnBeforeScroll(onBeforeScroll);
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

	private Locator checkUserLocator(List<Integer> constraints) throws IllegalStateException {
		Locator loc = userLocators.get(constraints);
		if (loc == null) {
			Rowset lrowset = getRowset();
			if (lrowset != null) {
				loc = lrowset.createLocator();
				loc.beginConstrainting();
				try {
					for (int colIdx : constraints) {
						loc.addConstraint(colIdx);
					}
				} finally {
					loc.endConstrainting();
				}
				userLocators.put(constraints, loc);
			}
		}
		return loc;
	}

	// Scriptable rowset interface

	protected Entity substitute;

	public Entity getSubstitute() {
		return substitute;
	}

	public void setSubstitute(Entity aSubstitute) {
		if (aSubstitute != this) {
			substitute = aSubstitute;
		}
	}

	public Object getSubstituteRowsetObject(String aFieldName) throws Exception {
		Entity lsubstitute = substitute;
		while (lsubstitute != null) {
			Rowset sRowset = lsubstitute.getRowset();
			if (sRowset != null && !sRowset.isBeforeFirst() && !sRowset.isAfterLast()) {
				Object value = sRowset.getObject(sRowset.getFields().find(aFieldName));
				if (value != null) {
					return value;
				}
			}
			lsubstitute = lsubstitute.getSubstitute();
		}
		return null;
	}

	public Object getSubstituteRowsetJsObject(int aColIndex) throws Exception {
		if (fields != null) {
			Field field = fields.get(aColIndex);
			if (field != null) {
				Object value = getSubstituteRowsetObject(field.getName());
				return Utils.toJs(value);
			}
		}
		return null;
	}

	protected native Row unwrapRow(JavaScriptObject aRowFacade) throws Exception/*-{
		return aRowFacade != null ? aRowFacade.unwrap() : null;
	}-*/;

	public boolean scrollTo(JavaScriptObject aRowFacade) throws Exception {
		Row row = unwrapRow(aRowFacade);
		return scrollTo(row);
	}

	public boolean scrollTo(Row aRow) throws Exception {
		if (rowset != null && aRow != null) {
			List<Integer> pkIndices = rowset.getFields().getPrimaryKeysIndicies();
			if (!pkIndices.isEmpty()) {
				Locator loc = checkUserLocator(pkIndices);
				Object[] keyValues = new Object[pkIndices.size()];
				for (int i = 0; i < keyValues.length; i++) {
					assert pkIndices.get(i) != null : "Primary keys indices must non null integers";
					int colIndex = pkIndices.get(i);
					keyValues[i] = aRow.getColumnObject(colIndex);
				}
				if (loc.find(keyValues)) {
					return loc.first();
				} else
					return false;
			} else
				throw new IllegalArgumentException("Scrolling possible only for rows with primary keys specified");
		} else
			return false;
	}

	/**
	 * Finds a single row and wraps it in appropriate js object.
	 * 
	 * @param aValue
	 *            Search key value.
	 * @return Wrapped row if it have been found and null otherwise.
	 */
	public JavaScriptObject findById(Object aValue) throws Exception {
		Row result = findRowById(aValue);
		return result != null ? publishRowFacade(result, this, null) : null;
	}
	
	public Row findRowById(Object aValue) throws Exception {
		List<Integer> pkIndicies = getFields().getPrimaryKeysIndicies();
		if (pkIndicies.size() == 1) {
			Object keyValue = Utils.toJava(aValue);
			keyValue = Converter.convert2RowsetCompatible(keyValue, getFields().get(pkIndicies.get(0)).getTypeInfo());
			Locator loc = checkUserLocator(pkIndicies);
			if (loc != null && loc.find(new Object[] { keyValue }))
				return loc.getRow(0);
			else
				return null;
		} else
			throw new IllegalArgumentException("There are must be only one primary key field to be searched on.");
	}

	public static final String BAD_FIND_AGRUMENTS_MSG = "Bad find agruments";
	public static final String BAD_FIND_ARGUMENT_MSG = "Argument at index %d must be a rowset's field.";

	/**
	 * Finds set of rows and wraps it in appropriate js objects.
	 * 
	 * @param aValues
	 *            Search key fields and key values.
	 * @return js array of wrapped rows.
	 * @throws RowsetException
	 * @throws IllegalStateException
	 */
	public JavaScriptObject find(JavaScriptObject aValues) throws Exception {
		if (aValues != null) {
			Fields fields = getFields();
			List<Integer> constraints = new ArrayList<Integer>();
			List<Object> keyValues = new ArrayList<Object>();
			if (aValues.<JsObject>cast().isArray()) {
				JsArrayMixed values = aValues.<JsArrayMixed> cast();
				if(values.length() % 2 == 0){
					for (int i = 0; i < values.length(); i += 2) {
						int colIndex = 0;
						DataTypeInfo typeInfo = null;
						try {
							JavaScriptObject jsField = values.getObject(i);
							Field field = RowsetUtils.unwrapField(jsField);
							colIndex = fields.find(field.getName());
							typeInfo = field.getTypeInfo();
						} catch (Exception ex) {
							String possibleFiledName = values.getString(i);
							Field field = fields.get(possibleFiledName);
							if(field != null){
								colIndex = fields.find(possibleFiledName);
								typeInfo = field.getTypeInfo();
							}else{
								colIndex = Double.valueOf(values.getNumber(i)).intValue();
								field = fields.get(colIndex);
								typeInfo = field.getTypeInfo();
							}
						}
						// field col index
						constraints.add(colIndex);
						// corresponding value
						Object keyValue = RowsetUtils.extractValueFromJsArray(values, i + 1);
						keyValues.add(Converter.convert2RowsetCompatible(keyValue, typeInfo));
					}
				} else {
					Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, BAD_FIND_AGRUMENTS_MSG);
				}
			}else{
				JsArrayString jsKeys = aValues.<JsObject>cast().keys();
				for(int i = 0; i < jsKeys.length(); i++){
					String jsKey = jsKeys.get(i);
					int colIndex = fields.find(jsKey);
					Field field = fields.get(colIndex);
					DataTypeInfo typeInfo = field.getTypeInfo();
					// field col index
					constraints.add(colIndex);
					// corresponding value
					Object keyValue = Utils.toJava(aValues.<JsObject>cast().getJava(jsKey));
					keyValues.add(Converter.convert2RowsetCompatible(keyValue, typeInfo));
				}
			}
			Locator loc = checkUserLocator(constraints);
			if (loc.find(keyValues.toArray())) {
                HashOrderer.TaggedList<RowWrap> subset = loc.getSubSet();
                if (subset.tag == null) {
            		JsArray<JavaScriptObject> arFound = JavaScriptObject.createArray().<JsArray<JavaScriptObject>> cast();
    				for (RowWrap rw : subset) {
    					arFound.push(publishRowFacade(rw.getRow(), this, null));
    				}
                    subset.tag = arFound; 
                }
                assert subset.tag instanceof JavaScriptObject;
                return (JavaScriptObject) subset.tag;
				
			}
		} else {
			Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, BAD_FIND_AGRUMENTS_MSG);
		}
		return JavaScriptObject.createArray();
	}

	public Row find(int aColIndex, Object aValue) throws Exception {
		List<Integer> constraints = new ArrayList<Integer>();
		List<Object> keyValues = new ArrayList<Object>();
		DataTypeInfo typeInfo = getFields().get(aColIndex).getTypeInfo();
		// field col index
		constraints.add(aColIndex);
		// correponding value
		keyValues.add(Converter.convert2RowsetCompatible(aValue, typeInfo));
		Locator loc = checkUserLocator(constraints);
		if (loc.find(keyValues.toArray()) && loc.getSize() > 0) {
			return loc.getRow(0);
		} else
			return null;
	}

	public JavaScriptObject createLocator(JavaScriptObject aConstraints) throws Exception {
		Fields fields = getFields();
		Locator loc = getRowset().createLocator();
		loc.beginConstrainting();
		try {
			JsArrayMixed constraints = aConstraints.<JsArrayMixed> cast();
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
				loc.addConstraint(colIndex);
			}
		} finally {
			loc.endConstrainting();
		}
		return publishLocatorFacade(loc, this);
	}

	public JavaScriptObject createFilter(JavaScriptObject aConstraints) throws Exception {
		JsArrayMixed constraints = aConstraints.<JsArrayMixed> cast();
		Fields fields = getFields();
		Filter filter = getRowset().createFilter();
		filter.beginConstrainting();
		try {
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
				filter.addConstraint(colIndex);
			}
		} finally {
			filter.endConstrainting();
		}
		return publishFilterFacade(filter, this);
	}

	public RowsComparator createSorting(JavaScriptObject aConstraints) {
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

	public static boolean isParameter(Field aField) {
		return aField instanceof Parameter;
	}

	protected native static int invokeComparatorFunc(JavaScriptObject aComparatorFun, JavaScriptObject row1, JavaScriptObject row2) throws Exception/*-{
		return aComparatorFun(row1, row2);
	}-*/;

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

			@Override
			public int compare(Row row1, Row row2) {
				// Row row1 = (Row)arg0;
				// Row row2 = (Row)arg1;
				try {
					return invokeComparatorFunc(aComparatorFunc, publishRowFacade(row1, Entity.this, null), publishRowFacade(row2, Entity.this, null));
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}

		};
		rowset.sort(comparator);
	}

	public void insert(JavaScriptObject aValues) throws Exception {
		JsArrayMixed fieldsValues = aValues.<JsArrayMixed> cast();
		Object[] initingValues = new Object[fieldsValues.length()];
		for (int i = 0; i < initingValues.length; i += 2) {
			// field
			try {
				initingValues[i] = RowsetUtils.unwrapField(fieldsValues.getObject(i));
			} catch (Exception ex) {
				initingValues[i] = RowsetUtils.extractValueFromJsArray(fieldsValues, i);
			}
			// value
			initingValues[i + 1] = RowsetUtils.extractValueFromJsArray(fieldsValues, i + 1);
		}
		rowset.insert(initingValues);
	}

	/**
	 * 
	 * @param aValues
	 *            JavaScript array containing duplets of field name and field value.
	 * @param aIndex
	 *            Index new row will be inserted at. 1 - based.
	 */
	public void insertAt(int aIndex, JavaScriptObject aValues, JavaScriptObject aTarget) throws Exception {
		JsArrayMixed fieldsValues = aValues.<JsArrayMixed> cast();
		Object[] initingValues = new Object[fieldsValues.length()];
		for (int i = 0; i < initingValues.length; i += 2) {
			// field
			try {
				initingValues[i] = RowsetUtils.unwrapField(fieldsValues.getObject(i));
			} catch (Exception ex) {
				initingValues[i] = RowsetUtils.extractValueFromJsArray(fieldsValues, i);
			}
			// value
			initingValues[i + 1] = RowsetUtils.extractValueFromJsArray(fieldsValues, i + 1);
		}
		Row inserted = new Row();
		inserted.setFields(fields);
		publishRowFacade(inserted, this, aTarget);
		rowset.insertAt(inserted, false, aIndex, initingValues);
	}

	public void setPublished(JavaScriptObject aPublished) {
		if(jsPublished != aPublished){
			jsPublished = aPublished;
			publishEntityFacade(this);
		}
	}

	@Override
	public JavaScriptObject getPublished() {
		return jsPublished;
	}
	
	public JavaScriptObject getElementClass() {
	    return jsElementClass;
    }
	
	public void setElementClass(JavaScriptObject aValue) {
	    jsElementClass = aValue;
    }
}
