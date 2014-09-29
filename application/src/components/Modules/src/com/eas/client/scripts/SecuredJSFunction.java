package com.eas.client.scripts;

import com.eas.client.cache.ScriptDocument;
import com.eas.client.login.PrincipalHost;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SecuredJSFunction extends SecuredJSObjectFacade {

    protected String name;

    public SecuredJSFunction(String aName, JSObject aDelegate, String aAppElementId, PrincipalHost aPrincipalHost, ScriptDocument aConfig) {
        super(aDelegate, aAppElementId, aPrincipalHost, aConfig);
        name = aName;
    }
    @Override
    public synchronized Object call(Object thiz, Object... args) {
        checkPropertyPermission(name, args);
        return super.call(thiz, args);
    }

    @Override
    public synchronized Object newObject(Object... args) {
        checkPropertyPermission(name, args);
        return super.newObject(args);
    }

}
