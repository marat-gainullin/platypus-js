/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.SqlAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.designer.datamodel.nodes.EntityNode;
import java.beans.PropertyChangeEvent;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author mg
 */
public class TableEntityNode extends EntityNode<FieldsEntity> {

    public TableEntityNode(FieldsEntity aEntity, Lookup aLookup) throws Exception {
        super(aEntity, null, new TableEntityNodeChildren(aEntity, null, aLookup), aLookup);
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public String getName() {
        return entity.getTableName();
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        nameToProperty.remove(NAME_PROP_NAME);
        return sheet;
    }

    @Override
    public void processNodePropertyChange(PropertyChangeEvent evt) {
        if (TITLE_PROP_NAME.equals(evt.getPropertyName())) {
            try {
                DbSchemeModel dbModel = entity.getModel();
                SqlActionsController sqlController = new SqlActionsController(dbModel);
                sqlController.setBasesProxy(dbModel.getBasesProxy());
                sqlController.setDatasourceName(dbModel.getDatasourceName());
                sqlController.setSchema(dbModel.getSchema());
                SqlAction laction = sqlController.createDescribeTableAction(entity.getTableName(), (String) evt.getNewValue());
                if (!laction.execute()) {
                    DbActionException ex = new DbActionException(laction.getErrorString());
                    ex.setParam1(entity.getTableName());
                    throw ex;
                }
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                return;
            }
        }
        super.processNodePropertyChange(evt);
    }
}
