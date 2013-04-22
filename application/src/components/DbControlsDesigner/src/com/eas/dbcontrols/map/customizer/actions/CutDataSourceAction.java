/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 *
 * @author pk
 */
public class CutDataSourceAction extends DbMapChangeAction {

    public CutDataSourceAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/cut.png"));
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
        TransferHandler.getCutAction().actionPerformed(new ActionEvent(((DbMapCustomizer) customizer).getFeaturesList(), 0, "cut"));
    }
}
