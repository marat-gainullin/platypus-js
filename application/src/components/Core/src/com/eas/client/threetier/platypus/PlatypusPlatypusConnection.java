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
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.concurrent.DeamonThreadFactory;
import com.eas.script.ScriptUtils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioProcessor;
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
    private final ThreadPoolExecutor requestsSender;
    private final ResourcePool<IoSession> sessionsPool;

    public PlatypusPlatypusConnection(URL aUrl, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts, int aMaximumThreads, int aMaximumConnections) throws Exception {
        super(aUrl, aOnCredentials, aMaximumAuthenticateAttempts);
        requestsSender = new ThreadPoolExecutor(aMaximumThreads, aMaximumThreads,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new DeamonThreadFactory("platypus-client-", false));
        requestsSender.allowCoreThreadTimeOut(true);
        sessionsPool = new BearResourcePool<IoSession>(aMaximumConnections) {

            @Override
            protected IoSession createResource() throws Exception {
                Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.FINE, "{0} is connecting to {1}:{2}.", new Object[]{Thread.currentThread().getName(), host, port});
                ConnectFuture onConnect = connector.connect();
                onConnect.awaitUninterruptibly();
                Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.FINE, "{0} is connected to {1}:{2}.", new Object[]{Thread.currentThread().getName(), host, port});
                return onConnect.getSession();
            }
        };
        host = aUrl.getHost();
        port = aUrl.getPort();

        ThreadPoolExecutor connectorExecutor = new ThreadPoolExecutor(aMaximumThreads, aMaximumThreads,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new DeamonThreadFactory("nio-connector-", true));
        connectorExecutor.allowCoreThreadTimeOut(true);
        ThreadPoolExecutor processorExecutor = new ThreadPoolExecutor(aMaximumThreads, aMaximumThreads,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new DeamonThreadFactory("nio-processor-", true));
        processorExecutor.allowCoreThreadTimeOut(true);
        connector = new NioSocketConnector(connectorExecutor, new NioProcessor(processorExecutor));
        //connector = new NioSocketConnector(null, new SimpleIoProcessorPool(NioProcessor.class, connectorExecutor, Runtime.getRuntime().availableProcessors() + 1));
        //connector = new NioSocketConnector();
        connector.setDefaultRemoteAddress(new InetSocketAddress(host, port));
        connector.setHandler(new IoHandlerAdapter() {

            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                RequestCallback rqc = (RequestCallback) session.getAttribute(RequestCallback.class.getSimpleName());
                rqc.response = (Response) message;
                synchronized (rqc.requestEnv.request) {// synchronized due to J2SE javadoc on wait()/notify() methods
                    rqc.requestEnv.request.setDone(true);
                    rqc.requestEnv.request.notifyAll();
                }
            }

            @Override
            public void messageSent(IoSession session, Object message) throws Exception {
                super.messageSent(session, message);
            }

            @Override
            public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.SEVERE, cause.getMessage());
                session.close(true);
            }

        });
        SSLContext sslContext = createSSLContext();
        SslFilter sslFilter = new SslFilter(sslContext);
        sslFilter.setUseClientMode(true);
        connector.getFilterChain().addLast("encryption", sslFilter);
        connector.getFilterChain().addLast("platypusCodec", new ProtocolCodecFilter(new RequestEncoder(), new ResponseDecoder()));
    }

    @Override
    public URL getUrl() {
        try {
            return new URL("platypus", host, port, "", new URLStreamHandler() {

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
        ScriptUtils.incAsyncsCount();
        enqueue(new RequestCallback(new RequestEnvelope(rq, null, null, null), (Response aResponse) -> {
            if (aResponse instanceof ErrorResponse) {
                if (onFailure != null) {
                    Exception cause = handleErrorResponse((ErrorResponse) aResponse);
                    if (ScriptUtils.getGlobalQueue() != null) {
                        ScriptUtils.getGlobalQueue().accept(() -> {
                            onFailure.accept(cause);
                        });
                    } else {
                        final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
                        synchronized (lock) {
                            onFailure.accept(cause);
                        }
                    }
                }
            } else {
                if (onSuccess != null) {
                    if (ScriptUtils.getGlobalQueue() != null) {
                        ScriptUtils.getGlobalQueue().accept(() -> {
                            if (rq instanceof LogoutRequest) {
                                sessionTicket = null;
                            }
                            onSuccess.accept((R) aResponse);
                        });
                    } else {
                        final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
                        synchronized (lock) {
                            if (rq instanceof LogoutRequest) {
                                sessionTicket = null;
                            }
                            onSuccess.accept((R) aResponse);
                        }
                    }
                }
            }
        }), onFailure);
    }

    private void startRequestTask(Runnable aTask) {
        Object closureLock = ScriptUtils.getLock();
        Object closureRequest = ScriptUtils.getRequest();
        Object closureResponse = ScriptUtils.getResponse();
        Object closureSession = ScriptUtils.getSession();
        PlatypusPrincipal closurePrincipal = PlatypusPrincipal.getInstance();
        requestsSender.submit(() -> {
            ScriptUtils.setLock(closureLock);
            ScriptUtils.setRequest(closureRequest);
            ScriptUtils.setResponse(closureResponse);
            ScriptUtils.setSession(closureSession);
            PlatypusPrincipal.setInstance(closurePrincipal);
            try {
                aTask.run();
            } finally {
                ScriptUtils.setLock(null);
                ScriptUtils.setRequest(null);
                ScriptUtils.setResponse(null);
                ScriptUtils.setSession(null);
                PlatypusPrincipal.setInstance(null);
            }
        });
    }

    private void enqueue(RequestCallback rqc, Consumer<Exception> onFailure) {
        startRequestTask(() -> {
            try {
                IoSession ioSession = sessionsPool.achieveResource();
                ioSession.setAttribute(RequestCallback.class.getSimpleName(), rqc);
                try {
                    Callable<Void> performer = () -> {
                        rqc.response = null;
                        rqc.completed = false;
                        rqc.requestEnv.request.setDone(false);
                        rqc.requestEnv.ticket = sessionTicket;
                        // enqueue network work
                        ioSession.write(rqc.requestEnv);
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
                                // nice try :-(
                                int authenticateAttempts = 0;
                                // Try to authenticate
                                while (rqc.response instanceof ErrorResponse
                                        && ((ErrorResponse) rqc.response).isAccessControl()
                                        && authenticateAttempts++ < maximumAuthenticateAttempts) {
                                    Credentials credentials = onCredentials.call();
                                    if (credentials != null) {
                                        rqc.requestEnv.userName = credentials.userName;
                                        rqc.requestEnv.password = credentials.password;
                                        sessionTicket = null;
                                        performer.call();
                                        if (!(rqc.response instanceof ErrorResponse) || !((ErrorResponse) rqc.response).isAccessControl()) {
                                            PlatypusPrincipal.setClientSpacePrincipal(new PlatypusPrincipal(credentials.userName, null, null, PlatypusPlatypusConnection.this));
                                        }
                                    } else {// Credentials are inaccessible, so leave things as is...
                                        authenticateAttempts = Integer.MAX_VALUE;
                                    }
                                }
                            }
                            return null;
                        });
                    }
                } finally {
                    ioSession.removeAttribute(RequestCallback.class.getSimpleName());
                    sessionsPool.returnResource(ioSession);
                }
                // Report about a result
                if (rqc.onComplete != null) {
                    rqc.completed = true;
                    rqc.onComplete.accept(rqc.response);
                } else {
                    synchronized (rqc) {// synchronized due to J2SE javadoc on wait()/notify() methods
                        rqc.completed = true;
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
            if (rq instanceof LogoutRequest) {
                sessionTicket = null;
            }
            return (R) rqc.response;
        }
    }

    @Override
    public void shutdown() {
        requestsSender.shutdown();
    }
}
