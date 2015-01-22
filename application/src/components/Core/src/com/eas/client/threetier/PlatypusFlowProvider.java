/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.exceptions.FlowProviderFailedException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.AppConnection;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class PlatypusFlowProvider implements FlowProvider {

    private static final String ROWSET_MISSING_IN_RESPONSE = "Rowset response hasn't returned any rowset. May be dml query is executed as select query.";

    protected PlatypusClient serverProxy;
    protected Fields expectedFields;
    protected boolean procedure;
    protected String entityName;
    protected AppConnection conn;

    public PlatypusFlowProvider(PlatypusClient aClient, String aEntityName, Fields aExpectedFields) {
        super();
        serverProxy = aClient;
        conn = serverProxy.getConn();
        entityName = aEntityName;
        expectedFields = aExpectedFields;
    }

    @Override
    public Rowset nextPage(Consumer<Rowset> onSuccess, Consumer<Exception> onFailure) throws RowsetException {
        throw new RowsetException("Method \"nextPage()\" is not supported in three-tier mode.");
    }

    @Override
    public Rowset refresh(Parameters aParams, Consumer<Rowset> onSuccess, Consumer<Exception> onFailure) throws RowsetException {
        ExecuteQueryRequest request = new ExecuteQueryRequest(entityName, aParams, expectedFields);
        if (onSuccess != null) {
            try {
                conn.<ExecuteQueryRequest.Response>enqueueRequest(request, (ExecuteQueryRequest.Response aResponse) -> {
                    if (aResponse.getRowset() == null) {
                        if (onFailure != null) {
                            onFailure.accept(new FlowProviderFailedException(ROWSET_MISSING_IN_RESPONSE));
                        }
                    } else {
                        aResponse.getRowset().setFlowProvider(this);
                        onSuccess.accept(aResponse.getRowset());
                    }
                }, (Exception aException) -> {
                    if (onFailure != null) {
                        onFailure.accept(aException);
                    }
                });
                return null;
            } catch (Exception ex) {
                throw new RowsetException(ex);
            }
        } else {
            try {
                ExecuteQueryRequest.Response response = conn.executeRequest(request);
                if (response.getRowset() == null) {
                    throw new FlowProviderFailedException(ROWSET_MISSING_IN_RESPONSE);
                }
                response.getRowset().setFlowProvider(this);
                return response.getRowset();
            } catch (Exception ex) {
                throw new RowsetException(ex);
            }
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
        return entityName;
    }

    @Override
    public int getPageSize() {
        throw new UnsupportedOperationException("Not supported yet."); //NOI18N
    }

    @Override
    public void setPageSize(int aValue) {
    }
}
