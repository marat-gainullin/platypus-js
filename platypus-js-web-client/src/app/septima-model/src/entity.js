define([
    'core/id',
    'core/logger',
    './managed',
    './orderer',
    'remote/requests',
    'core/extend',
    'core/invoke'], function (
        Id,
        Logger,
        M,
        Orderer,
        Requests,
        extend,
        Invoke) {
    function Query(entityName) {
        function prepareCommandRequest(parameters) {
            var command = {
                kind: 'command',
                entity: entityName,
                parameters: {}
            };
            for (var p in parameters)
                command.parameters[p] = parameters[p];
            return command;
        }

        function requestData(parameters, onSuccess, onFailure) {
            return Requests.requestData(entityName, parameters, onSuccess, onFailure);
        }

        Object.defineProperty(this, 'entityName', {
            get: function () {
                return entityName;
            },
            set: function (aValue) {
                entityName = aValue;
            }
        });

        Object.defineProperty(this, 'requestData', {
            get: function () {
                return requestData;
            }
        });
        Object.defineProperty(this, 'prepareCommandRequest', {
            get: function () {
                return prepareCommandRequest;
            }
        });
    }

    function Entity(serverEntityName) {
        if (!(this instanceof Entity))
            throw "Use 'new Entity()' please";
        Array.apply(this);
        var self = this;

        // Entity's chnge log is used as well ass model.changeLog to 
        // accomplish future changeLog replay while revert feature.
        var changeLog = [];

        var scalarNavigationProperties = new Map();
        var collectionNavigationProperties = new Map();

        function addScalarNavigation(aNavigation) {
            scalarNavigationProperties.set(aNavigation.name, aNavigation);
        }

        function addCollectionNavigation(aNavigation) {
            collectionNavigationProperties.set(aNavigation.name, aNavigation);
        }

        function clearScalarNavigations() {}
        scalarNavigationProperties.clear();

        function clearCollectionNavigations() {
            collectionNavigationProperties.clear();
        }

        var onRequery = null;
        var lastSnapshot = [];
        var title = '';
        var name = '';
        var model = null;
        var queryProxy = new Query(serverEntityName);
        var elementClass = null;
        var inRelations = new Set();
        var outRelations = new Set();
        // TODO: Add keysNames filling
        var keysNames = new Set();
        var requiredNames = new Set();


        var elementClass = null;

        var valid = false;
        var pending = null;
        var pendingOnSuccess = null;
        var pendingOnFailure = null;
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

        function fromRight() {
            var right = [];
            outRelations.forEach(function (relation) {
                right.push(relation.rightEntity);
            });
            return right;
        }

        function collectRight() {
            var collected = new Set();
            // Breadth first collecting
            var right = fromRight();
            for (var r = 0; r < right.length; r++) {
                var rightEntity = right[r];
                collected.add(rightEntity);
                Array.prototype.push.apply(right, rightEntity.fromRight());
            }
            return Array.from(collected);
        }

        function invalidate() {
            valid = false;
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

        function start(/*Low level event*/_onSuccess, /*Low level event*/_onFailure) {
            if (pending)
                throw "Can't start new request, while previous request is in progress";
            if (valid)
                throw "Can't start request for valid entity";
            if (keysNames.size === 0)
                Logger.warning("'keysNames' for '" + name + "' are absent. Keys auto generation and 'findByKey()' will not work properly");
            pendingOnSuccess = _onSuccess;
            pendingOnFailure = _onFailure;
            bindParameters();
            pending = queryProxy.requestData(parameters, function (data) {
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
                if (onRequery) {
                    Invoke.later(function () {
                        onRequery();
                    });
                }
            }, function (reason) {
                valid = true;
                var onFailure = pendingOnFailure;
                pending = null;
                pendingOnSuccess = null;
                pendingOnFailure = null;
                if (onFailure) {
                    onFailure(reason);
                }
            });
        }

        function cancel() {
            if (pending) {
                pending.cancel();
                return true;
            } else {
                return false;
            }
        }

        function enqueueUpdate(params) {
            var command = queryProxy.prepareCommandRequest(params);
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

        function requestData(params, onSuccess, onFailure) {
            if (onSuccess) {
                queryProxy.requestData(params, onSuccess, onFailure);
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
                var command = queryProxy.prepareCommandRequest(params);
                Requests.requestCommit([command], onSuccess, onFailure);
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
            scalarNavigationProperties.forEach(function (ormDef, scalarName) {
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

        var onChange = null;

        function managedOnChange(aSubject, aChange) {
            if (!tryToComplementInsert(aSubject, aChange)) {
                var updateChange = new Update(queryProxy.entityName);
                // Generate changeLog keys for update
                keysNames.forEach(function (keyName) {
                    // Tricky processing of primary keys modification case.
                    updateChange.keys[keyName] = keyName === aChange.propertyName ? aChange.oldValue : aChange.newValue;
                });
                updateChange.data[aChange.propertyName] = aChange.newValue;
                changeLog.push(updateChange);
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
            if (onChange) {
                Invoke.later(function () {
                    onChange(aChange);
                });
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

        var onInsert = null;
        var onDelete = null;

        M.manageArray(self, {
            spliced: function (added, deleted) {
                added.forEach(function (aAdded) {
                    justInserted = aAdded;
                    justInsertedChange = new Insert(queryProxy.entityName);
                    keysNames.forEach(function (keyName) {
                        if (!aAdded[keyName]) // If key is already assigned, than we have to preserve its value
                            aAdded[keyName] = Id.generate();
                    });
                    for (var na in aAdded) {
                        justInsertedChange.data[na] = aAdded[na];
                    }
                    changeLog.push(justInsertedChange);
                    model.changeLog.push(justInsertedChange);
                    for (var aOrdererKey in orderers) {
                        var aOrderer = orderers[aOrdererKey];
                        aOrderer.add(aAdded);
                    }
                    acceptInstance(aAdded);
                    fireOppositeScalarsChanges(aAdded);
                    fireOppositeCollectionsChanges(aAdded);
                });
                deleted.forEach(function (aDeleted) {
                    if (aDeleted === justInserted) {
                        justInserted = null;
                        justInsertedChange = null;
                    }
                    var deleteChange = new Delete(queryProxy.entityName);
                    // Generate changeLog keys for delete
                    keysNames.forEach(function (keyName) {
                        // Tricky processing of primary keys modification case.
                        deleteChange.keys[keyName] = aDeleted[keyName];
                    });
                    changeLog.push(deleteChange);
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
                if (onInsert && added.length > 0) {
                    Invoke.later(function () {
                        onInsert({
                            source: self,
                            items: added
                        });
                    });
                }
                if (onDelete && deleted.length > 0) {
                    Invoke.later(function () {
                        onDelete({
                            source: self,
                            items: deleted
                        });
                    });
                }
                M.fire(self, {
                    source: self,
                    propertyName: 'length'
                });
            }
        });
        var onScroll = null;
        var cursor = null;
        function scrolled(aValue) {
            var oldCursor = cursor;
            var newCursor = aValue;
            cursor = aValue;
            if (onScroll) {
                Invoke.later(function () {
                    onScroll({
                        source: self,
                        propertyName: 'cursor',
                        oldValue: oldCursor,
                        newValue: newCursor
                    });
                });
            }
            M.fire(self, {
                source: self,
                propertyName: 'cursor',
                oldValue: oldCursor,
                newValue: newCursor
            });
        }
        M.listenable(self);

        function find(aCriteria) {
            if (typeof aCriteria === 'function' && Array.prototype.find) {
                return Array.prototype.find.call(self, aCriteria);
            } else {
                var keys = Object.keys(aCriteria);
                keys = keys.sort();
                var ordererKey = keys.join(' | ');
                var orderer = orderers[ordererKey];
                if (!orderer) {
                    orderer = new Orderer(keys);
                    self.forEach(function (item) {
                        orderer.add(item);
                    });
                    orderers[ordererKey] = orderer;
                }
                var found = orderer.find(aCriteria);
                return found;
            }
        }

        function findByKey(aKeyValue) {
            if (keysNames.size > 0) {
                var criteria = {};
                keysNames.forEach(function (keyName) {
                    criteria[keyName] = aKeyValue;
                });
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
                Array.prototype.splice.call(self, 0, self.length);
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
                acceptInstance(accepted);
            }
            orderers = {};
            M.fire(self, {
                source: self,
                propertyName: 'length'
            });
            self.forEach(function (aItem) {
                fireOppositeScalarsChanges(aItem);
                fireOppositeCollectionsChanges(aItem);
            });
        }

        // TODO: Eliminatre snapshots and transform snapshots feature.
        function commit() {
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
            changeLog = [];
        }

        // TODO: Change revert implementation to changeLog undo.
        function revert() {
            if (lastSnapshot) {
                acceptData(lastSnapshot, true);
            }
            changeLog = [];
        }

        function addInRelation(relation) {
            inRelations.add(relation);
        }

        function addOutRelation(relation) {
            outRelations.add(relation);
        }

        Object.defineProperty(this, 'keysNames', {
            get: function () {
                return keysNames;
            }
        });
        Object.defineProperty(this, 'params', {
            get: function () {
                return parameters;
            }
        });
        Object.defineProperty(this, 'cursor', {
            get: function () {
                return cursor;
            },
            set: function (aValue) {
                scrolled(aValue);
            }
        });
        Object.defineProperty(this, 'revert', {
            get: function () {
                return revert;
            }
        });
        Object.defineProperty(this, 'commit', {
            get: function () {
                return commit;
            }
        });
        Object.defineProperty(this, 'find', {
            get: function () {
                return find;
            }
        });
        Object.defineProperty(this, 'findByKey', {
            get: function () {
                return findByKey;
            }
        });
        Object.defineProperty(this, 'findBycore/id', {
            get: function () {
                return findById;
            }
        });
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });
        Object.defineProperty(this, 'onScroll', {
            get: function () {
                return onScroll;
            },
            set: function (aValue) {
                onScroll = aValue;
            }
        });
        Object.defineProperty(this, 'onInsert', {
            get: function () {
                return onInsert;
            },
            set: function (aValue) {
                onInsert = aValue;
            }
        });
        Object.defineProperty(this, 'onDelete', {
            get: function () {
                return onDelete;
            },
            set: function (aValue) {
                onDelete = aValue;
            }
        });
        Object.defineProperty(this, 'onChange', {
            get: function () {
                return onChange;
            },
            set: function (aValue) {
                onChange = aValue;
            }
        });
        Object.defineProperty(this, 'onRequeried', {
            get: function () {
                return onRequery;
            },
            set: function (aValue) {
                onRequery = aValue;
            }
        });
        Object.defineProperty(this, 'onRequery', {
            get: function () {
                return onRequery;
            },
            set: function (aValue) {
                onRequery = aValue;
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
        Object.defineProperty(this, 'proxy', {
            get: function () {
                return queryProxy;
            }
        });
        Object.defineProperty(this, 'query', {
            get: function () {
                return requestData;
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
        Object.defineProperty(this, 'model', {
            get: function () {
                return model;
            },
            set: function (aValue) {
                model = aValue;
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
        Object.defineProperty(this, 'valcore/id', {
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
        Object.defineProperty(this, 'inRelatedValcore/id', {
            get: function () {
                return inRelatedValid;
            }
        });
        Object.defineProperty(this, 'fromRight', {
            get: function () {
                return fromRight;
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
    extend(Entity, Array);
    return Entity;
});