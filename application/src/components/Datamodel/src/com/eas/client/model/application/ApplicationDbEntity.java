/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.SQLUtils;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.sqldrivers.SqlDriver;
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
    protected void achieveOrRefreshRowset() throws Exception {
        if (query != null) {
            SqlCompiledQuery compiledQuery = query.compile();
            compiledQuery.setSessionId(model.getSessionId());
            Parameters queryParameters = compiledQuery.getParameters();
            if (queryParameters != null) {
                if (rowset == null) {
                    // The first time we obtain a rowset...
                    rowset = compiledQuery.executeQuery();
                    forwardChangeLog();
                    rowset.addRowsetListener(this);
                    changeSupport.firePropertyChange("rowset", null, rowset);
                    rowset.getRowsetChangeSupport().fireRequeriedEvent();
                } else {
                    rowset.refresh(queryParameters);
                }
                if (query.isProcedure()) {
                    for (int i = 1; i <= queryParameters.getParametersCount(); i++) {
                        Parameter param = queryParameters.get(i);
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
            } else if (tableName != null) {
                query = SQLUtils.validateTableSqlQuery(getTableDbId(), getTableName(), getTableSchemaName(), model.getClient());
            } else {
                assert false;
            }
            if (query != null) {
                Fields queryFields = query.getFields();
                if (queryFields != null) {
                    SqlDriver driver = model.getClient().getDbMetadataCache(query.getDbId()).getConnectionDriver();
                    for (Field field : queryFields.toCollection()) {
                        driver.getTypesResolver().resolve2Application(field);
                    }
                }
            }
        }
    }
}
