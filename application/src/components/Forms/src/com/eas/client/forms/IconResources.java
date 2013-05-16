/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.scripts.ScriptRunner;
import com.eas.resources.images.IconCache;
import javax.swing.ImageIcon;

/**
 *
 * @author mg
 */
public class IconResources {

    public static ImageIcon load(String imageName) throws Exception {
        byte[] resData = ScriptRunner.PlatypusScriptedResource.load(imageName);
        if (resData != null) {
            return new ImageIcon(resData);
        } else {
            return IconCache.load(imageName);
        }
    }
}
