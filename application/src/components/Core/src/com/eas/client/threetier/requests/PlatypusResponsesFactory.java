/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.Response;
import java.util.Collections;

/**
 *
 * @author mg
 */
public class PlatypusResponsesFactory implements PlatypusRequestVisitor {

    protected long requestId;
    protected Response response;

    public PlatypusResponsesFactory(long aRequestId) {
        super();
        requestId = aRequestId;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        response = new AppQueryResponse(requestId, null);
    }

    @Override
    public void visit(LoginRequest rq) throws Exception {
        response = new LoginRequest.Response(requestId, null);
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
        response = new LogoutRequest.Response(requestId);
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        response = new CommitRequest.Response(requestId, 0);
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
        response = new CreateServerModuleResponse(requestId, null, Collections.<String>emptySet(), true);
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        response = new DisposeServerModuleRequest.Response(requestId);
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        response = new ExecuteServerModuleMethodRequest.Response(requestId, null);
    }

    @Override
    public void visit(AppElementChangedRequest rq) throws Exception {
        response = new AppElementChangedRequest.Response(requestId);
    }

    @Override
    public void visit(DbTableChangedRequest rq) throws Exception {
        response = new DbTableChangedRequest.Response(requestId);
    }

    @Override
    public void visit(HelloRequest rq) throws Exception {
        response = new HelloRequest.Response(requestId);
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        response = new RowsetResponse(requestId, null, 0, rq.getExpectedFields());
    }

    @Override
    public void visit(KeepAliveRequest rq) throws Exception {
        response = new KeepAliveRequest.Response(requestId);
    }

    @Override
    public void visit(StartAppElementRequest rq) throws Exception {
        response = new StartAppElementRequest.Response(requestId, null);
    }

    @Override
    public void visit(IsUserInRoleRequest rq) throws Exception {
        response = new IsUserInRoleRequest.Response(requestId, false);
    }

    @Override
    public void visit(IsAppElementActualRequest rq) throws Exception {
        response = new IsAppElementActualRequest.Response(requestId, false);
    }

    @Override
    public void visit(AppElementRequest rq) throws Exception {
        response = new AppElementRequest.Response(requestId, null);
    }
}
