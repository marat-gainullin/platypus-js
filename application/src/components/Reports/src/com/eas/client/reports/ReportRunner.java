/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.eas.client.Client;
import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.login.PrincipalHost;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptRunner;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class ReportRunner extends ScriptRunner {

    public static final String BEFORE_RENDER_HANDLER_NAME = "onBeforeRender"; //NOI18N
    protected byte[] template;
    private Function onBeforeRender;
    protected String format;

    public ReportRunner(String aReportId, Client aClient, Scriptable aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, Object[] args) throws Exception {
        super(aReportId, aClient, aScope, aPrincipalHost, aCompiledScriptDocumentsHost, args);
        setPrototype(ReportRunnerPrototype.getInstance());
    }

    @Override
    protected void prepare(ScriptDocument scriptDoc, Object[] args) throws Exception {
        assert scriptDoc instanceof ReportDocument;
        template = ((ReportDocument) scriptDoc).getTemplate();
        format = ((ReportDocument) scriptDoc).getFormat();
        super.prepare(scriptDoc, args);
    }

    @Override
    protected void prepareScript(ScriptDocument scriptDoc, Object[] args) throws Exception {
        super.prepareScript(scriptDoc, args);
        onBeforeRender = model.getHandler(BEFORE_RENDER_HANDLER_NAME);
    }

    @Override
    protected void shrink() throws Exception {
        template = null;
        super.shrink();
    }

    @Override
    public String getClassName() {
        return ReportRunner.class.getName();
    }

    @Override
    protected void definePropertiesAndMethods() {
        super.definePropertiesAndMethods();
        defineFunctionProperties(new String[]{
            "show",
            "print",
            "save"}, ReportRunner.class, EMPTY);
        defineProperty(BEFORE_RENDER_HANDLER_NAME, ReportRunner.class, EMPTY);
    }
    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + " * Shows report as Excel application.\n"
            + " */";

    @ScriptFunction(jsDoc = SHOW_JSDOC)
    public void show() throws Exception {
        if (template != null) {
            execute();
            invokeOnBeforeRender();
            ExcelReport xlsReport = new ExcelReport(model, this);
            xlsReport.setTemplate(new CompactBlob(template));
            xlsReport.show();
        }
    }
    private static final String PRINT_JSDOC = ""
            + "/**\n"
            + " * Runs printing.\n"
            + " */";

    @ScriptFunction(jsDoc = PRINT_JSDOC)
    public void print() throws Exception {
        if (template != null) {
            execute();
            invokeOnBeforeRender();
            ExcelReport xlsReport = new ExcelReport(model, this);
            xlsReport.setTemplate(new CompactBlob(template));
            xlsReport.print();
        }
    }

    public void save(String aFileName) throws Exception {
        if (template != null) {
            execute();
            invokeOnBeforeRender();
            ExcelReport xlsReport = new ExcelReport(model, this);
            xlsReport.setTemplate(new CompactBlob(template));
            xlsReport.save(aFileName);
        }
    }

    /**
     * Invokes handler for the Report pre-render event
     */
    private void invokeOnBeforeRender() throws Exception {
        ScriptUtils.inContext(new ScriptUtils.ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                if (onBeforeRender != null) {
                    onBeforeRender.call(cx, ReportRunner.this, ReportRunner.this, new Object[]{Context.javaToJS(new ScriptSourcedEvent(ReportRunner.this), ReportRunner.this)});
                }
                return null;
            }
        });
    }
}
