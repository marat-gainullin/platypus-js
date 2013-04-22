/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.actions;

import com.eas.dbcontrols.DbControlChangeEdit;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.ScriptEvents;
import java.awt.event.ActionEvent;

/**
 *
 * @author mg
 */
public abstract class DbControlSnapshotAction extends DbControlChangeAction {

    protected DesignInfo designInfo;
    protected ScriptEvents scriptEvents;

    public DbControlSnapshotAction() {
        super();
    }

    public ScriptEvents getScriptEvents() {
        return scriptEvents;
    }

    public void setScriptEvents(ScriptEvents aValue) {
        scriptEvents = aValue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DesignInfo before = designInfo.copy();
        DesignInfo after = designInfo.copy();
        if (after instanceof DbControlDesignInfo) {
            processChangedDesignInfo((DbControlDesignInfo) after);
            if (!before.isEqual(after)) {
                DbControlChangeEdit edit = new DbControlChangeEdit(scriptEvents, designInfo, before, after);
                edit.redo();
                undoSupport.postEdit(edit);
            }
        }
    }

    public DesignInfo getDesignInfo() {
        return designInfo;
    }

    public void setDesignInfo(DesignInfo aValue) {
        designInfo = aValue;
    }

    protected abstract void processChangedDesignInfo(DbControlDesignInfo after);
}
