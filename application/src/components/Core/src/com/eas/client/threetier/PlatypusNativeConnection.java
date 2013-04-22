/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;

/**
 *
 * @author pk
 */
public class PlatypusNativeConnection extends PlatypusConnection {

    public final static int RECONNECTION_DELAY = 5000; //5sec
    private Map<Long, Request> activeRequests = new ConcurrentHashMap<>();
    private BlockingDeque<Request> requestQueue = new LinkedBlockingDeque<>();
    private String host;
    private int port;
    private SocketFactory socketFactory;
    private Socket socket;
    private RequestsSender sender;
    private ResponsesReceiver receiver;
    private Thread senderThread;
    private Thread receiverThread;
    private int reconnectionCount = 0;

    public PlatypusNativeConnection(SocketFactory aSocketFactory, String aHost, int aPort) {
        host = aHost;
        port = aPort;
        socketFactory = aSocketFactory;
    }

    public void enqueueRequest(Request rq) throws InterruptedException {
        requestQueue.offer(rq);
    }

    @Override
    public void executeRequest(Request rq) throws Exception {
        requestQueue.offer(rq);
        rq.waitCompletion();
        if (rq.getResponse() instanceof ErrorResponse) {
            handleErrorResponse((ErrorResponse) rq.getResponse());
        }
    }

    @Override
    public synchronized void connect() throws Exception {
        Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.INFO, "Connecting to {0}:{1}", new Object[]{host, port});
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        socket = socketFactory.createSocket(host, port);
        ProtoWriter sigWriter = new ProtoWriter(socket.getOutputStream());
        sigWriter.putSignature();
        sigWriter.flush();
        ProtoReader sigReader = new ProtoReader(socket.getInputStream());
        sigReader.getSignature();
        Logger.getLogger(PlatypusNativeConnection.class.getName()).finest("Got signature from server.");
    }

    public synchronized int reconnect(int count) throws Exception {
        final boolean socketNull = socket == null,
                connected = socketNull ? false : socket.isConnected(),
                closed = socketNull ? true : socket.isClosed(),
                bound = socketNull ? false : socket.isBound(),
                outputShutdown = socketNull ? null : socket.isOutputShutdown(),
                inputShutdown = socketNull ? null : socket.isInputShutdown();
        if (count == reconnectionCount) {
            Thread.sleep(RECONNECTION_DELAY);
            connect();
            /*
            if (sessionId != null) {
            Request rq = new LoginRequest(login, password, sessionId);
            ProtoWriter writer = new ProtoWriter(socket.getOutputStream());
            rq.write(writer);
            writer.flush();
            }
             */
            return ++reconnectionCount;
        } else {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).finest(String.format("Reconnection aborted, already connected: socketNull=%b, connected=%b, closed=%b, bound=%b, outputShutdown=%b, inputShutdown=%b", socketNull, connected, closed, bound, outputShutdown, inputShutdown));
            return reconnectionCount;
        }
    }

    public synchronized int getReconnectionCount() {
        return reconnectionCount;
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void shutdown() {
        receiverThread.interrupt();
        senderThread.interrupt();
        disconnect();
        try {
            receiverThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            senderThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public OutputStream getSocketOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public InputStream getSocketInputStream() throws IOException {
        return socket.getInputStream();
    }

    public void createExchangeThreads() {
        sender = new RequestsSender(requestQueue, activeRequests, this);
        receiver = new ResponsesReceiver(this, activeRequests);
        senderThread = new Thread(sender, "Requests sender");
        receiverThread = new Thread(receiver, "Responses receiver");
        senderThread.setDaemon(true);
        receiverThread.setDaemon(true);
        senderThread.start();
        receiverThread.start();
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String aValue) {
        sessionId = aValue;
    }
    
}
