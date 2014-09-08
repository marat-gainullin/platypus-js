/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Response;

/**
 *
 * @author mg
 */
public class PlatypusResponsesFactory implements PlatypusRequestVisitor {

    protected Response response;

    public PlatypusResponsesFactory() {
        super();
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        response = new AppQueryRequest.Response(null, null);
    }

    @Override
    public void visit(LoginRequest rq) throws Exception {
        response = new LoginRequest.Response(null);
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
        response = new LogoutRequest.Response();
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        response = new CommitRequest.Response(0);
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
        response = new CreateServerModuleRequest.Response(null);
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        response = new DisposeServerModuleRequest.Response();
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        response = new ExecuteServerModuleMethodRequest.Response(null);
    }

    @Override
    public void visit(HelloRequest rq) throws Exception {
        response = new HelloRequest.Response();
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        response = new ExecuteQueryRequest.Response(null, 0, rq.getExpectedFields());
    }

    @Override
    public void visit(KeepAliveRequest rq) throws Exception {
        response = new KeepAliveRequest.Response();
    }

    @Override
    public void visit(StartAppElementRequest rq) throws Exception {
        response = new StartAppElementRequest.Response(null);
    }

    @Override
    public void visit(IsUserInRoleRequest rq) throws Exception {
        response = new IsUserInRoleRequest.Response(false);
    }

    @Override
    public void visit(ModuleStructureRequest rq) throws Exception {
        response = new ModuleStructureRequest.Response();
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
        response = new ResourceRequest.Response();
    }
}
