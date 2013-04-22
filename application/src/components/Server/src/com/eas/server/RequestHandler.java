/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @param <T>
 * @author pk
 */
public abstract class RequestHandler<T extends Request> implements Runnable {

    private T request;
    private PlatypusServerCore serverCore;
    private Response response;

    public RequestHandler(PlatypusServerCore aServerCore, T aRequest) {
        super();
        serverCore = aServerCore;
        request = aRequest;
    }

    public T getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public SessionManager getSessionManager() {
        assert serverCore != null;
        return serverCore.getSessionManager();
    }

    public PlatypusServerCore getServerCore() {
        return serverCore;
    }

    @Override
    public void run() {
        try {
            response = handle();
        } catch (SQLException ex) {
            Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, String.format("SQLException on request %d of type %d | %s. Message: %s. sqlState: %s, errorCode: %d", request.getID(), request.getType(), request.getClass().getSimpleName(), ex.getMessage(), ex.getSQLState(), ex.getErrorCode()), ex);
            response = new ErrorResponse(request.getID(), ex);
        } catch (AccessControlException ex) {
            Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, String.format("AccessControlException on request %d of type %d | %s. Message: %s.", request.getID(), request.getType(), request.getClass().getSimpleName(), ex.getMessage()), ex);
            response = new ErrorResponse(request.getID(), ex);
        } catch (Throwable t) {
            Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, String.format("Exception on request %d of type %d | %s.", request.getID(), request.getType(), request.getClass().getSimpleName()), t);
            response = new ErrorResponse(request.getID(), t.getMessage() != null && !t.getMessage().isEmpty() ? t.getMessage() : t.getClass().getSimpleName());
        }
    }

    protected abstract Response handle() throws Exception;
}
