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
    public void visit(LogoutRequest rq) throws Exception {
        response = new LogoutRequest.Response();
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        response = new CommitRequest.Response(0);
    }

    @Override
    public void visit(ServerModuleStructureRequest rq) throws Exception {
        response = new ServerModuleStructureRequest.Response(null);
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        response = new DisposeServerModuleRequest.Response();
    }

    @Override
    public void visit(RPCRequest rq) throws Exception {
        response = new RPCRequest.Response(null);
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        response = new ExecuteQueryRequest.Response(null, rq.getExpectedFields());
    }

    @Override
    public void visit(CredentialRequest rq) throws Exception {
        response = new CredentialRequest.Response(null);
    }

    @Override
    public void visit(ModuleStructureRequest rq) throws Exception {
        response = new ModuleStructureRequest.Response(null);
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
        response = new ResourceRequest.Response();
    }
}
