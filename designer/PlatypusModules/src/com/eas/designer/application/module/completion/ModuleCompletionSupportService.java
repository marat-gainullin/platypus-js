/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author vv
 */
@ServiceProvider(service = CompletionSupportService.class)
public class ModuleCompletionSupportService implements CompletionSupportService {
    
    private static final String MODULE_CONSTRUCTOR_NAME = "Module";//NOI18N
    private static final String SERVER_MODULE_CONSTRUCTOR_NAME = "ServerModule";//NOI18N
    private static final String MODULE_CONSTRUCTOR_JSDOC = "/**\n"
            + "* Creates new Platypus application element instance.\n"//NOI18N
            + "* @param name Application element name\n"//NOI18N
            + "*/";//NOI18N
    private static final String SERVER_MODULE_CONSTRUCTOR_JSDOC = "/**\n"
            + "* Creates new proxy to a Platypus application element instance on the server.\n"//NOI18N
            + "* @param name Server application element name\n"//NOI18N
            + "*/";//NOI18N

    @Override
    public Class<?> getClassByName(String name) {
        return null;
    }

    @Override
    public Collection<SystemConstructorCompletionItem> getSystemConstructors(CompletionPoint point) {
        List<SystemConstructorCompletionItem> constructors = new ArrayList<>();
        constructors.add(new SystemConstructorCompletionItem(MODULE_CONSTRUCTOR_NAME, "", Collections.EMPTY_LIST, MODULE_CONSTRUCTOR_JSDOC, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        constructors.add(new SystemConstructorCompletionItem(SERVER_MODULE_CONSTRUCTOR_NAME, "", Collections.EMPTY_LIST, SERVER_MODULE_CONSTRUCTOR_JSDOC, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        return constructors;
    }
    
}
