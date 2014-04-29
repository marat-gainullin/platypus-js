/*
 * 
 * Platypus requests visitor
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.HelloRequest;

/**
 *
 * @author kl
 */
public interface PlatypusRequestVisitor {
    
    public void visit(AppQueryRequest rq) throws Exception;
    
    public void visit(LoginRequest rq) throws Exception;
    
    public void visit(LogoutRequest rq) throws Exception;
    
    public void visit(CommitRequest rq) throws Exception;
    
    public void visit(CreateServerModuleRequest rq) throws Exception;
    
    public void visit(DisposeServerModuleRequest rq) throws Exception;
    
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception;
   
    public void visit(AppElementChangedRequest rq) throws Exception;
    
    public void visit(DbTableChangedRequest rq) throws Exception;
    
    public void visit(HelloRequest rq) throws Exception;
    
    public void visit(ExecuteQueryRequest rq) throws Exception;
    
    public void visit(KeepAliveRequest rq) throws Exception;
    
    public void visit(StartAppElementRequest rq) throws Exception;

    public void visit(IsUserInRoleRequest rq) throws Exception;
    
    public void visit(IsAppElementActualRequest rq) throws Exception;
    
    public void visit(AppElementRequest rq) throws Exception;
}
