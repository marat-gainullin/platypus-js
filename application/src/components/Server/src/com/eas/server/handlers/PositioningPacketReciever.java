/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.script.ScriptUtils;
import com.eas.sensors.api.Packet;
import com.eas.sensors.api.PacketReciever;
import com.eas.sensors.api.RetranslateFactory;
import com.eas.server.PlatypusServerCore;

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
        serverCore.executeMethod(moduleName, RECIEVER_METHOD_NAME, new Object[]{aPacket}, serverCore.getSessionManager().getSystemSession(), (Object result) -> {
            if (result != null) {
                assert result instanceof String;
                assert sender != null;
                sender.send(aPacket, (String) result);
            }
        }, null);
        return null;
    }
}
