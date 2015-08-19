/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.model.application.ApplicationDbEntity;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class EntityJSObject extends AbstractJSObject {

    protected ApplicationDbEntity entity;
    protected String name;
    protected JSObject cursor;
    protected JSObject[] elements = new JSObject[20];

    protected EntityJSObject() {
        super();
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new InstanceJSObject();
        }
        cursor = elements[0];
    }

    public EntityJSObject(ApplicationDbEntity aEntity) {
        this();
        entity = aEntity;
    }

    public EntityJSObject(String aName) {
        this();
        name = aName;
    }

    public ApplicationDbEntity getEntity() {
        return entity;
    }
    
    public String getName(){
        return entity != null ? entity.getName() : name;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean hasMember(String name) {
        if ("length".equals(name) || "cursor".equals(name)) {
            return true;
        } else {
            return super.hasMember(name);
        }
    }

    @Override
    public Object getMember(String name) {
        switch (name) {
            case "length":
                return elements.length;
            case "cursor":
                return cursor;
            default:
                return super.getMember(name);
        }
    }

    @Override
    public Object getSlot(int index) {
        return elements[index];
    }

}
