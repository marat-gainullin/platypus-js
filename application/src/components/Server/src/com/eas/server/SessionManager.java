/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.SystemPlatypusPrincipal;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages active sessions.
 *
 * <p>
 * This class is responsible for tracking down active session, their creation
 * whenever needed, and removing.</p>
 *
 * @author pk, mg refactoring
 */
public class SessionManager {

    private final PlatypusServerCore serverCore;
    private final Map<String, Session> sessions = new HashMap<>();
    protected ThreadLocal<Session> currentSession = new ThreadLocal<>();

    /**
     * Creates a new session manager.
     */
    public SessionManager(PlatypusServerCore aServerCore) {
        super();
        serverCore = aServerCore;
    }

    /**
     * Creates a new session object for the specified user.
     *
     * <p>
     * The session instance returned is already registered inside this
     * manager.</p>
     *
     * <p>
     * It is assumed that by the time this method is called, the user already
     * authenticated successfully.</p>
     *
     * @param aPrincipal who initiated a session.
     * @param sessionId session id; use IDGenerator to generate.
     * @return a new Session instance.
     */
    public synchronized Session createSession(PlatypusPrincipal aPrincipal, String sessionId) {
        assert sessionId != null;
        assert !sessions.containsKey(sessionId);
        Session result = new Session(serverCore, sessionId, aPrincipal);
        sessions.put(sessionId, result);
        return result;
    }

    public synchronized Session getOrCreateSession(PlatypusPrincipal aPrincipal, String sessionId) {
        assert sessionId != null;
        if (!sessions.containsKey(sessionId)) {
            return createSession(aPrincipal, sessionId);
        } else {
            return sessions.get(sessionId);
        }
    }

    public synchronized Session getSystemSession() {
        Session result = sessions.get(null);
        if (result == null) {
            result = new Session(serverCore, null, new SystemPlatypusPrincipal());
            sessions.put(null, result);
        }
        return result;
    }

    /**
     * Returns the session with given id.
     *
     * @param sessionId the session id
     * @return session instance, or null if no such session.
     */
    public synchronized Session get(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Removes specified session from manager.
     *
     * <p>
     * This method calls the <code>cleanup()</code> method of the session, so
     * nothing is needed else to close the session.</p>
     *
     * @param sessionId the session to remove.
     * @return instance removed, or null if no such session found.
     */
    public synchronized Session remove(String sessionId) {
        Session removed = sessions.remove(sessionId);
        if (removed != null) {
            removed.cleanup();
        }
        return removed;
    }

    /**
     * Returns a set of active sessions.
     *
     * <p>
     * However this method is synchronized, caller should always use the set
     * returned in a section synchronized with this class instance to protect it
     * from modifications made by other threads.</p>
     *
     * @return set of active sessions.
     */
    public synchronized Set<Entry<String, Session>> entrySet() {
        return sessions.entrySet();
    }

    public Session getCurrentSession() {
        return currentSession.get();
    }

    public void setCurrentSession(Session aSession) {
        if (aSession == null) {
            currentSession.remove();
        } else {
            currentSession.set(aSession);
        }
    }
}
