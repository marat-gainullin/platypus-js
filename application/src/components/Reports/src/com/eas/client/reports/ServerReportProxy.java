/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.AppClient;
import com.eas.client.threetier.requests.ExecuteServerReportRequest;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author AB
 */
public class ServerReportProxy extends ScriptableObject {

    private AppClient client;
    private String moduleName;

    /**
     * Constructs server script runner for ordinary platypus session reports.
     *
     * @param aModuleName
     * @param aClient
     * @param aScope
     * @throws Exception
     */
    public ServerReportProxy(String aModuleName, AppClient aClient, Scriptable aScope) throws Exception {
        super(aScope, null);

        client = aClient;
        moduleName = aModuleName;
        defineFunctionProperties(new String[]{
                    "show", "print", "save"
                }, ServerReportProxy.class, EMPTY);
    }

    private ExecuteServerReportRequest.NamedArgument[] collectRequestArguments() {
        List<ExecuteServerReportRequest.NamedArgument> arguments = new ArrayList<>();
        for (Object key : getIds()) {
            if (key instanceof String) {
                Object value = get(key);
                if ((!(value instanceof Function) && !(value instanceof ScriptableObject))
                        || (value != null && java.util.Date.class.getSimpleName().equals(((ScriptableObject) value).getClassName()))) {
                    arguments.add(new ExecuteServerReportRequest.NamedArgument((String) key, ScriptUtils.js2Java(value)));
                }
            }
        }
        return arguments.toArray(new ExecuteServerReportRequest.NamedArgument[]{});
    }
    /**
     * Print report.
     *
     * @throws Exception
     */
    private static final String PRINT_JSDOC = ""
            + "/**\n"
            + " * Run printing report.\n"
            + " */";

    @ScriptFunction(jsDoc = PRINT_JSDOC)
    public void print() throws Exception {
        ExecuteServerReportRequest rq = new ExecuteServerReportRequest(IDGenerator.genID(), moduleName);
        rq.setArguments(collectRequestArguments());
        client.executeRequest(rq);
        byte[] result = ((ExecuteServerReportRequest.Response) rq.getResponse()).getResult();
        ExcelReport exRep = new ExcelReport();
        String repPath = exRep.generateReportPath(((ExecuteServerReportRequest.Response) rq.getResponse()).getFormat());
        exRep.saveReport(result, repPath);
        File f = new File(repPath);
        f.deleteOnExit();
        exRep.shellPrintReport(repPath);
    }
    /**
     * Save report.
     *
     * @param aFileName
     * @throws Exception
     */
    private static final String SAVE_JSDOC = ""
            + "/**\n"
            + " * Save report in file with extention xlsx.\n"
            + " * aFileName - path to file.\n"
            + " */";

    @ScriptFunction(jsDoc = SAVE_JSDOC)
    public void save(String aFileName) throws Exception {
        ExecuteServerReportRequest rq = new ExecuteServerReportRequest(IDGenerator.genID(), moduleName);
        rq.setArguments(collectRequestArguments());
        client.executeRequest(rq);
        byte[] result = ((ExecuteServerReportRequest.Response) rq.getResponse()).getResult();
        ExcelReport exRep = new ExcelReport();
        exRep.saveReport(result, aFileName);
    }
    /**
     * Show report.
     *
     * @throws Exception
     */
    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + " * Shows report as Excel application.\n"
            + " */";

    @ScriptFunction(jsDoc = SHOW_JSDOC)
    public void show() throws Exception {
        ExecuteServerReportRequest rq = new ExecuteServerReportRequest(IDGenerator.genID(), moduleName);
        rq.setArguments(collectRequestArguments());
        client.executeRequest(rq);
        byte[] result = ((ExecuteServerReportRequest.Response) rq.getResponse()).getResult();
        ExcelReport exRep = new ExcelReport();
        String repPath = exRep.generateReportPath(((ExecuteServerReportRequest.Response) rq.getResponse()).getFormat());
        exRep.saveReport(result, repPath);
        File f = new File(repPath);
        f.deleteOnExit();
        exRep.shellShowReport(repPath);
    }

    @Override
    public String getClassName() {
        return "ServerReport";
    }
}
