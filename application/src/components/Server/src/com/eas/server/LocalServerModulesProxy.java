/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.AppElementFiles;
import com.eas.client.ServerModuleInfo;
import com.eas.client.ServerModulesProxy;
import com.eas.client.cache.ScriptDocument;
import com.eas.script.Scripts;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class LocalServerModulesProxy implements ServerModulesProxy {

    protected PlatypusServerCore serverCore;

    public LocalServerModulesProxy(PlatypusServerCore aServerCore) {
        super();
        serverCore = aServerCore;
    }

    @Override
    public ServerModuleInfo getCachedStructure(String aModuleName) throws Exception {
        AppElementFiles files = serverCore.getIndexer().nameToFiles(aModuleName);
        if (files != null && files.isModule()) {
            ScriptDocument config = serverCore.getScriptsConfigs().get(aModuleName, files);
            ServerModuleInfo info = new ServerModuleInfo(aModuleName, config.getFunctionProperties(), true);
            return info;
        } else {
            throw new IllegalArgumentException(String.format("No module: %s, or it is not a module", aModuleName));
        }
    }

    @Override
    public ServerModuleInfo getServerModuleStructure(String aModuleName, Scripts.Space aSpace, Consumer<ServerModuleInfo> onSuccess, Consumer<Exception> onFailure) throws Exception {
        try {
            ServerModuleInfo info = getCachedStructure(aModuleName);
            if (onSuccess != null) {
                aSpace.process(() -> {
                    onSuccess.accept(info);
                });
                return null;
            } else {
                return info;
            }
        } catch (Exception ex) {
            if (onSuccess != null) {
                aSpace.process(() -> {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    } else {
                        Logger.getLogger(LocalServerModulesProxy.class.getName()).log(Level.WARNING, null, ex);
                    }
                });
            } else {
                throw ex;
            }
            return null;
        }
    }

    @Override
    public Object callServerModuleMethod(String aModuleName, String aMethodName, Scripts.Space aSpace, JSObject onSuccess, JSObject onFailure, Object... aArguments) throws Exception {
        Scripts.getContext().incAsyncsCount();
        serverCore.executeMethod(aModuleName, aMethodName, aArguments, false, (Object aResult) -> {
            onSuccess.call(null, new Object[]{aSpace.toJs(aResult)});
        }, (Exception ex) -> {
            onFailure.call(null, new Object[]{ex.toString()});
        });
        return null;
    }

}
