/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.bearsoft.rowset.utils.IDGenerator;

/**
 *
 * @author mg
 */
public class SystemPlatypusPrincipal extends PlatypusPrincipal {

    public SystemPlatypusPrincipal() {
        super("system-" + IDGenerator.genID());
    }

    @Override
    public boolean hasRole(String aRole) throws Exception {
        return true;
    }
}
