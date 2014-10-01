/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.Application;
import com.eas.client.forms.DbFormDesignInfo;
import com.eas.store.Object2Dom;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class ModelFormLoader {
    
    public static DbFormDesignInfo load(Document aDoc, Application aApp) {
        DbFormDesignInfo di = new DbFormDesignInfo();
        Object2Dom.transform(di, aDoc);
        return di;
    }
}
