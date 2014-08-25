/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.requests.DbTableChangedRequest;
import com.eas.server.PlatypusServerCore;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class DbTableChangedRequestHandler extends CommonRequestHandler<DbTableChangedRequest, DbTableChangedRequest.Response> {

    public DbTableChangedRequestHandler(PlatypusServerCore aServerCore, DbTableChangedRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Consumer<DbTableChangedRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            getServerCore().getDatabasesClient().dbTableChanged(getRequest().getDatabaseId(), getRequest().getSchema(), getRequest().getTable());
            if (onSuccess != null) {
                onSuccess.accept(new DbTableChangedRequest.Response());
            }
        } catch (Exception ex) {
            if (onFailure != null) {
                onFailure.accept(ex);
            }
        }
    }
}
