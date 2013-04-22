/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.opc;

import com.eas.opc.da.dcom.IOPCCommon;
import java.net.UnknownHostException;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;

/**
 *
 * @author pk
 */
public class OPCCommon {
    protected JISession session;
    protected JIComServer virtualServer;
    protected IJIComObject comServer;
    protected IOPCCommon opcCommon;

    public void connect(String progID, String domain, String hostname, String username, String password) throws JIException, UnknownHostException
    {
        if (session != null || virtualServer != null || comServer != null || opcCommon != null)
            throw new IllegalStateException("Already connected."); //NOI18N
        session = JISession.createSession(domain, username, String.valueOf(password));
        virtualServer = new JIComServer(JIProgId.valueOf(progID), hostname, session);
        comServer = virtualServer.createInstance();
        opcCommon = new IOPCCommon(comServer);
    }

    public void disconnect()
    {
        opcCommon = null;
        comServer = null;
        virtualServer = null;
        session = null;
    }

    public int getLocaleID() throws JIException
    {
        return opcCommon.getLocaleID();
    }

    public void setLocaleID(int lcid) throws JIException
    {
        opcCommon.setLocaleID(lcid);
    }

    public int[] queryAvailableLocaleIDs() throws JIException
    {
        return opcCommon.queryAvailableLocaleIDs();
    }

    public void setClientName(String clientName) throws JIException
    {
        opcCommon.setClientName(clientName);
    }

    public String getErrorString(long errorCode) throws JIException
    {
        return opcCommon.getErrorString(errorCode);
    }


}
