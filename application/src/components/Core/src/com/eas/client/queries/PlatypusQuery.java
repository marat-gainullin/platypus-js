/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.Command;
import com.eas.client.metadata.Parameter;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.PlatypusFlowProvider;
import com.eas.script.Scripts;
import java.util.Map;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Query of data for three-tier application. Uses three-tier Flow provider for
 * retriving data and for applying data changes.
 *
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
    public boolean isMetadataAccessible() {
        return true;
    }

    @Override
    public JSObject execute(Scripts.Space aSpace, Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (serverProxy != null && entityName != null) {
            PlatypusFlowProvider flow = new PlatypusFlowProvider(serverProxy, entityName, fields);
            JSObject rowset = flow.refresh(params, aSpace, onSuccess, onFailure);
            return rowset;
        } else {
            return null;
        }
    }

    public Command prepareCommand() {
        Command command = new Command(entityName);
        for (int i = 0; i < params.getParametersCount(); i++) {
            Parameter param = params.get(i + 1);
            // Command couldn't contain JavaScript values, because of multithreading model, ChangesJSONWriter, etc.
            command.getParameters().add(new ChangeValue(param.getName(), Scripts.getSpace().toJava(param.getValue())));
        }
        return command;
    }

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

    public void setServerProxy(PlatypusClient aServerProxy) {
        serverProxy = aServerProxy;
    }
}
