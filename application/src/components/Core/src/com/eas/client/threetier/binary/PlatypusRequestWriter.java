/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.binary;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.serial.ChangesWriter;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.serial.CustomSerializer;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.PlatypusRowsetWriter;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.RowsetJsonWriter;
import com.eas.client.threetier.requests.AppElementChangedRequest;
import com.eas.client.threetier.requests.AppElementRequest;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DbTableChangedRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.client.threetier.requests.ExecuteServerReportRequest;
import com.eas.client.threetier.requests.IsAppElementActualRequest;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.client.threetier.requests.StartAppElementRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import com.eas.script.ScriptUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Undefined;

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
            writer.put(RequestsTags.TAG_REQUEST, aRequest.getID());
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
    public void visit(AppQueryRequest rq) throws Exception {
        ProtoWriter pw = new ProtoWriter(out);
        pw.put(RequestsTags.TAG_QUERY_ID, rq.getQueryId());
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
    public void visit(CreateServerModuleRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
        writer.flush();
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
        writer.flush();
    }

    public static void putValue(ProtoWriter writer, int nullValueTag, int undefinedValueTag, int functionValueTag, int typeTag, int nameTag, String aArgumentName, int valueTag, Object arg) throws IOException, Exception {
        if (arg == null) {
            writer.put(nullValueTag);
        } else if (arg instanceof Undefined) {
            writer.put(undefinedValueTag);
        } else if (arg instanceof Function) {
            writer.put(functionValueTag);
        } else {
            if (aArgumentName != null && !aArgumentName.isEmpty()) {
                writer.put(nameTag, aArgumentName);
            }
            ExecuteServerModuleMethodRequest.ArgumentType at = ExecuteServerModuleMethodRequest.ArgumentType.getArgumentType(arg);
            if (at == null) {
                throw new IllegalArgumentException(arg.toString());
            }
            writer.put(typeTag, at.getTypeID());
            switch (at) {
                case BIG_DECIMAL:
                    writer.put(valueTag, (BigDecimal) arg);
                    break;
                case BIG_INTEGER:
                    writer.put(valueTag, new BigDecimal((BigInteger) arg));
                    break;
                case BOOLEAN:
                    writer.put(valueTag, ((Boolean) arg).booleanValue() ? 1 : 0);
                    break;
                case BYTE:
                    writer.put(valueTag, (Byte) arg);
                    break;
                case CHARACTER:
                    writer.put(valueTag, ((Character) arg).toString());
                    break;
                case DATE:
                    writer.put(valueTag, (Date) arg);
                    break;
                case DOUBLE:
                    writer.put(valueTag, (Double) arg);
                    break;
                case FLOAT:
                    writer.put(valueTag, ((Float) arg).doubleValue());
                    break;
                case INTEGER:
                    writer.put(valueTag, (Integer) arg);
                    break;
                case LONG:
                    writer.put(valueTag, (Long) arg);
                    break;
                case SHORT:
                    writer.put(valueTag, ((Short) arg).intValue());
                    break;
                case STRING:
                    writer.put(valueTag, (String) arg);
                    break;
                case OBJECT:
                    if (arg instanceof Rowset) {
                        RowsetJsonWriter jsonWriter = new RowsetJsonWriter((Rowset) arg);
                        writer.put(valueTag, jsonWriter.write());
                    } else {
                        writer.put(valueTag, (String) ScriptUtils.toJson(arg));
                    }
                    break;
            }
        }
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
        writer.put(RequestsTags.TAG_METHOD_NAME, rq.getMethodName());
        for (Object arg : rq.getArguments()) {
            putValue(writer, RequestsTags.TAG_NULL_ARGUMENT, RequestsTags.TAG_UNDEFINED_ARGUMENT, RequestsTags.TAG_FUNCTION_RESULT, RequestsTags.TAG_ARGUMENT_TYPE, RequestsTags.TAG_ARGUMENT_NAME, null, RequestsTags.TAG_ARGUMENT_VALUE, arg);
        }
        writer.flush();
    }

    @Override
    public void visit(AppElementChangedRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rq.getDatabaseId() != null) {
            writer.put(RequestsTags.DATABASE_TAG, rq.getDatabaseId());
        }
        if (rq.getEntityId() != null) {
            writer.put(RequestsTags.ENTITY_ID_TAG, rq.getEntityId());
        }
        writer.flush();
    }

    @Override
    public void visit(DbTableChangedRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        if (rq.getDatabaseId() != null) {
            writer.put(RequestsTags.DATABASE_TAG, rq.getDatabaseId());
        }
        if (rq.getSchema() != null) {
            writer.put(RequestsTags.SCHEMA_NAME_TAG, rq.getSchema());
        }
        writer.put(RequestsTags.TABLE_NAME_TAG, rq.getTable());
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

    @Override
    public void visit(IsAppElementActualRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_APP_ELEMENT_ID, rq.getAppElementId());
        writer.put(RequestsTags.TAG_TEXT_SIZE, rq.getTxtContentSize());
        writer.put(RequestsTags.TAG_TEST_CRC32, rq.getTxtContentCrc32());
        writer.flush();
    }

    @Override
    public void visit(AppElementRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_APP_ELEMENT_ID, rq.getAppElementId());
        writer.flush();
    }

    @Override
    public void visit(ExecuteServerReportRequest rq) throws Exception {
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(RequestsTags.TAG_MODULE_NAME, rq.getModuleName());
        if (rq.getArguments() != null && rq.getArguments().length > 0) {
            for (ExecuteServerReportRequest.NamedArgument arg : rq.getArguments()) {
                putValue(writer, RequestsTags.TAG_NULL_ARGUMENT, RequestsTags.TAG_UNDEFINED_ARGUMENT, RequestsTags.TAG_FUNCTION_RESULT, RequestsTags.TAG_ARGUMENT_TYPE, RequestsTags.TAG_ARGUMENT_NAME, arg.getName(), RequestsTags.TAG_ARGUMENT_VALUE, arg.getValue());
            }
        }
        writer.flush();
    }
}
