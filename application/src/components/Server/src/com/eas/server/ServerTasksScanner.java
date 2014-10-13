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

    // protocol, acceptors
    private final Map<String, String> acceptors = new HashMap<>();
    private final Set<String> authorizers = new HashSet<>();
    private final Set<String> residents = new HashSet<>();

    private final Map<String, Collection<String>> validators = new HashMap<>();
    private final ScriptSecurityConfigs securityConfigs;

    public ServerTasksScanner(ScriptSecurityConfigs aSecurityConfigs) {
        super();
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
                if (annotations != null) {
                    annotations.stream().forEach((JsDoc.Tag tag) -> {
                        if (JsDoc.Tag.ACCEPTOR_TAG.equals(tag.getName()) || JsDoc.Tag.ACCEPTED_PROTOCOL_TAG.equals(tag.getName())) {
                            if (tag.getParams() == null || tag.getParams().isEmpty()) {
                                if (acceptors.containsKey(null)) {
                                    Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.WARNING, "Duplicated acceptor \"{0}\" on any protocol. Ignored.", aAppElementName);
                                } else {
                                    acceptors.put(null, aAppElementName);
                                    Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.INFO, "Acceptor \"{0}\" on any protocol has been registered.", aAppElementName);
                                }
                            } else {
                                tag.getParams().stream().forEach((String protocol) -> {
                                    if (acceptors.containsKey(protocol)) {
                                        Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.WARNING, "Duplicated acceptor \"{0}\" on protocol \"{1}\". Ignored.", new Object[]{aAppElementName, protocol});
                                    } else {
                                        acceptors.put(protocol, aAppElementName);
                                        Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.INFO, "Acceptor \"{0}\" on protocol \"{0}\" has been registered.", new Object[]{aAppElementName, protocol});
                                    }
                                });
                            }
                        }
                    });
                    if (annotations.stream().anyMatch((JsDoc.Tag tag) -> {
                        return JsDoc.Tag.AUTHORIZER_TAG.equals(tag.getName());
                    })) {
                        authorizers.add(aAppElementName);
                        Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.INFO, "Authorizer \"{0}\" has been registered", aAppElementName);
                    }
                    if (annotations.stream().anyMatch((JsDoc.Tag tag) -> {
                        return JsDoc.Tag.RESIDENT_TAG.equals(tag.getName());
                    })) {
                        residents.add(aAppElementName);
                    }
                    annotations.stream().forEach((JsDoc.Tag tag) -> {
                        if (JsDoc.Tag.VALIDATOR_TAG.equals(tag.getName())) {
                            validators.put(aAppElementName, tag.getParams());
                            Logger.getLogger(ServerTasksScanner.class.getName()).log(Level.INFO, "Validator \"{0}\" on datasources {1} has been registered", new Object[]{aAppElementName, tag.getParams().toString()});
                        }
                    });
                }
            } catch (Exception ex) {
                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Map<String, String> getAcceptors() {
        return acceptors;
    }

    public Set<String> getAuthorizers() {
        return authorizers;
    }

    public Set<String> getResidents() {
        return residents;
    }

    public Map<String, Collection<String>> getValidators() {
        return validators;
    }
}
