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
import com.eas.client.model.script.ParametersHostObject;
import com.eas.client.scripts.ScriptRunner;
import com.eas.script.ScriptUtils;
import com.eas.script.ScriptUtils.ScriptAction;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * Specific <code>FlowProvider</code> implementation, that allows js modules to be used as data sources.
 * @see FlowProvider
 * @author mg
 */
public class PlatypusScriptedFlowProvider implements FlowProvider {

    protected int pageSize = NO_PAGING_PAGE_SIZE;
    protected String sessionId;
    protected DbClient client;
    protected ScriptRunner source;
    protected Fields expectedFields;

    public PlatypusScriptedFlowProvider(DbClient aClient, Fields aExpectedFields, ScriptRunner aSource, String aSessionId) {
        client = aClient;
        expectedFields = aExpectedFields;
        sessionId = aSessionId;
        source = aSource;
    }

    @Override
    public String getEntityId() {
        return source.getApplicationElementId();
    }

    private Rowset readRowset(Object oFirstPage, Context cx, Object[] aArgs) throws RowsetMissingException, RowsetException, EvaluatorException {
        Rowset rowset = new Rowset(expectedFields);
        if (oFirstPage instanceof Function) {
            Function f = (Function) oFirstPage;
            Object oRowset = f.call(cx, source.getParentScope(), source, aArgs);
            if (oRowset instanceof Scriptable) {
                Scriptable sRowset = (Scriptable) oRowset;
                Object oLength = sRowset.get("length", sRowset);
                if (oLength instanceof Number) {
                    List<Row> rows = new ArrayList<>();
                    int length = ((Number) oLength).intValue();
                    for (int i = 0; i < length; i++) {
                        Object oRow = sRowset.get(i, sRowset);
                        if (oRow instanceof Scriptable) {
                            Scriptable sRow = (Scriptable) oRow;
                            Row row = new Row(expectedFields);
                            for (Field field : expectedFields.toCollection()) {
                                Object jsValue = sRow.get(field.getName(), sRow);
                                if (Scriptable.NOT_FOUND == jsValue) {
                                    Logger.getLogger(PlatypusScriptedFlowProvider.class.getName()).log(Level.WARNING, "{0} property was not found while reading script data from {1}", new Object[]{field.getName(), source.getApplicationElementId()});
                                } else {
                                    Object javaValue = ScriptUtils.js2Java(jsValue);
                                    row.setColumnObject(expectedFields.find(field.getName()), javaValue);
                                }
                            }
                            rows.add(row);
                        }
                    }
                    rowset.setCurrent(rows);
                    rowset.currentToOriginal();
                }
            }
        }
        return rowset;
    }

    @Override
    public Rowset refresh(final Parameters aParameters) throws Exception {
        return ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Rowset run(Context cx) throws Exception {
                Object oFirstPage = source.get("fetch");
                Rowset rowset = readRowset(oFirstPage, cx, new Object[]{new ParametersHostObject(aParameters, source)});
                return rowset;
            }
        });
    }

    @Override
    public Rowset nextPage() throws Exception {
        return ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Rowset run(Context cx) throws Exception {
                Object oFirstPage = source.get("nextPage");
                Rowset rowset = readRowset(oFirstPage, cx, new Object[]{});
                return rowset;
            }
        });
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
        return client.getChangeLog(source.getApplicationElementId(), sessionId);
    }

    @Override
    public TransactionListener.Registration addTransactionListener(TransactionListener tl) {
        return client.addTransactionListener(tl);
    }
}
