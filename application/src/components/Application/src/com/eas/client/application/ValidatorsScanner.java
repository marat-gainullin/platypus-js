/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.AppElementFiles;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.cache.ScriptDocument;
import com.eas.script.JsDoc;
import com.eas.util.FileUtils;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class ValidatorsScanner implements ApplicationSourceIndexer.ScanCallback {

    private final ScriptsConfigs scriptsConfigs;
    private final Map<String, Collection<String>> validators = new HashMap<>();

    public ValidatorsScanner(ScriptsConfigs aScriptsConfigs) {
        super();
        scriptsConfigs = aScriptsConfigs;
    }

    @Override
    public void fileScanned(String aAppElementName, File file) {
        if (PlatypusFiles.JAVASCRIPT_EXTENSION.equalsIgnoreCase(FileUtils.getFileExtension(file))) {
            try {
                AppElementFiles files = new AppElementFiles();
                files.addFile(file);
                ScriptDocument doc = scriptsConfigs.get(aAppElementName, files);
                List<JsDoc.Tag> annotations = doc.getModuleAnnotations();
                if (annotations != null) {
                    annotations.stream().forEach((JsDoc.Tag tag) -> {
                        if (JsDoc.Tag.VALIDATOR_TAG.equalsIgnoreCase(tag.getName())) {
                            validators.put(aAppElementName, tag.getParams());
                            Logger.getLogger(ValidatorsScanner.class.getName()).log(Level.INFO, "Validator \"{0}\" on datasources {1} has been registered", new Object[]{aAppElementName, tag.getParams().toString()});
                        }
                    });
                }
            } catch (Exception ex) {
                Logger.getLogger(ValidatorsScanner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Map<String, Collection<String>> getValidators() {
        return validators;
    }
}
