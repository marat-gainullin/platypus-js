/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.DatabasesClient;
import com.eas.client.DummyDataSource;
import com.eas.client.PlatypusJdbcFlowProvider;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.script.Scripts;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 * Specific <code>FlowProvider</code> implementation, that allows js modules to
 * be used as data sources.
 *
 * @see FlowProvider
 * @author mg
 */
public class ScriptedFlowProvider extends PlatypusJdbcFlowProvider {

    protected JSObject source;

    public ScriptedFlowProvider(DatabasesClient aClient, Fields aExpectedFields, JSObject aSource) throws Exception {
        super(aClient, "-no-name-", null, new DummyDataSource(), null, null, ScriptedQuery.JAVASCRIPT_QUERY_CONTENTS, null, null);
        pageSize = NO_PAGING_PAGE_SIZE;
        client = aClient;
        expectedFields = aExpectedFields;
        source = aSource;
    }

    @Override
    public String getEntityName() {
        return (String) ((JSObject) source.getMember("constructor")).getMember("name");
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
    public JSObject refresh(final Parameters aParameters, Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (source.hasMember("fetch")) {
            Object oFetch = source.getMember("fetch");
            if (oFetch instanceof JSObject) {
                JSObject jsFetch = (JSObject) oFetch;
                if (jsFetch.isFunction()) {
                    JSObject jsParams = Scripts.makeObj();
                    for (int i = 0; i < aParameters.getParametersCount(); i++) {
                        Parameter p = aParameters.get(i + 1);
                        jsParams.setMember(p.getName(), Scripts.toJs(p.getValue()));
                    }
                    if (onSuccess != null) {
                        final ExecutionChecker exChecker = new ExecutionChecker();
                        Object oRowset = jsFetch.call(source, Scripts.toJs(new Object[]{
                            jsParams,
                            new AbstractJSObject() {

                                @Override
                                public Object call(final Object thiz, final Object... args) {
                                    if (exChecker.isExecutionNeeded()) {
                                        try {
                                            JSObject jsRowset = args.length > 0 ? (JSObject) Scripts.toJava(args[0]) : null;
                                            try {
                                                onSuccess.accept(jsRowset);
                                            } catch (Exception ex) {
                                                Logger.getLogger(ScriptedFlowProvider.class.getName()).log(Level.SEVERE, null, ex);
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
                                                    onFailure.accept(new Exception(String.valueOf(Scripts.toJava(args[0]))));
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
                            onSuccess.accept((JSObject) Scripts.toJava(oRowset));
                            exChecker.setExecutionNeeded(false);
                        }
                        return null;
                    } else {
                        Object oRowset = jsFetch.call(source, Scripts.toJs(new Object[]{jsParams}));
                        if (!JSType.nullOrUndefined(oRowset)) {
                            return (JSObject) Scripts.toJava(oRowset);
                        } else {
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public JSObject nextPage(Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (source.hasMember("nextPage")) {
            Object oNextPage = source.getMember("nextPage");
            if (oNextPage instanceof JSObject) {
                JSObject jsNextPage = (JSObject) oNextPage;
                if (jsNextPage.isFunction()) {
                    if (onSuccess != null) {
                        final ExecutionChecker exChecker = new ExecutionChecker();
                        Object oRowset = jsNextPage.call(source, Scripts.toJs(new Object[]{
                            new AbstractJSObject() {

                                @Override
                                public Object call(final Object thiz, final Object... args) {
                                    if (exChecker.isExecutionNeeded()) {
                                        try {
                                            Object oRowset = args.length > 0 ? Scripts.toJava(args[0]) : null;
                                            JSObject jsRowset = (JSObject) oRowset;
                                            try {
                                                onSuccess.accept(jsRowset);
                                            } catch (Exception ex) {
                                                Logger.getLogger(ScriptedFlowProvider.class.getName()).log(Level.SEVERE, null, ex);
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
                                                    onFailure.accept(new Exception(String.valueOf(Scripts.toJava(args[0]))));
                                                }
                                            } else {
                                                onFailure.accept(new Exception("No error information from nextPage method"));
                                            }
                                        }
                                    }
                                    return null;
                                }
                            }
                        }));
                        if (!JSType.nullOrUndefined(oRowset)) {
                            onSuccess.accept((JSObject) Scripts.toJava(oRowset));
                            exChecker.setExecutionNeeded(false);
                        } else {
                            return null;
                        }
                    } else {
                        Object oRowset = jsNextPage.call(source, Scripts.toJs(new Object[]{}));
                        if (!JSType.nullOrUndefined(oRowset)) {
                            return (JSObject) Scripts.toJava(oRowset);
                        } else {
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int aValue) {
        pageSize = aValue;
    }

    @Override
    public boolean isProcedure() {
        return false;
    }

    @Override
    public void setProcedure(boolean bln) {
    }
}
