/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.json.ChangesJSONWriter;
import com.eas.client.changes.Change;
import com.eas.client.Application;
import com.eas.client.ModulesProxy;
import com.eas.client.RemoteModulesProxy;
import com.eas.client.RemoteServerModulesProxy;
import com.eas.client.ServerModulesProxy;
import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.cache.ServerDataStorage;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.RemoteQueriesProxy;
import com.eas.client.threetier.requests.*;
import com.eas.script.Scripts;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusClient implements Application<PlatypusQuery>, ServerDataStorage {

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
    protected ScriptsConfigs securityConfigs;
    protected FormsDocuments forms;
    protected ReportsConfigs reports;
    protected ModelsDocuments models;

    public PlatypusClient(PlatypusConnection aConn) throws Exception {
        super();
        url = aConn.getUrl();
        conn = aConn;
        queries = new RemoteQueriesProxy(aConn, this);
        modules = new RemoteModulesProxy(aConn);
        serverModulesProxy = new RemoteServerModulesProxy(aConn);
        securityConfigs = new ScriptsConfigs();
        forms = new FormsDocuments();
        reports = new ReportsConfigs();
        models = new ModelsDocuments();
    }

    public PlatypusConnection getConn() {
        return conn;
    }

    @Override
    public Type getType() {
        return Type.CLIENT;
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
    public ScriptsConfigs getScriptsConfigs() {
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

    @Override
    public int commit(List<Change> aLog, Scripts.Space aSpace, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        String changesJson = ChangesJSONWriter.write(aLog);
        CommitRequest request = new CommitRequest(changesJson);
        if (onSuccess != null) {
            conn.<CommitRequest.Response>enqueueRequest(request, aSpace, (CommitRequest.Response aResponse) -> {
                onSuccess.accept(aResponse.getUpdated());
            }, (Exception aException) -> {
                if (onFailure != null) {
                    onFailure.accept(aException);
                }
            });
            return 0;
        } else {
            CommitRequest.Response response = conn.executeRequest(request);
            return response.getUpdated();
        }
    }

    public void shutdown() {
        if (conn != null) {
            conn.shutdown();
            conn = null;
        }
    }
}
