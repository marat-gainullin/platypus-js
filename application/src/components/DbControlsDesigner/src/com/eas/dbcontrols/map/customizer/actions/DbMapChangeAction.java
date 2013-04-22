/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.map.customizer.actions;

import com.eas.dbcontrols.ScriptEvents;
import com.eas.dbcontrols.actions.DbControlChangeAction;
import com.eas.dbcontrols.map.customizer.DbMapCustomizer;

/**
 *
 * @author mg
 */
public abstract class DbMapChangeAction extends DbControlChangeAction{

    protected DbMapCustomizer customizer;
    protected ScriptEvents scriptEvents;

    public DbMapChangeAction(DbMapCustomizer aCustomizer)
    {
        super();
        customizer = aCustomizer;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }

    public ScriptEvents getScriptEvents() {
        return scriptEvents;
    }

    public void setScriptEvents(ScriptEvents aScriptEvents) {
        scriptEvents = aScriptEvents;
    }
}
