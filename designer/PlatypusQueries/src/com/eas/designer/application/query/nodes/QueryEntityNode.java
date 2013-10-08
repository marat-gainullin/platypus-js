/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.query.QueryEntity;
import com.eas.designer.datamodel.nodes.EntityNode;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import static org.openide.nodes.Sheet.PROPERTIES;
import org.openide.util.Lookup;

/**
 *
 * @author mg
 */
public class QueryEntityNode extends EntityNode<QueryEntity> {

    public static final String ALIAS_PROP_NAME = "alias";

    public QueryEntityNode(QueryEntity aEntity, UndoRedo.Manager aUndoReciever, Lookup aLookup) throws Exception {
        super(aEntity, aUndoReciever, new QueryEntityNodeChildren(aEntity, aUndoReciever, aLookup), aLookup);
    }

    @Override
    public String getName() {
        String schemaName = entity.getTableSchemaName();
        String tableName = entity.getTableName();
        String res = (schemaName != null && !schemaName.isEmpty()) ? schemaName + "." + tableName : tableName;
        return res != null ? res : "";
    }

    @Override
    public String getDisplayName() {
        String text = super.getDisplayName();
        if (text == null || text.isEmpty()) {
            text = DatamodelDesignUtils.getLocalizedString("noName");
        }
        return text;
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    protected Sheet createSheet() {
        try {
            Sheet sheet = super.createSheet();
            nameToProperty.remove(NAME_PROP_NAME);
            Sheet.Set defaultSet = sheet.get(PROPERTIES);
            defaultSet.remove(PROP_NAME);
            PropertySupport.Reflection<String> aliasProp = new PropertySupport.Reflection<>(entity, String.class, ALIAS_PROP_NAME);
            aliasProp.setName(ALIAS_PROP_NAME);
            defaultSet.put(aliasProp);
            nameToProperty.put(ALIAS_PROP_NAME, aliasProp);
            return sheet;
        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault().notify(ex);
            return super.createSheet();
        }
    }
}
