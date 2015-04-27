/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.Command;
import com.eas.client.dataflow.FlowProvider;
import com.eas.client.metadata.Parameter;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.PlatypusFlowProvider;
import java.util.Map;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Query of data for three-tier application. Uses three-tier Flow provider for retriving data and 
 * for applying data changes.
 * @author mg
 */
public class PlatypusQuery extends Query {

    protected PlatypusClient serverProxy;

    public PlatypusQuery(PlatypusClient aServerProxy) {
        super();
        serverProxy = aServerProxy;
    }

    protected PlatypusQuery(PlatypusQuery aSource) {
        super(aSource);
        serverProxy = aSource.getServerProxy();
    }

    public PlatypusClient getServerProxy() {
        return serverProxy;
    }

    @Override
    public JSObject execute(Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception {
        FlowProvider flow = createFlow();
        JSObject rowset = flow.refresh(params, onSuccess, onFailure);
        return rowset;
    }
    
    public Command prepareCommand(){
        Command command = new Command(entityName);
        for (int i = 0; i < params.getParametersCount(); i++) {
            Parameter p = params.get(i + 1);
            command.getParameters().add(new ChangeValue(p.getName(), p.getValue(), p.getTypeInfo()));
        }
        return command;
    }

    /**
     * Merges some minimum of information on fields, because server is responsible on full resolving,
     * like comments, primary and foreign keys and correct types, including geometries.
     * This method does last time tricks, such as primary keys on key-less (synthetic, view and so on) rowsets.
     * May be this method will do something else in future.
     * @param destFields Fields to be merged with etalon fields.
     * @param sourceFields Etalon fields, likely a query fields, got from server.
     */
    /*
    protected void lightMergeFields(Fields destFields, Fields sourceFields) {
        for (int i = 1; i <= sourceFields.getFieldsCount(); i++) {
            Field srcField = sourceFields.get(i);
            Field rowsetField = destFields.get(srcField.getName());
            if (rowsetField != null) {
                rowsetField.setPk(srcField.isPk());
                // Further tricks...
            }
        }
    }
    */ 

    /**
     * 
     * @return 
     */
    @Override
    public PlatypusQuery copy() {
        return new PlatypusQuery(this);
    }

    @Override
    public Map<String, Map<String, String>> getParametersBinds() {
        return null;
    }

    private FlowProvider createFlow() {
        if (serverProxy != null && entityName != null) {
            return new PlatypusFlowProvider(serverProxy, entityName, fields);
        }else
            return null;
    }

    public void setServerProxy(PlatypusClient aServerProxy) {
        serverProxy = aServerProxy;
    }
}
