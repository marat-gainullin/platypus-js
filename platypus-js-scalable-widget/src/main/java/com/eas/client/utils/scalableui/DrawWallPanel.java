/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.utils.scalableui;

import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Mg
 */
public class DrawWallPanel extends JPanel {

    EventsTargetPanel eventsTarget = null;

    public DrawWallPanel(EventsTargetPanel eventTarget) {
        super();
        eventsTarget = eventTarget;
    }

    @Override
    public void paint(Graphics g) {
    }
}
