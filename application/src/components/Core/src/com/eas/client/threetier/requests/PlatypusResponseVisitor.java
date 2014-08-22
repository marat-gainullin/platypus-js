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

    public void visit(ErrorResponse rsp) throws Exception;

    public void visit(HelloRequest.Response rsp) throws Exception;

    public void visit(StartAppElementRequest.Response rsp) throws Exception;

    public void visit(ExecuteQueryRequest.Response rsp) throws Exception;

    public void visit(LogoutRequest.Response rsp) throws Exception;

    public void visit(LoginRequest.Response rsp) throws Exception;

    public void visit(KeepAliveRequest.Response rsp) throws Exception;

    public void visit(IsUserInRoleRequest.Response rsp) throws Exception;

    public void visit(IsAppElementActualRequest.Response rsp) throws Exception;

    public void visit(ExecuteServerModuleMethodRequest.Response rsp) throws Exception;

    public void visit(DisposeServerModuleRequest.Response rsp) throws Exception;

    public void visit(DbTableChangedRequest.Response rsp) throws Exception;

    public void visit(CreateServerModuleRequest.Response rsp) throws Exception;

    public void visit(CommitRequest.Response rsp) throws Exception;

    public void visit(AppQueryRequest.Response rsp) throws Exception;

    public void visit(AppElementRequest.Response rsp) throws Exception;

    public void visit(AppElementChangedRequest.Response rsp) throws Exception;
}
