/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rendering;

import java.awt.Rectangle;
import javax.swing.JLabel;

/**
 *
 * @author Gala
 */
public class NonRepaintableLabel extends JLabel {

    protected boolean repaintable;

    public NonRepaintableLabel(boolean aRepaintable) {
        super();
        repaintable = aRepaintable;
    }

    @Override
    public void repaint() {
        if (repaintable) {
            super.repaint();
        }
    }

    @Override
    public void repaint(Rectangle r) {
        if (repaintable) {
            super.repaint(r);
        }
    }

    @Override
    public void repaint(long tm) {
        if (repaintable) {
            super.repaint(tm);
        }
    }

    @Override
    public void repaint(int x, int y, int width, int height) {
        if (repaintable) {
            super.repaint(x, y, width, height);
        }
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        if (repaintable) {
            super.repaint(tm, x, y, width, height);
        }
    }
}
