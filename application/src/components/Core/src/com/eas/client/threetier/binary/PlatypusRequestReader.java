/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.binary;

import com.bearsoft.rowset.changes.serial.ChangesReader;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.serial.CustomSerializer;
import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.PlatypusRowsetReader;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.requests.AppElementChangedRequest;
import com.eas.client.threetier.requests.AppElementRequest;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DbTableChangedRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import static com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest.ArgumentType.STRING;
import com.eas.client.threetier.requests.IsAppElementActualRequest;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.client.threetier.requests.PlatypusRequestsFactory;
import com.eas.client.threetier.requests.StartAppElementRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoReaderException;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import com.eas.script.ScriptUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jdk.nashorn.internal.runtime.Undefined;

/**
 *
 * @author mg
 */
public class PlatypusRequestReader implements PlatypusRequestVisitor {

    protected byte[] bytes;

    public PlatypusRequestReader(byte[] aBytes) {
        super();
        bytes = aBytes;
    }

    /**
     *
     * @param reader
     * @return
     * @throws IOException
     * @throws ProtoReaderException
     */
    public static Request read(ProtoReader reader) throws Exception {
        Request rq = null;
        Long id = null;
        Integer type = null;
        byte[] data = null;
        do {
            switch (reader.getNextTag()) {
                case RequestsTags.TAG_REQUEST:
                    id = reader.getLong();
                    break;
                case RequestsTags.TAG_REQUEST_TYPE:
                    type = reader.getInt();
                    break;
                case RequestsTags.TAG_REQUEST_DATA:
                    data = reader.getSubStreamData();
                    break;
                case RequestsTags.TAG_REQUEST_END:
                    if (id == null) {
                        throw new NullPointerException("id");
                    }
                    if (type == null) {
                        throw new NullPointerException("type");
                    }
                    rq = PlatypusRequestsFactory.create(id, type);
                    PlatypusRequestReader requestReader = new PlatypusRequestReader(data);
                    rq.accept(requestReader);
                    break;
            }
        } while (reader.getCurrentTag() != CoreTags.TAG_EOF && reader.getCurrentTag() != RequestsTags.TAG_REQUEST_END);
        return rq;
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_QUERY_ID)) {
            throw new NullPointerException("No query specified");
        }
        rq.setQueryId(dom.getChild(RequestsTags.TAG_QUERY_ID).getString());
    }

    @Override
    public void visit(LoginRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (input.containsChild(RequestsTags.TAG_LOGIN)) {
            rq.setLogin(input.getChild(RequestsTags.TAG_LOGIN).getString());
        } else {
            rq.setLogin(null);
        }
        if (input.containsChild(RequestsTags.TAG_PASSWORD)) {
            rq.setPassword(input.getChild(RequestsTags.TAG_PASSWORD).getString());
        } else {
            rq.setPassword(null);
        }
        if (input.containsChild(RequestsTags.TAG_SESSION_TO_RESTORE)) {
            rq.setSession2restore(input.getChild(RequestsTags.TAG_SESSION_TO_RESTORE).getString());
        } else {
            rq.setSession2restore(null);
        }
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(bytes));
        rq.setChanges(null);
        do {
            switch (reader.getNextTag()) {
                case RequestsTags.TAG_CHANGES:
                    rq.setChanges(ChangesReader.read(reader.getSubStreamData(), customReadersContainer));
                    break;
            }
        } while (reader.getCurrentTag() != CoreTags.TAG_EOF);
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (!input.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            throw new ProtoReaderException("Module name not specified.");
        }
        rq.setModuleName(input.getChild(RequestsTags.TAG_MODULE_NAME).getString());
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (!input.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            throw new ProtoReaderException("Module name is not specified.");
        }
        rq.setModuleName(input.getChild(RequestsTags.TAG_MODULE_NAME).getString());
    }

    public static Object getValue(ProtoNode node, ExecuteServerModuleMethodRequest.ArgumentType at) throws ProtoReaderException {
        switch (at) {
            case BIG_DECIMAL:
                return (node.getBigDecimal());
            case BIG_INTEGER:
                return (node.getBigDecimal().toBigInteger());
            case BOOLEAN:
                return (node.getInt() != 0);
            case BYTE:
                return (node.getByte());
            case CHARACTER:
                return (node.getString().charAt(0));
            case DATE:
                return (node.getDate());
            case DOUBLE:
                return (node.getDouble());
            case FLOAT:
                return ((float) node.getDouble());
            case INTEGER:
                return (node.getInt());
            case LONG:
                return (node.getLong());
            case SHORT:
                return ((short) node.getInt());
            case STRING:
                return (node.getString());
            case OBJECT:
                return ScriptUtils.parseJson(node.getString());
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        final Iterator<ProtoNode> it = input.iterator();
        final List<Object> args = new ArrayList<>();
        ExecuteServerModuleMethodRequest.ArgumentType at = null;
        while (it.hasNext()) {
            final ProtoNode node = it.next();
            switch (node.getNodeTag()) {
                case RequestsTags.TAG_MODULE_NAME:
                    rq.setModuleName(node.getString());
                    break;
                case RequestsTags.TAG_METHOD_NAME:
                    rq.setMethodName(node.getString());
                    break;
                case RequestsTags.TAG_NULL_ARGUMENT:
                    args.add(null);
                    break;
                case RequestsTags.TAG_UNDEFINED_ARGUMENT:
                    args.add(Undefined.getUndefined());
                    break;
                case RequestsTags.TAG_ARGUMENT_TYPE:
                    at = ExecuteServerModuleMethodRequest.ArgumentType.getArgumentType(node.getInt());
                    break;
                case RequestsTags.TAG_ARGUMENT_VALUE: {
                    assert at != null : "Argument type is not known";
                    args.add(getValue(node, at));
                    break;
                }
            }
        }
        rq.setArguments(args.toArray());
    }

    @Override
    public void visit(AppElementChangedRequest rq) throws Exception {
        ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        rq.setDatabaseId(input.containsChild(RequestsTags.DATABASE_TAG) ? input.getChild(RequestsTags.DATABASE_TAG).getString() : null);
        rq.setEntityId(input.containsChild(RequestsTags.ENTITY_ID_TAG) ? input.getChild(RequestsTags.ENTITY_ID_TAG).getString() : null);
    }

    @Override
    public void visit(DbTableChangedRequest rq) throws Exception {
        ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        rq.setDatabaseId(input.containsChild(RequestsTags.DATABASE_TAG) ? input.getChild(RequestsTags.DATABASE_TAG).getString() : null);
        rq.setSchema(input.containsChild(RequestsTags.SCHEMA_NAME_TAG) ? input.getChild(RequestsTags.SCHEMA_NAME_TAG).getString() : null);
        if (!input.containsChild(RequestsTags.TABLE_NAME_TAG)) {
            throw new ProtoReaderException("No table specified");
        }
        rq.setTable(input.getChild(RequestsTags.TABLE_NAME_TAG).getString());
    }

    @Override
    public void visit(HelloRequest rq) throws Exception {
    }
    private static PlatypusRowsetReader customReadersContainer = new PlatypusRowsetReader(null);

    public static Parameter readParameter(ProtoNode node) throws ProtoReaderException {
        Parameter param = new Parameter();
        Object value;
        int paramType;
        int paramMode;
        String paramTypeName = null;
        String paramTypeClassName = null;
        String paramName = null;
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE)) {
            throw new ProtoReaderException("No parameter type");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME)) {
            throw new ProtoReaderException("No parameter type name");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME)) {
            throw new ProtoReaderException("No parameter type java class name");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_MODE)) {
            throw new ProtoReaderException("No parameter mode");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_NAME)) {
            throw new ProtoReaderException("No parameter name");
        }
        paramType = node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE).getInt();
        paramTypeName = node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME).getString();
        paramTypeClassName = node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME).getString();
        paramMode = node.getChild(RequestsTags.TAG_SQL_PARAMETER_MODE).getInt();
        paramName = node.getChild(RequestsTags.TAG_SQL_PARAMETER_NAME).getString();
        param.getTypeInfo().setSqlType(paramType);
        param.getTypeInfo().setSqlTypeName(paramTypeName);
        param.getTypeInfo().setJavaClassName(paramTypeClassName);
        if (node.containsChild(RequestsTags.TAG_SQL_PARAMETER_DESCRIPTION)) {
            param.setDescription(node.getChild(RequestsTags.TAG_SQL_PARAMETER_DESCRIPTION).getString());
        }
        value = null;
        if (node.containsChild(RequestsTags.TAG_SQL_PARAMETER_VALUE)) {
            ProtoNode valueNode = node.getChild(RequestsTags.TAG_SQL_PARAMETER_VALUE);
            CustomSerializer serializer = customReadersContainer.getSerializer(param.getTypeInfo());
            if (serializer != null) {
                try {
                    value = serializer.deserialize(valueNode.getData(), valueNode.getOffset(), valueNode.getSize(), param.getTypeInfo());
                } catch (RowsetException ex) {
                    throw new ProtoReaderException(ex);
                }
            } else {
                value = valueNode.getJDBCCompatible(paramType);
            }
        }
        param.setName(paramName);
        param.setValue(value);
        param.setMode(paramMode);
        return param;
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        rq.setParams(new Parameters());
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_QUERY_ID)) {
            throw new NullPointerException("No query specified");
        }
        rq.setQueryId(dom.getChild(RequestsTags.TAG_QUERY_ID).getString());
        Iterator<ProtoNode> it = dom.iterator();
        while (it.hasNext()) {
            ProtoNode node = it.next();
            if (node.getNodeTag() == RequestsTags.TAG_SQL_PARAMETER) {
                Parameter param = readParameter(node);
                rq.getParams().add(param);
            }
        }
    }

    @Override
    public void visit(KeepAliveRequest rq) throws Exception {
    }

    @Override
    public void visit(StartAppElementRequest rq) throws Exception {
    }

    @Override
    public void visit(IsUserInRoleRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (!input.containsChild(RequestsTags.TAG_ROLE_NAME)) {
            throw new ProtoReaderException("No user name");
        }
        rq.setRoleName(input.getChild(RequestsTags.TAG_ROLE_NAME).getString());
    }

    @Override
    public void visit(IsAppElementActualRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (!input.containsChild(RequestsTags.TAG_APP_ELEMENT_ID)) {
            throw new ProtoReaderException("No application element id");
        }
        if (!input.containsChild(RequestsTags.TAG_TEXT_SIZE)) {
            throw new ProtoReaderException("No text content size");
        }
        if (!input.containsChild(RequestsTags.TAG_TEST_CRC32)) {
            throw new ProtoReaderException("No text content crc32");
        }
        rq.setAppElementId(input.getChild(RequestsTags.TAG_APP_ELEMENT_ID).getString());
        rq.setTxtContentSize(input.getChild(RequestsTags.TAG_TEXT_SIZE).getLong());
        rq.setTxtContentCrc32(input.getChild(RequestsTags.TAG_TEST_CRC32).getLong());
    }

    @Override
    public void visit(AppElementRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (!input.containsChild(RequestsTags.TAG_APP_ELEMENT_ID)) {
            throw new ProtoReaderException("No application element id");
        }
        rq.setAppElementId(input.getChild(RequestsTags.TAG_APP_ELEMENT_ID).getString());
    }
}
