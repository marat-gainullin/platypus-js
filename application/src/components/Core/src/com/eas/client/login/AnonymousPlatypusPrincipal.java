/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.script.NoPublisherException;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author vv
 */
public class AnonymousPlatypusPrincipal extends PlatypusPrincipal {

    public AnonymousPlatypusPrincipal(String aName) {
        super(aName);
    }

    @Override
    public boolean hasRole(String string, Consumer<Boolean> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            onSuccess.accept(Boolean.FALSE);
        }
        return false;
    }

    @Override
    public String getStartAppElement(Consumer<String> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            onSuccess.accept(null);
        }
        return null;
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

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
