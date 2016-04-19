/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.script.JsDoc;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class RestPointsScanner implements ApplicationSourceIndexer.ScanCallback {

    private final static String GET_ANNOTATION = "@get";
    private final static String PUT_ANNOTATION = "@put";
    private final static String POST_ANNOTATION = "@post";
    private final static String DELETE_ANNOTATION = "@delete";

    private final Map<String, RPCPoint> gets = new HashMap<>();
    private final Map<String, RPCPoint> puts = new HashMap<>();
    private final Map<String, RPCPoint> posts = new HashMap<>();
    private final Map<String, RPCPoint> deletes = new HashMap<>();
    private final Map<String, Map<String, RPCPoint>> methoded = new HashMap<>();

    public RestPointsScanner() {
        super();
        methoded.put("get", gets);
        methoded.put("put", puts);
        methoded.put("post", posts);
        methoded.put("delete", deletes);
    }

    public Map<String, Map<String, RPCPoint>> getMethoded() {
        return methoded;
    }

    public Map<String, RPCPoint> getGets() {
        return gets;
    }

    public Map<String, RPCPoint> getPuts() {
        return puts;
    }

    public Map<String, RPCPoint> getPosts() {
        return posts;
    }

    public Map<String, RPCPoint> getDeletes() {
        return deletes;
    }

    @Override
    public void moduleScanned(String aModuleName, ScriptDocument.ModuleDocument aModuleDocument, File aFile) {
        Map<String, Set<JsDoc.Tag>> annotations = aModuleDocument.getPropertyAnnotations();
        if (annotations != null) {
            annotations.entrySet().stream().forEach((Map.Entry<String, Set<JsDoc.Tag>> tagsEntry) -> {
                String propName = tagsEntry.getKey();
                RPCPoint rpcPoint = new RPCPoint(aModuleName, propName);
                Set<JsDoc.Tag> tags = tagsEntry.getValue();
                tags.stream().forEach((JsDoc.Tag aTag) -> {
                    if (GET_ANNOTATION.equalsIgnoreCase(aTag.getName())) {
                        extractUri(rpcPoint, aTag, (String aUri) -> {
                            gets.put(aUri, rpcPoint);
                        });
                    }
                    if (PUT_ANNOTATION.equalsIgnoreCase(aTag.getName())) {
                        extractUri(rpcPoint, aTag, (String aUri) -> {
                            puts.put(aUri, rpcPoint);
                        });
                    }
                    if (POST_ANNOTATION.equalsIgnoreCase(aTag.getName())) {
                        extractUri(rpcPoint, aTag, (String aUri) -> {
                            posts.put(aUri, rpcPoint);
                        });
                    }
                    if (DELETE_ANNOTATION.equalsIgnoreCase(aTag.getName())) {
                        extractUri(rpcPoint, aTag, (String aUri) -> {
                            deletes.put(aUri, rpcPoint);
                        });
                    }
                });
            });
        }
    }

    private void extractUri(RPCPoint rpcPoint, JsDoc.Tag aTag, Consumer<String> withUri) {
        if (aTag.getParams() != null && !aTag.getParams().isEmpty()) {
            withUri.accept(aTag.getParams().get(0));
        } else {
            Logger.getLogger(RestPointsScanner.class.getName()).log(Level.WARNING, "Annotation {0} in {1}.{2} missing uri parameter.", new Object[]{aTag.getName(), rpcPoint.getModuleName(), rpcPoint.getMethodName()});
        }
    }
}
