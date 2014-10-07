/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.bearsoft.rowset.resourcepool.BearResourcePool;
import com.bearsoft.rowset.resourcepool.ResourcePool;
import com.eas.client.login.Credentials;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.concurrent.DeamonThreadFactory;
import com.eas.script.ScriptUtils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.concurrent.Callable;
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
 * @author pk, mg refactoring
 */
public class PlatypusPlatypusConnection extends PlatypusConnection {

    private final String host;
    private final int port;
    private String sessionTicket;
    private final SocketConnector connector;
    private final RequestEncoder requestEncoder = new RequestEncoder();
    private final ExecutorService requestsSender = Executors.newCachedThreadPool(new DeamonThreadFactory("platypus-client-"));
    private final ResourcePool<IoSession> sessionsPool = new BearResourcePool<IoSession>(10) {

        @Override
        protected IoSession createResource() throws Exception {
            Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.INFO, "Connecting to {0}:{1}.", new Object[]{host, port});
            ConnectFuture onConnect = connector.connect(new InetSocketAddress(host, port));
            onConnect.awaitUninterruptibly();
            Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.INFO, "Connected to  {0}:{1}.", new Object[]{host, port});
            return onConnect.getSession();
        }
    };

    public PlatypusPlatypusConnection(URL aUrl, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts) throws Exception {
        super(aUrl, aOnCredentials, aMaximumAuthenticateAttempts);
        host = aUrl.getHost();
        port = aUrl.getPort();
        connector = new NioSocketConnector();
        connector.setHandler(new IoHandlerAdapter() {

            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                RequestCallback rqc = (RequestCallback) session.getAttribute(RequestCallback.class.getSimpleName());
                session.removeAttribute(RequestCallback.class.getSimpleName());
                sessionsPool.returnResource(session);
                Response response = (Response) message;
                rqc.response = response;
                rqc.requestEnv.request.setDone(true);
                synchronized (rqc.requestEnv.request) {// synchronized due to J2SE javadoc on wait()/notify() methods
                    rqc.requestEnv.request.notifyAll();
                }
            }

            @Override
            public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.SEVERE, null, cause);
                session.close(true);
            }

        });
        connector.getFilterChain().addLast("platypusCodec", new ProtocolCodecFilter(requestEncoder, new ResponseDecoder()));
        final SslFilter sslFilter = new SslFilter(createSSLContext());
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
            Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public <R extends Response> void enqueueRequest(Request rq, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        enqueue(new RequestCallback(new RequestEnvelope(rq, null, null, null), (Response aResponse) -> {
            final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
            synchronized (lock) {
                if (aResponse instanceof ErrorResponse) {
                    if (onFailure != null) {
                        onFailure.accept(handleErrorResponse((ErrorResponse) aResponse));
                    }
                } else {
                    if (onSuccess != null) {
                        onSuccess.accept((R) aResponse);
                    }
                }
            }
        }), onFailure);
    }

    private void startRequestTask(Runnable aTask){
        PlatypusPrincipal closurePrincipal = PlatypusPrincipal.getInstance();
        Object closureRequest = ScriptUtils.getRequest();
        Object closureResponse = ScriptUtils.getResponse();
        Object closureLock = ScriptUtils.getLock();
        requestsSender.submit(() -> {
            ScriptUtils.setLock(closureLock);
            ScriptUtils.setRequest(closureRequest);
            ScriptUtils.setResponse(closureResponse);
            PlatypusPrincipal.setInstance(closurePrincipal);
            try {
                aTask.run();
            } finally {
                ScriptUtils.setLock(null);
                ScriptUtils.setRequest(null);
                ScriptUtils.setResponse(null);
                PlatypusPrincipal.setInstance(null);
            }
        });
    }
    
    private void enqueue(RequestCallback rqc, Consumer<Exception> onFailure) {
        startRequestTask(() -> {
            try {
                IoSession session = sessionsPool.achieveResource();
                Callable<Void> performer = () -> {
                    rqc.response = null;
                    rqc.requestEnv.request.setDone(false);
                    rqc.requestEnv.ticket = sessionTicket;
                    // enqueue network work
                    session.write(rqc.requestEnv);
                    // wait completion from the network subsystem
                    synchronized (rqc.requestEnv.request) {// synchronized due to J2SE javadoc on wait()/notify() methods
                        while (!rqc.requestEnv.request.isDone()) {
                            rqc.requestEnv.request.wait();
                        }
                    }
                    sessionTicket = rqc.requestEnv.ticket;
                    return null;
                };
                // Try to communicate with the server
                performer.call();
                if (rqc.response instanceof ErrorResponse
                        && ((ErrorResponse) rqc.response).isAccessControl()) {
                    sequence.in(() -> {
                        // probably new ticket from another thread...
                        rqc.requestEnv.userName = null;
                        rqc.requestEnv.password = null;
                        performer.call();
                        if (rqc.response instanceof ErrorResponse
                                && ((ErrorResponse) rqc.response).isAccessControl()) {
                            // nice try :(
                            int authenticateAttempts = 0;
                            // Try to authenticate
                            while (rqc.response instanceof ErrorResponse
                                    && ((ErrorResponse) rqc.response).isAccessControl()
                                    && authenticateAttempts++ < maximumAuthenticateAttempts) {
                                Credentials credentials = onCredentials.call();
                                if (credentials != null) {
                                    rqc.requestEnv.userName = credentials.userName;
                                    rqc.requestEnv.password = credentials.password;
                                    performer.call();
                                } else {// Credentials are inaccessible, so leave things as is...
                                    authenticateAttempts = Integer.MAX_VALUE;
                                }
                            }
                        }
                        return null;
                    });
                }
                // Report about a result
                if (rqc.onComplete != null) {
                    rqc.onComplete.accept(rqc.response);
                } else {
                    synchronized (rqc) {// synchronized due to J2SE javadoc on wait()/notify() methods
                        rqc.notifyAll();
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.SEVERE, null, ex);
                if (onFailure != null) {
                    onFailure.accept(ex);
                }
            }
        });
    }

    @Override
    public <R extends Response> R executeRequest(Request rq) throws Exception {
        RequestCallback rqc = new RequestCallback(new RequestEnvelope(rq, null, null, null), null);
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
}
