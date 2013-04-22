/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.combo.rt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 *
 * @author mg
 */
public class SemiBorderTextField extends JTextField {

    protected boolean borderless;
    protected Color focusColor;

    public SemiBorderTextField(boolean aIsBorderless) {
        super();
        borderless = aIsBorderless;
        if (borderless) {
            setBorder(null);
        }
        getFocusColor();
    }

    public void getFocusColor() {
        Color uiColor = UIManager.getColor("RadioButton.focus");
        if (uiColor != null) {
            focusColor = uiColor;
        }else
            focusColor = new Color(245, 245, 245);
    }

    @Override
    public void setBorder(Border border) {
        if (borderless) {
            super.setBorder(null);
        } else {
            super.setBorder(border);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isFocusOwner()) {
            Rectangle bounds = getBounds();
            bounds.x = 0;
            bounds.y = 0;
            paintFocus(g, bounds);
        }
    }

    protected void paintFocus(Graphics g, Rectangle textRect) {
        g.setColor(focusColor);
        BasicGraphicsUtils.drawDashedRect(g, textRect.x, textRect.y, textRect.width, textRect.height);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (borderless) {
            setBorder(null);
        }
        getFocusColor();
    }

    public boolean isBorderless() {
        return borderless;
    }

    public void setBorderless(boolean borderless) {
        this.borderless = borderless;
    }
}
