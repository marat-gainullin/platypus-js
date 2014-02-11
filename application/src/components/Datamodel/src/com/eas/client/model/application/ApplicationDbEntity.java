/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.DbMetadataCache;
import com.eas.client.SQLUtils;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.sql.ParameterMetaData;
import java.util.List;

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

    public ApplicationDbEntity(String aQueryId) {
        super(aQueryId);
    }

    @Override
    protected List<Change> getChangeLog() throws Exception {
        validateQuery();
        String dbId = tableName != null ? tableDbId : query != null ? query.getDbId() : null;
        return model.getChangeLog(dbId);
    }

    @Override
    protected void refreshRowset() throws Exception {
        if (query != null) {
            SqlCompiledQuery compiled = query.compile();
            compiled.setSessionId(model.getSessionId());
            Parameters rowsetParams = compiled.getParameters();
            if (rowsetParams != null) {
                rowset.refresh(rowsetParams);
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
        }
    }

    @Override
    public void validateQuery() throws Exception {
        if (query == null) {
            if (queryId != null) {
                query = model.getClient().getAppQuery(queryId);
                query.clearRoles();
            } else if (tableName != null) {
                query = SQLUtils.validateTableSqlQuery(getTableDbId(), getTableName(), getTableSchemaName(), model.getClient());
            } else {
                assert false : "Entity must have queryId or tableName to validate it's query";
            }
            prepareRowsetByQuery();
        }
    }

    public void prepareRowsetByQuery() throws Exception {
        Rowset oldRowset = rowset;
        if (rowset != null) {
            rowset.removeRowsetListener(this);
            unforwardChangeLog();
            rowset = null;
        }
        if (query != null) {
            SqlCompiledQuery compiled = query.compile();
            compiled.setSessionId(model.getSessionId());
            rowset = compiled.prepareRowset();
            if (tableName != null && !tableName.isEmpty()) {// such resolving is needed here because table queries are not processed by StoredQueryFactory
                DbMetadataCache mdCache = model.getClient().getDbMetadataCache(query.getDbId());
                SqlDriver driver = mdCache.getConnectionDriver();
                TypesResolver resolver = driver.getTypesResolver();
                for (Field field : rowset.getFields().toCollection()) {
                    resolver.resolve2Application(field);
                }
            }
            forwardChangeLog();
            rowset.addRowsetListener(this);
            changeSupport.firePropertyChange("rowset", oldRowset, rowset);
        }
    }
}
