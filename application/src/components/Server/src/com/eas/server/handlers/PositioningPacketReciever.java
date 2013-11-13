/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.script.ScriptUtils;
import com.eas.sensors.positioning.DevicesCommunication;
import com.eas.sensors.positioning.DevicesCommunication.DeviceRequest;
import com.eas.sensors.positioning.PacketReciever;
import com.eas.sensors.positioning.PositioningIoHandler;
import com.eas.sensors.positioning.PositioningPacket;
import com.eas.sensors.retranslate.RetranslatePacketFactory;
import com.eas.sensors.retranslate.http.HttpPushEncoder;
import com.eas.server.PlatypusServerCore;
import com.eas.server.PositioningPacketStorage;
import java.net.IDN;
import java.net.InetSocketAddress;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 *
 * @author mg
 */
public class PositioningPacketReciever implements PacketReciever {

    public static final String RECIEVER_METHOD_NAME = "recieved";
    public static final Pattern URL_PATTERN = Pattern.compile("(?:(?<SCHEMA>[a-z0-9\\+\\.\\-]+):)?(?://)?(?:(?<USER>[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]+):(?<PASS>[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]*)@)?(?<URL>[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]+):?(?<PORT>\\d+)?(?<PATH>/[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]+)*(?<FILE>/[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]+)?(?<QUERY>\\?[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"\\;\\/\\?\\:\\@\\=\\&]+)?");
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
        Object result = serverCore.executeServerModuleMethod(moduleId, RECIEVER_METHOD_NAME, new Object[]{aPacket});
        if (result != null) {
            result = ScriptUtils.js2Java(result);
            assert result instanceof String;
            send(aPacket, (String) result);
        }
        return null;
    }

    public static void send(PositioningPacket aPacket, String aUrl) throws Exception {
        Matcher m = URL_PATTERN.matcher(aUrl);
        while (m.find()) {
            send(aPacket, IDN.toASCII(m.group("URL").toLowerCase()), Integer.parseInt(m.group("PORT")), m.group("SCHEMA").toLowerCase(),
                    m.group("USER"), m.group("PASS"), m.group("PATH"));
        }
    }

    public static Object send(PositioningPacket aPacket, String aHost, Integer aPort, String aProtocolName, String aUser, String aPassword, String aPath) throws Exception {
        if (aHost != null && !aHost.isEmpty()
                && aProtocolName != null && !aProtocolName.isEmpty()
                && aPort != null && aPort > 0 && aPort < 65535
                && aPacket != null) {
            if (RetranslatePacketFactory.isProtocolSupported(aProtocolName)) {
                StringBuilder sId = new StringBuilder();
                sId.append(aPacket.getImei()).append(aProtocolName).append(aHost).append(aPort);
                String id = sId.toString();
                IoSession ioSession = retranslateSessions.get(id);
                if (ioSession == null) {
                    IoConnector connector = new NioSocketConnector();
                    if (aProtocolName.equals(RetranslatePacketFactory.HTTPS_PROTOCOL_NAME)) {
                        TrustManager[] trustAllCerts = new TrustManager[]{
                            new X509TrustManager() {
                                @Override
                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                @Override
                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                }

                                @Override
                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }
                        };
                        SSLContext sc = SSLContext.getInstance("TLS");
                        sc.init(null, trustAllCerts, new java.security.SecureRandom());
                        SslFilter filter = new SslFilter(sc);
                        filter.setUseClientMode(true);
                        filter.setNeedClientAuth(false);
                        connector.getFilterChain().addLast("ssl", filter);
                    }
                    ProtocolEncoder encoder = RetranslatePacketFactory.getPacketEncoder(aProtocolName);
                    if (encoder instanceof HttpPushEncoder) {
                        HttpPushEncoder encod = (HttpPushEncoder) encoder;
                        encod.setHost(aHost);
                        encod.setPath(aPath);
                        encod.setUser(aUser);
                        encod.setPassword(aPassword);
                    }
                    connector.getFilterChain().addLast(aProtocolName, new ProtocolCodecFilter(encoder, RetranslatePacketFactory.getPacketDecoder(aProtocolName)));
                    connector.setHandler(RetranslatePacketFactory.getPacketHandler(aProtocolName, retranslateSessions));
                    connector.setConnectTimeoutMillis(WAIT_SEND_TIMEOUT);
                    ConnectFuture future = connector.connect(new InetSocketAddress(aHost, aPort));
                    future.awaitUninterruptibly();
                    if (future.isConnected()) {
                        ioSession = future.getSession();
                        ioSession.getConfig().setUseReadOperation(true);
                        ioSession.setAttribute(ATTRIBUTE_SESSION_ID, id);
                        retranslateSessions.put(id, ioSession);
                        WriteFuture write = ioSession.write(aPacket);
                        write.awaitUninterruptibly(WAIT_SEND_TIMEOUT);
                    } else {
                        connector.dispose();
                    }
                } else {
                    WriteFuture write = ioSession.write(aPacket);
                    write.awaitUninterruptibly(WAIT_SEND_TIMEOUT);
                }
                return ioSession;
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
}
