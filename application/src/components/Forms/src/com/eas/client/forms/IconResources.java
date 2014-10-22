/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.scripts.ScriptedResource;
import com.eas.resources.images.IconCache;
import com.eas.script.ScriptUtils;
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
            ScriptUtils.submitTask(() -> {
                try {
                    ImageIcon loaded = loadSync(aResourceName);
                    ScriptUtils.acceptTaskResult(() -> {
                        onSuccess.call(null, new Object[]{ScriptUtils.toJs(loaded)});
                    });
                } catch (Exception ex) {
                    Logger.getLogger(IconResources.class.getName()).log(Level.SEVERE, null, ex);
                    if (onFailure != null) {
                        ScriptUtils.acceptTaskResult(() -> {
                            onFailure.call(null, new Object[]{ScriptUtils.toJs(ex.getMessage())});
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
        ImageIcon icon = IconCache.load(imageName);
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
