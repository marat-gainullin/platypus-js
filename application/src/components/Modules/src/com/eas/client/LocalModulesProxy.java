/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.scripts.DependenciesWalker;
import com.eas.client.settings.SettingsConstants;
import com.eas.script.Scripts;
import com.eas.util.FileUtils;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class LocalModulesProxy implements ModulesProxy {

    protected ApplicationSourceIndexer indexer;
    protected ModelsDocuments modelsDocs;
    protected final String startModuleName;

    public LocalModulesProxy(ApplicationSourceIndexer aIndexer, ModelsDocuments aModelsDocs, String aStartModuleName) throws Exception {
        super();
        indexer = aIndexer;
        modelsDocs = aModelsDocs;
        if (aStartModuleName.toLowerCase().endsWith(PlatypusFiles.JAVASCRIPT_FILE_END)) {
            startModuleName = aStartModuleName.substring(0, aStartModuleName.length() - PlatypusFiles.JAVASCRIPT_FILE_END.length());
        } else {
            startModuleName = aStartModuleName;
        }
    }

    @Override
    public Path getLocalPath() {
        return indexer.getAppPath();
    }

    @Override
    public ModuleStructure getModule(String aName, Scripts.Space aSpace, Consumer<ModuleStructure> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Callable<ModuleStructure> doWork = () -> {
            String name = aName;
            if (name == null || name.isEmpty()) {
                if (startModuleName == null || startModuleName.isEmpty()) {
                    throw new IllegalStateException("Default application element must present if you whant to resolve empty string names.");
                }
                name = startModuleName;
            }
            if (name != null) {
                File file = indexer.nameToFile(name);
                if (file != null && file.getName().endsWith(PlatypusFiles.JAVASCRIPT_FILE_END)) {
                    ModuleStructure structure = new ModuleStructure();
                    structure.getParts().addFile(file);
                    String jsSource = FileUtils.readString(file, SettingsConstants.COMMON_ENCODING);
                    DependenciesWalker walker = new DependenciesWalker(jsSource, (String aModuleCandidate) -> {
                        File depFile = indexer.nameToFile(aModuleCandidate);
                        if (depFile != null && depFile.exists()) {
                            if (depFile.getName().endsWith(PlatypusFiles.JAVASCRIPT_FILE_END)) {
                                return true;
                            } else {
                                Logger.getLogger(LocalModulesProxy.class.getName()).log(Level.WARNING, "JavaScript identifier: {0} found that is the same with non-module application element.", aModuleCandidate);
                            }
                        }// ordinary script class
                        return false;
                    });
                    walker.walk();
                    structure.getClientDependencies().addAll(walker.getDependencies());
                    structure.getServerDependencies().addAll(walker.getServerDependencies());
                    //Query dependencies from loadEntity() calls
                    structure.getQueryDependencies().addAll(walker.getQueryDependencies());
                    Path jsPath = Paths.get(file.toURI());
                    String jsedName = jsPath.getFileName().toString();
                    String woExtension = jsedName.substring(0, jsedName.length() - PlatypusFiles.JAVASCRIPT_EXTENSION.length());
                    File modelFile = jsPath.resolveSibling(woExtension + PlatypusFiles.MODEL_EXTENSION).toFile();
                    if (modelFile.exists()) {
                        structure.getParts().addFile(modelFile);
                        //Query dependencies from model's xml
                        Document modelDoc = modelsDocs.get(jsPath.toString(), modelFile);// cache documents by file of bundle (not by module name)
                        if (modelDoc != null) {
                            Element rootNode = modelDoc.getDocumentElement();
                            NodeList docNodes = rootNode.getElementsByTagName(Model2XmlDom.ENTITY_TAG_NAME);
                            for (int i = docNodes.getLength() - 1; i >= 0; i--) {
                                Node entityNode = docNodes.item(i);
                                Node queryIdAttribute = entityNode.getAttributes().getNamedItem(Model2XmlDom.QUERY_ID_ATTR_NAME);
                                if (queryIdAttribute != null) {
                                    String sQueryName = queryIdAttribute.getNodeValue();
                                    structure.getQueryDependencies().add(sQueryName);
                                }
                            }
                        }
                    }
                    File layoutFile = jsPath.resolveSibling(woExtension + PlatypusFiles.FORM_EXTENSION).toFile();
                    if (layoutFile.exists()) {
                        structure.getParts().addFile(layoutFile);
                    }
                    return structure;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        };
        if (onSuccess != null) {
            try {
                ModuleStructure structure = doWork.call();
                aSpace.process(() -> {
                    onSuccess.accept(structure);
                });
            } catch (Exception ex) {
                if (onFailure != null) {
                    aSpace.process(() -> {
                        onFailure.accept(ex);
                    });
                }
            }
            return null;
        } else {
            return doWork.call();
        }
    }

    @Override
    public File getResource(String aResourceName, Scripts.Space aSpace, Consumer<File> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Callable<File> doWork = ()->{
            String relativePath = aResourceName.replace("/", File.separator);
            Path resolved = indexer.getAppPath().resolve(relativePath);
            return resolved.toFile();
        };
        if (onSuccess != null) {
            try {
                File structure = doWork.call();
                aSpace.process(() -> {
                    onSuccess.accept(structure);
                });
            } catch (Exception ex) {
                if (onFailure != null) {
                    aSpace.process(() -> {
                        onFailure.accept(ex);
                    });
                }
            }
            return null;
        } else {
            return doWork.call();
        }
    }

    @Override
    public File nameToFile(String aName) throws Exception {
        return indexer.nameToFile(aName);
    }

    @Override
    public String getDefaultModuleName(File aFile) {
        return indexer.getDefaultModuleName(aFile);
    }

}
