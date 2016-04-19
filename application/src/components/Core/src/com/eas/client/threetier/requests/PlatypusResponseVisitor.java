/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

/**
 *
 * @author mg
 */
public interface PlatypusResponseVisitor {

    public void visit(ExceptionResponse rsp) throws Exception;

    public void visit(SqlExceptionResponse rsp) throws Exception;

    public void visit(AccessControlExceptionResponse rsp) throws Exception;

    public void visit(JsonExceptionResponse rsp) throws Exception;
    
    public void visit(CredentialRequest.Response rsp) throws Exception;

    public void visit(ExecuteQueryRequest.Response rsp) throws Exception;

    public void visit(LogoutRequest.Response rsp) throws Exception;

    public void visit(RPCRequest.Response rsp) throws Exception;

    public void visit(DisposeServerModuleRequest.Response rsp) throws Exception;

    public void visit(ServerModuleStructureRequest.Response rsp) throws Exception;

    public void visit(CommitRequest.Response rsp) throws Exception;

    public void visit(ModuleStructureRequest.Response rsp) throws Exception;

    public void visit(ResourceRequest.Response rsp) throws Exception;

    public void visit(AppQueryRequest.Response rsp) throws Exception;
}
