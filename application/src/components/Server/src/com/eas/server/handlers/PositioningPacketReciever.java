/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.script.ScriptUtils;
import com.eas.sensors.api.Packet;
import com.eas.sensors.api.PacketReciever;
import com.eas.sensors.api.RetranslateFactory;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class PositioningPacketReciever implements PacketReciever {

    public static final String RECIEVER_METHOD_NAME = "recieved";
    protected String moduleName;
    protected PlatypusServerCore serverCore;
    private final RetranslateFactory sender;

    public PositioningPacketReciever(PlatypusServerCore aServer, String aModuleName, RetranslateFactory aRetranslateFactory) {
        super();
        serverCore = aServer;
        moduleName = aModuleName;
        sender = aRetranslateFactory;
    }

    @Override
    public Object received(Packet aPacket) throws Exception {
        Session session = serverCore.getSessionManager().getSystemSession();
        PlatypusPrincipal.setInstance(session.getPrincipal());
        ScriptUtils.setSession(session);
        try {
            serverCore.executeMethod(moduleName, RECIEVER_METHOD_NAME, new Object[]{aPacket}, session, (Object result) -> {
                if (result != null) {
                    assert result instanceof String;
                    assert sender != null;
                    sender.send(aPacket, (String) result);
                }
            }, (Exception ex) -> {
                Logger.getLogger(PositioningPacketReciever.class.getName()).log(Level.WARNING, null, ex);
            }, null);
        } finally {
            PlatypusPrincipal.setInstance(null);
            ScriptUtils.setSession(null);
        }
        return null;
    }
}
