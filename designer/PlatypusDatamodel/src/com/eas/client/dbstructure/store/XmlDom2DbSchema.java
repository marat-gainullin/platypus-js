/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.store;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.DbClient;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.model.store.XmlDom2DbSchemeModel;
import com.eas.xml.dom.XmlDomUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class XmlDom2DbSchema extends XmlDom2DbSchemeModel {

    protected XmlDom2DbSchema(Document aDoc) {
        super(aDoc);
    }

    public static DbSchemeModel transform(DbClient aClient, Document aDoc) throws Exception {
        DbSchemeModel model = new DbSchemeModel(aClient);
        model.accept(new XmlDom2DbSchema(aDoc));
        return model;
    }

    @Override
    public void visit(FieldsEntity entity) {
        if (entity != null) {
            entity.setEntityId(readLongAttribute(Model2XmlDom.ENTITY_ID_ATTR_NAME, null));
            entity.setTableName(currentNode.getAttribute(Model2XmlDom.TABLE_NAME_ATTR_NAME));
            readEntityDesignAttributes(entity);
            DbSchemeModel dm = entity.getModel();
            if (dm != null) {
                dm.addEntity(entity);
            }
            Fields fields = new Fields();
            Element lcurrentNode = currentNode;
            try {
                Element paramsEl = getElementByTagName(currentNode, DbSchema2XmlDom.FIELDS_TAG_NAME);
                if (paramsEl != null) {
                    List<Element> pnl = XmlDomUtils.elementsByTagName(paramsEl, Model2XmlDom.PARAMETER_TAG_NAME);
                    Set<String> names = new HashSet<>();
                    for (int i = 0; i < pnl.size(); i++) {
                        currentNode = pnl.get(i);
                        Field param = new Field();
                        visit(param);
                        param.setTableName(entity.getTableName());
                        param.setSchemaName(entity.getTableSchemaName());
                        String paramName = param.getName();
                        if (paramName != null && !paramName.isEmpty() && !names.contains(paramName)) {
                            names.add(paramName);
                            fields.add(param);
                        }
                    }
                }
            } finally {
                currentNode = lcurrentNode;
            }
            entity.setFields(fields);
        }
    }
}
