/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.eas.controls.DesignInfo;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class DbControlChangeEdit extends DbControlEdit {

    public static Method selectValueMethod = null;
    public static Method handleValueMethod = null;

    static {
        try {
            selectValueMethod = FakeDbControlEvents.class.getMethod("selectValue", new Class[]{Object.class});
            handleValueMethod = FakeDbControlEvents.class.getMethod("handleValue", new Class[]{Object.class, Object.class, Object.class});
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(DbControlChangeEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    protected DesignInfo controlInfo = null;
    protected DesignInfo before = null;
    protected DesignInfo after = null;
    protected ScriptEvents scriptEvents;

    public DbControlChangeEdit(ScriptEvents aScriptEvents, DesignInfo aControlInfo, DesignInfo aBefore, DesignInfo aAfter) {
        super();
        scriptEvents = aScriptEvents;
        controlInfo = aControlInfo;
        before = aBefore;
        after = aAfter;
    }

    @Override
    public void undo() throws CannotUndoException {
        if (controlInfo != null) {
            controlInfo.assign(before);
            if (controlInfo instanceof DbControlDesignInfo && scriptEvents != null) {
                synchronizeEvents(scriptEvents, selectValueMethod, ((DbControlDesignInfo) after).getSelectFunction(), ((DbControlDesignInfo) before).getSelectFunction());
                synchronizeEvents(scriptEvents, handleValueMethod, ((DbControlDesignInfo) after).getHandleFunction(), ((DbControlDesignInfo) before).getHandleFunction());
            }
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        if (controlInfo != null) {
            controlInfo.assign(after);
            if (controlInfo instanceof DbControlDesignInfo && scriptEvents != null) {
                synchronizeEvents(scriptEvents, selectValueMethod, ((DbControlDesignInfo) before).getSelectFunction(), ((DbControlDesignInfo) after).getSelectFunction());
                synchronizeEvents(scriptEvents, handleValueMethod, ((DbControlDesignInfo) before).getHandleFunction(), ((DbControlDesignInfo) after).getHandleFunction());
            }
        }
    }

    public static void synchronizeEvents(ScriptEvents aScriptEvents, Method aMethod, String oldValue, String newValue) {
        if (oldValue == null || oldValue.isEmpty()) {
            if (newValue != null && !newValue.isEmpty()) {
                aScriptEvents.incHandlerUseWithoutPositioning(aMethod, newValue);
            }
        } else if (newValue == null || newValue.isEmpty()) {
            if (oldValue != null && !oldValue.isEmpty()) {
                aScriptEvents.decHandlerUse(oldValue);
            }
        } else if (!newValue.equals(oldValue)) {
            aScriptEvents.renameHandler(oldValue, aMethod, newValue);
        }
    }
}
