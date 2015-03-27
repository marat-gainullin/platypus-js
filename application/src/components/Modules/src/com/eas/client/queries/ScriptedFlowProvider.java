/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.DatabasesClient;
import com.eas.client.PlatypusJdbcFlowProvider;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.RowsetMissingException;
import com.eas.script.ScriptUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.Undefined;

/**
 * Specific <code>FlowProvider</code> implementation, that allows js modules to
 * be used as data sources.
 *
 * @see FlowProvider
 * @author mg
 */
public class ScriptedFlowProvider extends PlatypusJdbcFlowProvider {

    protected int pageSize = NO_PAGING_PAGE_SIZE;
    protected DatabasesClient client;
    protected JSObject source;
    protected Fields expectedFields;

    public ScriptedFlowProvider(DatabasesClient aClient, Fields aExpectedFields, JSObject aSource) throws Exception {
        super(aClient, "-no-name-", null, null, null, null, null, null, null);
        client = aClient;
        expectedFields = aExpectedFields;
        source = aSource;
    }

    @Override
    public String getEntityId() {
        return (String) ((JSObject) source.getMember("constructor")).getMember("name");
    }
/*
    private void readRowset(Object oRowset, Rowset aRowset) throws RowsetMissingException, RowsetException {
        if (oRowset instanceof JSObject) {
            JSObject sRowset = (JSObject) oRowset;
            Object oLength = sRowset.getMember("length");
            if (oLength instanceof Number) {
                List<Row> rows = new ArrayList<>();
                int length = ((Number) oLength).intValue();
                for (int i = 0; i < length; i++) {
                    Object oRow = sRowset.getSlot(i);
                    if (oRow instanceof JSObject) {
                        JSObject sRow = (JSObject) oRow;
                        Row row = new Row(getEntityId(), expectedFields);
                        for (Field field : expectedFields.toCollection()) {
                            if (sRow.hasMember(field.getName())) {
                                Object javaValue = ScriptUtils.toJava(sRow.getMember(field.getName()));
                                row.setColumnObject(expectedFields.find(field.getName()), javaValue);
                            } else {
                                Logger.getLogger(ScriptedFlowProvider.class.getName()).log(Level.WARNING, "{0} property was not found while reading script data from {1}", new Object[]{field.getName(), getEntityId()});
                            }
                        }
                        rows.add(row);
                    }
                }
                aRowset.setCurrent(rows);
                aRowset.currentToOriginal();
            }
        }
    }
*/
    private class ExecutionChecker {

        boolean isExecutionNeeded;

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
                    JSObject jsParams = ScriptUtils.makeObj();
                    for (int i = 0; i < aParameters.getParametersCount(); i++) {
                        Parameter p = aParameters.get(i + 1);
                        jsParams.setMember(p.getName(), ScriptUtils.toJs(p.getValue()));
                    }
                    if (onSuccess != null) {
                        final ExecutionChecker exChecker = new ExecutionChecker();
                        Object oRowset = jsFetch.call(source, ScriptUtils.toJs(new Object[]{
                            jsParams,
                            new AbstractJSObject() {

                                @Override
                                public Object call(final Object thiz, final Object... args) {
                                    if (exChecker.isExecutionNeeded()) {
                                        try {
                                            Object ojsRowset = args.length > 0 ? ScriptUtils.toJava(args[0]) : null;
                                            Rowset rowset = new Rowset(expectedFields);
                                            readRowset(ojsRowset, rowset);
                                            try {
                                                onSuccess.accept(rowset);
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
                                                    onFailure.accept(new Exception(String.valueOf(ScriptUtils.toJava(args[0]))));
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
                        if (oRowset != null && !(oRowset instanceof Undefined)) {
                            Rowset rowset = new Rowset(expectedFields);
                            readRowset((JSObject) oRowset, rowset);
                            onSuccess.accept(rowset);
                            exChecker.setExecutionNeeded(false);
                        }
                        return null;
                    } else {
                        Object oRowset = jsFetch.call(source, ScriptUtils.toJs(new Object[]{jsParams}));
                        if (oRowset == null || oRowset instanceof Undefined) {
                            return null;
                        } else {
                            Rowset rowset = new Rowset(expectedFields);
                            readRowset((JSObject) oRowset, rowset);
                            return rowset;
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
                        Object oRowset = jsNextPage.call(source, ScriptUtils.toJs(new Object[]{
                            new AbstractJSObject() {

                                @Override
                                public Object call(final Object thiz, final Object... args) {
                                    if (exChecker.isExecutionNeeded()) {
                                        try {
                                            Object pjsRowset = args.length > 0 ? args[0] : null;
                                            Rowset rowset = new Rowset(expectedFields);
                                            readRowset(pjsRowset, rowset);
                                            try {
                                                onSuccess.accept(rowset);
                                            } catch (Exception ex) {
                                                Logger.getLogger(ScriptedFlowProvider.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        } catch (RowsetMissingException | RowsetException ex) {
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
                                                    onFailure.accept(new Exception(String.valueOf(ScriptUtils.toJava(args[0]))));
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
                        if (oRowset != null && !(oRowset instanceof Undefined)) {
                            Rowset rowset = new Rowset(expectedFields);
                            readRowset((JSObject) oRowset, rowset);
                            onSuccess.accept(rowset);
                            exChecker.setExecutionNeeded(false);
                        }
                        return null;
                    } else {
                        Object oRowset = jsNextPage.call(source, ScriptUtils.toJs(new Object[]{}));
                        if (oRowset == null || oRowset instanceof Undefined) {
                            return null;
                        } else {
                            Rowset rowset = new Rowset(expectedFields);
                            readRowset((JSObject) oRowset, rowset);
                            return rowset;
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
