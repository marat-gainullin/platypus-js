/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.serial.BinaryRowsetReader;
import com.eas.client.ServerModuleInfo;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.report.Report;
import com.eas.client.threetier.PlatypusRowsetReader;
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
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoReaderException;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import com.eas.script.ScriptUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public class PlatypusResponseReader implements PlatypusResponseVisitor {

    protected byte[] bytes;
    protected int offset;
    protected int size;

    public PlatypusResponseReader(byte[] aBytes) {
        this(aBytes, 0, aBytes.length);
    }

    public PlatypusResponseReader(byte[] aBytes, int aOffset, int aSize) {
        super();
        bytes = aBytes;
        offset = aOffset;
        size = aSize;
    }

    @Override
    public void visit(ErrorResponse rsp) throws Exception {
        if (bytes != null) {
            final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes, offset, size);
            if (input.containsChild(RequestsTags.TAG_RESPONSE_ERROR)) {
                rsp.setErrorMessage(input.getChild(RequestsTags.TAG_RESPONSE_ERROR).getString());
            }
            if (input.containsChild(RequestsTags.TAG_RESPONSE_SQL_ERROR_CODE)) {
                rsp.setSqlErrorCode(input.getChild(RequestsTags.TAG_RESPONSE_SQL_ERROR_CODE).getInt());
            }
            if (input.containsChild(RequestsTags.TAG_RESPONSE_SQL_ERROR_STATE)) {
                rsp.setSqlState(input.getChild(RequestsTags.TAG_RESPONSE_SQL_ERROR_STATE).getString());
            }
            rsp.setAccessControl(input.containsChild(RequestsTags.TAG_RESPONSE_ACCESS_CONTROL));
        }
    }

    @Override
    public void visit(HelloRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(CredentialRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (dom.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            rsp.setAppElementName(dom.getChild(RequestsTags.TAG_MODULE_NAME).getString());
        }
    }

    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(bytes));
        do {
            switch (reader.getNextTag()) {
                case RequestsTags.TAG_ROWSET:
                    BinaryRowsetReader rsReader = new PlatypusRowsetReader(rsp.getExpectedFields());
                    Rowset rowset = rsReader.read(reader.getSubStream());
                    rowset.beforeFirst();
                    rsp.setRowset(rowset);
                    break;
                case RequestsTags.TAG_UPDATE_COUNT:
                    rsp.setUpdateCount(reader.getInt());
                    break;

            }
        } while (reader.getCurrentTag() != CoreTags.TAG_EOF);
    }

    @Override
    public void visit(LogoutRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(LoginRequest.Response rsp) throws Exception {
        ProtoReader reader = new ProtoReader((new ByteArrayInputStream(bytes)));
        rsp.setSessionId(reader.getString(RequestsTags.TAG_SESSION_ID));
    }

    @Override
    public void visit(KeepAliveRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(IsUserInRoleRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        rsp.setRole(dom.containsChild(RequestsTags.TAG_ROLE));
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest.Response rsp) throws Exception {
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
            result = ScriptUtils.parseDates(ScriptUtils.parseJson(input.getChild(RequestsTags.TAG_RESULT_VALUE).getString()));
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
    }

    @Override
    public void visit(AppQueryRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (dom.containsChild(RequestsTags.TAG_QUERY_ID)) {
            if (!dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
                throw new NullPointerException("No query time-stamp specified");
            }
            rsp.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
            PlatypusQuery appQuery = new PlatypusQuery(null);
            if (!dom.containsChild(RequestsTags.TAG_FIELDS)) {
                throw new ProtoReaderException("Query fields are not specified");
            }
            appQuery.setEntityId(dom.getChild(RequestsTags.TAG_QUERY_ID).getString());
            if (dom.containsChild(RequestsTags.TAG_DML)) {
                appQuery.setManual(dom.getChild(RequestsTags.TAG_DML).getInt() == 1);
            }
            ProtoNode titleNode = dom.getChild(RequestsTags.TAG_TITLE);
            if (titleNode != null) {
                appQuery.setTitle(titleNode.getString());
            }

            BinaryRowsetReader rsReader = new BinaryRowsetReader();
            Fields fields = rsReader.parseFieldsNode(dom.getChild(RequestsTags.TAG_FIELDS));
            appQuery.setFields(fields);
            List<ProtoNode> paramsNodes = dom.getChildren(RequestsTags.TAG_QUERY_SQL_PARAMETER);
            for (ProtoNode node : paramsNodes) {
                appQuery.getParameters().add(PlatypusRequestReader.readParameter(node));
            }
            List<ProtoNode> rolesNodes = dom.getChildren(RequestsTags.TAG_READ_ROLE);
            if (rolesNodes != null) {
                for (ProtoNode node : rolesNodes) {
                    appQuery.getReadRoles().add(node.getString());
                }
            }
            rolesNodes = dom.getChildren(RequestsTags.TAG_WRITE_ROLE);
            if (rolesNodes != null) {
                for (ProtoNode node : rolesNodes) {
                    appQuery.getWriteRoles().add(node.getString());
                }
            }
            rsp.setAppQuery(appQuery);
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
    public void visit(CreateServerModuleRequest.Response rsp) throws Exception {
        final ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (dom.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            if (!dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
                throw new NullPointerException("No server module info time-stamp specified");
            }
            rsp.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
            String moduleName = dom.getChild(RequestsTags.TAG_MODULE_NAME).getString();
            boolean permitted = dom.containsChild(RequestsTags.TAG_MODULE_PERMITTED);
            Set<String> functionNames = new HashSet<>();
            if (dom.containsChild(RequestsTags.TAG_MODULE_FUNCTION_NAMES)) {
                List<ProtoNode> functionNodes = dom.getChild(RequestsTags.TAG_MODULE_FUNCTION_NAMES).getChildren(RequestsTags.TAG_MODULE_FUNCTION_NAME);
                for (ProtoNode functionNode : functionNodes) {
                    assert functionNode != null;
                    functionNames.add(functionNode.getString());
                }
            }
            rsp.setInfo(new ServerModuleInfo(moduleName, functionNames, permitted));
        }
    }

}
