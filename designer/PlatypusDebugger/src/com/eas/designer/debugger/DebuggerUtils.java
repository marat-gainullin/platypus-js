/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.BreakpointsMBean;
import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.application.indexer.PlatypusPathRecognizer;
import java.io.IOException;
import java.util.concurrent.Future;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerEngine.Destructor;
import org.netbeans.api.debugger.DebuggerInfo;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mg
 */
public class DebuggerUtils {

    public static void attachDebugger(DebuggerEnvironment env, int aRetryCount) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + env.host + ":" + String.valueOf(env.port) + "/jmxrmi");
        ObjectName mBeanDebuggerName = new ObjectName(DebuggerMBean.DEBUGGER_MBEAN_NAME);
        ObjectName mBeanBreakpointsName = new ObjectName(BreakpointsMBean.BREAKPOINTS_MBEAN_NAME);
        MBeanDebuggerListener listener = new MBeanDebuggerListener(env.project);

        MBeanServerConnection jmxConnection = null;
        DebuggerMBean debugger = null;
        BreakpointsMBean breakpoints = null;
        // let's wait until program register it's mbeans.
        int ioCounter = 1;
        JMXConnector jmxc = null;
        while (true) {
            try {
                jmxc = JMXConnectorFactory.connect(url, null);
                jmxConnection = jmxc.getMBeanServerConnection();
                jmxConnection.addNotificationListener(mBeanDebuggerName, listener, null, null);
                debugger = JMX.newMBeanProxy(jmxConnection, mBeanDebuggerName, DebuggerMBean.class);
                breakpoints = JMX.newMBeanProxy(jmxConnection, mBeanBreakpointsName, BreakpointsMBean.class);
                break;
            } catch (InstanceNotFoundException | IOException ex) {
                if (aRetryCount > 0 && ex instanceof InstanceNotFoundException) {
                    Thread.sleep(250);
                }
                if (++ioCounter > aRetryCount) {
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
        } else {
            listener.processWaiter = startProcessWaiting(jmxc, dEngines);
        }
    }

    public static void startDebugging(DebuggerEnvironment env) throws Exception {
        // transfer breakpoints
        Breakpoint[] breaks = DebuggerManager.getDebuggerManager().getBreakpoints();
        for (Breakpoint breakPoint : breaks) {
            if (breakPoint instanceof PlatypusBreakpoint) {
                PlatypusBreakpoint pBreak = (PlatypusBreakpoint) breakPoint;
                pBreak.remoteAdd(env.mBreakpoints);
            }
        }
        env.mDebuggerListener.debuggingStarted = true;
        env.mDebuggerListener.running = true;
    }

    public static void killEngine(DebuggerEngine engine) throws Exception {
        DebuggerEnvironment env = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
        try {
            if (env != null && env.runningProgram == null) {// Debugger was attached to external program
                for (Breakpoint breakpoint : DebuggerManager.getDebuggerManager().getBreakpoints()) {
                    if (breakpoint instanceof PlatypusBreakpoint) {
                        PlatypusBreakpoint pbreak = (PlatypusBreakpoint) breakpoint;
                        pbreak.remoteRemove(env.mBreakpoints);
                    }
                }
                if (!env.mDebuggerListener.running) {
                    env.mDebugger.continueRun();
                }
            }
        } finally {
            Destructor d = engine.new Destructor();
            d.killEngine();
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
        thread.setDaemon(true);
        thread.start();
    }

    public static Thread startProcessWaiting(final JMXConnector jmxc, final DebuggerEngine[] dEngines) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        jmxc.getMBeanServerConnection();
                        Thread.sleep(250);
                    }
                } catch (IOException ex) {
                    for (DebuggerEngine engine : dEngines) {
                        Destructor d = engine.new Destructor();
                        d.killEngine();
                    }
                } catch (InterruptedException ex) {
                    // no op
                    ex = null;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    public static String getUrlAsRelativePath(FileObject sourceFile) {
        Project p = FileOwnerQuery.getOwner(sourceFile);
        FileObject rootFileObject = getSourcesRoot(p);
        assert FileUtil.isParentOf(rootFileObject, sourceFile) : String.format("Source file %s root is not found in project's source roots.", sourceFile.getPath());
        return FileUtil.getRelativePath(rootFileObject, sourceFile);
    }

    public static FileObject getFileObjectByUrl(Project project, String url) {
        FileObject rootFileObject = getSourcesRoot(project);
        return rootFileObject.getFileObject(url);
    }

    private static FileObject getSourcesRoot(Project project) {
        ClassPathProvider cpp = project.getLookup().lookup(ClassPathProvider.class);
        if (cpp != null) {
            FileObject[] roots = cpp.findClassPath(null, PlatypusPathRecognizer.SOURCE_CP).getRoots();
            if (roots.length != 1) {
                throw new IllegalStateException("Only one root supported for now.");
            }
            return roots[0];
        } else {
            throw new IllegalStateException("ClassPathProvider is not found in project's lookup.");
        }
    }
}
