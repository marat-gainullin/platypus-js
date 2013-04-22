/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import com.eas.proto.ProtoReaderException;
import java.io.IOException;

/**
 *
 * @author mg
 */
public class PlatypusRequestsFactory {
    
    public static Request create(long aRequestId, int aType) throws IOException, ProtoReaderException
    {
        switch (aType)
        {
            case Requests.rqHello:
                return new HelloRequest(aRequestId);
            case Requests.rqLogin:
                return new LoginRequest(aRequestId);
            // Database access requests
            case Requests.rqExecuteQuery:
                return new ExecuteQueryRequest(aRequestId);
            case Requests.rqCommit:
                return new CommitRequest(aRequestId);
            case Requests.rqAppQuery:
                return new AppQueryRequest(aRequestId);
            // Server modules requests
            case Requests.rqCreateServerModule:
                return new CreateServerModuleRequest(aRequestId);
            case Requests.rqDisposeServerModule:
                return new DisposeServerModuleRequest(aRequestId);
            case Requests.rqExecuteServerModuleMethod:
                return new ExecuteServerModuleMethodRequest(aRequestId);
            case Requests.rqLogout:
                return new LogoutRequest(aRequestId);
            case Requests.rqAppElementChanged:
                return new AppElementChangedRequest(aRequestId);
            case Requests.rqDbTableChanged:
                return new DbTableChangedRequest(aRequestId);
            case Requests.rqKeepAlive:
                return new KeepAliveRequest(aRequestId);
            case Requests.rqOutHash:
                return new OutHashRequest(aRequestId);
            case Requests.rqIsAppElementActual:
                return new IsAppElementActualRequest(aRequestId);
            case Requests.rqAppElement:
                return new AppElementRequest(aRequestId);
            case Requests.rqIsUserInRole:
                return new IsUserInRoleRequest(aRequestId);
            case Requests.rqStartAppElement:
                return new StartAppElementRequest(aRequestId);
            case Requests.rqExecuteReport:
                return new ExecuteServerReportRequest(aRequestId);
            default:
                return null;
        }
    }
}
