/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.Credentials;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.AccessControlExceptionResponse;
import com.eas.client.threetier.requests.ExceptionResponse;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.concurrent.PlatypusThreadFactory;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.executor.ExecutorFilter;
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
    private final Executor networkProcessor;
    private final SSLContext sslContext;
    private final int maximumConnections;
    // sync requests
    private final RequestEncoder syncEncoder;
    private final ResponseDecoder syncDecoder;
    // WARNING!!! syncSocket is intended only for single threaded using
    private final Socket syncSocket;

    public PlatypusPlatypusConnection(URL aUrl, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts, Executor aNetworkProcessor, int aMaximumConnections, boolean aInteractive) throws Exception {
        super(aUrl, null, aOnCredentials, aMaximumAuthenticateAttempts);

        host = aUrl.getHost();
        port = aUrl.getPort();

        networkProcessor = aNetworkProcessor;
        sslContext = createSSLContext();

        maximumConnections = Math.max(1, aMaximumConnections);

        connector = configureConnector(networkProcessor, sslContext, aInteractive);

        //syncSocket = sslContext.getSocketFactory().createSocket(); commented out until MINA Sslfilter bugs will be fixed
        syncSocket = new Socket();
        syncEncoder = new RequestEncoder();
        syncDecoder = new ResponseDecoder();
    }

    private NioSocketConnector configureConnector(Executor aProcessor, SSLContext sslContext, boolean aInteractive) throws Exception {
        ThreadPoolExecutor ioProcessorExecutor = new ThreadPoolExecutor(1, 1,
                3L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new PlatypusThreadFactory("polling-", false));
        ioProcessorExecutor.allowCoreThreadTimeOut(true);
        NioSocketConnector lconnector = new NioSocketConnector(aProcessor, new NioProcessor(ioProcessorExecutor));
        lconnector.setDefaultRemoteAddress(new InetSocketAddress(host, port));
        //SslFilter sslFilter = new SslFilter(sslContext); commented out until MINA Sslfilter bugs will be fixed
        //sslFilter.setUseClientMode(true); commented out until MINA Sslfilter bugs will be fixed
        /**
         * The sslFilter("encryption") filter should be placed first, according
         * to MINA architecture, but we have to communicate with a user about
         * untrusted certificates. All of this should be done in AWT Event
         * thread and so, we have to place ssqlFilter's work to AWT event
         * thread. It it would be placed in NioProcessor thread, than we will
         * get a deadlock. - AWT: write with ssl error -> handshake start and
         * wait for result from first network communication. - NioProcessor:
         * perform the handshake and ask a user about untrusted certificate and
         * wait for AWT dialog processing.
         */
        if (aInteractive) {
            lconnector.getFilterChain().addLast("executor", new ExecutorFilter(aProcessor));
            //lconnector.getFilterChain().addLast("encryption", sslFilter); commented out until MINA Sslfilter bugs will be fixed
        } else {
            //lconnector.getFilterChain().addLast("encryption", sslFilter); commented out until MINA Sslfilter bugs will be fixed
            lconnector.getFilterChain().addLast("executor", new ExecutorFilter(aProcessor));
        }
        lconnector.getFilterChain().addLast("platypusCodec", new ProtocolCodecFilter(new RequestEncoder(), new ResponseDecoder()));
        lconnector.setHandler(new IoHandlerAdapter() {

            @Override
            public void messageReceived(IoSession session, Object aResponse) throws Exception {
                RequestEnvelope requestEnv = (RequestEnvelope) session.getAttribute(RequestEnvelope.class.getSimpleName());
                sessionTicket = requestEnv.ticket;
                session.removeAttribute(RequestEnvelope.class.getSimpleName());
                pendingChanged(session, null);
                // Report about a response
                if (requestEnv.onComplete != null) {
                    requestEnv.onComplete.accept((Response) aResponse);
                }
            }

            @Override
            public void messageSent(IoSession session, Object message) throws Exception {
                super.messageSent(session, message);
            }

            @Override
            public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
                super.sessionIdle(session, status);
            }

            @Override
            public void sessionClosed(IoSession session) throws Exception {
                super.sessionClosed(session); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.SEVERE, cause.toString());
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
        networkProcessor.execute(() -> {
            IoSession _newSession = aNewSession;
            RequestEnvelope _request = aRequest;
            int rVersion;
            int rNewVersion;
            do {
                rVersion = requestsVersion.get();
                rNewVersion = rVersion + 1;
                if (rNewVersion == Integer.MAX_VALUE) {
                    rNewVersion = 0;
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
            } while (!requestsVersion.compareAndSet(rVersion, rNewVersion));
        });
    }

    @Override
    public <R extends Response> void enqueueRequest(Request aRequest, Scripts.Space aSpace, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        if (Scripts.getContext() != null) {
            Scripts.getContext().incAsyncsCount();
        }
        Attempts attemps = new Attempts();
        Consumer<Response> responseHandler = (Response response) -> {
            if (response instanceof ExceptionResponse) {
                if (onFailure != null) {
                    Exception cause = handleErrorResponse((ExceptionResponse) response, aSpace);
                    onFailure.accept(cause);
                }
            } else if (onSuccess != null) {
                if (aRequest instanceof LogoutRequest) {
                    credentials = null;
                    sessionTicket = null;
                }
                onSuccess.accept((R) response);
            }

        };
        retry(aRequest, aSpace, responseHandler, attemps);
    }

    private <R extends Response> void retry(Request aRequest, Scripts.Space aSpace, Consumer<Response> responseHandler, Attempts attemps) {
        Credentials sentCreds = credentials;
        Scripts.LocalContext context = Scripts.getContext();
        RequestEnvelope requestEnv = new RequestEnvelope(aRequest, credentials != null ? credentials.userName : null, credentials != null ? credentials.password : null, sessionTicket, (Response response) -> {
            Scripts.setContext(context);
            try {
                aSpace.process(() -> {
                    try {
                        if (response instanceof AccessControlExceptionResponse && ((AccessControlExceptionResponse) response).isNotLoggedIn()) {
                            if (attemps.count++ < maximumAuthenticateAttempts) {
                                if (credentials != null && !credentials.equals(sentCreds)) {
                                    retry(aRequest, aSpace, responseHandler, attemps);
                                } else {
                                    Credentials creds = onCredentials.call();
                                    if (creds != null) {
                                        credentials = creds;
                                        sessionTicket = null;
                                        retry(aRequest, aSpace, responseHandler, attemps);
                                    } else { // Credentials are inaccessible, so leave things as is...
                                        responseHandler.accept(response);
                                    }
                                }
                            } else {// Maximum authentication attempts per request exceeded, so leave things as is...
                                responseHandler.accept(response);
                            }
                        } else {
                            PlatypusPrincipal.setClientSpacePrincipal(credentials != null ? new PlatypusPrincipal(credentials.userName, null, null, this) : new AnonymousPlatypusPrincipal());
                            responseHandler.accept(response);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } finally {
                Scripts.setContext(null);
            }
        });
        pendingChanged(null, requestEnv);
    }

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
    public <R extends Response> R executeRequest(Request aRequest) throws Exception {
        R response = retrySync(aRequest);
        int authenticateAttempts = 0;
        while (response instanceof AccessControlExceptionResponse && ((AccessControlExceptionResponse) response).isNotLoggedIn() && authenticateAttempts++ < maximumAuthenticateAttempts && onCredentials != null) {
            Credentials creds = onCredentials.call();
            if (creds != null) {
                credentials = creds;
                sessionTicket = null;
            }
            response = retrySync(aRequest);
        }
        if (response instanceof ExceptionResponse) {
            throw handleErrorResponse((ExceptionResponse) response, Scripts.getSpace());
        } else {
            if (aRequest instanceof LogoutRequest) {
                credentials = null;
                sessionTicket = null;
            }
            return (R) response;
        }
    }

    private <R extends Response> R retrySync(Request aRequest) throws Exception {
        RequestEnvelope requestEnv = new RequestEnvelope(aRequest, null, null, sessionTicket, null);
        Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.FINE, "{0} is connecting to {1}:{2}.", new Object[]{Thread.currentThread().getName(), host, port});
        if (!syncSocket.isConnected()) {
            syncSocket.connect(new InetSocketAddress(host, port));
        }
        Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.FINE, "{0} is connected to {1}:{2}.", new Object[]{Thread.currentThread().getName(), host, port});
        SyncProtocolEncoderOutput requestOut = new SyncProtocolEncoderOutput();
        syncEncoder.encode(null, requestEnv, requestOut);
        Object oFiltered = requestOut.getFiltered();
        OutputStream os = syncSocket.getOutputStream();
        IoBuffer toWrite = (IoBuffer) oFiltered;
        os.write(toWrite.array());
        byte[] readBuffer = new byte[1024 * 16];
        ByteArrayOutputStream accumulated = new ByteArrayOutputStream();
        InputStream is = syncSocket.getInputStream();
        int read = 0;
        while (read > -1) {
            read = is.read(readBuffer);
            accumulated.write(readBuffer, 0, read);
            SyncProtocolDecoderOutput responseOut = new SyncProtocolDecoderOutput();
            IoSession session = new DummySession();
            session.setAttribute(RequestEnvelope.class.getSimpleName(), requestEnv);
            if (syncDecoder.doDecode(session, IoBuffer.wrap(accumulated.toByteArray()), responseOut)) {
                sessionTicket = requestEnv.ticket;
                return (R) responseOut.getFiltered();
            }
        }
        throw new Exception("No response was recieved via platypus protocol");
    }

    @Override
    public void shutdown() {
        try {
            if (syncSocket.isConnected()) {
                syncSocket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
