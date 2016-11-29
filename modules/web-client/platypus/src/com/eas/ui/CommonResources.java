/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 *
 * @author mg
 */
public interface CommonResources extends ClientBundle {

    public static CommonResources INSTANCE = GWT.create(CommonResources.class);

    public interface CommonStyles extends CssResource {

        public String unselectable();
        
        public String borderSized();
        
        public String withoutDropdown();
    }

    public CommonStyles commons();

}
