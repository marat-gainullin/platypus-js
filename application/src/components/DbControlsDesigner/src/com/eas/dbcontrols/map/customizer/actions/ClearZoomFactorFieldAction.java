/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.datamodel.ModelElementRef;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import com.eas.util.edits.ModifyBeanPropertyEdit;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pk
 */
public class ClearZoomFactorFieldAction extends DbMapChangeAction {

    public ClearZoomFactorFieldAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
    }

    @Override
    public boolean isEnabled() {
        if (customizer != null && customizer.getBean() != null) {
            final DbMapDesignInfo designInfo = ((DbMap) ((DbMapCustomizer) customizer).getBean()).getDesignInfo();
            return designInfo.getZoomFactorFieldRef() != null;
        } else {
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DbMapDesignInfo designInfo = ((DbMap) ((DbMapCustomizer) customizer).getBean()).getDesignInfo();
        if (designInfo.getZoomFactorFieldRef() != null) {
            try {
                final ModifyBeanPropertyEdit<ModelElementRef> edit = new ModifyBeanPropertyEdit<>(ModelElementRef.class, designInfo, DbMapDesignInfo.PROP_ZOOM_FACTOR_FIELD_REF, designInfo.getZoomFactorFieldRef(), null);
                designInfo.setZoomFactorFieldRef(null);
                customizer.getUndoSupport().postEdit(edit);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ClearZoomFactorFieldAction.class.getName()).log(Level.SEVERE, null, ex);
                ModifyBeanPropertyEdit.showNoSetterError(customizer, DbMapDesignInfo.PROP_ZOOM_FACTOR_FIELD_REF, designInfo);
            }
        }
    }
}
