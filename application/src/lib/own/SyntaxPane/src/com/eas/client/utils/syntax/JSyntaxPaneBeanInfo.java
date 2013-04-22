/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.utils.syntax;

import java.awt.Image;
import java.beans.SimpleBeanInfo;
import java.net.URL;
import javax.swing.ImageIcon;
/**
 *
 * @author Marat
 */
public class JSyntaxPaneBeanInfo extends SimpleBeanInfo {

    ImageIcon m32x32 = null;
    ImageIcon m24x24 = null;
    ImageIcon m16x16 = null;
    
    @Override
    public Image getIcon(int iconKind) {
        ClassLoader classloader = JSyntaxPane.class.getClassLoader();

        URL url = null;
        switch(iconKind)
        {
            case ICON_COLOR_16x16:
                url = classloader.getResource( "com/eas/client/utils/syntax/text_rich_marked16x16.png" );
                if(url != null)
                {
                    ImageIcon imic = new ImageIcon(url);
                    return imic.getImage();
                }
            break;
            case ICON_COLOR_32x32:
                url = classloader.getResource( "com/eas/client/utils/syntax/text_rich_marked32x32.png" );
                if(url != null)
                {
                    ImageIcon imic = new ImageIcon(url);
                    return imic.getImage();
                }
            break;
        }
        return null;
    }

}
