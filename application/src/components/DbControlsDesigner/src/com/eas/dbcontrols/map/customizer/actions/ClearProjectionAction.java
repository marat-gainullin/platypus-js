/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import com.eas.util.edits.ModifyBeanPropertyEdit;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.CompoundEdit;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author pk
 */
public class ClearProjectionAction extends DbMapChangeAction {

    public ClearProjectionAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
    }

    @Override
    public boolean isEnabled() {
        if (customizer != null && customizer.getBean() != null) {
            final DbMapDesignInfo designInfo = ((DbMap) ((DbMapCustomizer) customizer).getBean()).getDesignInfo();
            return designInfo.getProjectionName() != null || designInfo.getProjectionParameters() != null;
        } else {
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            final DbMapDesignInfo designInfo = ((DbMap) ((DbMapCustomizer) customizer).getBean()).getDesignInfo();
            final CompoundEdit compoundEdit = new CompoundEdit();
            final ModifyBeanPropertyEdit<String> projectionNameEdit =
                    new ModifyBeanPropertyEdit<>(String.class, designInfo, DbMapDesignInfo.PROP_PROJECTION_NAME,
                    designInfo.getProjectionName(), null);
            final ModifyBeanPropertyEdit<ParameterValueGroup> projectionParametersEdit =
                    new ModifyBeanPropertyEdit<>(ParameterValueGroup.class, designInfo, DbMapDesignInfo.PROP_PROJECTION_PARAMETERS,
                    designInfo.getProjectionParameters(), null);
            designInfo.setProjectionName(null);
            designInfo.setProjectionParameters(null);
            compoundEdit.addEdit(projectionNameEdit);
            compoundEdit.addEdit(projectionParametersEdit);
            compoundEdit.end();
            customizer.getUndoSupport().postEdit(compoundEdit);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ClearProjectionAction.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
