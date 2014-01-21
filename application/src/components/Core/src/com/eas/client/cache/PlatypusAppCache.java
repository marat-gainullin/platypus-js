/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.cache;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.AppClient;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.threetier.requests.AppElementRequest;
import com.eas.client.threetier.requests.IsAppElementActualRequest;

/**
 *
 * @author mg
 */
public class PlatypusAppCache extends AppElementsCache{

    protected AppClient client;
    
    public PlatypusAppCache(AppClient aClient) throws Exception{
        super();
        client = aClient;
    }

    @Override
    public String getApplicationPath() {
        return client.getUrl();
    }

    @Override
    public boolean isActual(String aId, long aTxtContentLength, long aTxtCrc32) throws Exception {
        IsAppElementActualRequest rq = new IsAppElementActualRequest(IDGenerator.genID(), aId, aTxtContentLength, aTxtCrc32);
        client.executeRequest(rq);
        return ((IsAppElementActualRequest.Response)rq.getResponse()).isActual();
    }

    @Override
    protected ApplicationElement achieveAppElement(String aId) throws Exception {
        AppElementRequest rq = new AppElementRequest(IDGenerator.genID(), aId);
        client.executeRequest(rq);
        return ((AppElementRequest.Response)rq.getResponse()).getAppElement();
    }

}
