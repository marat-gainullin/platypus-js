/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.DatabasesClient;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.xml.dom.XmlDomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class XmlDom2DbSchemeModel extends XmlDom2Model<FieldsEntity, DbSchemeModel> implements ModelVisitor<FieldsEntity, DbSchemeModel> {

    public XmlDom2DbSchemeModel(Document aDoc) {
        super();
        doc = aDoc;
    }

    public XmlDom2DbSchemeModel(Element aTag) {
        super();
        modelElement = aTag;
    }

    public static DbSchemeModel transform(DatabasesClient aClient, Document aDoc) throws Exception {
        DbSchemeModel model = new DbSchemeModel(aClient);
        model.accept(new XmlDom2DbSchemeModel(aDoc));
        return model;
    }

    @Override
    public void visit(DbSchemeModel aModel) {
        readModel(aModel);
        if (XmlDomUtils.hasAttribute(currentNode, "ds", Model2XmlDom.DATAMODEL_DATASOURCE)) {
            String datasourceAttr = XmlDomUtils.getAttribute(currentNode, "ds", Model2XmlDom.DATAMODEL_DATASOURCE);
            if (datasourceAttr != null && !"null".equals(datasourceAttr)) {
                aModel.setDatasourceName(datasourceAttr);
            }
        } else {
            // legacy code
            if (XmlDomUtils.hasAttribute(currentNode, "ddi", Model2XmlDom.DATAMODEL_DB_ID)) {
                String dbIdAttr = XmlDomUtils.getAttribute(currentNode, "ddi", Model2XmlDom.DATAMODEL_DB_ID);
                if (dbIdAttr != null && !"null".equals(dbIdAttr)) {
                    aModel.setDatasourceName(dbIdAttr);
                }
            }
        }
        if (XmlDomUtils.hasAttribute(currentNode, "s", Model2XmlDom.DATAMODEL_DATASOURCE_SCHEMA_NAME)) {
            String schemaAttr = XmlDomUtils.getAttribute(currentNode, "s", Model2XmlDom.DATAMODEL_DATASOURCE_SCHEMA_NAME);
            if (schemaAttr != null && !"null".equals(schemaAttr)) {
                aModel.setSchema(schemaAttr);
            }
        } else if (XmlDomUtils.hasAttribute(currentNode, "dsn", Model2XmlDom.DATAMODEL_DB_SCHEMA_NAME)) {
            // legacy code
            String schemaAttr = XmlDomUtils.getAttribute(currentNode, "dsn", Model2XmlDom.DATAMODEL_DB_SCHEMA_NAME);
            if (schemaAttr != null && !"null".equals(schemaAttr)) {
                aModel.setSchema(schemaAttr);
            }
        } else {
            aModel.setSchema("");
        }
    }

    @Override
    public void visit(FieldsEntity entity) {
        if (entity != null) {
            entity.setEntityId(readLongAttribute(currentNode, "ei", Model2XmlDom.ENTITY_ID_ATTR_NAME, null));
            entity.setTableName(XmlDomUtils.getAttribute(currentNode, "tn", Model2XmlDom.TABLE_NAME_ATTR_NAME));
            readEntityDesignAttributes(entity);
            DbSchemeModel model = entity.getModel();
            if (model != null) {
                model.addEntity(entity);
            }
        }
    }
}
