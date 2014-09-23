/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.bearsoft.rowset.resourcepool.BearResourcePool;
import com.bearsoft.rowset.resourcepool.ResourcePool;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.ErrorResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 *
 * @author pk
 */
public class PlatypusNativeConnection extends PlatypusConnection {

    private final String host;
    private final int port;
    private final SocketConnector connector;
    private final RequestEncoder requestEncoder = new RequestEncoder();
    private final ExecutorService requestsSender = Executors.newCachedThreadPool();
    private final ResourcePool<IoSession> sessionsPool = new BearResourcePool<IoSession>(10) {

        @Override
        protected IoSession createResource() throws Exception {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.INFO, "Connecting to {0}:{1}.", new Object[]{host, port});
            ConnectFuture onConnect = connector.connect(new InetSocketAddress(host, port));
            onConnect.awaitUninterruptibly();
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.INFO, "Connected to  {0}:{1}.", new Object[]{host, port});
            return onConnect.getSession();
        }
    };

    public PlatypusNativeConnection(URL aURL) throws Exception {
        super();
        host = aURL.getHost();
        port = aURL.getPort();
        connector = new NioSocketConnector();
        connector.setHandler(new IoHandlerAdapter() {

            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                RequestCallback rqc = (RequestCallback) session.getAttribute(RequestCallback.class.getSimpleName());
                session.removeAttribute(RequestCallback.class.getSimpleName());
                sessionsPool.returnResource(session);
                Response response = (Response) message;
                rqc.response = response;
                rqc.request.setDone(true);
                rqc.request.notifyAll();
            }

            @Override
            public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.SEVERE, null, cause);
                session.close(true);
            }

        });
        connector.getFilterChain().addLast("platypusCodec", new ProtocolCodecFilter(requestEncoder, new ResponseDecoder()));
        final SslFilter sslFilter = new SslFilter(PlatypusClient.createSSLContext());
        connector.getFilterChain().addLast("encryption", sslFilter);
    }

    @Override
    public URL getUrl() {
        try {
            return new URL("platypus", host, port, null, new URLStreamHandler() {

                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
        } catch (MalformedURLException ex) {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void setLoginCredentials(String aUserName, String aPassword, String aSessionId) {
        requestEncoder.setSessionTicket(aSessionId);
        super.setLoginCredentials(aUserName, aPassword, aSessionId);
    }

    @Override
    public <R extends Response> void enqueueRequest(Request rq, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        enqueue(new RequestCallback(rq, (Response aResponse) -> {
            if (aResponse instanceof ErrorResponse) {
                if (onFailure != null) {
                    onFailure.accept(handleErrorResponse((ErrorResponse) aResponse));
                }
            } else {
                if (onSuccess != null) {
                    onSuccess.accept((R) aResponse);
                }
            }
        }), onFailure);
    }

    private void enqueue(RequestCallback rqc, Consumer<Exception> onFailure) {
        requestsSender.submit(() -> {
            try {
                IoSession session = sessionsPool.achieveResource();
                session.write(rqc.request);
                synchronized (rqc.request) {// synchronized due to J2SE javadoc on wait()/notify() methods
                    while (!rqc.request.isDone()) {
                        rqc.request.wait();
                    }
                }
                if (rqc.onComplete != null) {
                    rqc.onComplete.accept(rqc.response);
                } else {
                    rqc.notifyAll();
                }
            } catch (Exception ex) {
                Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.SEVERE, null, ex);
                if (onFailure != null) {
                    onFailure.accept(ex);
                }
            }
        });
    }

    @Override
    public <R extends Response> R executeRequest(Request rq) throws Exception {
        RequestCallback rqc = new RequestCallback(rq, null);
        enqueue(rqc, null);
        rqc.waitCompletion();
        if (rqc.response instanceof ErrorResponse) {
            throw handleErrorResponse((ErrorResponse) rqc.response);
        } else {
            return (R) rqc.response;
        }
    }

    @Override
    public void shutdown() {
        requestsSender.shutdown();
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
