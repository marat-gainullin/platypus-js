/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.cache.ActualCache;
import com.eas.client.cache.PlatypusFiles;
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
public class ApplicationModels extends ActualCache<Document> {

    protected static ApplicationModels content = new ApplicationModels();

    public static Document transform(String aName, AppElementFiles aFiles) throws Exception {
        return content.get(aName, aFiles);
    }

    public ApplicationModels() {
        super();
    }

    @Override
    public synchronized Document get(String aName, AppElementFiles aFiles) throws Exception {
        AppElementFiles files = new AppElementFiles();
        files.addFile(aFiles.findFileByExtension(PlatypusFiles.MODEL_EXTENSION));
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
