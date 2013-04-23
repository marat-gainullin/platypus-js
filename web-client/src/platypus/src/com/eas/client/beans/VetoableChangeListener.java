/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.beans;


/**
 *
 * @author mg
 */
public interface VetoableChangeListener extends java.util.EventListener {
    /**
     * This method gets called when a constrained property is changed.
     *
     * @param     evt a <code>PropertyChangeEvent</code> object describing the
     *                event source and the property that has changed.
     * @exception PropertyVetoException if the recipient wishes the property
     *              change to be rolled back.
     */
    void vetoableChange(PropertyChangeEvent evt)
                                throws Exception;
}
