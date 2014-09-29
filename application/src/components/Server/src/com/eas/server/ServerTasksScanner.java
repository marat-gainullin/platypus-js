/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.ScriptDocument;
import com.eas.script.JsDoc;
import com.eas.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class ServerTasksScanner implements ApplicationSourceIndexer.ScanCallback {
    
    private final Set<String> tasks;

    public ServerTasksScanner(Set<String> aTasks) {
        super();
        tasks = aTasks;
    }

    @Override
    public void fileScanned(String aAppElementName, File file) {
        if (PlatypusFiles.JAVASCRIPT_EXTENSION.equalsIgnoreCase(FileUtils.getFileExtension(file))) {
            try {
                ScriptDocument doc = ScriptDocument.parse(FileUtils.readString(file, PlatypusFiles.DEFAULT_ENCODING));
                List<JsDoc.Tag> annotations = doc.getModuleAnnotations();
                if (annotations != null) {
                    for (JsDoc.Tag tag : annotations) {
                        if (JsDoc.Tag.ACCEPTOR_TAG.equals(tag.getName())
                                || JsDoc.Tag.AUTHORIZER_TAG.equals(tag.getName())
                                || JsDoc.Tag.RESIDENT_TAG.equals(tag.getName())
                                || JsDoc.Tag.VALIDATOR_TAG.equals(tag.getName())
                                ) {
                            tasks.add(aAppElementName);
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
