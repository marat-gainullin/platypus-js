/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.scripts.ScriptedResource;
import com.eas.script.Scripts;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class IconResources {

    public static ImageIcon load(String aResourceName, JSObject onSuccess, JSObject onFailure) throws Exception {
        Scripts.Space space = Scripts.getSpace();
        return load(aResourceName, space, onSuccess != null ? (ImageIcon aLoaded) -> {
            onSuccess.call(null, new Object[]{aLoaded});
        } : null, onSuccess != null ? (Exception ex) -> {
            onFailure.call(null, new Object[]{space.toJs(ex.getMessage())});
        } : null);
    }

    public static ImageIcon load(String aResourceName, Scripts.Space aSpace, Consumer<ImageIcon> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            ScriptedResource.load(aResourceName, aSpace, (Object aLoaded) -> {
                if (aLoaded instanceof byte[]) {
                    byte[] content = (byte[]) aLoaded;
                    onSuccess.accept(new ImageIcon(content));
                } else {
                    if (onFailure != null) {
                        onFailure.accept(new IllegalArgumentException(aResourceName + " is not a binary resource"));
                    }
                }
            }, (Exception ex) -> {
                if (onFailure != null) {
                    onFailure.accept(ex);
                }
            });
            return null;
        } else {
            Object loaded = ScriptedResource.load(aResourceName);
            if (loaded instanceof byte[]) {
                byte[] content = (byte[]) loaded;
                return new ImageIcon(content);
            } else {
                throw new IllegalArgumentException(aResourceName + " is not a binary resource");
            }
        }
    }
}
