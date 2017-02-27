package com.eas.client.queries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.eas.application.WebFlowProvider;
import com.eas.client.AppClient;
import com.eas.client.dataflow.FlowProvider;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.core.Cancellable;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Abstract platypus query with parameters.
 *
 * @author mg
 */
public class Query {

    protected AppClient client;
    // parameters propagation. ParamName - DsName, ParamName
    protected Map<String, Map<String, String>> parametersBinds = new HashMap<>();
    protected Fields fields = new Fields();
    protected Parameters params = new Parameters();
    protected String title;
    protected String entityName;
    protected boolean procedure;
    protected Set<String> readRoles = new HashSet<>();
    protected Set<String> writeRoles = new HashSet<>();

    /**
     * Creates an instance of Query with empty SQL query text and parameters.
     */
    public Query() {
    }

    public Query(AppClient aClient) {
        this();
        client = aClient;
    }

    public Query copy()
    {
    	return new Query(this);
    }
    
    /**
     * Copy constructor
     *
     * @param aSource Another Query instance the data to be copied from.
     */
    protected Query(Query aSource) {
        if (aSource != null) {
            procedure = aSource.isProcedure();
            entityName = aSource.getEntityName();
            String aTitle = aSource.getTitle();
            if (aTitle != null) {
                title = new String(aTitle.toCharArray());
            }
            Parameters lpmdi = aSource.getParameters();
            if (lpmdi != null) {
                params = lpmdi.copy();
            }
            Fields sFields = aSource.getFields();
            if (sFields != null) {
                fields = sFields.copy();
            }
            parametersBinds.clear();
            Map<String, Map<String, String>> lparametersBinds = aSource.getParametersBinds();
            if (lparametersBinds != null) {
                Set<Entry<String, Map<String, String>>> lentries = lparametersBinds.entrySet();
                if (lentries != null) {
                    Iterator<Entry<String, Map<String, String>>> entIt = lentries.iterator();
                    if (entIt != null) {
                        while (entIt.hasNext()) {
                            Entry<String, Map<String, String>> lent = entIt.next();
                            if (lent != null) {
                                String parName = lent.getKey();
                                if (parName != null && !parName.isEmpty()) {
                                    Map<String, String> lParValue = lent.getValue();
                                    if (lParValue != null) {
                                        Set<Entry<String, String>> lpEntries = lParValue.entrySet();
                                        if (lpEntries != null) {
                                            Iterator<Entry<String, String>> lpEntIt = lpEntries.iterator();
                                            if (lpEntIt != null) {
                                                Map<String, String> lparamBinds = new HashMap<>();
                                                parametersBinds.put(new String(parName.toCharArray()), lparamBinds);
                                                while (lpEntIt.hasNext()) {
                                                    Entry<String, String> lpEnt = lpEntIt.next();
                                                    String dsName = lpEnt.getKey();
                                                    String dsParName = lpEnt.getValue();
                                                    if (dsName != null && !dsName.isEmpty()
                                                            && dsParName != null && !dsParName.isEmpty()) {
                                                        lparamBinds.put(new String(dsName.toCharArray()), new String(dsParName.toCharArray()));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            client = aSource.getClient();
        }
    }

    public void setEntityName(String aValue) {
        entityName = aValue;
    }

    public Cancellable execute(Callback<JavaScriptObject, String> aCallback) throws Exception {
        FlowProvider flow = createFlow();
        return flow.refresh(params, aCallback);
    }
    
    public native JavaScriptObject prepareCommand()/*-{
		var B = @com.eas.core.Predefine::boxing;
        var command = {kind: 'command', entity: this.@com.eas.client.queries.Query::getEntityName()(), parameters: {}};
        var nParameters = this.@com.eas.client.queries.Query::getParameters()();
        var pCount = nParameters.@com.eas.client.metadata.Parameters::getParametersCount()();
        for (var i = 0; i < pCount; i++) {
            var nParameter = nParameters.@com.eas.client.metadata.Parameters::get(I)(i + 1);
            command.parameters[nParameter.@com.eas.client.metadata.Parameter::getName()()] = B.boxAsJs(nParameter.@com.eas.client.metadata.Parameter::getJsValue()());
        }
        return command;
    }-*/;
    
    private FlowProvider createFlow() {
    	assert client != null : "A client must be specified";
        return new WebFlowProvider(client, entityName, fields);
    }

    public void setClient(AppClient aClient) {
        client = aClient;
    }

    public AppClient getClient() {
        return client;
    }

    /**
     * Returns whether this query is stored procedure call.
     *
     * @return True if this query is stored procedure call.
     */
    public boolean isProcedure() {
        return procedure;
    }

    /**
     * Sets procedure flag.
     *
     * @param aValue
     */
    public void setProcedure(boolean aValue) {
        procedure = aValue;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields aValue) {
        fields = aValue;
    }

    public Parameters getParameters() {
        return params;
    }

    public void putParameter(String aName, String aType, Object aValue) {
        if (params == null) {
            params = new Parameters();
        }
        Parameter param = params.get(aName);
        if (param == null) {
            param = new Parameter();
            params.add(param);
        }
        param.setName(aName.toUpperCase());
        param.setType(aType);
        param.setDefaultValue(aValue);
        param.setValue(aValue);
    }

    public void putParameter(String aName, String aType, Object aDefaultValue, Object aValue) {
        if (params == null) {
            params = new Parameters();
        }
        Parameter param = params.get(aName);
        if (param == null) {
            param = new Parameter();
            params.add(param);
        }
        param.setName(aName);
        param.setType(aType);
        param.setDefaultValue(aDefaultValue);
        param.setValue(aValue);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String aValue) {
        title = aValue;
    }

    public Map<String, Map<String, String>> getParametersBinds() {
        return parametersBinds;
    }

    public void setParametersBinds(Map<String, Map<String, String>> aValue) {
        parametersBinds = aValue;
    }

    /**
     * @return The application element identifier;
     */
    public String getEntityName() {
        return entityName;
    }

    public void setParameters(Parameters aParameters) {
        params = aParameters;
    }
}
