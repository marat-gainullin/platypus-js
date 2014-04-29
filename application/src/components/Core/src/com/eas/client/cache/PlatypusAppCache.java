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
    public String translateScriptPath(String aName) throws Exception {
        String res = generatePath(aName) + File.separator;
        ApplicationElement appElement = get(aName);
        if(appElement != null && appElement.getType() != ClientConstants.ET_RESOURCE){
            res += "." + PlatypusFiles.JAVASCRIPT_EXTENSION;
        }
        return res;
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
