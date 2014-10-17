/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.gui.grid.data.ChildrenFetcher;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.queries.Query;
import com.eas.dbcontrols.RowsetDbControl;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class GridChildrenFetcher implements ChildrenFetcher<Row> {

    //
    protected RowsetDbControl hostControl;
    protected ApplicationEntity<?, ?, ?> fetchWith;
    protected Parameter fetchingParameter;
    protected int parameterSourceColIndex;

    public GridChildrenFetcher(RowsetDbControl aHostControl, ApplicationEntity<?, ?, ?> aFetchWith, Parameter aFetchingParameter, int aParameterSourceColIndex) {
        super();
        hostControl = aHostControl;
        fetchWith = aFetchWith;
        fetchingParameter = aFetchingParameter;
        parameterSourceColIndex = aParameterSourceColIndex;
    }

    /**
     * One fetch operation.
     *
     * @param aParentRow Row the children will be fetched of.
     * @param aFetchCompleter Completer, wich will be asked to send proper
     * events to model controls, such a grid.
     * @param aAsynchronous
     */
    @Override
    public void fetch(Row aParentRow, Runnable aFetchCompleter, boolean aAsynchronous) {
        hostControl.addProcessedRow(aParentRow);
        Runnable r = new Fetcher(aParentRow, aFetchCompleter, aAsynchronous);
        r.run();
    }

    private class Fetcher implements Runnable {

        private final Row parentRow;
        private final Runnable fetchCompleter;
        private final boolean asynchronous;

        public Fetcher(Row aParentRow, Runnable aFetchCompleter, boolean aAsynchronous) {
            parentRow = aParentRow;
            fetchCompleter = aFetchCompleter;
            asynchronous = aAsynchronous;
        }

        @Override
        public void run() {
            try {
                // setup parameters
                fetchWith.bindQueryParameters();
                Query query = fetchWith.getQuery().copy();
                Parameter param2Init = query.getParameters().get(fetchingParameter.getName());
                param2Init.setValue(parentRow.getColumnObject(parameterSourceColIndex));
                // execute and accept results
                Consumer<Rowset> fetchedAcceptor = (Rowset childrenRowset) -> {
                    try {
                        List<Integer> pkFields = childrenRowset.getFields().getPrimaryKeysIndicies();
                        childrenRowset.beforeFirst();
                        Rowset generalRowset = fetchWith.getRowset();
                        int preservedCursorPos = generalRowset.getCursorPos();
                        Set<RowsetListener> listeners = generalRowset.getRowsetChangeSupport().getRowsetListeners();
                        generalRowset.getRowsetChangeSupport().setRowsetListeners(null);
                        FlowProvider generalRowsetFlow = generalRowset.getFlowProvider();
                        generalRowset.setFlowProvider(null);
                        boolean oldModified = generalRowset.isModified();
                        try {
                            for (Row row : childrenRowset.getCurrent()) {
                                row.removePropertyChangeListener(childrenRowset);
                                row.removeVetoableChangeListener(childrenRowset);
                                Object[] initing = new Object[pkFields.size() * 2];
                                for (int i = 0; i < pkFields.size(); i++) {
                                    initing[i * 2] = pkFields.get(i);
                                    initing[i * 2 + 1] = row.getCurrentValues()[pkFields.get(i) - 1];
                                }
                                generalRowset.insert(row, true, initing);
                                row.clearInserted();
                                row.clearUpdated();
                            }
                            childrenRowset.setCurrent(new ArrayList<>());
                            childrenRowset.currentToOriginal();
                        } finally {
                            generalRowset.setModified(oldModified);
                            generalRowset.setFlowProvider(generalRowsetFlow);
                            generalRowset.getRowsetChangeSupport().setRowsetListeners(listeners);
                        }
                        try {
                            generalRowset.absolute(preservedCursorPos);
                        } finally {
                            fetchCompleter.run();
                            EventQueue.invokeLater(() -> {
                                try {
                                    hostControl.removeProcessedRow(parentRow);
                                    hostControl.makeVisible(generalRowset.getCurrentRow(), true);
                                } catch (Exception ex) {
                                    Logger.getLogger(GridChildrenFetcher.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(GridChildrenFetcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                };
                if (asynchronous) {
                    query.execute((Rowset aRowset) -> {
                        fetchedAcceptor.accept(aRowset);
                    }, (Exception ex) -> {
                        Logger.getLogger(GridChildrenFetcher.class.getName()).log(Level.SEVERE, null, ex);
                    });
                } else {
                    Rowset fetched = query.execute(null, null);
                    fetchedAcceptor.accept(fetched);
                }
            } catch (Exception ex) {
                Logger.getLogger(GridChildrenFetcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
