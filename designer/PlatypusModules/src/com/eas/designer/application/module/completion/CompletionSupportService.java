/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.indexer.AppElementInfo;
import java.util.Collection;


/**
 *
 * @author vv
 */
public interface CompletionSupportService {

    public Class<?> getClassByName(String name);
        
    public Collection<SystemConstructorCompletionItem> getSystemConstructors(JsCompletionProvider.CompletionPoint point);
    
    public Collection<AppElementConstructorCompletionItem> getAppElementsConstructors(Collection<AppElementInfo> appElements, JsCompletionProvider.CompletionPoint point);
}
