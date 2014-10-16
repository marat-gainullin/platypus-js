/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only.
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.queries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.exceptions.InvalidFieldsExceptionException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.application.AppClient;
import com.eas.client.application.WebFlowProvider;

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
    protected String title = null;
    protected String entityName;
    protected boolean procedure = false;
    protected boolean manual = false;
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
            manual = aSource.isManual();
            entityName = aSource.getEntityId();
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

    public void setEntityId(String aValue) {
        entityName = aValue;
        createFlow();
    }

    public Rowset prepareRowset() throws InvalidFieldsExceptionException{
    	Rowset rowset = new Rowset(createFlow());
        rowset.setFields(fields);
        return rowset;
    }
    
    public Change prepareCommand() throws Exception {
		Command command = new Command(entityName);
		command.parameters = new Change.Value[params.getParametersCount()];
		for (int i = 0; i < command.parameters.length; i++) {
			Parameter p = params.get(i + 1);
			command.parameters[i] = new Change.Value(p.getName(), p.getValue(), p.getTypeInfo());
		}
		return command;
    }

    /**
     * Merges some minimum of information on fields, because server is
     * responsible on full resolving, like comments, primary and foreign keys
     * and correct types, including geometries. This method does last time
     * tricks, such as primary keys on key-less (synthetic, view and so on)
     * rowsets. May be this method will do something else in future.
     *
     * @param destFields Fields to be merged with etalon fields.
     * @param sourceFields Etalon fields, likely a query fields, got from
     * server.
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

    /**
     * @return True if model avoid to execute this query automatically.
     */
    public boolean isManual() {
        return manual;
    }

    /**
     * Sets dml flag.
     *
     * @param aValue
     */
    public void setManual(boolean aValue) {
        manual = aValue;
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

    public void putParameter(String aName, DataTypeInfo aTypeInfo, Object aValue) {
        if (params == null) {
            params = new Parameters();
        }
        Parameter param = params.get(aName);
        if (param == null) {
            param = new Parameter();
            params.add(param);
        }
        param.setName(aName.toUpperCase());
        param.setTypeInfo(aTypeInfo.copy());
        param.setDefaultValue(aValue);
        param.setValue(aValue);
    }

    public void putParameter(String aName, int aType, Object aDefaultValue, Object aValue) {
        if (params == null) {
            params = new Parameters();
        }
        Parameter param = params.get(aName);
        if (param == null) {
            param = new Parameter();
            params.add(param);
        }
        param.setName(aName);
        param.getTypeInfo().setType(aType);
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
    public String getEntityId() {
        return entityName;
    }

    public void setParameters(Parameters aParameters) {
        params = aParameters;
    }
}
