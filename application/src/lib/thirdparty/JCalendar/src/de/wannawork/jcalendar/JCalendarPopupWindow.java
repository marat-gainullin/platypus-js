/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.wannawork.jcalendar;

import java.awt.Window;
import javax.swing.JWindow;

/**
 *
 * @author mg
 */
public class JCalendarPopupWindow extends JWindow{

    public JCalendarPopupWindow()
    {
        this(null);
    }

    public JCalendarPopupWindow(Window owner)
    {
        super(owner);
    }
}
