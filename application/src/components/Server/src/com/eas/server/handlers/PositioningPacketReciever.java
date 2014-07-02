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
import java.util.regex.Pattern;
import org.apache.mina.core.service.IoConnector;

/**
 *
 * @author mg
 */
public class PositioningPacketReciever implements PacketReciever {
    private final int CONNECTION_PROCESSORS_COUNT = 10;
    public static final String RECIEVER_METHOD_NAME = "recieved";
    public static final Pattern URL_PATTERN = Pattern.compile("(?:(?<SCHEMA>[a-z0-9\\+\\.\\-]+):)?(?://)?(?:(?<USER>[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]+):(?<PASS>[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]*)@)?(?<URL>[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]+):?(?<PORT>\\d+)?(?<PATH>[/a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]+)*(?<FILE>/[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"]+)?(?<QUERY>\\?[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\\"\\;\\/\\?\\:\\@\\=\\&]+)?");
    protected String moduleId;
    protected PlatypusServerCore serverCore;
    protected int processorsCount;
    private final Map<String, IoConnector> connectors = new ConcurrentHashMap<>();
    private RetranslateFactory sender;
    
    public PositioningPacketReciever(PlatypusServerCore aServer, String aModuleId, int aProcessorsCount) {
        super();
        serverCore = aServer;
        moduleId = aModuleId;
        processorsCount = aProcessorsCount;
    }

    @Override
    public Object received(Packet aPacket) throws Exception {
        Object result = serverCore.executeServerModuleMethod(moduleId, RECIEVER_METHOD_NAME, new Object[]{aPacket});
        if (result != null) {
             result = ScriptUtils.toJava(result);
            assert result instanceof String;
            assert sender != null;
            sender.send(aPacket, (String) result);
        }
        return null;
    }

//    public void send(Packet aPacket, String aUrl) throws Exception {
//        Matcher m = URL_PATTERN.matcher(aUrl);
//        while (m.find()) {
//            send(aPacket, IDN.toASCII(m.group("URL").toLowerCase()), Integer.parseInt(m.group("PORT")), m.group("SCHEMA").toLowerCase(),
//                    m.group("USER"), m.group("PASS"), m.group("PATH"), m.group("QUERY"));
//        }
//    }
//
//    public void send(Object aData, String aUrl) throws Exception {
//        Matcher m = URL_PATTERN.matcher(aUrl);
//        while (m.find()) {
//            send(aData, IDN.toASCII(m.group("URL").toLowerCase()), Integer.parseInt(m.group("PORT")), m.group("SCHEMA").toLowerCase(),
//                    m.group("USER"), m.group("PASS"), m.group("PATH"), m.group("QUERY"));
//        }
//    }
//
//    public Object send(Packet aPacket, String aHost, Integer aPort, String aProtocolName, String aUser, String aPassword, String aPath, String aQuery) throws Exception {
//        if (aHost != null && !aHost.isEmpty()
//                && aProtocolName != null && !aProtocolName.isEmpty()
//                && aPort != null && aPort > 0 && aPort < 65535
//                && aPacket != null && sender != null) {
//            if (sender.isServiceSupport(aProtocolName)) {
//                StringBuilder sId = new StringBuilder();
//                sId.append(aPacket.getID()).append(aProtocolName).append(aHost).append(aPort);
//                String id = sId.toString();
//                IoSession ioSession = retranslateSessions.get(id);
//                if (ioSession == null || ioSession.isClosing()) {
//                    ioSession = send(aPacket, aHost, aPort, aProtocolName, aUser, aPassword, aPath, aQuery, null);
//                    if (ioSession != null) {
//                        ioSession.setAttribute(ATTRIBUTE_SESSION_ID, id);
//                        retranslateSessions.put(id, ioSession);
//                    }
//                } else {
//                    ioSession = send(aPacket, aHost, aPort, aProtocolName, aUser, aPassword, aPath, aQuery, ioSession);
//                }
//                return ioSession;
//            }
//        }
//        return null;
//    }
//
//    public Object send(Object aData, String aHost, Integer aPort, String aProtocolName, String aUser, String aPassword, String aPath, String aQuery) throws Exception {
//        if (aHost != null && !aHost.isEmpty()
//                && aProtocolName != null && !aProtocolName.isEmpty()
//                && aPort != null && aPort > 0 && aPort < 65535
//                && aData != null && sender != null) {
//            if (sender.isServiceSupport(aProtocolName)) {
//                return send(aData, aHost, aPort, aProtocolName, aUser, aPassword, aPath, aQuery, null);
//            }
//        }
//        return null;
//    }
//
//    private IoSession send(Object aData, String aHost, Integer aPort, String aProtocolName, String aUser, String aPassword, String aPath, String aQuery, IoSession aSession) throws Exception {
//        
//        if (aSession == null) {
//            IoConnector connector = connectors.get(aProtocolName);
//            if (connector == null) {
//                connector = new NioSocketConnector(processorsCount > 0 ? processorsCount : CONNECTION_PROCESSORS_COUNT);
//                sender.constructFiltersChain(connector.getFilterChain(), aProtocolName);
//                IoHandlerAdapter handler = sender.getPacketHandler(aProtocolName, retranslateSessions);
//                handler.setHost(aHost);
//                handler.setPath(aPath);
//                handler.setUser(aUser);
//                handler.setQuery(aQuery);
//                handler.setPassword(aPassword);
//                handler.setPort(String.valueOf(aPort));
//
//                connector.setHandler(handler);
//                connector.setConnectTimeoutMillis(WAIT_SEND_TIMEOUT);
//                connectors.put(aProtocolName, connector);
//            }
//            ConnectFuture future = connector.connect(new InetSocketAddress(aHost, aPort));
//            future.awaitUninterruptibly();
//            if (future.isConnected()) {
//                aSession = future.getSession();
//                aSession.getConfig().setUseReadOperation(true);
//                aSession.write(aData);
//            } 
//        } else {
//            aSession.write(aData);
//        }
//        return aSession;
//    }

    /**
     *
     * @param aDeviceID
     * @return
     */
    //@Override TODO
    public Object getRequest(String aDeviceID) {
        return null;//PositioningIoHandler.devicesRequests.getRequest(aDeviceID);
    }

    //@Override TODO
    public Object getWaitingRequest(String aDeviceID) {
        return null;//PositioningIoHandler.waitingRequests.getRequest(aDeviceID);
    }

    //@Override TODO
    public void putWaitingRequest(String aDeviceID, Object aRequest) {
        //PositioningIoHandler.waitingRequests.putRequest(aDeviceID, aRequest);
    }
}
