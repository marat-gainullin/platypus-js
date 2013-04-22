/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.debugger;

import org.netbeans.spi.debugger.SessionProvider;

/**
 *
 * @author mg
 */
@SessionProvider.Registration(path=DebuggerConstants.DEBUGGER_SERVICERS_PATH)
public class PlatypusDebuggerSessionProvider extends SessionProvider{

    @Override
    public String getSessionName() {
        return DebuggerConstants.DEBUGGER_ENGINE_ID;
    }

    @Override
    public String getLocationName() {
        return DebuggerConstants.LOCATION_NAME;
    }

    @Override
    public String getTypeID() {
        return DebuggerConstants.SESION_NAME;
    }

    @Override
    public Object[] getServices() {
        return new Object[]{};
    }
}
