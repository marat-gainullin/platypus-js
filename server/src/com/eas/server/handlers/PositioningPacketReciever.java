/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.script.Scripts;
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

    public PositioningPacketReciever(PlatypusServerCore aServer, String aModuleName, RetranslateFactory aPacketSender) {
        super();
        serverCore = aServer;
        moduleName = aModuleName;
        sender = aPacketSender;
    }

    @Override
    public Object received(Packet aPacket) throws Exception {
        Session session = serverCore.getSessionManager().getSystemSession();
        Scripts.LocalContext context = new Scripts.LocalContext(session.getPrincipal(), session);
        session.getSpace().process(context, () -> {
            serverCore.executeMethod(moduleName, RECIEVER_METHOD_NAME, new Object[]{aPacket}, true, (Object result) -> {
                if (result != null) {
                    assert result instanceof String;
                    assert sender != null;
                    sender.send(aPacket, (String) result);
                }
            }, (Exception ex) -> {
                Logger.getLogger(PositioningPacketReciever.class.getName()).log(Level.WARNING, null, ex);
            });
        });
        return null;
    }
}
