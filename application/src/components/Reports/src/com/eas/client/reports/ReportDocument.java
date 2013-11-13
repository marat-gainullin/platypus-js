/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.reports;

import com.eas.client.model.application.ApplicationModel;
import com.eas.client.reports.store.ReportDocument2Dom;
import com.eas.client.scripts.ScriptDocument;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class ReportDocument extends ScriptDocument{

    protected byte[] template;
    private String format;

    public ReportDocument(byte[] aTemplate, ApplicationModel<?, ?, ?, ?> aModel, String aSource)
    {
        super(aModel, aSource);
        template = aTemplate;
    }

    public ReportDocument(byte[] aTemplate, String aScriptId, ApplicationModel<?, ?, ?, ?> aModel, String aSource)
    {
        super(aScriptId, aModel, aSource);
        template = aTemplate;
    }

    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(byte[] aValue) {
        template = aValue;
    }

    @Override
    public Document toDom() throws Exception {
        return ReportDocument2Dom.reportDocument2Dom(this);
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param aFormat the format to set
     */
    public void setFormat(String aFormat) {
        this.format = aFormat;
    }
    
    public long getSize() {
        if (template != null) {
            return template.length;
        }
        return 0;
    } 
}
