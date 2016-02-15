/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.ScriptDocument;
import com.eas.script.JsDoc;
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

    private final Map<String, Collection<String>> validators = new HashMap<>();

    public ValidatorsScanner() {
        super();
    }

    @Override
    public void moduleScanned(String aModuleName, ScriptDocument.ModuleDocument aModuleDoc, File file) {
        try {
            List<JsDoc.Tag> annotations = aModuleDoc.getAnnotations();
            if (annotations != null) {
                annotations.stream().forEach((JsDoc.Tag tag) -> {
                    if (JsDoc.Tag.VALIDATOR_TAG.equalsIgnoreCase(tag.getName())) {
                        validators.put(aModuleName, tag.getParams());
                        Logger.getLogger(ValidatorsScanner.class.getName()).log(Level.INFO, "Validator \"{0}\" on datasources {1} has been registered", new Object[]{aModuleName, tag.getParams().toString()});
                    }
                });
            }
        } catch (Exception ex) {
            Logger.getLogger(ValidatorsScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, Collection<String>> getValidators() {
        return validators;
    }
}
