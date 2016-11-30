/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.report.Report;
import com.eas.client.model.application.ApplicationModel;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import jdk.nashorn.api.scripting.JSObject;

/**
 * TODO Create factory for ReportTemplate descendants as new formats will be
 * added.
 *
 * @author mg
 */
public class ReportTemplate implements HasPublished {

    protected int timezoneOffset;
    protected byte[] content;
    protected String name;
    protected String format;
    protected JSObject scriptData;
    protected JSObject fixed;
    protected JSObject published;

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Creates report template.\n"
            + " * @param content The report binary body (array of byte).\n"
            + " * @param name The generated report default.\n"
            + " * @param format The generated report format hint (used by runtime while report generation).\n"
            + " * @param data Object that propeties can be added to the report.\n"
            + " */"
            + "", params = {"content", "name", "format", "data"})
    public ReportTemplate(byte[] aContent, String aNameTemplate, String aFormat, JSObject aData) {
        super();
        fixed = Scripts.getSpace().makeArray();
        content = aContent;
        name = aNameTemplate;
        format = aFormat;
        scriptData = aData;
    }

    public ReportTemplate(byte[] aContent, String aNameTemplate, String aFormat, ApplicationModel<?, ?> aData) {
        super();
        fixed = Scripts.getSpace().makeArray();
        content = aContent;
        name = aNameTemplate;
        format = aFormat;
        scriptData = aData.getPublished();
    }

    public byte[] getContent() {
        return content;
    }

    public String getFormat() {
        return format;
    }

    public JSObject getScriptData() {
        return scriptData;
    }

    public void setScriptData(JSObject aValue) {
        scriptData = aValue;
    }

    private static final String GENERATEREPORT_JSDOC = ""
            + "/**\n"
            + " * Generate report from template.\n"
            + " */";

    @ScriptFunction(jsDoc = GENERATEREPORT_JSDOC)
    public Report generateReport() throws Exception {
        if (format != null) {
            ExelTemplate reportTemplate = new ExelTemplate(scriptData, format, this);
            byte[] generated = reportTemplate.create();
            return new Report(generated, format, name);
        }
        return null;
    }

    public void injectPublished(JSObject aValue) {
        published = aValue;
    }

    @Override
    public JSObject getPublished() {
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    private static final String NAME_JSDOC = ""
            + "/**\n"
            + " * Name of the generated report's file.\n"
            + " */";

    @ScriptFunction(jsDoc = NAME_JSDOC)
    public String getName() {
        return name;
    }

    @ScriptFunction
    public void setName(String aName) {
        name = aName;
    }

    private static final String FIXED_JSDOC = ""
            + "/**\n"
            + " * Array of name collections, that will fixed.\n"
            + " */";

    @ScriptFunction(jsDoc = FIXED_JSDOC)
    public JSObject getFixed() {
        return fixed;
    }
    
    @ScriptFunction
    public void setFixed(JSObject aValue) {
        fixed = aValue;
    }
    
    private static final String TIMEZONE_OFFSET_JSDOC = ""
            + "/**\n"
            + " * Array of name collections, that will fixed.\n"
            + " */";

    @ScriptFunction(jsDoc = TIMEZONE_OFFSET_JSDOC)
    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    @ScriptFunction
    public void setTimezoneOffset(int aValue) {
        timezoneOffset = aValue;
    }
}
