/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.Application;
import com.eas.client.forms.FormFactory;
import jdk.nashorn.api.scripting.JSObject;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class ModelFormLoader {
    
    public static FormFactory load(Document aDoc, Application<?> aApp, JSObject aModel) throws Exception {
        FormFactory factory = new FormFactory(aDoc.getDocumentElement(), aModel);
        factory.parse();
        return factory;
    }
}
