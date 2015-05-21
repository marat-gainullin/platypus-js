/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.changes.BinaryChanges;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import com.eas.script.Scripts;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class PlatypusRequestWriter implements PlatypusRequestVisitor {

    protected OutputStream out;
    protected Scripts.Space space;

    public PlatypusRequestWriter(OutputStream aOut, Scripts.Space aSpace) {
        super();
        out = aOut;
        space = aSpace;
    }

    public static void write(Request aRequest, ProtoWriter writer, Scripts.Space aSpace) throws IOException {
        try {
            writer.put(RequestsTags.TAG_REQUEST_TYPE, aRequest.getType());
            writer.put(RequestsTags.TAG_REQUEST_DATA);
            ByteArrayOutputStream subOut = new ByteArrayOutputStream();
            PlatypusRequestWriter requestsWriter = new PlatypusRequestWriter(subOut, aSpace);
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
    public void visit(LogoutRequest rq) throws Exception {
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_CHANGES);
        writer.put(CoreTags.TAG_STREAM, BinaryChanges.write(rq.getChanges()));
        writer.flush();
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
        writer.flush();
    }

    @Override
    public void visit(RPCRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
        writer.put(RequestsTags.TAG_METHOD_NAME, rq.getMethodName());
        for (Object arg : rq.getArguments()) {
            writer.put(RequestsTags.TAG_ARGUMENT_VALUE, space.toJson(JSType.nullOrUndefined(arg) ? null : arg));
        }
        writer.flush();
    }

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
            if (paramValue != null) {
                writer.putJDBCCompatible(RequestsTags.TAG_SQL_PARAMETER_VALUE, aParam.getTypeInfo().getSqlType(), paramValue);
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
        writer.put(RequestsTags.TAG_QUERY_ID, rq.getQueryName());
        for (Field param : rq.getParams().toCollection()) {
            writer.put(RequestsTags.TAG_SQL_PARAMETER);
            writer.put(CoreTags.TAG_STREAM, writeParameter((Parameter) param));
        }
        writer.flush();
    }

    @Override
    public void visit(CredentialRequest rq) throws Exception {
    }
}
