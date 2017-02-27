/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

/**
 *
 * @author kl
 */
public class PlatypusHttpRequestParams {

    public static final String QUERY_ID = "__queryId";
    public static final String TYPE = "__type";
    public static final String MODULE_NAME = "__moduleName";
    public static final String METHOD_NAME = "__methodName";
    public static final String PARAMS_ARRAY = "__param[]";
    
    public static boolean isSystemParameter(String aName){
        return QUERY_ID.equalsIgnoreCase(aName)
                || TYPE.equalsIgnoreCase(aName)
                || MODULE_NAME.equalsIgnoreCase(aName)
                || METHOD_NAME.equalsIgnoreCase(aName)
                || PARAMS_ARRAY.equalsIgnoreCase(aName);
    }
}
