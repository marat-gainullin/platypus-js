/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.threetier.Response;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import com.eas.server.filter.AppElementsFilter;
import com.eas.server.handlers.AppElementRequestHandler;
import java.security.AccessControlException;

/**
 *
 * @author mg
 */
public class FilteredAppElementRequestHandler extends SessionRequestHandler<FilteredAppElementRequest> {

    protected AppElementRequestHandler appElementHandler;
    
    public FilteredAppElementRequestHandler(PlatypusServerCore aServerCore, Session aSession, FilteredAppElementRequest aRequest) {
        super(aServerCore, aSession, aRequest);
        appElementHandler = new AppElementRequestHandler(aServerCore, aSession, aRequest);
    }

    @Override
    protected Response handle2() throws Exception {
        AppElementsFilter.Filtered filtered = getServerCore().getBrowsersFilter().get(getRequest().getAppElementId());
        if (filtered != null) {
            if (filtered.rolesAllowed != null && !getServerCore().getPrincipal().hasAnyRole(filtered.rolesAllowed)) {
                throw new AccessControlException(String.format(AppElementRequestHandler.ACCESS_DENIED_MSG, getRequest().getAppElementId(), getRequest().getAppElementId(), getServerCore().getPrincipal().getName()));
            } else {
                return new FilteredAppElementRequest.FilteredResponse(filtered);
            }
        } else {
            appElementHandler.run();
            return appElementHandler.getResponse();
        }
    }
}
