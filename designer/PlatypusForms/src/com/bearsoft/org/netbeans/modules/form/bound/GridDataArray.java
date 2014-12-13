/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class GridDataArray extends AbstractJSObject {

    protected JSObject cursor;
    protected JSObject[] elements = new JSObject[20];

    public GridDataArray() {
        super();
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new FakeJSObject();
        }
        cursor = elements[0];
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
