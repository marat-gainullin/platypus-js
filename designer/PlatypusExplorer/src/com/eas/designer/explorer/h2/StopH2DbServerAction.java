/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.h2;

import com.eas.designer.explorer.server.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class StopH2DbServerAction extends AbstractAction implements ContextAwareAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        H2DbServerNode contextNode = actionContext.lookup(H2DbServerNode.class);
        if (contextNode != null) {
            final H2Dabatabase serverInstance = contextNode.getServer();
            return new AbstractAction() {
                @Override
                public boolean isEnabled() {
                    return serverInstance.getServerState() != ServerState.STOPPED;
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
                        return NbBundle.getMessage(StopH2DbServerAction.class, "CTL_CancelServerAction"); // NOI18N
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
