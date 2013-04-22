/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.forms.store.FormDocument2Dom;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.scripts.ScriptDocument;
import com.eas.controls.FormDesignInfo;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class FormDocument extends ScriptDocument{
    
    private FormDesignInfo formDesignInfo;

    public FormDocument(ApplicationModel<?, ?, ?, ?> aDatamodel, String aSource, FormDesignInfo aFormDesignInfo) {
        super(aDatamodel, aSource);
        formDesignInfo = aFormDesignInfo;
    }

    public FormDocument(String aEntityId, ApplicationModel<?, ?, ?, ?> aDatamodel, String aSource, FormDesignInfo aFormDesignInfo) {
        super(aEntityId, aDatamodel, aSource);
        formDesignInfo = aFormDesignInfo;
    }

    public FormDesignInfo getFormDesignInfo() {
        return formDesignInfo;
    }

    public void setFormDesignInfo(FormDesignInfo aDesignInfo) {
        formDesignInfo = aDesignInfo;
    }

    @Override
    public Document toDom() throws Exception {
        return FormDocument2Dom.scriptDocument2Dom(this);
    }
    
}
