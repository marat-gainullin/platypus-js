/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.datamodel.gui.selectors.ModelElementSelector;
import com.eas.client.datamodel.gui.selectors.ModelElementValidator;
import com.eas.client.geo.FeatureStyleDescriptor;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import com.eas.dbcontrols.map.customizer.FeaturesListModel;
import com.eas.dbcontrols.map.customizer.edits.ModifyStyleEdit;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;

/**
 *
 * @author pk
 */
public class SelectLabelFieldAction extends DbMapChangeAction implements ModelElementValidator {

    private ModelElementRef featureDatasource;

    public SelectLabelFieldAction(DbMapCustomizer aCustomizer) {
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
                final FeatureStyleDescriptor oldStyle = feature.getStyle();
                final FeatureStyleDescriptor newStyle = oldStyle.clone();
                featureDatasource = feature.getRef();
                final ModelElementRef ref = ModelElementSelector.selectDatamodelElement(customizer.getDatamodel(),
                        oldStyle.getLabelField() == null ? featureDatasource : oldStyle.getLabelField(),
                        ModelElementSelector.STRICT_DATASOURCE_FIELD_SELECTION_SUBJECT, this, customizer, "Select label field...");
                if (ref != null && !ref.equals(oldStyle.getLabelField())) {
                    final ModifyStyleEdit edit = new ModifyStyleEdit(feature, oldStyle, newStyle);
                    newStyle.setLabelField(ref);
                    feature.setStyle(newStyle);
                    customizer.getUndoSupport().postEdit(edit);
                }
            } catch (Exception ex) {
                Logger.getLogger(ClearStyleFieldAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean validateDatamodelElementSelection(ModelElementRef der) {
        return featureDatasource != null && der != null && featureDatasource.getEntityId() != null
                && featureDatasource.getEntityId().equals(der.getEntityId());
    }
}
