/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.geo.FeatureStyleDescriptor;
import com.eas.client.geo.RowsetFeatureDescriptor;
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
public class ClearStyleFieldAction extends DbMapChangeAction {

    public static final String FILLCOLOR = "fillColor";
    public static final String HALOCOLOR = "haloColor";
    public static final String LABELFIELD = "labelField";
    public static final String LABELFONT = "labelFont";
    public static final String LINECOLOR = "lineColor";
    public static final String OPACITY = "opacity";
    public static final String POINTSYMBOL = "pointSymbol";
    public static final String SIZE = "size";

    public ClearStyleFieldAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
    }

    @Override
    public boolean isEnabled() {
        if (customizer != null) {
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
                if (LABELFIELD.equals(e.getActionCommand()) && oldStyle.getLabelField() != null) {
                    newStyle.setLabelField(null);
                } else if (LABELFONT.equals(e.getActionCommand()) && oldStyle.getFont() != null) {
                    newStyle.setFont(null);
                } else if (SIZE.equals(e.getActionCommand()) && oldStyle.getSize() != null) {
                    newStyle.setSize(null);
                } else if (LINECOLOR.equals(e.getActionCommand()) && oldStyle.getLineColor() != null) {
                    newStyle.setLineColor(null);
                } else if (FILLCOLOR.equals(e.getActionCommand()) && oldStyle.getFillColor() != null) {
                    newStyle.setFillColor(null);
                } else if (HALOCOLOR.equals(e.getActionCommand()) && oldStyle.getHaloColor() != null) {
                    newStyle.setHaloColor(null);
                } else if (OPACITY.equals(e.getActionCommand()) && oldStyle.getOpacity() != null) {
                    newStyle.setOpacity(null);
                } else if (POINTSYMBOL.equals(e.getActionCommand()) && oldStyle.getPointSymbol() != null) {
                    newStyle.setPointSymbol(null);
                } else {
                    return;
                }
                final ModifyStyleEdit edit = new ModifyStyleEdit(feature, oldStyle, newStyle);
                feature.setStyle(newStyle);
                ((DbMapCustomizer) customizer).updateStyleView();
                customizer.getUndoSupport().postEdit(edit);
            } catch (Exception ex) {
                Logger.getLogger(ClearStyleFieldAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
