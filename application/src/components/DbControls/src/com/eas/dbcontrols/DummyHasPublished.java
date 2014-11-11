/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols;

import com.eas.script.HasPublished;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class DummyHasPublished implements HasPublished{

    protected JSObject published;
    
    public DummyHasPublished(JSObject aPublished){
        super();
        published = aPublished;
    }
    
    @Override
    public JSObject getPublished() {
        return published;
    }

    @Override
    public void setPublished(JSObject aPublished) {
        throw new UnsupportedOperationException("Not supported in dummy implementation.");
    }
    
}
