package com.eas.client.utils.scalableui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author Marat
 */
public class JScalableScrollPane extends JScrollPane implements ScaleListener{
    
    protected JScalablePanel scalablePanel = null;
    
    public JScalableScrollPane() {
        super();
        scalablePanel = new JScalablePanel();
        getViewport().setView(scalablePanel);
        scalablePanel.addScaleListener(this);
        viewport.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        setOpaque(false);
        setAutoscrolls(true);
    }

    public JScalableScrollPane(int vsbPolicy, int hsbPolicy) {
        this();
        setVerticalScrollBarPolicy(vsbPolicy);
        setHorizontalScrollBarPolicy(hsbPolicy);
    }

    public JScalablePanel getScalablePanel() {
        return scalablePanel;
    }

    public void addScaleListener(ScaleListener scaleListener)
    {
        if(scalablePanel != null)
            scalablePanel.addScaleListener(scaleListener);
    }
    
    public void removeScaleListener(ScaleListener scaleListener)
    {
        if(scalablePanel != null)
            scalablePanel.removeScaleListener(scaleListener);
    }
    
    public void checkComponents() {
        scalablePanel.checkComponents();
    }

    @Override
    public void setViewportView(Component view) {
        setViewContent(view);
    }
    
    protected void setViewContent(Component aComp) {
        scalablePanel.setContent(aComp);
        if(aComp instanceof JComponent)
            scalablePanel.setAutoscrolls(((JComponent)aComp).getAutoscrolls());
    }

    @Override
    public void scaleChanged(float oldScale, float newScale) {
        doLayout();
        revalidate();
    }
    
    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        super.paintBorder(g);
        Color lOldColor = g.getColor();
        Rectangle lb = getBounds();
        if(verticalScrollBar != null && verticalScrollBar.isVisible())
        {
            g.setColor(getBackground());
            Rectangle lrt = verticalScrollBar.getBounds();
            lrt.y = 0;
            lrt.height = lb.height;
            g.fillRect(lrt.x, lrt.y+1, lrt.width, lrt.height-1);
            g.setColor(lOldColor);
            paintComp(g, verticalScrollBar);
        }
        if(horizontalScrollBar != null && horizontalScrollBar.isVisible())
        {
            g.setColor(getBackground());
            Rectangle lrt = horizontalScrollBar.getBounds();
            lrt.x = 0;
            lrt.width = lb.width;
            g.fillRect(lrt.x+1, lrt.y, lrt.width-1, lrt.height);
            g.setColor(lOldColor);
            paintComp(g, horizontalScrollBar);
        }
    }

    void paintComp(Graphics g, Component aComp)
    {
        if(aComp != null && aComp.isVisible())
        {
            Point lLoc = aComp.getLocation();
            g.translate(lLoc.x, lLoc.y);
            aComp.paint(g);
            g.translate(-lLoc.x, -lLoc.y);
        }
    }
    
}
