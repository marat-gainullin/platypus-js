/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.AppElementFiles;
import com.eas.client.ModulesProxy;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.cache.ScriptSecurityConfigs;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.scripts.ScriptedResource;
import com.eas.script.JsDoc;
import com.eas.sensors.api.RetranslateFactory;
import com.eas.sensors.api.SensorsFactory;
import com.eas.server.mina.platypus.PlatypusRequestsHandler;
import com.eas.server.mina.platypus.RequestDecoder;
import com.eas.server.mina.platypus.ResponseEncoder;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoEventType;
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
    //public final static int DEFAULT_CLIENT_THREADS = 3;
    //public final static int DEFAULT_WORKER_THREADS = 3;
    protected SSLContext sslContext;
    public final static String HTTP_PROTOCOL = "http";
    public final static String HTTPS_PROTOCOL = "https";
    public final static int DEFAULT_EXECUTOR_POOL_SIZE = 16;
    private final SensorsFactory acceptorsFactory;
    private final RetranslateFactory retranslateFactory;
    private final InetSocketAddress[] listenAddresses;
    private final Map<Integer, String> portsProtocols;
    private final Map<Integer, Integer> portsSessionIdleTimeouts;
    private final Map<Integer, Integer> portsSessionIdleCheckIntervals;
    private final Map<Integer, Integer> portsNumWorkerThreads;

    public PlatypusServer(ApplicationSourceIndexer aIndexer, ModulesProxy aModules, QueriesProxy<SqlQuery> aQueries, ScriptedDatabasesClient aDatabasesClient, SSLContext aSslContext, InetSocketAddress[] aAddresses, Map<Integer, String> aPortsProtocols, Map<Integer, Integer> aPortsSessionIdleTimeouts, Map<Integer, Integer> aPortsSessionIdleCheckInterval, Map<Integer, Integer> aPortsNumWorkerThreads, Set<String> aTasks, ScriptSecurityConfigs aSecurityConfigs, String aDefaultAppElement) throws Exception {
        super(aIndexer, aModules, aQueries, aDatabasesClient, aTasks, aSecurityConfigs, aDefaultAppElement);

        if (aAddresses == null) {
            throw new NullPointerException("listenAddresses");
        } else if (aAddresses.length == 0) {
            throw new IllegalArgumentException("listenAddresses is empty");
        }
        listenAddresses = aAddresses;
        portsProtocols = aPortsProtocols;
        portsSessionIdleTimeouts = aPortsSessionIdleTimeouts;
        portsSessionIdleCheckIntervals = aPortsSessionIdleCheckInterval;
        portsNumWorkerThreads = aPortsNumWorkerThreads;
        sslContext = aSslContext;
        acceptorsFactory = obtainAcceptorsFactory();
        retranslateFactory = obtainRetranslateFactory();
    }

    public void start() throws Exception {
        Logger.getLogger(PlatypusServer.class.getName()).log(Level.INFO, "Application is located at: {0}", modules.getLocalPath());
        instance = this;// Hack, but server is natural singleton and so it is ok.
        startServerTasks();
        for (InetSocketAddress s : listenAddresses) {
            initializeAndBindAcceptor(s);
        }
        assert listenAddresses != null : "listenAddresses != null";
        assert listenAddresses.length > 0 : "listenAddresses.length > 0";
    }
    
    private String findAcceptorModule(String aProtocol) throws Exception {
        String lastAcceptor = null;
        for (String taskModuleName : tasks) {
            ScriptedResource.require(new String[]{taskModuleName});
            AppElementFiles files = modules.nameToFiles(taskModuleName);
            if (files != null && files.isModule()) {
                ScriptDocument sDoc = securityConfigs.get(taskModuleName, files);
                boolean isAcceptor = false;
                Set<String> protocols = new HashSet<>();
                for (JsDoc.Tag tag : sDoc.getModuleAnnotations()) {
                    if (JsDoc.Tag.ACCEPTOR_TAG.equals(tag.getName())) {
                        isAcceptor = true;
                    }
                    if (JsDoc.Tag.ACCEPTED_PROTOCOL_TAG.equals(tag.getName()) && tag.getParams() != null && !tag.getParams().isEmpty()) {
                        protocols.addAll(tag.getParams());
                    }
                }
                if (isAcceptor) {
                    if (!protocols.isEmpty()) {// specific protocol acceptor
                        if (protocols.contains(aProtocol)) {
                            return taskModuleName;
                        }
                    } else {
                        lastAcceptor = taskModuleName;
                    }
                }
            }
        }
        return lastAcceptor;
    }

    private void tryToInitializeAndBindSensorAcceptor(String protocol, InetSocketAddress s) throws IOException, Exception {
        Logger logger = Logger.getLogger(ServerMain.class.getName());
        if (acceptorsFactory != null && acceptorsFactory.isSupported(protocol)) {
            String acceptorModuleId = findAcceptorModule(protocol);
            if (acceptorModuleId != null) {
                Integer numWorkerThreads = portsNumWorkerThreads != null ? portsNumWorkerThreads.get(s.getPort()) : null;
                if (numWorkerThreads == null || numWorkerThreads == 0) {
                    numWorkerThreads = DEFAULT_EXECUTOR_POOL_SIZE;
                }
                Integer sessionIdleTime = portsSessionIdleTimeouts != null ? portsSessionIdleTimeouts.get(s.getPort()) : null;
                if (sessionIdleTime == null || sessionIdleTime == 0) {
                    sessionIdleTime = 360;
                }
                Integer sessionIdleCheckInterval = portsSessionIdleCheckIntervals != null ? portsSessionIdleCheckIntervals.get(s.getPort()) : null;
                if (sessionIdleCheckInterval == null || sessionIdleCheckInterval == 0) {
                    sessionIdleCheckInterval = 360;
                }

                IoAcceptor sensorAcceptor = acceptorsFactory.create(protocol, numWorkerThreads, sessionIdleTime, sessionIdleCheckInterval, new com.eas.server.handlers.PositioningPacketReciever(this, acceptorModuleId, retranslateFactory));
                if (sensorAcceptor != null) {
                    sensorAcceptor.bind(s);
                    logger.info(String.format("Listening on %s; protocol: %s", s.toString(), protocol));
                }
            } else {
                logger.info(String.format("Acceptor server module was not found for protocol \"%s\"", protocol));
            }
        } else {
            logger.info(String.format("Protocol \"%s\" is not supported", protocol));
        }
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
        Integer numWorkerThreads = portsNumWorkerThreads != null ? portsNumWorkerThreads.get(s.getPort()) : null;
        if (numWorkerThreads == null || numWorkerThreads == 0) {
            numWorkerThreads = DEFAULT_EXECUTOR_POOL_SIZE;
        }

        final ExecutorService executor = new OrderedThreadPoolExecutor(numWorkerThreads);

        final IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("executor", new ExecutorFilter(executor, IoEventType.EXCEPTION_CAUGHT,
                IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT, IoEventType.SESSION_CLOSED));
        acceptor.getFilterChain().addLast("encryption", sslFilter);
        acceptor.getFilterChain().addLast("platypusCodec", new ProtocolCodecFilter(new ResponseEncoder(), new RequestDecoder()));
        PlatypusRequestsHandler handler = new PlatypusRequestsHandler(this);
        acceptor.setHandler(handler);

        Integer sessionIdleTime = portsSessionIdleTimeouts != null ? portsSessionIdleTimeouts.get(s.getPort()) : null;
        if (sessionIdleTime == null || sessionIdleTime == 0) {
            sessionIdleTime = PlatypusRequestsHandler.SESSION_TIME_OUT;
        }
        Integer sessionIdleCheckInterval = portsSessionIdleCheckIntervals != null ? portsSessionIdleCheckIntervals.get(s.getPort()) : null;
        if (sessionIdleCheckInterval == null || sessionIdleCheckInterval == 0) {
            sessionIdleCheckInterval = PlatypusRequestsHandler.IDLE_TIME_EVENT;
        }

        handler.setSessionIdleCheckInterval(sessionIdleCheckInterval);
        handler.setSessionIdleTime(sessionIdleTime);

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

    private SensorsFactory obtainAcceptorsFactory() {
        SensorsFactory recieveFactory = null;
        try {
            Class<SensorsFactory> acceptorsFactoryClass = (Class<SensorsFactory>) Class.forName("com.eas.sensors.AcceptorsFactory");
            recieveFactory = acceptorsFactoryClass.newInstance();
        } catch (ClassNotFoundException e) {
            Logger.getLogger(PlatypusServer.class.getName()).info("Sensors is not found (acceptors lookup).");
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(PlatypusServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recieveFactory;
    }

    private RetranslateFactory obtainRetranslateFactory() {
        RetranslateFactory factory = null;
        try {
            Class<RetranslateFactory> retranslateFactoryClass = (Class<RetranslateFactory>) Class.forName("com.eas.sensors.ConnectorsFactory");
            factory = retranslateFactoryClass.getConstructor(new Class<?>[]{Map.class}).newInstance(portsNumWorkerThreads);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(PlatypusServer.class.getName()).info("Sensors is not found (retranslators lookup).");
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PlatypusServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return factory;
    }

    public SensorsFactory getAcceptorsFactory() {
        return acceptorsFactory;
    }

    public RetranslateFactory getRetranslateFactory() {
        return retranslateFactory;
    }
}
