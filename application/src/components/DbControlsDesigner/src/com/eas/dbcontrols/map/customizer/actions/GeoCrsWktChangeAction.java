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
import javax.swing.text.JTextComponent;

/**
 *
 * @author mg
 */
public class GeoCrsWktChangeAction extends DbMapChangeAction
{
    public GeoCrsWktChangeAction(DbMapCustomizer aCustomizer)
    {
        super(aCustomizer);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            final String newValue = ((JTextComponent) e.getSource()).getText();
            final DbMapDesignInfo designInfo = ((DbMap) ((DbMapCustomizer) customizer).getBean()).getDesignInfo();
            final String oldValue = designInfo.getGeoCrsWkt();
            if (newValue == null && oldValue != null || newValue != null && !newValue.equals(oldValue))
            {
                designInfo.setGeoCrsWkt(newValue);
                final ModifyBeanPropertyEdit<String> edit =
                        new ModifyBeanPropertyEdit<>(String.class, designInfo, DbMapDesignInfo.PROP_MAP_GEO_CRS_WKT, oldValue, newValue);
                customizer.getUndoSupport().postEdit(edit);
            }
        } catch (NoSuchMethodException ex)
        {
            Logger.getLogger(ClearProjectionAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}