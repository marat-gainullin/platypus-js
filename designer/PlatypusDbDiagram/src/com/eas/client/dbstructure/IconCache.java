/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure;

/**
 *
 * @author mg
 */
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Marat
 */
public class IconCache
{
    private static final Map<String, Icon> icons = new HashMap<>();
    private static final String iconsPrefix = "com/eas/client/dbstructure/resources/";

    public static Icon getIcon(String iconName)
    {
        Icon lic = icons.get(iconName);
        if (lic == null)
        {
            URL url = IconCache.class.getClassLoader().getResource(iconsPrefix + iconName);
            if (url != null)
            {
                lic = new ImageIcon(url);
                icons.put(iconName, lic);
            }
        }
        return lic;
    }
}
