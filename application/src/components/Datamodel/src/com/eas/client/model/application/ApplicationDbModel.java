/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.StoredQueryFactory;
import com.eas.client.queries.QueriesProxy;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ApplicationDbModel extends ApplicationModel<ApplicationDbEntity, ApplicationDbParametersEntity, SqlQuery> {

    protected Map<String, List<Change>> changeLogs = new HashMap<>();
    protected DatabasesClient basesProxy;

    public ApplicationDbModel(QueriesProxy<SqlQuery> aQueries) {
        super(aQueries);
        parametersEntity = new ApplicationDbParametersEntity(this);
    }

    public ApplicationDbModel(DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aQueries) {
        this(aQueries);
        basesProxy = aBasesProxy;
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

    @Override
    public void setParametersEntity(ApplicationDbParametersEntity aParamsEntity) {
        super.setParametersEntity(aParamsEntity);
        parametersEntity.setModel(this);
    }

    @Override
    public int commit(Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        return basesProxy.commit(changeLogs, onSuccess, onFailure);
    }

    @Override
    public void commited() {
        changeLogs.values().stream().forEach((changeLog) -> {
            changeLog.clear();
        });
        super.commited();
    }

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
        List<Change> changeLog = changeLogs.get(aDatasourceName);
        if (changeLog == null) {
            changeLog = new ArrayList<>();
            changeLogs.put(aDatasourceName, changeLog);
        }
        return changeLog;
    }

    public synchronized ApplicationDbEntity createEntity(String aSqlText) throws Exception {
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
    public synchronized ApplicationDbEntity createEntity(String aSqlText, String aDatasourceName) throws Exception {
        if (basesProxy == null) {
            throw new NullPointerException("Null basesProxy detected while creating a query");
        }
        ApplicationDbEntity modelEntity = newGenericEntity();
        modelEntity.setName(USER_DATASOURCE_NAME);
        SqlQuery query = new SqlQuery(basesProxy, aDatasourceName, aSqlText);
        query.setEntityId(String.valueOf(IDGenerator.genID()));
        StoredQueryFactory factory = new StoredQueryFactory(basesProxy, null, true);
        factory.putTableFieldsMetadata(query);// only select will be filled with output columns
        modelEntity.setQuery(query);
        modelEntity.prepareRowsetByQuery();
        // .schema collection will be empty if query is not a select
        return modelEntity;
    }

    public void executeSql(String aSql) throws Exception {
        executeSql(aSql, null);
    }

    private static final String EXECUTE_SQL_JSDOC = ""
            + "/**\n"
            + "* Executes a SQL query against specific datasource. This method works only in two tier components of a system.\n"
            + "* @param sqlText SQL text for the new entity.\n"
            + "* @param datasourceName. The specific databsource name (optional).\n"
            + "* @param onSuccess Success callback. Have a number argument, indicating updated rows count (optional).\n"
            + "* @param onFailure Failure callback. Have a string argument, indicating an error occured (optional).\n"
            + "* @return an entity instance.\n"
            + "*/";

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

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
