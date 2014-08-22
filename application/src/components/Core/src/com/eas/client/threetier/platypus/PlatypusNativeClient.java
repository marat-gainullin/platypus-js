/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.threetier.PlatypusClient;
import java.net.URI;

/**
 *
 * @author mg
 */
public class PlatypusNativeClient extends PlatypusClient {

    public PlatypusNativeClient(String aUrl) throws Exception {
        super(aUrl, new PlatypusNativeConnection(createSSLContext(), new URI(aUrl).getHost(), new URI(aUrl).getPort()));
    }
}
