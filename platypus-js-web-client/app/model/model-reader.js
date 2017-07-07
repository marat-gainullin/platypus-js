define(['orm', 'entity', 'logger'], function (Orm, Entity, Logger) {

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
        var model = new Orm.Model();

        function getAttribute(aElement, aShortName, aLongName, defaultValue) {
            if (aElement.hasAttribute(aShortName))
                return aElement.hasAttribute(aShortName);
            else if (aElement.hasAttribute(aLongName))
                return aElement.hasAttribute(aLongName);
            else
                return defaultValue;
        }

        var relationsResolvers = [];

        function readEntity(element) {
            var entity = new Entity();
            var name = getAttribute(element, "n", DATASOURCE_NAME_ATTR_NAME, null);
            if (name) {
                entity.name = name;
            }
            var title = getAttribute(element, "tt", DATASOURCE_TITLE_ATTR_NAME, null);
            if (title) {
                entity.title = title;
            }
            var entityId = getAttribute(element, "ei", ENTITY_ID_ATTR_NAME, null);
            if ("null".equals(entityId)) {
                entityId = null;
            }
            if (!entityId)
                throw "Entity id must present";
            entity.setEntityId(entityId);
            var queryId = getAttribute(element, "qi", QUERY_ID_ATTR_NAME, null);
            if ("null".equals(queryId)) {
                queryId = null;
            }
            if (!queryId)
                throw "Query name must present";
            entity.query = new Orm.Query(queryId);
            return entity;
        }

        function readRelation(element) {
            var relation = new Orm.Relation();
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
                    var lEntity = model.getEntityById(leftEntityId);
                    if (lEntity) {
                        relation.leftEntity = lEntity;
                        lEntity.addOutRelation(relation);
                    } else {
                        Logger.severe('Relation has no left entity');
                    }
                    var rEntity = model.getEntityById(rightEntityId);
                    if (rEntity) {
                        relation.rightEntity = rEntity;
                        rEntity.addInRelation(relation);
                    } else {
                        Logger.severe('Relation has no right entity');
                    }
                } catch (ex) {
                    Logger.severe(ex);
                }
            });
            return relation;
        }

        function readReferenceRelation(element) {
            var referenceRelation = new Orm.ReferenceRelation();
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
                    var lEntity = model.getEntityById(leftEntityId);
                    if (lEntity) {
                        relation.leftEntity = lEntity;
                    } else {
                        Logger.severe('Reference relation has no left entity');
                    }
                    var rEntity = model.getEntityById(rightEntityId);
                    if (rEntity) {
                        relation.rightEntity = rEntity;
                    } else {
                        Logger.severe('Reference relation has no right entity');
                    }
                } catch (ex) {
                    Logger.severe(ex);
                }
            });
            return referenceRelation;
        }

        if (modelElement && model) {
            var nl = modelElement.childNodes;
            if (nl && nl.length > 0) {
                for (var i = 0; i < nl.length; i++) {
                    var nodeName = nl[i].nodeName;
                    if ("e" === nodeName || ENTITY_TAG_NAME === nodeName) {
                        var entity = readEntity(nl[i]);
                        entity.setModel(model);
                        model.addEntity(entity);
                    } else if ("r" === nodeName || RELATION_TAG_NAME === nodeName) {
                        var relation = readRelation(nl[i], relation);
                        model.addRelation(relation);
                    } else if ("rr" === nodeName || REFERENCE_RELATION_TAG_NAME === nodeName) {
                        var referenceRelation = readReferenceRelation(nl[i], referenceRelation);
                        model.addReferenceRelation(referenceRelation);
                    }
                }
            }
            relationsResolvers.forEach(function (resolver) {
                resolver();
            });
            model.checkRelationsIntegrity();
            model.checkReferenceRelationsIntegrity();
        }
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