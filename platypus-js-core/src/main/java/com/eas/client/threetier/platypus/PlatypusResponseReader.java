/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.report.Report;
import com.eas.client.threetier.Request;
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
import com.eas.client.threetier.requests.PlatypusResponsesFactory;
import com.eas.client.threetier.requests.SqlExceptionResponse;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author mg
 */
public class PlatypusResponseReader implements PlatypusResponseVisitor {

    protected byte[] bytes;

    public PlatypusResponseReader(byte[] aBytes) {
        super();
        bytes = aBytes;
    }

    public static Response read(ProtoReader reader, Request aRequest) throws Exception {
        byte[] data = null;
        Response rsp = null;
        do {
            switch (reader.getNextTag()) {
                case RequestsTags.TAG_RESPONSE:
                    PlatypusResponsesFactory factory = new PlatypusResponsesFactory();
                    aRequest.accept(factory);
                    rsp = factory.getResponse();
                    break;
                case RequestsTags.TAG_ERROR_RESPONSE:
                    rsp = new ExceptionResponse();
                    break;
                case RequestsTags.TAG_SQL_ERROR_RESPONSE:
                    rsp = new SqlExceptionResponse();
                    break;
                case RequestsTags.TAG_JSON_ERROR_RESPONSE:
                    rsp = new JsonExceptionResponse();
                    break;
                case RequestsTags.TAG_ACCESS_CONTROL_ERROR_RESPONSE:
                    rsp = new AccessControlExceptionResponse();
                    break;
                case RequestsTags.TAG_RESPONSE_DATA:
                    data = reader.getSubStreamData();
                    break;
                case RequestsTags.TAG_RESPONSE_END:
            }
        } while (reader.getCurrentTag() != CoreTags.TAG_EOF && reader.getCurrentTag() != RequestsTags.TAG_RESPONSE_END);
        if (rsp != null && data != null) {
            PlatypusResponseReader responseReader = new PlatypusResponseReader(data);
            rsp.accept(responseReader);
            return rsp;
        } else {
            throw new NullPointerException("Response data must present");
        }
    }

    @Override
    public void visit(ExceptionResponse rsp) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (input.containsChild(RequestsTags.TAG_RESPONSE_ERROR)) {
            rsp.setErrorMessage(input.getChild(RequestsTags.TAG_RESPONSE_ERROR).getString());
        }
    }

    @Override
    public void visit(AccessControlExceptionResponse rsp) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (input.containsChild(RequestsTags.TAG_RESPONSE_ERROR)) {
            rsp.setErrorMessage(input.getChild(RequestsTags.TAG_RESPONSE_ERROR).getString());
        }
        if (input.containsChild(RequestsTags.TAG_RESPONSE_ACCESS_CONTROL_NOT_LOGGED_IN)) {
            rsp.setNotLoggedIn(true);
        }
    }

    @Override
    public void visit(SqlExceptionResponse rsp) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (input.containsChild(RequestsTags.TAG_RESPONSE_ERROR)) {
            rsp.setErrorMessage(input.getChild(RequestsTags.TAG_RESPONSE_ERROR).getString());
        }
        if (input.containsChild(RequestsTags.TAG_RESPONSE_SQL_ERROR_CODE)) {
            rsp.setSqlErrorCode(input.getChild(RequestsTags.TAG_RESPONSE_SQL_ERROR_CODE).getInt());
        }
        if (input.containsChild(RequestsTags.TAG_RESPONSE_SQL_ERROR_STATE)) {
            rsp.setSqlState(input.getChild(RequestsTags.TAG_RESPONSE_SQL_ERROR_STATE).getString());
        }
    }

    @Override
    public void visit(JsonExceptionResponse rsp) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (input.containsChild(RequestsTags.TAG_RESPONSE_ERROR)) {
            rsp.setErrorMessage(input.getChild(RequestsTags.TAG_RESPONSE_ERROR).getString());
        }
        rsp.setJsonContent(input.getChild(RequestsTags.TAG_RESPONSE_JSON).getString());
    }

    @Override
    public void visit(CredentialRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (dom.containsChild(CoreTags.TAG_USER_NAME)) {
            rsp.setName(dom.getChild(CoreTags.TAG_USER_NAME).getString());
        }
    }

    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(bytes));
        do {
            switch (reader.getNextTag()) {
                case RequestsTags.TAG_RESULT_VALUE:
                    String json = reader.getString();
                    rsp.setJson(json);
                    break;
            }
        } while (reader.getCurrentTag() != CoreTags.TAG_EOF);
    }

    @Override
    public void visit(LogoutRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(RPCRequest.Response rsp) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        Object result = null;
        if (input.containsChild(RequestsTags.TAG_FORMAT) && input.containsChild(RequestsTags.TAG_FILE_NAME)) {
            ProtoNode dataNode = input.getChild(RequestsTags.TAG_RESULT_VALUE);
            ByteArrayOutputStream st = new ByteArrayOutputStream();
            st.write(dataNode.getData(), dataNode.getOffset(), dataNode.getSize());
            result = new Report(st.toByteArray(),
                    input.getChild(RequestsTags.TAG_FORMAT).getString(),
                    input.getChild(RequestsTags.TAG_FILE_NAME).getString());
        } else if (input.containsChild(RequestsTags.TAG_RESULT_VALUE)) {
            result = input.getChild(RequestsTags.TAG_RESULT_VALUE).getString();
        }
        rsp.setResult(result);
    }

    @Override
    public void visit(DisposeServerModuleRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(CommitRequest.Response rsp) throws Exception {
        ProtoReader reader = new ProtoReader((new ByteArrayInputStream(bytes)));
        rsp.setUpdated(reader.getInt(RequestsTags.UPDATED_TAG));
    }

    @Override
    public void visit(ModuleStructureRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        String text = dom.getChild(RequestsTags.TAG_RESULT_VALUE).getString();
        rsp.setJson(text);
        /*
         Collection<ProtoNode> parts = dom.getChildren(RequestsTags.TAG_RESOURCE_NAME);
         if (parts != null) {
         for (ProtoNode node : parts) {
         rsp.getStructure().add(node.getString());
         }
         }
         Collection<ProtoNode> clientDependencies = dom.getChildren(RequestsTags.TAG_MODULE_CLIENT_DEPENDENCY);
         if (clientDependencies != null) {
         for (ProtoNode node : clientDependencies) {
         rsp.getClientDependencies().add(node.getString());
         }
         }
         Collection<ProtoNode> serverDependencies = dom.getChildren(RequestsTags.TAG_MODULE_SERVER_DEPENDENCY);
         if (serverDependencies != null) {
         for (ProtoNode node : serverDependencies) {
         rsp.getServerDependencies().add(node.getString());
         }
         }
         Collection<ProtoNode> queriesDependecies = dom.getChildren(RequestsTags.TAG_MODULE_QUERY_DEPENDENCY);
         if (queriesDependecies != null) {
         for (ProtoNode node : queriesDependecies) {
         rsp.getQueryDependencies().add(node.getString());
         }
         }
         */
    }

    @Override
    public void visit(AppQueryRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (dom.containsChild(RequestsTags.TAG_RESULT_VALUE)) {
            if (!dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
                throw new NullPointerException("No query time-stamp specified");
            }
            rsp.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
            String text = dom.getChild(RequestsTags.TAG_RESULT_VALUE).getString();
            rsp.setAppQueryJson(text);
        }
    }

    @Override
    public void visit(ResourceRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (dom.containsChild(RequestsTags.TAG_RESOUCRE_CONTENT)) {
            if (!dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
                throw new NullPointerException("No resource time-stamp specified");
            }
            rsp.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
            ByteArrayOutputStream st = new ByteArrayOutputStream();
            ProtoNode dataNode = dom.getChild(RequestsTags.TAG_RESOUCRE_CONTENT);
            st.write(dataNode.getData(), dataNode.getOffset(), dataNode.getSize());
            rsp.setContent(st.toByteArray());
        }
    }

    @Override
    public void visit(ServerModuleStructureRequest.Response rsp) throws Exception {
        final ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (dom.containsChild(RequestsTags.TAG_RESULT_VALUE)) {
            if (!dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
                throw new NullPointerException("No server module info time-stamp specified");
            }
            rsp.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
            rsp.setInfoJson(dom.getChild(RequestsTags.TAG_RESULT_VALUE).getString());
        }
    }

}
