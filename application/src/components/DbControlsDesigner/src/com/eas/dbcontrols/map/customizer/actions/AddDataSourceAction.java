/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.datamodel.ApplicationEntity;
import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.datamodel.gui.selectors.ModelElementSelector;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.DbControlsDesignUtils;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import com.eas.dbcontrols.map.customizer.edits.ModifyFeaturesEdit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pk
 */
public class AddDataSourceAction extends DbMapChangeAction {

    public AddDataSourceAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isEnabled()) {
            final DbMap map = (DbMap) customizer.getBean();
            final ModelElementRef selectedEntity = ModelElementSelector.selectDatamodelElement(customizer.getDatamodel(), null, ModelElementSelector.DATASOURCE_SELECTION_SUBJECT, null, customizer, DbControlsDesignUtils.getLocalizedString("selectDatasource"));
            if (selectedEntity != null) {
                final ApplicationEntity featureEntity = customizer.getDatamodel().getEntityById(selectedEntity.getEntityId());
                final RowsetFeatureDescriptor newFeature = new RowsetFeatureDescriptor(featureEntity.getTitle(), featureEntity, selectedEntity);
                final List<RowsetFeatureDescriptor> featuresAfter = new ArrayList<>(map.getDesignInfo().getFeatures());
                int selIndex = customizer.getFeaturesList().getSelectedIndex();
                if (selIndex == -1) {
                    selIndex = customizer.getFeaturesList().getModel().getSize() - 1;
                }
                featuresAfter.add(selIndex + 1, newFeature);
                final ModifyFeaturesEdit edit = new ModifyFeaturesEdit(map.getDesignInfo(), map.getDesignInfo().getFeatures(), featuresAfter);
                edit.redo();
                customizer.getUndoSupport().postEdit(edit);
                customizer.getFeaturesList().setSelectedIndex(selIndex + 1);
            }
        }
    }
}
