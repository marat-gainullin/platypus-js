/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols;

import java.lang.reflect.Method;

/**
 *
 * @author mg
 */
public interface ScriptEvents {

    public int incHandlerUseWithoutPositioning(Method aListenerMethod, String aHandlerName);
    public int incHandlerUse(Method aListenerMethod, String aHandlerName);
    public int decHandlerUse(String aHandlerName);
    public void renameHandler(String oldName, Method aNewListenerMethod, String newName);
    public String[] getAllEventHandlers();
    public String findFreeHandlerName(String aSubjectName, Method aListenerMethod);
}
