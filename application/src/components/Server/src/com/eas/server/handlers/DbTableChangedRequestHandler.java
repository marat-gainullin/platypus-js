/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.server.handlers;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.DbTableChangedRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;

/**
 *
 * @author mg
 */
public class DbTableChangedRequestHandler extends SessionRequestHandler<DbTableChangedRequest>
{
    public DbTableChangedRequestHandler(PlatypusServerCore server, Session session, DbTableChangedRequest rq)
    {
        super(server, session, rq);
    }

    @Override
    public Response handle2() throws Exception
    {
        getServerCore().getDatabasesClient().dbTableChanged(getRequest().getDatabaseId(), getRequest().getSchema(), getRequest().getTable());
        return new DbTableChangedRequest.Response(getRequest().getID());
    }
}
