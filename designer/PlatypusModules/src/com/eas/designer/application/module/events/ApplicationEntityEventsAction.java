/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.events;

import com.eas.designer.application.module.nodes.ApplicationEntityNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

@ActionID(id = "com.eas.designer.application.module.events.ApplicationEntityEventsAction", category = "Edit")
@ActionRegistration(displayName = "#ApplicationEntityEventsAction")
public final class ApplicationEntityEventsAction extends CookieAction {

    @Override
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class<?>[]{ApplicationEntityEventsCookie.class};
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        // fallback code...
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ApplicationEntityEventsAction.class, "CTL_EntityEventsAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return getPopupPresenter();
    }

    /**
     * Returns a JMenuItem that presents this action in a Popup Menu.
     *
     * @return the JMenuItem representation for the action
     */
    @Override
    public JMenuItem getPopupPresenter() {
        JMenu popupMenu = new JMenu(
                NbBundle.getMessage(ApplicationEntityEventsAction.class, "CTL_EntityEventsAction")); // NOI18N

        popupMenu.setEnabled(isEnabled());
        HelpCtx.setHelpIDString(popupMenu, ApplicationEntityEventsAction.class.getName());

        popupMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                try {
                    JMenu menu = (JMenu) e.getSource();
                    createEventSubmenu(menu);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        return popupMenu;
    }

    private void createEventSubmenu(JMenu menu) throws Exception {
        if (menu.getMenuComponentCount() > 0) {
            menu.removeAll();
        }

        Node[] nodes = getActivatedNodes();
        if (nodes.length != 1) {
            return;
        }
        assert nodes[0] instanceof ApplicationEntityNode;
        ApplicationEntityNode entityNode = (ApplicationEntityNode) nodes[0];
        ResourceBundle bundle = NbBundle.getBundle(ApplicationEntityEventsAction.class);

        ApplicationEntityEventDesc[] events = ApplicationEntityEventDesc.getApplicableEvents();

        java.beans.EventSetDescriptor lastEventSetDesc = null;
        JMenu eventSetMenu = null;
        boolean eventSetHasHandlers = false;

        for (int i = 0; i < events.length; i++) {
            ApplicationEntityEventDesc event = events[i];
            String handler = (String) entityNode.getPropertyByName(event.getName()).getValue();
            JMenuItem jmi = null;

            if (handler == null || handler.isEmpty()) {
                jmi = new EventMenuItem(
                        MessageFormat.format(
                        bundle.getString("FMT_CTL_EventNoHandlers"), // NOI18N
                        new Object[]{event.getName()}),
                        entityNode,
                        event,
                        null);
            } else {
                jmi = new EventMenuItem(
                        MessageFormat.format(
                        bundle.getString("FMT_CTL_EventOneHandler"), // NOI18N
                        new Object[]{event.getName(), handler}),
                        entityNode,
                        event,
                        handler);
            }

            if (jmi != null) {
                if (event.getEventSetDescriptor() != lastEventSetDesc) {
                    if (eventSetHasHandlers) {
                        setBoldFontForMenuText(eventSetMenu);
                    }

                    String name = event.getEventSetDescriptor().getName();
                    eventSetMenu = new JMenu(name.substring(0, 1).toUpperCase()
                            + name.substring(1));
                    HelpCtx.setHelpIDString(eventSetMenu,
                            ApplicationEntityEventsAction.class.getName());
                    addSortedMenuItem(menu, eventSetMenu);
                    eventSetHasHandlers = false;
                    lastEventSetDesc = event.getEventSetDescriptor();
                }

                if (!(jmi instanceof JMenu)) {
                    jmi.addActionListener(getMenuItemListener());
                }

                HelpCtx.setHelpIDString(jmi, ApplicationEntityEventsAction.class.getName());

                if (handler != null && !handler.isEmpty()) {
                    eventSetHasHandlers = true;
                    setBoldFontForMenuText(jmi);
                }

                addSortedMenuItem(eventSetMenu, jmi);
            }
        }

        if (eventSetHasHandlers) {
            setBoldFontForMenuText(eventSetMenu);
        }
    }

    private static void setBoldFontForMenuText(JMenuItem mi) {
        java.awt.Font font = mi.getFont();
        mi.setFont(font.deriveFont(font.getStyle() | java.awt.Font.BOLD));
    }

    private static void addSortedMenuItem(JMenu menu, JMenuItem menuItem) {
        int n = menu.getMenuComponentCount();
        String text = menuItem.getText();
        for (int i = 0; i < n; i++) {
            String tx = ((JMenuItem) menu.getMenuComponent(i)).getText();
            if (text.compareTo(tx) < 0) {
                menu.add(menuItem, i);
                return;
            }
        }
        menu.add(menuItem);
    }

    private ActionListener getMenuItemListener() {
        if (menuItemListener == null) {
            menuItemListener = new EventMenuItemListener();
        }
        return menuItemListener;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return activatedNodes != null && activatedNodes.length == 1 && activatedNodes[0].getLookup().lookup(ApplicationEntityEventsCookie.class) != null;
    }

    // --------
    private static class EventMenuItem extends JMenuItem {

        private ApplicationEntityNode node;
        private ApplicationEntityEventDesc event;
        private String handlerName;

        EventMenuItem(String aText, ApplicationEntityNode aNode, ApplicationEntityEventDesc aEvent, String aHandlerName) {
            super(aText);
            node = aNode;
            event = aEvent;
            handlerName = aHandlerName;
        }

        public ApplicationEntityNode getNode() {
            return node;
        }

        public ApplicationEntityEventDesc getEvent() {
            return event;
        }

        public String getHandlerName() {
            return handlerName;
        }
    }

    private static class EventMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            if (!(source instanceof EventMenuItem)) {
                return;
            }

            EventMenuItem mi = (EventMenuItem) source;
            ApplicationEntityEventDesc event = mi.getEvent();
            Node.Property<String> prop = mi.getNode().getPropertyByName(event.getName());
            if (prop != null) {
                String handlerName = mi.getHandlerName();
                try {
                    ApplicationModuleEvents moduleEvents = mi.getNode().getLookup().lookup(ApplicationModuleEvents.class);
                    if (handlerName == null) {
                        handlerName = moduleEvents.findFreeHandlerName(mi.getNode().getEntity(), event);
                        prop.setValue(handlerName);
                    } else {
                        // only for text editor positioning
                        moduleEvents.incHandlerUse(event, handlerName);
                    }
                } catch (Exception ex) {
                }
            }
        }
    }
    private ActionListener menuItemListener;
}
