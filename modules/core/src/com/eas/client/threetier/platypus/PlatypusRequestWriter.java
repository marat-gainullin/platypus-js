/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.metadata.Parameter;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.ServerModuleStructureRequest;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
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
        pw.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
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
    public void visit(ServerModuleStructureRequest rq) throws Exception {
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
        writer.put(RequestsTags.TAG_CHANGES, rq.getChangesJson());
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
        for (String argJson : rq.getArgumentsJsons()) {
            writer.put(RequestsTags.TAG_ARGUMENT_VALUE, argJson);
        }
        writer.flush();
    }

    public static byte[] writeParameter(Parameter aParam) throws IOException {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ProtoWriter writer = new ProtoWriter(outStream);
            writer.put(RequestsTags.TAG_SQL_PARAMETER_NAME, aParam.getName());
            writer.put(RequestsTags.TAG_SQL_PARAMETER_TYPE, aParam.getType());
            writer.put(RequestsTags.TAG_SQL_PARAMETER_MODE, aParam.getMode());
            if (aParam.getDescription() != null) {
                writer.put(RequestsTags.TAG_SQL_PARAMETER_DESCRIPTION, aParam.getDescription());
            }
            Object paramValue = aParam.getValue();
            if (paramValue != null) {
                if (paramValue instanceof Number) {
                    writer.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, ((Number) paramValue).doubleValue());
                } else if (paramValue instanceof Boolean) {
                    writer.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, (Boolean) paramValue);
                } else if (paramValue instanceof CharSequence) {
                    writer.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, paramValue.toString());
                } else if (paramValue instanceof Date) {
                    writer.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, (Date) paramValue);
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
        writer.put(RequestsTags.TAG_QUERY_ID, rq.getQueryName());
        for (Map.Entry<String, String> pEntry : rq.getParamsJsons().entrySet()) {
            writer.put(RequestsTags.TAG_SQL_PARAMETER);
            ByteArrayOutputStream pOut = new ByteArrayOutputStream();
            ProtoWriter pWriter = new ProtoWriter(pOut);
            pWriter.put(RequestsTags.TAG_SQL_PARAMETER_NAME, pEntry.getKey());
            pWriter.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, pEntry.getValue());
            pWriter.flush();
            writer.put(CoreTags.TAG_STREAM, pOut);
        }
        writer.flush();
    }

    @Override
    public void visit(CredentialRequest rq) throws Exception {
    }
}
