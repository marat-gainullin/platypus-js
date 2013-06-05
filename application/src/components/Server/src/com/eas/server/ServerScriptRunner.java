/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.login.PrincipalHost;
import com.eas.client.reports.ExcelReport;
import com.eas.client.reports.ReportDocument;
import com.eas.client.reports.ReportRunner;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptResolverHost;
import com.eas.client.scripts.ScriptRunner;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import java.rmi.AccessException;
import java.util.HashSet;
import java.util.Set;
import org.mozilla.javascript.*;

/**
 *
 * @author pk, mg
 */
public class ServerScriptRunner extends ScriptRunner {

    public static final String MODULES_SCRIPT_NAME = "Modules";
    public static final String SCRIPT_ID_PARAM_NAME = "script-id";
    private PlatypusServerCore serverCore;
    private Session creationSession;
    protected ModuleConfig config;

    public ServerScriptRunner(PlatypusServerCore aServerCore, Session aCreationSession, ModuleConfig aConfig, ScriptableObject aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, ScriptResolverHost aScriptResolverHost) throws Exception {
        super(aConfig.getModuleId(), aServerCore.getDatabasesClient(), aScope, aPrincipalHost, aCompiledScriptDocumentsHost, aScriptResolverHost);
        serverCore = aServerCore;
        creationSession = aCreationSession;
        config = aConfig;
        defineProperty(MODULES_SCRIPT_NAME, serverCore.getScriptsCache(), ScriptableObject.READONLY);
    }

    public String getModuleId() {
        return config.getModuleId();
    }

    public Session getCreationSession() {
        return creationSession;
    }

    public PlatypusServerCore getServerCore() {
        return serverCore;
    }

    public synchronized Object executeMethod(String methodName, Object[] arguments) throws Exception {
        Context context = ScriptUtils.enterContext();
        try {
            execute();
            final Object oFunction = get(methodName, this);
            if (oFunction instanceof SecureFunction) {
                SecureFunction f = (SecureFunction) oFunction;
                if (!(f.unwrap() instanceof FunctionObject)) {
                    if (arguments != null) {
                        for (int i = 0; i < arguments.length; i++) {
                            if (arguments[i] != null) {
                                arguments[i] = ScriptUtils.javaToJS(arguments[i], this);
                            }
                        }
                    }
                    return ScriptUtils.js2Java(f.call(context, this, this, arguments));
                }
            }
            throw new IllegalArgumentException("Unknown method \"" + methodName + "()\"");
        } finally {
            Context.exit();
        }
    }

    public synchronized Object getValue(String propertyName) throws Exception {
        ScriptUtils.enterContext();
        try {
            execute();
            Object prop = get(propertyName, this);
            if (prop instanceof Undefined || prop == Scriptable.NOT_FOUND) {
                return Undefined.instance;
            } else {
                return ScriptUtils.js2Java(prop);
            }
        } finally {
            Context.exit();
        }
    }

    public synchronized void setValue(String propertyName, Object argument) throws Exception {
        ScriptUtils.enterContext();
        try {
            execute();
            put(propertyName, this, ScriptUtils.javaToJS(argument, this));
        } finally {
            Context.exit();
        }
    }
    
    public synchronized byte[] executeReport() throws Exception {
        throw new AccessException("Module is not a \"report\".");
    }

    @Override
    protected void definePropertiesAndMethods() {
        super.definePropertiesAndMethods();
        defineFunctionProperties(new String[]{
                    "getCreationSession",
                    "getServerCore"
                }, ServerScriptRunner.class, EMPTY);
    }

    public Set<String> getFunctionsNames() {
        ScriptUtils.enterContext();
        try {
            Set<String> names = new HashSet<>();
            for (Object key : getIds()) {
                if (key instanceof String && get(key) instanceof SecureFunction) {
                    SecureFunction f = (SecureFunction) get(key);
                    Function uf = f.unwrap();
                    if (!(uf instanceof FunctionObject)) {
                        names.add((String) key);
                    }
                }
            }
            return names;
        } finally {
            Context.exit();
        }
    }

    public boolean isReport() {
        return false;
    }

    public ModuleConfig getModuleConfig() {
        return config;
    }

    public void destroy() {
        // no op yet
    }
}
