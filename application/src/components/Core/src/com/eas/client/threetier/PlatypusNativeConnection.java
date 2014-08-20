/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.resourcepool.BearResourcePool;
import com.bearsoft.rowset.resourcepool.ResourcePool;
import com.eas.client.threetier.mina.platypus.PlatypusRequestsHandler;
import com.eas.client.threetier.mina.platypus.RequestEncoder;
import com.eas.client.threetier.mina.platypus.ResponseDecoder;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.mina.core.future.ConnectFuture;
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

    public static class RequestCallback {

        public Request request;
        public Response response;
        public Consumer<Response> onComplete;

        public RequestCallback(Request aRequest, Consumer<Response> aOnSuccess) {
            super();
            request = aRequest;
            onComplete = aOnSuccess;
        }

        public synchronized void waitCompletion() throws InterruptedException {
            while (!request.isDone()) {
                wait();
            }
        }
    }

    private final String host;
    private final int port;
    private final SocketConnector connector;
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

    public PlatypusNativeConnection(SSLContext aSSLContext, String aHost, int aPort) {
        super();
        host = aHost;
        port = aPort;
        connector = new NioSocketConnector();
        connector.setHandler(new PlatypusRequestsHandler() {

            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                RequestCallback rqc = (RequestCallback) session.getAttribute(RequestCallback.class.getSimpleName());
                session.removeAttribute(RequestCallback.class.getSimpleName());
                sessionsPool.returnResource(session);
                Response response = (Response) message;
                rqc.request.setDone(true);
                if (rqc.onComplete != null) {
                    rqc.onComplete.accept(response);
                } else {
                    rqc.response = response;
                    rqc.notifyAll();
                }
            }

        });
        connector.getFilterChain().addLast("platypusCodec", new ProtocolCodecFilter(new RequestEncoder(), new ResponseDecoder()));
        if (aSSLContext != null) {
            final SslFilter sslFilter = new SslFilter(aSSLContext);
            connector.getFilterChain().addLast("encryption", sslFilter);
        }
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
                session.setAttribute(RequestCallback.class.getSimpleName(), rqc);
                session.write(rqc.request);
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
