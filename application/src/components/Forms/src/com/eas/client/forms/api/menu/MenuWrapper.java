/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

/**
 *
 * @author mg
 */
public class MenuWrapper {

    public static Menu wrap(JMenu aDelegate) {
        return new Menu(aDelegate);
    }

    public static MenuBar wrap(JMenuBar aDelegate) {
        return new MenuBar(aDelegate);
    }

    public static MenuItem wrap(JMenuItem aDelegate) {
        return new MenuItem(aDelegate);
    }

    public static PopupMenu wrap(JPopupMenu aDelegate) {
        return new PopupMenu(aDelegate);
    }

    public static RadioMenuItem wrap(JRadioButtonMenuItem aDelegate) {
        return new RadioMenuItem(aDelegate);
    }

    public static CheckMenuItem wrap(JCheckBoxMenuItem aDelegate) {
        return new CheckMenuItem(aDelegate);
    }
    
    public static MenuSeparator wrap(JSeparator aDelegate) {
        return new MenuSeparator(aDelegate);
    }
}
