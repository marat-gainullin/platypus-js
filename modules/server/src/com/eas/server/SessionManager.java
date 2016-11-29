/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.login.SystemPlatypusPrincipal;
import com.eas.script.Scripts;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;

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

    public static class Singleton {

        public static final SessionManager instance = init();

        private static SessionManager init() {
            try {
                return new SessionManager();
            } catch (ScriptException ex) {
                Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, "Unable to establish script engines", ex);
                return null;
            }
        }
    }

    protected final Map<String, Session> sessions = new ConcurrentHashMap<>();
    protected final Session systemSession;

    /**
     * Creates a new session manager.
     *
     * @throws javax.script.ScriptException
     */
    public SessionManager() throws ScriptException {
        super();
        Session created = new Session(null);
        Scripts.Space space = Scripts.createSpace();
        created.setSpace(space);
        created.setPrincipal(new SystemPlatypusPrincipal());
        systemSession = created;
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
     * @param sessionId session id; use IDGenerator to generate.
     * @return a new Session instance.
     * @throws javax.script.ScriptException
     */
    public Session create(String sessionId) throws ScriptException {
        assert sessionId != null;
        Session created = new Session(sessionId);
        Scripts.Space space = Scripts.createSpace();
        created.setSpace(space);
        sessions.put(sessionId, created);
        return created;
    }

    public Session getSystemSession() {
        return systemSession;
    }

    /**
     * Returns the session with given id.
     *
     * @param sessionId the session id
     * @return session instance, or null if no such session.
     */
    public Session get(String sessionId) {
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
    public Session remove(String sessionId) {
        Session removed = sessions.remove(sessionId);
        if (removed != null) {
            removed.cleanup();
        }
        return removed;
    }
}
