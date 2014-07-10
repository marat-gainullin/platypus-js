/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.script.ScriptUtils;
import com.eas.sensors.api.Packet;
import com.eas.sensors.api.PacketReciever;
import com.eas.sensors.api.RetranslateFactory;
import com.eas.server.PlatypusServerCore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.service.IoConnector;

/**
 *
 * @author mg
 */
public class PositioningPacketReciever implements PacketReciever {

    public static final String RECIEVER_METHOD_NAME = "recieved";
    protected String moduleId;
    protected PlatypusServerCore serverCore;
    private final RetranslateFactory sender;

    public PositioningPacketReciever(PlatypusServerCore aServer, String aModuleId, RetranslateFactory aRetranslateFactory) {
        super();
        serverCore = aServer;
        moduleId = aModuleId;
        sender = aRetranslateFactory;
    }

    @Override
    public Object received(Packet aPacket) throws Exception {
        Object result = serverCore.executeServerModuleMethod(moduleId, RECIEVER_METHOD_NAME, new Object[]{aPacket});
        result = ScriptUtils.toJava(result);
        if (result != null) {
            assert result instanceof String;
            assert sender != null;
            sender.send(aPacket, (String) result);
        }
        return null;
    }
}
