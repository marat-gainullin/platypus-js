/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.eas.client.AppClient;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.PlatypusThreeTierFlowProvider;
import java.util.Map;

/**
 * Query of data for three-tier application. Uses three-tier Flow provider for retriving data and 
 * for applying data changes.
 * @author mg
 */
public class PlatypusQuery extends Query<AppClient> {

    protected FlowProvider flow;

    public PlatypusQuery(AppClient aClient) {
        super(aClient);
    }

    protected PlatypusQuery(Query<AppClient> aSource) {
        super(aSource);
        client = aSource.getClient();
        createFlow();
    }

    @Override
    public void setEntityId(String aValue) {
        super.setEntityId(aValue);
        createFlow();
    }

    @Override
    public Rowset execute() throws Exception {
        Rowset rs = new Rowset(flow);
        rs.refresh(params);
        //lightMergeFields(rs.getFields(), fields);
        return rs;
    }

    @Override
    public void enqueueUpdate() throws Exception {
        client.enqueueUpdate(entityId, params);
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

    @Override
    public Query copy() {
        return new PlatypusQuery(this);
    }

    @Override
    public Map<String, Map<String, String>> getParametersBinds() {
        return null;
    }

    private void createFlow() {
        if (client != null && entityId != null) {
            flow = new PlatypusThreeTierFlowProvider(client, entityId, fields);
        }
    }

    public void setClient(PlatypusClient aClient) {
        client = aClient;
        createFlow();
    }
}
