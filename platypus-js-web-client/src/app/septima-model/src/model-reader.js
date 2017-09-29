define([
    './model',
    './entity',
    'core/logger'], function (
        Model,
        Entity,
        Logger) {

    var ENTITY_TAG_NAME = "entity";
    var RELATION_TAG_NAME = "relation";
    var REFERENCE_RELATION_TAG_NAME = "referenceRelation";
    var ENTITY_ID_ATTR_NAME = "entityId";
    var QUERY_ID_ATTR_NAME = "queryId";
    var DATASOURCE_NAME_ATTR_NAME = "Name";
    var DATASOURCE_TITLE_ATTR_NAME = "Title";
    var LEFT_ENTITY_ID_ATTR_NAME = "leftEntityId";
    var LEFT_ENTITY_FIELD_ATTR_NAME = "leftEntityFieldName";
    var LEFT_ENTITY_PARAMETER_ATTR_NAME = "leftEntityParameterName";
    var RIGHT_ENTITY_ID_ATTR_NAME = "rightEntityId";
    var RIGHT_ENTITY_FIELD_ATTR_NAME = "rightEntityFieldName";
    var RIGHT_ENTITY_PARAMETER_ATTR_NAME = "rightEntityParameterName";
    var SCALAR_PROP_NAME_ATTR_NAME = "scalarPropertyName";
    var COLLECTION_PROP_NAME_ATTR_NAME = "collectionPropertyName";

    function modelElementByBundleName(aElement, aBundleName) {
        if (aElement.tagName === "datamodel") {
            return aElement;// The high level code had to do everything in the right way
        } else {
            var child = aElement.firstElementChild;
            while (child) {
                if (child.hasAttribute("bundle-name")) {
                    var bundleName = child.getAttribute("bundle-name");
                    if (bundleName === aBundleName) {
                        return child;
                    }
                }
                child = child.nextElementSibling;
            }
        }
        return null;
    }

    function transform(modelElement) {
        var model = new Model();

        function getAttribute(aElement, aShortName, aLongName, defaultValue) {
            if (aElement.hasAttribute(aShortName))
                return aElement.getAttribute(aShortName);
            else if (aElement.hasAttribute(aLongName))
                return aElement.getAttribute(aLongName);
            else
                return defaultValue;
        }

        var relationsResolvers = [];
        var entitiesById = new Map();

        function readEntity(element) {
            var serverEntityName = getAttribute(element, "qi", QUERY_ID_ATTR_NAME, null);
            if ("null" === serverEntityName) {
                serverEntityName = null;
            }
            if (!serverEntityName)
                throw "Server entity name ('" + QUERY_ID_ATTR_NAME + "' or 'qi' attribute) must present";
            var entityId = getAttribute(element, "ei", ENTITY_ID_ATTR_NAME, null);
            if ("null" === entityId) {
                entityId = null;
            }
            if (!entityId)
                throw "Entity id ('" + ENTITY_ID_ATTR_NAME + "' or 'n' attribute) must present";
            var entity = new Entity(serverEntityName);
            var name = getAttribute(element, "n", DATASOURCE_NAME_ATTR_NAME, null);
            if (!name) {
                throw "Entity variable name ('" + DATASOURCE_NAME_ATTR_NAME + "' or 'n' attribute) must present";
            }
            entity.name = name;
            var title = getAttribute(element, "tt", DATASOURCE_TITLE_ATTR_NAME, null);
            if (title) {
                entity.title = title;
            }
            entitiesById.set(entityId, entity);
            model.addEntity(entity);
        }

        function readRelation(element) {
            var relation = {};
            var leftEntityId = getAttribute(element, "lei", LEFT_ENTITY_ID_ATTR_NAME, null);
            var leftFieldName = getAttribute(element, "lef", LEFT_ENTITY_FIELD_ATTR_NAME, null);
            var leftParameterName = getAttribute(element, "lep", LEFT_ENTITY_PARAMETER_ATTR_NAME, null);
            var rightEntityId = getAttribute(element, "rei", RIGHT_ENTITY_ID_ATTR_NAME, null);
            var rightFieldName = getAttribute(element, "ref", RIGHT_ENTITY_FIELD_ATTR_NAME, null);
            var rightParameterName = getAttribute(element, "rep", RIGHT_ENTITY_PARAMETER_ATTR_NAME, null);
            if (leftParameterName) {
                relation.leftItem = leftParameterName;
                relation.leftParameter = true;
            } else if (leftFieldName) {
                relation.leftItem = leftFieldName;
                relation.leftParameter = false;
            }
            if (rightParameterName) {
                relation.rightItem = rightParameterName;
                relation.rightParameter = true;
            } else if (rightFieldName) {
                relation.rightItem = rightFieldName;
                relation.rightParameter = false;
            }
            relationsResolvers.push(function () {
                try {
                    var lEntity = entitiesById.get(leftEntityId);
                    if (lEntity) {
                        relation.leftEntity = lEntity;
                        lEntity.addOutRelation(relation);
                    } else {
                        Logger.severe('Relation has no left entity. Entity id is: ' + leftEntityId);
                    }
                    var rEntity = entitiesById.get(rightEntityId);
                    if (rEntity) {
                        relation.rightEntity = rEntity;
                        rEntity.addInRelation(relation);
                    } else {
                        Logger.severe('Relation has no right entity. Entity id is: ' + rightEntityId);
                    }
                    if (lEntity && rEntity)
                        model.addRelation(relation);
                } catch (ex) {
                    Logger.severe(ex);
                }
            });
        }

        function readReferenceRelation(element) {
            var referenceRelation = {};
            var leftEntityId = getAttribute(element, "lei", LEFT_ENTITY_ID_ATTR_NAME, null);
            var rightEntityId = getAttribute(element, "rei", RIGHT_ENTITY_ID_ATTR_NAME, null);
            var leftFieldName = getAttribute(element, "lef", LEFT_ENTITY_FIELD_ATTR_NAME, null);
            var rightFieldName = getAttribute(element, "ref", RIGHT_ENTITY_FIELD_ATTR_NAME, null);
            var scalarPropertyName = getAttribute(element, "spn", SCALAR_PROP_NAME_ATTR_NAME, null);
            var collectionPropertyName = getAttribute(element, "cpn", COLLECTION_PROP_NAME_ATTR_NAME, null);
            referenceRelation.leftField = leftFieldName ? leftFieldName.trim() : null;
            referenceRelation.rightField = rightFieldName ? rightFieldName.trim() : null;
            referenceRelation.scalarPropertyName = scalarPropertyName ? scalarPropertyName.trim() : null;
            referenceRelation.collectionPropertyName = collectionPropertyName ? collectionPropertyName.trim() : null;

            relationsResolvers.push(function () {
                try {
                    var lEntity = entitiesById.get(leftEntityId);
                    if (lEntity) {
                        referenceRelation.leftEntity = lEntity;
                    } else {
                        Logger.severe('Reference relation has no left entity. Entity id is: ' + leftEntityId);
                    }
                    var rEntity = entitiesById.get(rightEntityId);
                    if (rEntity) {
                        referenceRelation.rightEntity = rEntity;
                    } else {
                        Logger.severe('Reference relation has no right entity. Entity id is: ' + rightEntityId);
                    }
                    if (lEntity && rEntity)
                        model.addAssociation(referenceRelation);
                } catch (ex) {
                    Logger.severe(ex);
                }
            });
        }

        if (modelElement && model) {
            var nl = modelElement.childNodes;
            if (nl && nl.length > 0) {
                for (var i = 0; i < nl.length; i++) {
                    var nodeName = nl[i].nodeName;
                    if ("e" === nodeName || ENTITY_TAG_NAME === nodeName) {
                        readEntity(nl[i]);
                    } else if ("r" === nodeName || RELATION_TAG_NAME === nodeName) {
                        readRelation(nl[i]);
                    } else if ("rr" === nodeName || REFERENCE_RELATION_TAG_NAME === nodeName) {
                        readReferenceRelation(nl[i]);
                    }
                }
            }
            relationsResolvers.forEach(function (resolver) {
                resolver();
            });
            model.processAssociations();
        }
        return model;
    }
    function read(aDocument, aModuleName) {
        try {
            var modelElement = aModuleName ? modelElementByBundleName(aDocument.documentElement, aModuleName) : aDocument.documentElement;
            if (modelElement) {
                return transform(modelElement);
            } else {
                return null;
            }
        } catch (ex) {
            Logger.severe(ex);
            throw ex;
        }
    }

    return read;
});