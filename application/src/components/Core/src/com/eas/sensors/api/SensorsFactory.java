/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.sensors.api;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.apache.mina.core.service.IoAcceptor;

/**
 *
 * @author Andrew
 */
public abstract class SensorsFactory {
    
    private static SensorsFactory instance;

    public static SensorsFactory getInstance() {
        return instance;
    }

    public static void init(SensorsFactory aValue) {
        if (instance == null) {
            instance = aValue;
        } else {
            throw new IllegalStateException("Already initialized.");
        }
    }
    
    public abstract boolean isSupported(String aProtocol);
    
    public abstract IoAcceptor create(String aProtocol, Executor aExecutor, int aSessionIdleTimeout, int aSessionIdleCheckInterval, PacketReciever aReciver) throws Exception;
    
    public abstract void sendRequest(Object aRequest, Consumer aCallback);
}
