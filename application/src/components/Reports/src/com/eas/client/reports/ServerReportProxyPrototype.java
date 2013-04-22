/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.AppClient;
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.scripts.ScriptRunnerPrototype;
import com.eas.client.scripts.ServerScriptProxy;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author AB
 */
public class ServerReportProxyPrototype extends IdScriptableObject {

    private static final String ID_CONSTRUCTOR = "constructor";
    private static final String ID_TOSOURCE = "toSource";
    private static final String ID_TOSTRING = "toString";
    private static final String ID_TOLOCALESTRING = "toLocaleString";
    private static final String SERVER_REPORT_TAG = "ServerReport";
    private static final String ONLY_CONSTRUCTOR_MSG = "Can't call %s(...). Only new %s(...) is allowed.";
    private static final String CONSTRUCTOR_PARAMETER_MISSING = "For new %s(...) constructor, module name/id parameter is required.";

    public static void init(Scriptable scope, boolean sealed) {
        com.eas.client.reports.ServerReportProxyPrototype obj = new com.eas.client.reports.ServerReportProxyPrototype();
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
    }

    @Override
    public String getClassName() {
        return SERVER_REPORT_TAG;
    }

    public ServerReportProxyPrototype() {
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
        initPrototypeMethod(SERVER_REPORT_TAG, id, s, arity);
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
        if (!f.hasTag(SERVER_REPORT_TAG)) {
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
                                    ServerReportProxy srproxy = new ServerReportProxy(scriptId, (AppClient) clientWrapper.getClient(), ScriptUtils.getScope());
                                    srproxy.setPrototype(this);
                                    return srproxy;
                                } else {
                                    ReportRunner rr = new ReportRunner(scriptId, clientWrapper.getClient(), ScriptUtils.getScope(), clientWrapper.getPrincipalHost(), clientWrapper.getCompiledScriptDocumentsHost(), clientWrapper.getScriptResolverHost());
                                    rr.setPrototype(this);
                                    return rr;
                                }
                            } catch (Exception ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        } else {
                            throw new IllegalArgumentException(String.format(ONLY_CONSTRUCTOR_MSG, SERVER_REPORT_TAG, SERVER_REPORT_TAG));
                        }
                    } else {
                        throw new IllegalArgumentException(String.format(CONSTRUCTOR_PARAMETER_MISSING, SERVER_REPORT_TAG, SERVER_REPORT_TAG));
                    }
                } else {
                    throw incompatibleCallError(f);
                }
            } else {
                throw incompatibleCallError(f);
            }
        }

        // The rest of Module.prototype methods require thisObj to be ServerReportProxy or ReportRunner

        if (!(thisObj instanceof ServerReportProxy) && !(thisObj instanceof ReportRunner)) {
            throw incompatibleCallError(f);
        }
        switch (id) {

            case Id_toString:
            case Id_toLocaleString: {
                // toLocaleString is just an alias for toString for now
                return String.format("Platypus server report proxy ( %s )", (thisObj instanceof ServerReportProxy) ? ((ServerScriptProxy) thisObj).getScriptName() : String.valueOf(((ReportRunner) thisObj).getApplicationElementId()));
            }

            case Id_toSource:
                return "function " + SERVER_REPORT_TAG + "(){\n/*compiled code*/\n}";

            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }
}

