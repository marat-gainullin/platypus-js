/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.DbClient;
import com.eas.client.SQLUtils;
import com.eas.client.model.RowsetMissingException;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.script.ScriptFunction;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class ApplicationDbModel extends ApplicationModel<ApplicationDbEntity, ApplicationDbParametersEntity, DbClient, SqlQuery> {

    protected String sessionId;
    protected Map<String, List<Change>> changeLogs = new HashMap<>();

    public ApplicationDbModel() {
        super();
        parametersEntity = new ApplicationDbParametersEntity(this);
    }

    public ApplicationDbModel(DbClient aClient) {
        this();
        setClient(aClient);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String aSessionId) {
        sessionId = aSessionId;
    }

    @Override
    public ApplicationDbEntity newGenericEntity() {
        return new ApplicationDbEntity(this);
    }

    @Override
    public void addEntity(ApplicationDbEntity aEntity) {
        aEntity.setModel(this);
        super.addEntity(aEntity);
    }

    @Override
    public void setParametersEntity(ApplicationDbParametersEntity aParamsEntity) {
        super.setParametersEntity(aParamsEntity);
        parametersEntity.setModel(this);
    }

    /**
     * Method checks if the type is supported for datamodel's internal usage.
     * The types are fields or parameters types. If the type is reported as
     * unsupported by this method, it doesn't mean that the type is unsupported
     * in our pltypus system at all. It means only that platypus application
     * designer will not be able to add fields or parameters of such types.
     *
     * @param type - the type to check.
     * @return true if the type is supported for datamodel's internal usage.
     */
    @Override
    public boolean isTypeSupported(int type) throws Exception {
        SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();
        Set<Integer> supportedTypes = driver.getSupportedJdbcDataTypes();
        if (SQLUtils.isTypeSupported(type)) {
            if (SQLUtils.isSameTypeGroup(type, Types.NUMERIC)) // numbers
            {
                return (type == Types.NUMERIC && supportedTypes.contains(Types.NUMERIC))
                        || (type == Types.DECIMAL && supportedTypes.contains(Types.DECIMAL));
            } else if (SQLUtils.isSameTypeGroup(type, Types.VARCHAR)) // strings
            {
                return type == Types.VARCHAR && supportedTypes.contains(Types.VARCHAR);
            } else if (SQLUtils.isSameTypeGroup(type, Types.DATE)) // dates
            {
                return (type == Types.DATE && supportedTypes.contains(Types.DATE))
                        || (type == Types.TIMESTAMP && supportedTypes.contains(Types.TIMESTAMP));
            } else if (SQLUtils.isSimpleTypesCompatible(type, Types.BLOB)) // large objects
            {
                return (type == Types.BLOB && supportedTypes.contains(Types.BLOB))
                        || (type == Types.CLOB && supportedTypes.contains(Types.CLOB));
            } else if (SQLUtils.isSimpleTypesCompatible(type, Types.BIT)) // logical
            {
                return type == Types.BOOLEAN && supportedTypes.contains(Types.BOOLEAN);
            } else if (SQLUtils.isSimpleTypesCompatible(type, Types.STRUCT)) // aggregating type
            {
                return type == Types.STRUCT && supportedTypes.contains(Types.STRUCT);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @ScriptFunction(jsDocText = "Saves model data changes. Calls aCallback when done."
    + "If model can't apply the changed, than exception is thrown. "
    + "In this case, application can call model.save() another time to save the changes. "
    + "If an application need to abort futher attempts and discard model data changes, "
    + "than it can call model.revert().")
    @Override
    public boolean save(Function aCallback) throws Exception {
        for (String dbId : changeLogs.keySet()) {
            client.getChangeLog(dbId, sessionId).addAll(changeLogs.get(dbId));
        }
        return super.save(aCallback);
    }

    @Override
    public int commit() throws Exception {
        if (commitable) {
            return client.commit(sessionId);
        } else {
            return 0;
        }
    }

    @Override
    public void saved() throws Exception {
        for (List<Change> changeLog : changeLogs.values()) {
            changeLog.clear();
        }
        fireCommited();
    }

    @Override
    public void revert() throws Exception {
        for (List<Change> changeLog : changeLogs.values()) {
            changeLog.clear();
        }
        fireReverted();
    }

    @Override
    public void rolledback() throws Exception {
    }

    @ScriptFunction(jsDocText = "Requeries model data with callback.")
    @Override
    public void requery(Function aCallback) throws Exception {
        for (List<Change> changeLog : changeLogs.values()) {
            changeLog.clear();
        }
        super.requery(aCallback);
    }

    public List<Change> getChangeLog(String aDbId) {
        List<Change> changeLog = changeLogs.get(aDbId);
        if (changeLog == null) {
            changeLog = new ArrayList<>();
            changeLogs.put(aDbId, changeLog);
        }
        return changeLog;
    }

    public synchronized Scriptable createQuery(String aDbId, String aSqlText, String aMainTable) throws Exception {
        if (client == null) {
            throw new NullPointerException("Null client detected while creating a query");
        }
        ApplicationDbEntity entity = newGenericEntity();
        entity.setName(USER_DATASOURCE_NAME);
        SqlQuery query = new SqlQuery(client, aDbId, aSqlText);
        if (aMainTable != null) {
            query.setEntityId(aMainTable);
        }
        if (query.getFields() == null || query.getFields().isEmpty()) {
            SqlCompiledQuery compiled = query.compileMetadataQuery();
            try {
                Rowset rs = compiled.executeQuery();
                query.setFields(rs.getFields());
            } catch (Exception ex) {
                query.setFields(null);
            }
        }
        entity.setQuery(query);
        addEntity(entity);
        return entity.defineProperties();
    }

    public synchronized Scriptable createQuery(String aDbId, String aSqlText) throws RowsetMissingException, Exception {
        return createQuery(aDbId, aSqlText, (String) null);
    }

    public synchronized Scriptable createQuery(String aDbId, String aSqlText, Parameters aParams) throws Exception {
        if (client == null) {
            throw new NullPointerException("Null client detected while creating a query");
        }
        ApplicationDbEntity entity = newGenericEntity();
        entity.setName(USER_DATASOURCE_NAME);
        SqlQuery query = new SqlQuery(client, aDbId, aSqlText);
        for (int i = 1; i <= aParams.getParametersCount(); i++) {
            Parameter param = aParams.get(i);
            query.putParameter(param.getName(), param.getTypeInfo(), param.getValue());
        }
        if (query.getFields() == null || query.getFields().isEmpty()) {
            SqlCompiledQuery compiled = query.compileMetadataQuery();
            try {
                Rowset rs = compiled.executeQuery();
                query.setFields(rs.getFields());
            } catch (Exception ex) {
                query.setFields(null);
            }
        }
        entity.setQuery(query);
        addEntity(entity);
        return entity.defineProperties();
    }
}
