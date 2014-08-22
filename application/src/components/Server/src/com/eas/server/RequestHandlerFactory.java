/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.requests.*;
import com.eas.proto.ProtoReaderException;
import com.eas.server.handlers.*;
import java.io.IOException;

/**
 *
 * @author pk, mg
 */
public class RequestHandlerFactory implements PlatypusRequestVisitor {

    protected RequestHandler<?> handler;
    protected PlatypusServerCore serverCore;
    protected Session session;

    public RequestHandlerFactory(PlatypusServerCore aServerCore, Session aSession) {
        super();
        serverCore = aServerCore;
        session = aSession;
    }

    public RequestHandler<?> getHandler() {
        return handler;
    }

    /**
     * Creates appropriate request handler.
     *
     * @param serverCore PlatypusServerCore instance.
     * @param session Session instance.
     * @param rq Request instance (wraped or unwrapped)
     * @return appropriate request handler instance.
     * @throws IOException
     * @throws ProtoReaderException
     * @see PlatypusServerCore
     * @see Session
     * @see UnknownRequest
     */
    public static RequestHandler<?> getHandler(PlatypusServerCore serverCore, Session session, Request rq) throws Exception {
        RequestHandlerFactory factory = new RequestHandlerFactory(serverCore, session);
        rq.accept(factory);
        return factory.getHandler();
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        handler = new AppQueryRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(LoginRequest rq) throws Exception {
        handler = new LoginRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
        handler = new LogoutRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        handler = new CommitRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
        handler = new CreateServerModuleRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        handler = new DisposeServerModuleRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        handler = new ExecuteServerModuleMethodRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(AppElementChangedRequest rq) throws Exception {
        handler = new AppElementChangedRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(DbTableChangedRequest rq) throws Exception {
        handler = new DbTableChangedRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(HelloRequest rq) throws Exception {
        handler = new HelloRequestHandler(serverCore, rq);
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        handler = new ExecuteQueryRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(KeepAliveRequest rq) throws Exception {
        handler = new KeepAliveRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(StartAppElementRequest rq) throws Exception {
        handler = new StartAppElementRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(IsUserInRoleRequest rq) throws Exception {
        handler = new IsUserInRoleRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(IsAppElementActualRequest rq) throws Exception {
        handler = new IsAppElementActualRequestHandler(serverCore, session, rq);
    }

    @Override
    public void visit(AppElementRequest rq) throws Exception {
        handler = new AppElementRequestHandler(serverCore, session, rq);
    }
}
