package com.eas.client.queries;

import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.script.Scripts;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.Map;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Abstract platypus query with parameters.
 *
 * @author mg
 */
public abstract class Query {

    // parameters propagation. ParamName - DsName, ParamName
    protected Map<String, Map<String, String>> parametersBinds = new HashMap<>();
    protected transient Fields fields = new Fields();
    protected transient Parameters params = new Parameters();
    protected transient String title;
    protected String entityName;
    protected boolean procedure;
    protected Set<String> readRoles = new HashSet<>();
    protected Set<String> writeRoles = new HashSet<>();
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    protected Query() {
        super();
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
                Set<Map.Entry<String, Map<String, String>>> lentries = lparametersBinds.entrySet();
                if (lentries != null) {
                    Iterator<Map.Entry<String, Map<String, String>>> entIt = lentries.iterator();
                    if (entIt != null) {
                        while (entIt.hasNext()) {
                            Map.Entry<String, Map<String, String>> lent = entIt.next();
                            if (lent != null) {
                                String parName = lent.getKey();
                                if (parName != null && !parName.isEmpty()) {
                                    Map<String, String> lParValue = lent.getValue();
                                    if (lParValue != null) {
                                        Set<Map.Entry<String, String>> lpEntries = lParValue.entrySet();
                                        if (lpEntries != null) {
                                            Iterator<Map.Entry<String, String>> lpEntIt = lpEntries.iterator();
                                            if (lpEntIt != null) {
                                                Map<String, String> lparamBinds = new HashMap<>();
                                                parametersBinds.put(new String(parName.toCharArray()), lparamBinds);
                                                while (lpEntIt.hasNext()) {
                                                    Map.Entry<String, String> lpEnt = lpEntIt.next();
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

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
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
        boolean oldValue = procedure;
        procedure = aValue;
        changeSupport.firePropertyChange("procedure", oldValue, procedure);
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
        param.setName(aName);
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

    public abstract JSObject execute(Scripts.Space aSpace, Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception;

    public abstract boolean isMetadataAccessible();
    
    /**
     * @return The application element identifier;
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param aValue the entityName to set
     */
    public void setEntityName(String aValue) {
        entityName = aValue;
    }
}
