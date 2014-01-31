/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.h2;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author vv
 */
public class StartH2DbServerAction extends AbstractAction implements ContextAwareAction {

    RequestProcessor RP = new RequestProcessor(StartH2DbServerAction.class);

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
                    return serverInstance.canStart();
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    RP.post(new Runnable() {
                        @Override
                        public void run() {
                            serverInstance.start();
                        }
                    });

                }

                @Override
                public Object getValue(String key) {
                    if (Action.NAME.equals(key)) {
                        return NbBundle.getMessage(StartH2DbServerAction.class, "CTL_StartServerAction"); // NOI18N
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
