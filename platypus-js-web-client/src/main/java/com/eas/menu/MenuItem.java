package com.eas.menu;

import com.eas.ui.Widget;

/**
 *
 * @author mgainullin
 */
public abstract class MenuItem extends Widget {

    protected Menu submenu;

    public Menu getSubmenu() {
        return submenu;
    }

    public void setSubmenu(Menu aMenu) {
        if (submenu != null) {
            submenu.setParentItem(null);
        }
        submenu = aMenu;
        if (submenu != null) {
            submenu.setParentItem(this);
        }
    }

}
