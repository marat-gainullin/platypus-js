/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.bearsoft.rowset.utils.IDGenerator;
import static com.eas.client.login.PlatypusPrincipal.HAS_ROLE_JS_DOC;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SystemPlatypusPrincipal extends PlatypusPrincipal {

    public SystemPlatypusPrincipal() {
        super("system-" + IDGenerator.genID(), null, null, null);
    }

    @ScriptFunction(jsDoc = HAS_ROLE_JS_DOC)
    @Override
    public boolean hasRole(String aRole) {
        return true;
    }

    @ScriptFunction(jsDoc = LOGOUT_JS_DOC, params = {"onSuccess", "onFailure"})
    @Override
    public void logout(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        if (aOnSuccess != null) {
            // async style
            aOnSuccess.call(null, new Object[]{});
        }
        // sync style
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
