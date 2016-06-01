/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.MetadataCache;
import com.eas.client.SQLUtils;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.changes.Change;
import com.eas.client.changes.Command;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
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
            model.getBasesProxy().executeUpdate(getQuery().compile(Scripts.getSpace()), (Integer aUpdated) -> {
                onSuccess.call(null, new Object[]{aUpdated});
            }, (Exception ex) -> {
                if (onFailure != null) {
                    onFailure.call(null, new Object[]{ex.getMessage()});
                }
            });
            return 0;
        } else {
            return model.getBasesProxy().executeUpdate(getQuery().compile(Scripts.getSpace()), null, null);
        }
    }

    @ScriptFunction(jsDoc = UPDATE_JSDOC, params = {"params", "onSuccess", "onFailure"})
    @Override
    public int update(JSObject aParams, JSObject onSuccess, JSObject onFailure) throws Exception {
        SqlQuery copied = query.copy();
        aParams.keySet().forEach((String pName) -> {
            Parameter p = copied.getParameters().get(pName);
            if (p != null) {
                Object jsValue = aParams.getMember(pName);
                p.setValue(Scripts.getSpace().toJava(jsValue));
            }
        });
        SqlCompiledQuery compiled = copied.compile();
        if (onSuccess != null) {
            model.getBasesProxy().executeUpdate(compiled, (Integer aUpdated) -> {
                onSuccess.call(null, new Object[]{aUpdated});
            }, (Exception ex) -> {
                if (onFailure != null) {
                    onFailure.call(null, new Object[]{ex.getMessage()});
                }
            });
            return 0;
        } else {
            return model.getBasesProxy().executeUpdate(compiled, null, null);
        }
    }

    @ScriptFunction(jsDoc = ENQUEUE_UPDATE_JSDOC, params = {"params"})
    @Override
    public void enqueueUpdate(JSObject aParams) throws Exception {
        SqlQuery copied = query.copy();
        if (aParams != null) {
            aParams.keySet().forEach((String pName) -> {
                Parameter p = copied.getParameters().get(pName);
                if (p != null) {
                    Object jsValue = aParams.getMember(pName);
                    // .toJava() call is inside compile().
                    p.setValue(jsValue);
                }
            });
        }
        // WARNING! Don't change copied.compile(Scripts.getSpace()) to copied.compile().
        // Not all parameters may be metioned in aParams object, so entity.params will be taken partially.
        // aParams argument may be omitted, and so, all parameters will be taken from entity.params.
        SqlCompiledQuery compiled = copied.compile(Scripts.getSpace());
        Command command = compiled.prepareCommand();
        List<Change> log = getChangeLog();
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
                    MetadataCache mdCache = model.getBasesProxy().getMetadataCache(query.getDatasourceName());
                    SqlDriver driver = mdCache.getDatasourceSqlDriver();
                    TypesResolver resolver = driver.getTypesResolver();
                    query.getFields().toCollection().stream().forEach((field) -> {
                        field.setType(resolver.toApplicationType(((JdbcField) field).getJdbcType(), field.getType()));
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
