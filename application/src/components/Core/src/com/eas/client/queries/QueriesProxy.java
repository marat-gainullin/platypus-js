/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.queries;

import java.util.function.Consumer;

/**
 *
 * @author mg
 * @param <Q>
 */
public interface QueriesProxy <Q extends Query> {
    
    public Q getQuery(String aName, Consumer<Q> onSuccess, Consumer<Exception> onFailure) throws Exception;
    
    public Q getCachedQuery(String aName);
}
