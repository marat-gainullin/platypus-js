/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da;

import com.eas.opc.OPCCommon;
import com.eas.opc.da.dcom.IOPCGroupStateMgt;
import com.eas.opc.da.dcom.IOPCServer;
import com.eas.opc.da.dcom.IOPCServer.AddGroupResult;
import com.eas.opc.da.dcom.OPCSERVERSTATUS;
import com.eas.opc.da.dcom.OPCShutdownListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;

/**
 *
 * @author pk
 */
public class OPCDAServerConnection extends OPCCommon
{
    protected IOPCServer opcServer;
    private final List<Group> knownGroups = new ArrayList<>();
    private final Map<Integer, Group> knownGroupsByServerHandle = new HashMap<>();
    private final Map<String, Group> knownGroupsByName = new HashMap<>();
    private final List<OPCShutdownListener> shutdownListeners = new ArrayList<>();

    static {
        try
        {
            JISystem.setInBuiltLogHandler(true);
        } catch (SecurityException | IOException ex)
        {
            Logger.getLogger(OPCDAServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        LogManager.getLogManager().getLogger("org.jinterop").setLevel(Level.WARNING);
    }

    @Override
    public void connect(String progID, String domain, String hostname, String username, String password) throws JIException, UnknownHostException
    {
        super.connect(progID, domain, hostname, username, password);
        opcServer = new IOPCServer(comServer);
        opcServer.addShutdownListener(new InternalShutDownListener());
    }

    public void addShutdownListener(OPCShutdownListener l) throws JIException
    {
        synchronized (shutdownListeners)
        {
            shutdownListeners.add(l);
            opcServer.addShutdownListener(l);
        }
    }

    public void removeShutdownListener(OPCShutdownListener l) throws JIException
    {
        synchronized (shutdownListeners)
        {
            shutdownListeners.remove(l);
            opcServer.removeShutdownListener(l);
        }
    }

    @Override
    public void disconnect()
    {
        cleanup();
        opcServer = null;
        super.disconnect();
    }

    public String getErrorString(long errorCode, int localeID) throws JIException
    {
        return opcServer.getErrorString(errorCode, localeID);
    }

    public ServerStatus getStatus() throws JIException
    {
        return new ServerStatus(opcServer.getStatus());
    }

    public Group addGroup(String name, boolean isActive, int requestedUpdateRate, int clientHandle, Integer timeBias, Float percentDeadBand, int localeID) throws JIException
    {
        final AddGroupResult addResult = opcServer.addGroup(name, isActive, requestedUpdateRate, clientHandle, timeBias, percentDeadBand, localeID, IOPCGroupStateMgt.IID_IOPCGroupStateMgt);
        final IOPCGroupStateMgt groupStateMgt = new IOPCGroupStateMgt(addResult.getIface());
        final int revisedUpdateRate = addResult.getRevisedUpdateRate();
        final int serverHandle = addResult.getServerGroup();
        final Group group = new Group(name, isActive, requestedUpdateRate, revisedUpdateRate, clientHandle, serverHandle, timeBias, percentDeadBand, localeID, groupStateMgt);
        synchronized (this)
        {
            knownGroups.add(group);
            knownGroupsByName.put(name, group);
            knownGroupsByServerHandle.put(serverHandle, group);
        }
        return group;
    }

    public void removeGroup(Group group, boolean force) throws JIException
    {
        boolean groupKnown = false;
        synchronized (this)
        {
            if (knownGroupsByServerHandle.get(group.getServerHandle()) == group || knownGroupsByName.get(group.getName()) == group
                    || knownGroups.contains(group))
            {
                knownGroups.remove(group);
                knownGroupsByName.remove(group.getName());
                knownGroupsByServerHandle.remove(group.getServerHandle());
                groupKnown = true;
            }
        }
        if (groupKnown)
            opcServer.removeGroup(group.getServerHandle(), force);
        else
            throw new NoSuchElementException("Unknown group.");
    }

    public Group addGroup(String name, boolean isActive, int requestedUpdateRate, int clientHandle) throws JIException
    {
        return addGroup(name, isActive, requestedUpdateRate, clientHandle, null, null, getLocaleID());
    }

    public Group[] getKnownGroups()
    {
        synchronized (this)
        {
            Group[] result = new Group[knownGroups.size()];
            result = knownGroups.toArray(result);
            return result;
        }
    }

    public Group getGroupByName(String name)
    {
        synchronized (this)
        {
            return knownGroupsByName.get(name);
        }
    }

    protected void cleanup()
    {
        synchronized (this)
        {
            for (Group group : knownGroups)
            {
                try
                {
                    group.cleanup();
                } catch (JIException ex)
                {
                    Logger.getLogger(OPCDAServerConnection.class.getName()).log(Level.SEVERE, "Failed to cleanup group " + group, ex);
                }
                try
                {
                    opcServer.removeGroup(group.getServerHandle(), false);
                } catch (JIException ex)
                {
                    Logger.getLogger(OPCDAServerConnection.class.getName()).log(Level.SEVERE, "Failed to remove group " + group, ex);
                }
            }
            knownGroups.clear();
            knownGroupsByName.clear();
            knownGroupsByServerHandle.clear();
        }
        synchronized (shutdownListeners)
        {
            for (OPCShutdownListener l : shutdownListeners)
            {
                try
                {
                    opcServer.removeShutdownListener(l);
                } catch (JIException ex)
                {
                    Logger.getLogger(OPCDAServerConnection.class.getName()).log(Level.SEVERE, "Failed to remove shutdown listener " + l, ex);
                }
            }
            shutdownListeners.clear();
        }
    }

    /**
     * OPCSERVERSTATE constants.
     */
    public static enum ServerState
    {
        RUNNING(1), FAILED(2), NOCONFIG(3), SUSPENDED(4), TEST(5);
        private final int id;

        private ServerState(int id)
        {
            this.id = id;
        }

        public static ServerState getState(int id)
        {
            switch (id)
            {
                case 1:
                    return RUNNING;
                case 2:
                    return FAILED;
                case 3:
                    return NOCONFIG;
                case 4:
                    return SUSPENDED;
                case 5:
                    return TEST;
                default:
                    throw new IllegalArgumentException(String.format("Unknown OPCSERVERSTATE constant %d", id)); //NOI18N
            }
        }

        public int getId()
        {
            return id;
        }
    }

    public static final class ServerStatus
    {
        private final int bandWidth;
        private final short buildNumber;
        private final Date currentTime;
        private final int groupCount;
        private final Date lastUpdateTime;
        private final short majorVersion;
        private final short minorVersion;
        private final short reserved;
        private final ServerState serverState;
        private final Date startTime;
        private final String vendorInfo;

        private ServerStatus(OPCSERVERSTATUS src)
        {
            bandWidth = src.getBandWidth();
            buildNumber = src.getBuildNumber();
            currentTime = src.getCurrentTime().getTime();
            groupCount = src.getGroupCount();
            lastUpdateTime = src.getLastUpdateTime().getTime();
            majorVersion = src.getMajorVersion();
            minorVersion = src.getMinorVersion();
            reserved = src.getReserved();
            serverState = ServerState.getState(src.getServerState());
            startTime = src.getStartTime().getTime();
            vendorInfo = src.getVendorInfo();
        }

        public int getBandWidth()
        {
            return bandWidth;
        }

        public short getBuildNumber()
        {
            return buildNumber;
        }

        public Date getCurrentTime()
        {
            return currentTime;
        }

        public int getGroupCount()
        {
            return groupCount;
        }

        public Date getLastUpdateTime()
        {
            return lastUpdateTime;
        }

        public short getMajorVersion()
        {
            return majorVersion;
        }

        public short getMinorVersion()
        {
            return minorVersion;
        }

        public short getReserved()
        {
            return reserved;
        }

        public ServerState getServerState()
        {
            return serverState;
        }

        public Date getStartTime()
        {
            return startTime;
        }

        public String getVendorInfo()
        {
            return vendorInfo;
        }
    }

    private class InternalShutDownListener implements OPCShutdownListener
    {
        public void shutdownRequested(String reason)
        {
            OPCDAServerConnection.this.cleanup();
            try
            {
                OPCDAServerConnection.this.opcServer.removeShutdownListener(this);
            } catch (JIException ex)
            {
                Logger.getLogger(OPCDAServerConnection.class.getName()).log(Level.SEVERE, "Failed to remove internal shutdown listener.", ex);
            }
        }
    }
}
