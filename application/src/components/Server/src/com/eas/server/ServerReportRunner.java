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
import static com.eas.client.reports.ReportRunner.BEFORE_RENDER_HANDLER_NAME;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptDocument;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.*;

/**
 *
 * @author pk, mg, ab
 */
public class ServerReportRunner extends ServerScriptRunner {

    private byte[] template;
    private Function onBeforeRender;
    private String format;

    public ServerReportRunner(PlatypusServerCore aServerCore, Session aCreationSession, String aModuleId, Scriptable aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, Object[] args) throws Exception {
        super(aServerCore, aCreationSession, aModuleId, aScope, aPrincipalHost, aCompiledScriptDocumentsHost, args);
        assert aCompiledScriptDocumentsHost != null;
        ScriptDocument scriptDoc = aCompiledScriptDocumentsHost.getDocuments().compileScriptDocument(appElementId);
        if (scriptDoc != null) {
            if (scriptDoc instanceof ReportDocument) {
                template = ((ReportDocument) scriptDoc).getTemplate();
                format = ((ReportDocument) scriptDoc).getFormat();
            }
        }
    }

    public synchronized byte[] executeReport() throws Exception {
        return ScriptUtils.inContext(new ScriptUtils.ScriptAction() {
            @Override
            public byte[] run(Context cx) throws Exception {
                if (template != null) {
                    execute();
                    Function preRendreHandler = onBeforeRender;
                    if (preRendreHandler != null) {
                        preRendreHandler.call(cx, ServerReportRunner.this, ServerReportRunner.this, new Object[]{Context.javaToJS(new ScriptSourcedEvent(ServerReportRunner.this), ServerReportRunner.this)});
                    }
                    ExcelReport er = new ExcelReport(model, ServerReportRunner.this);
                    er.setTemplate(new CompactBlob(template));
                    return er.create();
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    protected void definePropertiesAndMethods() {
        super.definePropertiesAndMethods();
        defineProperty(ReportRunner.BEFORE_RENDER_HANDLER_NAME, ServerReportRunner.class, EMPTY);
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }
}
