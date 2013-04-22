/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.image.rt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Марат
 */
public class ImagePanel extends JPanel {

    protected int horizontalAlign = SwingConstants.LEFT;
    protected int verticalAlign = SwingConstants.CENTER;
    protected Image image = null;

    public ImagePanel()
    {
        super();
    }

    public int getHorizontalAlign() {
        return horizontalAlign;
    }

    public void setHorizontalAlign(int aValue) {
        this.horizontalAlign = aValue;
        repaint();
    }

    public int getVerticalAlign() {
        return verticalAlign;
    }

    public void setVerticalAlign(int aValue) {
        this.verticalAlign = aValue;
        repaint();
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image aValue) {
        image = aValue;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Rectangle bounds = getBounds();
            Rectangle imageRect = convertView2Model(bounds);
            Rectangle viewRect = convertModel2View(imageRect);

            g.drawImage(image, viewRect.x, viewRect.y, viewRect.x + viewRect.width, viewRect.y + viewRect.height,
                    imageRect.x, imageRect.y, imageRect.x + imageRect.width, imageRect.y + imageRect.height, null);
        }
    }

    protected float calcScale() {
        Dimension selfSize = getSize();
        int imWidth = image.getWidth(null);
        int imHeight = image.getHeight(null);
        float wK = (float)selfSize.width/imWidth;
        float hK = (float)selfSize.height/imHeight;
        return Math.min(wK, hK);
    }

    protected Rectangle convertView2Model(Rectangle aRect) {
        float k = calcScale();
        Rectangle modelRect = new Rectangle(aRect);
        modelRect.x /= k;
        modelRect.y /= k;
        modelRect.width /= k;
        modelRect.height /= k;
        if (modelRect.width > image.getWidth(null)) {
            modelRect.width = image.getWidth(null);
        }
        if (modelRect.height > image.getHeight(null)) {
            modelRect.height = image.getHeight(null);
        }
        return modelRect;
    }

    protected Rectangle convertModel2View(Rectangle aRect) {
        float k = calcScale();
        Rectangle viewRect = new Rectangle(aRect);
        viewRect.x *= k;
        viewRect.y *= k;
        viewRect.width *= k;
        viewRect.height *= k;
        Dimension selfSize = getSize();
        if (viewRect.width < selfSize.width) {
            switch (horizontalAlign) {
                case SwingConstants.LEFT:
                    break;
                case SwingConstants.CENTER:
                    viewRect.x += (selfSize.width - viewRect.width) / 2;
                    break;
                case SwingConstants.RIGHT:
                    viewRect.x += selfSize.width - viewRect.width;
                    break;
            }
        }
        if (viewRect.height < selfSize.height) {
            switch (verticalAlign) {
                case SwingConstants.TOP:
                    break;
                case SwingConstants.CENTER:
                    viewRect.y += (selfSize.height - viewRect.height) / 2;
                    break;
                case SwingConstants.BOTTOM:
                    viewRect.y += selfSize.height - viewRect.height;
                    break;
            }
        }
        return viewRect;
    }
}
