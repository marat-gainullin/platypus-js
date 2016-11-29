/*
 * 
 * Platypus requests visitor
 */
package com.eas.client.threetier.requests;

/**
 *
 * @author kl
 */
public interface PlatypusRequestVisitor {
    
    public void visit(AppQueryRequest rq) throws Exception;
    
    public void visit(LogoutRequest rq) throws Exception;
    
    public void visit(CommitRequest rq) throws Exception;
    
    public void visit(ModuleStructureRequest rq) throws Exception;
    
    public void visit(ResourceRequest rq) throws Exception;
    
    public void visit(ServerModuleStructureRequest rq) throws Exception;
    
    public void visit(DisposeServerModuleRequest rq) throws Exception;
    
    public void visit(RPCRequest rq) throws Exception;
   
    public void visit(ExecuteQueryRequest rq) throws Exception;
    
    public void visit(CredentialRequest rq) throws Exception;
    
}
