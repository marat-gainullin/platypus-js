/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.binary;

import com.eas.proto.CoreTags;

/**
 *
 * @author mg
 */
public class RequestsTags extends CoreTags{

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
    //LoginRequest
    public static final int TAG_LOGIN = 6;
    public static final int TAG_PASSWORD = 7;
    public static final int TAG_SESSION_TO_RESTORE = 8;
    //CommitRequest
    public static final int TAG_CHANGES = 9;
    //CreateServerModuleRequest
    //ExecuteServerModuleMethodRequest
    //SetServerModulePropertyRequest
    //GetServerModulePropertyRequest
    public static final int TAG_MODULE_NAME = 10;
    public static final int TAG_METHOD_NAME = 11;
    public static final int TAG_ARGUMENT_TYPE = 12;
    public static final int TAG_ARGUMENT_VALUE = 13;
    public static final int TAG_NULL_ARGUMENT = 14;
    public static final int TAG_UNDEFINED_ARGUMENT = 15;
    //SetServerModulePropertyRequest
    //GetServerModulePropertyRequest
    public static final int TAG_PROPERTY_NAME = 16;
    //DbTableChangedRequest
    //AppElementChangedRequest
    public static final int DATABASE_TAG = 17;
    //AppElementChangedRequest
    public static final int ENTITY_ID_TAG = 18;
    //
    public static final int SCHEMA_NAME_TAG = 19;
    public static final int TABLE_NAME_TAG = 20;
    public static final int TAG_SQL_PARAMETER = 21;
    public static final int TAG_SQL_PARAMETER_TYPE = 22;
    public static final int TAG_SQL_PARAMETER_TYPE_NAME = 23;
    public static final int TAG_SQL_PARAMETER_TYPE_CLASS_NAME = 24;
    public static final int TAG_SQL_PARAMETER_VALUE = 25;
    public static final int TAG_SQL_PARAMETER_MODE = 26;
    public static final int TAG_SQL_PARAMETER_NAME = 27;
    public static final int TAG_SQL_PARAMETER_DESCRIPTION = 28;
    //OutHashRequest
    public static final int TAG_USERNAME = 29;
    //IsUserInRoleRequest
    public static final int TAG_ROLE_NAME = 30;
    //AppElementRequest
    //IsAppElementActualRequest
    public static final int TAG_APP_ELEMENT_ID = 31;
    //IsAppElementActualRequest
    public static final int TAG_TEXT_SIZE = 32;
    public static final int TAG_TEST_CRC32 = 33;
    //ExecuteServerReportRequest
    public static final int TAG_ARGUMENT_NAME = 34;
    /**
     * ******************* responses tags *************************************
     */
    public static final int TAG_RESPONSE = 34; //long inside, id of request, marks the start of a response.
    public static final int TAG_ERROR_RESPONSE = 35; //long inside, id of request, marks the start of a response.
    public static final int TAG_RESPONSE_ERROR = 36; //string inside, message of exception.
    public static final int TAG_RESPONSE_DATA = 37; //any request-specific response data as a substream.
    public static final int TAG_RESPONSE_END = 38; //empty, marks the end of a response.
    public static final int TAG_RESPONSE_SQL_ERROR_CODE = 39; //int, sql exception vendor error code (if it is SQLException).
    public static final int TAG_RESPONSE_SQL_ERROR_STATE = 40; //string, sql exception SQLState property (if it is SQLException).
    public static final int TAG_RESPONSE_ACCESS_CONTROL = 41; //flag tag, indicating that access control exception occured on the server side.
    //StartAppElementRequest.Response
    //SetServerModulePropertyRequest.Response
    public static final int TAG_RESULT_TYPE = 42;
    public static final int TAG_NULL_RESULT = 43;
    public static final int TAG_RESULT_VALUE = 44;
    public static final int TAG_UNDEFINED_RESULT = 45;
    public static final int TAG_FORMAT = 46;
    //RowsetResponse
    public static final int TAG_ROWSET = 46;
    public static final int TAG_PARAMETERS = 47;
    public static final int TAG_UPDATE_COUNT = 48;
    //OutHashRequest.Response
    public static final int TAG_RESULT_CODE = 49;
    public static final int TAG_RESULT_ERROR_STR = 50;
    //LoginRequest.Response
    public static final int TAG_SESSION_ID = 51;
    //IsUserInRoleRequest.Response
    public static final int TAG_ROLE = 52;
    //IsAppElementActualRequest.Response
    public static final int TAG_ACTUAL = 53;
    //GetServerModulePropertyRequest.Response
    public static final int TAG_FUNCTION_RESULT = 54;
    //ExecuteServerReportRequest.Response
    //CreateServerModuleResponse
    public static final int TAG_MODULE_ID = 55;
    //CommitRequest.Response
    public static final int UPDATED_TAG = 56;
    //AppQueryResponse
    public static final int TAG_TITLE = 57;
    public static final int TAG_DML = 58;
    public static final int TAG_FIELDS = 59;
    public static final int TAG_QUERY_SQL_PARAMETER = 60;
    public static final int TAG_READ_ROLE = 61;
    public static final int TAG_WRITE_ROLE = 62;
    //AppElementRequest.Response
    public static final int TAG_NAME = 63;
    public static final int TAG_TYPE = 64;
    public static final int TAG_TEXT = 65;
    public static final int TAG_TEXT_LENGTH = 66;
    public static final int TAG_TEXT_CRC32 = 67;
    public static final int TAG_RESOURCE = 68;
}
