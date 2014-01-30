/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.h2;

import com.eas.designer.explorer.server.*;
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

/**
 *
 * @author vv
 */
public class H2DbServerNode extends AbstractNode implements ChangeListener {

    private final Action START_SERVER_ACTION = new StartH2DbServerAction();
    private final Action STOP_SERVER_ACTION = new StopH2DbServerAction();
    private static final String SERVER_PACKAGE_PREFIX = "com/eas/designer/explorer/server/"; //NOI18N
    private static final String H2_PACKAGE_PREFIX = "com/eas/designer/explorer/h2/"; //NOI18N
    private static final Image ICON_BASE = ImageUtilities.icon2Image(ImageUtilities.loadImageIcon(H2_PACKAGE_PREFIX + "h2.png", true)); //NOI18N
    private static final Image RUNNING_ICON = ImageUtilities.icon2Image(ImageUtilities.loadImageIcon(SERVER_PACKAGE_PREFIX + "running.png", true)); //NOI18N
    private static final Image WAITING_ICON = ImageUtilities.icon2Image(ImageUtilities.loadImageIcon(SERVER_PACKAGE_PREFIX + "waiting.png", true)); //NOI18N
    
    private final H2Dabatabase serverInstance;
    protected Action[] actions;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public H2DbServerNode(H2Dabatabase aServer) {
        super(Children.LEAF);
        serverInstance = aServer;
        serverInstance.addChangeListener(WeakListeners.change(this, serverInstance));
    }
    
    public H2Dabatabase getServer() {
        return serverInstance;
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
        aList.add(START_SERVER_ACTION);
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
