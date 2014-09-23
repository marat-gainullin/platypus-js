/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.report.Report;
import com.bearsoft.rowset.compacts.CompactBlob;
import com.eas.client.cache.ReportConfig;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 * TODO Create factory for ReportTemplate descendants as new formats will be added.  
 * @author mg
 */
public class ReportTemplate implements HasPublished{

    protected ReportConfig config;
    //
    protected String name;
    protected JSObject scriptData;
    private static JSObject publisher;
    protected Object published;
    
    public ReportTemplate(ReportConfig aConfig, JSObject aData) {
        super();
        config = aConfig;
        scriptData = aData;
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
            ExelTemplate reportTemplate = new ExelTemplate(scriptData, config.getFormat());
            reportTemplate.setTemplate(new CompactBlob(config.getTemplateContent()));
            byte[] generated = reportTemplate.create();
            return new Report(generated, config.getFormat(), name);
        }
        return null;
    }
    
    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

    public void setName(String aName) {
        name = aName;
    }
    
}
