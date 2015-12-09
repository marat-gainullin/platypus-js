/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.changes.JdbcChangeValue;
import com.eas.client.dataflow.JdbcFlowProvider;
import com.eas.client.dataflow.JdbcReader;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.queries.ContextHost;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.script.Scripts;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * This flow provider implements transaction capability for standard
 * JdbcFlowProvider. It enqueues changes in transactional queue instead of
 * actual writing to underlying database. It relies on transactional assumption:
 * all enqueued changes will be actually applied at commmit or reverted at
 * rollback.
 *
 * @author mg
 */
public class PlatypusJdbcFlowProvider extends JdbcFlowProvider<String> {

    protected String entityName;
    protected DatabasesClient client;
    protected MetadataCache cache;
    protected SqlDriver sqlDriver;
    protected ContextHost contextHost;

    public PlatypusJdbcFlowProvider(DatabasesClient aClient, String aDataSourceName, String aEntityName, DataSource aDataSource, Consumer<Runnable> aDataPuller, MetadataCache aCache, String aClause, Fields aExpectedFields, ContextHost aContextHost) throws Exception {
        super(aDataSourceName, aDataSource, aDataPuller, aClause, aExpectedFields);
        entityName = aEntityName;
        client = aClient;
        cache = aCache;
        sqlDriver = cache.getDatasourceSqlDriver();
        contextHost = aContextHost;
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    protected JdbcReader obtainJdbcReader() {
        return new JdbcReader(expectedFields, (Wrapper aRsultSetOrCallableStatement, int aColumnIndex, Connection aConnection) -> {
            return sqlDriver.readGeometry(aRsultSetOrCallableStatement, aColumnIndex, aConnection);
        }, (int aJdbcType, String aRDBMSType) -> {
            return sqlDriver.getTypesResolver().toApplicationType(aJdbcType, aRDBMSType);
        });
    }
    
    @Override
    protected int assignParameter(Parameter aParameter, PreparedStatement aStatement, int aParameterIndex, Connection aConnection) throws SQLException {
        if (Scripts.GEOMETRY_TYPE_NAME.equals(aParameter.getType())) {
            try {
                JdbcChangeValue jv = sqlDriver.convertGeometry(aParameter.getValue().toString(), aConnection);
                Object paramValue = jv.value;
                int jdbcType = jv.jdbcType;
                String sqlTypeName = jv.sqlTypeName;
                int assignedJdbcType = assign(paramValue, aParameterIndex, aStatement, jdbcType, sqlTypeName);
                checkOutParameter(aParameter, aStatement, aParameterIndex, jdbcType);
                return assignedJdbcType;
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        } else {
            return super.assignParameter(aParameter, aStatement, aParameterIndex, aConnection);
        }
    }

    @Override
    protected void acceptOutParameter(Parameter aParameter, CallableStatement aStatement, int aParameterIndex, Connection aConnection) throws SQLException {
        if (Scripts.GEOMETRY_TYPE_NAME.equals(aParameter.getType())) {
            try {
                String sGeometry = sqlDriver.readGeometry(aStatement, aParameterIndex, aConnection);
                aParameter.setValue(sGeometry);
            } catch (Exception ex) {
                Logger.getLogger(PlatypusJdbcFlowProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            super.acceptOutParameter(aParameter, aStatement, aParameterIndex, aConnection);
        }
    }

    @Override
    protected void prepareConnection(Connection aConnection) throws Exception {
        if (contextHost != null && contextHost.preparationContext() != null && !contextHost.preparationContext().isEmpty()) {
            cache.getDatasourceSqlDriver().applyContextToConnection(aConnection, contextHost.preparationContext());
        }
    }

    @Override
    protected void unprepareConnection(Connection aConnection) throws Exception {
        // In the following condition, _PR_eparation context is checked. It's right, because we need to cancel _PR_eparation
        // if it has been made. And so, condition checks a _PR_eparation context, but _UN_preparation context is applied.
        // If no preparation has been made, no unpreparation should occur!
        if (contextHost != null && contextHost.preparationContext() != null && !contextHost.preparationContext().isEmpty()) {
            cache.getDatasourceSqlDriver().applyContextToConnection(aConnection, contextHost.unpreparationContext());
        }
    }
}
