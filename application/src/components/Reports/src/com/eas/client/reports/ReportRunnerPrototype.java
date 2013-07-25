/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.scripts.ScriptRunner;
import com.eas.client.scripts.ScriptRunnerPrototype;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class ReportRunnerPrototype extends IdScriptableObject {

    protected static final int Id_constructor = 1,
            Id_toString = 2,
            Id_toLocaleString = 3,
            Id_toSource = 4,
            MAX_PROTOTYPE_ID = 4;
    private static final String ID_CONSTRUCTOR = "constructor";
    private static final String ID_TOSTRING = "toString";
    private static final String ID_TOLOCALESTRING = "toLocaleString";
    private static final String ID_TOSOURCE = "toSource";
    private static final String ONLY_CONSTRUCTOR_MSG = "Can't call %s(...). Only new %s(...) is allowed.";
    private static final String CONSTRUCTOR_PARAMETER_MISSING = "For new %s(...) constructor, report name/id parameter is required.";
    protected static ReportRunnerPrototype reportPrototype;

    protected static ReportRunnerPrototype getInstance() {
        if (reportPrototype == null) {
            reportPrototype = new ReportRunnerPrototype();
        }
        return reportPrototype;
    }

    public static void init(Scriptable scope, boolean sealed) {
        init(scope, sealed, getInstance());
        reportPrototype.setPrototype(ScriptRunnerPrototype.getInstance());
    }

    public static void init(Scriptable scope, boolean sealed, ReportRunnerPrototype obj) {
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
        if (obj != getInstance()) {
            obj.setPrototype(getInstance());
        }
        if (sealed) {
            obj.sealObject();
        }
    }

    @Override
    public String getClassName() {
        return "Report";
    }

    public ReportRunnerPrototype() {
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
        initPrototypeMethod(getClassName(), id, s, arity);
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

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
            Scriptable thisObj, Object[] args) {
        if (!f.hasTag(getClassName())) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == Id_constructor) {
            if (args.length >= 1) {
                if (thisObj == null) {
                    if (args[0] != null) {
                        if (thisObj == null) {
                            // Здесь можно вставить разрешение имен модулей
                            String scriptId = args[0].toString();
                            try {
                                ScriptRunner clientWrapper = ScriptRunnerPrototype.lookupScriptRunner(scope);
                                assert clientWrapper != null : ScriptRunnerPrototype.BAD_SCRIPT_SCOPE_MSG;
                                ReportRunner rr = new ReportRunner(scriptId, clientWrapper.getClient(), ScriptUtils.getScope(), clientWrapper.getPrincipalHost(), clientWrapper.getCompiledScriptDocumentsHost(), (args.length > 1 && args[1] instanceof Object[]) ? (Object[]) args[1] : null);
                                rr.setPrototype(this);
                                return rr;
                            } catch (Exception ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        } else {
                            throw new IllegalArgumentException(String.format(ONLY_CONSTRUCTOR_MSG, getClassName(), getClassName()));
                        }
                    } else {
                        throw new IllegalArgumentException(String.format(CONSTRUCTOR_PARAMETER_MISSING, getClassName(), getClassName()));
                    }
                } else {
                    throw incompatibleCallError(f);
                }
            } else {
                throw incompatibleCallError(f);
            }
        }

        if (thisObj instanceof ReportRunnerPrototype) {
            switch (id) {

                case Id_toString:
                case Id_toLocaleString: {
                    // toLocaleString is just an alias for toString for now
                    return "[platypus report]";
                }

                default:
                    throw new IllegalArgumentException(String.valueOf(id));
            }
        }

        // The rest of Module.prototype methods require thisObj to be ScriptRunner

        if (!(thisObj instanceof ReportRunner)) {
            throw incompatibleCallError(f);
        }
        ReportRunner thisReportRunner = (ReportRunner) thisObj;
        switch (id) {

            case Id_toString:
            case Id_toLocaleString: {
                // toLocaleString is just an alias for toString for now
                return String.format("%s (Platypus report)", thisReportRunner.getApplicationElementId());
            }

            case Id_toSource:
                return "function Report(){\n/*compiled code*/\n}";

            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }
}
