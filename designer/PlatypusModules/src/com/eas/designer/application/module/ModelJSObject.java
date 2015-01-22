/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import jdk.nashorn.api.scripting.AbstractJSObject;

/**
 *
 * @author mg
 */
public class ModelJSObject extends AbstractJSObject {

    protected ApplicationDbModel model;

    public ModelJSObject(ApplicationDbModel aModel) {
        super();
        model = aModel;
    }

    public ApplicationDbModel getModel() {
        return model;
    }

    @Override
    public boolean hasMember(String name) {
        ApplicationDbEntity entity = model.getEntityByName(name);
        return entity != null;
    }

    @Override
    public Object getMember(String name) {
        ApplicationDbEntity entity = model.getEntityByName(name);
        return entity != null ? entity.getPublished() : null;
    }

}
