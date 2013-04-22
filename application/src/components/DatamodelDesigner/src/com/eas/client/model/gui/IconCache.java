/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */

package com.eas.client.model.gui;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Marat
 */
public class IconCache {

    private static final Map<String, ImageIcon> icons = new HashMap<>();
    private static final String iconsPrefix = "com/eas/client/model/gui/resources/";
    
    public static ImageIcon getIcon(String iconName)
    {
        ImageIcon lic = icons.get(iconName);
        if(lic == null)
        {
            URL url = IconCache.class.getClassLoader().getResource(iconsPrefix+iconName);
            if(url != null)
            {
                lic = new ImageIcon(url);
                icons.put(iconName, lic);
            }
        }
        return lic;
    }

    public static Image getImage(String iconName)
    {
        Icon lic = getIcon(iconName);
        if(lic != null && lic instanceof ImageIcon)
            return ((ImageIcon)lic).getImage();
        return null;
    }
    
}
