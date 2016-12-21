package com.eas.model;

import com.google.gwt.core.client.JavaScriptObject;

public class JsModel {
	
	public static JavaScriptObject managed;
	public static JavaScriptObject orderer;

	public native static void init()/*-{
		
		function predefine(aDeps, aName, aDefiner){
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}
		
		predefine([], 'managed', function(){
		    var releaseName = '-platypus-orm-release-func';
		     // Substitutes properties of anObject with observable properties using Object.defineProperty().
		     // ES6 Object.observe() is not suitable for ordering/orm tasks
		     // because of asynchonous nature of its events.
		     // @param anObject An object to be reorganized.
		     // @param aOnChange a callback called on every change of properties.
		     // @param aOnChange a callback called on before every change of properties.
		     // @returns anObject by pass for convinence.
		    function manageObject(anObject, aOnChange, aOnBeforeChange) {
		        if (!anObject[releaseName]) {
		            var container = {};
		            for (var p in anObject) {
		                container[p] = anObject[p];
		                (function () {
		                    var _p = p;
		                    Object.defineProperty(anObject, ('' + _p), {
		                        enumerable: true,
		                        configurable: true,
		                        get: function () {
		                            return container[_p];
		                        },
		                        set: function (aValue) {
		                            var _oldValue = container[_p];
		                            if(_oldValue != aValue){
			                            var _beforeState = null;
			                            if(aOnBeforeChange)
			                                _beforeState = aOnBeforeChange(anObject, {source: anObject, propertyName: _p, oldValue: _oldValue, newValue: aValue});
			                            container[_p] = aValue;
			                            aOnChange(anObject, {source: anObject, propertyName: _p, oldValue: _oldValue, newValue: aValue, beforeState: _beforeState});
		                            }
		                        }
		                    });
		                })();
		            }
		            Object.defineProperty(anObject, releaseName, {
		                configurable: true,
		                value: function () {
		                    delete anObject[releaseName];
		                    for (var p in anObject) {
		                        var pValue = anObject[p];
		                        delete anObject[p];
		                        anObject[p] = pValue;
		                    }
		                }});
		        }
		        return {release: function () {
		                anObject[releaseName]();
		            }};
		    }
		
		    function unmanageObject(anObject) {
		        if (anObject[releaseName]) {
		            anObject[releaseName]();
		        }
		    }
		    function manageArray(aTarget, aOnChange) {
		        Object.defineProperty(aTarget, "pop", {
		            value: function () {
		                var popped = Array.prototype.pop.call(aTarget);
		                if (popped) {
		                    aOnChange.spliced([], [popped]);
		                    if (popped === cursor) {
		                        aTarget.cursor = aTarget.length > 0 ? aTarget[aTarget.length - 1] : null;
		                    }
		                }
		                return popped;
		            }
		        });
		        Object.defineProperty(aTarget, "shift", {
		            value: function () {
		                var shifted = Array.prototype.shift.call(aTarget);
		                if (shifted) {
		                    aOnChange.spliced([], [shifted]);
		                    if (shifted === cursor) {
		                        aTarget.cursor = aTarget.length > 0 ? aTarget[0] : null;
		                    }
		                }
		                return shifted;
		            }
		        });
		        Object.defineProperty(aTarget, "push", {
		            value: function () {
		                var newLength = Array.prototype.push.apply(aTarget, arguments);
		                var added = [];
		                for (var a = 0; a < arguments.length; a++) {
		                    added.push(arguments[a]);
		                }
		                aOnChange.spliced(added, []);
		                if (added.length > 0)
		                    aTarget.cursor = added[added.length - 1];
		                return newLength;
		            }
		        });
		        Object.defineProperty(aTarget, "unshift", {
		            value: function () {
		                var newLength = Array.prototype.unshift.apply(aTarget, arguments);
		                var added = [];
		                for (var a = 0; a < arguments.length; a++) {
		                    added.push(arguments[a]);
		                }
		                aOnChange.spliced(added, []);
		                if (added.length > 0)
		                    aTarget.cursor = added[added.length - 1];
		                return newLength;
		            }
		        });
		        Object.defineProperty(aTarget, "reverse", {
		            value: function () {
		                var reversed = Array.prototype.reverse.apply(aTarget);
		                if (aTarget.length > 0) {
		                    aOnChange.spliced([], []);
		                }
		                return reversed;
		            }
		        });
		        Object.defineProperty(aTarget, "sort", {
		            value: function () {
		                var sorted = Array.prototype.sort.apply(aTarget, arguments);
		                if (aTarget.length > 0) {
		                    aOnChange.spliced([], []);
		                }
		                return sorted;
		            }
		        });
		        Object.defineProperty(aTarget, "splice", {
		            value: function () {
		                var beginDeleteAt = arguments[0];
		                if(beginDeleteAt < 0)
		                    beginDeleteAt = aTarget.length - beginDeleteAt;
		                var deleted = Array.prototype.splice.apply(aTarget, arguments);
		                var added = [];
		                for (var a = 2; a < arguments.length; a++) {
		                    var aAdded = arguments[a];
		                    added.push(aAdded);
		                }
		                aOnChange.spliced(added, deleted);
		                if (added.length > 0) {
		                    aTarget.cursor = added[added.length - 1];
		                } else {
		                    if (deleted.indexOf(cursor) !== -1){
		                        if(beginDeleteAt >= 0 && beginDeleteAt < aTarget.length)
		                            aTarget.cursor = aTarget[beginDeleteAt];
		                        else if(beginDeleteAt - 1 >= 0 && beginDeleteAt - 1 < aTarget.length)
		                            aTarget.cursor = aTarget[beginDeleteAt - 1];
		                        else
		                            aTarget.cursor = null;
		                    }
		                }
		                return deleted;
		            }
		        });
		        var cursor = null;
		        Object.defineProperty(aTarget, 'cursor', {
		            get: function () {
		                return cursor;
		            },
		            set: function (aValue) {
		                if (cursor !== aValue) {
		                    var oldCursor = cursor;
		                    cursor = aValue;
		                    aOnChange.scrolled(aTarget, oldCursor, cursor);
		                }
		            }
		        });
		        return aTarget;
		    }
		    var module = {};
		    Object.defineProperty(module, 'manageObject', {
		    	enumerable: true,
		    	value: manageObject
		    });
		    Object.defineProperty(module, 'unmanageObject', {
		    	enumerable: true,
		    	value: unmanageObject
		    });
		    Object.defineProperty(module, 'manageArray', {
		    	enumerable: true,
		    	value: manageArray
		    });
		    @com.eas.model.JsModel::managed = module;
		    return module;
		});
		
		predefine([], 'orderer', function(){
			function Orderer(aKeysNames) {
			    var keyNames = aKeysNames.sort();
			    keyNames.forEach(function (aKeyName) {
			    });
			    function calcKey(anObject) {
			        var key = '';
			        keyNames.forEach(function (aKeyName) {
			            var datum = anObject[aKeyName];
			            if (key.length > 0)
			                key += ' | ';
			            key += datum instanceof Date ? $wnd.JSON.stringify(datum) : '' + datum;
			        });
			        return key;
			    }
			
			    this.inKeys = function (aKeyName) {
			        return keyNames.indexOf(aKeyName) !== -1;
			    };
			
			    var map = {};
			    this.add = function (anObject) {
			        var key = calcKey(anObject);
			        var subset = map[key];
			        if (!subset) {
			            subset = new Set();
			            map[key] = subset;
			        }
			        subset.add(anObject);
			    };
			    this['delete'] = function (anObject) {
			        var key = calcKey(anObject);
			        var subset = map[key];
			        if (subset) {
			            subset['delete'](anObject);
			            if (subset.size === 0) {
			                delete map[key];
			            }
			        }
			    };
			    this.find = function (aCriteria) {
			        var key = calcKey(aCriteria);
			        var subset = map[key];
			        if (!subset) {
			            return [];
			        } else {
			            var found = [];
			            subset.forEach(function (item) {
			                found.push(item);
			            });
			            return found;
			        }
			    };
			}
		    @com.eas.model.JsModel::orderer = Orderer;
			return Orderer;
		});
		
		predefine([], 'orm', function(){
			function requireEntities(aEntities, aOnSuccess, aOnFailure){
				var entities;
				if(!Array.isArray(aEntities)){
					aEntities = aEntities + "";
					if(aEntities.length > 5 && aEntities.trim().substring(0, 5).toLowerCase() === "<?xml"){
						entities = [];
						var pattern = /queryId="(.+?)"/ig;
						var groups;
						while((groups = pattern.exec(aEntities)) != null){
							if(groups.length > 1){
								entities.push(groups[1]);
							}
						}
					}else{
						entities = [aEntities];
					}
				}else{
					entities = aEntities;
				}
				@com.eas.application.Loader::jsLoadQueries(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(entities, aOnSuccess, aOnFailure);
			}
			function readModelDocument(aDocument, aModuleName, aTarget){
				if(!aTarget)
					aTarget = {};
				var nativeModel = @com.eas.model.store.XmlDom2Model::transform(Lcom/google/gwt/xml/client/Document;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDocument, aModuleName, aTarget);
				if(nativeModel){
					nativeModel.@com.eas.model.Model::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(aTarget);			
					return aTarget;
				}else{
					return null;
				}
			}
			function readModel(aModelContent, aTarget){
				var doc = @com.google.gwt.xml.client.XMLParser::parse(Ljava/lang/String;)(aModelContent ? aModelContent + "" : "");
				return readModelDocument(doc, null, aTarget);
			}
			function loadModel(aModuleName, aTarget) {
				var aClient = @com.eas.client.AppClient::getInstance()();
				var modelDoc = aClient.@com.eas.client.AppClient::getModelDocument(Ljava/lang/String;)(aModuleName);
				if(modelDoc){
					return readModelDocument(modelDoc, aModuleName, aTarget);
				} else {
					throw 'Model definition for module "' + aModuleName + '" is not found';
				}
			}		
            var module = {};
            Object.defineProperty(module, 'loadModel', {
                enumerable: true,
                value: loadModel
            });
            Object.defineProperty(module, 'readModel', {
                enumerable: true,
                value: readModel
            });
            Object.defineProperty(module, 'requireEntities', {
                enumerable: true,
                value: requireEntities
            });
            return module;
		});
		
	}-*/;
}
