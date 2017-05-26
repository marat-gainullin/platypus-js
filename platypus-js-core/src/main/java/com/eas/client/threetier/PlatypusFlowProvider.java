/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameters;
import com.eas.client.AppConnection;
import com.eas.client.dataflow.FlowProviderFailedException;
import com.eas.client.metadata.Parameter;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.script.Scripts;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class PlatypusFlowProvider {

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

    public JSObject refresh(Parameters aParams, Scripts.Space aSpace, Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws FlowProviderFailedException {
        Map<String, String> params = new HashMap<>();
        for(int p = 1; p <= aParams.getParametersCount(); p++){
            Parameter param = aParams.get(p);
            params.put(param.getName(), aSpace.toJson(aSpace.toJs(param.getValue())));
        }
        ExecuteQueryRequest request = new ExecuteQueryRequest(entityName, params, expectedFields);
        if (onSuccess != null) {
            try {
                conn.<ExecuteQueryRequest.Response>enqueueRequest(request, aSpace, (ExecuteQueryRequest.Response aResponse) -> {
                    if (aResponse.getJson() == null) {
                        if (onFailure != null) {
                            onFailure.accept(new FlowProviderFailedException(ROWSET_MISSING_IN_RESPONSE + " while handling entity: " + getEntityName()));
                        }
                    } else {
                        onSuccess.accept((JSObject)aSpace.parseJsonWithDates(aResponse.getJson()));
                    }
                }, (Exception aException) -> {
                    if (onFailure != null) {
                        onFailure.accept(aException);
                    }
                });
                return null;
            } catch (Exception ex) {
                throw new FlowProviderFailedException(ex, getEntityName());
            }
        } else {
            try {
                ExecuteQueryRequest.Response response = conn.executeRequest(request);
                if (response.getJson() == null) {
                    throw new FlowProviderFailedException(ROWSET_MISSING_IN_RESPONSE + " while handling entity: " + getEntityName());
                }
                return (JSObject)aSpace.parseJsonWithDates(response.getJson());
            } catch (Exception ex) {
                throw new FlowProviderFailedException(ex, getEntityName());
            }
        }
    }

    public boolean isProcedure() {
        return procedure;
    }

    public void setProcedure(boolean aProcedure) {
        procedure = aProcedure;
    }

    public String getEntityName() {
        return entityName;
    }
}
