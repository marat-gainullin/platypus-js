/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.AppClient;
import com.eas.client.threetier.PlatypusClient;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusHttpClient extends PlatypusClient implements AppClient {

    public PlatypusHttpClient(String aUrl) throws Exception {
        super(aUrl, new PlatypusHttpConnection(aUrl));
    }
}
