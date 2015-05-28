/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.AppElementFiles;
import com.eas.client.settings.SettingsConstants;
import com.eas.util.FileUtils;
import com.eas.xml.dom.Source2XmlDom;
import java.io.File;
import java.util.Set;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class FormsDocuments extends ActualCache<Document> {

    public FormsDocuments() {
        super();
    }

    @Override
    public Document get(String aName, AppElementFiles aFiles) throws Exception {
        AppElementFiles files = new AppElementFiles();
        if (aFiles.hasExtension(PlatypusFiles.FORM_EXTENSION)) {
            files.addFile(aFiles.findFileByExtension(PlatypusFiles.FORM_EXTENSION));
        } else {
            throw new IllegalStateException("Application element " + aName + " has no form layout file (*." + PlatypusFiles.FORM_EXTENSION + ")");
        }
        return super.get(aName, files);
    }

    @Override
    protected Document parse(String aName, AppElementFiles aFiles) throws Exception {
        Set<File> files = aFiles.getFiles();
        assert files.size() == 1;
        String modelContent = FileUtils.readString(files.iterator().next(), SettingsConstants.COMMON_ENCODING);
        return Source2XmlDom.transform(modelContent);
    }

}
