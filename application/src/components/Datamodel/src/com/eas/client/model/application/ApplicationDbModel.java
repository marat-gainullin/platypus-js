/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.DbClient;
import com.eas.client.SQLUtils;
import com.eas.client.model.StoredQueryFactory;
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
            if (SQLUtils.getTypeGroup(type) == SQLUtils.TypesGroup.NUMBERS) // numbers
            {
                return (type == Types.NUMERIC && supportedTypes.contains(Types.NUMERIC))
                        || (type == Types.DECIMAL && supportedTypes.contains(Types.DECIMAL));
            } else if (SQLUtils.getTypeGroup(type) == SQLUtils.TypesGroup.STRINGS) // strings
            {
                return type == Types.VARCHAR && supportedTypes.contains(Types.VARCHAR);
            } else if (SQLUtils.getTypeGroup(type) == SQLUtils.TypesGroup.DATES) // dates
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

    private static final String SAVE_JSDOC = ""
            + "/**\n"
            + "* Saves model data changes.\n"
            + "* If model can't apply the changed data, than exception is thrown. In this case, application can call model.save() another time to save the changes.\n"
            + "* If an application needs to abort futher attempts and discard model data changes, use <code>model.revert()</code>.\n"
            + "* @param callback the function to be envoked after the data changes saved (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = SAVE_JSDOC, params = {"callback"})
    @Override
    public boolean save(Function aCallback) throws Exception {
        return super.save(aCallback);
    }

    @Override
    public int commit() throws Exception {
        for (List<Change> changeLog : changeLogs.values()) {
            for(Change change : changeLog){
                change.trusted = true;
            }
        }
        return client.commit(changeLogs);
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

    public void requery(Function aOnSuccess) throws Exception {
        requery(aOnSuccess, null);
    }

    private static final String REQUERY_JSDOC = ""
            + "/**\n"
            + "* Requeries the model data. Forses the model data refresh, no matter if its parameters has changed or not.\n"
            + "* @param onSuccessCallback the handler function for refresh data on success event (optional).\n"
            + "* @param onFailureCallback the handler function for refresh data on failure event (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = REQUERY_JSDOC, params = {"onSuccessCallback", "onFailureCallback"})
    @Override
    public void requery(Function aOnSuccess, Function aOnFailure) throws Exception {
        for (List<Change> changeLog : changeLogs.values()) {
            changeLog.clear();
        }
        super.requery(aOnSuccess, aOnFailure);
    }

    public List<Change> getChangeLog(String aDbId) {
        List<Change> changeLog = changeLogs.get(aDbId);
        if (changeLog == null) {
            changeLog = new ArrayList<>();
            changeLogs.put(aDbId, changeLog);
        }
        return changeLog;
    }

    public synchronized Scriptable createEntity(String aSqlText) throws Exception {
        return createEntity(aSqlText, null);
    }

    private static final String CREATE_ENTITY_JSDOC = ""
            + "/**\n"
            + "* Creates new entity of model, based on passed sql query. This method works only in two tier components of a system.\n"
            + "* @param sqlText SQL text for the new entity.\n"
            + "* @param dbId the concrete database ID (optional).\n"
            + "* @return an entity instance.\n"
            + "*/";

    @ScriptFunction(jsDoc = CREATE_ENTITY_JSDOC, params = {"sqlText", "datasourceName"})
    public synchronized Scriptable createEntity(String aSqlText, String aDatasourceName) throws Exception {
        if (client == null) {
            throw new NullPointerException("Null client detected while creating a query");
        }
        ApplicationDbEntity modelEntity = newGenericEntity();
        modelEntity.setName(USER_DATASOURCE_NAME);
        SqlQuery query = new SqlQuery(client, aDatasourceName, aSqlText);
        query.setEntityId(String.valueOf(IDGenerator.genID()));
        StoredQueryFactory factory = new StoredQueryFactory(client, client.getAppCache(), true);
        factory.putTableFieldsMetadata(query);// only select will be filled with output columns
        modelEntity.setQuery(query);
        modelEntity.prepareRowsetByQuery();
        return modelEntity.defineProperties();// .schema collection will be empty if query is not a select
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Executed sql query. This method works only in two tier components of a system.\n"
            + " */")
    public void executeSql(String aSql) throws Exception {
        executeSql(aSql, null);
    }

    private static final String EXECUTE_SQL_JSDOC = ""
            + "/**\n"
            + "* Executes a SQL query against specific datasource. This method works only in two tier components of a system.\n"
            + "* @param sqlText SQL text for the new entity.\n"
            + "* @param dbId the concrete database ID (optional).\n"
            + "* @return an entity instance.\n"
            + "*/";

    @ScriptFunction(jsDoc = EXECUTE_SQL_JSDOC, params = {"sqlText", "datasourceName"})
    public void executeSql(String aSqlClause, String aDatasourceName) throws Exception {
        if (client == null) {
            throw new NullPointerException("Null client detected while creating a query");
        }
        SqlCompiledQuery compiled = new SqlCompiledQuery(client, aDatasourceName, aSqlClause);
        client.executeUpdate(compiled);
    }
}
