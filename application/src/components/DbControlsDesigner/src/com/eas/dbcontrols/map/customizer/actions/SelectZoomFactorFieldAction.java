/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.datamodel.gui.selectors.ModelElementSelector;
import com.eas.dbcontrols.DbControlsDesignUtils;
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
public class SelectZoomFactorFieldAction extends DbMapChangeAction
{
    public SelectZoomFactorFieldAction(DbMapCustomizer aCustomizer)
    {
        super(aCustomizer);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        final DbMapDesignInfo designInfo = ((DbMap) ((DbMapCustomizer) customizer).getBean()).getDesignInfo();

        final ModelElementRef newValue = ModelElementSelector.selectDatamodelElement(customizer.getDatamodel(),
                designInfo.getZoomFactorFieldRef(),
                ModelElementSelector.PARAMETER_SELECTION_SUBJECT, null, customizer,
                DbControlsDesignUtils.getLocalizedString("selectZoomFactorParameter"));
        if (newValue != null && !newValue.equals(designInfo.getZoomFactorFieldRef()))
        {
            try
            {
                final ModifyBeanPropertyEdit<ModelElementRef> edit = new ModifyBeanPropertyEdit<>(ModelElementRef.class, designInfo, DbMapDesignInfo.PROP_ZOOM_FACTOR_FIELD_REF, designInfo.getZoomFactorFieldRef(), newValue);
                designInfo.setZoomFactorFieldRef(newValue);
                customizer.getUndoSupport().postEdit(edit);
            } catch (NoSuchMethodException ex)
            {
                Logger.getLogger(SelectZoomFactorFieldAction.class.getName()).log(Level.SEVERE, null, ex);
                ModifyBeanPropertyEdit.showNoSetterError(customizer, DbMapDesignInfo.PROP_ZOOM_FACTOR_FIELD_REF, designInfo);
            }
        }
    }
}
