/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.spi.server.ServerInstanceFactory;
import org.netbeans.spi.server.ServerInstanceProvider;

/**
 *
 * @author vv
 */
public class PlatypusServerInstanceProvider implements ServerInstanceProvider {

    private static PlatypusServerInstance platypusDevServer;

    @Override
    public List<ServerInstance> getInstances() {
        List<ServerInstance> instances = new ArrayList<>();
        ServerInstance instance = ServerInstanceFactory.createServerInstance(getPlatypusDevServer());
        instances.add(instance);
        return instances;
    }

    public static synchronized PlatypusServerInstance getPlatypusDevServer() {
        if (platypusDevServer == null) {
            platypusDevServer = new PlatypusServerInstance();
        }
        return platypusDevServer;
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
    }
}
