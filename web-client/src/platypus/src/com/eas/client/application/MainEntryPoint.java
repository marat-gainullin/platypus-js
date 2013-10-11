/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.google.gwt.core.client.EntryPoint;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point.
 * @author mg
 */
public class MainEntryPoint implements EntryPoint {

    /**
     * Creates a new instance of MainEntryPoint
     */
    public MainEntryPoint() {
    }
    
    /**
     * The entry point method, called automatically by loading a module that
     * declares an implementing class as an entry-point
     */
    @Override
    public void onModuleLoad() {        
        try {
            Application.run();
        } catch (Exception ex) {
            Logger.getLogger(MainEntryPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
