/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.bearsoft.rowset.utils.IDGenerator;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SystemPlatypusPrincipal extends PlatypusPrincipal {

    public SystemPlatypusPrincipal() {
        super("system-" + IDGenerator.genID(), null, null, null);
    }

    @Override
    public boolean hasRole(String aRole) {
        return true;
    }

    @Override
    public void logout(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        if (aOnSuccess != null) {
            // async style
            aOnSuccess.call(null, new Object[]{});
        }
        // sync style
    }
}
