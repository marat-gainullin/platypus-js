/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.settings.SettingsConstants;
import com.eas.util.FileUtils;
import com.eas.xml.dom.Source2XmlDom;
import java.io.File;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class ModelsDocuments extends ActualCache<Document> {

    public ModelsDocuments() {
        super();
    }

    @Override
    protected Document parse(String aName, File aFile) throws Exception {
        String modelContent = FileUtils.readString(aFile, SettingsConstants.COMMON_ENCODING);
        return Source2XmlDom.transform(modelContent);
    }

}
