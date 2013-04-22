/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.actions;

import com.eas.dbcontrols.DbControlChangeEdit;
import com.eas.dbcontrols.edits.ModifyMapEventEdit;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

/**
 *
 * @author pk
 */
public class ModifyMapEventListenerAction extends DbMapChangeAction {

    public ModifyMapEventListenerAction(DbMapCustomizer aCustomizer) {
        super(aCustomizer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            final String newValue = getNewValue((JComboBox) e.getSource());
            final DbMapDesignInfo designInfo = ((DbMap) ((DbMapCustomizer) customizer).getBean()).getDesignInfo();
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

    private String getNewValue(JComboBox cb) {
        String script = null;
        ComboBoxEditor cbe = cb.getEditor();
        if (cbe != null && cbe.getEditorComponent() != null) {
            Component ec = cbe.getEditorComponent();
            if (ec instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) ec;
                script = tc.getText();
            }
        }
        if (script == null || script.isEmpty()) {
            script = (String) cb.getSelectedItem();
        }
        return script;
    }
}
