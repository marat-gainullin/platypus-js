/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.geo.FeatureStyleDescriptor;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.fontchooser.FontChooser;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import com.eas.dbcontrols.map.customizer.FeaturesListModel;
import com.eas.dbcontrols.map.customizer.edits.ModifyStyleEdit;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;

/**
 *
 * @author pk
 */
public class SelectLabelFontAction extends DbMapChangeAction
{
    public SelectLabelFontAction(DbMapCustomizer aCustomizer)
    {
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
    public void actionPerformed(ActionEvent e)
    {
        if (customizer != null && customizer.getDatamodel() != null && customizer instanceof DbMapCustomizer && customizer.getBean() instanceof DbMap)
        {
            final JList featuresList = ((DbMapCustomizer) customizer).getFeaturesList();
            final DbMapDesignInfo designInfo = ((FeaturesListModel) featuresList.getModel()).getDesignInfo();
            final int[] selectedIndices = featuresList.getSelectedIndices();
            if (selectedIndices != null && selectedIndices.length == 1)
            {
                final RowsetFeatureDescriptor feature = designInfo.getFeatures().get(selectedIndices[0]);
                final Font selectedFont = FontChooser.chooseFont(customizer, feature.getStyle().getFont(), "Select label font...");
                if (selectedFont != null)
                {
                    try
                    {
                        FeatureStyleDescriptor oldStyle = feature.getStyle();
                        FeatureStyleDescriptor newStyle = oldStyle.clone();
                        newStyle.setFont(selectedFont);
                        final ModifyStyleEdit edit = new ModifyStyleEdit(feature, oldStyle, newStyle);
                        feature.setStyle(newStyle);
                        customizer.getUndoSupport().postEdit(edit);
                        ((DbMapCustomizer) customizer).updateStyleView();
                    } catch (Exception ex)
                    {
                        Logger.getLogger(SelectColorAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
