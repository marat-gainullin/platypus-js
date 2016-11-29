/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts.ole;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;

/**
 * Representation of an active session with a COM server.
 * 
 * @author vv
 */
public class ComSession extends AbstractJSObject {

    private JISession session;
    private JIComServer comServer;

    /**
     * @param domain Domain of the user associated with this session.
     * @param userName Name of the user has required rights on the host.
     * @param password Password of the user.
     */
    public ComSession(String domain, String userName, String password) {
        super();
        //super.defineFunctionProperties(new String[]{"destroy", "createObject"}, ComSession.class, ScriptableObject.READONLY);//NOI18N
        session = JISession.createSession(domain, userName, password);

    }

    /**
     * Creates OLE automation interface wrapper. 
     * @param progId user-friendly string such as "Excel.Application".
     * @param host address of the host where the COM object resides.This should be in the IEEE IP format (e.g. 192.168.170.6) or a resolvable HostName.
     * @return a ComObject instance
     * @throws JIException
     * @throws UnknownHostException  
     */
    public ComObject createObject(String progId, String host) throws JIException, UnknownHostException {
        comServer = new JIComServer(JIProgId.valueOf(progId), host, session);
        IJIComObject comObject = comServer.createInstance();
        return ComObject.getInstance(comObject);
    }

    /*
     * Used to destroy the session, this release all references of the COM server and it's interfaces.
     * It should be called in the end after the developer is done with the COM server.
     */
    public void destroy() {
        try {
            JISession.destroySession(session);
        } catch (JIException ex) {
            Logger.getLogger(ComSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getClassName() {
        return ComSession.class.getSimpleName();
    }
}
