/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.Event;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.bearsoft.org.netbeans.modules.form.PersistenceException;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormDataObject;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormSupport;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.eas.client.scripts.ScriptRunner;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import com.eas.designer.application.module.completion.ModuleThisCompletionContext;
import com.eas.script.ScriptFunction;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.ErrorManager;

/**
 *
 * @author vv
 */
public class FormCompletionContext extends ModuleCompletionContext {

    private static final Class EVENT_WRAPPER_CLASS = com.eas.client.forms.api.events.EventsWrapper.class;
    private static final String EVENTS_WRAPPER_METHOD_NAME = "wrap";//NOI18N

    public FormCompletionContext(PlatypusModuleDataObject dataObject, Class<? extends ScriptRunner> aClass) {
        super(dataObject, aClass);
    }

    @Override
    public ModuleThisCompletionContext createThisContext(boolean anEnableJsElementsCompletion) {
        return new FormThisCompletionContext(this, anEnableJsElementsCompletion);
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        super.applyCompletionItems(point, offset, resultSet);

    }

    @Override
    protected Class<?> getEventHandlerFunctionParameterClass(String functionName) {
        Class<?> clazz = super.getEventHandlerFunctionParameterClass(functionName);
        if (clazz == null) {
            for (RADComponent<?> component : getFormModel().getAllComponents()) {
                for (Event event : component.getAllEvents()) {
                    String[] eventHandlers = event.getEventHandlers();
                    if (eventHandlers != null) {
                        for (String eventHanler : eventHandlers) {
                            if (eventHanler.equals(functionName)) {
                                Class<?>[] parameterTypes = event.getListenerMethod().getParameterTypes();
                                if (parameterTypes != null && parameterTypes.length > 0) {
                                    Class<?> scriptEventClass = lowLevelEventType2ScriptEventType(parameterTypes[0]);
                                    return scriptEventClass != null ? scriptEventClass : com.eas.client.forms.api.events.Event.class;
                                }
                            }
                        }
                    }
                }
            }
        }
        return clazz;
    }

    protected FormModel getFormModel() {
        PlatypusFormDataObject formDataObject = (PlatypusFormDataObject) getDataObject();
        PlatypusFormSupport support = formDataObject.getLookup().lookup(PlatypusFormSupport.class);
        try {
            support.loadForm();
        } catch (PersistenceException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return support.getFormModel();

    }

    @Override
    protected void fillSystemConstructors(CompletionPoint point, CompletionResultSet resultSet) {
        super.fillSystemConstructors(point, resultSet);
        for (Class<?> clazz : FormUtils.getPlatypusApiClasses()) {
            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (constructor.isAnnotationPresent(ScriptFunction.class)) {
                    ScriptFunction annotation = constructor.getAnnotation(ScriptFunction.class);
                    addItem(resultSet,
                            point.getFilter(),
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
    }

    private Class<?> lowLevelEventType2ScriptEventType(Class<?> aClass) {
        for (Method method : EVENT_WRAPPER_CLASS.getMethods()) {
            if (method.getName().equals(EVENTS_WRAPPER_METHOD_NAME) && Modifier.isStatic(method.getModifiers())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes != null && parameterTypes.length > 0 && parameterTypes[0].equals(aClass)) {
                    return method.getReturnType();
                }
            }
        }
        return null;
    }
}
