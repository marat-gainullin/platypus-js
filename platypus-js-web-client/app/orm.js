define(['./logger', './client', './internals', './invoke', './managed', './model-reader'], function (Logger, Client, Utils, Invoke, M, readModelDocument) {

    function Query(entityName) {
        function prepareCommand(parameters) {
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
            pending = Client.requestData(entityName, parameters, onSuccess, onFailure);
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
        Object.defineProperty(this, 'prepareCommand', {
            get: function () {
                return prepareCommand;
            }
        });
    }

    function Model() {
        var relations = new Set();
        var referenceRelations = new Set();
        var entities = new Map();
        var changeLog = [];

        function addRelation(relation) {
            relations.add(relation);
        }

        function addReferenceRelation(referenceRelation) {
            referenceRelations.add(referenceRelation);
        }

        function addEntity(entity) {
            entities.set(entity.entityId, entity);
        }

        function getEntity(id) {
            return entities.get(id);
        }

        function start(toInvalidate, onSuccess, onFailure) {
            function entitiesValid() {
                var valid = true;
                entities.forEach(function (entity, id) {
                    if (!entity.valid) {
                        valid = false;
                    }
                });
                return valid;
            }

            function entitiesPending() {
                var pending = false;
                entities.forEach(function (entity, id) {
                    if (entity.pending) {
                        pending = true;
                    }
                });
                return pending;
            }

            var failures = [];
            function complete(failureReason) {
                if (failureReason)
                    failures.push(failureReason);
                if (entitiesValid()) {
                    if (failures.length > 0) {
                        if (onFailure) {
                            onFailure(failures);
                        }
                    } else {
                        if (onSuccess) {
                            onSuccess();
                        }
                    }
                } else {
                    pushInvalidToPending();
                }
            }

            function pushInvalidToPending() {
                entities.forEach(function (entity, id) {
                    if (!entity.valid && !entity.pending && entity.inRelatedValid()) {
                        entity.start(function () {
                            
                            complete();
                        }, function (reason) {
                            complete(reason);
                        });
                    }
                });
            }

            if (entitiesPending()) {
                throw "Can't start new data quering process while previous is in progress";
            } else {
                toInvalidate.forEach(function (entity) {
                    entity.invalidate();
                });
                if (entitiesValid()) { // In case of empty model, there are will not be invalid entities, even after invalidation.
                    if (onSuccess) {
                        Invoke.later(onSuccess);
                    }
                } else {
                    pushInvalidToPending();
                }
            }
            return {
                cancel: function () {
                    entities.forEach(function (entity, id) {
                        if (entity.pending) {
                            entity.cancel();
                        }
                    });
                }
            };
        }

        function requery(onSuccess, onFailure) {
            if (onSuccess) {
                var toInvalidate = Array.from(entities.values());
                start(toInvalidate, onSuccess, onFailure);
            } else {
                throw "Synchronous Model.requery() method is not supported within browser client. So 'onSuccess' is required argument.";
            }
        }

        function revert() {
            changeLog = [];
            entities.forEach(function (e, id) {
                e.applyLastSnapshot();
            });
        }

        function commited() {
            changeLog = [];
            entities.forEach(function (e, id) {
                e.takeSnapshot();
            });
        }

        function rolledback() {
            Logger.info("Model changes are rolled back");
        }

        function save(onSuccess, onFailure) {
            if (onSuccess) {
                Client.requestCommit(changeLog, function () {
                    commited();
                    onSuccess();
                }, function () {
                    rolledback();
                    onFailure();
                });
            } else {
                throw 'onSuccess is required argument';
            }
        }

        function ScalarNavigation(relation) {
            this.enumerable = false;
            this.configurable = true;
            this.get = function () {
                var criterion = {};
                criterion[relation.rightField] = this[relation.leftField]; // Warning! 'this' here is data array's element!
                var found = relation.rightEntity.find(criterion);
                return found && found.length === 1 ? found[0] : null;
            };
            this.set = function (aValue) {
                this[relation.leftField] = aValue ? aValue[relation.rightField] : null;
            };
            this.name = relation.scalarPropertyName;
            this.oppositeName = relation.collectionPropertyName;
            this.baseName = relation.leftField;
        }

        function CollectionNavigation(relation) {
            this.enumerable = false;
            this.configurable = true;
            this.get = function () {
                var criterion = {};
                var targetKey = this[relation.rightField];// Warning! 'this' here is data array's element!
                criterion[relation.leftField] = targetKey;
                var found = relation.leftEntity.find(criterion);
                M.manageArray(found, {
                    spliced: function (added, deleted) {
                        added.forEach(function (item) {
                            item[relation.leftField] = targetKey;
                        });
                        deleted.forEach(function (item) {
                            item[relation.leftField] = null;
                        });
                        M.fire(found, {
                            source: found,
                            propertyName: 'length'
                        });
                    },
                    scrolled: function (aSubject, oldCursor, newCursor) {
                        M.fire(found, {
                            source: found,
                            propertyName: 'cursor',
                            oldValue: oldCursor,
                            newValue: newCursor
                        });
                    }
                });
                M.listenable(found);
                return found;
            };
            this.name = relation.collectionPropertyName;
            this.oppositeName = relation.scalarPropertyName;
        }

        function processReferenceRelations() {
            entities.forEach(function(entity, id){
                entity.clearScalarNavigations();
                entity.clearCollectionNavigations();
            });
            referenceRelations.forEach(function (relation) {
                if (relation.scalarPropertyName)
                    relation.leftEntity.addScalarNavigation(new ScalarNavigation(relation));
                if (relation.collectionPropertyName)
                    relation.rightEntity.addCollectionNavigation(new CollectionNavigation(relation));
            });
        }

        Object.defineProperty(this, 'requery', {
            get: function () {
                return requery;
            }
        });
        Object.defineProperty(this, 'revert', {
            get: function () {
                return revert;
            }
        });
        Object.defineProperty(this, 'save', {
            get: function () {
                return save;
            }
        });
        Object.defineProperty(this, 'changeLog', {
            get: function () {
                return changeLog;
            }
        });
        Object.defineProperty(this, 'modified', {
            get: function () {
                return changeLog && changeLog.length > 0;
            }
        });
        Object.defineProperty(this, 'addRelation', {
            get: function () {
                return addRelation;
            }
        });
        Object.defineProperty(this, 'addReferenceRelation', {
            get: function () {
                return addReferenceRelation;
            }
        });
        Object.defineProperty(this, 'addEntity', {
            get: function () {
                return addEntity;
            }
        });
        Object.defineProperty(this, 'getEntity', {
            get: function () {
                return getEntity;
            }
        });
        Object.defineProperty(this, 'processReferenceRelations', {
            get: function () {
                return processReferenceRelations;
            }
        });
    }

    var SERVER_ENTITY_TOUCHED_NAME = "Entity ";

    var loadedEntities = new Map();

    function loadEntities(aQueriesNames, onSuccess, onFailure) {
        if (aQueriesNames.length > 0) {
            var process = new Utils.Process(aQueriesNames.size, function () {
                onSuccess();
            }, function (aReasons) {
                onFailure(aReasons);
            });
            aQueriesNames.forEach(function (queryName) {
                var urlQuery = Client.params(Client.param(Client.RequestParams.TYPE, Client.RequestTypess.rqAppQuery), Client.param(Client.RequestParams.QUERY_ID, queryName));
                return Client.startApiRequest(null, urlQuery, "", Client.Methods.GET, null, function (aResponse) {
                    if (Client.isJsonResponse(aResponse)) {
                        loadedEntities.set(queryName, aResponse.responseJSON);
                        Logger.info(SERVER_ENTITY_TOUCHED_NAME + queryName + ' - Loaded');
                        process.onSuccess();
                    } else {
                        process.onFailure(aResponse.responseText);
                    }
                }, function (aResponse) {
                    Logger.severe(aResponse.responseText);
                    process.onFailure(aResponse.responseText);
                });
                Logger.info('Loading ' + SERVER_ENTITY_TOUCHED_NAME + queryName + ' ...');
            });
        } else {
            later(onSuccess);
        }
    }

    function requireEntities(aEntities, aOnSuccess, aOnFailure) {
        var entities;
        if (!Array.isArray(aEntities)) {
            aEntities = aEntities + "";
            if (aEntities.length > 5 && aEntities.trim().substring(0, 5).toLowerCase() === "<?xml") {
                entities = [];
                var pattern = /queryId="(.+?)"/ig;
                var groups = pattern.exec(aEntities);
                while (groups) {
                    if (groups.length > 1) {
                        entities.push(groups[1]);
                    }
                    groups = pattern.exec(aEntities);
                }
            } else {
                entities = [aEntities];
            }
        } else {
            entities = aEntities;
        }
        var toLoad = entities.filter(function (entityName) {
            return !loadedEntities.has(entityName);
        });
        loadEntities(toLoad, function () {
            var resolved = [];
            for (var i = 0; i < entities.length; i++) {
                resolved.push(loadedEntities.get(entities[i]));
            }
            aOnSuccess.apply(null, resolved);
        }, aOnFailure);
    }

    function readModel(aModelContent, aTarget) {
        var doc = new DOMParser().parseFromString(aModelContent ? aModelContent + "" : "", "text/xml");
        return readModelDocument(doc, null, aTarget);
    }

    function loadModel(aModuleName, aTarget) {
        Logger.warning("'loadModel' is deprecated. Use 'createModel' instead.");
        return createModel(aModuleName, aTarget);
    }

    function createModel(aModuleName, aTarget) {
        if (arguments.length > 0) {
            if (window.platypusjs && window.platypusjs.getModelDocument) {
                var modelDoc = window.platypusjs.getModelDocument(aModuleName);
                if (modelDoc) {
                    return readModelDocument(modelDoc, aModuleName, aTarget);
                } else {
                    throw 'Model definition for module "' + aModuleName + '" is not found';
                }
            } else {
                throw "Prefetched model definitions are not accessible. Use Platypus.js AMD loader and switch 'prefetch' configuration flag on or call 'createModel()' without arguments to create a model without prefetch model definition.";
            }
        } else {
            return readModel('<?xml version="1.0" encoding="UTF-8"?><datamodel></datamodel>');
        }
    }

    var module = {};
    Object.defineProperty(module, 'Query', {
        get: function () {
            return Query;
        }
    });
    Object.defineProperty(module, 'Model', {
        get: function () {
            return Model;
        }
    });
    Object.defineProperty(module, 'loadModel', {
        enumerable: true,
        value: loadModel
    });
    Object.defineProperty(module, 'createModel', {
        enumerable: true,
        value: createModel
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