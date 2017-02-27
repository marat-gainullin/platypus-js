/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.report.Report;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.client.threetier.requests.ServerModuleStructureRequest;
import com.eas.script.JsObjectException;
import com.eas.script.Scripts;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class RemoteServerModulesProxy implements ServerModulesProxy{

    public static final String LENGTH_PROP_NAME = "length";
    public static final String CREATE_MODULE_RESPONSE_FUNCTIONS_PROP = "functions";
    public static final String CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP = "isPermitted";
    private static final String NEITHER_SM_INFO = "Neither cached nor network server module info found";

    protected Map<String, ActualCacheEntry<ServerModuleInfo>> entries = new ConcurrentHashMap<>();
    protected PlatypusConnection conn;

    public RemoteServerModulesProxy(PlatypusConnection aConn) {
        super();
        conn = aConn;
    }
    
    @Override
    public ServerModuleInfo getCachedStructure(String aName) throws Exception {
        ActualCacheEntry<ServerModuleInfo> entry = entries.get(aName);
        if (entry != null) {
            return entry.getValue();
        } else {
            return null;
        }
    }
    
    @Override
    public ServerModuleInfo getServerModuleStructure(String aName, Scripts.Space aSpace, Consumer<ServerModuleInfo> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Date localTimeStamp = null;
        ActualCacheEntry<ServerModuleInfo> entry = entries.get(aName);
        if (entry != null) {
            localTimeStamp = entry.getTimeStamp();
        }
        ServerModuleStructureRequest request = new ServerModuleStructureRequest(aName, localTimeStamp);
        if (onSuccess != null) {
            conn.enqueueRequest(request, aSpace, (ServerModuleStructureRequest.Response response) -> {
                String infoJson = response.getInfoJson();
                if (infoJson != null) {
                    ServerModuleInfo info = readInfo(aName, (JSObject)aSpace.parseJson(infoJson));
                    entries.put(aName, new ActualCacheEntry<>(info, response.getTimeStamp()));
                    onSuccess.accept(info);
                } else {
                    assert entry != null : NEITHER_SM_INFO;
                    onSuccess.accept(entry.getValue());
                }
            }, (Exception ex) -> {
                onFailure.accept(ex);
            });
            return null;
        } else {
            ServerModuleStructureRequest.Response response = conn.executeRequest(request);
            String infoJson = response.getInfoJson();
            if (infoJson != null) {
                ServerModuleInfo info = readInfo(aName, (JSObject)aSpace.parseJson(infoJson));
                entries.put(aName, new ActualCacheEntry<>(info, response.getTimeStamp()));
                return info;
            } else {
                assert entry != null : NEITHER_SM_INFO;
                return entry.getValue();
            }
        }
    }

    private ServerModuleInfo readInfo(String aModuleName, JSObject jsProxy) {
        Set<String> functions = new HashSet<>();
        JSObject jsFunctions = (JSObject) jsProxy.getMember(CREATE_MODULE_RESPONSE_FUNCTIONS_PROP);
        int length = JSType.toInteger(jsFunctions.getMember(LENGTH_PROP_NAME));
        for (int i = 0; i < length; i++) {
            String fName = JSType.toString(jsFunctions.getSlot(i));
            functions.add(fName);
        }
        boolean permitted = JSType.toBoolean(jsProxy.getMember(CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP));
        return new ServerModuleInfo(aModuleName, functions, permitted);
    }
    
    @Override
    public Object callServerModuleMethod(String aModuleName, String aMethodName, Scripts.Space aSpace, JSObject onSuccess, JSObject onFailure, Object... aArguments) throws Exception {
        if (onSuccess != null) {
            executeServerModuleMethod(aModuleName, aMethodName, aSpace, (Object aResult) -> {
                onSuccess.call(null, new Object[]{aResult});
            }, (Exception ex) -> {
                if (onFailure != null) {
                    onFailure.call(null, new Object[]{ex instanceof JsObjectException ? ((JsObjectException)ex).getData()  : ex.getMessage()});
                }
            }, aArguments);
            return null;
        } else {
            return executeServerModuleMethod(aModuleName, aMethodName, aSpace, null, null, aArguments);
        }
    }

    public Object executeServerModuleMethod(String aModuleName, String aMethodName, Scripts.Space aSpace, Consumer<Object> onSuccess, Consumer<Exception> onFailure, Object... aArguments) throws Exception {
        String[] argumentsJsons = new String[aArguments.length];
        for (int i = 0; i < argumentsJsons.length; i++) {
            argumentsJsons[i] = aSpace.toJson(aArguments[i]);
        }
        final RPCRequest request = new RPCRequest(aModuleName, aMethodName, argumentsJsons);
        if (onSuccess != null) {
            conn.<RPCRequest.Response>enqueueRequest(request, aSpace, (RPCRequest.Response aResponse) -> {
                Object sResult = adoptRPCResponse(aResponse, aSpace);
                onSuccess.accept(sResult);
            }, onFailure);
            return null;
        } else {
            RPCRequest.Response response = conn.executeRequest(request);
            Object sResult = adoptRPCResponse(response, aSpace);
            return sResult;
        }
    }

    private Object adoptRPCResponse(RPCRequest.Response aResponse, Scripts.Space aSpace) {
        Object sResult;
        Object rpcResult = aResponse.getResult();
        if(rpcResult instanceof String){
            sResult = aSpace.parseJsonWithDates((String)rpcResult);
        }else if(rpcResult instanceof Report){
            Report report = (Report)rpcResult;
            sResult = report.getPublished();
        }else{
            sResult = rpcResult;
        }
        return sResult;
    }
}
