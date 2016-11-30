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

    public static ImageIcon load(String aResourceName, String aCalledFromFile, JSObject onSuccess, JSObject onFailure) throws Exception {
        return load(aResourceName, aCalledFromFile, Scripts.getSpace(), onSuccess != null ? (ImageIcon aLoaded) -> {
            onSuccess.call(null, new Object[]{aLoaded});
        } : null, onFailure != null ? (Exception ex) -> {
            onFailure.call(null, new Object[]{ex.getMessage()});
        } : null);
    }

    public static ImageIcon load(String aResourceName, String aCalledFromFile, Scripts.Space aSpace, Consumer<ImageIcon> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            ScriptedResource._load(aResourceName, aCalledFromFile, aSpace, (Object aLoaded) -> {
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
            Object loaded = ScriptedResource._load(aResourceName, aCalledFromFile, aSpace);
            if (loaded instanceof byte[]) {
                byte[] content = (byte[]) loaded;
                return new ImageIcon(content);
            } else {
                throw new IllegalArgumentException(aResourceName + " is not a binary resource");
            }
        }
    }
}
