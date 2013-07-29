/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.view;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;

/**
 *
 * @author mg
 */
public class IconsListCellRenderer extends DefaultListCellRenderer {

    protected List<Icon> extraIcons = new ArrayList<>();
        
    public void addIcon(Icon anIcon) {
        if (getIcon() == null) {
            setIcon(anIcon);
        } else {
            extraIcons.add(anIcon);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Icon icon : extraIcons) {
            icon.paintIcon(this, g, 2, 0);
        }
    }
}
