/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.binary;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.serial.BinaryRowsetReader;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.PlatypusRowsetReader;
import com.eas.client.threetier.requests.AppElementChangedRequest;
import com.eas.client.threetier.requests.AppElementRequest;
import com.eas.client.threetier.requests.AppQueryResponse;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleResponse;
import com.eas.client.threetier.requests.DbTableChangedRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.client.threetier.requests.IsAppElementActualRequest;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import com.eas.client.threetier.requests.RowsetResponse;
import com.eas.client.threetier.requests.StartAppElementRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoReaderException;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import com.eas.script.ScriptUtils;
import com.eas.xml.dom.Source2XmlDom;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public void visit(ErrorResponse rsp) throws Exception {
        if (bytes != null) {
            final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
            if (input.containsChild(RequestsTags.TAG_RESPONSE_ERROR)) {
                rsp.setError(input.getChild(RequestsTags.TAG_RESPONSE_ERROR).getString());
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
    public void visit(StartAppElementRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (dom.containsChild(RequestsTags.TAG_APP_ELEMENT_ID)) {
            rsp.setAppElementId(dom.getChild(RequestsTags.TAG_APP_ELEMENT_ID).getString());
        }
    }

    @Override
    public void visit(RowsetResponse rsp) throws Exception {
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
    public void visit(IsAppElementActualRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        rsp.setActual(dom.containsChild(RequestsTags.TAG_ACTUAL));
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest.Response rsp) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        Object result = null;
        if (input.containsChild(RequestsTags.TAG_RESULT_VALUE)) {
            result = ScriptUtils.parseDates(ScriptUtils.parseJson(input.getChild(RequestsTags.TAG_RESULT_VALUE).getString()));
        }
        rsp.setResult(result);
    }

    @Override
    public void visit(DisposeServerModuleRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(DbTableChangedRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(CreateServerModuleResponse rsp) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        boolean permitted = false;
        if (!input.containsChild(RequestsTags.TAG_MODULE_ID)) {
            throw new ProtoReaderException("No module ID specified!");
        }
        rsp.setModuleName(input.getChild(RequestsTags.TAG_MODULE_ID).getString());
        if (input.containsChild(RequestsTags.TAG_MODULE_PERMITTED)) {
            permitted = input.getChild(RequestsTags.TAG_MODULE_PERMITTED).getBoolean();
        }
        rsp.setPermitted(permitted);
        if (input.containsChild(RequestsTags.TAG_MODULE_FUNCTION_NAMES)) {
            Set<String> functionNames = new HashSet<>();
            List<ProtoNode> functionNodes = input.getChild(RequestsTags.TAG_MODULE_FUNCTION_NAMES).getChildren(RequestsTags.TAG_MODULE_FUNCTION_NAME);
            for (ProtoNode functionNode : functionNodes) {
                assert functionNode != null;
                functionNames.add(functionNode.getString());
            }
            rsp.setFunctionsNames(functionNames);
        }
    }

    @Override
    public void visit(CommitRequest.Response rsp) throws Exception {
        ProtoReader reader = new ProtoReader((new ByteArrayInputStream(bytes)));
        rsp.setUpdated(reader.getInt(RequestsTags.UPDATED_TAG));
    }

    @Override
    public void visit(AppQueryResponse rsp) throws Exception {
        PlatypusQuery appQuery = new PlatypusQuery(null);
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_QUERY_ID)) {
            throw new ProtoReaderException("Query is not specified");
        }
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

    @Override
    public void visit(AppElementRequest.Response rsp) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (dom.containsChild(RequestsTags.TAG_APP_ELEMENT_ID)) {
            ApplicationElement appElement = new ApplicationElement();
            appElement.setId(dom.getChild(RequestsTags.TAG_APP_ELEMENT_ID).getString());
            appElement.setType(dom.getChild(RequestsTags.TAG_TYPE).getInt());
            appElement.setName(dom.getChild(RequestsTags.TAG_NAME).getString());
            if (dom.containsChild(RequestsTags.TAG_RESOURCE)) {
                ProtoNode resNode = dom.getChild(RequestsTags.TAG_RESOURCE);
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bStream.write(resNode.getData(), resNode.getOffset(), resNode.getSize());
                appElement.setBinaryContent(bStream.toByteArray());
            } else {
                if (dom.containsChild(RequestsTags.TAG_TEXT)) {
                    appElement.setContent(Source2XmlDom.transform(dom.getChild(RequestsTags.TAG_TEXT).getString()));
                }
                appElement.setTxtContentLength(dom.getChild(RequestsTags.TAG_TEXT_LENGTH).getLong());
                appElement.setTxtCrc32(dom.getChild(RequestsTags.TAG_TEXT_CRC32).getLong());
            }
            rsp.setAppElement(appElement);
        }
    }

    @Override
    public void visit(AppElementChangedRequest.Response rsp) throws Exception {
    }
}
