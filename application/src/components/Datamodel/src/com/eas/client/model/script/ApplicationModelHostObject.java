/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.queries.Query;
import com.eas.script.NativeJavaHostObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class ApplicationModelHostObject<M extends ApplicationModel<E, ?, ?, Q>, Q extends Query<?>, E extends ApplicationEntity<M, Q, E>> extends NativeJavaHostObject {

    protected M model;

    public ApplicationModelHostObject(Scriptable scope, M aModel) throws Exception {
        super(scope, aModel, null);
        model = aModel;
        model.setPublished(this);
    }

}
