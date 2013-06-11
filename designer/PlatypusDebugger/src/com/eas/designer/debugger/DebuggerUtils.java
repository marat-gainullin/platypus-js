/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.BreakpointsMBean;
import com.eas.debugger.jmx.server.DebuggerMBean;
import java.io.IOException;
import java.util.concurrent.Future;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerEngine.Destructor;
import org.netbeans.api.debugger.DebuggerInfo;
import org.netbeans.api.debugger.DebuggerManager;

/**
 *
 * @author mg
 */
public class DebuggerUtils {

    public static void attachDebugger(DebuggerEnvironment env) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + env.host + ":" + String.valueOf(env.port) + "/jmxrmi");
        ObjectName mBeanDebuggerName = new ObjectName(DebuggerMBean.DEBUGGER_MBEAN_NAME);
        ObjectName mBeanBreakpointsName = new ObjectName(BreakpointsMBean.BREAKPOINTS_MBEAN_NAME);
        MBeanDebuggerListener listener = new MBeanDebuggerListener(env.project);

        JMXConnector jmxc = null;
        MBeanServerConnection jmxConnection = null;
        DebuggerMBean debugger = null;
        BreakpointsMBean breakpoints = null;
        // let's wait until program register it's mbeans.
        int ioCounter = 0;
        while (true) {
            try {
                jmxc = JMXConnectorFactory.connect(url, null);
                jmxConnection = jmxc.getMBeanServerConnection();
                jmxConnection.addNotificationListener(mBeanDebuggerName, listener, null, null);
                debugger = JMX.newMBeanProxy(jmxConnection, mBeanDebuggerName, DebuggerMBean.class);
                breakpoints = JMX.newMBeanProxy(jmxConnection, mBeanBreakpointsName, BreakpointsMBean.class);
                break;
            } catch (InstanceNotFoundException ex) {
                Thread.sleep(500);
            } catch (IOException ex) {
                if (ioCounter <= 16) {
                    ioCounter++;
                    Thread.sleep(500);
                } else {
                    throw ex;
                }
            }
        }
        env.mBreakpoints = breakpoints;
        env.mDebugger = debugger;
        env.mDebuggerListener = listener;
        DebuggerInfo di = DebuggerInfo.create(DebuggerConstants.DEBUGGER_SERVICERS_PATH, new Object[]{env, jmxConnection});
        DebuggerEngine[] dEngines = DebuggerManager.getDebuggerManager().startDebugging(di);
        if (env.runningProgram != null) {
            startProcessWaiting(env.runningProgram, dEngines);
        }
    }

    public static void startProcessWaiting(final Future<Integer> runningProgram, final DebuggerEngine[] dEngines) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    runningProgram.get();
                } catch (Exception ex) {
                    // no op
                }
                for (DebuggerEngine engine : dEngines) {
                    Destructor d = engine.new Destructor();
                    d.killEngine();
                }
            }
        });
        thread.start();
    }
}
