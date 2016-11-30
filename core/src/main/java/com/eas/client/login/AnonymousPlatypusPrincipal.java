/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.script.Scripts;
import com.eas.util.IdGenerator;
import java.util.Collections;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author vv
 */
public class AnonymousPlatypusPrincipal extends PlatypusPrincipal {

    public AnonymousPlatypusPrincipal() {
        this("anonymous-" + IdGenerator.genId());
    }

    public AnonymousPlatypusPrincipal(String aName) {
        super(aName, null, Collections.emptySet(), null);
    }

    @Override
    public boolean hasRole(String aRole) {
        return false;
    }

    @Override
    public void logout(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        if (aOnSuccess != null) {
            // async style
            Scripts.getSpace().process(() -> {
                aOnSuccess.call(null, new Object[]{});
            });
        }
        // sync style
    }
}
