define(['logger', 'client', 'internals'], function (Logger, Client, Utils) {

    var SERVER_ENTITY_TOUCHED_NAME = "Entity-";

    var loadedEntities = new Map();

    function loadEntities(aQueriesNames, onSuccess, onFailure) {
        if (aQueriesNames.length > 0) {
            var process = new Utils.Process(aQueriesNames.size, function () {
                onSuccess();
            }, function (aReasons) {
                onFailure(aReasons);
            });
            aQueriesNames.forEach(function (queryName) {
                var urlQuery = Client.params(Client.param(Client.PlatypusRequestParams.TYPE, Client.Requests.rqAppQuery + ''), Client.param(Client.PlatypusRequestParams.QUERY_ID, queryName));
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
                Logger.info('Loading ' + SERVER_ENTITY_TOUCHED_NAME + queryName + '...');
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

    function readModelDocument(aDocument, aModuleName, aTarget) {
        if (!aTarget)
            aTarget = {};
        var nativeModel = /*@com.eas.model.store.XmlDom2Model::*/transform(aDocument, aModuleName, aTarget);
        if (nativeModel) {
            //nativeModel.@com.eas.model.Model::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(aTarget);			
            return aTarget;
        } else {
            return null;
        }
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