/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.AppCache;
import com.eas.client.login.PrincipalHost;
import com.eas.script.JsDoc;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SecuredJSConstructor extends SecuredJSObjectFacade {

    protected AppCache cache;
    protected long sourceLength;
    protected long sourceCRC32;

    public SecuredJSConstructor(JSObject aDelegate, String aAppElementId, long aSourceLength, long aSourceCRC32, AppCache aCache, PrincipalHost aPrincipalHost, ScriptDocument aConfig) {
        super(aDelegate, aAppElementId, aPrincipalHost, aConfig);
        cache = aCache;
        sourceLength = aSourceLength;
        sourceCRC32 = aSourceCRC32;
    }

    public void replace(JSObject aDelegate, long aSourceLength, long aSourceCRC32, ScriptDocument aConfig) {
        delegate = aDelegate;
        config = aConfig;
        sourceLength = aSourceLength;
        sourceCRC32 = aSourceCRC32;
    }

    public Set<String> getModuleAllowedRoles() {
        return config.getModuleAllowedRoles();
    }
    
    public Map<String, Set<String>> getPropertyAllowedRoles() {
        return config.getPropertyAllowedRoles();
    }

    public List<JsDoc.Tag> getModuleAnnotations() {
        return config.getModuleAnnotations() != null ? Collections.unmodifiableList(config.getModuleAnnotations()) : null;
    }
    
    public boolean hasModuleAnnotation(String anAnnotationName) {
        return config.hasModuleAnnotation(anAnnotationName);
    }

    @Override
    public synchronized Object call(Object thiz, Object... args) {
        checkAutoUpdate();
        checkModulePermissions();
        Object res = super.call(thiz, args);
        if (res instanceof JSObject) {
            return new SecuredJSObject((JSObject) res, appElementId, principalHost, config);
        } else {
            return res;
        }
    }

    @Override
    public synchronized Object newObject(Object... args) {
        checkAutoUpdate();
        checkModulePermissions();
        Object res = super.newObject(args);
        if (res instanceof JSObject) {
            return new SecuredJSObject((JSObject) res, appElementId, principalHost, config);
        } else {
            return res;
        }
    }

    private void checkAutoUpdate() {
        try {
            if (!cache.isActual(appElementId, sourceLength, sourceCRC32)) {
                cache.remove(appElementId);
                PlatypusScriptedResource.executeScriptResource(appElementId, this);
            }
        } catch (Exception ex) {
            Logger.getLogger(SecuredJSConstructor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
