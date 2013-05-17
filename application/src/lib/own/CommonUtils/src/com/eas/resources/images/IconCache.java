/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.resources.images;

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

    public static ImageIcon load(String imageName) {
        return getIcon(imageName);
    }

    public static ImageIcon getIcon(String iconName) {
        if (iconName != null) {
            ImageIcon icon = icons.get(iconName);
            if (icon == null) {
                URL url = IconCache.class.getResource(iconName);
                if (url != null) {
                    icon = new ImageIcon(url, iconName);
                    icons.put(iconName, icon);
                }
            }
            return icon;
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

    public static URL getImageUrl(String iconName) {
        return IconCache.class.getResource(iconName);
    }
}
