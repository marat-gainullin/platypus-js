/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.CompletionSupportService;
import com.eas.designer.application.module.completion.JsCompletionItem;
import com.eas.designer.application.module.completion.JsFieldCompletionItem;
import com.eas.designer.application.module.completion.SystemConstructorCompletionItem;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
            + "* Creates new Platypus Form application element instance.\n"
            + "* @param name Form application element name\n"
            + "*/";

    @Override
    public Class getClassByName(String name) {
        return FormUtils.getPlatypusApiClassByName(name);
    }

    @Override
    public Collection<JsCompletionItem> getSystemConstructors(CompletionPoint point) {
        List<JsCompletionItem> items = new ArrayList<>();
        for (Class<?> clazz : FormUtils.getPlatypusApiClasses()) {
            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (constructor.isAnnotationPresent(ScriptFunction.class)) {
                    ScriptFunction annotation = constructor.getAnnotation(ScriptFunction.class);
                    items.add(
                            new ComponentConstructorCompletionItem(annotation.name().isEmpty() ?
                                    clazz.getSimpleName() : annotation.name(),
                                    "",//NOI18N
                                    Arrays.<String>asList(annotation.params()),
                                    annotation.jsDoc(),
                                    point.getCaretBeginWordOffset(),
                                    point.getCaretEndWordOffset()));
                    break;
                }
            }
        }
        items.add(new SystemConstructorCompletionItem(FORM_CONSTRUCTOR_NAME,
                "",//NOI18N
                new ArrayList<String>() {
                    {
                        add("name");//NOI18N
                    }
                },
                FORM_CONSTRUCTOR_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        return items;
    }

    @Override
    public Collection<JsCompletionItem> getSystemObjects(CompletionPoint point) {
        List<JsCompletionItem> items = new ArrayList<>();
        for (Class<?> clazz : FormUtils.getPlatypusApiClasses()) {
            if (clazz.isAnnotationPresent(ScriptObj.class)) {
                ScriptObj objectInfo = clazz.getAnnotation(ScriptObj.class);
                    items.add(
                            new FormObjectCompletionItem(objectInfo.name().isEmpty() ?
                                    clazz.getSimpleName() : objectInfo.name(),
                                    "",//NOI18N
                                    objectInfo.jsDoc(),
                                    point.getCaretBeginWordOffset(),
                                    point.getCaretEndWordOffset()));
        }}
        return items;
    }
}
