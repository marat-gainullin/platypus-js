/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.visitors.ModelVisitor;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class DbSchemeModel2XmlDom extends Model2XmlDom<FieldsEntity, DbSchemeModel> implements ModelVisitor<FieldsEntity, DbSchemeModel> {

    public static Document transform(DbSchemeModel aModel) {
        DbSchemeModel2XmlDom transformer = new DbSchemeModel2XmlDom();
        return transformer.model2XmlDom(aModel);
    }

    @Override
    public void visit(FieldsEntity entity) {
        if (entity != null) {
            Element node = doc.createElement(FIELDS_ENTITY_TAG_NAME);
            currentNode.appendChild(node);

            node.setAttribute(ENTITY_ID_ATTR_NAME, String.valueOf(entity.getEntityId()));
            node.setAttribute(TABLE_NAME_ATTR_NAME, entity.getTableName());
            writeEntityDesignAttributes(node, entity);
        }
    }

    @Override
    public void visit(DbSchemeModel aModel) {
        Set<Relation<FieldsEntity>> rels = aModel.getRelations();
        aModel.setRelations(null);// Avoid relations serialization. All Neccessary information will be restorerd from database
        try {
            writeModel(aModel);
        } finally {
            aModel.setRelations(rels);
        }
        if (currentNode != null && currentNode instanceof Element) {
            Element el = (Element) currentNode;
            el.setAttribute(DATAMODEL_DATASOURCE, String.valueOf(aModel.getDatasourceName()));
            String lSchema = aModel.getSchema();
            if (lSchema != null) {
                el.setAttribute(DATAMODEL_DATASOURCE_SCHEMA_NAME, String.valueOf(lSchema));
            }
        }
    }
}
