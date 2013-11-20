/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.login.PrincipalHost;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptRunner;
import com.eas.script.ScriptUtils;
import com.eas.script.ScriptUtils.ScriptAction;
import java.util.HashSet;
import java.util.Set;
import org.mozilla.javascript.*;

/**
 *
 * @author pk, mg, ab
 */
public class ServerScriptRunner extends ScriptRunner {

    public static final String MODULES_SCRIPT_NAME = "Modules";
    private PlatypusServerCore serverCore;
    private Session creationSession;

    public ServerScriptRunner(PlatypusServerCore aServerCore, Session aCreationSession, String aModuleId, Scriptable aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, Object[] args) throws Exception {
        super(aModuleId, aServerCore.getDatabasesClient(), aScope, aPrincipalHost, aCompiledScriptDocumentsHost, args);
        serverCore = aServerCore;
        creationSession = aCreationSession;
    }

    public String getModuleId() {
        return super.getApplicationElementId();
    }

    public Session getCreationSession() {
        return creationSession;
    }

    public PlatypusServerCore getServerCore() {
        return serverCore;
    }

    public synchronized Object executeMethod(final String aMethodName, final Object[] aArguments) throws Exception {
        return ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                execute();
                final Object oFunction = get(aMethodName, ServerScriptRunner.this);
                if (oFunction instanceof SecureFunction) {
                    SecureFunction f = (SecureFunction) oFunction;
                    if (!(f.unwrap() instanceof FunctionObject)) {
                        if (aArguments != null) {
                            for (int i = 0; i < aArguments.length; i++) {
                                if (aArguments[i] != null) {
                                    aArguments[i] = ScriptUtils.javaToJS(aArguments[i], ServerScriptRunner.this);
                                }
                            }
                        }
                        return ScriptUtils.js2Java(f.call(cx, ServerScriptRunner.this, ServerScriptRunner.this, aArguments));
                    }
                }
                throw new IllegalArgumentException("Unknown method \"" + aMethodName + "()\"");
            }
        });
    }

    public synchronized Object getValue(final String aPropertyName) throws Exception {
        return ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                execute();
                Object prop = get(aPropertyName, ServerScriptRunner.this);
                if (prop instanceof Undefined || prop == Scriptable.NOT_FOUND) {
                    return Undefined.instance;
                } else {
                    return ScriptUtils.js2Java(prop);
                }
            }
        });
    }

    public synchronized void setValue(final String aPropertyName, final Object aValue) throws Exception {
        ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                execute();
                put(aPropertyName, ServerScriptRunner.this, ScriptUtils.javaToJS(aValue, ServerScriptRunner.this));
                return null;
            }
        });
    }

    @Override
    protected void definePropertiesAndMethods() {
        super.definePropertiesAndMethods();
        defineFunctionProperties(new String[]{
            "getCreationSession",
            "getServerCore"
        }, ServerScriptRunner.class, EMPTY);
    }

    public Set<String> getFunctionsNames() throws Exception {
        return ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Set<String> run(Context cx) throws Exception {
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
            }
        });
    }

    public void destroy() {
        // no op yet
    }
}
