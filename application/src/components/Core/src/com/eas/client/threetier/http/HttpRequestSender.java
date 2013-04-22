/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.ResponsesReceiver;
import com.eas.client.threetier.binary.PlatypusRequestWriter;
import com.eas.client.threetier.binary.PlatypusResponseReader;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.client.threetier.requests.*;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoReaderException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kl
 */
public class HttpRequestSender implements PlatypusRequestVisitor {

    public static final String RESPONSE_MISSING_MSG = "%s must have a response.";
    protected PlatypusHttpConnection conn;

    public HttpRequestSender(PlatypusHttpConnection aConnection) {
        super();
        conn = aConnection;
    }

    public void enqueueRequest(Request rq) throws Exception {
        setupCommonParams(rq);
        conn.connect();
        if (PlatypusHttpConstants.HTTP_METHOD_POST.equals(conn.getMethod())) {
            writeRequestBody(rq);
        }
    }

    private void waitRequestCompletion(Request rq) throws Exception {
        if (conn.getHttpConnection().getResponseCode() == HttpURLConnection.HTTP_OK) {
            if (conn.getHttpConnection().getContentLength() > 0) {
                Long requestId = null;
                byte[] responseData = null;
                boolean errorResponse = false;
                InputStream is = conn.getHttpConnection().getInputStream();
                ProtoReader reader = new ProtoReader(new BufferedInputStream(is));
                try {
                    int tag = -1;
                    while (tag != RequestsTags.TAG_RESPONSE_END) {
                        tag = reader.getNextTag();
                        switch (tag) {
                            case RequestsTags.TAG_RESPONSE:
                                requestId = reader.getLong();
                                break;
                            case RequestsTags.TAG_ERROR_RESPONSE:
                                requestId = reader.getLong();
                                errorResponse = true;
                                break;
                            case RequestsTags.TAG_RESPONSE_DATA:
                                responseData = reader.getSubStreamData();
                                break;
                            case RequestsTags.TAG_RESPONSE_END:
                                respond(rq, responseData, errorResponse, "");
                                requestId = null;
                                responseData = null;
                                errorResponse = false;
                                break;
                        }
                    }
                } catch (ProtoReaderException ex) {
                    Logger.getLogger(ResponsesReceiver.class.getName()).log(Level.SEVERE, "Error reading response on request " + requestId, ex);
                    respond(rq, null, true, ex.getMessage());
                    requestId = null;
                    responseData = null;
                    errorResponse = false;
                }
            }
            conn.acceptCookies();
        } else {
            Logger.getLogger(ResponsesReceiver.class.getName()).log(Level.SEVERE, String.format("Server error %d. %s: ", conn.getHttpConnection().getResponseCode(), conn.getHttpConnection().getResponseMessage()));
            respond(rq, null, true, conn.getHttpConnection().getResponseCode() + " " + conn.getHttpConnection().getResponseMessage());
        }
    }

    private void writeRequestBody(Request rq) throws Exception {
        OutputStream connOutStream = conn.getHttpConnection().getOutputStream();
        rq.accept(new PlatypusRequestWriter(connOutStream));
    }

    /**
     * Установка базовых параметров запроса id и type
     *
     * @param rq запрос
     */
    private void setupCommonParams(Request rq) {
        conn.putParam(PlatypusHttpRequestParams.ID, rq.getID());
        conn.putParam(PlatypusHttpRequestParams.TYPE, rq.getType());
    }

    /**
     * Отправка запроса и получение ответа
     *
     * @param rq запрос
     * @throws Exception
     */
    private void execute(Request rq) throws Exception {
        enqueueRequest(rq);
        waitRequestCompletion(rq);
        conn.disconnect();
    }

    private void respond(Request request, byte[] responseData, boolean aError, String aDefaultErrorMessage) throws Exception {
        if (request != null) {
            Response response;
            if (aError) {
                response = new ErrorResponse(request.getID(), aDefaultErrorMessage);
            } else {
                PlatypusResponsesFactory responseFactory = new PlatypusResponsesFactory(request.getID());
                request.accept(responseFactory);
                response = responseFactory.getResponse();
            }
            PlatypusResponseReader responseReader = new PlatypusResponseReader(responseData);
            response.accept(responseReader);
            synchronized (request) {
                request.setResponse(response);
                request.setDone(true);
                request.notifyAll();
            }
        } else {
            Logger.getLogger(HttpRequestSender.class.getName()).log(Level.INFO, "Got response for unknown request {0}", request.getID());
        }
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        conn.putParam(PlatypusHttpRequestParams.QUERY_ID, rq.getQueryId());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(AppElementChangedRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        conn.putParam(PlatypusHttpRequestParams.DATABASE_ID, rq.getDatabaseId());
        conn.putParam(PlatypusHttpRequestParams.QUERY_ID, rq.getEntityId());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_POST);
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        conn.putParam(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(DbTableChangedRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        conn.putParam(PlatypusHttpRequestParams.DATABASE_ID, rq.getDatabaseId());
        conn.putParam(PlatypusHttpRequestParams.SCHEMA, rq.getSchema());
        conn.putParam(PlatypusHttpRequestParams.TABLE, rq.getTable());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        conn.putParam(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_POST);
        conn.putParam(PlatypusHttpRequestParams.QUERY_ID, rq.getQueryId());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_POST);
        conn.putParam(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName());
        conn.putParam(PlatypusHttpRequestParams.METHOD_NAME, rq.getMethodName());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(KeepAliveRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        execute(rq);
    }

    @Override
    public void visit(HelloRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        execute(rq);
    }

    @Override
    public void visit(LoginRequest rq) throws Exception {
        conn.authenticate(rq.getLogin(), rq.getPassword());
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        execute(rq);
    }

    @Override
    public void visit(OutHashRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        conn.putParam(PlatypusHttpRequestParams.LOGIN, rq.getUserName());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(StartAppElementRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(IsUserInRoleRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        conn.putParam(PlatypusHttpRequestParams.ROLE_NAME, rq.getRoleName());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(IsAppElementActualRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        conn.putParam(PlatypusHttpRequestParams.ENTITY_ID, rq.getAppElementId());
        conn.putParam(PlatypusHttpRequestParams.TEXT_CONTENT_SIZE, rq.getTxtContentSize());
        conn.putParam(PlatypusHttpRequestParams.TEXT_CONTENT_CRC32, rq.getTxtContentCrc32());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }

    @Override
    public void visit(AppElementRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_GET);
        conn.putParam(PlatypusHttpRequestParams.ENTITY_ID, rq.getAppElementId());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }
   
    @Override
    public void visit(ExecuteServerReportRequest rq) throws Exception {
        conn.setMethod(PlatypusHttpConstants.HTTP_METHOD_POST);
        conn.putParam(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName());
        execute(rq);
        assert rq.getResponse() != null : String.format(RESPONSE_MISSING_MSG, rq.getClass().getSimpleName());
    }
}
