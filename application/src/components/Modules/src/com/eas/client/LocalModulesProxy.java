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
import com.eas.util.FileUtils;
import java.io.File;
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

    public LocalModulesProxy(ApplicationSourceIndexer aIndexer, ModelsDocuments aModelsDocs) throws Exception {
        super();
        indexer = aIndexer;
        modelsDocs = aModelsDocs;
    }

    @Override
    public String getLocalPath() {
        return indexer.calcSrcPath();
    }

    @Override
    public ModuleStructure getModule(String aName, Consumer<ModuleStructure> onSuccess, Consumer<Exception> onFailure) throws Exception {
        AppElementFiles files = indexer.nameToFiles(aName);
        ModuleStructure structure = new ModuleStructure();
        files.getFiles().stream().forEach((file) -> {
            structure.getParts().addFile(file);
        });
        File jsFile = structure.getParts().findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);
        if (jsFile != null) {
            String jsSource = FileUtils.readString(jsFile, SettingsConstants.COMMON_ENCODING);
            DependenciesWalker walker = new DependenciesWalker(jsSource, (String aModuleCandidate) -> {
                AppElementFiles depFiles = indexer.nameToFiles(aModuleCandidate);
                if (depFiles != null) {
                    if (depFiles.isModule()) {
                        return true;
                    } else {
                        Logger.getLogger(DependenciesWalker.class.getName()).log(Level.WARNING, "Possible name duplication (JavaScript indentifier {0} found that is the same with non-module application element).", aModuleCandidate);
                    }
                }// ordinary script class
                return false;
            });
            walker.walk();
            structure.getClientDependencies().addAll(walker.getDependencies());
            structure.getServerDependencies().addAll(walker.getServerDependencies());
            //Query dependencies from loadEntity() calls
            structure.getQueryDependencies().addAll(walker.getQueryDependencies());
            //Query dependencies from model's xml
            Document modelDoc = modelsDocs.get(aName, structure.getParts());
            Element rootNode = modelDoc.getDocumentElement();
            NodeList docNodes = rootNode.getElementsByTagName(Model2XmlDom.ENTITY_TAG_NAME);
            for (int i = docNodes.getLength() - 1; i >= 0; i--) {
                Node entityNode = docNodes.item(i);
                Node queryIdAttribute = entityNode.getAttributes().getNamedItem(Model2XmlDom.QUERY_ID_ATTR_NAME);
                if(queryIdAttribute != null){
                    String sQueryId = queryIdAttribute.getNodeValue();
                    structure.getQueryDependencies().add(sQueryId);
                }
            }
            return structure;
        } else {
            return null;
        }
    }

}
