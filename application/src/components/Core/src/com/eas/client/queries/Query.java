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

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.Client;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

/** 
 * Abstract platypus query with parameters.
 *
 * @author mg
 * @param <T>
 */
public abstract class Query<T extends Client> {

    protected T core;
    // parameters propagation. ParamName - DsName, ParamName
    protected Map<String, Map<String, String>> parametersBinds = new HashMap<>();
    protected transient Fields fields = new Fields();
    protected transient Parameters params = new Parameters();
    protected transient String title;
    protected String datasourceName;
    protected String entityId;
    protected boolean procedure;
    protected boolean manual;
    protected Set<String> readRoles = new HashSet<>();
    protected Set<String> writeRoles = new HashSet<>();
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /** 
     * Creates an instance of Query with empty SQL query text and
     * parameters map.
     */
    protected Query() {
        super();
    }

    public Query(T aClient) {
        this();
        core = aClient;
    }

    /**
     * Copy constructor
     * @param aSource Another Query instance the data to be copied from.
     */
    protected Query(Query<T> aSource) {
        if (aSource != null) {
            String sourceDatasourceName = aSource.getDbId();
            if (sourceDatasourceName != null) {
                datasourceName = sourceDatasourceName;
            }
            procedure = aSource.isProcedure();
            manual = aSource.isManual();
            entityId = aSource.getEntityId();
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

            Set<String> sRoles = aSource.getReadRoles();
            if (sRoles != null) {
                readRoles = new HashSet<>();
                for (String sRole : sRoles) {
                    readRoles.add(new String(sRole.toCharArray()));
                }
            }

            sRoles = aSource.getWriteRoles();
            if (sRoles != null) {
                writeRoles = new HashSet<>();
                for (String sRole : sRoles) {
                    writeRoles.add(new String(sRole.toCharArray()));
                }
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
            core = aSource.getClient();
        }
    }

    public abstract Query copy();

    public Set<String> getReadRoles() {
        return readRoles;
    }

    public void setReadRoles(Set<String> aRoles) {
        readRoles = aRoles;
    }

    public Set<String> getWriteRoles() {
        return writeRoles;
    }

    public void setWriteRoles(Set<String> aRoles) {
        writeRoles = aRoles;
    }

    public T getClient() {
        return core;
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    /**
     * Returns whether this query is stored procedure call.
     * @return True if this query is stored procedure call.
     */
    public boolean isProcedure() {
        return procedure;
    }

    /**
     * Sets procedure flag.
     * @param aValue 
     */
    public void setProcedure(boolean aValue) {
        boolean oldValue = procedure;
        procedure = aValue;
        changeSupport.firePropertyChange("procedure", oldValue, procedure);
    }

    /**
     * Returns whether this query is executed manually.
     * Such execution way effictive with data manipulation (INSERT, UPDATE, DELETE) queries
     * and some others.
     * @return True if this query is data manipulation query.
     */
    public boolean isManual() {
        return manual;
    }

    /**
     * Sets manual flag.
     * @param aValue 
     */
    public void setManual(boolean aValue) {
        boolean oldValue = manual;
        manual = aValue;
        changeSupport.firePropertyChange("manual", oldValue, manual);
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
        param.setName(aName);
        param.setTypeInfo(aTypeInfo.copy());
        param.setDefaultValue(aValue);
        param.setValue(aValue);
    }

    /*
    public void putParameter(String aName, int aType, Object aValue) {
    if (params == null) {
    params = new Parameters();
    }
    Parameter param = params.getApplicationElement(aName);
    if (param == null) {
    param = new Parameter();
    params.add(param);
    }
    param.setName(aName.toUpperCase());
    param.getTypeInfo().setSqlType(aType);
    param.setDefaultValue(aValue);
    param.setValue(aValue);
    }
     */
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
        param.getTypeInfo().setSqlType(aType);
        param.setDefaultValue(aDefaultValue);
        param.setValue(aValue);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String aValue) {
        String oldValue = title;
        title = aValue;
        changeSupport.firePropertyChange("title", oldValue, title);
    }

    public Map<String, Map<String, String>> getParametersBinds() {
        return parametersBinds;
    }

    public void setParametersBinds(Map<String, Map<String, String>> aValue) {
        parametersBinds = aValue;
    }

    public abstract Rowset execute(Consumer<Rowset> onSuccess, Consumer<Exception> onFailure) throws Exception;
    
    //public abstract void enqueueUpdate() throws Exception;
    
    /**
     * @return the datasourceName
     */
    public String getDbId() {
        return datasourceName;
    }

    /**
     * @param aValue A datasourceName to set to the squery.
     */
    public void setDbId(String aValue) {
        String oldValue = datasourceName;
        datasourceName = aValue;
        changeSupport.firePropertyChange("dbId", oldValue, datasourceName);
    }

    /**
     * @return The application element identifier;
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * @param aValue the entityId to set
     */
    public void setEntityId(String aValue) {
        entityId = aValue;
    }
}
