/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.store;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.DbSchemeModel2XmlDom;
import com.eas.client.model.dbscheme.FieldsEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Serializing for db structure export.
 * @author mg
 */
public class DbSchema2XmlDom extends DbSchemeModel2XmlDom {

    protected DbSchema2XmlDom() {
        super();
    }

    public static Document transform(DbSchemeModel dm)
    {
        DbSchema2XmlDom transformer = new DbSchema2XmlDom();
        return transformer.model2XmlDom(dm);
    }

    @Override
    public void visit(FieldsEntity entity) {
        if (entity != null) {
            Element node = doc.createElement(ENTITY_TAG_NAME);
            currentNode.appendChild(node);

            node.setAttribute(ENTITY_ID_ATTR_NAME, String.valueOf(entity.getEntityID()));
            node.setAttribute(TABLE_DB_ID_ATTR_NAME, String.valueOf(entity.getTableDbId()));
            node.setAttribute(TABLE_SCHEMA_NAME_ATTR_NAME, entity.getTableSchemaName());
            node.setAttribute(TABLE_NAME_ATTR_NAME, entity.getTableName());
            writeEntityDesignAttributes(node, entity);

            Node lcurrentNode = currentNode;
            try {
                currentNode = node;
                visit(entity.getFields());
            } finally {
                currentNode = lcurrentNode;
            }
        }
    }
    public static final String FIELDS_TAG_NAME = "Fields";

    public void visit(Fields aFields) {
        if (aFields != null && aFields.getFieldsCount() > 0) {
            Element node = doc.createElement(FIELDS_TAG_NAME);
            currentNode.appendChild(node);
            Node lcurrentNode = currentNode;
            try {
                currentNode = node;
                for (int i = 1; i <= aFields.getFieldsCount(); i++) {
                    Field field = aFields.get(i);
                    visit(field);
                }
            } finally {
                currentNode = lcurrentNode;
            }
        }
    }
}
