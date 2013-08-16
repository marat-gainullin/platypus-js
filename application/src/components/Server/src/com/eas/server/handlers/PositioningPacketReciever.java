/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.scripts.ScriptRunner;
import com.eas.script.ScriptUtils;
import com.eas.sensors.positioning.DevicesCommunication;
import com.eas.sensors.positioning.DevicesCommunication.DeviceRequest;
import com.eas.sensors.positioning.PacketReciever;
import com.eas.sensors.positioning.PositioningIoHandler;
import com.eas.sensors.positioning.PositioningPacket;
import com.eas.sensors.retranslate.RetranslatePacketFactory;
import com.eas.server.PlatypusServerCore;
import com.eas.server.PositioningPacketStorage;
import com.eas.server.ServerScriptRunner;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 *
 * @author mg
 */
public class PositioningPacketReciever implements PacketReciever {

    public static final String RECIEVER_METHOD_NAME = "recieved";
    public static final String SEND_REQUEST_METHOD_NAME = "sendRequest";
    public static final String GET_RESPONSE_METHOD_NAME = "getResponse";
    protected String moduleId;
    protected PlatypusServerCore serverCore;
    private PositioningPacketStorage packetStorage = new PositioningPacketStorage();
    private PositioningPacketStorage invalidPacketStorage = new PositioningPacketStorage(false);

    public PositioningPacketReciever(PlatypusServerCore aServer, String aModuleId) {
        super();
        serverCore = aServer;
        moduleId = aModuleId;
    }

    @Override
    public Object received(PositioningPacket aPacket) throws Exception {
        ServerScriptRunner module = serverCore.getSessionManager().getSystemSession().getModule(moduleId);
        if (module == null) {
            module = new ServerScriptRunner(serverCore, serverCore.getSessionManager().getSystemSession(), moduleId, ScriptRunner.initializePlatypusStandardLibScope(), serverCore, serverCore, new Object[]{});
        }
        module.execute();
        serverCore.getSessionManager().setCurrentSession(serverCore.getSessionManager().getSystemSession());
        Object result;
        try {
            result = module.executeMethod(RECIEVER_METHOD_NAME, new Object[]{aPacket});
        } finally {
            serverCore.getSessionManager().setCurrentSession(null);
        }
        if (result != null) {
            result = ScriptUtils.js2Java(result);
            assert result instanceof String;
            ServiceUrl url = new ServiceUrl((String) result);
            send(aPacket, url.getHost(), url.getPort(), url.getProtocol());
        }
        return null;
    }

    public Object send(PositioningPacket aPacket, String aHost, Integer aPort, String aProtocolName) {
        if (aHost != null && !aHost.isEmpty()
                && aProtocolName != null && !aProtocolName.isEmpty()
                && aPort != null && aPort > 0 && aPort < 65535
                && aPacket != null) {
            if (RetranslatePacketFactory.isProtocolSupported(aProtocolName)) {
                String imei = aPacket.getImei();
                IoSession ioSession = retranslateSessions.get(imei);
                if (ioSession == null) {
                    IoConnector connector = new NioSocketConnector();
                    connector.getFilterChain().addLast(aProtocolName, new ProtocolCodecFilter(RetranslatePacketFactory.getPacketEncoder(aProtocolName), RetranslatePacketFactory.getPacketDecoder(aProtocolName)));
                    connector.setHandler(RetranslatePacketFactory.getPacketHandler(aProtocolName, retranslateSessions));
                    connector.setConnectTimeoutMillis(WAIT_SEND_TIMEOUT);
                    ConnectFuture future = connector.connect(new InetSocketAddress(aHost, aPort));
                    future.awaitUninterruptibly();
                    if (future.isConnected()) {
                        IoSession session = future.getSession();
                        session.setAttribute(IMEI_ATTRIBUTE, imei);
                        retranslateSessions.put(imei, session);
                        WriteFuture write = session.write(aPacket);
                        write.awaitUninterruptibly(WAIT_SEND_TIMEOUT);
                    } else {
                        connector.dispose();
                    }
                } else {
                    WriteFuture write = ioSession.write(aPacket);
                    write.awaitUninterruptibly(WAIT_SEND_TIMEOUT);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param aDeviceID
     * @return
     */
    @Override
    public DevicesCommunication.DeviceRequest getRequest(String aDeviceID) {
        return PositioningIoHandler.devicesRequests.getRequest(aDeviceID);
    }

    @Override
    public DeviceRequest getWaitingRequest(String aDeviceID) {
        return PositioningIoHandler.waitingRequests.getRequest(aDeviceID);
    }

    @Override
    public void putWaitingRequest(String aDeviceID, DeviceRequest aRequest) {
        PositioningIoHandler.waitingRequests.putRequest(aDeviceID, aRequest);
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
    public PositioningPacketStorage getInvalidPacketStorage() {
        return invalidPacketStorage;
    }

    /**
     *
     */
    public static class ServiceUrl {

        private final Pattern URL_PATTERN = Pattern.compile("(?<PROTOCOL>[A-Za-z0-9\\+\\.\\-]+)(://)(?<HOST>[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\"
                + "*\\'\\(\\)\\,\\\"]+)(:)(?<PORT>\\d+)");
        private String protocol;
        private String host;
        private int port;

        public ServiceUrl(String aUrl) {
            if (aUrl != null && !aUrl.isEmpty()) {
                Matcher matcher = URL_PATTERN.matcher(aUrl);
                if (matcher.matches()) {
                    port = Integer.valueOf(matcher.group("PORT"));
                    host = matcher.group("HOST");
                    protocol = matcher.group("PROTOCOL");
                }
            }
        }

        /**
         * @return the protocol
         */
        public String getProtocol() {
            return protocol;
        }

        /**
         * @param aProtocol the protocol to set
         */
        public void setProtocol(String aProtocol) {
            protocol = aProtocol;
        }

        /**
         * @return the host
         */
        public String getHost() {
            return host;
        }

        /**
         * @param aHost the host to set
         */
        public void setHost(String aHost) {
            host = aHost;
        }

        /**
         * @return the port
         */
        public int getPort() {
            return port;
        }

        /**
         * @param a the port to set
         */
        public void setPort(int aPort) {
            port = aPort;
        }
    }
}
