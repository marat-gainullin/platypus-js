/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.DatabaseMdCache;
import com.eas.client.SQLUtils;
import com.eas.client.SqlQuery;
import com.eas.client.changes.Change;
import com.eas.client.changes.Command;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.script.ScriptFunction;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ApplicationDbEntity extends ApplicationEntity<ApplicationDbModel, SqlQuery, ApplicationDbEntity> {

    public ApplicationDbEntity() {
        super();
    }

    public ApplicationDbEntity(ApplicationDbModel aModel) {
        super(aModel);
    }

    public ApplicationDbEntity(String aEntityId) {
        super(aEntityId);
    }

    @Override
    public void accept(ModelVisitor<ApplicationDbEntity, ApplicationDbModel> visitor) {
        visitor.visit(this);
    }

    @ScriptFunction(jsDoc = EXECUTE_UPDATE_JSDOC, params = {"onSuccess", "onFailure"})
    @Override
    public int executeUpdate(JSObject onSuccess, JSObject onFailure) throws Exception {
        if (onSuccess != null) {
            model.getBasesProxy().executeUpdate(getQuery().compile(), (Integer aUpdated) -> {
                onSuccess.call(null, new Object[]{aUpdated});
            }, (Exception ex) -> {
                if (onFailure != null) {
                    onFailure.call(null, new Object[]{ex.getMessage()});
                }
            });
            return 0;
        } else {
            return model.getBasesProxy().executeUpdate(getQuery().compile(), null, null);
        }
    }

    @ScriptFunction(jsDoc = ENQUEUE_UPDATE_JSDOC)
    @Override
    public void enqueueUpdate() throws Exception {
        List<Change> log = getChangeLog();
        Command command = getQuery().compile().prepareCommand();
        log.add(command);
    }

    @Override
    public List<Change> getChangeLog() throws Exception {
        validateQuery();
        String datasourceName = tableName != null && !tableName.isEmpty() ? tableDatasourceName : query != null ? query.getDatasourceName() : null;
        return model.getChangeLog(datasourceName);
    }

    @Override
    public void validateQuery() throws Exception {
        if (query == null) {
            if (queryName != null) {
                SqlQuery q = model.queries.getCachedQuery(queryName);
                if (q != null) {
                    query = q.copy();
                }
            } else if (tableName != null) {
                try {
                    query = SQLUtils.validateTableSqlQuery(getTableDatasourceName(), getTableName(), getTableSchemaName(), model.getBasesProxy());
                    // such resolving is needed here because table queries are not processed by StoredQueryFactory
                    DatabaseMdCache mdCache = model.getBasesProxy().getDbMetadataCache(query.getDatasourceName());
                    SqlDriver driver = mdCache.getConnectionDriver();
                    TypesResolver resolver = driver.getTypesResolver();
                    query.getFields().toCollection().stream().forEach((field) -> {
                        resolver.resolve2Application(field);
                    });
                } catch (Exception ex) {
                    query = null;
                    if (ex instanceof NamingException) {
                        Logger.getLogger(ApplicationDbEntity.class.getName()).log(Level.WARNING, ex.getMessage());
                    } else {
                        Logger.getLogger(ApplicationDbEntity.class.getName()).log(Level.WARNING, null, ex);
                    }
                }
            } else {
                assert false : "Entity must have queryName or tableName to validate it's query";
            }
        }
    }
}
