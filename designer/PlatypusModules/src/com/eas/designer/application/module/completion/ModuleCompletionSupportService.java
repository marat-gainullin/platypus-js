/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.scripts.ScriptRunner;
import com.eas.designer.application.module.ModuleUtils;
import com.eas.script.ScriptObj;
import java.util.ArrayList;
import java.util.Collection;
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
            + "* Creates new Platypus application element instance.\n"
            + "* @param name Application element name\n"
            + "*/";
    private static final String SERVER_MODULE_CONSTRUCTOR_JSDOC = "/**\n"
            + "* Creates new proxy to a Platypus application element instance on the server.\n"
            + "* @param name Server application element name\n"
            + "*/";

    @Override
    public Class<?> getClassByName(String name) {
        return ModuleUtils.getPlatypusApiClassByName(name);
    }

    @Override
    public Collection<JsCompletionItem> getSystemConstructors(CompletionPoint point) {
        List<JsCompletionItem> items = new ArrayList<>();
        items.add(new SystemConstructorCompletionItem(MODULE_CONSTRUCTOR_NAME,
                "",//NOI18N
                new ArrayList<String>() {
                    {
                        add("name");//NOI18N
                    }
                },
                MODULE_CONSTRUCTOR_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        items.add(new SystemConstructorCompletionItem(SERVER_MODULE_CONSTRUCTOR_NAME,
                "",//NOI18N
                new ArrayList<String>() {
                    {
                        add("name");//NOI18N
                    }
                }, SERVER_MODULE_CONSTRUCTOR_JSDOC, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        return items;
    }

    @Override
    public Collection<JsCompletionItem> getSystemObjects(CompletionPoint point) {
        List<JsCompletionItem> items = new ArrayList<>();
        for (Class<?> clazz : ModuleUtils.getPlatypusApiClasses()) {
            if (clazz.isAnnotationPresent(ScriptObj.class)) {
                ScriptObj objectInfo = clazz.getAnnotation(ScriptObj.class);
                    items.add(
                            new JsFieldCompletionItem(objectInfo.name().isEmpty() ?
                                    clazz.getSimpleName() : objectInfo.name(),
                                    "",//NOI18N
                                    objectInfo.jsDoc(),
                                    point.getCaretBeginWordOffset(),
                                    point.getCaretEndWordOffset()));
        }}
        return items;
    }

}
