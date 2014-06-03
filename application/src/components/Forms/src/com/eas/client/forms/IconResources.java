/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.scripts.PlatypusScriptedResource;
import com.eas.resources.images.IconCache;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import javax.swing.ImageIcon;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
@ScriptObj(name = "Icon", jsDoc = "/**\n"
        + "* Image icon.\n"
        + "*/")
public class IconResources implements HasPublished {

    private static JSObject publisher;
    protected Object published;
    
    @ScriptFunction(params = {"path"}, jsDoc = "/**\n"
            + "* Loads an image resource.\n"
            + "* @param path a path or an URL to the icon resource\n"
            + "*/")
    public static ImageIcon load(String imageName) throws Exception {
        ImageIcon icon = IconCache.load(imageName);
        if (icon != null) {
            return icon;
        } else {
            byte[] resData = PlatypusScriptedResource.load(imageName);
            if (resData != null) {
                return new ImageIcon(resData);
            } else {
                return null;
            }
        }
    }
    
    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    } 
    
}
