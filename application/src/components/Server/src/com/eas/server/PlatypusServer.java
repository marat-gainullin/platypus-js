/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.DatabasesClient;
import com.eas.client.cache.FilesAppCache;
import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.Request;
import com.eas.server.mina.platypus.PlatypusRequestsHandler;
import com.eas.server.mina.platypus.RequestDecoder;
import com.eas.server.mina.platypus.ResponseEncoder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 *
 * @author pk, mg refactoring
 */
public class PlatypusServer extends PlatypusServerCore {

    public final static int DEFAULT_PORT = 8500;
    public final static String DEFAULT_PROTOCOL = "platypus";
    public final static int DEFAULT_CLIENT_THREADS = 3;
    public final static int DEFAULT_WORKER_THREADS = 3;
    protected SSLContext sslContext;
    public final static String HTTP_PROTOCOL = "http";
    public final static String HTTPS_PROTOCOL = "https";
    public final static int DEFAULT_EXECUTOR_POOL_SIZE = 16;
    private final ExecutorService bgTasksExecutor;
    private final InetSocketAddress[] listenAddresses;
    private final Map<Integer, String> portsProtocols;
    private IoAcceptor sensorAcceptor;

    public PlatypusServer(DatabasesClient aDatabasesClient, SSLContext aSslContext, InetSocketAddress[] aAddresses, Map<Integer, String> aPortsProtocols, Set<ModuleConfig> aModuleConfigs, String aDefaultAppElement) throws Exception {
        super(aDatabasesClient, aModuleConfigs, aDefaultAppElement);

        if (aAddresses == null) {
            throw new NullPointerException("listenAddresses");
        } else if (aAddresses.length == 0) {
            throw new IllegalArgumentException("listenAddresses is empty");
        }
        bgTasksExecutor = Executors.newCachedThreadPool();
        listenAddresses = aAddresses;
        portsProtocols = aPortsProtocols;
        sslContext = aSslContext;
    }

    public void start() throws Exception {
        for (InetSocketAddress s : listenAddresses) {
            initializeAndBindAcceptor(s);
        }
        Logger.getLogger(PlatypusServer.class.getName()).log(Level.INFO, "Application elements are located at: {0}", databasesClient.getAppCache() instanceof FilesAppCache ? ((FilesAppCache)databasesClient.getAppCache()).getSrcPathName() : databasesClient.getSettings().getUrl());
        startBackgroundTasks(bgTasksExecutor);
        assert listenAddresses != null : "listenAddresses != null";
        assert listenAddresses.length > 0 : "listenAddresses.length > 0";
    }

    public void stop(int awaitTimeout, TimeUnit timeUnit) {
        try {
            databasesClient.shutdown();
        } catch (Exception ex) {
            Logger.getLogger(PlatypusServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enqueuePlatypusRequest(final Request aRequest, final Runnable onFailure, final Runnable onSuccess) throws Exception {
        bgTasksExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final RequestHandler handler = RequestHandlerFactory.getHandler(PlatypusServer.this, sessionManager.getSystemSession(), aRequest);
                    handler.run();
                    if (handler.getResponse() instanceof ErrorResponse) {
                        if (onFailure != null) {
                            onFailure.run();
                        }
                    } else {
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusServer.class.getName()).log(Level.SEVERE, null, ex);
                    if (onFailure != null) {
                        onFailure.run();
                    }
                }
            }
        });
    }

    private void tryToInitializeAndBindSensorAcceptor(String protocol, InetSocketAddress s) throws IOException, Exception {
        Logger logger = Logger.getLogger(ServerMain.class.getName());
        if (com.eas.sensors.positioning.AcceptorsFactory.isSupported(protocol)) {
            ModuleConfig acceptorConfig = findAcceptorModule(protocol);
            if (acceptorConfig != null) {
                sensorAcceptor = com.eas.sensors.positioning.AcceptorsFactory.create(protocol, new com.eas.server.handlers.PositioningPacketReciever(this, acceptorConfig.getModuleId()));
                if (sensorAcceptor != null) {
                    sensorAcceptor.bind(s);
                    logger.info(String.format("\nListening on %s; protocol: %s\n", s.toString(), protocol));
                }
            } else {
                logger.info(String.format("\nAcceptor server module was not found for protocol \"%s\"", protocol));
            }
        } else {
            logger.info(String.format("\nProtocol \"%s\" is not supported", protocol));
        }
    }

    private int startBackgroundTasks(ExecutorService executor) throws Exception {
        int startedTasks = 0;
        for (ModuleConfig config : moduleConfigs) {
            if (startBackgroundTask(config, executor)) {
                startedTasks++;
            }
        }
        return startedTasks;
    }

    private void initializeAndBindAcceptor(InetSocketAddress s) throws IOException, Exception {
        // archive protocol for address
        String protocol = DEFAULT_PROTOCOL;
        if (portsProtocols.containsKey(s.getPort())) {
            protocol = portsProtocols.get(s.getPort());
        }
        // initialize acceptor according to protocol
        if (DEFAULT_PROTOCOL.equalsIgnoreCase(protocol)) {
            initializeAndBindPlatypusAcceptor(s);
        } else {
            tryToInitializeAndBindSensorAcceptor(protocol, s);
        }
    }

    private void initializeAndBindPlatypusAcceptor(InetSocketAddress s) throws IOException, Exception {
        final SslFilter sslFilter = new SslFilter(sslContext);
        final ExecutorService executor = new OrderedThreadPoolExecutor(DEFAULT_EXECUTOR_POOL_SIZE);

        final IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("executor", new ExecutorFilter(executor));
        acceptor.getFilterChain().addLast("encryption", sslFilter);
        acceptor.getFilterChain().addLast("platypusRequestCodec", new ProtocolCodecFilter(new ResponseEncoder(), new RequestDecoder()));
        acceptor.setHandler((IoHandler) new PlatypusRequestsHandler(this));

        acceptor.bind(s);

        Logger logger = Logger.getLogger(ServerMain.class.getName());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\nListening on %s; protocol: platypus\n", s.toString()));
        if (sslFilter.getEnabledCipherSuites() != null) {
            sb.append("Enabled cipher suites: ").append(sslFilter.getEnabledCipherSuites()).append("\n");
        }
        if (sslFilter.getEnabledProtocols() != null) {
            sb.append("Enabled protocols: ").append(sslFilter.getEnabledProtocols()).append("\n");
        }
        logger.info(sb.toString());
    }

    private ModuleConfig findAcceptorModule(String aProtocol) throws Exception {
        ModuleConfig commonAcceptorConfig = null;
        ModuleConfig specificAcceptorConfig = null;
        for (ModuleConfig config : moduleConfigs) {
            String protocol = config.getAcceptProtocol();
            if (config.isAcceptor()) {
                if (protocol == null || protocol.isEmpty()) {
                    commonAcceptorConfig = config;
                } else if (protocol.equalsIgnoreCase(aProtocol)) {
                    specificAcceptorConfig = config;
                }
            }
        }
        if (specificAcceptorConfig != null) {
            return specificAcceptorConfig;
        } else {
            return commonAcceptorConfig;
        }
    }

    /**
     * @return the sensorAcceptor
     */
    public IoAcceptor getSensorAcceptor() {
        return sensorAcceptor;
    }

    /**
     * @param aAcceptor the sensorAcceptor to set
     */
    public void setSensorAcceptor(IoAcceptor aAcceptor) {
        sensorAcceptor = aAcceptor;
    }
}
