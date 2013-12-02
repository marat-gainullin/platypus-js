/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormDataLoader;
import com.eas.client.cache.PlatypusFiles;
import com.eas.designer.application.indexer.AppElementInfo;
import com.eas.designer.application.module.completion.AppElementConstructorCompletionItem;
import com.eas.designer.application.module.completion.CompletionSupportService;
import com.eas.designer.application.module.completion.JsCompletionProvider;
import com.eas.designer.application.module.completion.ModuleCompletionSupportService;
import com.eas.designer.application.module.completion.SystemConstructorCompletionItem;
import com.eas.script.ScriptFunction;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author vv
 */
@ServiceProvider(service = CompletionSupportService.class)
public class FormCompletionSupportService implements CompletionSupportService {

    private static final String FORM_CONSTRUCTOR_NAME = "Form";//NOI18N
    private static final String FORM_CONSTRUCTOR_JSDOC = "/**\n"
            + "* Creates new Platypus Form application element instance.\n"//NOI18N
            + "* @param name Form application element name\n"//NOI18N
            + "*/";//NOI18N

    @Override
    public Class getClassByName(String name) {
        return FormUtils.getPlatypusApiClassByName(name);
    }

    @Override
    public Collection<SystemConstructorCompletionItem> getSystemConstructors(JsCompletionProvider.CompletionPoint point) {
        List<SystemConstructorCompletionItem> constructors = new ArrayList<>();
        constructors.add(new SystemConstructorCompletionItem(FORM_CONSTRUCTOR_NAME, "", Collections.EMPTY_LIST, FORM_CONSTRUCTOR_JSDOC, point.caretBeginWordOffset, point.caretEndWordOffset));
        return constructors;
    }
}
