/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.rt;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.AppCache;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.login.PrincipalHost;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.util.ListenerRegistration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mg
 */
public class DummyTestDbClient implements DbClient{

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int executeUpdate(SqlCompiledQuery scq) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int commit(Map<String, List<Change>> aLogs) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rollback() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AppCache getAppCache() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DbMetadataCache getDbMetadataCache(String l) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SqlQuery getAppQuery(String l) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void appEntityChanged(String l) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dbTableChanged(String l, String string, String string1) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Rowset getDbTypesInfo(String l) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public PrincipalHost getPrincipalHost() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getConnectionSchema(String l) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getConnectionDialect(String l) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getStartAppElement() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListenerRegistration addTransactionListener(TransactionListener tl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FlowProvider createFlowProvider(String string, String string1, String string2, Fields fields, Set<String> set, Set<String> set1) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListenerRegistration addQueriesListener(QueriesListener ql) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SqlDriver getConnectionDriver(String string) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
}
