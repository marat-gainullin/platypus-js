/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.serial.BinaryRowsetWriter;
import com.eas.client.ServerModuleInfo;
import com.eas.client.report.Report;
import com.eas.client.threetier.PlatypusRowsetWriter;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.client.threetier.requests.HelloRequest;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.StartAppElementRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import com.eas.script.ScriptUtils;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Set;
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
        if (response instanceof ErrorResponse) {
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
    public void visit(ErrorResponse rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getErrorMessage() != null) {
            writer.put(RequestsTags.TAG_RESPONSE_ERROR, rsp.getErrorMessage());
        }
        if (rsp.getSqlErrorCode() != null) {
            writer.put(RequestsTags.TAG_RESPONSE_SQL_ERROR_CODE, rsp.getSqlErrorCode());
        }
        if (rsp.getSqlState() != null) {
            writer.put(RequestsTags.TAG_RESPONSE_SQL_ERROR_STATE, rsp.getSqlState());
        }
        if (rsp.isAccessControl()) {
            writer.put(RequestsTags.TAG_RESPONSE_ACCESS_CONTROL);
        }
        writer.flush();
    }

    @Override
    public void visit(HelloRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(1, "Hello, world!");
        writer.flush();
    }

    @Override
    public void visit(StartAppElementRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getAppElementId() != null) {
            writer.put(RequestsTags.TAG_MODULE_NAME, rsp.getAppElementId());
        }
        writer.flush();
    }

    /*
     protected void writeParameters(OutputStream aOut) throws IOException {
     ProtoWriter writer = new ProtoWriter(aOut);
     for (Field param : parameters.toCollection()) {
     writer.put(ExecuteQueryRequest.TAG_SQL_PARAMETER);
     writer.put(CoreTags.TAG_STREAM, ExecuteQueryRequest.writeParameter((Parameter) param));
     }
     writer.flush();
     }
     */
    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_UPDATE_COUNT, rsp.getUpdateCount());
        if (rsp.getRowset() != null) {
            ByteArrayOutputStream rowsetStream = new ByteArrayOutputStream();
            BinaryRowsetWriter rsWriter = new PlatypusRowsetWriter();
            rsWriter.write(rsp.getRowset(), rowsetStream);
            writer.put(RequestsTags.TAG_ROWSET);
            writer.put(CoreTags.TAG_STREAM, rowsetStream);
        }
    }

    @Override
    public void visit(LogoutRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(LoginRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_SESSION_ID, rsp.getSessionId());
        writer.flush();
    }

    @Override
    public void visit(KeepAliveRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(IsUserInRoleRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.isRole()) {
            writer.put(RequestsTags.TAG_ROLE);
        }
        writer.flush();
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getResult() instanceof Report) {
            Report report = (Report) rsp.getResult();
            writer.put(RequestsTags.TAG_FILE_NAME, report.getName());
            writer.put(RequestsTags.TAG_FORMAT, report.getFormat());
            writer.put(RequestsTags.TAG_RESULT_VALUE, report.getReport());
        } else {
            writer.put(RequestsTags.TAG_RESULT_VALUE, ScriptUtils.toJson(rsp.getResult()));
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
        for (String partName : rsp.getStructure()) {
            writer.put(RequestsTags.TAG_RESOURCE_NAME, partName);
        }
        for (String clientDependency : rsp.getClientDependencies()) {
            writer.put(RequestsTags.TAG_MODULE_CLIENT_DEPENDENCY, clientDependency);
        }
        for (String serverDependency : rsp.getServerDependencies()) {
            writer.put(RequestsTags.TAG_MODULE_SERVER_DEPENDENCY, serverDependency);
        }
        for (String queryDependency : rsp.getQueryDependencies()) {
            writer.put(RequestsTags.TAG_MODULE_QUERY_DEPENDENCY, queryDependency);
        }
        writer.flush();
    }

    @Override
    public void visit(AppQueryRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getAppQuery() != null) {
            writer.put(RequestsTags.TAG_TIMESTAMP, rsp.getTimeStamp());
            writer.put(RequestsTags.TAG_QUERY_ID, rsp.getAppQuery().getEntityId());
            ByteArrayOutputStream fieldsStream = new ByteArrayOutputStream();
            PlatypusRowsetWriter rsWriter = new PlatypusRowsetWriter();
            rsWriter.writeFields(rsp.getAppQuery().getFields(), fieldsStream);
            writer.put(RequestsTags.TAG_DML, rsp.getAppQuery().isManual() ? 1 : 0);
            if (rsp.getAppQuery().getTitle() != null) {
                writer.put(RequestsTags.TAG_TITLE, rsp.getAppQuery().getTitle());
            }
            writer.put(RequestsTags.TAG_FIELDS);
            writer.put(CoreTags.TAG_STREAM, fieldsStream);
            for (Field param : rsp.getAppQuery().getParameters().toCollection()) {
                writer.put(RequestsTags.TAG_QUERY_SQL_PARAMETER);
                writer.put(CoreTags.TAG_STREAM, PlatypusRequestWriter.writeParameter((Parameter) param));
            }
            Set<String> roles = rsp.getAppQuery().getReadRoles();
            for (String role : roles) {
                writer.put(RequestsTags.TAG_READ_ROLE, role);
            }
            roles = rsp.getAppQuery().getWriteRoles();
            for (String role : roles) {
                writer.put(RequestsTags.TAG_WRITE_ROLE, role);
            }
            writer.flush();
        }
    }

    @Override
    public void visit(ResourceRequest.Response rsp) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rsp.getContent() != null) {
            writer.put(RequestsTags.TAG_TIMESTAMP, rsp.getTimeStamp());
            writer.put(RequestsTags.TAG_RESOUCRE_CONTENT, rsp.getContent());
        }
        writer.flush();
    }

    @Override
    public void visit(CreateServerModuleRequest.Response rsp) throws Exception {
        ProtoWriter pw = new ProtoWriter(out);
        if (rsp.getInfo() != null) {
            ServerModuleInfo info = rsp.getInfo();
            pw.put(RequestsTags.TAG_MODULE_NAME, info.getModuleName());
            pw.put(RequestsTags.TAG_TIMESTAMP, rsp.getTimeStamp());
            if (info.isPermitted()) {
                pw.put(RequestsTags.TAG_MODULE_PERMITTED);
            }
            if (!info.getFunctionsNames().isEmpty()) {
                ByteArrayOutputStream functions = new ByteArrayOutputStream();
                ProtoWriter fw = new ProtoWriter(functions);
                for (String functionName : info.getFunctionsNames()) {
                    fw.put(RequestsTags.TAG_MODULE_FUNCTION_NAME, functionName);
                }
                fw.flush();
                pw.put(RequestsTags.TAG_MODULE_FUNCTION_NAMES);
                pw.put(CoreTags.TAG_STREAM, functions);
            }
        }
        pw.flush();
    }

}
