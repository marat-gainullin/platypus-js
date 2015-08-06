/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.view;

import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.view.EntityViewsManager;
import com.eas.client.model.gui.view.FieldsParametersListCellRenderer;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import javax.swing.Icon;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class TableEntityView extends EntityView<FieldsEntity> {

    protected TypesResolver resolver;

    public TableEntityView(FieldsEntity aEntity, EntityViewsManager<FieldsEntity> aMovesManager) throws Exception {
        super(aEntity, aMovesManager);
    }

    @Override
    public void initFieldsParamsRenderer() throws Exception {
        fieldsParamsRenderer = new FieldsParametersListCellRenderer<FieldsEntity>(DatamodelDesignUtils.getFieldsFont(), DatamodelDesignUtils.getBindedFieldsFont(), entity) {

            @Override
            protected Icon calcIcon(String typeName) {
                try {
                    if (resolver == null) {
                        resolver = getEntity().getModel().getBasesProxy().getConnectionDriver(getEntity().getModel().getDatasourceName()).getTypesResolver();
                    }
                    String appTypeName = resolver.toApplicationType(typeName);
                    return super.calcIcon(appTypeName);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    return null;
                }
            }

        };
    }

    @Override
    protected boolean isEditable() {
        return true;
    }

    @Override
    protected boolean isParameterized() {
        return false;
    }
}
