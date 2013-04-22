/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.resources.images;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author mg
 */
public class IconCache {

    private static final Pattern pattern = Pattern.compile("https?://.*");
    private static final Map<String, ImageIcon> icons = new HashMap<>();

    public static ImageIcon load(String imageName) {
        return getIcon(imageName);
    }

    public static ImageIcon getIcon(String iconName) {
        if (iconName != null) {
            ImageIcon lic = icons.get(iconName);
            if (lic == null) {
                try {
                    Matcher htppMatcher = pattern.matcher(iconName);
                    URL url = htppMatcher.matches() ? new URL(iconName) :  IconCache.class.getResource(iconName);
                    if (url != null) {
                        lic = new ImageIcon(url, iconName);
                        icons.put(iconName, lic);
                    }
                } catch (MalformedURLException ex) {
                    Logger.getLogger(IconCache.class.getName()).log(Level.SEVERE, null, ex);
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

    public static URL getImageUrl(String iconName) {
        return IconCache.class.getResource(iconName);
    }
}
