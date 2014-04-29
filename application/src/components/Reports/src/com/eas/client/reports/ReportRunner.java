/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.eas.client.events.PublishedSourcedEvent;
import com.eas.client.model.application.ApplicationModel;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ReportRunner implements HasPublished {

    protected byte[] template;
    protected ApplicationModel<?, ?, ?, ?> model;
    protected JSObject scriptData;
    protected JSObject onBeforeRender;
    protected String format;
    //
    protected Object published;

    public ReportRunner(byte[] aTemplate, ApplicationModel<?, ?, ?, ?> aModel, String aFormat) {
        super();
        template = aTemplate;
        model = aModel;
        format = aFormat;
    }

    @Override
    public Object getPublished() {
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        published = aValue;
    }

    public JSObject getScriptData() {
        return scriptData;
    }

    public void setScriptData(JSObject aValue) {
        scriptData = aValue;
    }

    private static final String ON_BEFORE_RENDER_JSDOC = ""
            + "/**\n"
            + " * onBeforeRender event handler is invoked before template processing.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_BEFORE_RENDER_JSDOC)
    public JSObject getOnBeforeRender() {
        return onBeforeRender;
    }

    @ScriptFunction()
    public void setOnBeforeRender(JSObject aValue) {
        onBeforeRender = aValue;
    }

    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + " * Shows report as Excel application.\n"
            + " */";

    @ScriptFunction(jsDoc = SHOW_JSDOC)
    public void show() throws Exception {
        if (template != null) {
            invokeOnBeforeRender();
            ExcelReport xlsReport = new ExcelReport(model, scriptData, format);
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
            invokeOnBeforeRender();
            ExcelReport xlsReport = new ExcelReport(model, scriptData, format);
            xlsReport.setTemplate(new CompactBlob(template));
            xlsReport.print();
        }
    }

    private static final String SAVE_JSDOC = ""
            + "/**\n"
            + " * Saves the report at a specified location.\n"
            + " * @param aFileName Name of a file, the generated report should be save in."
            + " */";

    @ScriptFunction(jsDoc = SAVE_JSDOC, params = {"aFileName"})
    public void save(String aFileName) throws Exception {
        if (template != null) {
            invokeOnBeforeRender();
            ExcelReport xlsReport = new ExcelReport(model, scriptData, format);
            xlsReport.setTemplate(new CompactBlob(template));
            xlsReport.save(aFileName);
        }
    }

    /**
     * Invokes handler for the Report pre-render event
     */
    private void invokeOnBeforeRender() throws Exception {
        if (onBeforeRender != null) {
            PublishedSourcedEvent event = new PublishedSourcedEvent(ReportRunner.this);
            onBeforeRender.call(this, new Object[]{event.getPublished()});
        }
    }
}
