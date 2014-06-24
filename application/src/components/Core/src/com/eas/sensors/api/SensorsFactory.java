/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.sensors.api;

import java.util.concurrent.ExecutorService;
import org.apache.mina.core.service.IoAcceptor;

/**
 *
 * @author Andrew
 */
public interface SensorsFactory {
    
    public abstract boolean isSupported(String aProtocol);
    
    public IoAcceptor create(String aProtocol, int aNumWorkerThreads, int aSessionIdleTimeout, int aSessionIdleCheckInterval, PacketReciever aReciver) throws Exception;
    
    public IoAcceptor create(String aProtocol, int aSessionIdleTimeout, int aSessionIdleCheckInterval, PacketReciever aReciver, ExecutorService executor) throws Exception;
}
