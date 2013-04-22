/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.datamodel.gui.selectors.ModelElementSelector;
import com.eas.client.datamodel.gui.selectors.ModelElementValidator;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import com.eas.dbcontrols.map.customizer.FeaturesListModel;
import com.eas.dbcontrols.map.customizer.edits.ModifyTypeFieldEdit;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;

/**
 *
 * @author mg
 */
public class SelectTypeFieldAction extends DbMapChangeAction implements ModelElementValidator {

    private ModelElementRef featureDatasource;

    public SelectTypeFieldAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
    }

    @Override
    public boolean isEnabled() {
        if (customizer != null && customizer.getDatamodel() != null && customizer instanceof DbMapCustomizer && customizer.getBean() instanceof DbMap) {
            final JList featuresList = ((DbMapCustomizer) customizer).getFeaturesList();
            final int[] selectedIndices = featuresList.getSelectedIndices();
            return selectedIndices != null && selectedIndices.length == 1;
        } else {
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JList featuresList = ((DbMapCustomizer) customizer).getFeaturesList();
        final DbMapDesignInfo designInfo = ((FeaturesListModel) featuresList.getModel()).getDesignInfo();
        final int[] selectedIndices = featuresList.getSelectedIndices();
        if (selectedIndices != null && selectedIndices.length == 1) {
            try {
                final RowsetFeatureDescriptor feature = designInfo.getFeatures().get(selectedIndices[0]);
                final ModelElementRef oldValue = feature.getTypeRef();
                featureDatasource = feature.getRef();
                final ModelElementRef newValue = ModelElementSelector.selectDatamodelElement(customizer.getDatamodel(),
                        oldValue == null ? featureDatasource : oldValue,
                        ModelElementSelector.STRICT_DATASOURCE_FIELD_SELECTION_SUBJECT, this, customizer, "Select type field...");
                if (newValue != null && !newValue.equals(oldValue)) {
                    final ModifyTypeFieldEdit edit = new ModifyTypeFieldEdit(feature, oldValue, newValue);
                    feature.setTypeRef(newValue);
                    customizer.getUndoSupport().postEdit(edit);
                    customizer.updateFeatureTypeView();
                }
            } catch (Exception ex) {
                Logger.getLogger(ClearStyleFieldAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean validateDatamodelElementSelection(ModelElementRef aSubject) {
        return featureDatasource != null && aSubject != null && featureDatasource.getEntityId() != null
                && featureDatasource.getEntityId().equals(aSubject.getEntityId());
    }
}
