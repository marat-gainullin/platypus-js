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
    protected Scripts.Space space;

    public PlatypusQuery(PlatypusClient aServerProxy, Scripts.Space aSpace) {
        super();
        serverProxy = aServerProxy;
        space = aSpace;
    }

    public Scripts.Space getSpace() {
        return space;
    }

    protected PlatypusQuery(PlatypusQuery aSource) {
        super(aSource);
        serverProxy = aSource.getServerProxy();
        space = aSource.getSpace();
    }

    public PlatypusClient getServerProxy() {
        return serverProxy;
    }

    @Override
    public JSObject execute(Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (serverProxy != null && entityName != null) {
            PlatypusFlowProvider flow = new PlatypusFlowProvider(serverProxy, entityName, fields);
            JSObject rowset = flow.refresh(params, onSuccess, onFailure);
            return rowset;
        } else {
            return null;
        }
    }

    public Command prepareCommand() {
        Command command = new Command(entityName);
        for (int i = 0; i < params.getParametersCount(); i++) {
            Parameter p = params.get(i + 1);
            command.getParameters().add(new ChangeValue(p.getName(), p.getValue(), p.getTypeInfo()));
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
