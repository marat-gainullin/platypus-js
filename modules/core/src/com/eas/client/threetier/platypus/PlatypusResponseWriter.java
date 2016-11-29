/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.report.Report;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.AccessControlExceptionResponse;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.ServerModuleStructureRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExceptionResponse;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.client.threetier.requests.JsonExceptionResponse;
import com.eas.client.threetier.requests.SqlExceptionResponse;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author mg
 */
public class PlatypusResponseWriter implements PlatypusResponseVisitor {

    public static final int COMPRESS_TOLERANCE = 1024 * 2;// 2KB
    public static final String ZIP_ENTRY_NAME = "responseData";
    protected OutputStream out;

    public PlatypusResponseWriter(OutputStream aOut) {
        super();
        out = aOut;
    }

    public static void write(Response response, ProtoWriter writer) throws Exception {
        if (response instanceof SqlExceptionResponse) {
            writer.put(RequestsTags.TAG_SQL_ERROR_RESPONSE);
        } else if (response instanceof JsonExceptionResponse) {
            writer.put(RequestsTags.TAG_JSON_ERROR_RESPONSE);
        } else if (response instanceof AccessControlExceptionResponse) {
            writer.put(RequestsTags.TAG_ACCESS_CONTROL_ERROR_RESPONSE);
        } else if (response instanceof ExceptionResponse) {
            writer.put(RequestsTags.TAG_ERROR_RESPONSE);
        } else {
            writer.put(RequestsTags.TAG_RESPONSE);
        }
        writer.put(RequestsTags.TAG_RESPONSE_DATA);
        ByteArrayOutputStream subOut = new ByteArrayOutputStream();
        PlatypusResponseWriter responseWriter = new PlatypusResponseWriter(subOut);
        response.accept(responseWriter);
        byte[] subOutBytes = subOut.toByteArray();
        if (subOutBytes.length > COMPRESS_TOLERANCE) {
            ByteArrayOutputStream zSubOut = new ByteArrayOutputStream();
            try (ZipOutputStream zStream = new ZipOutputStream(zSubOut)) {
                ZipEntry ze = new ZipEntry(ZIP_ENTRY_NAME);
                zStream.putNextEntry(ze);
                zStream.write(subOutBytes);
                zStream.flush();
            }
            writer.put(CoreTags.TAG_COMPRESSED_STREAM, zSubOut.toByteArray());
        } else {
            writer.put(CoreTags.TAG_STREAM, subOutBytes);
        }
        writer.put(RequestsTags.TAG_RESPONSE_END);
        writer.flush();
    }

    @Override
    public void visit(ExceptionResponse rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_RESPONSE_ERROR, rsp.getErrorMessage());
        writer.flush();
    }

    @Override
    public void visit(SqlExceptionResponse rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_RESPONSE_ERROR, rsp.getErrorMessage());
        assert rsp.getSqlErrorCode() != null || rsp.getSqlState() != null : "SqlExceptionResponse without sqlCode and sqlState detected";
        if (rsp.getSqlErrorCode() != null) {
            writer.put(RequestsTags.TAG_RESPONSE_SQL_ERROR_CODE, rsp.getSqlErrorCode());
        }
        if (rsp.getSqlState() != null) {
            writer.put(RequestsTags.TAG_RESPONSE_SQL_ERROR_STATE, rsp.getSqlState());
        }
        writer.flush();
    }

    @Override
    public void visit(AccessControlExceptionResponse rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_RESPONSE_ERROR, rsp.getErrorMessage());
        writer.put(RequestsTags.TAG_RESPONSE_ACCESS_CONTROL);
        if (rsp.isNotLoggedIn()) {
            writer.put(RequestsTags.TAG_RESPONSE_ACCESS_CONTROL_NOT_LOGGED_IN);
        }
        writer.flush();
    }

    @Override
    public void visit(JsonExceptionResponse rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_RESPONSE_ERROR, rsp.getErrorMessage());
        writer.put(RequestsTags.TAG_RESPONSE_JSON, rsp.getJsonContent());
        writer.flush();
    }

    @Override
    public void visit(CredentialRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getName() != null) {
            writer.put(CoreTags.TAG_USER_NAME, rsp.getName());
        }
        writer.flush();
    }

    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getJson() != null) {
            writer.put(RequestsTags.TAG_RESULT_VALUE, rsp.getJson());
        }
    }

    @Override
    public void visit(LogoutRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(RPCRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getResult() instanceof Report) {
            Report report = (Report) rsp.getResult();
            writer.put(RequestsTags.TAG_FILE_NAME, report.getName());
            writer.put(RequestsTags.TAG_FORMAT, report.getFormat());
            writer.put(RequestsTags.TAG_RESULT_VALUE, report.getBody());
        } else {
            writer.put(RequestsTags.TAG_RESULT_VALUE, (String) rsp.getResult());
        }
        writer.flush();
    }

    @Override
    public void visit(DisposeServerModuleRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(CommitRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.UPDATED_TAG, rsp.getUpdated());
        writer.flush();
    }

    @Override
    public void visit(ModuleStructureRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_RESULT_VALUE, rsp.getJson());
        writer.flush();
    }

    @Override
    public void visit(AppQueryRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getAppQueryJson() != null) {
            assert rsp.getTimeStamp() != null;
            writer.put(RequestsTags.TAG_TIMESTAMP, rsp.getTimeStamp());
            writer.put(RequestsTags.TAG_RESULT_VALUE, rsp.getAppQueryJson());
            writer.flush();
        }
    }

    @Override
    public void visit(ResourceRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getContent() != null) {
            assert rsp.getTimeStamp() != null;
            writer.put(RequestsTags.TAG_TIMESTAMP, rsp.getTimeStamp());
            writer.put(RequestsTags.TAG_RESOUCRE_CONTENT, rsp.getContent());
        }
        writer.flush();
    }

    @Override
    public void visit(ServerModuleStructureRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getInfoJson() != null) {
            assert rsp.getTimeStamp() != null;
            writer.put(RequestsTags.TAG_TIMESTAMP, rsp.getTimeStamp());
            writer.put(RequestsTags.TAG_RESULT_VALUE, rsp.getInfoJson());
        }
        writer.flush();
    }

}
