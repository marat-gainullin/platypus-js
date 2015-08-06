/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.entities;

import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.view.EntityViewsManager;
import com.eas.client.model.gui.view.FieldsParametersListCellRenderer;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.beans.PropertyChangeEvent;
import javax.swing.Icon;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class QueryEntityView extends EntityView<QueryEntity> {

    protected TypesResolver resolver;

    public QueryEntityView(QueryEntity aEntity, EntityViewsManager<QueryEntity> aMovesManager) throws Exception {
        super(aEntity, aMovesManager);
        entity.getChangeSupport().addPropertyChangeListener(QueryEntity.ALIAS_PROPERTY, (PropertyChangeEvent evt) -> {
            titleLabel.setText(getCheckedEntityTitle());
            titleLabel.invalidate();
            reLayout();
        });
    }

    @Override
    public void initFieldsParamsRenderer() throws Exception {
        fieldsParamsRenderer = new FieldsParametersListCellRenderer<QueryEntity>(DatamodelDesignUtils.getFieldsFont(), DatamodelDesignUtils.getBindedFieldsFont(), entity) {

            @Override
            protected Icon calcIcon(String typeName) {
                try {
                    if (resolver == null) {
                        resolver = getEntity().getModel().getBasesProxy().getConnectionDriver(getEntity().getModel().getDatasourceName()).getTypesResolver();
                    }
                    Icon res = super.calcIcon(typeName);
                    if (res == null) {
                        String appTypeName = resolver.toApplicationType(typeName);
                        return super.calcIcon(appTypeName);
                    } else {
                        return res;
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    return null;
                }
            }

        };
    }

    @Override
    protected boolean isEditable() {
        return false;
    }

    @Override
    protected boolean isParameterized() {
        return true;
    }
}
