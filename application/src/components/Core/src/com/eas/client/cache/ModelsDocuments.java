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
public class ModelsDocuments extends ActualCache<Document> {

    public ModelsDocuments() {
        super();
    }

    @Override
    public Document get(String aName, AppElementFiles aFiles) throws Exception {
        AppElementFiles files = new AppElementFiles();
        if (aFiles.hasExtension(PlatypusFiles.MODEL_EXTENSION)) {
            files.addFile(aFiles.findFileByExtension(PlatypusFiles.MODEL_EXTENSION));
        } else {
            throw new IllegalStateException("Application element " + aName + " has no model definition file (" + PlatypusFiles.MODEL_EXTENSION + ")");
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
