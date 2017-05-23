package com.eas.client.model.application;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.StoredQueryFactory;
import com.eas.client.changes.Change;
import com.eas.client.model.Model;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.QueriesProxy;
import com.eas.script.ScriptFunction;
import com.eas.util.IdGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ApplicationDbModel extends ApplicationModel<ApplicationDbEntity, SqlQuery> {

    protected Map<String, List<Change>> changeLogs = new HashMap<>();
    protected DatabasesClient basesProxy;

    public ApplicationDbModel(QueriesProxy<SqlQuery> aQueries) {
        super(aQueries);
    }

    public ApplicationDbModel(DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aQueries) {
        this(aQueries);
        basesProxy = aBasesProxy;
    }

    @Override
    public <M extends Model<ApplicationDbEntity, ?>> void accept(ModelVisitor<ApplicationDbEntity, M> visitor) {
        visitor.visit((M) this);
    }

    public DatabasesClient getBasesProxy() {
        return basesProxy;
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
    private static final String MODIFIED_JSDOC = ""
            + "/**\n"
            + " * Flag is set to true if model has been modified.\n"
            + " */";

    @ScriptFunction(jsDoc = MODIFIED_JSDOC)
    @Override
    public boolean isModified() throws Exception {
        return changeLogs.values().stream().anyMatch((List<Change> aLog) -> {
            return !aLog.isEmpty();
        });
    }

    @Override
    public int commit(Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Map<String, List<Change>> logs = changeLogs;
        changeLogs = new HashMap<>();
        // Change logs are cleared unconditionaly because of
        // compliance of synchronous and asynchronous cases with errors while commit in mind.
        return basesProxy.commit(logs, onSuccess, onFailure);
    }

    @Override
    public void commited() {
        super.commited();
    }

    @ScriptFunction(jsDoc = REVERT_JSDOC)
    @Override
    public void revert() {
        changeLogs.values().stream().forEach((changeLog) -> {
            changeLog.clear();
        });
        super.revert();
    }

    @ScriptFunction(jsDoc = REQUERY_JSDOC, params = {"onSuccess", "onFailure"})
    @Override
    public void requery(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        changeLogs.values().stream().forEach((changeLog) -> {
            changeLog.clear();
        });
        super.requery(aOnSuccess, aOnFailure);
    }

    public List<Change> getChangeLog(String aDatasourceName) {
        String datasourceName = aDatasourceName;
        // basesProxy.getDefaultDatasourceName() is needed here to avoid multi transaction
        // actions against the same datasource, leading to unexpected
        // row-level locking and deadlocks in two phase transaction commit process
        if (datasourceName == null || datasourceName.isEmpty()) {
            datasourceName = basesProxy.getDefaultDatasourceName();
        }
        List<Change> changeLog = changeLogs.get(datasourceName);
        if (changeLog == null) {
            changeLog = new ArrayList<>();
            changeLogs.put(datasourceName, changeLog);
        }
        return changeLog;
    }

    public void forEachChange(Consumer<Change> aActor) {
        changeLogs.entrySet().stream().forEach((Map.Entry<String, List<Change>> aEntry) -> {
            aEntry.getValue().stream().forEach(aActor);
        });
    }

    public ApplicationDbEntity createEntity(String aSqlText) throws Exception {
        return createEntity(aSqlText, null);
    }

    private static final String CREATE_ENTITY_JSDOC = ""
            + "/**\n"
            + " * Creates new entity of model, based on passed sql query. This method works only in two tier components of a system.\n"
            + " * @param sqlText SQL text for the new entity.\n"
            + " * @param datasourceName the concrete database ID (optional).\n"
            + " * @return an entity instance.\n"
            + " */";

    @ScriptFunction(jsDoc = CREATE_ENTITY_JSDOC, params = {"sqlText", "datasourceName"})
    public ApplicationDbEntity createEntity(String aSqlText, String aDatasourceName) throws Exception {
        if (basesProxy == null) {
            throw new NullPointerException("null basesProxy detected while creating a query");
        }
        ApplicationDbEntity created = newGenericEntity();
        created.setName(USER_DATASOURCE_NAME);
        SqlQuery query = new SqlQuery(basesProxy, aDatasourceName, aSqlText);
        query.setEntityName(IdGenerator.genStringId());
        StoredQueryFactory factory = new StoredQueryFactory(basesProxy, null, null);
        factory.putTableFieldsMetadata(query);// only select will be filled with output columns
        created.setQuery(query);
        // .schema collection will be empty if query is not a select
        return created;
    }

    public void executeSql(String aSql) throws Exception {
        executeSql(aSql, null);
    }

    private static final String EXECUTE_SQL_JSDOC = ""
            + "/**\n"
            + " * Executes a SQL query against specific datasource. This method works only in two tier components of a system.\n"
            + " * @param sqlText SQL text for the new entity.\n"
            + " * @param datasourceName. The specific databsource name (optional).\n"
            + " * @param onSuccess Success callback. Have a number argument, indicating updated rows count (optional).\n"
            + " * @param onFailure Failure callback. Have a string argument, indicating an error occured (optional).\n"
            + " * @return an entity instance.\n"
            + " */";

    @ScriptFunction(jsDoc = EXECUTE_SQL_JSDOC, params = {"sqlText", "datasourceName"})
    public void executeSql(String aSqlClause, String aDatasourceName, JSObject onSuccess, JSObject onFailure) throws Exception {
        if (basesProxy == null) {
            throw new NullPointerException("Null basesProxy detected while creating a query");
        }
        SqlCompiledQuery compiled = new SqlCompiledQuery(basesProxy, aDatasourceName, aSqlClause);
        if (onSuccess != null) {
            basesProxy.executeUpdate(compiled, (Integer updated) -> {
                onSuccess.call(null, new Object[]{updated});
            }, (Exception ex) -> {
                if (onFailure != null) {
                    onFailure.call(null, new Object[]{ex.getMessage()});
                }
            });
        } else {
            basesProxy.executeUpdate(compiled, null, null);
        }
    }

    public void executeSql(String aSqlClause, String aDatasourceName, JSObject onSuccess) throws Exception {
        executeSql(aSqlClause, aDatasourceName, onSuccess, null);
    }

    public void executeSql(String aSqlClause, String aDatasourceName) throws Exception {
        executeSql(aSqlClause, aDatasourceName, null, null);
    }
}
