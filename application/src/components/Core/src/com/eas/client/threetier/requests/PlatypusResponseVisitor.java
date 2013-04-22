/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.HelloRequest;

/**
 *
 * @author mg
 */
public interface PlatypusResponseVisitor {

    public void visit(ErrorResponse rsp) throws Exception;

    public void visit(HelloRequest.Response rsp) throws Exception;

    public void visit(StartAppElementRequest.Response rsp) throws Exception;

    public void visit(RowsetResponse rsp) throws Exception;

    public void visit(OutHashRequest.Response rsp) throws Exception;

    public void visit(LogoutRequest.Response rsp) throws Exception;

    public void visit(LoginRequest.Response rsp) throws Exception;

    public void visit(KeepAliveRequest.Response rsp) throws Exception;

    public void visit(IsUserInRoleRequest.Response rsp) throws Exception;

    public void visit(IsAppElementActualRequest.Response rsp) throws Exception;

    public void visit(ExecuteServerReportRequest.Response rsp) throws Exception;

    public void visit(ExecuteServerModuleMethodRequest.Response rsp) throws Exception;

    public void visit(DisposeServerModuleRequest.Response rsp) throws Exception;

    public void visit(DbTableChangedRequest.Response rsp) throws Exception;

    public void visit(CreateServerModuleResponse rsp) throws Exception;

    public void visit(CommitRequest.Response rsp) throws Exception;

    public void visit(AppQueryResponse rsp) throws Exception;

    public void visit(AppElementRequest.Response rsp) throws Exception;

    public void visit(AppElementChangedRequest.Response rsp) throws Exception;
}
