/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 *
 * @author pk
 */
public class GarbageSessionsCollector implements Runnable
{
    public static final String SER_PREFS_PATH = "/com/eas/server";
    private final SessionManager sessionManager;
    private final PlatypusServerCore serverCore;
    private final long idleTimeout;
    private final long idleCheckInterval;

    public GarbageSessionsCollector(PlatypusServerCore aServerCore)
    {
        super();
        serverCore = aServerCore;
        sessionManager = serverCore.getSessionManager();
        idleTimeout = Preferences.systemRoot().node(SER_PREFS_PATH).getLong("idleSessionTimeout", 60) * 60 * 1000; //1 hour
        idleCheckInterval = Preferences.systemRoot().node(SER_PREFS_PATH).getLong("idleCheckInterval", 5) * 60 * 1000; //5 minutes
    }

    @Override
    public void run()
    {
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                Thread.sleep(idleCheckInterval);
                final Set<Entry<String, Session>> entries = new HashSet<>();
                synchronized (sessionManager)
                {
                    entries.addAll(sessionManager.entrySet());
                }
                for (final Entry<String, Session> e : entries)
                {
                    final Session session = (Session) e.getValue();
                    if (e.getKey() != null && (new Date().getTime() - session.getATime() > idleTimeout))
                    {
                        Logger.getLogger(GarbageSessionsCollector.class.getName()).info(String.format("Closing idle session %s with %s", session.getId(), session.getUser()));
                        sessionManager.remove(session.getId());
                        //TODO shutdown all the tasks that this session began, including its spawned threads.
                        serverCore.getDatabasesClient().removeChangeLog(session.getId());
                    }
                }
                sessionManager.getTemporaryPasswords().cleanup();
            }
        } catch (InterruptedException ex)
        {
            Logger.getLogger(GarbageSessionsCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
