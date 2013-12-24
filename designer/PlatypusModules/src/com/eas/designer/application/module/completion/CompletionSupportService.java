/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import java.util.Collection;


/**
 *
 * @author vv
 */
public interface CompletionSupportService {

    public Class<?> getClassByName(String name);
        
    public Collection<JsCompletionItem> getSystemConstructors(CompletionPoint point);
   
    public Collection<JsCompletionItem> getSystemObjects(CompletionPoint point);
}
