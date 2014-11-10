/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.DatabaseMdCache;
import com.eas.client.SQLUtils;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.sql.ParameterMetaData;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;
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

    private static final String EXECUTE_UPDATE_JSDOC = ""
            + "/**\n"
            + "* Applies the updates into the database and commits the transaction.\n"
            + "* @param onSuccess Success callback. It has an argument, - updates rows count.\n"
            + "* @param onFailure Failure callback. It has an argument, - exception occured while applying updates into the database.\n"
            + "*/";

    @ScriptFunction(jsDoc = EXECUTE_UPDATE_JSDOC)
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

    private static final String ENQUEUE_UPDATE_JSDOC = ""
            + "/**\n"
            + "* Adds the updates into the change log as a command.\n"
            + "*/";

    @ScriptFunction(jsDoc = ENQUEUE_UPDATE_JSDOC)
    @Override
    public void enqueueUpdate() throws Exception {
        List<Change> log = getChangeLog();
        Command command = getQuery().compile().prepareCommand();
        log.add(command);
    }

    @Override
    protected List<Change> getChangeLog() throws Exception {
        validateQuery();
        String datasourceName = tableName != null && !tableName.isEmpty() ? tableDatasourceName : query != null ? query.getDatasourceName() : null;
        return model.getChangeLog(datasourceName);
    }

    @Override
    protected void refreshRowset(final Consumer<Void> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        SqlCompiledQuery compiled = query.compile();
        Parameters rowsetParams = compiled.getParameters();
        if (rowsetParams != null) {
            if (model.process != null || aOnSuccess != null) {
                Future<Void> f = new RowsetRefreshTask(aOnFailure);
                rowset.refresh(rowsetParams, (Rowset aRowset) -> {
                    if (!f.isCancelled()) {
                        valid = true;
                        pending = null;
                        pullOutParameters(rowsetParams);
                        model.terminateProcess(ApplicationDbEntity.this, null);
                        if (aOnSuccess != null) {
                            aOnSuccess.accept(null);
                        }
                    }
                }, (Exception ex) -> {
                    if (!f.isCancelled()) {
                        valid = true;
                        pending = null;
                        model.terminateProcess(ApplicationDbEntity.this, ex);
                        if (aOnFailure != null) {
                            aOnFailure.accept(ex);
                        }
                    }
                });
                pending = f;
            } else {
                rowset.refresh(rowsetParams, null, null);
                pullOutParameters(rowsetParams);
            }
        }
    }

    private void pullOutParameters(Parameters rowsetParams) {
        if (query.isProcedure()) {
            for (int i = 1; i <= rowsetParams.getParametersCount(); i++) {
                Parameter param = rowsetParams.get(i);
                if (param.getMode() == ParameterMetaData.parameterModeOut
                        || param.getMode() == ParameterMetaData.parameterModeInOut) {
                    Parameter innerParam = query.getParameters().get(param.getName());
                    if (innerParam != null) {
                        innerParam.setValue(param.getValue());
                    }
                }
            }
        }
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
            prepareRowsetByQuery();
        }
    }

    @Override
    public void prepareRowsetByQuery() throws Exception {
        Rowset oldRowset = rowset;
        if (rowset != null) {
            rowset.removeRowsetListener(this);
            unforwardChangeLog();
            rowset = null;
        }
        if (query != null) {
            SqlCompiledQuery compiled = query.compile();
            rowset = compiled.prepareRowset();
            if (tableName != null && !tableName.isEmpty()) {// such resolving is needed here because table queries are not processed by StoredQueryFactory
                DatabaseMdCache mdCache = model.getBasesProxy().getDbMetadataCache(query.getDatasourceName());
                SqlDriver driver = mdCache.getConnectionDriver();
                TypesResolver resolver = driver.getTypesResolver();
                rowset.getFields().toCollection().stream().forEach((field) -> {
                    resolver.resolve2Application(field);
                });
            }
            forwardChangeLog();
            rowset.addRowsetListener(this);
            changeSupport.firePropertyChange("rowset", oldRowset, rowset);
        }
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
