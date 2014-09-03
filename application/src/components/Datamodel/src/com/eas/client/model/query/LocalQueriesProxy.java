/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.query;

import com.eas.client.AppElementFiles;
import com.eas.client.DbClient;
import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.SqlQuery;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class LocalQueriesProxy implements QueriesProxy<SqlQuery> {

    protected Map<String, ActualCacheEntry<SqlQuery>> entries = new ConcurrentHashMap<>();
    protected StoredQueryFactory factory;
    protected ApplicationSourceIndexer indexer;

    public LocalQueriesProxy(DbClient aCore, String aAppPathName) throws Exception {
        this(aCore, new ApplicationSourceIndexer(aAppPathName));
    }

    public LocalQueriesProxy(DbClient aCore, ApplicationSourceIndexer aIndexer) throws Exception {
        super();
        indexer = aIndexer;
        factory = new StoredQueryFactory(aCore, this, indexer);
    }

    @Override
    public SqlQuery getQuery(String aName, Consumer<SqlQuery> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Callable<SqlQuery> doWork = () -> {
            SqlQuery query;
            Date cachedTimeStamp = null;
            ActualCacheEntry<SqlQuery> entry = entries.get(aName);
            if (entry != null) {
                cachedTimeStamp = entry.getTimeStamp();
            }
            AppElementFiles files = indexer.nameToFiles(aName);
            Date filesTimeStamp = files.getLastModified();
            if (cachedTimeStamp == null || filesTimeStamp.after(cachedTimeStamp)) {
                query = factory.loadQuery(aName);
                entries.put(aName, new ActualCacheEntry<>(query, filesTimeStamp));
            } else {
                assert entry != null;
                query = entry.getValue();
            }
            return query;
        };
        if (onSuccess != null) {
            try {
                SqlQuery query = doWork.call();
                try {
                    onSuccess.accept(query);
                } catch (Exception ex) {
                    Logger.getLogger(LocalQueriesProxy.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {
                if (onFailure != null) {
                    onFailure.accept(ex);
                }
            }
            return null;
        } else {
            return doWork.call();
        }
    }

}
