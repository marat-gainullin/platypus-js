/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.dbcontrols.DbControlChangeEdit;
import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.edits.ModifyMapEventEdit;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;

/**
 *
 * @author mg
 */
public class ClearMapEventAction extends DbMapChangeAction {

    public ClearMapEventAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/delete.png"));
    }

    @Override
    public boolean isEnabled() {
        if (customizer != null && customizer.getBean() != null) {
            final DbMapDesignInfo designInfo = ((DbMap) customizer.getBean()).getDesignInfo();
            return designInfo != null && designInfo.getMapEventListener() != null && !designInfo.getMapEventListener().isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            final DbMapDesignInfo designInfo = ((DbMap) customizer.getBean()).getDesignInfo();
            final String newValue = "";
            final String oldValue = designInfo.getMapEventListener();
            if (newValue == null && oldValue != null || newValue != null && !newValue.equals(oldValue)) {
                designInfo.setMapEventListener(newValue);
                DbControlChangeEdit.synchronizeEvents(scriptEvents, ModifyMapEventEdit.mapEventMethod, oldValue, newValue);
                final ModifyMapEventEdit edit =
                        new ModifyMapEventEdit(scriptEvents, designInfo, oldValue, newValue);
                customizer.getUndoSupport().postEdit(edit);
            }
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ClearProjectionAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
