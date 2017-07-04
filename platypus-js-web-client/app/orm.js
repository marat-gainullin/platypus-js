define(['logger'], function (Logger) {
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
        jsLoadQueries(entities, aOnSuccess, aOnFailure);
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
            if (window.platypusjs) {
                var modelDoc = window.platypusjs.documents.get(aModuleName + ".model");
                if (modelDoc) {
                    return readModelDocument(modelDoc, aModuleName, aTarget);
                } else {
                    throw 'Model definition for module "' + aModuleName + '" is not found';
                }
            } else {
                throw 'To use models cache for module "' + aModuleName + '", use Platypus.js modules loader please.';
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