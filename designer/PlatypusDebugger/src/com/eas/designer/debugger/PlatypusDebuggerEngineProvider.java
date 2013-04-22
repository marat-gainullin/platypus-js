/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.debugger;

import org.netbeans.api.debugger.DebuggerEngine.Destructor;
import org.netbeans.spi.debugger.DebuggerEngineProvider;
/**
 *
 * @author mg
 */
@DebuggerEngineProvider.Registration(path=DebuggerConstants.DEBUGGER_SERVICERS_PATH)
public class PlatypusDebuggerEngineProvider extends DebuggerEngineProvider{

    protected Destructor destructor;

    public PlatypusDebuggerEngineProvider()
    {
        super();
    }

    @Override
    public String[] getLanguages() {
        return new String[]{DebuggerConstants.LANGUAGE_NAME};
    }

    @Override
    public String getEngineTypeID() {
        return DebuggerConstants.DEBUGGER_ENGINE_ID;
    }

    @Override
    public Object[] getServices() {
        return new Object[]{};
    }

    @Override
    public void setDestructor(Destructor aValue) {
        destructor = aValue;
    }
}
