/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.bearsoft.rowset.changes.serial.ChangesWriter;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.serial.CustomSerializer;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.threetier.requests.HelloRequest;
import com.eas.client.threetier.PlatypusRowsetWriter;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.StartAppElementRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import com.eas.script.ScriptUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author mg
 */
public class PlatypusRequestWriter implements PlatypusRequestVisitor {

    protected OutputStream out;

    public PlatypusRequestWriter(OutputStream aOut) {
        super();
        out = aOut;
    }

    public static void write(Request aRequest, ProtoWriter writer) throws IOException {
        try {
            writer.put(RequestsTags.TAG_REQUEST_TYPE, aRequest.getType());
            writer.put(RequestsTags.TAG_REQUEST_DATA);
            ByteArrayOutputStream subOut = new ByteArrayOutputStream();
            PlatypusRequestWriter requestsWriter = new PlatypusRequestWriter(subOut);
            aRequest.accept(requestsWriter);
            byte[] subOutBytes = subOut.toByteArray();
            if (subOutBytes.length > 1024 * 2) {
                ByteArrayOutputStream zSubOut = new ByteArrayOutputStream();
                try (ZipOutputStream zStream = new ZipOutputStream(zSubOut)) {
                    ZipEntry ze = new ZipEntry("requestData");
                    zStream.putNextEntry(ze);
                    zStream.write(subOutBytes);
                    zStream.flush();
                }
                writer.put(CoreTags.TAG_COMPRESSED_STREAM, zSubOut.toByteArray());
            } else {
                writer.put(CoreTags.TAG_STREAM, subOutBytes);
            }
            writer.put(RequestsTags.TAG_REQUEST_END);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void visit(ModuleStructureRequest rq) throws Exception {
        ProtoWriter pw = new ProtoWriter(out);
        pw.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleOrResourceName());
        pw.flush();
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        ProtoWriter pw = new ProtoWriter(out);
        pw.put(RequestsTags.TAG_QUERY_ID, rq.getQueryName());
        if (rq.getTimeStamp() != null) {
            pw.put(RequestsTags.TAG_TIMESTAMP, rq.getTimeStamp());
        }
        pw.flush();
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
        ProtoWriter pw = new ProtoWriter(out);
        pw.put(RequestsTags.TAG_RESOURCE_NAME, rq.getResourceName());
        if (rq.getTimeStamp() != null) {
            pw.put(RequestsTags.TAG_TIMESTAMP, rq.getTimeStamp());
        }
        pw.flush();
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
        ProtoWriter pw = new ProtoWriter(out);
        pw.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
        if (rq.getTimeStamp() != null) {
            pw.put(RequestsTags.TAG_TIMESTAMP, rq.getTimeStamp());
        }
        pw.flush();
    }

    @Override
    public void visit(LoginRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rq.getLogin() != null) {
            writer.put(RequestsTags.TAG_LOGIN, rq.getLogin());
        }
        if (rq.getPassword() != null) {
            writer.put(RequestsTags.TAG_PASSWORD, rq.getPassword());
        }
        if (rq.getSession2restore() != null) {
            writer.put(RequestsTags.TAG_SESSION_TO_RESTORE, rq.getSession2restore());
        }
        writer.flush();
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_CHANGES);
        writer.put(CoreTags.TAG_STREAM, ChangesWriter.write(rq.getChanges(), customWritersContainer));
        writer.flush();
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
        writer.flush();
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
        writer.put(RequestsTags.TAG_METHOD_NAME, rq.getMethodName());
        for (Object arg : rq.getArguments()) {
            writer.put(RequestsTags.TAG_ARGUMENT_VALUE, ScriptUtils.toJson(arg));
        }
        writer.flush();
    }

    @Override
    public void visit(HelloRequest rq) throws Exception {
    }
    private static PlatypusRowsetWriter customWritersContainer = new PlatypusRowsetWriter();

    public static byte[] writeParameter(Parameter aParam) throws IOException {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ProtoWriter writer = new ProtoWriter(outStream);
            writer.put(RequestsTags.TAG_SQL_PARAMETER_TYPE, aParam.getTypeInfo().getSqlType());
            writer.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME, aParam.getTypeInfo().getSqlTypeName());
            writer.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME, aParam.getTypeInfo().getJavaClassName());
            writer.put(RequestsTags.TAG_SQL_PARAMETER_MODE, aParam.getMode());
            writer.put(RequestsTags.TAG_SQL_PARAMETER_NAME, aParam.getName());
            if (aParam.getDescription() != null) {
                writer.put(RequestsTags.TAG_SQL_PARAMETER_DESCRIPTION, aParam.getDescription());
            }
            Object paramValue = aParam.getValue();
            if (paramValue == RowsetUtils.UNDEFINED_SQL_VALUE) {
                paramValue = null;
            }
            if (paramValue != null) {
                CustomSerializer serializer = customWritersContainer.getSerializer(aParam.getTypeInfo());
                if (serializer != null) {
                    writer.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, serializer.serialize(paramValue, aParam.getTypeInfo()));
                } else {
                    writer.putJDBCCompatible(RequestsTags.TAG_SQL_PARAMETER_VALUE, aParam.getTypeInfo().getSqlType(), paramValue);
                }
            }
            writer.flush();
            return outStream.toByteArray();
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_QUERY_ID, rq.getQueryId());
        for (Field param : rq.getParams().toCollection()) {
            writer.put(RequestsTags.TAG_SQL_PARAMETER);
            writer.put(CoreTags.TAG_STREAM, writeParameter((Parameter) param));
        }
        writer.flush();
    }

    @Override
    public void visit(KeepAliveRequest rq) throws Exception {
    }

    @Override
    public void visit(StartAppElementRequest rq) throws Exception {
    }

    @Override
    public void visit(IsUserInRoleRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_ROLE_NAME, rq.getRoleName());
        writer.flush();
    }
}
