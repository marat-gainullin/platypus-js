/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.geo.RowsetFeatureDescriptor;
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
public class ClearTypeFieldAction extends DbMapChangeAction {

    public ClearTypeFieldAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
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
                final ModelElementRef newValue = null;
                if (oldValue != null) {
                    final ModifyTypeFieldEdit edit = new ModifyTypeFieldEdit(feature, oldValue, newValue);
                    feature.setTypeRef(newValue);
                    customizer.getUndoSupport().postEdit(edit);
                    customizer.updateFeatureTypeView();
                }
            } catch (Exception ex) {
                Logger.getLogger(ClearTypeFieldAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        final JList featuresList = ((DbMapCustomizer) customizer).getFeaturesList();
        final DbMapDesignInfo designInfo = ((FeaturesListModel) featuresList.getModel()).getDesignInfo();
        final int[] selectedIndices = featuresList.getSelectedIndices();
        if (selectedIndices != null && selectedIndices.length == 1) {
            final RowsetFeatureDescriptor feature = designInfo.getFeatures().get(selectedIndices[0]);
            return feature.getTypeRef() != null;
        }
        return false;
    }
}
