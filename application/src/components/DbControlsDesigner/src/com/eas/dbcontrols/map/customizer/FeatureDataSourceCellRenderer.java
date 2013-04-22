/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import com.eas.client.datamodel.ApplicationParametersEntity;
import com.eas.client.datamodel.gui.DatamodelDesignUtils;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.DesignIconCache;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author pk
 */
public class FeatureDataSourceCellRenderer extends DefaultListCellRenderer {

    public FeatureDataSourceCellRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof RowsetFeatureDescriptor) {
            final RowsetFeatureDescriptor desc = (RowsetFeatureDescriptor) value;
            if (desc.getEntity() != null) {
                if (desc.getEntity() instanceof ApplicationParametersEntity) {
                    if (desc.getTypeName() != null) {
                        setText(String.format("%s (%s)", desc.getTypeName(), DatamodelDesignUtils.getLocalizedString("Parameters")));
                    } else {
                        setText(DatamodelDesignUtils.getLocalizedString("Parameters"));
                    }
                } else {
                    setText(String.format("%s (%s)", desc.getTypeName(), desc.getEntity().getTitle()));
                }
            } else {
                setText(desc.getTypeName() == null || desc.getTypeName().isEmpty() ? "???" : desc.getTypeName());
            }
            setIcon(DesignIconCache.getIcon("16x16/layers.png"));
        } else {
            setText(String.valueOf(value));
        }
        return this;
    }
}
