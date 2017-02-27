package com.eas.client.utils.scalableui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.SwingUtilities;

/**
 *
 * @author Marat
 */
public class ScalablePopup extends Popup {

    public class ScalablePopupPanel extends JPanel {

        Component contentsOwner = null;
        public boolean allowHide = true;

        public ScalablePopupPanel(LayoutManager layout) {
            super(layout);
        }

        public JComponent getContents() {
            if (getComponentCount() > 0 && getComponent(0) != null &&
                    getComponent(0) instanceof JComponent) {
                return (JComponent) getComponent(0);
            }
            return null;
        }

        Component getContentsOwner() {
            return contentsOwner;
        }

        public void setContentsOwner(Component aContentsOwner) {
            contentsOwner = aContentsOwner;
        }
    }
    ScalablePopupPanel ppPanel = new ScalablePopupPanel(new BorderLayout());

    protected ScalablePopup() {
        super();
    }

    @Override
    public void show() {
        ppPanel.setVisible(true);
    }

    @Override
    public void hide() {
        if (ppPanel.getContents() instanceof ScalableComboPopup) {
            ppPanel.setVisible(false);
            Container cont = ppPanel.getParent();
            if (cont != null) {
                cont.remove(ppPanel);
            }
            ppPanel.setContentsOwner(null);
        } else if (ppPanel.getContents() instanceof JPopupMenu) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ppPanel.setVisible(false);
                    Container cont = ppPanel.getParent();
                    if (cont != null) {
                        cont.remove(ppPanel);
                    }
                    ppPanel.setContentsOwner(null);
                }
            });
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (ppPanel != null) {
                    ppPanel.removeAll(); // to avoid possible memory leaks
                }
            }
        });

    }

    void init4Combo(Component owner, ScalableComboPopup contents, int x, int y) {
        JScalablePanel scp = ZoomRepaintManager.getPanelContainer(owner);
        if (scp != null) {
            ppPanel.removeAll();
            ppPanel.add(contents, BorderLayout.CENTER);
            ppPanel.setPreferredSize(contents.getPreferredSize());
            ppPanel.setMinimumSize(contents.getPreferredSize());
            ppPanel.setSize(contents.getPreferredSize());
            ppPanel.setContentsOwner(owner);
            Point lpt = new Point(0, owner.getHeight());
            lpt = SwingUtilities.convertPoint(owner, lpt, scp.getContentPanel());
            ppPanel.setLocation(lpt);
            scp.getContentPanel().add(ppPanel, 0);
            scp.getContentPanel().validate();
            if (owner instanceof JComboBox && contents instanceof JPopupMenu) {
                final JComboBox lcombo = (JComboBox) owner;
                final JPopupMenu lppm = (JPopupMenu) contents;
                ItemListener itListener = new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                lppm.setVisible(false);
                            }
                        });
                        lcombo.removeItemListener(this);
                    }
                };
                lcombo.addItemListener(itListener);
            }
        }
    }

    void init4Menu(Component owner, JPopupMenu contents, int x, int y) {
        JScalablePanel scp = ZoomRepaintManager.getPanelContainer(owner);
        if (scp != null) {
            ppPanel.removeAll();
            ppPanel.add(contents, BorderLayout.CENTER);
            ppPanel.setPreferredSize(contents.getPreferredSize());
            ppPanel.setMinimumSize(contents.getPreferredSize());
            ppPanel.setSize(contents.getPreferredSize());
            ppPanel.setContentsOwner(owner);

            Point lpt = new Point(0, 0);
            if (owner != null && owner instanceof JComponent) {
                JComponent ljowner = (JComponent) owner;
                Object propO = ljowner.getClientProperty(JScalablePanel.MOUSE_PRESSED_PT_CLIENT_PROPERTY);
                if (propO != null && propO instanceof Point) {
                    lpt = (Point) propO;
                }
            }
            lpt = SwingUtilities.convertPoint(owner, lpt, scp.getContentPanel());
            ppPanel.setLocation(lpt);

            scp.getContentPanel().add(ppPanel, 0);
            scp.getContentPanel().validate();
        }
    }
}
