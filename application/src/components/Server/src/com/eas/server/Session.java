/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.SystemPlatypusPrincipal;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.server.scripts.ModulesJSFacade;
import com.eas.server.scripts.SyncJSObjectFacade;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 * A client session
 *
 * <p>
 * This object is created to represent a session with successfully authenticated
 * client. It is used to associate various resources such as tasks with a a
 * client. Whenever a session is <code>cleanup()</code>-ed, the resources are
 * deleted.</p> Method rollback of database client is also invoked.
 *
 * @author pk, mg refactoring
 */
public class Session implements HasPublished {

    protected Object published;
    //
    private final PlatypusServerCore serverCore;
    private final String sessionId;
    private String userContext;
    private PlatypusPrincipal principal;// re-login for the same session is allowed in EE servers
    private final long ctime;
    private final AtomicLong atime = new AtomicLong();
    private final Map<String, JSObject> modulesInstances = new HashMap<>();
    private int maxInactiveInterval = 3600000; // 1 hour
    private final JSObject jsModules = new ModulesJSFacade(this);

    /**
     * Creates a new session with given session id.
     *
     * @param aServerCore
     * @param aSessionId unique session id.
     * @param aPrincipal
     */
    public Session(PlatypusServerCore aServerCore, String aSessionId, PlatypusPrincipal aPrincipal) {
        sessionId = aSessionId;
        ctime = System.currentTimeMillis();
        atime.set(ctime);
        serverCore = aServerCore;
        setPrincipal(aPrincipal);
    }

    /**
     * Deletes all resources belonging to this session.
     */
    public synchronized void cleanup() {
        // server modules
        modulesInstances.clear();
        // data in client's transaction
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
     * <p>
     * The last access time is the last time accessed() was called. This
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
     * <p>
     * The last access time is the last time accessed() was called. This
     * mechanism is used to track down sessions which have been idle for a long
     * time, i.e. possible zombies.</p>
     *
     * <p>
     * Call this method once for each client request inside this session.</p>
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
     * <p>
     * The user name is stored only for informational purposes.
     *
     * @return user name.
     */
    public String getUser() {
        return principal.getName();
    }

    public PlatypusPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(PlatypusPrincipal aPrincipal) {
        if ((principal != null && aPrincipal != null && !principal.getName().equals(aPrincipal.getName())) || principal == null || aPrincipal == null) {
            userContext = null;
            String userName = "";
            if (serverCore != null && serverCore.getDatabasesClient() != null) {
                try {
                    if (aPrincipal != null) {
                        userName = aPrincipal.getName();
                    }
                    Map<String, String> userProps = DatabasesClient.getUserProperties(serverCore.getDatabasesClient(), userName, null, null);
                    userContext = userProps.get(ClientConstants.F_USR_CONTEXT);
                } catch (Exception ex) {
                    Logger.getLogger(SessionManager.class.getName()).log(Level.WARNING, "Could not get user {0} properties.", userName);
                }
            }
        }
        principal = aPrincipal;
    }

    /**
     * Returns server module by name.
     *
     * @param aName
     * @return
     */
    public synchronized JSObject getModule(String aName) {
        return modulesInstances.get(aName);
    }

    public synchronized boolean containsModule(String aName) {
        return modulesInstances.containsKey(aName);
    }

    public synchronized void registerModule(JSObject aModule) {
        JSObject c = (JSObject) aModule.getMember("constructor");
        String name = JSType.toString(c.getMember("name"));
        if (principal instanceof SystemPlatypusPrincipal) {
            modulesInstances.put(name, new SyncJSObjectFacade(aModule));
        } else {
            modulesInstances.put(name, aModule);
        }
    }

    public synchronized void unregisterModule(String aModuleName) {
        modulesInstances.remove(aModuleName);
    }

    public synchronized void unregisterModules() {
        modulesInstances.clear();
    }

    public synchronized Set<Map.Entry<String, JSObject>> getModulesEntries() {
        return Collections.unmodifiableSet(modulesInstances.entrySet());
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

    /**
     * @return the usrContext
     */
    public String getContext() {
        return userContext;
    }

    /**
     * @param aContext the usrContext to set
     */
    public void setContext(String aContext) {
        userContext = aContext;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Contains modules collection of this session.\n"
            + " */")
    public JSObject getModules() {
        return jsModules;
    }

    @Override
    public void setPublished(Object aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
