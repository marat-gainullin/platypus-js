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
            // Resources request
            case Requests.rqResource:
                return new ResourceRequest();
            // Data access requests
            case Requests.rqAppQuery:
                return new AppQueryRequest();
            case Requests.rqExecuteQuery:
                return new ExecuteQueryRequest();
            case Requests.rqCommit:
                return new CommitRequest();
            // Modules requests
            case Requests.rqModuleStructure:
                return new ModuleStructureRequest();
            // Server modules requests
            case Requests.rqCreateServerModule:
                return new ServerModuleStructureRequest();
            case Requests.rqDisposeServerModule:
                return new DisposeServerModuleRequest();
            case Requests.rqExecuteServerModuleMethod:
                return new RPCRequest();
            // User sessions requests
            case Requests.rqLogout:
                return new LogoutRequest();
            case Requests.rqCredential:
                return new CredentialRequest();
            default:
                return null;
        }
    }
}
