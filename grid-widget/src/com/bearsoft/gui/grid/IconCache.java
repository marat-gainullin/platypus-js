/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author mg
 */
public class IconCache {

    private static final Map<String, ImageIcon> icons = new HashMap<>();
    private static final String iconsPrefix = IconCache.class.getPackage().getName().replace('.', '/') + "/resources/";

    public static ImageIcon getIcon(String iconName) {
        if (iconName != null) {
            ImageIcon lic = icons.get(iconName);
            if (lic == null) {
                URL url = IconCache.class.getClassLoader().getResource(iconsPrefix + iconName);
                if (url != null) {
                    lic = new ImageIcon(url, iconName);
                    icons.put(iconName, lic);
                }
            }
            return lic;
        } else {
            return null;
        }
    }

    public static Image getImage(String iconName) {
        Icon lic = getIcon(iconName);
        if (lic != null && lic instanceof ImageIcon) {
            return ((ImageIcon) lic).getImage();
        }
        return null;
    }
}
