/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.edits;

import com.eas.dbcontrols.DbControlChangeEdit;
import com.eas.dbcontrols.FakeDbControlEvents;
import com.eas.dbcontrols.ScriptEvents;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.util.edits.ModifyBeanPropertyEdit;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class ModifyMapEventEdit extends ModifyBeanPropertyEdit<String> {

    public static Method mapEventMethod = null;

    static {
        try {
            mapEventMethod = FakeDbControlEvents.class.getMethod("mapEvent", new Class[]{EventObject.class});
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ModifyMapEventEdit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ModifyMapEventEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    protected ScriptEvents scriptEvents;
    protected String oldValue;
    protected String newValue;

    public ModifyMapEventEdit(ScriptEvents aScriptEvents, DbMapDesignInfo aDesignInfo, String aOldValue, String aNewValue) throws NoSuchMethodException {
        super(String.class, aDesignInfo, DbMapDesignInfo.PROP_MAP_EVENT_LISTENER, aOldValue, aNewValue);
        scriptEvents = aScriptEvents;
        newValue = aNewValue;
        oldValue = aOldValue;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        DbControlChangeEdit.synchronizeEvents(scriptEvents, mapEventMethod, oldValue, newValue);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        DbControlChangeEdit.synchronizeEvents(scriptEvents, mapEventMethod, newValue, oldValue);
    }
}
