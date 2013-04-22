/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.utils.scalableui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Mg
 */
public class EventsTargetPanel extends JPanel {

    protected float scale = 1f;
    protected float oldScale = 1f;
    protected RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    protected boolean painting = false;

    public boolean isPainting() {
        return painting;
    }

    public EventsTargetPanel() {
        super();
        setLayout(new ScalableBorderLayout());
        setDoubleBuffered(true);
    }

    @Override
    public Dimension getSize() {
        Dimension lpref = super.getSize();
        lpref.height = Math.round(lpref.height * scale);
        lpref.width = Math.round(lpref.width * scale);
        return lpref;
    }

    @Override
    public Dimension getSize(Dimension rv) {
        Dimension lrv = getSize();
        rv.height = lrv.height;
        rv.width = lrv.width;
        return rv;
    }

    @Override
    public void setSize(int width, int height) {
        height = Math.round(height / scale);
        width = Math.round(width / scale);
        super.setSize(width, height);
    }

    @Override
    public void setSize(Dimension d) {
        d.height = Math.round(d.height / scale);
        d.width = Math.round(d.width / scale);
        super.setPreferredSize(d);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension lpref = super.getPreferredSize();
        lpref.height = Math.round(lpref.height * scale);
        lpref.width = Math.round(lpref.width * scale);
        return lpref;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        preferredSize.height = Math.round(preferredSize.height / scale);
        preferredSize.width = Math.round(preferredSize.width / scale);
        super.setPreferredSize(preferredSize);
    }

    @Override
    public void setBounds(Rectangle r) {
        r.height = Math.round(r.height / scale);
        r.width = Math.round(r.width / scale);
        super.setBounds(r);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        height = Math.round(height / scale);
        width = Math.round(width / scale);
        super.setBounds(x, y, width, height);
    }

    @Override
    public Rectangle getBounds() {
        Rectangle lrt = super.getBounds();
        lrt.height = Math.round(lrt.height * scale);
        lrt.width = Math.round(lrt.width * scale);
        return lrt;
    }

    @Override
    public Rectangle getBounds(Rectangle rv) {
        Rectangle lrt = super.getBounds(rv);
        lrt.height = Math.round(lrt.height * scale);
        lrt.width = Math.round(lrt.width * scale);
        return lrt;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float aScale) {
        oldScale = scale;
        scale = aScale;
        repaint();
    }

    public float getOldScale() {
        return oldScale;
    }

    protected Point convertPoint(Point point, Component dest) {
        Point lpoint = new Point(Math.round(point.x / scale), Math.round(point.y / scale));
        return SwingUtilities.convertPoint(this, lpoint, dest);
    }

    @Override
    public void paint(Graphics g) {
        assert (getParent() instanceof JScalablePanel);
        painting = true;
        try {
            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.addRenderingHints(renderingHints);
                if (Math.abs(scale - 1.0f) >= 1e-6) {
                    g2d.scale(scale, scale);
                    super.paint(g);
                    g2d.scale(1 / scale, 1 / scale);
                } else {
                    super.paint(g);
                }
            } else {
                super.paint(g);
            }
        } finally {
            painting = false;
        }
    }

    @Override
    public boolean isShowing() {
        return super.isShowing() || (getParent() != null && getParent().isShowing());
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() || (getParent() != null && getParent().isVisible());
    }
}
