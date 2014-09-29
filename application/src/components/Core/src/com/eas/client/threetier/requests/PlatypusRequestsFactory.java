/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import com.eas.proto.ProtoReaderException;
import java.io.IOException;

/**
 *
 * @author mg
 */
public class PlatypusRequestsFactory {

    public static Request create(int aType) throws IOException, ProtoReaderException {
        switch (aType) {
            case Requests.rqHello:
                return new HelloRequest();
            case Requests.rqLogin:
                return new LoginRequest();
            // Database access requests
            case Requests.rqExecuteQuery:
                return new ExecuteQueryRequest();
            case Requests.rqCommit:
                return new CommitRequest();
            case Requests.rqAppQuery:
                return new AppQueryRequest();
            // Server modules requests
            case Requests.rqCreateServerModule:
                return new CreateServerModuleRequest();
            case Requests.rqDisposeServerModule:
                return new DisposeServerModuleRequest();
            case Requests.rqExecuteServerModuleMethod:
                return new ExecuteServerModuleMethodRequest();
            case Requests.rqLogout:
                return new LogoutRequest();
            case Requests.rqKeepAlive:
                return new KeepAliveRequest();
            case Requests.rqCredential:
                return new CredentialRequest();
            default:
                return null;
        }
    }
}
