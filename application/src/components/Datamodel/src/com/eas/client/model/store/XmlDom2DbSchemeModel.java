/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.DbClient;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.visitors.DbSchemeModelVisitor;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class XmlDom2DbSchemeModel extends XmlDom2Model<FieldsEntity> implements DbSchemeModelVisitor{

    public XmlDom2DbSchemeModel(Document aDoc) {
        super();
        doc = aDoc;
    }

    public static DbSchemeModel transform(DbClient aClient, Document aDoc) throws Exception {
        DbSchemeModel model = new DbSchemeModel(aClient);
        model.accept(new XmlDom2DbSchemeModel(aDoc));
        return model;
    }

    @Override
    public void visit(DbSchemeModel aModel) {
        readModel(aModel);
        if (currentNode.hasAttribute(Model2XmlDom.DATAMODEL_DB_ID)) {
            String dbIdAttr = currentNode.getAttribute(Model2XmlDom.DATAMODEL_DB_ID);
            if (dbIdAttr != null && !"null".equals(dbIdAttr)) {
                aModel.setDbId(dbIdAttr);
            }
        }
        String schemaAttr = currentNode.getAttribute(Model2XmlDom.DATAMODEL_DB_SCHEMA_NAME);
        if (schemaAttr != null && !"null".equals(schemaAttr)) {
            aModel.setSchema(schemaAttr);
        }
    }

    @Override
    public void visit(FieldsEntity entity) {
        if (entity != null) {
            entity.setEntityID(readLongAttribute(Model2XmlDom.ENTITY_ID_ATTR_NAME, null));
            entity.setTableName(currentNode.getAttribute(Model2XmlDom.TABLE_NAME_ATTR_NAME));
            readEntityDesignAttributes(entity);
            readOldUserData(entity);
            DbSchemeModel model = entity.getModel();
            if (model != null) {
                model.addEntity(entity);
            }
        }
    }
}
