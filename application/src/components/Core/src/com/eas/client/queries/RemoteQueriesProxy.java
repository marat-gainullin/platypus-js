/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.json.QueryJSONReader;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.script.Scripts;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class RemoteQueriesProxy implements QueriesProxy<PlatypusQuery> {

    protected PlatypusConnection conn;
    protected PlatypusClient core;
    protected Map<String, ActualCacheEntry<PlatypusQuery>> entries = new ConcurrentHashMap<>();

    public RemoteQueriesProxy(PlatypusConnection aConn, PlatypusClient aCore) {
        super();
        conn = aConn;
        core = aCore;
    }

    @Override
    public PlatypusQuery getQuery(String aName, Scripts.Space aSpace, Consumer<PlatypusQuery> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Date localTimeStamp = null;
        ActualCacheEntry<PlatypusQuery> entry = entries.get(aName);
        if (entry != null) {
            localTimeStamp = entry.getTimeStamp();
        }
        AppQueryRequest request = new AppQueryRequest(aName, localTimeStamp);
        if (onSuccess != null) {
            conn.<AppQueryRequest.Response>enqueueRequest(request, aSpace, (AppQueryRequest.Response aResponse) -> {
                if (aResponse.getAppQueryJson() != null) {
                    PlatypusQuery query = QueryJSONReader.read((JSObject) aSpace.parseJson(aResponse.getAppQueryJson()));
                    query.setServerProxy(core);
                    assert aName.equals(query.getEntityName());
                    entries.put(aName, new ActualCacheEntry<>(query, aResponse.getTimeStamp()));
                    onSuccess.accept(query);
                } else {
                    assert entry != null : NEITHER_QUERY_DATA;
                    onSuccess.accept(entry.getValue());
                }
            }, onFailure);
            return null;
        } else {
            AppQueryRequest.Response response = conn.executeRequest(request);
            if (response.getAppQueryJson() != null) {
                PlatypusQuery query = QueryJSONReader.read((JSObject) aSpace.parseJson(response.getAppQueryJson()));
                query.setServerProxy(core);
                assert aName.equals(query.getEntityName());
                entries.put(aName, new ActualCacheEntry<>(query, response.getTimeStamp()));
                return query;
            } else {
                assert entry != null : NEITHER_QUERY_DATA;
                return entry.getValue();

            }
        }
    }

    @Override
    public PlatypusQuery getCachedQuery(String aName) {
        ActualCacheEntry<PlatypusQuery> entry = entries.get(aName);
        if (entry != null) {
            return entry.getValue();
        } else {
            return null;
        }
    }

    private static final String NEITHER_QUERY_DATA = "Neither cached, nor network response query found";

}
