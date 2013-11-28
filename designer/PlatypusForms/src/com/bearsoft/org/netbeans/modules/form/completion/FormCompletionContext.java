/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.eas.client.scripts.ScriptRunner;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.JsCompletionProvider;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import com.eas.designer.application.module.completion.ModuleThisCompletionContext;
import com.eas.script.ScriptFunction;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class FormCompletionContext extends ModuleCompletionContext {

    public FormCompletionContext(PlatypusModuleDataObject dataObject, Class<? extends ScriptRunner> aClass) {
        super(dataObject, aClass);
    }
    
    @Override
    public ModuleThisCompletionContext createThisContext() {
        return new FormThisCompletionContext(this);
    }

    @Override
    public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        super.applyCompletionItems(point, offset, resultSet);
        
    }

    @Override
    protected void fillSystemConstructors(JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
        super.fillSystemConstructors(point, resultSet);
        for (Class<?> clazz : FormUtils.getPlatypusApiClasses()) {
            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (constructor.isAnnotationPresent(ScriptFunction.class)) {
                    ScriptFunction annotation = constructor.getAnnotation(ScriptFunction.class);
                    addItem(resultSet,
                            point.filter,
                            new ComponentConstructorCompletionItem(clazz.getSimpleName(),
                            "",//NOI18N
                            Arrays.<String>asList(annotation.params()),
                            annotation.jsDoc(),
                            point.caretBeginWordOffset,
                            point.caretEndWordOffset));
                    break;
                }
            }
        }
    }

    
}
