/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import javax.swing.JComponent;

/**
 *
 * @author AB
 */
public class Ordering {
    
    public static void toFront(JComponent aParent, Component<?> aComp) {
        aParent.setComponentZOrder(aComp.delegate, 0);
        aParent.revalidate();
        aParent.repaint();
    }

    public static void toBack(JComponent aParent, Component<?> aComp) {
        int zOrder = aParent.getComponentCount();
        aParent.setComponentZOrder(aComp.delegate, --zOrder);
        aParent.revalidate();
        aParent.repaint();
    }
    public static void toFront(JComponent aParent, Component<?> aComp, int aCount) {
        int zOrder = aParent.getComponentZOrder(aComp.delegate);
        if ((zOrder - aCount) >= 0) {
            aParent.setComponentZOrder(aComp.delegate, zOrder - aCount);
            aParent.revalidate();
            aParent.repaint();
        }
    }

    public static void toBack(JComponent aParent, Component<?> aComp, int aCount) {
        int zOrder = aParent.getComponentZOrder(aComp.delegate);
        if ((zOrder + aCount) < aParent.getComponentCount()) {
            aParent.setComponentZOrder(aComp.delegate, zOrder + aCount);
            aParent.revalidate();
            aParent.repaint();
        }
    }
}
