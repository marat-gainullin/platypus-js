define([
    'core/logger',
    'remote/requests',
    'core/utils',
    './model-reader',
    'core/invoke'], function (
        Logger,
        Requests,
        Utils,
        readModelDocument,
        Invoke) {
    var global = window;

    var SERVER_ENTITY_TOUCHED_NAME = "Entity ";

    var loadedEntities = new Map();

    function loadEntities(entitiesNames, onSuccess, onFailure) {
        if (entitiesNames.length > 0) {
            var process = new Utils.Process(entitiesNames.length, function () {
                onSuccess();
            }, function (aReasons) {
                onFailure(aReasons);
            });
            entitiesNames.forEach(function (entityName) {
                return Requests.requestEntity(entityName, function (entity) {
                    loadedEntities.set(entityName, entity);
                    process.onSuccess();
                }, function (reason) {
                    Logger.severe(reason);
                    process.onFailure(reason);
                });
                Logger.info('Loading ' + SERVER_ENTITY_TOUCHED_NAME + entityName + ' ...');
            });
        } else {
            Invoke.later(onSuccess);
        }
    }

    function requireEntities(aEntitiesNames, aOnSuccess, aOnFailure) {
        var entitiesNames;
        if (!Array.isArray(aEntitiesNames)) {
            aEntitiesNames = aEntitiesNames + "";
            if (aEntitiesNames.length > 5 && aEntitiesNames.trim().substring(0, 5).toLowerCase() === "<?xml") {
                entitiesNames = [];
                var pattern = /queryId="(.+?)"/ig;
                var groups = pattern.exec(aEntitiesNames);
                while (groups) {
                    if (groups.length > 1) {
                        entitiesNames.push(groups[1]);
                    }
                    groups = pattern.exec(aEntitiesNames);
                }
            } else {
                entitiesNames = [aEntitiesNames];
            }
        } else {
            entitiesNames = aEntitiesNames;
        }
        var toLoad = entitiesNames.filter(function (entityName) {
            return !loadedEntities.has(entityName);
        });
        loadEntities(toLoad, function () {
            var resolved = [];
            for (var i = 0; i < entitiesNames.length; i++) {
                resolved.push(loadedEntities.get(entitiesNames[i]));
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
            if (global.platypusjs && global.platypusjs.getModelDocument) {
                var modelDoc = global.platypusjs.getModelDocument(aModuleName);
                if (modelDoc) {
                    return readModelDocument(modelDoc, aModuleName, aTarget);
                } else {
                    throw 'Model definition for module "' + aModuleName + '" is not found';
                }
            } else {
                throw "Fetched model definitions are not accessible. Use Platypus.js AMD loader and switch 'autofetch' configuration flag on or call 'createModel()' without arguments to create a model without fetched model definition.";
            }
        } else {
            return readModel('<?xml version="1.0" encoding="UTF-8"?><datamodel></datamodel>');
        }
    }

    var module = {};
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