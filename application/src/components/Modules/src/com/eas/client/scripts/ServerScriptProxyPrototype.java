/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.AppClient;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;

/**
 * ServerScriptProxy integration with Rhino.
 * @author mg
 */
public class ServerScriptProxyPrototype extends IdScriptableObject {

    private static final String ID_CONSTRUCTOR = "constructor";
    private static final String ID_TOSOURCE = "toSource";
    private static final String ID_TOSTRING = "toString";
    private static final String ID_TOLOCALESTRING = "toLocaleString";
    private static final String SERVER_MODULE_TAG = "ServerModule";
    private static final String ONLY_CONSTRUCTOR_MSG = "Can't call %s(...). Only new %s(...) is allowed.";
    private static final String CONSTRUCTOR_PARAMETER_MISSING = "For new %s(...) constructor, module name/id parameter is required.";

    public static void init(Scriptable scope, boolean sealed) {
        ServerScriptProxyPrototype obj = new ServerScriptProxyPrototype();
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
        if(sealed)
            obj.sealObject();
    }

    @Override
    public String getClassName() {
        return SERVER_MODULE_TAG;
    }

    public ServerScriptProxyPrototype() {
        super();
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case Id_constructor:
                arity = 1;
                s = ID_CONSTRUCTOR;
                break;
            case Id_toString:
                arity = 1;
                s = ID_TOSTRING;
                break;
            case Id_toLocaleString:
                arity = 1;
                s = ID_TOLOCALESTRING;
                break;
            case Id_toSource:
                arity = 0;
                s = ID_TOSOURCE;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(SERVER_MODULE_TAG, id, s, arity);
    }

    @Override
    protected int findPrototypeId(String s) {
        switch (s) {
            case ID_CONSTRUCTOR:
                return Id_constructor;
            case ID_TOSTRING:
                return Id_toString;
            case ID_TOLOCALESTRING:
                return Id_toLocaleString;
            case ID_TOSOURCE:
                return Id_toSource;
            default:
                return 0;
        }
    }
    private static final int Id_constructor = 1,
            Id_toString = 2,
            Id_toLocaleString = 3,
            Id_toSource = 4,
            MAX_PROTOTYPE_ID = 4;

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
            Scriptable thisObj, Object[] args) {
        if (!f.hasTag(SERVER_MODULE_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == Id_constructor) {
            if (args.length >= 1) {
                if (thisObj == null) {
                    if (args.length >= 1 && args[0] != null) {
                        String scriptId = args[0].toString();
                        if (thisObj == null) {
                            try {
                                final ScriptRunner clientWrapper = ScriptRunnerPrototype.lookupScriptRunner(scope);
                                assert clientWrapper != null : ScriptRunnerPrototype.BAD_SCRIPT_SCOPE_MSG;
                                if (clientWrapper.getClient() instanceof AppClient) {
                                    ServerScriptProxy ssproxy = new ServerScriptProxy(scriptId, (AppClient) clientWrapper.getClient(), ScriptUtils.getScope());
                                    ssproxy.setPrototype(this);
                                    return ssproxy;
                                } else {
                                    ScriptRunner srunner = new ScriptRunner(scriptId, clientWrapper.getClient(), ScriptRunner.initializePlatypusStandardLibScope(), clientWrapper.getPrincipalHost(), clientWrapper.getCompiledScriptDocumentsHost(), (args.length > 1 && args[1] instanceof Object[]) ? (Object[]) args[1] : null);
                                    srunner.setPrototype(this);
                                    srunner.execute();
                                    return srunner;
                                }
                            } catch (Exception ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        } else {
                            throw new IllegalArgumentException(String.format(ONLY_CONSTRUCTOR_MSG, SERVER_MODULE_TAG, SERVER_MODULE_TAG));
                        }
                    } else {
                        throw new IllegalArgumentException(String.format(CONSTRUCTOR_PARAMETER_MISSING, SERVER_MODULE_TAG, SERVER_MODULE_TAG));
                    }
                } else {
                    throw incompatibleCallError(f);
                }
            } else {
                throw incompatibleCallError(f);
            }
        }

        if (thisObj instanceof ServerScriptProxyPrototype) {
            switch (id) {

                case Id_toString:
                case Id_toLocaleString: {
                    // toLocaleString is just an alias for toString for now
                    return "[platypus server module proxy]";
                }
                default:
                    throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        // The rest of Module.prototype methods require thisObj to be ServerScriptProxy or ScriptRunner

        if (!(thisObj instanceof ServerScriptProxy) && !(thisObj instanceof ScriptRunner)) {
            throw incompatibleCallError(f);
        }
        switch (id) {

            case Id_toString:
            case Id_toLocaleString: {
                // toLocaleString is just an alias for toString for now
                return String.format("%s (Platypus server module proxy)", (thisObj instanceof ServerScriptProxy) ? ((ServerScriptProxy) thisObj).getScriptName() : String.valueOf(((ScriptRunner) thisObj).getApplicationElementId()));
            }

            case Id_toSource:
                return "function " + SERVER_MODULE_TAG + "(){\n/*compiled code*/\n}";

            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    /*@Override
     public Object get(String name, Scriptable start) {
     return super.get(name, start);
     }

     @Override
     public void put(String name, Scriptable start, Object value) {
     super.put(name, start, value);
     }*/
}
