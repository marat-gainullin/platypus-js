/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.proto.CoreTags;

/**
 *
 * @author mg
 */
public class RequestsTags extends CoreTags {

    /**
     * ******************* requests tags *************************************
     */
    // protocol tags
    public static final int TAG_REQUEST = 1;
    public static final int TAG_REQUEST_TYPE = 2;
    public static final int TAG_REQUEST_DATA = 3;
    public static final int TAG_REQUEST_END = 4;
    //AppQueryRequest
    //ExecuteQueryRequest
    public static final int TAG_QUERY_ID = 5;
    //CommitRequest
    public static final int TAG_CHANGES = 9;
    //CreateServerModuleRequest
    //ExecuteServerModuleMethodRequest
    //SetServerModulePropertyRequest
    //GetServerModulePropertyRequest
    //ResourceRequest
    //ModuleStructureRequest
    public static final int TAG_MODULE_NAME = 10;
    public static final int TAG_METHOD_NAME = 11;
    public static final int TAG_ARGUMENT_TYPE = 12;
    public static final int TAG_ARGUMENT_VALUE = 13;
    public static final int TAG_NULL_ARGUMENT = 14;
    public static final int TAG_UNDEFINED_ARGUMENT = 15;
    //
    public static final int SCHEMA_NAME_TAG = 19;
    public static final int TABLE_NAME_TAG = 20;
    public static final int TAG_SQL_PARAMETER = 21;
    public static final int TAG_SQL_PARAMETER_TYPE = 22;
    public static final int TAG_SQL_PARAMETER_VALUE = 25;
    public static final int TAG_SQL_PARAMETER_MODE = 26;
    public static final int TAG_SQL_PARAMETER_NAME = 27;
    public static final int TAG_SQL_PARAMETER_DESCRIPTION = 28;
    /**
     * ******************* responses tags *************************************
     */
    public static final int TAG_RESPONSE = 34; //marker of an ordinary response.
    public static final int TAG_ERROR_RESPONSE = 35; //marker of a general error response.
    public static final int TAG_JSON_ERROR_RESPONSE = 136; //flag tag, indicating that JsObjectException occured on the server side.
    public static final int TAG_SQL_ERROR_RESPONSE = 137; //flag tag, indicating that JsObjectException occured on the server side.
    public static final int TAG_ACCESS_CONTROL_ERROR_RESPONSE = 138; //flag tag, indicating that JsObjectException occured on the server side.
    public static final int TAG_RESPONSE_ERROR = 36; //string inside, message of exception.
    public static final int TAG_RESPONSE_DATA = 37; //any request-specific response data as a substream.
    public static final int TAG_RESPONSE_END = 38; //empty, marks the end of a response.
    public static final int TAG_RESPONSE_SQL_ERROR_CODE = 39; //int, sql exception vendor error code (if it is SQLException).
    public static final int TAG_RESPONSE_SQL_ERROR_STATE = 40; //string, sql exception SQLState property (if it is SQLException).
    public static final int TAG_RESPONSE_ACCESS_CONTROL = 41; //flag tag, indicating that access control exception occured on the server side.
    public static final int TAG_RESPONSE_ACCESS_CONTROL_NOT_LOGGED_IN = 42;
    public static final int TAG_RESPONSE_JSON = 143;
    //StartAppElementRequest.Response
    //SetServerModulePropertyRequest.Response
    public static final int TAG_FILE_NAME = 43;
    public static final int TAG_FORMAT = 44;
    public static final int TAG_RESULT_VALUE = 45;
    //ExecuteQueryRequest.Response
    public static final int TAG_PARAMETERS = 47;
    public static final int TAG_UPDATE_COUNT = 48;
    //CreateServerModuleResuest.Reponse
    public static final int TAG_MODULE_PERMITTED = 56;
    public static final int TAG_MODULE_FUNCTION_NAMES = 57;
    public static final int TAG_MODULE_FUNCTION_NAME = 58;
    //CommitRequest.Response
    public static final int UPDATED_TAG = 59;
    //AppQueryRequest.Response
    public static final int TAG_TITLE = 60;
    public static final int TAG_DML = 61;
    public static final int TAG_FIELDS = 62;
    public static final int TAG_QUERY_SQL_PARAMETER = 63;
    public static final int TAG_READ_ROLE = 64;
    public static final int TAG_WRITE_ROLE = 65;
    //ResourceRequest
    //ResourceRequest.Response
    public static final int TAG_TIMESTAMP = 66;
    //ResourceRequest.Response
    public static final int TAG_RESOUCRE_CONTENT = 67;
    //ResourceRequest
    //ModuleStructureRequest.Response
    public static final int TAG_RESOURCE_NAME = 68;
    //ModuleStructureRequest.Response
    public static final int TAG_MODULE_CLIENT_DEPENDENCY = 69;
    public static final int TAG_MODULE_SERVER_DEPENDENCY = 70;
    public static final int TAG_MODULE_QUERY_DEPENDENCY = 71;
}
