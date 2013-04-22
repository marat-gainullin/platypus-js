/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Mutex;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author vv
 */
public class PlatypusServerNode extends AbstractNode implements ChangeListener {

    private final Action STOP_SERVER_ACTION = new StopServerAction();
    private static final String PACKAGE_PREFIX = "com/eas/designer/explorer/server/"; //NOI18N
    private static final Image ICON_BASE = ImageUtilities.icon2Image(ImageUtilities.loadImageIcon(PACKAGE_PREFIX + "server.png", true)); //NOI18N
    private static final Image RUNNING_ICON = ImageUtilities.icon2Image(ImageUtilities.loadImageIcon(PACKAGE_PREFIX + "running.png", true)); //NOI18N
    private static final Image WAITING_ICON = ImageUtilities.icon2Image(ImageUtilities.loadImageIcon(PACKAGE_PREFIX + "waiting.png", true)); //NOI18N
    
    private final PlatypusServerInstance serverInstance;
    protected Action[] actions;

    public PlatypusServerNode(PlatypusServerInstance aServer) {
        super(Children.LEAF, Lookups.fixed(aServer));
        serverInstance = aServer;
        serverInstance.addChangeListener(WeakListeners.change(this, serverInstance));
    }

    @Override
    public String getDisplayName() {
        return serverInstance.getDisplayName();
    }

    @Override
    public String getName() {
        return serverInstance.getDisplayName();
    }

    @Override
    public Image getIcon(int type) {
        return badgeIcon(ICON_BASE);
    }
    
    @Override
    public Image getOpenedIcon(int type) {
        return badgeIcon(ICON_BASE);
    } 
    
    @Override
    public Action[] getActions(boolean context) {
        if (actions == null) {
            List<Action> lactions = new ArrayList<>();
            fillActions(lactions);
            actions = lactions.toArray(new Action[lactions.size()]);
        }
        return actions;
    }

    protected void fillActions(List<Action> aList) {
        aList.add(STOP_SERVER_ACTION);
    }

    private Image badgeIcon(Image origImg) {
        Image badge = null;
        switch (serverInstance.getServerState()) {
            case RUNNING:
                badge = RUNNING_ICON;
                break;
            case STARTING:
                badge = WAITING_ICON;
                break;
        }
        return badge != null ? ImageUtilities.mergeImages(origImg, badge, 15, 8) : origImg;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Mutex.EVENT.readAccess(new Runnable() {
            @Override
            public void run() {
                fireIconChange();
            }
        });
    }
}
