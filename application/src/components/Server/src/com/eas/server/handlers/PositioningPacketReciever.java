/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.sensors.positioning.PacketReciever;
import com.eas.sensors.positioning.PositioningPacket;
import com.eas.server.PlatypusServer;
import com.eas.server.PositioningPacketStorage;
import com.eas.server.ServerScriptRunner;
import com.eas.server.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class PositioningPacketReciever implements PacketReciever {

    public static final String RECIEVER_METHOD_NAME = "received";
    public static final String PENDING_METHOD_NAME = "pending";
    protected String moduleName;
    protected PlatypusServer server;
    private PositioningPacketStorage packetStorage = new PositioningPacketStorage();
    private PositioningPacketStorage packetNotValidStorage = new PositioningPacketStorage(false);

    public PositioningPacketReciever(PlatypusServer aServer, String aModuleName) {
        super();
        server = aServer;
        moduleName = aModuleName;
    }

    @Override
    public Object received(final PositioningPacket aPacket) {
        try {
            if (aPacket != null) {
                if (aPacket.getValidity()) {
                    ExecuteServerModuleMethodRequest request = new ExecuteServerModuleMethodRequest(IDGenerator.genID(), moduleName, RECIEVER_METHOD_NAME, new Object[]{null});
                    request.getArguments()[0] = aPacket;
                    server.enqueuePlatypusRequest(request, new Runnable() { //onFailure
                        @Override
                        public void run() {
                            packetStorage.put(PositioningPacketStorage.constructKey(aPacket), aPacket);
                        }
                    }, new Runnable() { //onSuccess
                        @Override
                        public void run() {
                            packetStorage.saveStorageToBase(PositioningPacketReciever.this);
                        }
                    });
                } else {
                    packetNotValidStorage.put(PositioningPacketStorage.constructKey(aPacket), aPacket);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PositioningPacketReciever.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Object pending(Object aDeviceId) {
        try {
            Session systemSession = server.getSessionManager().getSystemSession();
            ServerScriptRunner module = systemSession.getModule(moduleName);
            return module.executeMethod(PENDING_METHOD_NAME, new Object[]{aDeviceId});
        } catch (Exception ex) {
            Logger.getLogger(PositioningPacketReciever.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the packetStorage
     */
    public PositioningPacketStorage getPacketStorage() {
        return packetStorage;
    }

    /**
     * @return the packetNotValidStorage
     */
    public PositioningPacketStorage getPacketNotValidStorage() {
        return packetNotValidStorage;
    }
}
