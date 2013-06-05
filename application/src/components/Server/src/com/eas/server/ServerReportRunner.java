/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.login.PrincipalHost;
import com.eas.client.reports.ExcelReport;
import com.eas.client.reports.ReportDocument;
import com.eas.client.reports.ReportRunner;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptResolverHost;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import java.rmi.AccessException;
import org.mozilla.javascript.*;

/**
 *
 * @author pk, mg
 */
public class ServerReportRunner extends ServerScriptRunner {

    private byte[] template;
    private Function onBeforRender;
    
     public ServerReportRunner(PlatypusServerCore aServerCore, Session aCreationSession, ModuleConfig aConfig, ScriptableObject aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, ScriptResolverHost aScriptResolverHost) throws Exception {
        super(aServerCore, aCreationSession, aConfig, aScope, aPrincipalHost, aCompiledScriptDocumentsHost, aScriptResolverHost);
        assert aCompiledScriptDocumentsHost != null;
        ScriptDocument scriptDoc = aCompiledScriptDocumentsHost.getDocuments().compileScriptDocument(appElementId);
        if (scriptDoc != null) {
            if (scriptDoc instanceof ReportDocument) {
                template = ((ReportDocument) scriptDoc).getTemplate();

            }
        }
    }
    
    @Override
    public synchronized Object executeMethod(String methodName, Object[] arguments) throws Exception {
        throw new AccessException("Could not execute method \"" + methodName + "()\" in report.");
    } 
     
    @ScriptFunction
    public Function getOnBeforeRender() {
        return onBeforRender;
    }

    @ScriptFunction
    public void setOnBeforeRender(Function aValue) {
        onBeforRender = aValue;
    }

    @Override
    public synchronized byte[] executeReport() throws Exception {
        Context cx = ScriptUtils.enterContext();
        try {
            if (template != null) {
                execute();
                Function preRendreHandler = getOnBeforeRender();
                if (preRendreHandler != null) {
                    preRendreHandler.call(cx, this, this, new Object[]{Context.javaToJS(new ScriptSourcedEvent(this), this)});
                }
                ExcelReport er = new ExcelReport(model, this);
                er.setTemplate(new CompactBlob(template));
                return er.create();
            } else {
                return null;
            }
        } finally {
            Context.exit();
        }
    }

    @Override
    protected void definePropertiesAndMethods() {
        super.definePropertiesAndMethods();
        defineProperty(ReportRunner.BEFORE_RENDER_HANDLER_NAME, ServerReportRunner.class, EMPTY);
    }

    @Override
    public boolean isReport() {
        return true;
    }
}
