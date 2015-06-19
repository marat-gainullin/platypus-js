/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.report.Report;
import com.eas.client.cache.ReportConfig;
import com.eas.client.model.application.ApplicationModel;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
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

    protected ReportConfig config;
    protected int timezoneOffset = 0;
    protected String name;
    protected JSObject scriptData;
    protected JSObject fixed;
    protected JSObject published;

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Creates report template.\n"
            + " * @param config The report binary body (array of byte) and some options.\n"
            + " * @param data Object that propeties can be added to the report.\n"
            + " */"
            + "", params = {"config", "data"})
    public ReportTemplate(ReportConfig aConfig, JSObject aData) {
        super();
        fixed = Scripts.getSpace().makeArray();
        config = aConfig;
        scriptData = aData;
        name = config.getNameTemplate();
    }

    public ReportTemplate(ReportConfig aConfig, ApplicationModel<?, ?> aData) {
        super();
        config = aConfig;
        scriptData = aData.getPublished();
        name = config.getNameTemplate();
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
        if (config != null) {
            ExelTemplate reportTemplate = new ExelTemplate(scriptData, config.getFormat(), this);
            byte[] generated = reportTemplate.create();
            return new Report(generated, config.getFormat(), name);
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

    public ReportConfig getConfig() {
        return config;
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
