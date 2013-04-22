/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.TransferHandler;

/**
 *
 * @author pk
 */
public class PasteDataSourceAction extends DbMapChangeAction {

    public PasteDataSourceAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/paste.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TransferHandler.getPasteAction().actionPerformed(new ActionEvent(((DbMapCustomizer) customizer).getFeaturesList(), 0, "paste"));
    }
}
