/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.Parameter;
import com.eas.client.scripts.ScriptedResource;
import com.eas.script.Scripts;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class ScriptedQuery extends SqlQuery {

    public static final String JAVASCRIPT_QUERY_CONTENTS = "JavaScript query";

    public ScriptedQuery(ScriptedQuery aSource) {
        super(aSource);
    }

    public ScriptedQuery(DatabasesClient aBasesProxy, String aModuleName) {
        super(aBasesProxy);
        publicAccess = true;
        sqlText = JAVASCRIPT_QUERY_CONTENTS;
        datasourceName = aModuleName;
        entityName = aModuleName;
        readRoles = Collections.<String>emptySet();
        writeRoles = Collections.<String>emptySet();
        procedure = false;
    }

    @Override
    public ScriptedQuery copy() {
        return new ScriptedQuery(this);
    }

    @Override
    public SqlCompiledQuery compile() throws Exception {
        throw new IllegalStateException("ScriptedQuery.compile is not supported.");
    }

    private class ExecutionChecker {

        boolean isExecutionNeeded = true;

        public boolean isExecutionNeeded() {
            return isExecutionNeeded;
        }

        public void setExecutionNeeded(boolean state) {
            isExecutionNeeded = state;
        }
    }

    @Override
    public JSObject execute(Scripts.Space aSpace, Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception {
        assert Scripts.getSpace() == aSpace : "Scripts.Space TLS assumption failed";
        if (onSuccess != null) {
            ScriptedResource._require(new String[]{entityName}, null, aSpace, new HashSet<>(), (Void v) -> {
                JSObject source = aSpace.createModule(entityName);
                if (source.hasMember("fetch")) {
                    Object oFetch = source.getMember("fetch");
                    if (oFetch instanceof JSObject) {
                        JSObject jsFetch = (JSObject) oFetch;
                        if (jsFetch.isFunction()) {
                            JSObject jsParams = aSpace.makeObj();
                            for (int i = 0; i < params.getParametersCount(); i++) {
                                Parameter p = params.get(i + 1);
                                jsParams.setMember(p.getName(), aSpace.toJs(p.getValue()));
                            }
                            final ExecutionChecker exChecker = new ExecutionChecker();
                            Object oRowset = jsFetch.call(source, aSpace.toJs(new Object[]{
                                jsParams,
                                new AbstractJSObject() {

                                    @Override
                                    public Object call(final Object thiz, final Object... args) {
                                        if (exChecker.isExecutionNeeded()) {
                                            try {
                                                JSObject jsRowset = args.length > 0 ? (JSObject) aSpace.toJava(args[0]) : null;
                                                try {
                                                    onSuccess.accept(jsRowset);
                                                } catch (Exception ex) {
                                                    Logger.getLogger(ScriptedQuery.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            } catch (Exception ex) {
                                                if (onFailure != null) {
                                                    onFailure.accept(ex);
                                                }
                                            }
                                        }
                                        return null;
                                    }
                                },
                                new AbstractJSObject() {

                                    @Override
                                    public Object call(final Object thiz, final Object... args) {
                                        if (exChecker.isExecutionNeeded()) {
                                            if (onFailure != null) {
                                                if (args.length > 0) {
                                                    if (args[0] instanceof Exception) {
                                                        onFailure.accept((Exception) args[0]);
                                                    } else {
                                                        onFailure.accept(new Exception(String.valueOf(aSpace.toJava(args[0]))));
                                                    }
                                                } else {
                                                    onFailure.accept(new Exception("No error information from fetch method"));
                                                }
                                            }
                                        }
                                        return null;
                                    }
                                }
                            }));
                            if (!JSType.nullOrUndefined(oRowset)) {
                                onSuccess.accept((JSObject) aSpace.toJava(oRowset));
                                exChecker.setExecutionNeeded(false);
                            }
                        }
                    }
                }
            }, onFailure);
            return null;
        } else {
            JSObject source = aSpace.createModule(entityName);
            if (source.hasMember("fetch")) {
                Object oFetch = source.getMember("fetch");
                if (oFetch instanceof JSObject) {
                    JSObject jsFetch = (JSObject) oFetch;
                    if (jsFetch.isFunction()) {
                        JSObject jsParams = aSpace.makeObj();
                        Object oRowset = jsFetch.call(source, aSpace.toJs(new Object[]{jsParams}));
                        if (!JSType.nullOrUndefined(oRowset)) {
                            return (JSObject) aSpace.toJava(oRowset);
                        }
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void setSqlText(String aValue) {
    }

    @Override
    public void setPublicAccess(boolean aValue) {
    }

    @Override
    public void setReadRoles(Set<String> aRoles) {
    }

    @Override
    public void setWriteRoles(Set<String> aRoles) {
    }

    @Override
    public void setProcedure(boolean aValue) {
    }

}
