/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.DbClient;
import com.eas.client.model.RowsetMissingException;
import com.eas.script.ScriptUtils;
import com.eas.util.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Specific <code>FlowProvider</code> implementation, that allows js modules to
 * be used as data sources.
 *
 * @see FlowProvider
 * @author mg
 */
public class PlatypusScriptedFlowProvider implements FlowProvider {

    protected int pageSize = NO_PAGING_PAGE_SIZE;
    protected DbClient client;
    protected JSObject source;
    protected Fields expectedFields;
    protected List<Change> changeLog = new ArrayList<>();

    public PlatypusScriptedFlowProvider(DbClient aClient, Fields aExpectedFields, JSObject aSource) {
        super();
        client = aClient;
        expectedFields = aExpectedFields;
        source = aSource;
    }

    @Override
    public String getEntityId() {
        return (String) ((JSObject) source.getMember("constructor")).getMember("name");
    }

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
                        Row row = new Row(expectedFields);
                        for (Field field : expectedFields.toCollection()) {
                            if (sRow.hasMember(field.getName())) {
                                Object javaValue = ScriptUtils.toJava(sRow.getMember(field.getName()));
                                row.setColumnObject(expectedFields.find(field.getName()), javaValue);
                            } else {
                                Logger.getLogger(PlatypusScriptedFlowProvider.class.getName()).log(Level.WARNING, "{0} property was not found while reading script data from {1}", new Object[]{field.getName(), getEntityId()});
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

    @Override
    public Rowset refresh(final Parameters aParameters, Consumer<Rowset> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (source.hasMember("fetch")) {
            Object oFirstPage = source.getMember("fetch");
            if (oFirstPage instanceof JSObject) {
                JSObject jsFirstPage = (JSObject) oFirstPage;
                if (jsFirstPage.isFunction()) {
                    if (onSuccess != null) {
                        jsFirstPage.call(source, ScriptUtils.toJs(new Object[]{new Consumer<JSObject>() {

                            @Override
                            public void accept(JSObject jsRowset) {
                                Rowset rowset = new Rowset(expectedFields);
                                try {
                                    readRowset(jsRowset, rowset);
                                } catch (Exception ex) {
                                    if (onFailure != null) {
                                        onFailure.accept(ex);
                                    }
                                    return;
                                }
                                onSuccess.accept(rowset);
                            }

                        }, onFailure}));
                        return null;
                    } else {
                        Rowset rowset = new Rowset(expectedFields);
                        Object oRowset = jsFirstPage.call(source, ScriptUtils.toJs(new Object[]{aParameters.getPublished()}));
                        readRowset((JSObject) oRowset, rowset);
                        return rowset;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Rowset nextPage(Consumer<Rowset> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (source.hasMember("nextPage")) {
            Object oNextPage = source.getMember("nextPage");
            if (oNextPage instanceof JSObject) {
                JSObject jsNextPage = (JSObject) oNextPage;
                if (jsNextPage.isFunction()) {
                    if (onSuccess != null) {
                        jsNextPage.call(source, ScriptUtils.toJs(new Object[]{new Consumer<JSObject>() {

                            @Override
                            public void accept(JSObject jsRowset) {
                                Rowset rowset = new Rowset(expectedFields);
                                try {
                                    readRowset(jsRowset, rowset);
                                } catch (Exception ex) {
                                    if (onFailure != null) {
                                        onFailure.accept(ex);
                                    }
                                    return;
                                }
                                onSuccess.accept(rowset);
                            }

                        }, onFailure}));
                        return null;
                    } else {
                        Object oRowset = jsNextPage.call(source, ScriptUtils.toJs(new Object[]{}));
                        if (oRowset != null) {
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

    @Override
    public List<Change> getChangeLog() {
        return changeLog;
    }

    @Override
    public ListenerRegistration addTransactionListener(TransactionListener tl) {
        return client.addTransactionListener(tl);
    }
}
