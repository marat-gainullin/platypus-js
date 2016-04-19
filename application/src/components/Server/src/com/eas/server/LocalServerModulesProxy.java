/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ServerModuleInfo;
import com.eas.client.ServerModulesProxy;
import com.eas.client.cache.ScriptDocument;
import com.eas.script.Scripts;
import com.eas.script.JsObjectException;
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
        ScriptDocument.ModuleDocument moduleDoc = serverCore.lookupModuleDocument(aModuleName);
        if (moduleDoc != null) {
            ServerModuleInfo info = new ServerModuleInfo(aModuleName, moduleDoc.getFunctionProperties(), true);
            return info;
        } else {
            throw new IllegalArgumentException(String.format("No module %s, or it is not a module", aModuleName));
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
        if (Scripts.getContext() != null) {
            Scripts.getContext().incAsyncsCount();
        }
        assert Scripts.getSpace() == aSpace;
        serverCore.executeMethod(aModuleName, aMethodName, aArguments, false, onSuccess != null ? (Object aResult) -> {
            onSuccess.call(null, new Object[]{aSpace.toJs(aResult)});
        } : null, (Exception ex) -> {
            if (onFailure != null) {
                onFailure.call(null, new Object[]{ex instanceof JsObjectException ? ((JsObjectException) ex).getData() : ex.toString()});
            } else {
                Logger.getLogger(LocalServerModulesProxy.class.getName()).log(Level.SEVERE, ex.toString());
            }
        });
        return null;// Only asynchronous form is allowed.
    }

}
