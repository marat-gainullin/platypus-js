/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.controls;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public interface FormEventsExecutor extends Scriptable {

    public Function getHandler(String aHandlerName);
    public Object executeEvent(Function aHandler, Scriptable aEventThis, Object anEvent);
}
