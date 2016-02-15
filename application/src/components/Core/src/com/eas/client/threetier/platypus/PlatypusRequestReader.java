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
import com.eas.client.threetier.requests.PlatypusRequestsFactory;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoReaderException;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import com.eas.script.Scripts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        Integer type = null;
        byte[] data = null;
        do {
            switch (reader.getNextTag()) {
                case RequestsTags.TAG_REQUEST_TYPE:
                    type = reader.getInt();
                    break;
                case RequestsTags.TAG_REQUEST_DATA:
                    data = reader.getSubStreamData();
                    break;
                case RequestsTags.TAG_REQUEST_END:
                    if (type != null) {
                        rq = PlatypusRequestsFactory.create(type);
                        PlatypusRequestReader requestReader = new PlatypusRequestReader(data);
                        rq.accept(requestReader);
                        break;
                    } else {
                        throw new NullPointerException("Request type must present");
                    }
            }
        } while (reader.getCurrentTag() != CoreTags.TAG_EOF && reader.getCurrentTag() != RequestsTags.TAG_REQUEST_END);
        return rq;
    }

    @Override
    public void visit(ModuleStructureRequest rq) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            throw new NullPointerException("No module name specified");
        }
        rq.setModuleName(dom.getChild(RequestsTags.TAG_MODULE_NAME).getString());
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_QUERY_ID)) {
            throw new NullPointerException("No query specified");
        }
        rq.setQueryName(dom.getChild(RequestsTags.TAG_QUERY_ID).getString());
        if (dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
            rq.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
        }
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_RESOURCE_NAME)) {
            throw new NullPointerException("No resource name specified");
        }
        rq.setResourceName(dom.getChild(RequestsTags.TAG_RESOURCE_NAME).getString());
        if (dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
            rq.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
        }
    }

    @Override
    public void visit(ServerModuleStructureRequest rq) throws Exception {
        final ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            throw new ProtoReaderException("Module name not specified.");
        }
        rq.setModuleName(dom.getChild(RequestsTags.TAG_MODULE_NAME).getString());
        if (dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
            rq.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
        }
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_CHANGES)) {
            throw new NullPointerException("No changes specified");
        }
        rq.setChangesJson(dom.getChild(RequestsTags.TAG_CHANGES).getString());
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (!input.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            throw new ProtoReaderException("Module name is not specified.");
        }
        rq.setModuleName(input.getChild(RequestsTags.TAG_MODULE_NAME).getString());
    }

    @Override
    public void visit(RPCRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        final Iterator<ProtoNode> it = input.iterator();
        final List<String> args = new ArrayList<>();
        while (it.hasNext()) {
            final ProtoNode node = it.next();
            switch (node.getNodeTag()) {
                case RequestsTags.TAG_MODULE_NAME:
                    rq.setModuleName(node.getString());
                    break;
                case RequestsTags.TAG_METHOD_NAME:
                    rq.setMethodName(node.getString());
                    break;
                case RequestsTags.TAG_ARGUMENT_VALUE: {
                    args.add(node.getString());
                    break;
                }
            }
        }
        rq.setArgumentsJsons(args.toArray(new String[]{}));
    }

    public static Parameter readParameter(ProtoNode node) throws ProtoReaderException {
        Parameter param = new Parameter();
        Object value;
        String paramType = null;
        int paramMode;
        String paramName;
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_NAME)) {
            throw new ProtoReaderException("No parameter name");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE)) {
            throw new ProtoReaderException("No parameter type");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_MODE)) {
            throw new ProtoReaderException("No parameter mode");
        }
        ProtoNode typeNode = node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE);
        if (typeNode != null) {
            paramType = typeNode.getString();
        }
        param.setType(paramType);
        paramMode = node.getChild(RequestsTags.TAG_SQL_PARAMETER_MODE).getInt();
        paramName = node.getChild(RequestsTags.TAG_SQL_PARAMETER_NAME).getString();
        if (node.containsChild(RequestsTags.TAG_SQL_PARAMETER_DESCRIPTION)) {
            param.setDescription(node.getChild(RequestsTags.TAG_SQL_PARAMETER_DESCRIPTION).getString());
        }
        value = null;
        if (node.containsChild(RequestsTags.TAG_SQL_PARAMETER_VALUE)) {
            ProtoNode valueNode = node.getChild(RequestsTags.TAG_SQL_PARAMETER_VALUE);
            if (paramType != null) {
                switch (paramType) {
                    case Scripts.STRING_TYPE_NAME:
                        value = valueNode.getString();
                        break;
                    case Scripts.NUMBER_TYPE_NAME:
                        value = valueNode.getDouble();
                        break;
                    case Scripts.DATE_TYPE_NAME:
                        value = valueNode.getDate();
                        break;
                    case Scripts.BOOLEAN_TYPE_NAME:
                        value = valueNode.getBoolean();
                        break;
                    case Scripts.GEOMETRY_TYPE_NAME:
                        value = valueNode.getString();
                        break;
                }
            } else {
                value = valueNode.getString();
            }
        }
        param.setName(paramName);
        param.setValue(value);
        param.setMode(paramMode);
        return param;
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        Map<String, String> params = new HashMap<>();
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_QUERY_ID)) {
            throw new NullPointerException("No query name specified");
        }
        rq.setQueryName(dom.getChild(RequestsTags.TAG_QUERY_ID).getString());
        List<ProtoNode> parametersNodes = dom.getChildren(RequestsTags.TAG_SQL_PARAMETER);
        for (ProtoNode paramNode : parametersNodes) {
            String paramName = paramNode.getChild(RequestsTags.TAG_SQL_PARAMETER_NAME).getString();
            String paramValue = paramNode.getChild(RequestsTags.TAG_SQL_PARAMETER_VALUE).getString();
            params.put(paramName, paramValue);
        }
        rq.setParamsJsons(params);
    }

    @Override
    public void visit(CredentialRequest rq) throws Exception {
    }
}
