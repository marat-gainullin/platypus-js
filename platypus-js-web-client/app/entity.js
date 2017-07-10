define(['./id', './logger', './managed', './orderer', './client'], function (Id, Logger, M, Orderer, Client) {

    function Entity(model, query) {
        var self = this;
        
        var scalarNavigationProperties = new Map();
        var collectionNavigationProperties = new Map();

        function addScalarNavigation(aNavigation) {
            scalarNavigationProperties.set(aNavigation.name, aNavigation);
        }

        function addCollectionNavigation(aNavigation) {
            collectionNavigationProperties.set(aNavigation.name, aNavigation);
        }
        
        function clearScalarNavigations(){}
            scalarNavigationProperties.clear();
        
        function clearCollectionNavigations(){
            collectionNavigationProperties.clear();
        }

        var onRequeried = null;
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


        var elementClass = null;

        var valid = false;
        var pending = null;
        var parameters = {};

        function inRelatedValid() {
            var allvalid = true;
            inRelations.forEach(function (relation) {
                if (relation.leftEntity && !relation.leftEntity.valid) {
                    allvalid = false;
                }
            });
            return allvalid;
        }

        function fromRight(){
            var right = [];
            outRelations.forEach(function(relation){
                right.push(relation.rightEntity);
            });
            return right;
        }

        function collectRight(){
            var collected = [];
            // Breadth first collecting
            var right = fromRight();
            for(var r = 0; r < right.length; r++){
                var rightEntity = right[r];
                collected.push(rightEntity);
                Array.prototype.push.apply(rightEntity, rightEntity.fromRight());
            }
            return collected;
        }

        function invalidate() {
            valid = false;
        }

        function cancel() {
            if (!pending)
                throw "Can't cancel absent request";
            pending.cancel();
            pending = null;
        }

        function bindParameters() {
            inRelations.forEach(function (relation) {
                var source = relation.leftEntity;
                if (relation.leftItem) {
                    var leftValue;
                    if (relation.leftParameter) {
                        leftValue = source.params[relation.leftItem];
                    } else {
                        if (source.cursor) {
                            leftValue = source.cursor[relation.leftItem];
                        } else if (source.length > 0) {
                            leftValue = source[0][relation.leftItem];
                        } else {
                            leftValue = null;
                        }
                    }
                    parameters[relation.rightItem] = typeof leftValue === 'undefined' ? null : leftValue;
                }
            });
        }

        function start(/*Low level event*/onSuccess, /*Low level event*/onFailure) {
            if (pending)
                throw "Can't start new request, while previous request is in progress";
            if (valid)
                throw "Can't start request for valid entity";
            pendingOnSuccess = onSuccess;
            pendingOnFailure = onFailure;
            bindParameters();
            pending = query.requestData(parameters, function (data) {
                acceptData(data, true);
                lastSnapshot = data;
                var onSuccess = pendingOnSuccess;
                pending = null;
                pendingOnSuccess = null;
                pendingOnFailure = null;
                valid = true;
                if (onSuccess) {
                    onSuccess();
                }
                if (onRequeried)
                    onRequeried();
            }, function () {
                valid = true;
                var onFailure = pendingOnFailure;
                pending = null;
                pendingOnSuccess = null;
                pendingOnFailure = null;
                if (onFailure) {
                    onFailure();
                }
            });
        }

        function enqueueUpdate(params) {
            var command = query.prepareCommand(params);
            model.changeLog.push(command);
        }

        function executeUpdate(onSuccess, onFailure) {
            Logger.warning('Entity.executeUpdate() is deprecated, Use Entity.update() instead.');
            update(onSuccess, onFailure);
        }

        function execute(onSuccess, onFailure) {
            Logger.warning('Entity.execute() is deprecated, Use Entity.requery() instead.');
            requery(onSuccess, onFailure);
        }

        function query(params, onSuccess, onFailure) {
            if (onSuccess) {
                query.requestData(params, onSuccess, onFailure);
            } else {
                throw "Synchronous Entity.query() method is not supported within browser client. So 'onSuccess' is required argument.";
            }
        }

        function requery(onSuccess, onFailure) {
            if (onSuccess) {
                var toInvalidate = collectRight();
                toInvalidate.push(self);
                model.start(toInvalidate, onSuccess, onFailure);
            } else {
                throw "Synchronous Entity.requery() method is not supported within browser client. So 'onSuccess' is required argument.";
            }
        }

        function append(data) {
            acceptData(data, false);
        }

        function update(params, onSuccess, onFailure) {
            if (onSuccess) {
                var command = query.prepareCommand(params);
                Client.requestCommit([command], onSuccess, onFailure);
            } else {
                throw "Synchronous Entity.update() method is not supported within browser client. So 'onSuccess' is required argument.";
            }
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

        function fireSelfScalarsOppositeCollectionsChanges(aSubject, aChange) {
            var expandingsOldValues = aChange.beforeState.selfScalarsOldValues;
            scalarNavigationProperties.forEach(aChange.propertyName, function (ormDef, scalarName) {
                if (aChange.propertyName === ormDef.baseName) {
                    var ormDefOppositeName = ormDef.oppositeName;
                    var expandingOldValue = expandingsOldValues[scalarName];
                    var expandingNewValue = aSubject[scalarName];
                    M.fire(aSubject, {
                        source: aChange.source,
                        propertyName: scalarName,
                        oldValue: expandingOldValue,
                        newValue: expandingNewValue
                    });
                    if (ormDefOppositeName) {
                        if (expandingOldValue) {
                            M.fire(expandingOldValue, {
                                source: expandingOldValue, propertyName: ormDefOppositeName
                            });
                        }
                        if (expandingNewValue) {
                            M.fire(expandingNewValue, {
                                source: expandingNewValue, propertyName: ormDefOppositeName
                            });
                        }
                    }
                }
            });
        }

        function prepareSelfScalarsChanges(aSubject, aChange) {
            var oldScalarValues = [];
            scalarNavigationProperties.forEach(function (ormDef, scalarName) {
                if (aChange.propertyName === ormDef.baseName && scalarName) {
                    oldScalarValues[scalarName] = aSubject[scalarName];
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
            collectionNavigationProperties.forEach(function (ormDef, collectionName) {
                var collection = aSubject[collectionName];
                collection.forEach(function (item) {
                    M.fire(item, {
                        source: item,
                        propertyName: ormDef.oppositeName
                    });
                });
            });
            collectionNavigationProperties.forEach(function (ormDef, collectionName) {
                M.fire(aSubject, {
                    source: aSubject, propertyName: collectionName
                });
            });
        }

        function prepareOppositeScalarsChanges(aSubject) {
            var firerers = [];
            collectionNavigationProperties.forEach(function (ormDef, collectionName) {
                var collection = aSubject[collectionName];
                collection.forEach(function (item) {
                    var ormDefOppositeName = ormDef.oppositeName;
                    if (ormDefOppositeName) {
                        firerers.push(function () {
                            M.fire(item, {
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
            scalarNavigationProperties.forEach(function (ormDef, scalarName) {
                var scalar = aSubject[scalarName];
                if (scalar && ormDef.oppositeName) {
                    M.fire(scalar, {
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

        var _onChange = null;

        function managedOnChange(aSubject, aChange) {
            if (!tryToComplementInsert(aSubject, aChange)) {
                var updateChange = new Update(query.entityName);
                // Generate changeLog keys for update
                keysNames.forEach(function (keyName) {
                    // Tricky processing of primary keys modification case.
                    updateChange.keys[keyName] = keyName === aChange.propertyName ? aChange.oldValue : aChange.newValue;
                });
                updateChange.data[aChange.propertyName] = aChange.newValue;
                model.changeLog.push(updateChange);
            }
            Object.keys(orderers).forEach(function (aOrdererKey) {
                var aOrderer = orderers[aOrdererKey];
                if (aOrderer.inKeys(aChange.propertyName)) {
                    aOrderer.add(aChange.source);
                }
            });
            M.fire(aSubject, aChange);
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

        function acceptInstance(aSubject) {
            for (var aFieldName in aSubject) {
                if (typeof aSubject[aFieldName] === 'undefined')
                    aSubject[aFieldName] = null;
            }
            M.manageObject(aSubject, managedOnChange, managedBeforeChange);
            M.listenable(aSubject);
            // ORM mutable scalar properties
            scalarNavigationProperties.forEach(function (scalarDef, scalarName) {
                Object.defineProperty(aSubject, scalarName, scalarDef);
            });
            // ORM mutable collection properties
            collectionNavigationProperties.forEach(function (collectionDef, collectionName) {
                Object.defineProperty(aSubject, collectionName, collectionDef);
            });
        }

        var _onInserted = null;
        var _onDeleted = null;
        var _onScrolled = null;

        M.manageArray(published, {
            spliced: function (added, deleted) {
                added.forEach(function (aAdded) {
                    justInserted = aAdded;
                    justInsertedChange = new Insert(query.entityName);
                    keysNames.forEach(function (keyName) {
                        if (!aAdded[keyName]) // If key is already assigned, than we have to preserve its value
                            aAdded[keyName] = Id.generate();
                    });
                    // TODO: Add a test with extra and unknown to a backend properties
                    for (var na in aAdded) {
                        justInsertedChange.data[na] = aAdded[na];
                    }
                    model.changeLog.push(justInsertedChange);
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
                    var deleteChange = new Delete(query.entityName);
                    // Generate changeLog keys for delete
                    keysNames.forEach(function (keyName) {
                        // Tricky processing of primary keys modification case.
                        deleteChange.keys[keyName] = aDeleted[keyName];
                    });
                    model.changeLog.push(deleteChange);
                    for (var aOrdererKey in orderers) {
                        var aOrderer = orderers[aOrdererKey];
                        aOrderer['delete'](aDeleted);
                    }
                    fireOppositeScalarsChanges(aDeleted);
                    fireOppositeCollectionsChanges(aDeleted);
                    M.unlistenable(aDeleted);
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
                M.fire(self, {
                    source: self,
                    propertyName: 'length'
                });
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
                M.fire(self, {
                    source: self,
                    propertyName: 'cursor',
                    oldValue: oldCursor,
                    newValue: newCursor
                });
                Logger.info('About to requery some entities, due to cursor changes in ' + query.entityName);
                var toInvalidate = collectRight();
                model.start(toInvalidate, function(){
                    Logger.info('Some model entities requeried, due to cursor changes in ' + query.entityName);
                }, function(e){
                    Logger.severe(e);
                });
            }
        });
        M.listenable(self);
        
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

        function acceptData(aData, aFreshData) {
            if (aFreshData) {
                Array.prototype.splice.call(published, 0, published.length);
            }
            for (var s = 0; s < aData.length; s++) {
                var dataRow = aData[s];
                var accepted;
                if (elementClass) {
                    accepted = new elementClass();
                } else {
                    accepted = {};
                }
                for (var sp in dataRow) {
                    accepted[sp] = dataRow[sp];
                }
                Array.prototype.push.call(self, accepted);
                acceptInstance(accepted, false);
            }
            orderers = {};
            published.cursor = published.length > 0 ? published[0] : null;
            M.fire(published, {
                source: published,
                propertyName: 'length'
            });
            self.forEach(function (aItem) {
                fireOppositeScalarsChanges(aItem);
                fireOppositeCollectionsChanges(aItem);
            });
        }

        function takeSnapshot() {
            lastSnapshot = [];
            self.forEach(function (aItem) {
                var cloned = {};
                for (var aFieldName in aItem) {
                    var typeOfField = typeof aItem[aFieldName];
                    if (typeOfField === 'undefined' || typeOfField === 'function')
                        cloned[aFieldName] = null;
                    else
                        cloned[aFieldName] = aItem[aFieldName];
                }
                lastSnapshot.push(cloned);
            });
        }

        function applyLastSnapshot() {
            if (lastSnapshot) {
                acceptData(lastSnapshot, true);
            }
        }

        function addInRelation(relation) {
            inRelations.add(relation);
        }

        function addOutRelation(relation) {
            outRelations.add(relation);
        }

        Object.defineProperty(self, 'params', {
            get: function () {
                return parameters;
            }
        });
        
        Object.defineProperty(published, 'applyLastSnapshot', {
            get: function () {
                return applyLastSnapshot;
            }
        });
        Object.defineProperty(published, 'takeSnapshot', {
            get: function () {
                return takeSnapshot;
            }
        });
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
                return title;
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
        Object.defineProperty(this, 'valid', {
            get: function () {
                return valid;
            },
            set: function (aValue) {
                valid = aValue;
            }
        });
        Object.defineProperty(this, 'pending', {
            get: function () {
                return !!pending;
            }
        });
        Object.defineProperty(this, 'start', {
            get: function () {
                return start;
            }
        });
        Object.defineProperty(this, 'cancel', {
            get: function () {
                return cancel;
            }
        });
        Object.defineProperty(this, 'invalidate', {
            get: function () {
                return invalidate;
            }
        });
        Object.defineProperty(this, 'inRelatedValid', {
            get: function () {
                return inRelatedValid;
            }
        });
        Object.defineProperty(this, 'collectRight', {
            get: function () {
                return collectRight;
            }
        });
        Object.defineProperty(this, 'addScalarNavigation', {
            get: function () {
                return addScalarNavigation;
            }
        });
        Object.defineProperty(this, 'addCollectionNavigation', {
            get: function () {
                return addCollectionNavigation;
            }
        });
        Object.defineProperty(this, 'clearScalarNavigations', {
            get: function () {
                return clearScalarNavigations;
            }
        });
        Object.defineProperty(this, 'clearCollectionNavigations', {
            get: function () {
                return clearCollectionNavigations;
            }
        });
    }
    return Entity;
});