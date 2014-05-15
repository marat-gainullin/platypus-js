/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report.completion;

import com.eas.client.reports.Report;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionContext;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import jdk.nashorn.internal.ir.VarNode;

/**
 *
 * @author vv
 */
public class ReportModuleCompletionContext extends ModuleCompletionContext {

    public static final String LOAD_FORM_METHOD_NAME = "loadReport";//NOI18N
    
    public ReportModuleCompletionContext(PlatypusModuleDataObject dataObject) {
        super(dataObject);
    }

    @Override
    public CompletionContext getVarContext(VarNode varNode) {
        CompletionContext cc = super.getVarContext(varNode);
        if (cc != null) {
            return cc;
        }
        if (isSystemObjectMethod(varNode.getAssignmentSource(), LOAD_FORM_METHOD_NAME)) {
            cc = new CompletionContext(Report.class);
        } 
        return cc;
    }
}
