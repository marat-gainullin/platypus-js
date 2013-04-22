/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class StopServerAction extends AbstractAction implements ContextAwareAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        Node contextNode = actionContext.lookup(Node.class);
        if (contextNode != null && contextNode.getLookup() != null) {
            final PlatypusServerInstance serverInstance = contextNode.getLookup().lookup(PlatypusServerInstance.class);
            return new AbstractAction() {
                @Override
                public boolean isEnabled() {
                    return serverInstance.getServerState() != PlatypusServerInstance.ServerState.STOPPED;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    serverInstance.stop();
                    try {
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                @Override
                public Object getValue(String key) {
                    if (Action.NAME.equals(key)) {
                        return NbBundle.getMessage(StopServerAction.class, "CTL_CancelServerAction"); // NOI18N
                    } else {
                        return super.getValue(key);
                    }
                }
            };
        } else {
            return null;
        }
    }

}
