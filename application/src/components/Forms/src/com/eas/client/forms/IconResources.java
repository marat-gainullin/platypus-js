/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.scripts.ScriptedResource;
import com.eas.script.Scripts;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class IconResources {

    public static ImageIcon load(String aResourceName, JSObject onSuccess, JSObject onFailure) throws Exception {
        if (onSuccess != null) {
            Scripts.Space space = Scripts.getSpace();
            Scripts.startBIO(() -> {
                try {
                    ImageIcon loaded = loadSync(aResourceName);
                    space.process(() -> {
                        onSuccess.call(null, new Object[]{space.toJs(loaded)});
                    });
                } catch (Exception ex) {
                    Logger.getLogger(IconResources.class.getName()).log(Level.SEVERE, null, ex);
                    if (onFailure != null) {
                        space.process(() -> {
                            onFailure.call(null, new Object[]{space.toJs(ex.getMessage())});
                        });
                    }
                }
            });
            return null;
        } else {
            return loadSync(aResourceName);
        }
    }
    
    protected static ImageIcon loadSync(String imageName) throws Exception {
        ImageIcon icon = IconCache.getIcon(imageName);
        if (icon != null) {
            return icon;
        } else {
            byte[] resData = (byte[])ScriptedResource.load(imageName);
            if (resData != null) {
                return new ImageIcon(resData);
            } else {
                return null;
            }
        }
    }
}
