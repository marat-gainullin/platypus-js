/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

/**
 * Wraps low level event in script form API event.
 * Important: keep conversion method name as <code>wrap</code>
 * @author mg
 */
public class EventsWrapper {

    public static MouseEvent wrap(java.awt.event.MouseEvent aEvent) {
        return new MouseEvent(aEvent);
    }

    public static KeyEvent wrap(java.awt.event.KeyEvent aEvent) {
        return new KeyEvent(aEvent);
    }

    public static ValueChangeEvent wrap(java.beans.PropertyChangeEvent aEvent) {
        return new ValueChangeEvent(aEvent);
    }

    public static ActionEvent wrap(java.awt.event.ActionEvent aEvent) {
        return new ActionEvent(aEvent);
    }

    public static FocusEvent wrap(java.awt.event.FocusEvent aEvent) {
        return new FocusEvent(aEvent);
    }

    public static ComponentEvent wrap(java.awt.event.ComponentEvent aEvent) {
        return new ComponentEvent(aEvent);
    }

    public static ContainerEvent wrap(java.awt.event.ContainerEvent aEvent) {
        return new ContainerEvent(aEvent);
    }
/*
    public static ItemEvent wrap(javax.swing.event.ChangeEvent aEvent) {
        return new ItemEvent(aEvent);
    }
*/
    public static WindowEvent wrap(java.awt.event.WindowEvent aEvent) {
        return new WindowEvent(aEvent);
    }
}
