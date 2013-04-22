/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.login.MD5Generator;
import com.eas.client.login.PlatypusPrincipal;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages active sessions.
 *
 * <p>This class is responsible for tracking down active session, their creation
 * whenever needed, and removing.</p>
 *
 * @author pk, mg refactoring
 */
public class SessionManager {

    private final PlatypusServerCore serverCore;
    private final Map<String, Session> sessions = new HashMap<>();
    protected ThreadLocal<Session> currentSession = new ThreadLocal<>();
    private final TemporaryPasswords temporaryPasswords = new TemporaryPasswords();

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
     * <p>The session instance returned is already registered inside this
     * manager.</p>
     *
     * <p>It is assumed that by the time this method is called, the user already
     * authenticated successfully.</p>
     *
     * @param user user name who initiated a session.
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
        Session result = null;
        if (!sessions.containsKey(sessionId)) {
            result = createSession(aPrincipal, sessionId);
        } else {
            result = sessions.get(sessionId);
        }
        return result;
    }

    public synchronized Session getSystemSession() {
        Session result = sessions.get(null);
        if (result == null) {
            result = new Session(serverCore, null, null);
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
     * <p> This method calls the
     * <code>cleanup()</code> method of the session, so nothing is needed else
     * to close the session.</p>
     *
     * @param sessionId the session to remove.
     * @return instance removed, or null if no such session found.
     */
    public synchronized Session remove(String sessionId) {
        Session removed = sessions.remove(sessionId);
        if (removed != null) {
            try {
                removed.cleanup();
            } catch (IOException ex) {
                Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return removed;
    }

    /**
     * Returns a set of active sessions.
     *
     * <p>However this method is synchronized, caller should always use the set
     * returned in a section synchronized with this class instance to protect it
     * from modifications made by other threads.</p>
     *
     * @return set of active sessions.
     */
    public synchronized Set<Entry<String, Session>> entrySet() {
        return sessions.entrySet();
    }

    public TemporaryPasswords getTemporaryPasswords() {
        return temporaryPasswords;
    }

    public static class TemporaryPasswords {

        public static String generatePassword() throws Exception {
            Long aId = IDGenerator.genID();
            String aStr = MD5Generator.generate(aId.toString());
            return aStr.substring(0, aStr.length() / 2);
        }
        private Map<String, TemporaryPassword> tempPasswords = new HashMap<>();

        public TemporaryPasswords() {
            super();
        }

        /**
         * Региструет временный пароль в списке временных паролей
         *
         * @param aUserName - имя пользователя
         * @param aPassword - MD5-хэш пароля
         * @return Вернут истину если удалось зарегистрировать указанный пароль
         */
        public synchronized boolean registerTempPassword(String aUserName, String aPassword) {
            cleanup();
            if (aUserName != null && !aUserName.isEmpty() && aPassword != null && !aPassword.isEmpty()) {
                TemporaryPassword tmpPassword;
                try {
                    tmpPassword = new TemporaryPassword(aPassword);
                } catch (Exception ex) {
                    Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    return false;
                }
                tempPasswords.put(aUserName, tmpPassword);
                return true;
            }
            return false;
        }

        /**
         * Проверяется правильность и актуальность временного пароля
         *
         * @param aUserName имя пользователя, не может быть null
         * @param aPassword мд5-хэш пароля, не может быть null
         * @return Возвращает истину если пароль правильный и актуальный
         */
        public synchronized boolean isUserPasswordCorrect(String aUserName, String aPassword) {
            cleanup();
            if (aPassword != null && aUserName != null) {
                TemporaryPassword password = tempPasswords.get(aUserName);
                if (password != null) {
                    if (aPassword.equals(password.getPassword())) {
                        return true;
                    }
                }
            }
            return false;
        }

        public synchronized void cleanup() {
            for (String key : tempPasswords.keySet()) {
                if (!tempPasswords.get(key).isActual()) {
                    tempPasswords.remove(key);
                }
            }
        }

        public static class TemporaryPassword {

            private Date startDate;
            private String password;
            private static final int actualTime = 7200000;// 2 hours

            public TemporaryPassword(String aPassword) throws Exception {
                super();
                this.password = MD5Generator.generate(aPassword);
                this.startDate = new Date();
            }

            public Date getStartDate() {
                return startDate;
            }

            public String getPassword() {
                return password;
            }

            public boolean isActual() {
                Date now = new Date();
                return now.getTime() < (startDate.getTime() + actualTime);
            }
        }
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
