/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.threetier.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A client session
 *
 * <p>This object is created to represent a session with successfully
 * authenticated client. It is used to associate various resources such as tasks
 * with a a client. Whenever a session is
 * <code>cleanup()</code>-ed, the resources are deleted.</p> Method rollback of
 * database client is also invoked.
 *
 * @author pk, mg refactoring
 */
public class Session implements PrincipalHost {

    private final PlatypusServerCore serverCore;
    private final String sessionId;
    private PlatypusPrincipal principal;// re-login for the same session is allowed in EE servers
    private final long ctime;
    private final AtomicLong atime = new AtomicLong();
    private final Map<String, ServerScriptRunner> activeModules = new HashMap<>();
    private final Map<Long, Response> pendingResponses = new HashMap<>();
    private int maxInactiveInterval = 3600000; // 1 hour
    /*
     // HttpSession
     private final Map<String, Object> values = new HashMap<>();
     private final Map<String, Object> attributes = new HashMap<>();
     * 
     */

    /**
     * Creates a new session with given session id.
     *
     * @param aSessionId unique session id.
     * @param aUser specifies username of the owner of this session.
     */
    public Session(PlatypusServerCore aServer, String aSessionId, PlatypusPrincipal aPrincipal) {
        sessionId = aSessionId;
        ctime = System.currentTimeMillis();
        atime.set(ctime);
        principal = aPrincipal;
        serverCore = aServer;
    }

    /**
     * Deletes all resources belonging to this session.
     */
    public synchronized void cleanup() throws IOException {
        // server modules
        activeModules.clear();
        // request's responses
        pendingResponses.clear();
        // data in client's transaction
        try {
            if (serverCore != null) { // this check is only for tests
                serverCore.getDatabasesClient().rollback();
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Returns the creation time of this session (server time).
     *
     * @return session creation time.
     */
    public long getCTime() {
        return ctime;
    }

    /**
     * Returns the last access time of this session (server time).
     *
     * <p> The last access time is the last time accessed() was called. This
     * mechanism is used to track down sessions which have been idle for a long
     * time, i.e. possible zombies.</p>
     *
     * @return last access time.
     */
    public long getATime() {
        return atime.get();
    }

    /**
     * Mark that this session was just accessed by its client, update last
     * access time.
     *
     * <p> The last access time is the last time accessed() was called. This
     * mechanism is used to track down sessions which have been idle for a long
     * time, i.e. possible zombies.</p>
     *
     * <p>Call this method once for each client request inside this session.</p>
     *
     * @return new last access time.
     */
    public long accessed() {
        atime.set(System.currentTimeMillis());
        return atime.get();
    }

    /**
     * Returns the user which initiated this session.
     *
     * <p>The user name is stored only for informational purposes.
     *
     * @return user name.
     */
    public String getUser() {
        return principal.getName();
    }

    @Override
    public PlatypusPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(PlatypusPrincipal aPrincipal) {
        principal = aPrincipal;
    }

    /**
     * Returns server module by name.
     *
     * @param aModuleName
     * @return
     */
    public ServerScriptRunner getModule(String aModuleName) {
        synchronized (activeModules) {
            return activeModules.get(aModuleName);
        }
    }

    public void registerModule(ServerScriptRunner aModule) {
        synchronized (activeModules) {
            activeModules.put(aModule.getApplicationElementId(), aModule);
        }
    }

    public void unregisterModule(String aModuleName) {
        synchronized (activeModules) {
            activeModules.remove(aModuleName);
        }
    }

    public void unregisterModules() {
        synchronized (activeModules) {
            activeModules.clear();
        }
    }

    public void addPendingResponse(Response response) {
        synchronized (pendingResponses) {
            pendingResponses.put(response.getRequestID(), response);
        }
    }

    public void removePendingResponse(Response response) {
        synchronized (pendingResponses) {
            final Response rsp = pendingResponses.get(response.getRequestID());
            if (rsp == response) {
                pendingResponses.remove(response.getRequestID());
            }
        }
    }

    public Response getPendingResponse(Long requestID) {
        synchronized (pendingResponses) {
            return pendingResponses.get(requestID);
        }
    }

    public void processPendingResponses(ResponseProcessor proc) throws Exception {
        synchronized (pendingResponses) {
            for (Entry<Long, Response> e : pendingResponses.entrySet()) {
                proc.processResponse(e.getValue());
            }
        }
    }

    /**
     * Returns this session's id.
     *
     * @return session id.
     */
    public String getId() {
        return sessionId;
    }

    public void setMaxInactiveInterval(int aInterval) {
        maxInactiveInterval = aInterval;
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public boolean isNew() {
        return false;
    }
}
