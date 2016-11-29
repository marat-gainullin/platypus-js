/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.Query;

/**
 *
 * @author mg
 * @param <Q>
 */
public interface Application<Q extends Query> {

    public static enum Type {
        CLIENT,
        TSA,
        SERVLET
    }

    public Type getType();

    public QueriesProxy<Q> getQueries();

    public ModulesProxy getModules();

    public ServerModulesProxy getServerModules();

    public ModelsDocuments getModels();

    public FormsDocuments getForms();

    public ReportsConfigs getReports();

    public ScriptsConfigs getScriptsConfigs();
}
