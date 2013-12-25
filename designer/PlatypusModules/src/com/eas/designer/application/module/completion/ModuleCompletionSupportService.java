/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.ModuleUtils;
import com.eas.script.ScriptObj;
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
public class ModuleCompletionSupportService implements CompletionSupportService {

    private static final String MODULE_CONSTRUCTOR_NAME = "Module";//NOI18N
    private static final String NAME_PARAMETER_NAME = "name";//NOI18N
    private static final String SERVER_MODULE_CONSTRUCTOR_NAME = "ServerModule";//NOI18N
    private static final String MODULE_CONSTRUCTOR_JSDOC = "/**\n"
            + "* Creates new Platypus application element instance.\n"
            + "* @param name Application element name\n"
            + "*/";
    private static final String SERVER_MODULE_CONSTRUCTOR_JSDOC = "/**\n"
            + "* Creates new proxy to a Platypus application element instance on the server.\n"
            + "* @param name Server application element name\n"
            + "*/";
    private static final String REQUIRE_FUNCTION_NAME = "require";//NOI18N
    private static final String RESOURCES_PARAMETER_NAME = "resources";//NOI18N
    private static final String SUCCESS_CALLBACK_PARAMETER_NAME = "successCallback";//NOI18N
    private static final String FAILURE_CALLBACK_PARAMETER_NAME = "failureCallback";//NOI18N
    private static final String REQUIRE_FUNCTION_JSDOC = "/**\n"
            + "* Resolves dependecies for some specific JavaScript resource in a 'manual' way.\n"
            + "* @param module names, relative resources paths string array or just one string\n"
            + "* @param successCallback on success callback handler function (optional)\n"
            + "* @param failureCallback on failure callback handler function (optional)\n"
            + "*/";
    
    private static final String LOGOUT_FUNCTION_NAME = "logout";//NOI18N
    private static final String CALLBACK_PARAMETER_NAME = "callback";//NOI18N
    private static final String LOGOUT_FUNCTION_JSDOC = "/**\n"
            + "* Log out form the current user session.\n"
            + "* @param callback a handler function for a user session termination success (optional)\n"
            + "*/";
    private static final String SELECT_FILE_FUNCTION_NAME = "selectFile";//NOI18N
    private static final String SELECT_FILE_FUNCTION_JSDOC = "/**\n"
            + "* Shows an dialog to select a file on a client.\n"
            + "* The result file object can be uploaded to the server using <code>upload</code> function\n"
            + "* @param callback a handler function for a file selection, takes an file object as a parameter\n"
            + "*/";
    
    private static final String UPLOAD_FUNCTION_NAME = "upload";//NOI18N
    private static final String PROGRESS_CALLBACK_PARAMETER_NAME = "progessCallback";//NOI18N
    private static final String UPLOAD_FUNCTION_JSDOC = "/**\n"
            + "* Upload a file from a client to the server.\n"
            + "* The file object have to be selected by <code>selectFile</code> function\n"
            + "* @param file a handler function for a file upload success\n"
            + "* @param successCallback a handler function for a file upload success\n"
            + "* @param progessCallback a handler function for a file upload success,"
            + " takes as a parameter <code>evt</code> object with the following propeties:"
            + " <code>loaded</code> -- the currectly loaded value and <code>total</code> -- the total file size\n"
            + "* @param failureCallback a handler function for a file upload failure, takes as a parameter an error string\n"
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
                        add(NAME_PARAMETER_NAME);
                    }
                },
                MODULE_CONSTRUCTOR_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        items.add(new SystemConstructorCompletionItem(SERVER_MODULE_CONSTRUCTOR_NAME,
                "",//NOI18N
                new ArrayList<String>() {
                    {
                        add(NAME_PARAMETER_NAME);
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
        items.add(new JsFunctionCompletionItem(REQUIRE_FUNCTION_NAME,
                "",//NOI18N
                Arrays.asList(new String[] { RESOURCES_PARAMETER_NAME, SUCCESS_CALLBACK_PARAMETER_NAME, FAILURE_CALLBACK_PARAMETER_NAME}),
                REQUIRE_FUNCTION_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        items.add(new JsFunctionCompletionItem(LOGOUT_FUNCTION_NAME,
                "",//NOI18N
                Arrays.asList(new String[] { CALLBACK_PARAMETER_NAME }),
                LOGOUT_FUNCTION_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        items.add(new JsFunctionCompletionItem(SELECT_FILE_FUNCTION_NAME,
                "",//NOI18N
                Arrays.asList(new String[] { CALLBACK_PARAMETER_NAME }),
                SELECT_FILE_FUNCTION_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        items.add(new JsFunctionCompletionItem(UPLOAD_FUNCTION_NAME,
                "",//NOI18N
                Arrays.asList(new String[] { SUCCESS_CALLBACK_PARAMETER_NAME, PROGRESS_CALLBACK_PARAMETER_NAME, FAILURE_CALLBACK_PARAMETER_NAME }),
                UPLOAD_FUNCTION_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        return items;
    }

}
