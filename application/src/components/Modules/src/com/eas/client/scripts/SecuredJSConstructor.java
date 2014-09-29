package com.eas.client.scripts;

import com.eas.client.cache.ScriptDocument;
import com.eas.client.login.PrincipalHost;
import com.eas.script.JsDoc;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SecuredJSConstructor extends SecuredJSObjectFacade {

    public SecuredJSConstructor(JSObject aDelegate, String aModuleName, PrincipalHost aPrincipalHost, ScriptDocument aConfig) {
        super(aDelegate, aModuleName, aPrincipalHost, aConfig);
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
        checkModulePermissions();
        Object res = super.call(thiz, args);
        if (res instanceof JSObject) {
            return new SecuredJSObject((JSObject) res, moduleName, principalHost, config);
        } else {
            return res;
        }
    }

    @Override
    public synchronized Object newObject(Object... args) {
        checkModulePermissions();
        Object res = super.newObject(args);
        if (res instanceof JSObject) {
            return new SecuredJSObject((JSObject) res, moduleName, principalHost, config);
        } else {
            return res;
        }
    }
}
