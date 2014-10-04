/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.ChangeValue;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.Application;
import com.eas.client.ModulesProxy;
import com.eas.client.RemoteModulesProxy;
import com.eas.client.ServerModulesProxy;
import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptSecurityConfigs;
import com.eas.client.cache.ServerDataStorage;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.RemoteQueriesProxy;
import com.eas.client.threetier.requests.*;
import com.eas.util.ListenerRegistration;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusClient implements Application<PlatypusQuery>, ServerDataStorage{

    // error messages
    public static final String ENQUEUEING_UPDATES_THREE_TIER_MSG = "Enqueueing updates are not allowed in three tier mode.";
    public static final String EXECUTING_UPDATES_THREE_TIER_MSG = "Executing updates are not allowed in three tier mode.";
    public static final String SQL_TEXT_PROVIDERS_ARE_NOT_ALLOWED_MSG = "Sql query text based flow providers are not allowed in three tier mode.";
    //
    protected URL url;
    protected PlatypusConnection conn;
    protected QueriesProxy<PlatypusQuery> queries;
    protected ModulesProxy modules;
    protected ServerModulesProxy serverModulesProxy;
    protected ScriptSecurityConfigs securityConfigs;
    protected FormsDocuments forms;
    protected ReportsConfigs reports;
    protected ModelsDocuments models;
    protected List<Change> changeLog = new ArrayList<>();
    protected Set<TransactionListener> transactionListeners = new HashSet<>();

    public PlatypusClient(PlatypusConnection aConn) throws Exception {
        super();
        url = aConn.getUrl();
        conn = aConn;
        queries = new RemoteQueriesProxy(aConn, this);
        modules = new RemoteModulesProxy(aConn);
        serverModulesProxy = new ServerModulesProxy(aConn);
        securityConfigs = new ScriptSecurityConfigs();
        forms = new FormsDocuments();
        reports = new ReportsConfigs();
        models = new ModelsDocuments();
    }

    @Override
    public QueriesProxy<PlatypusQuery> getQueries() {
        return queries;
    }

    @Override
    public ModulesProxy getModules() {
        return modules;
    }

    @Override
    public ServerModulesProxy getServerModules() {
        return serverModulesProxy;
    }
    
    @Override
    public ScriptSecurityConfigs getSecurityConfigs() {
        return securityConfigs;
    }

    @Override
    public ModelsDocuments getModels() {
        return models;
    }

    @Override
    public ReportsConfigs getReports() {
        return reports;
    }

    @Override
    public FormsDocuments getForms() {
        return forms;
    }

    public URL getUrl() {
        return url;
    }

    public ListenerRegistration addTransactionListener(final TransactionListener aListener) {
        transactionListeners.add(aListener);
        return () -> {
            transactionListeners.remove(aListener);
        };
    }

    public List<Change> getChangeLog() {
        return changeLog;
    }

    public FlowProvider createFlowProvider(String aQueryId, Fields aExpectedFields) {
        return new PlatypusFlowProvider(this, conn, aQueryId, aExpectedFields);
    }

    public Object executeServerModuleMethod(String aModuleName, String aMethodName, Consumer<Object> onSuccess, Consumer<Exception> onFailure, Object... aArguments) throws Exception {
        final ExecuteServerModuleMethodRequest request = new ExecuteServerModuleMethodRequest(aModuleName, aMethodName, aArguments);
        if (onSuccess != null) {
            conn.<ExecuteServerModuleMethodRequest.Response>enqueueRequest(request, (ExecuteServerModuleMethodRequest.Response aResponse) -> {
                onSuccess.accept(aResponse.getResult());
            }, onFailure);
            return null;
        } else {
            ExecuteServerModuleMethodRequest.Response response = conn.executeRequest(request);
            return response.getResult();
        }
    }

    public int commit(Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Runnable doWork = () -> {
            changeLog.clear();
            for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
                try {
                    l.commited();
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        CommitRequest request = new CommitRequest(changeLog);
        if (onSuccess != null) {
            conn.<CommitRequest.Response>enqueueRequest(request, (CommitRequest.Response aResponse) -> {
                doWork.run();
                onSuccess.accept(aResponse.getUpdated());
            }, (Exception aException) -> {
                rollback();
                if (onFailure != null) {
                    onFailure.accept(aException);
                }
            });
            return 0;
        } else {
            try {
                CommitRequest.Response response = conn.executeRequest(request);
                doWork.run();
                return response.getUpdated();
            } catch (Exception ex) {
                rollback();
                throw ex;
            }
        }
    }

    protected void rollback() {
        try {
            changeLog.clear();
            for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
                try {
                    l.rolledback();
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logout(Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        LogoutRequest request = new LogoutRequest();
        if (onSuccess != null) {
            conn.<LogoutRequest.Response>enqueueRequest(request, (LogoutRequest.Response aResponse) -> {
                onSuccess.accept(null);
            }, (Exception aException) -> {
                if (onFailure != null) {
                    onFailure.accept(aException);
                }
            });
        } else {
            conn.executeRequest(request);
        }
    }

    public void shutdown() {
        if (conn != null) {
            conn.shutdown();
            conn = null;
        }
    }

    @Override
    public void enqueueUpdate(String aQueryName, Parameters aParams) throws Exception {
        Command command = new Command(aQueryName);
        command.parameters = new ChangeValue[aParams.getParametersCount()];
        for (int i = 0; i < command.parameters.length; i++) {
            Parameter p = aParams.get(i + 1);
            command.parameters[i] = new ChangeValue(p.getName(), p.getValue(), p.getTypeInfo());
        }
        changeLog.add(command);
    }
}
