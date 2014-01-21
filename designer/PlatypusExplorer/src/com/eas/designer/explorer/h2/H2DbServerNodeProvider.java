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
        synchronized (this) {
            List<Node> newList = new ArrayList<>();
            Node node = new H2DbServerNode(H2Dabatabase.getDefault());
            newList.add(node);
            setNodes(newList);
        }
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
