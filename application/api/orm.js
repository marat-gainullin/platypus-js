/* global Java, Function */
define(['logger', 'boxing', 'managed', 'orderer', 'datamodel/application-db-model', 'datamodel/application-platypus-model'
            , 'datamodel/application-db-entity', 'datamodel/application-platypus-entity', 'core/index', 'datamodel/index']
        , function (Logger, B, M, Orderer, ApplicationDbModel, ApplicationPlatypusModel, ApplicationDbEntity, ApplicationPlatypusEntity, Core, Datamodel) {
            var ScriptsClass = Java.type("com.eas.script.Scripts");
            var aSpace = ScriptsClass.getSpace();
            // core imports
            var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
            var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
            var JavaStringArrayClass = Java.type("java.lang.String[]");
            var Source2XmlDom = Java.type('com.eas.xml.dom.Source2XmlDom');
            var FieldsClassName = "com.eas.client.metadata.Fields";
            var ParametersClassName = "com.eas.client.metadata.Parameters";
            var ModelLoaderClass = Java.type('com.eas.client.scripts.ApplicationModelLoader');
            var TwoTierModelClass = Java.type('com.eas.client.model.application.ApplicationDbModel');
            var ThreeTierModelClass = Java.type('com.eas.client.model.application.ApplicationPlatypusModel');

            function fieldsAndParametersPublisher(aDelegate) {
                var target = {};
                var nnFields = aDelegate.toCollection();
                var lengthMet = false;
                for (var n = 0; n < nnFields.size(); n++) {
                    (function () {
                        var nField = nnFields[n];
                        var pField = EngineUtilsClass.unwrap(nField.getPublished());
                        if ('length' == nField.name)
                            lengthMet = true;
                        Object.defineProperty(target, nField.name, {
                            value: pField
                        });
                        Object.defineProperty(target, n, {
                            value: pField
                        });
                    })();
                }
                if (!lengthMet)
                    Object.defineProperty(target, 'length', {
                        value: nnFields.size()
                    });
                return target;
            }

            aSpace.putPublisher(ParametersClassName, fieldsAndParametersPublisher);
            aSpace.putPublisher(FieldsClassName, fieldsAndParametersPublisher);

            var addListenerName = '-platypus-listener-add-func';
            var removeListenerName = '-platypus-listener-remove-func';
            var fireChangeName = '-platypus-change-fire-func';
            function listenable(aTarget) {
                var listeners = new Set();
                Object.defineProperty(aTarget, addListenerName, {value: function (aListener) {
                        listeners.add(aListener);
                    }});
                Object.defineProperty(aTarget, removeListenerName, {value: function (aListener) {
                        listeners.delete(aListener);
                    }});
                Object.defineProperty(aTarget, fireChangeName, {value: function (aChange) {
                        Object.freeze(aChange);
                        var _listeners = [];
                        listeners.forEach(function (aListener) {
                            _listeners.push(aListener);
                        });
                        _listeners.forEach(function (aListener) {
                            aListener(aChange);
                        });
                    }});
                return function () {
                    unlistenable(aTarget);
                };
            }

            function unlistenable(aTarget) {
                delete aTarget[addListenerName];
                delete aTarget[removeListenerName];
            }

            function listen(aTarget, aListener) {
                if (aTarget[addListenerName]) {
                    aTarget[addListenerName](aListener);
                    return function () {
                        aTarget[removeListenerName](aListener);
                    };
                } else {
                    return null;
                }
            }

            function unlisten(aTarget, aListener) {
                aTarget[removeListenerName](aListener);
            }

            function fire(aTarget, aChange) {
                try {
                    aTarget[fireChangeName](aChange);
                } catch (e) {
                    Logger.severe(e);
                }
            }

            function listenElements(aData, aPropListener) {
                function subscribe(aData, aListener) {
                    return listen(aData, aListener);
                }
                var subscribed = [];
                for (var i = 0; i < aData.length; i++) {
                    var remover = subscribe(aData[i], aPropListener);
                    if (remover) {
                        subscribed.push(remover);
                    }
                }
                return {
                    unlisten: function () {
                        subscribed.forEach(function (aEntry) {
                            aEntry();
                        });
                    }
                };
            }
            aSpace.setListenElementsFunc(listenElements);

            function listenInstance(aTarget, aPath, aPropListener) {
                if (aTarget[addListenerName]) {
                    function subscribe(aData, aListener, aPropName) {
                        return listen(aData, function (aChange) {
                            if (!aPropName || aChange.propertyName === aPropName) {
                                aListener(aChange);
                            }
                        });
                    }
                    var subscribed = [];
                    function listenPath() {
                        subscribed = [];
                        var data = aTarget;
                        var path = aPath.split(".");
                        for (var i = 0; i < path.length; i++) {
                            var propName = path[i];
                            var listener = i === path.length - 1 ? aPropListener : function (aChange) {
                                subscribed.forEach(function (aEntry) {
                                    aEntry();
                                });
                                listenPath();
                                aPropListener(aChange);
                            };
                            var cookie = subscribe(data, listener, propName);
                            if (cookie) {
                                subscribed.push(cookie);
                                if (data[propName])
                                    data = data[propName];
                                else
                                    break;
                            } else {
                                break;
                            }
                        }
                    }
                    if (aTarget) {
                        listenPath();
                    }
                    return {
                        unlisten: function () {
                            subscribed.forEach(function (aEntry) {
                                aEntry();
                            });
                        }
                    };
                } else {
                    return null;
                }
            }
            aSpace.setListenFunc(listenInstance);

            function fireSelfScalarsOppositeCollectionsChanges(aSubject, aChange, nFields) {
                var ormDefs = nFields.getOrmScalarExpandings().get(aChange.propertyName);
                if (ormDefs) {
                    var expandingsOldValues = aChange.beforeState.selfScalarsOldValues;
                    ormDefs.forEach(function (ormDef) {
                        if (ormDef.getName()) {
                            var expandingOldValue = expandingsOldValues[ormDef.getName()];
                            var expandingNewValue = aSubject[ormDef.getName()];
                            fire(aSubject, {source: aChange.source, propertyName: ormDef.getName(), oldValue: expandingOldValue, newValue: expandingNewValue});
                            if (ormDef.getOppositeName()) {
                                if (expandingOldValue) {
                                    fire(expandingOldValue, {source: expandingOldValue, propertyName: ormDef.getOppositeName()});
                                }
                                if (expandingNewValue) {
                                    fire(expandingNewValue, {source: expandingNewValue, propertyName: ormDef.getOppositeName()});
                                }
                            }
                        }
                    });
                }
            }

            function prepareSelfScalarsChanges(aSubject, aChange, nFields) {
                var ormDefs = nFields.getOrmScalarExpandings().get(aChange.propertyName);
                var oldScalarValues = [];
                if (ormDefs) {
                    ormDefs.forEach(function (ormDef) {
                        if (ormDef && ormDef.getName()) {
                            oldScalarValues[ormDef.getName()] = aSubject[ormDef.getName()];
                        }
                    });
                }
                return oldScalarValues;
            }

            function fireOppositeScalarsSelfCollectionsChanges(aSubject, aChange, nFields) {
                var oppositeScalarsFirerers = aChange.beforeState.oppositeScalarsFirerers;
                if (oppositeScalarsFirerers) {
                    oppositeScalarsFirerers.forEach(function (aFirerer) {
                        aFirerer();
                    });
                }
                var collectionsDefs = nFields.getOrmCollectionsDefinitions().entrySet();
                if (collectionsDefs) {
                    collectionsDefs.forEach(function (aEntry) {
                        var collectionName = aEntry.getKey();
                        var ormDef = aEntry.getValue();
                        var collection = aSubject[collectionName];
                        collection.forEach(function (item) {
                            fire(item, {source: item, propertyName: ormDef.getOppositeName()});
                        });
                    });
                    collectionsDefs.forEach(function (aEntry) {
                        var collectionName = aEntry.getKey();
                        fire(aSubject, {source: aSubject, propertyName: collectionName});
                    });
                }
            }

            function prepareOppositeScalarsChanges(aSubject, nFields) {
                var firerers = [];
                var collectionsDefs = nFields.getOrmCollectionsDefinitions().entrySet();
                collectionsDefs.forEach(function (aEntry) {
                    var collectionName = aEntry.getKey();
                    var ormDef = aEntry.getValue();
                    var collection = aSubject[collectionName];
                    collection.forEach(function (item) {
                        if (ormDef.getOppositeName()) {
                            firerers.push(function () {
                                fire(item, {source: item, propertyName: ormDef.getOppositeName()});
                            });
                        }
                    });
                });
                return firerers;
            }

            function fireOppositeScalarsChanges(aSubject, nFields) {
                var collected = prepareOppositeScalarsChanges(aSubject, nFields);
                collected.forEach(function (aFirerer) {
                    aFirerer();
                });
            }

            function fireOppositeCollectionsChanges(aSubject, nFields) {
                var scalarsDefs = nFields.getOrmScalarDefinitions().entrySet();
                scalarsDefs.forEach(function (aEntry) {
                    var scalarName = aEntry.getKey();
                    if (scalarName) {
                        var ormDef = aEntry.getValue();
                        var scalar = aSubject[scalarName];
                        if (scalar && ormDef.getOppositeName()) {
                            fire(scalar, {source: scalar, propertyName: ormDef.getOppositeName()});
                        }
                    }
                });
            }

            function generateChangeLogKeys(keys, nFields, propName, aSubject, oldValue) {
                if (nFields) {
                    for (var i = 1; i <= nFields.getFieldsCount(); i++) {
                        var field = nFields.get(i);
                        if (field.isPk()) {
                            var fieldName = field.getName();
                            var value = aSubject[fieldName];
                            // Some tricky processing of primary keys modification case ...
                            if (fieldName === propName) {
                                value = oldValue;
                            }
                            keys.add(new ValueClass(fieldName, value));
                        }
                    }
                }
            }

            aSpace.setCollectionDefFunc(
                    function (sourcePublishedEntity, targetFieldName, sourceFieldName) {
                        var _self = this;
                        _self.enumerable = false;
                        _self.configurable = true;
                        _self.get = function () {
                            var criterion = {};
                            var targetKey = this[targetFieldName];
                            criterion[sourceFieldName] = targetKey;
                            var found = sourcePublishedEntity.find(criterion);
                            M.manageArray(found, {
                                spliced: function (added, deleted) {
                                    added.forEach(function (item) {
                                        item[sourceFieldName] = targetKey;
                                    });
                                    deleted.forEach(function (item) {
                                        item[sourceFieldName] = null;
                                    });
                                    fire(found, {source: found, propertyName: 'length'});
                                },
                                scrolled: function (aSubject, oldCursor, newCursor) {
                                    fire(found, {source: found, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
                                }
                            });
                            listenable(found);
                            return found;
                        };
                    });

            var InsertClass = Java.type('com.eas.client.changes.Insert');
            var DeleteClass = Java.type('com.eas.client.changes.Delete');
            var UpdateClass = Java.type('com.eas.client.changes.Update');
            var ValueClass = Java.type('com.eas.client.changes.ChangeValue');

            function loadModelDocument(aDocument, aTarget) {
                var model = ModelLoaderClass.load(aDocument, ScriptedResourceClass.getApp());
                var modelCTor;
                if (model instanceof TwoTierModelClass) {
                    modelCTor = ApplicationDbModel;
                } else if (model instanceof ThreeTierModelClass) {
                    modelCTor = ApplicationPlatypusModel;
                } else {
                    throw "Can't determine model's type.";
                }
                if (aTarget) {
                    modelCTor.call(aTarget, model);
                } else {
                    aTarget = new modelCTor(model);
                }
                function publishEntity(nEntity) {
                    var entityCTor;
                    if (model instanceof TwoTierModelClass) {
                        entityCTor = ApplicationDbEntity;
                    } else if (model instanceof ThreeTierModelClass) {
                        entityCTor = ApplicationPlatypusEntity;
                    } else {
                        throw "Can't determine model's type.";
                    }
                    var justInserted = null;
                    var justInsertedChange = null;
                    var orderers = {};
                    var published = [];

                    function managedOnChange(aSubject, aChange) {
                        if (!tryToComplementInsert(aSubject, aChange)) {
                            var updateChange = new UpdateClass(nEntity.getQueryName());
                            generateChangeLogKeys(updateChange.keys, nFields, aChange.propertyName, aSubject, aChange.oldValue);
                            updateChange.data.add(new ValueClass(aChange.propertyName, aChange.newValue));
                            nEntity.getChangeLog().add(updateChange);
                        }
                        Object.keys(orderers).forEach(function (aOrdererKey) {
                            var aOrderer = orderers[aOrdererKey];
                            if (aOrderer.inKeys(aChange.propertyName)) {
                                aOrderer.add(aChange.source);
                            }
                        });
                        fire(aSubject, aChange);
                        fireSelfScalarsOppositeCollectionsChanges(aSubject, aChange, nFields);// Expanding change
                        var field = noFields[aChange.propertyName];
                        if (field && field.pk) {
                            fireOppositeScalarsSelfCollectionsChanges(aSubject, aChange, nFields);
                        }
                    }
                    function managedBeforeChange(aSubject, aChange) {
                        var oldScalars = prepareSelfScalarsChanges(aSubject, aChange, nFields);
                        var oppositeScalarsFirerers = prepareOppositeScalarsChanges(aSubject, nFields);
                        Object.keys(orderers).forEach(function (aOrdererKey) {
                            var aOrderer = orderers[aOrdererKey];
                            if (aOrderer.inKeys(aChange.propertyName)) {
                                aOrderer.delete(aChange.source);
                            }
                        });
                        return {selfScalarsOldValues: oldScalars, oppositeScalarsFirerers: oppositeScalarsFirerers};
                    }
                    function tryToComplementInsert(aSubject, aChange) {
                        var complemented = false;
                        if (aSubject === justInserted && noFields[aChange.propertyName] && !noFields[aChange.propertyName].nullable) {
                            var met = false;
                            for (var d = 0; d < justInsertedChange.data.length; d++) {
                                var iv = justInsertedChange.data[d];
                                if (iv.name == aChange.propertyName) {
                                    met = true;
                                    break;
                                }
                            }
                            if (!met) {
                                justInsertedChange.getData().add(new ValueClass(aChange.propertyName, aChange.newValue));
                                complemented = true;
                            }
                        }
                        return complemented;
                    }
                    function acceptInstance(aSubject, aReassignOrmValues) {
                        Object.keys(noFields).forEach(function (aFieldName) {
                            if (typeof aSubject[aFieldName] === 'undefined')
                                aSubject[aFieldName] = null;
                        });
                        if (aReassignOrmValues) {
                            var scalarsContainer = {};
                            for each (var defsEntry in nFields.getOrmScalarDefinitions().entrySet()) {
                                var sd = defsEntry.getKey();
                                if (typeof aSubject[sd] !== 'undefined') {
                                    scalarsContainer[sd] = aSubject[sd];
                                    aSubject[sd] = null;
                                }
                            }
                            var collectionsContainer = {};
                            for each (var defsEntry in nFields.getOrmCollectionsDefinitions().entrySet()) {
                                var cd = defsEntry.getKey();
                                if (typeof aSubject[cd] !== 'undefined') {
                                    collectionsContainer[cd] = aSubject[cd];
                                    aSubject[cd] = null;
                                }
                            }
                        }
                        M.manageObject(aSubject, managedOnChange, managedBeforeChange);
                        listenable(aSubject);
                        // ORM mutable scalar and collection properties
                        for each (var defsEntry in nFields.getOrmScalarDefinitions().entrySet()) {
                            var jsDef = EngineUtilsClass.unwrap(defsEntry.getValue().getJsDef());
                            var sd = defsEntry.getKey();
                            Object.defineProperty(aSubject, sd, jsDef);
                            if (aReassignOrmValues && typeof scalarsContainer[sd] !== 'undefined') {
                                aSubject[sd] = scalarsContainer[sd];
                            }
                        }
                        for each (var defsEntry in nFields.getOrmCollectionsDefinitions().entrySet()) {
                            var jsDef = EngineUtilsClass.unwrap(defsEntry.getValue().getJsDef());
                            var cd = defsEntry.getKey();
                            Object.defineProperty(aSubject, cd, jsDef);
                            if (aReassignOrmValues && typeof collectionsContainer[cd] !== 'undefined') {
                                var definedCollection = aSubject[cd];
                                var savedCollection = collectionsContainer[cd];
                                if (definedCollection && savedCollection && savedCollection.length > 0) {
                                    for (var pi = 0; pi < savedCollection.length; pi++)
                                        definedCollection.push(savedCollection[pi]);
                                }
                            }
                        }
                    }

                    var _onInserted = null;
                    var _onDeleted = null;
                    var _onScrolled = null;
                    M.manageArray(published, {
                        spliced: function (added, deleted) {
                            added.forEach(function (aAdded) {
                                justInserted = aAdded;
                                justInsertedChange = new InsertClass(nEntity.getQueryName());
                                for (var nf = 0; nf < nnFields.size(); nf++) {
                                    var nField = nnFields[nf];
                                    if (!aAdded[nField.name] && nField.pk) {
                                        aAdded[nField.name] = nField.generateValue();
                                    }
                                }
                                for (var na in aAdded) {
                                    var nField = noFields[na];
                                    if (nField) {
                                        var v = aAdded[na];
                                        var cv = new ValueClass(nField.name, v);
                                        justInsertedChange.data.add(cv);
                                    }
                                }
                                nEntity.getChangeLog().add(justInsertedChange);
                                for (var aOrdererKey in orderers) {
                                    var aOrderer = orderers[aOrdererKey];
                                    aOrderer.add(aAdded);
                                }
                                acceptInstance(aAdded, true);
                                fireOppositeScalarsChanges(aAdded, nFields);
                                fireOppositeCollectionsChanges(aAdded, nFields);
                            });
                            deleted.forEach(function (aDeleted) {
                                if (aDeleted === justInserted) {
                                    justInserted = null;
                                    justInsertedChange = null;
                                }
                                var deleteChange = new DeleteClass(nEntity.getQueryName());
                                generateChangeLogKeys(deleteChange.keys, nFields, null, aDeleted, null);
                                nEntity.getChangeLog().add(deleteChange);
                                for (var aOrdererKey in orderers) {
                                    var aOrderer = orderers[aOrdererKey];
                                    aOrderer.delete(aDeleted);
                                }
                                fireOppositeScalarsChanges(aDeleted, nFields);
                                fireOppositeCollectionsChanges(aDeleted, nFields);
                                unlistenable(aDeleted);
                                M.unmanageObject(aDeleted);
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
                            fire(published, {source: published, propertyName: 'length'});
                        },
                        scrolled: function (aSubject, oldCursor, newCursor) {
                            if (_onScrolled) {
                                try {
                                    _onScrolled({source: published, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
                                } catch (e) {
                                    Logger.severe(e);
                                }
                            }
                            fire(published, {source: published, propertyName: 'cursor', oldValue: oldCursor, newValue: newCursor});
                        }
                    });
                    var pSchema = {};
                    Object.defineProperty(published, "schema", {
                        value: pSchema
                    });
                    var pkFieldName = '';
                    var nFields = nEntity.getFields();
                    var nnFields = nFields.toCollection();
                    var noFields = {};
                    // schema
                    var pSchemaLengthMet = false;
                    for (var n = 0; n < nnFields.size(); n++) {
                        (function () {
                            var nField = nnFields[n];
                            noFields[nField.name] = nField;
                            if (nField.isPk())
                                pkFieldName = nField.name;
                            var schemaDesc = {
                                value: nField.getPublished()
                            };
                            if ('length' == nField.name)
                                pSchemaLengthMet = true;
                            if (!pSchema[nField.name]) {
                                Object.defineProperty(pSchema, nField.name, schemaDesc);
                            } else {
                                var eTitle = nEntity.title ? " [" + nEntity.title + "]" : "";
                                throw "Duplicated field name found: " + nField.name + " in entity " + nEntity.name + eTitle;
                            }
                            Object.defineProperty(pSchema, n, schemaDesc);
                        })();
                    }
                    if (!pSchemaLengthMet) {
                        Object.defineProperty(pSchema, 'length', {value: nnFields.size()});
                    }
                    // entity.params.p1 syntax
                    var nParameters = nEntity.getQuery().getParameters();
                    var ncParameters = nParameters.toCollection();
                    var pParams = {};
                    for (var p = 0; p < ncParameters.size(); p++) {
                        (function () {
                            var nParameter = ncParameters[p];
                            var pDesc = {
                                get: function () {
                                    return nParameter.jsValue;
                                },
                                set: function (aValue) {
                                    nParameter.jsValue = aValue;
                                }
                            };
                            Object.defineProperty(pParams, nParameter.name, pDesc);
                            Object.defineProperty(pParams, p, pDesc);
                        })();
                    }
                    Object.defineProperty(published, 'params', {value: pParams});
                    // entity.params.schema.p1 syntax
                    var pParamsSchema = EngineUtilsClass.unwrap(nParameters.getPublished());
                    if (!pParams.schema)
                        Object.defineProperty(pParams, 'schema', {value: pParamsSchema});
                    Object.defineProperty(published, 'find', {value: function (aCriteria) {
                            if(typeof aCriteria === 'function' && Array.prototype.find)
                                return Array.prototype.find.call(published, aCriteria);
                            else{
                                var keys = Object.keys(aCriteria);
                                keys = keys.sort();
                                var ordererKey = keys.join(' | ');
                                var orderer = orderers[ordererKey];
                                if (!orderer) {
                                    orderer = new Orderer(keys);
                                    published.forEach(function (item) {
                                        orderer.add(item);
                                    });
                                    orderers[ordererKey] = orderer;
                                }
                                var found = orderer.find(aCriteria);
                                return found;
                            }
                        }});
                    Object.defineProperty(published, 'findByKey', {value: function (aKeyValue) {
                            var criteria = {};
                            criteria[pkFieldName] = aKeyValue;
                            var found = published.find(criteria);
                            return found.length > 0 ? found[0] : null;
                        }});
                    Object.defineProperty(published, 'findById', {value: function (aKeyValue) {
                            Logger.warning('findById() is deprecated. Use findByKey() instead.');
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
                    nEntity.setSnapshotConsumer(function (aSnapshot, aFreshData) {
                        if (aFreshData) {
                            Array.prototype.splice.call(published, 0, published.length);
                        }
                        for (var s = 0; s < aSnapshot.length; s++) {
                            var snapshotInstance = aSnapshot[s];
                            var accepted;
                            if (nEntity.getElementClass()) {
                                var instanceCtor = EngineUtilsClass.unwrap(nEntity.getElementClass());
                                accepted = new instanceCtor();
                            } else {
                                accepted = {};
                            }
                            for (var sp in snapshotInstance) {
                                accepted[sp] = snapshotInstance[sp];
                            }
                            Array.prototype.push.call(published, accepted);
                            acceptInstance(accepted, false);
                        }
                        orderers = {};
                        published.cursor = published.length > 0 ? published[0] : null;
                        fire(published, {source: published, propertyName: 'length'});
                        published.forEach(function (aItem) {
                            fireOppositeScalarsChanges(aItem, nFields);
                            fireOppositeCollectionsChanges(aItem, nFields);
                        });
                    });
                    nEntity.setSnapshotProducer(function () {
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
                    listenable(published);
                    entityCTor.call(published, nEntity);
                    //WARNING Don't delete unwrap, due to it is used in methods bodies.
                    for (var protoEntryName in entityCTor.prototype) {
                        if (!published[protoEntryName]) {
                            var protoEntry = entityCTor.prototype[protoEntryName];
                            if (protoEntry instanceof Function) {
                                Object.defineProperty(published, protoEntryName, {value: protoEntry});
                            }
                        }
                    }
                    return published;
                }
                var entities = model.entities();
                for each (var enEntity in entities) {
                    enEntity.validateQuery();
                    if (enEntity.name) {
                        (function () {
                            var ppEntity = publishEntity(enEntity);
                            Object.defineProperty(aTarget, enEntity.name, {
                                value: ppEntity,
                                enumerable: true
                            });
                        })();
                    }
                }
                model.createORMDefinitions();
                aTarget.loadEntity = function (queryName) {
                    var lnEntity = model.loadEntity(B.boxAsJava(queryName));
                    return publishEntity(lnEntity);
                };
                aTarget.createEntity = function (sqlText, datasourceName) {
                    var lnEntity = model.createEntity(B.boxAsJava(sqlText), B.boxAsJava(datasourceName));
                    return publishEntity(lnEntity);
                };
                return aTarget;
            }
            function loadModel(aName, aTarget) {
                var files = ScriptedResourceClass.getApp().getModules().nameToFiles(aName);
                if (files) {
                    var modelDocument = ScriptedResourceClass.getApp().getModels().get(aName, files);
                    return loadModelDocument(modelDocument, aTarget);
                } else {
                    throw "Model definition '" + aName + "' missing.";
                }
            }

            function readModel(aContent, aTarget) {
                var modelDocument = Source2XmlDom.transform(aContent);
                return loadModelDocument(modelDocument, aTarget);
            }

            function requireEntities(aEntities, aOnSuccess, aOnFailure) {
                var entities;
                if (!Array.isArray(aEntities)) {
                    aEntities = aEntities + "";
                    if (aEntities.length > 5 && aEntities.trim().substring(0, 5).toLowerCase() === "<?xml") {
                        entities = [];
                        var pattern = /queryId="(.+?)"/ig;
                        var groups;
                        while ((groups = pattern.exec(aEntities)) != null) {
                            if (groups.length > 1) {
                                entities.push(groups[1]);
                            }
                        }
                    } else {
                        entities = [aEntities];
                    }
                } else {
                    entities = aEntities;
                }
                ScriptedResourceClass.loadEntities(Java.to(entities, JavaStringArrayClass), aOnSuccess ? aOnSuccess : null, aOnFailure ? aOnFailure : null);
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
        }
);
