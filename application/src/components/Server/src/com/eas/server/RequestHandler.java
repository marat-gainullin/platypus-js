/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.ErrorResponse;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @param <T>
 * @author pk
 */
public abstract class RequestHandler<T extends Request> {

    private static final String GENERAL_EXCEPTION_MESSAGE = "Exception on request of type %d | %s.";
    private static final String ACCESS_CONTROL_EXCEPTION_MESSAGE = "AccessControlException on request of type %d | %s. Message: %s.";
    private static final String SQL_EXCEPTION_MESSAGE = "SQLException on request of type %d | %s. Message: %s. sqlState: %s, errorCode: %d";

    private final T request;
    private final PlatypusServerCore serverCore;

    public RequestHandler(PlatypusServerCore aServerCore, T aRequest) {
        super();
        serverCore = aServerCore;
        request = aRequest;
    }

    public T getRequest() {
        return request;
    }

    public SessionManager getSessionManager() {
        assert serverCore != null;
        return serverCore.getSessionManager();
    }

    public PlatypusServerCore getServerCore() {
        return serverCore;
    }

    public Response call(Consumer<Response> onComplete) {
        if (onComplete != null) {
            try {
                handle((Response aResponse) -> {
                    onComplete.accept(aResponse);
                }, (Exception ex) -> {
                    if (ex instanceof SQLException) {
                        SQLException sex = (SQLException)ex;
                        Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, String.format(SQL_EXCEPTION_MESSAGE, request.getType(), request.getClass().getSimpleName(), sex.getMessage(), sex.getSQLState(), sex.getErrorCode()), sex);
                        onComplete.accept(new ErrorResponse(sex));
                    } else if (ex instanceof AccessControlException) {
                        AccessControlException aex = (AccessControlException)ex;
                        Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, String.format(ACCESS_CONTROL_EXCEPTION_MESSAGE, request.getType(), request.getClass().getSimpleName(), aex.getMessage()), aex);
                        onComplete.accept(new ErrorResponse(aex));
                    } else {                        
                        Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, String.format(GENERAL_EXCEPTION_MESSAGE, request.getType(), request.getClass().getSimpleName()), ex);
                        onComplete.accept(new ErrorResponse(ex.getMessage() != null && !ex.getMessage().isEmpty() ? ex.getMessage() : ex.getClass().getSimpleName()));
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        } else {
            try {
                return handle(null, null);
            } catch (SQLException ex) {
                Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, String.format(SQL_EXCEPTION_MESSAGE, request.getType(), request.getClass().getSimpleName(), ex.getMessage(), ex.getSQLState(), ex.getErrorCode()), ex);
                return new ErrorResponse(ex);
            } catch (AccessControlException ex) {
                Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, String.format(ACCESS_CONTROL_EXCEPTION_MESSAGE, request.getType(), request.getClass().getSimpleName(), ex.getMessage()), ex);
                return new ErrorResponse(ex);
            } catch (Exception t) {
                Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, String.format(GENERAL_EXCEPTION_MESSAGE, request.getType(), request.getClass().getSimpleName()), t);
                return new ErrorResponse(t.getMessage() != null && !t.getMessage().isEmpty() ? t.getMessage() : t.getClass().getSimpleName());
            }
        }
    }
    
    protected abstract Response handle(Consumer<Response> onSuccess, Consumer<Exception> onFailure) throws Exception;
}
