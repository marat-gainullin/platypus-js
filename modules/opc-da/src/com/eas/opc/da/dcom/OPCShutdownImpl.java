/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import java.util.logging.Logger;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JILocalCoClass;
import org.jinterop.dcom.core.JILocalInterfaceDefinition;
import org.jinterop.dcom.core.JILocalMethodDescriptor;
import org.jinterop.dcom.core.JILocalParamsDescriptor;
import org.jinterop.dcom.core.JIString;

/**
 *
 * @author pk
 */
public class OPCShutdownImpl
{
    final static String IID_IOPCShutdown = "f31dfde1-07b6-11d2-b2d8-0060083ba1fb";
    private OPCShutdownListener listener;
    private JILocalCoClass localClass;

    OPCShutdownImpl(OPCShutdownListener listener)
    {
        this.listener = listener;
        createCoClass();
    }

    JILocalCoClass getLocalClass()
    {
        return localClass;
    }

    private void createCoClass()
    {
        localClass = new JILocalCoClass(new JILocalInterfaceDefinition(IID_IOPCShutdown, false), this, false);
        JILocalParamsDescriptor shutdownParams = new JILocalParamsDescriptor();
        shutdownParams.addInParamAsObject(JIString.class, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        JILocalMethodDescriptor shutdownDesc = new JILocalMethodDescriptor("ShutdownRequest", 0, shutdownParams);
        localClass.getInterfaceDefinition().addMethodDescriptor(shutdownDesc);
    }

    public void ShutdownRequest(JIString reason)
    {
        Logger.getLogger(OPCShutdownImpl.class.getName()).finest("ShutdownRequest, reason=" + reason);
        listener.shutdownRequested(reason.getString());
    }
}
