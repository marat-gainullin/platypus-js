/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import javax.swing.JComponent;

/**
 *
 * @author AB
 */
public class Ordering {

    public static void toFront(JComponent aParent, JComponent aComp) {
        aParent.setComponentZOrder(aComp, 0);
        aParent.revalidate();
        aParent.repaint();
    }

    public static void toBack(JComponent aParent, JComponent aComp) {
        int zOrder = aParent.getComponentCount();
        aParent.setComponentZOrder(aComp, --zOrder);
        aParent.revalidate();
        aParent.repaint();
    }

    public static void toFront(JComponent aParent, JComponent aComp, int aCount) {
        int zOrder = aParent.getComponentZOrder(aComp);
        if ((zOrder - aCount) >= 0) {
            aParent.setComponentZOrder(aComp, zOrder - aCount);
            aParent.revalidate();
            aParent.repaint();
        }
    }

    public static void toBack(JComponent aParent, JComponent aComp, int aCount) {
        int zOrder = aParent.getComponentZOrder(aComp);
        if ((zOrder + aCount) < aParent.getComponentCount()) {
            aParent.setComponentZOrder(aComp, zOrder + aCount);
            aParent.revalidate();
            aParent.repaint();
        }
    }
}
