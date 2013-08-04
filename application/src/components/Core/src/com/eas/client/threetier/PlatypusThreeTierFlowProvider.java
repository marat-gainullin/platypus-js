/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.exceptions.FlowProviderFailedException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.AppClient;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.RowsetResponse;
import java.util.List;

/**
 *
 * @author mg
 */
public class PlatypusThreeTierFlowProvider implements FlowProvider {

    protected AppClient client;
    protected Fields expectedFields;
    protected boolean procedure = false;
    protected String entityId;

    public PlatypusThreeTierFlowProvider(AppClient aClient, String aEntityId, Fields aExpectedFields) {
        super();
        client = aClient;
        entityId = aEntityId;
        expectedFields = aExpectedFields;
    }

    @Override
    public List<Change> getChangeLog() {
        return client.getChangeLog();
    }

    @Override
    public TransactionListener.Registration addTransactionListener(TransactionListener tl) {
        return client.addTransactionListener(tl);
    }

    @Override
    public Rowset nextPage() throws RowsetException {
        throw new RowsetException("Method \"nextPage()\" is not supported in three-tier mode.");
    }

    @Override
    public Rowset refresh(Parameters aParams) throws RowsetException {
        try {
            ExecuteQueryRequest request = new ExecuteQueryRequest(IDGenerator.genID(), entityId, aParams, expectedFields);
            client.executeRequest(request);
            RowsetResponse response = (RowsetResponse) request.getResponse();
            // let's return parameters
            /*
            if (procedure) {
                for (int i = 1; i <= aParams.getParametersCount(); i++) {
                    Parameter innerParam = aParams.get(i);
                    if (innerParam.getMode() == ParameterMetaData.parameterModeOut
                            || innerParam.getMode() == ParameterMetaData.parameterModeInOut) {
                        Parameter paramFromServer = response.getParameters().get(i);
                        innerParam.setValue(paramFromServer.getValue());
                    }
                }
            }
            */ 
            // let's retrun rowset
            Rowset rs = response.getRowset();
            if(rs == null) {
                throw new FlowProviderFailedException("Rowset response hasn't returned any rowset. May be dml query is executed as select query.");
            }
            rs.setFlowProvider(this);
            return rs;
        } catch (Exception ex) {
            throw new FlowProviderFailedException(ex);
        }
    }

    @Override
    public boolean isProcedure() {
        return procedure;
    }

    @Override
    public void setProcedure(boolean aProcedure) {
        procedure = aProcedure;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public int getPageSize() {
        throw new UnsupportedOperationException("Not supported yet."); //NOI18N
    }
    
    @Override
    public void setPageSize(int i) {
    }
}
