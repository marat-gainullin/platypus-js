/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.ScriptDocument;
import com.eas.script.JsDoc;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class ServerTasksScanner implements ApplicationSourceIndexer.ScanCallback {

    // protocol acceptors
    private final Map<String, String> acceptors = new HashMap<>();
    private final Set<String> residents = new HashSet<>();
    private final Map<String, Collection<String>> validators = new HashMap<>();

    public ServerTasksScanner() {
        super();
    }

    @Override
    public void moduleScanned(String aModuleName, ScriptDocument.ModuleDocument aModuleDocument, File aFile) {
        List<JsDoc.Tag> annotations = aModuleDocument.getAnnotations();
        if (annotations != null) {
            annotations.stream().forEach((JsDoc.Tag tag) -> {
                if (JsDoc.Tag.ACCEPTOR_TAG.equalsIgnoreCase(tag.getName()) || JsDoc.Tag.ACCEPTED_PROTOCOL_TAG.equalsIgnoreCase(tag.getName())) {
                    if (tag.getParams() == null || tag.getParams().isEmpty()) {
                        if (acceptors.containsKey(null)) {
                            Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.WARNING, "Duplicated acceptor \"{0}\" on any protocol. Ignored.", aModuleName);
                        } else {
                            acceptors.put(null, aModuleName);
                            Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.INFO, "Acceptor \"{0}\" on any protocol has been registered.", aModuleName);
                        }
                    } else {
                        tag.getParams().stream().forEach((String protocol) -> {
                            if (acceptors.containsKey(protocol)) {
                                Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.WARNING, "Duplicated acceptor \"{0}\" on protocol \"{1}\". Ignored.", new Object[]{aModuleName, protocol});
                            } else {
                                acceptors.put(protocol, aModuleName);
                                Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.INFO, "Acceptor \"{0}\" on protocol \"{0}\" has been registered.", new Object[]{aModuleName, protocol});
                            }
                        });
                    }
                }
            });
            if (annotations.stream().anyMatch((JsDoc.Tag tag) -> {
                return JsDoc.Tag.RESIDENT_TAG.equalsIgnoreCase(tag.getName());
            })) {
                residents.add(aModuleName);
            }
            annotations.stream().forEach((JsDoc.Tag tag) -> {
                if (JsDoc.Tag.VALIDATOR_TAG.equalsIgnoreCase(tag.getName())) {
                    validators.put(aModuleName, tag.getParams());
                    Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.INFO, "Validator \"{0}\" on datasources {1} has been registered", new Object[]{aModuleName, tag.getParams().toString()});
                }
            });
        }
    }

    public Map<String, String> getAcceptors() {
        return acceptors;
    }

    public Set<String> getResidents() {
        return residents;
    }

    public Map<String, Collection<String>> getValidators() {
        return validators;
    }
}
