/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ModulesProxy;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.queries.QueriesProxy;
import com.eas.concurrent.PlatypusThreadFactory;
import com.eas.sensors.api.RetranslateFactory;
import com.eas.sensors.api.SensorsFactory;
import com.eas.server.mina.platypus.PlatypusRequestsHandler;
import com.eas.server.mina.platypus.RequestDecoder;
import com.eas.server.mina.platypus.ResponseEncoder;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 *
 * @author pk, mg refactoring
 */
public class PlatypusServer extends PlatypusServerCore {

    public final static int DEFAULT_PORT = 8500;
    public final static String DEFAULT_PROTOCOL = "platypus";
    protected SSLContext sslContext;
    public final static String HTTP_PROTOCOL = "http";
    public final static String HTTPS_PROTOCOL = "https";
    public final static int DEFAULT_EXECUTOR_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private final SensorsFactory acceptorsFactory;
    private final RetranslateFactory retranslateFactory;
    private final InetSocketAddress[] listenAddresses;
    private final Map<Integer, String> portsProtocols;
    private final Map<Integer, Integer> portsSessionIdleTimeouts;
    private final Map<Integer, Integer> portsSessionIdleCheckIntervals;
    private final Executor executor;

    public PlatypusServer(ApplicationSourceIndexer aIndexer, ModulesProxy aModules, QueriesProxy<SqlQuery> aQueries, ScriptedDatabasesClient aDatabasesClient, SSLContext aSslContext, InetSocketAddress[] aAddresses, Map<Integer, String> aPortsProtocols, Map<Integer, Integer> aPortsSessionIdleTimeouts, Map<Integer, Integer> aPortsSessionIdleCheckInterval, Executor aExecutor, ScriptsConfigs aScriptsConfigs, String aDefaultAppElement) throws Exception {
        super(aIndexer, aModules, aQueries, aDatabasesClient, aScriptsConfigs, aDefaultAppElement);
        if (aAddresses == null) {
            throw new NullPointerException("listenAddresses is null");
        } else if (aAddresses.length == 0) {
            throw new IllegalArgumentException("listenAddresses is empty");
        }
        executor = aExecutor;
        listenAddresses = aAddresses;
        portsProtocols = aPortsProtocols;
        portsSessionIdleTimeouts = aPortsSessionIdleTimeouts;
        portsSessionIdleCheckIntervals = aPortsSessionIdleCheckInterval;
        sslContext = aSslContext;
        acceptorsFactory = obtainAcceptorsFactory();
        retranslateFactory = obtainRetranslateFactory();
    }

    @Override
    public Type getType() {
        return Type.TSA;
    }

    public void start(Set<String> aResidents, Map<String, String> aAcceptors) throws Exception {
        //instance = this;// Hack, but server is natural singleton and so it is ok.
        for (InetSocketAddress s : listenAddresses) {
            initializeAndBindAcceptor(s, aAcceptors, executor);
        }
        assert listenAddresses != null : "listenAddresses != null";
        assert listenAddresses.length > 0 : "listenAddresses.length > 0";
    }

    private void tryToInitializeAndBindSensorAcceptor(String protocol, InetSocketAddress s, Map<String, String> aAcceptors, Executor aExecutor) throws IOException, Exception {
        if (acceptorsFactory != null && acceptorsFactory.isSupported(protocol)) {
            String acceptorModuleName = aAcceptors.get(protocol);
            if (acceptorModuleName == null) {
                acceptorModuleName = aAcceptors.get(null);
            }
            if (acceptorModuleName != null) {
                Integer sessionIdleTime = portsSessionIdleTimeouts != null ? portsSessionIdleTimeouts.get(s.getPort()) : null;
                if (sessionIdleTime == null || sessionIdleTime == 0) {
                    sessionIdleTime = 360;
                }
                Integer sessionIdleCheckInterval = portsSessionIdleCheckIntervals != null ? portsSessionIdleCheckIntervals.get(s.getPort()) : null;
                if (sessionIdleCheckInterval == null || sessionIdleCheckInterval == 0) {
                    sessionIdleCheckInterval = 360;
                }

                IoAcceptor sensorAcceptor = acceptorsFactory.create(protocol, aExecutor, sessionIdleTime, sessionIdleCheckInterval, new com.eas.server.handlers.PositioningPacketReciever(this, acceptorModuleName, retranslateFactory));
                if (sensorAcceptor != null) {
                    sensorAcceptor.bind(s);
                    Logger.getLogger(PlatypusServer.class.getName()).log(Level.INFO, "Listening {0} protocol on {1}", new Object[]{protocol, s.toString()});
                }
            } else {
                Logger.getLogger(PlatypusServer.class.getName()).log(Level.INFO, "Acceptor module was not found for {0} protocol", protocol);
            }
        } else {
            Logger.getLogger(PlatypusServer.class.getName()).log(Level.INFO, "{0} protocol is not supported", protocol);
        }
    }

    private void initializeAndBindAcceptor(InetSocketAddress s, Map<String, String> aAcceptors, Executor aExecutor) throws IOException, Exception {
        // archive protocol for address
        String protocol = DEFAULT_PROTOCOL;
        if (portsProtocols.containsKey(s.getPort())) {
            protocol = portsProtocols.get(s.getPort());
        }
        // initialize acceptor according to protocol
        if (DEFAULT_PROTOCOL.equalsIgnoreCase(protocol)) {
            initializeAndBindPlatypusAcceptor(s);
        } else {
            tryToInitializeAndBindSensorAcceptor(protocol, s, aAcceptors, aExecutor);
        }
    }

    private void initializeAndBindPlatypusAcceptor(InetSocketAddress s) throws IOException, Exception {
        //final SslFilter sslFilter = new SslFilter(sslContext); commented out until MINA Sslfilter bugs will be fixed
        ThreadPoolExecutor connectionsPollerExecutor = new ThreadPoolExecutor(1, 1,
                3L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new PlatypusThreadFactory("nio-polling-", false));
        final IoAcceptor acceptor = new NioSocketAcceptor(connectionsPollerExecutor, new NioProcessor(executor));
        //acceptor.getFilterChain().addLast("encryption", sslFilter); commented out until MINA Sslfilter bugs will be fixed
        acceptor.getFilterChain().addLast("platypusCodec", new ProtocolCodecFilter(new ResponseEncoder(), new RequestDecoder()));
        /*
        acceptor.getFilterChain().addLast("executor", new ExecutorFilter(executor, IoEventType.EXCEPTION_CAUGHT,
                IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT, IoEventType.SESSION_CLOSED, IoEventType.SESSION_IDLE, IoEventType.CLOSE, IoEventType.WRITE));
         */
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
        Logger.getLogger(ServerMain.class.getName()).log(Level.INFO, "Listening platypus protocol on {0}", s.toString());
    }

    private SensorsFactory obtainAcceptorsFactory() {
        SensorsFactory recieveFactory = null;
        try {
            Class<SensorsFactory> acceptorsFactoryClass = (Class<SensorsFactory>) Class.forName("com.eas.sensors.AcceptorsFactory");
            recieveFactory = acceptorsFactoryClass.newInstance();
        } catch (ClassNotFoundException e) {
            Logger.getLogger(PlatypusServer.class.getName()).log(Level.INFO, "Sensors acceptors are not on the classpath.");
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(PlatypusServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recieveFactory;
    }

    private RetranslateFactory obtainRetranslateFactory() {
        RetranslateFactory factory = null;
        try {
            Class<RetranslateFactory> retranslateFactoryClass = (Class<RetranslateFactory>) Class.forName("com.eas.sensors.ConnectorsFactory");
            factory = retranslateFactoryClass.getConstructor(new Class<?>[]{Map.class}).newInstance(DEFAULT_EXECUTOR_POOL_SIZE);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(PlatypusServer.class.getName()).info("Sensors retranslators are not on the classpath.");
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
