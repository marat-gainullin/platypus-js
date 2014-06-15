/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols;

import com.eas.script.HasPublished;

/**
 *
 * @author mg
 */
public class DummyHasPublished implements HasPublished{

    protected Object published;
    
    public DummyHasPublished(Object aPublished){
        super();
        published = aPublished;
    }
    
    @Override
    public Object getPublished() {
        return published;
    }

    @Override
    public void setPublished(Object aPublished) {
        throw new UnsupportedOperationException("Not supported in dummy implementation.");
    }
    
}
