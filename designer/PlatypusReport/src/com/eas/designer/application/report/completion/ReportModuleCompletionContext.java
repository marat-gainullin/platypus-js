/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report.completion;

import com.eas.client.report.Report;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionContext;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import java.util.Map;
import jdk.nashorn.internal.ir.VarNode;

/**
 *
 * @author vv
 */
public class ReportModuleCompletionContext extends ModuleCompletionContext {

    public static final String LOAD_REPORT_METHOD_NAME = "loadReport";//NOI18N
    
    public ReportModuleCompletionContext(PlatypusModuleDataObject dataObject) {
        super(dataObject);
    }

    @Override
    public void injectVarContext(Map<String, CompletionContext> contexts, VarNode varNode) {
        super.injectVarContext(contexts, varNode);
        if (isSystemObjectMethod(varNode.getAssignmentSource(), LOAD_REPORT_METHOD_NAME)) {
            contexts.put(varNode.getName().getName(), new CompletionContext(Report.class));
        }
    }
}
