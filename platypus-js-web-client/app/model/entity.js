define(['id', 'invoke', 'logger', 'managed', 'orderer'], function (Id, Invoke, Logger, M, Orderer) {
    var PENDING_ASSUMPTION_FAILED_MSG = "pending assigned to null without pending.cancel() call.";
    var QUERY_REQUIRED = "All model entities must have a query";

    function Entity(model, query) {
        var cursorListener;
        var pending;
        var valid = false;

        var onRequeried = null;
        var snapshotConsumer;
        var snapshotProducer;
        var lastSnapshot = [];
        var title = '';
        var name = '';
        var entityId = Id.generate();
        var model = null;
        var query = null;
        var elementClass = null;
        var inRelations = new Set();
        var outRelations = new Set();
        // TODO: Add keysNames filling
        var keysNames = new Set();
        var requiredNames = new Set();

        var self = this;

        var elementClass = null;

        function enqueueUpdate(params) {
        }

        function executeUpdate(onSuccess, onFailure) {
        }

        function execute(onSuccess, onFailure) {
        }

        function query(params, onSuccess, onFailure) {
        }

        function requery(onSuccess, onFailure) {
        }

        function append(data) {
        }

        function update(params, onSuccess, onFailure) {
        }

        function isPk(aPropertyName) {
            return keysNames.has(aPropertyName);
        }

        function isRequired(aPropertyName) {
            return requiredNames.has(aPropertyName);
        }

        function Insert(aEntityName) {
            this.kind = 'insert';
            this.entity = aEntityName;
            this.data = {};
        }
        function Delete(aEntityName) {
            this.kind = 'delete';
            this.entity = aEntityName;
            this.keys = {};
        }
        function Update(aEntityName) {
            this.kind = 'update';
            this.entity = aEntityName;
            this.keys = {};
            this.data = {};
        }

        function fireSelfScalarsOppositeCollectionsChanges(aSubject, aChange, nFields) {
            var expandingsOldValues = aChange.beforeState.selfScalarsOldValues;
            var ormDefs = /*nFields.@com.eas.client.metadata.Fields::*/forEachOrmScalarExpandings(aChange.propertyName, function (ormDef) {
                var ormDefName = ormDef.name;
                if (ormDefName) {
                    var ormDefOppositeName = ormDef.oppositeName;
                    var expandingOldValue = expandingsOldValues[ormDefName];
                    var expandingNewValue = aSubject[ormDefName];
                    /*@com.eas.core.Utils::*/fire(aSubject, {
                        source: aChange.source,
                        propertyName: ormDefName,
                        oldValue: expandingOldValue,
                        newValue: expandingNewValue
                    });
                    if (ormDefOppositeName) {
                        if (expandingOldValue) {
                            /*@com.eas.core.Utils::*/fire(expandingOldValue, {
                                source: expandingOldValue, propertyName: ormDefOppositeName
                            });
                        }
                        if (expandingNewValue) {
                            /*@com.eas.core.Utils::*/fire(expandingNewValue, {
                                source: expandingNewValue, propertyName: ormDefOppositeName
                            });
                        }
                    }
                }
            });
        }

        function prepareSelfScalarsChanges(aSubject, aChange, nFields) {
            var oldScalarValues = [];
            /*nFields.@com.eas.client.metadata.Fields::*/forEachOrmScalarExpandings(aChange.propertyName, function (ormDef) {
                if (ormDef && ormDef.name) {
                    oldScalarValues[ormDef.name] = aSubject[ormDef.name];
                }
            });
            return oldScalarValues;
        }

        function fireOppositeScalarsSelfCollectionsChanges(aSubject, aChange) {
            var oppositeScalarsFirerers = aChange.beforeState.oppositeScalarsFirerers;
            if (oppositeScalarsFirerers) {
                oppositeScalarsFirerers.forEach(function (aFirerer) {
                    aFirerer();
                });
            }
            var collectionsDefs = /*nFields.@com.eas.client.metadata.Fields::*/getOrmCollectionsDefinitions();
            if (collectionsDefs) {
                collectionsDefs.forEach(function (ormDef, collectionName) {
                    var collection = aSubject[collectionName];
                    collection.forEach(function (item) {
                        /*@com.eas.core.Utils::*/fire(item, {
                            source: item,
                            propertyName: ormDef.oppositeName
                        });
                    });
                });
                collectionsDefs.forEach(function (ormDef, collectionName) {
                    /*@com.eas.core.Utils::*/fire(aSubject, {
                        source: aSubject, propertyName: collectionName
                    });
                });
            }
        }

        function prepareOppositeScalarsChanges(aSubject) {
            var firerers = [];
            var collectionsDefs = /*nFields.@com.eas.client.metadata.Fields::*/getOrmCollectionsDefinitions();
            collectionsDefs.forEach(function (ormDef, collectionName) {
                var collection = aSubject[collectionName];
                collection.forEach(function (item) {
                    var ormDefOppositeName = ormDef.oppositeName;
                    if (ormDefOppositeName) {
                        firerers.push(function () {
                            /*@com.eas.core.Utils::*/fire(item, {
                                source: item,
                                propertyName: ormDefOppositeName
                            });
                        });
                    }
                });
            });
            return firerers;
        }

        function fireOppositeScalarsChanges(aSubject) {
            var collected = prepareOppositeScalarsChanges(aSubject);
            collected.forEach(function (aFirerer) {
                aFirerer();
            });
        }

        function fireOppositeCollectionsChanges(aSubject) {
            var scalarsDefs = /*nFields.@com.eas.client.metadata.Fields::*/getOrmScalarDefinitions();
            scalarsDefs.forEach(function (ormDef, scalarName) {
                var scalar = aSubject[scalarName];
                if (scalar && ormDef.oppositeName) {
                    /*@com.eas.core.Utils::*/fire(scalar, {
                        source: scalar,
                        propertyName: ormDef.oppositeName
                    });
                }
            });
        }

        var justInserted = null;
        var justInsertedChange = null;
        var orderers = {};
        var published = [];

        // TODO: Replace using of changeLog with model.changeLog
        var changeLog = model.changeLog;
        var _onChange = null;

        function managedOnChange(aSubject, aChange) {
            if (!tryToComplementInsert(aSubject, aChange)) {
                var updateChange = new Update(query.name);
                // Generate changeLog keys for update
                keysNames.forEach(function (keyName) {
                    // Tricky processing of primary keys modification case.
                    updateChange.keys[keyName] = keyName === aChange.propertyName ? aChange.oldValue : aChange.newValue;
                });
                updateChange.data[aChange.propertyName] = aChange.newValue;
                changeLog.push(updateChange);
            }
            Object.keys(orderers).forEach(function (aOrdererKey) {
                var aOrderer = orderers[aOrdererKey];
                if (aOrderer.inKeys(aChange.propertyName)) {
                    aOrderer.add(aChange.source);
                }
            });
            /*@com.eas.core.Utils::*/fire(aSubject, aChange);
            fireSelfScalarsOppositeCollectionsChanges(aSubject, aChange);// Expanding change
            if (isPk(aChange.propertyName)) {
                fireOppositeScalarsSelfCollectionsChanges(aSubject, aChange);
            }
            if (_onChange) {
                try {
                    _onChange(aChange);
                } catch (e) {
                    Logger.severe(e);
                }
            }
        }
        function managedBeforeChange(aSubject, aChange) {
            var oldScalars = prepareSelfScalarsChanges(aSubject, aChange);
            var oppositeScalarsFirerers = prepareOppositeScalarsChanges(aSubject);
            Object.keys(orderers).forEach(function (aOrdererKey) {
                var aOrderer = orderers[aOrdererKey];
                if (aOrderer.inKeys(aChange.propertyName)) {
                    aOrderer['delete'](aChange.source);
                }
            });
            return {
                selfScalarsOldValues: oldScalars,
                oppositeScalarsFirerers: oppositeScalarsFirerers
            };
        }

        function tryToComplementInsert(aSubject, aChange) {
            var complemented = false;
            if (aSubject === justInserted && isRequired(aChange.propertyName)) {
                var met = false;
                var iData = justInsertedChange.data;
                for (var d in iData) {
                    //var iv = iData[d];
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

        function javaOrmDefsToJs(aOrmDefs) {
            var jsDefs = {};
            var aOrmDefsIt = aOrmDefs.entrySet().iterator();
            while (aOrmDefsIt.hasNext()) {
                var defsEntry = aOrmDefsIt.next();
                var ormDef = defsEntry.getValue();
                var jsDef = ormDef.getJsDef();
                jsDefs[defsEntry.getKey()] = jsDef;
            }
            return jsDefs;
        }
        function acceptInstance(aSubject, aReassignOrmValues) {
            for (var aFieldName in aSubject) {
                if (typeof aSubject[aFieldName] === 'undefined')
                    aSubject[aFieldName] = null;
            }
            var scalarsContainer = {};
            var scalarJsDefs = /*nFields.@com.eas.client.metadata.Fields::*/getOrmScalarDefinitions();
            if (aReassignOrmValues) {
                scalarJsDefs.forEach(function (scalarDef, scalarName) {
                    if (typeof aSubject[scalarName] !== 'undefined') {
                        scalarsContainer[scalarName] = aSubject[scalarName];
                        aSubject[scalarName] = null;
                    }
                });
            }
            var collectionsContainer = {};
            var collectionsJsDefs = /*nFields.@com.eas.client.metadata.Fields::*/getOrmCollectionsDefinitions();
            if (aReassignOrmValues) {
                collectionsJsDefs.forEach(function (collectionDef, collectionName) {
                    if (typeof aSubject[collectionName] !== 'undefined') {
                        collectionsContainer[collectionName] = aSubject[collectionName];
                        aSubject[collectionName] = null;
                    }
                });
            }

            M.manageObject(aSubject, managedOnChange, managedBeforeChange);
            /*@com.eas.core.Utils::*/listenable(aSubject);
            // ORM mutable scalar and collection properties
            scalarJsDefs.forEach(function (scalarDef, scalarName) {
                Object.defineProperty(aSubject, scalarName, scalarDef);
                if (aReassignOrmValues && typeof scalarsContainer[scalarName] !== 'undefined') {
                    aSubject[scalarName] = scalarsContainer[scalarName];
                }
            });
            collectionsJsDefs.forEach(function (collectionDef, collectionName) {
                Object.defineProperty(aSubject, collectionName, collectionDef);
                if (aReassignOrmValues && typeof collectionsContainer[collectionName] !== 'undefined') {
                    var definedCollection = aSubject[collectionName];
                    var savedCollection = collectionsContainer[collectionName];
                    if (definedCollection && savedCollection && savedCollection.length > 0) {
                        for (var pi = 0; pi < savedCollection.length; pi++) {
                            definedCollection.push(savedCollection[pi]);
                        }
                    }
                }
            });
        }

        var _onInserted = null;
        var _onDeleted = null;
        var _onScrolled = null;

        M.manageArray(published, {
            spliced: function (added, deleted) {
                added.forEach(function (aAdded) {
                    justInserted = aAdded;
                    justInsertedChange = new Insert(query.name);
                    keysNames.forEach(function (keyName) {
                        if (!aAdded[keyName]) // If key is already assigned, than we have to preserve its value
                            aAdded[keyName] = Id.generate();
                    });
                    // TODO: Add a test with extra and unknown to a backend properties
                    for (var na in aAdded) {
                        justInsertedChange.data[na] = aAdded[na];
                    }
                    changeLog.push(justInsertedChange);
                    for (var aOrdererKey in orderers) {
                        var aOrderer = orderers[aOrdererKey];
                        aOrderer.add(aAdded);
                    }
                    acceptInstance(aAdded, true);
                    fireOppositeScalarsChanges(aAdded);
                    fireOppositeCollectionsChanges(aAdded);
                });
                deleted.forEach(function (aDeleted) {
                    if (aDeleted === justInserted) {
                        justInserted = null;
                        justInsertedChange = null;
                    }
                    var deleteChange = new Delete(query.name);
                    // Generate changeLog keys for delete
                    keysNames.forEach(function (keyName) {
                        // Tricky processing of primary keys modification case.
                        deleteChange.keys[keyName] = aDeleted[keyName];
                    });
                    changeLog.push(deleteChange);
                    for (var aOrdererKey in orderers) {
                        var aOrderer = orderers[aOrdererKey];
                        aOrderer['delete'](aDeleted);
                    }
                    fireOppositeScalarsChanges(aDeleted);
                    fireOppositeCollectionsChanges(aDeleted);
                    /*@com.eas.core.Utils::*/unlistenable(aDeleted);
                    M.unmanageObject(aDeleted);
                });
                if (_onInserted && added.length > 0) {
                    try {
                        _onInserted({
                            source: published,
                            items: added
                        });
                    } catch (e) {
                        Logger.severe(e);
                    }
                }
                if (_onDeleted && deleted.length > 0) {
                    try {
                        _onDeleted({
                            source: published,
                            items: deleted
                        });
                    } catch (e) {
                        Logger.severe(e);
                    }
                }
                /*@com.eas.core.Utils::*/fire(self, {source: self, propertyName: 'length'});
            },
            scrolled: function (aSubject, oldCursor, newCursor) {
                if (_onScrolled) {
                    try {
                        _onScrolled({
                            source: published,
                            propertyName: 'cursor',
                            oldValue: oldCursor,
                            newValue: newCursor
                        });
                    } catch (e) {
                        Logger.severe(e);
                    }
                }
                /*@com.eas.core.Utils::*/fire(self, {
                    source: self,
                    propertyName: 'cursor',
                    oldValue: oldCursor,
                    newValue: newCursor
                });
            }
        });
        // entity.params.p1 syntax
        Object.defineProperty(self, 'params', {
            get: function () {
                return query.parameters;
            },
            set: function (aValue) {
                query.parameters = aValue;
            }
        });
        function find(aCriteria) {
            if (typeof aCriteria === 'function' && Array.prototype.find) {
                return Array.prototype.find.call(published, aCriteria);
            } else {
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
        }
        function findByKey(aKeyValue) {
            if (keysNames.length > 0) {
                var criteria = {};
                criteria[keysNames[0]] = aKeyValue;
                var found = find(criteria);
                return found.length > 0 ? found[0] : null;
            } else {
                return null;
            }
        }
        function findById(aKeyValue) {
            Logger.warning('findById() is deprecated. Use findByKey() instead.');
            return findByKey(aKeyValue);
        }
        var toBeDeletedMark = '-platypus-to-be-deleted-mark';
        function remove(toBeDeleted) {
            toBeDeleted = toBeDeleted.forEach ? toBeDeleted : [toBeDeleted];
            toBeDeleted.forEach(function (anInstance) {
                anInstance[toBeDeletedMark] = true;
            });
            for (var d = self.length - 1; d >= 0; d--) {
                if (self[d][toBeDeletedMark]) {
                    self.splice(d, 1);
                }
            }
            toBeDeleted.forEach(function (anInstance) {
                delete anInstance[toBeDeletedMark];
            });
        }
        Object.defineProperty(published, 'find', {
            get: function () {
                return find;
            }
        });
        Object.defineProperty(published, 'findByKey', {
            get: function () {
                return findByKey;
            }
        });
        Object.defineProperty(published, 'findById', {
            get: function () {
                return findById;
            }
        });
        Object.defineProperty(published, 'remove', {
            get: function () {
                return remove;
            }
        });
        Object.defineProperty(published, 'onScroll', {
            get: function () {
                return _onScrolled;
            },
            set: function (aValue) {
                _onScrolled = aValue;
            }
        });
        Object.defineProperty(published, 'onInsert', {
            get: function () {
                return _onInserted;
            },
            set: function (aValue) {
                _onInserted = aValue;
            }
        });
        Object.defineProperty(published, 'onDelete', {
            get: function () {
                return _onDeleted;
            },
            set: function (aValue) {
                _onDeleted = aValue;
            }
        });
        Object.defineProperty(published, 'onChange', {
            get: function () {
                return _onChange;
            },
            set: function (aValue) {
                _onChange = aValue;
            }
        });
        function snapshotConsumer(aSnapshot, aFreshData) {
            if (aFreshData) {
                Array.prototype.splice.call(published, 0, published.length);
            }
            for (var s = 0; s < aSnapshot.length; s++) {
                var snapshotInstance = aSnapshot[s];
                var accepted;
                if (elementClass) {
                    accepted = new elementClass();
                } else {
                    accepted = {};
                }
                for (var sp in snapshotInstance) {
                    accepted[sp] = snapshotInstance[sp];
                }
                Array.prototype.push.call(self, accepted);
                acceptInstance(accepted, false);
            }
            orderers = {};
            published.cursor = published.length > 0 ? published[0] : null;
            /*@com.eas.core.Utils::*/fire(published, {
                source: published,
                propertyName: 'length'
            });
            self.forEach(function (aItem) {
                fireOppositeScalarsChanges(aItem);
                fireOppositeCollectionsChanges(aItem);
            });
        }
        function snapshotProducer() {
            var snapshot = [];
            self.forEach(function (aItem) {
                var cloned = {};
                for (var aFieldName in aItem) {
                    var typeOfField = typeof aItem[aFieldName];
                    if (typeOfField === 'undefined' || typeOfField === 'function')
                        cloned[aFieldName] = null;
                    else
                        cloned[aFieldName] = aItem[aFieldName];
                }
                snapshot.push(cloned);
            });
            return snapshot;
        }
        /*@com.eas.core.Utils::*/listenable(self);

        function addInRelation(relation){
            inRelations.add(relation);
        }

        function addOutRelation(relation){
            outRelations.add(relation);
        }

        Object.defineProperty(this, 'elementClass', {
            get: function () {
                return elementClass;
            },
            set: function (aValue) {
                elementClass = aValue;
            }
        });
        Object.defineProperty(this, 'onRequeried', {
            get: function () {
                return onRequeried;
            },
            set: function (aValue) {
                onRequeried = aValue;
            }
        });
        Object.defineProperty(this, 'enqueueUpdate', {
            get: function () {
                return enqueueUpdate;
            }
        });
        Object.defineProperty(this, 'executeUpdate', {
            get: function () {
                return executeUpdate;
            }
        });
        Object.defineProperty(this, 'execute', {
            get: function () {
                return execute;
            }
        });
        Object.defineProperty(this, 'query', {
            get: function () {
                return query;
            }
        });
        Object.defineProperty(this, 'requery', {
            get: function () {
                return requery;
            }
        });
        Object.defineProperty(this, 'append', {
            get: function () {
                return append;
            }
        });
        Object.defineProperty(this, 'update', {
            get: function () {
                return update;
            }
        });
        Object.defineProperty(this, 'entityId', {
            get: function () {
                return entityId;
            },
            set: function (aValue) {
                entityId = aValue;
            }
        });
        Object.defineProperty(this, 'title', {
            get: function () {
                return query ? query.title : title;
            },
            set: function (aValue) {
                title = aValue;
            }
        });
        Object.defineProperty(this, 'name', {
            get: function () {
                return name;
            },
            set: function (aValue) {
                name = aValue;
            }
        });
        Object.defineProperty(this, 'query', {
            get: function () {
                return query;
            },
            set: function (aValue) {
                query = aValue;
            }
        });
        Object.defineProperty(this, 'model', {
            get: function () {
                return model;
            },
            set: function (aValue) {
                model = aValue;
            }
        });
        Object.defineProperty(this, 'elementClass', {
            get: function () {
                return elementClass;
            },
            set: function (aValue) {
                elementClass = aValue;
            }
        });
        Object.defineProperty(this, 'valid', {
            get: function () {
                return valid;
            },
            set: function (aValue) {
                valid = aValue;
            }
        });
        Object.defineProperty(this, 'changeLog', {
            get: function () {
                return model.changeLog;
            }
        });
        Object.defineProperty(this, 'addInRelation', {
            get: function () {
                return addInRelation;
            }
        });
        Object.defineProperty(this, 'addOutRelation', {
            get: function () {
                return addOutRelation;
            }
        });
    }
    return Entity;
});