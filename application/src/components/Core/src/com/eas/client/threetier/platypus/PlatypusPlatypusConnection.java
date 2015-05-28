/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.login.Credentials;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.proto.ProtoReader;
import com.eas.script.Scripts;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
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

    private final Queue<IoSession> sessions = new ConcurrentLinkedQueue<>();
    private final Queue<RequestEnvelope> pending = new ConcurrentLinkedQueue<>();

    private String sessionTicket;
    private final SocketConnector connector;
    private final Executor requestsProcessor;
    private final SSLContext sslContext;
    private final int maximumConnections;
    // sync requests
    private final RequestEncoder syncEncoder;
    private final ResponseDecoder syncDecoder;

    public PlatypusPlatypusConnection(URL aUrl, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts, Executor aNetworkProcessor, int aMaximumConnections) throws Exception {
        super(aUrl, aOnCredentials, aMaximumAuthenticateAttempts);

        host = aUrl.getHost();
        port = aUrl.getPort();

        requestsProcessor = aNetworkProcessor;
        sslContext = createSSLContext();

        maximumConnections = aMaximumConnections;

        connector = configureConnector(requestsProcessor, sslContext);
        syncEncoder = new RequestEncoder();
        syncDecoder = new ResponseDecoder();
    }

    private NioSocketConnector configureConnector(Executor aProcessor, SSLContext sslContext) throws Exception {
        NioSocketConnector lconnector = new NioSocketConnector(aProcessor, new NioProcessor(aProcessor));
        lconnector.setDefaultRemoteAddress(new InetSocketAddress(host, port));
        SslFilter sslFilter = new SslFilter(sslContext);
        sslFilter.setUseClientMode(true);
        lconnector.getFilterChain().addLast("encryption", sslFilter);
        lconnector.getFilterChain().addLast("platypusCodec", new ProtocolCodecFilter(new RequestEncoder(), new ResponseDecoder()));
        lconnector.setHandler(new IoHandlerAdapter() {

            @Override
            public void messageReceived(IoSession session, Object in) throws Exception {
                RequestEnvelope requestEnv = (RequestEnvelope) session.getAttribute(RequestEnvelope.class.getSimpleName());
                sessionTicket = requestEnv.ticket;
                session.removeAttribute(RequestEnvelope.class.getSimpleName());
                pendingChanged(session, null);
                // Report about a response
                if (requestEnv.onComplete != null) {
                    requestEnv.onComplete.accept((InputStream) in);
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
        return lconnector;
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

    // Zombie counters...
    protected volatile int pendingSize;
    protected volatile int sessionsSize;
    protected AtomicInteger requestsVersion = new AtomicInteger();

    protected void pendingChanged(IoSession aNewSession, RequestEnvelope aRequest) {
        requestsProcessor.execute(() -> {
            IoSession _newSession = aNewSession;
            RequestEnvelope _request = aRequest;
            int rVersion;
            do {
                rVersion = requestsVersion.get();
                if (rVersion == Integer.MAX_VALUE) {
                    rVersion = -1;
                }
                // Some social payload
                if (_newSession != null) {
                    //if (pendingSize >= sessionsSize) {
                    sessions.offer(_newSession);
                    sessionsSize++;
                    //}
                    _newSession = null;
                }
                if (_request != null) {
                    pending.offer(_request);
                    _request = null;
                    pendingSize++;
                }
                // end of social payload
                IoSession session = sessions.poll();
                RequestEnvelope request = pending.poll();
                if (session != null && request != null) {
                    session.setAttribute(RequestEnvelope.class.getSimpleName(), request);
                    session.write(request);
                    pendingSize--;
                } else {
                    if (session != null) {
                        sessions.offer(session);
                    }
                    if (request != null) {
                        pending.offer(request);
                    }
                    if (request != null && session == null && sessionsSize < maximumConnections) {
                        ConnectFuture onConnect = connector.connect();
                        onConnect.addListener((IoFuture future) -> {
                            IoSession created = future.getSession();
                            pendingChanged(created, null);
                        });
                    }
                }
            } while (!requestsVersion.compareAndSet(rVersion, rVersion + 1));
        });
    }

    @Override
    public <R extends Response> void enqueueRequest(Request aRequest, Scripts.Space aSpace, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        aSpace.incAsyncsCount();
        RequestEnvelope requestEnv = new RequestEnvelope(aRequest, null, null, sessionTicket, (InputStream is) -> {
            aSpace.process(() -> {
                try {
                    final ProtoReader responseReader = new ProtoReader(is);
                    Response response;
                    try {
                        response = PlatypusResponseReader.read(responseReader, aRequest, aSpace);
                    } finally {
                        is.close();
                    }
                    if (response instanceof ErrorResponse) {
                        if (onFailure != null) {
                            Exception cause = handleErrorResponse((ErrorResponse) response);
                            onFailure.accept(cause);
                        }
                    } else {
                        if (onSuccess != null) {
                            if (aRequest instanceof LogoutRequest) {
                                sessionTicket = null;
                                if (onLogout != null) {
                                    onLogout.run();
                                }
                            }
                            onSuccess.accept((R) response);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        pendingChanged(null, requestEnv);
    }

    /*
     private void enqueue(RequestCallback rqc, Consumer<Exception> onFailure) {
     Runnable doer = () -> {
     try {
     try {
     Callable<Void> performer = () -> {
     rqc.response = null;
     rqc.completed = false;
     rqc.requestEnv.request.setDone(false);
     rqc.requestEnv.ticket = sessionTicket;
     // enqueue network work
     ioSession.write(rqc.requestEnv);
     // wait completion from the network subsystem
     rqc.requestEnv.waitRequestCompletion();
     sessionTicket = rqc.requestEnv.ticket;
     return null;
     };
     // Try to communicate with the server
     performer.call();
     if (rqc.response instanceof ErrorResponse
     && ((ErrorResponse) rqc.response).isNotLoggedIn()) {
     sequence.in(() -> {
     // probably new ticket from another thread...
     rqc.requestEnv.userName = null;
     rqc.requestEnv.password = null;
     performer.call();
     if (rqc.response instanceof ErrorResponse
     && ((ErrorResponse) rqc.response).isNotLoggedIn()) {
     // nice try :-(
     int authenticateAttempts = 0;
     // Try to authenticate
     while (rqc.response instanceof ErrorResponse
     && ((ErrorResponse) rqc.response).isNotLoggedIn()
     && authenticateAttempts++ < maximumAuthenticateAttempts) {
     Credentials credentials = onCredentials.call();
     if (credentials != null) {
     rqc.requestEnv.userName = credentials.userName;
     rqc.requestEnv.password = credentials.password;
     sessionTicket = null;
     performer.call();
     if (!(rqc.response instanceof ErrorResponse) || !((ErrorResponse) rqc.response).isAccessControl()) {
     PlatypusPrincipal.setClientSpacePrincipal(new PlatypusPrincipal(credentials.userName, null, null, PlatypusPlatypusConnection.this));
     if (onLogin != null) {
     onLogin.run();
     }
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
     rqc.completed = true;
     }
     } catch (Exception ex) {
     Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.SEVERE, null, ex);
     if (onFailure != null) {
     onFailure.accept(ex);
     }
     }
     };
     if (onFailure != null) {
     requestsSender.submit(doer);
     } else {
     doer.run();
     }
     }
     */
    protected static class SyncProtocolEncoderOutput implements ProtocolEncoderOutput {

        protected Object filtered;

        public SyncProtocolEncoderOutput() {
        }

        public Object getFiltered() {
            return filtered;
        }

        @Override
        public void write(Object encodedMessage) {
            filtered = encodedMessage;
        }

        @Override
        public void mergeAll() {
        }

        @Override
        public WriteFuture flush() {
            return null;
        }

    };

    protected static class SyncProtocolDecoderOutput implements ProtocolDecoderOutput {

        protected Object filtered;

        public SyncProtocolDecoderOutput() {
        }

        public Object getFiltered() {
            return filtered;
        }

        @Override
        public void write(Object decodedMessage) {
            filtered = decodedMessage;
        }

        @Override
        public void flush(IoFilter.NextFilter nextFilter, IoSession session) {
        }

    };

    @Override
    public <R extends Response> R executeRequest(Request rq) throws Exception {
        RequestEnvelope requestEnv = new RequestEnvelope(rq, null, null, sessionTicket, null);
        Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.FINE, "{0} is connecting to {1}:{2}.", new Object[]{Thread.currentThread().getName(), host, port});
        try (Socket conn = sslContext.getSocketFactory().createSocket()) {
            conn.connect(new InetSocketAddress(host, port));
            Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.FINE, "{0} is connected to {1}:{2}.", new Object[]{Thread.currentThread().getName(), host, port});
            SyncProtocolEncoderOutput requestOut = new SyncProtocolEncoderOutput();
            syncEncoder.encode(null, requestEnv, requestOut);
            Object oFiltered = requestOut.getFiltered();
            try (OutputStream os = conn.getOutputStream()) {
                IoBuffer toWrite = (IoBuffer) oFiltered;
                os.write(toWrite.array());
            }
            byte[] readBuffer = new byte[1024 * 16];
            ByteArrayOutputStream accumulated = new ByteArrayOutputStream();
            try (InputStream is = conn.getInputStream()) {
                int read = 0;
                while (read > -1) {
                    read = is.read(readBuffer);
                    accumulated.write(readBuffer, 0, read);
                    SyncProtocolDecoderOutput responseOut = new SyncProtocolDecoderOutput();
                    if (syncDecoder.doDecode(null, IoBuffer.wrap(accumulated.toByteArray()), responseOut)) {
                        try (InputStream responseIs = (InputStream) responseOut.getFiltered()) {
                            final ProtoReader responseReader = new ProtoReader(responseIs);
                            Response response = PlatypusResponseReader.read(responseReader, rq, Scripts.getSpace());
                            if (response instanceof ErrorResponse) {
                                throw handleErrorResponse((ErrorResponse) response);
                            } else {
                                if (rq instanceof LogoutRequest) {
                                    sessionTicket = null;
                                    if (onLogout != null) {
                                        onLogout.run();
                                    }
                                }
                                return (R) response;
                            }
                        }
                    }
                }
            }
            throw new Exception("No response was recieved via platypus protocol");
        }
    }

    @Override
    public void shutdown() {
    }
}
