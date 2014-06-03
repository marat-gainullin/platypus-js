/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.AppClient;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.script.NoPublisherException;
import java.util.HashSet;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class AppPlatypusPrincipal extends PlatypusPrincipal {

    protected AppClient client;
    protected Set<String> allowedRoles = new HashSet<>();

    public AppPlatypusPrincipal(String aUserName, AppClient aClient) {
        super(aUserName);
        client = aClient;
    }

    @Override
    public boolean hasRole(String aRole) throws Exception {
        if (allowedRoles.contains(aRole)) {
            return true;
        } else {
            IsUserInRoleRequest rq = new IsUserInRoleRequest(IDGenerator.genID(), aRole);
            client.executeRequest(rq);
            boolean res = ((IsUserInRoleRequest.Response) rq.getResponse()).isRole();
            if (res) {
                allowedRoles.add(aRole);
            }
            return res;
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
