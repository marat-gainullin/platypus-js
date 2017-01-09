/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

/**
 *
 * @author mg
 */
public class JDropDownButton extends JButton {

    public int ARROW_ISET_SIZE = 11;
    protected JPopupMenu dropDownMenu;
    protected int[] xCoords = new int[3];
    protected int[] yCoords = new int[3];

    /**
     * Creates a button with no set text or icon.
     */
    public JDropDownButton() {
        this(null, null);
    }

    /**
     * Creates a button with an icon.
     *
     * @param icon the Icon image to display on the button
     */
    public JDropDownButton(Icon icon) {
        this(null, icon);
    }

    /**
     * Creates a button with text.
     *
     * @param text the text of the button
     */
    public JDropDownButton(String text) {
        this(text, null);
    }

    /**
     * Creates a button where properties are taken from the
     * <code>Action</code> supplied.
     *
     * @param a the <code>Action</code> used to specify the new button
     *
     * @since 1.3
     */
    public JDropDownButton(Action a) {
        this();
        setAction(a);
    }

    /**
     * Creates a button with initial text and an icon.
     *
     * @param text the text of the button
     * @param icon the Icon image to display on the button
     */
    public JDropDownButton(String text, Icon icon) {
        super(text, icon);
    }

    public JPopupMenu getDropDownMenu() {
        return dropDownMenu;
    }

    public void setDropDownMenu(JPopupMenu aDropDownMenu) {
        dropDownMenu = aDropDownMenu;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        if (size != null) {
            return new Dimension(size.width + ARROW_ISET_SIZE, size.height);
        }
        return null;
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension sMax = super.getMaximumSize();
        Dimension pref = getPreferredSize();
        return new Dimension(Math.max(sMax.width, pref.width), sMax.height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        xCoords[0] = size.width - 8;
        xCoords[1] = size.width - 3;
        xCoords[2] = size.width - 6;

        yCoords[0] = size.height / 2 - 2;
        yCoords[1] = yCoords[0];
        yCoords[2] = size.height / 2 + 1;
        Color bk = getBackground();
        if (!isEnabled()) {
            g.setColor(bk.darker());
        } else {
            g.setColor(bk.darker().darker().darker().darker());
        }
        g.fillPolygon(xCoords, yCoords, 3);

        if (isEnabled() && getModel().isRollover()) {
            g.setColor(bk.darker());
            g.drawLine(size.width - ARROW_ISET_SIZE, 4, size.width - ARROW_ISET_SIZE, size.height - 5);
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (isEnabled() && dropDownMenu != null && e != null
                && e.getID() == MouseEvent.MOUSE_RELEASED
                && e.getButton() == MouseEvent.BUTTON1) {
            Dimension size = super.getSize();
            if (size != null) {
                int x = e.getX();
                if (x > size.width - ARROW_ISET_SIZE) {
                    String lText = getText();
                    String lTooltip = getToolTipText();
                    Icon lIcon = getIcon();
                    Action laction = getAction();
                    ActionListener[] al = getActionListeners();
                    for (ActionListener l : al) {
                        if (l != laction) {
                            removeActionListener(l);
                        }
                    }
                    try {
                        setAction(null);
                        dropDownMenu.show(this, x, e.getY());
                        super.processMouseEvent(e);
                    } finally {
                        for (ActionListener l : al) {
                            if (l != laction) {
                                addActionListener(l);
                            }
                        }
                        setAction(laction);
                        if (laction == null) {
                            setText(lText);
                            setToolTipText(lTooltip);
                            setIcon(lIcon);
                        }
                    }
                } else {
                    super.processMouseEvent(e);
                }
            } else {
                super.processMouseEvent(e);
            }
        } else {
            super.processMouseEvent(e);
        }
    }
}
