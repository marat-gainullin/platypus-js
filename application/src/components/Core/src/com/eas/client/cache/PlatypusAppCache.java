/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.cache;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.AppClient;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.threetier.requests.AppElementRequest;
import com.eas.client.threetier.requests.IsAppElementActualRequest;
import java.io.File;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class PlatypusAppCache extends AppElementsCache{

    protected AppClient client;
    
    public PlatypusAppCache(AppClient aClient) throws Exception{
        super("app-" + String.valueOf(aClient.getUrl().hashCode()));
        client = aClient;
    }

    @Override
    public String getApplicationPath() {
        return client.getUrl();
    }

    @Override
    public boolean isActual(String aId, long aTxtContentLength, long aTxtCrc32, Consumer<Boolean> onSuccess, Consumer<Exception> onFailure) throws Exception {
        return client.isActual(aId, aTxtContentLength, aTxtCrc32, onSuccess, onFailure);
    }

    @Override
    protected ApplicationElement achieveAppElement(String aAppElementId, Consumer<ApplicationElement> onSuccess, Consumer<Exception> onFailure) throws Exception {
        return client.getAppElement(aAppElementId, onSuccess, onFailure);
    }

}
