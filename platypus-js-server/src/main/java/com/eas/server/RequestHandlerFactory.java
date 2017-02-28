/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.*;
import com.eas.proto.ProtoReaderException;
import com.eas.server.handlers.*;
import java.io.IOException;

/**
 *
 * @author pk, mg
 */
public class RequestHandlerFactory implements PlatypusRequestVisitor {

    protected RequestHandler<? extends Request, ? extends Response> handler;
    protected PlatypusServerCore serverCore;

    public RequestHandlerFactory(PlatypusServerCore aServerCore) {
        super();
        serverCore = aServerCore;
    }

    public RequestHandler<? extends Request, ? extends Response> getHandler() {
        return handler;
    }

    /**
     * Creates appropriate request handler.
     *
     * @param serverCore PlatypusServerCore instance.
     * @param rq Request instance (wraped or unwrapped)
     * @return appropriate request handler instance.
     * @throws IOException
     * @throws ProtoReaderException
     * @see PlatypusServerCore
     * @see Session
     */
    public static RequestHandler<? extends Request, ? extends Response> getHandler(PlatypusServerCore serverCore, Request rq) throws Exception {
        RequestHandlerFactory factory = new RequestHandlerFactory(serverCore);
        rq.accept(factory);
        return factory.getHandler();
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        handler = new AppQueryRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
        handler = new LogoutRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        handler = new CommitRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(ServerModuleStructureRequest rq) throws Exception {
        handler = new ServerModuleStructureRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        handler = new DisposeServerModuleRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(RPCRequest rq) throws Exception {
        handler = new RPCRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        handler = new ExecuteQueryRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(CredentialRequest rq) throws Exception {
        handler = new CredentialRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(ModuleStructureRequest rq) throws Exception {
        handler = new ModuleStructureRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
        handler = new ResourceRequestHandler(serverCore, rq);
    }
}
