/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.AppElementFiles;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.cache.ScriptSecurityConfigs;
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
    private final ScriptSecurityConfigs securityConfigs;

    public ServerTasksScanner(Set<String> aTasks, ScriptSecurityConfigs aSecurityConfigs) {
        super();
        tasks = aTasks;
        securityConfigs = aSecurityConfigs;
    }

    @Override
    public void fileScanned(String aAppElementName, File file) {
        if (PlatypusFiles.JAVASCRIPT_EXTENSION.equalsIgnoreCase(FileUtils.getFileExtension(file))) {
            try {
                AppElementFiles files = new AppElementFiles();
                files.addFile(file);
                ScriptDocument doc = securityConfigs.get(aAppElementName, files);
                List<JsDoc.Tag> annotations = doc.getModuleAnnotations();
                if (annotations != null && annotations.stream().anyMatch((JsDoc.Tag tag) -> {
                    return JsDoc.Tag.ACCEPTOR_TAG.equals(tag.getName())
                            || JsDoc.Tag.AUTHORIZER_TAG.equals(tag.getName())
                            || JsDoc.Tag.RESIDENT_TAG.equals(tag.getName())
                            || JsDoc.Tag.VALIDATOR_TAG.equals(tag.getName());
                })) {
                    tasks.add(aAppElementName);
                }
            } catch (Exception ex) {
                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
