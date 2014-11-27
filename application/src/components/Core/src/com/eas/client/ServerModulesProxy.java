/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ServerModulesProxy {

    protected PlatypusConnection conn;
    protected Map<String, ActualCacheEntry<ServerModuleInfo>> entries = new ConcurrentHashMap<>();

    public ServerModulesProxy(PlatypusConnection aConn) {
        super();
        conn = aConn;
        conn.setOnLogin(() -> {
            entries.clear();
        });
        conn.setOnLogout(() -> {
            entries.clear();
        });
    }

    public ServerModuleInfo getCachedStructure(String aName) throws Exception {
        ActualCacheEntry<ServerModuleInfo> entry = entries.get(aName);
        if (entry != null) {
            return entry.getValue();
        } else {
            return null;
        }
    }

    public ServerModuleInfo getServerModuleStructure(String aName, Consumer<ServerModuleInfo> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Date localTimeStamp = null;
        ActualCacheEntry<ServerModuleInfo> entry = entries.get(aName);
        if (entry != null) {
            localTimeStamp = entry.getTimeStamp();
        }
        CreateServerModuleRequest request = new CreateServerModuleRequest(aName, localTimeStamp);
        if (onSuccess != null) {
            conn.enqueueRequest(request, (CreateServerModuleRequest.Response response) -> {
                ServerModuleInfo info = response.getInfo();
                if (info != null) {
                    entries.put(aName, new ActualCacheEntry<>(info, response.getTimeStamp()));
                    onSuccess.accept(info);
                } else {
                    assert entry != null : NEITHER_SM_INFO;
                    onSuccess.accept(entry.getValue());
                }
            }, onFailure);
            return null;
        } else {
            CreateServerModuleRequest.Response response = conn.executeRequest(request);
            ServerModuleInfo info = response.getInfo();
            if (info != null) {
                entries.put(aName, new ActualCacheEntry<>(info, response.getTimeStamp()));
                return info;
            } else {
                assert entry != null : NEITHER_SM_INFO;
                return entry.getValue();
            }
        }
    }
    private static final String NEITHER_SM_INFO = "Neither cached nor network response server module info found";

    public Object callServerModuleMethod(String aModuleName, String aMethodName, JSObject onSuccess, JSObject onFailure, Object... aArguments) throws Exception {
        if (onSuccess != null) {
            executeServerModuleMethod(aModuleName, aMethodName, (Object aResult) -> {
                onSuccess.call(null, new Object[]{aResult});
            }, (Exception ex) -> {
                if (onFailure != null) {
                    onFailure.call(null, new Object[]{ex.getMessage()});
                }
            }, aArguments);
            return null;
        } else {
            return executeServerModuleMethod(aModuleName, aMethodName, null, null, aArguments);
        }
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
}
