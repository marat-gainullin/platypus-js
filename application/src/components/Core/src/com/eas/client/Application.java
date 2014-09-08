/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.Query;

/**
 *
 * @author mg
 * @param <Q>
 */
public interface Application<Q extends Query> {

    public QueriesProxy<Q> getQueries();

    public ModulesProxy getModules();

    public ServerModulesProxy getServerModules();
}
