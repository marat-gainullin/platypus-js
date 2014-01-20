/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.h2;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.db.explorer.node.NodeProvider;
import org.netbeans.api.db.explorer.node.NodeProviderFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class H2DbServerNodeProvider extends NodeProvider {
    
    private static H2DbServerNodeProvider DEFAULT;
    private static H2DbServerInstance devDatabaseServer;
    
    public static NodeProviderFactory getFactory() {
        return FactoryHolder.FACTORY;
    }
    
    public static H2DbServerNodeProvider getDefault() {
        return DEFAULT;
    }

    private H2DbServerNodeProvider(Lookup lookup) {
        super(lookup);
    }
    
    @Override
    protected void initialize() {
        List<Node> newList = new ArrayList<>();
        Node node = new H2DbServerNode(getPlatypusDevServer());
        newList.add(node);
        setNodes(newList);
    }
    
    public static synchronized H2DbServerInstance getPlatypusDevServer() {
        if (devDatabaseServer == null) {
            devDatabaseServer = new H2DbServerInstance();
        }
        return devDatabaseServer;
    }
    
    private static class FactoryHolder {
        static final NodeProviderFactory FACTORY = new NodeProviderFactory() {
            @Override
            public H2DbServerNodeProvider createInstance(Lookup lookup) {
                DEFAULT = new H2DbServerNodeProvider(lookup);
                return DEFAULT;
            }
        };
    }
    

}
