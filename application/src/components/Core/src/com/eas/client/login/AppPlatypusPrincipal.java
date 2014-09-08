/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.client.threetier.requests.StartAppElementRequest;
import com.eas.script.NoPublisherException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class AppPlatypusPrincipal extends PlatypusPrincipal {

    protected PlatypusConnection conn;
    protected Set<String> allowedRoles = new HashSet<>();

    public AppPlatypusPrincipal(String aUserName, PlatypusConnection aConn) {
        super(aUserName);
        conn = aConn;
    }

    @Override
    public boolean hasRole(String aRole, Consumer<Boolean> onSuccess, Consumer<Exception> onFailure) throws Exception {
        IsUserInRoleRequest request = new IsUserInRoleRequest(aRole);
        if (onSuccess != null) {
            if (allowedRoles.contains(aRole)) {
                onSuccess.accept(true);
            } else {
                conn.<IsUserInRoleRequest.Response>enqueueRequest(request, (IsUserInRoleRequest.Response aResponse) -> {
                    if (aResponse.isRole()) {
                        allowedRoles.add(aRole);
                    }
                    onSuccess.accept(aResponse.isRole());
                }, (Exception aException) -> {
                    if (onFailure != null) {
                        onFailure.accept(aException);
                    }
                });
            }
            return false;
        } else {
            if (allowedRoles.contains(aRole)) {
                return true;
            } else {
                IsUserInRoleRequest.Response response = conn.executeRequest(request);
                if (response.isRole()) {
                    allowedRoles.add(aRole);
                }
                return response.isRole();
            }
        }
    }

    @Override
    public String getStartAppElement(Consumer<String> onSuccess, Consumer<Exception> onFailure) throws Exception {
        StartAppElementRequest request = new StartAppElementRequest();
        if (onSuccess != null) {
            conn.<StartAppElementRequest.Response>enqueueRequest(request, (StartAppElementRequest.Response aResponse) -> {
                onSuccess.accept(aResponse.getAppElementId());
            }, (Exception aException) -> {
                if (onFailure != null) {
                    onFailure.accept(aException);
                }
            });
            return null;
        } else {
            StartAppElementRequest.Response response = conn.executeRequest(request);
            return response.getAppElementId();
        }
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
