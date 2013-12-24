/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.scripts.ScriptRunner;
import com.eas.resources.images.IconCache;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import javax.swing.ImageIcon;

/**
 *
 * @author mg
 */
@ScriptObj(name = "Icon", jsDoc = "/**\n"
        + "* Image icon.\n"
        + "*/")
public class IconResources {

    @ScriptFunction(params = {"path"}, jsDoc = "/**\n"
            + "* Loads an image resource.\n"
            + "* @param path a path or an URL to the icon resource\n"
            + "*/")
    public static ImageIcon load(String imageName) throws Exception {
        ImageIcon icon = IconCache.load(imageName);
        if (icon != null) {
            return icon;
        } else {
            byte[] resData = ScriptRunner.PlatypusScriptedResource.load(imageName);
            if (resData != null) {
                return new ImageIcon(resData);
            } else {
                return null;
            }
        }
    }
}
