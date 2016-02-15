/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.Relation;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.model.visitors.QueryModelVisitor;
import com.eas.client.queries.QueriesProxy;
import com.eas.xml.dom.XmlDomUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class XmlDom2QueryModel extends XmlDom2Model<QueryEntity, QueryModel> implements QueryModelVisitor {

    public XmlDom2QueryModel(Document aDoc) {
        super();
        doc = aDoc;
    }

    public XmlDom2QueryModel(Element aModelElement) {
        super();
        modelElement = aModelElement;
    }

    public static QueryModel transform(DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aQueries, Document aDoc) throws Exception {
        QueryModel model = new QueryModel(aBasesProxy, aQueries);
        model.accept(new XmlDom2QueryModel(aDoc));
        return model;
    }

    public static QueryModel transform(DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aQueries, Element aModelElement) throws Exception {
        QueryModel model = new QueryModel(aBasesProxy, aQueries);
        model.accept(new XmlDom2QueryModel(aModelElement));
        return model;
    }

    @Override
    protected void readEntities(QueryModel aModel) {
        Element paramsEl = XmlDomUtils.getElementByTagName(currentNode, "ps", Model2XmlDom.PARAMETERS_TAG_NAME);
        if (paramsEl != null) {
            Parameters parameters = aModel.getParameters();
            List<Element> pnl = XmlDomUtils.elementsByTagName(paramsEl, "p", Model2XmlDom.PARAMETER_TAG_NAME);
            if (pnl != null && parameters != null) {
                Element lcurrentNode = currentNode;
                try {
                    Set<String> names = new HashSet<>();
                    pnl.stream().forEach((Element pnl1) -> {
                        currentNode = pnl1;
                        Parameter param = new Parameter();
                        visit(param);
                        String paramName = param.getName();
                        if (paramName != null && !paramName.isEmpty() && !names.contains(paramName)) {
                            names.add(paramName);
                            parameters.add(param);
                        }
                    });
                } finally {
                    currentNode = lcurrentNode;
                }
            }
        }
        Element paramsEntityEl = XmlDomUtils.getElementByTagName(currentNode, "pe", Model2XmlDom.PARAMETERS_ENTITY_TAG_NAME);
        if (paramsEntityEl != null) {
            QueryEntity pe = aModel.getParametersEntity();
            if (pe != null) {
                Element lcurrentNode = currentNode;
                try {
                    currentNode = paramsEntityEl;
                    pe.accept(this);
                } finally {
                    currentNode = lcurrentNode;
                }
            }
        }
        super.readEntities(aModel);
    }

    @Override
    protected void resolveRelation(QueryModel aModel, Long leftEntityId, String leftParameterName, Relation<QueryEntity> relation, String leftFieldName, Long rightEntityId, String rightParameterName, String rightFieldName) {
        super.resolveRelation(aModel, leftEntityId, leftParameterName, relation, leftFieldName, rightEntityId, rightParameterName, rightFieldName);
        try {
            if (QueryModel.PARAMETERS_ENTITY_ID == leftEntityId) {
                QueryParametersEntity lEntity = aModel.getParametersEntity();
                if (leftParameterName != null && !leftParameterName.isEmpty()) {
                    Fields fields = lEntity.getFields();
                    if (fields != null) {
                        relation.setLeftField(fields.get(leftParameterName));
                    } else if (!aModel.isRelationsAgressiveCheck()) {
                        relation.setLeftField(new Parameter(leftParameterName));
                    }
                } else if (leftFieldName != null && !leftFieldName.isEmpty()) {
                    Fields fields = lEntity.getFields();
                    if (fields != null) {
                        relation.setLeftField(fields.get(leftFieldName));
                    } else if (!aModel.isRelationsAgressiveCheck()) {
                        relation.setLeftField(new Parameter(leftFieldName));
                    }
                }
                if (lEntity != null) {
                    relation.setLeftEntity(lEntity);
                    lEntity.addOutRelation(relation);
                }
            }
            if (QueryModel.PARAMETERS_ENTITY_ID == rightEntityId) {
                QueryParametersEntity rEntity = aModel.getParametersEntity();
                if (rightParameterName != null && !rightParameterName.isEmpty()) {
                    Fields fields = rEntity.getFields();
                    if (fields != null) {
                        relation.setRightField(fields.get(rightParameterName));
                    } else if (!aModel.isRelationsAgressiveCheck()) {
                        relation.setRightField(new Parameter(rightParameterName));
                    }
                } else if (rightFieldName != null && !rightFieldName.isEmpty()) {
                    Fields fields = rEntity.getFields();
                    if (fields != null) {
                        relation.setRightField(fields.get(rightFieldName));
                    } else if (!aModel.isRelationsAgressiveCheck()) {
                        relation.setRightField(new Parameter(rightFieldName));
                    }
                }
                if (rEntity != null) {
                    relation.setRightEntity(rEntity);
                    rEntity.addInRelation(relation);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(XmlDom2Model.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    @Override
    public void visit(QueryModel aModel) {
        Runnable resolver = readModel(aModel);
        if (XmlDomUtils.hasAttribute(currentNode, "ds", Model2XmlDom.DATAMODEL_DATASOURCE)) {
            String datasourceName = XmlDomUtils.getAttribute(currentNode, "ds", Model2XmlDom.DATAMODEL_DATASOURCE);
            if (datasourceName != null && !"null".equals(datasourceName)) {
                aModel.setDatasourceName(datasourceName);
            }
        } else {
            // legacy code
            if (XmlDomUtils.hasAttribute(currentNode, "ddi", Model2XmlDom.DATAMODEL_DB_ID)) {
                String datasourceName = XmlDomUtils.getAttribute(currentNode, "ddi", Model2XmlDom.DATAMODEL_DB_ID);
                if (datasourceName != null && !"null".equals(datasourceName)) {
                    aModel.setDatasourceName(datasourceName);
                }
            }
        }
        aModel.getEntities().values().stream().forEach((e) -> {
            try {
                aModel.getQueries().getQuery(e.getQueryName(), null, null, null);
                e.validateQuery();
            } catch (Exception ex) {
                Logger.getLogger(XmlDom2QueryModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        resolver.run();
    }

    @Override
    public void visit(QueryParametersEntity entity) {
        readEntityDesignAttributes(entity);
    }

    @Override
    public void visit(QueryEntity entity) {
        entity.setAlias(XmlDomUtils.getAttribute(currentNode, "ta", Model2XmlDom.ENTITY_TABLE_ALIAS));
        entity.setTitle(XmlDomUtils.getAttribute(currentNode, "tt", Model2XmlDom.DATASOURCE_TITLE_ATTR_NAME));
        readEntity(entity);
    }

    @Override
    public void visit(Relation<QueryEntity> relation) {
        super.visit(relation);
        if (currentModel != null) {
            currentModel.addRelation(relation);
        }
    }
}
