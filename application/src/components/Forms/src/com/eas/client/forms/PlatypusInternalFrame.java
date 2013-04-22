/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import javax.swing.JInternalFrame;

/**
 *
 * @author mg
 */
public class PlatypusInternalFrame extends JInternalFrame{
    
    protected FormRunner owner;
    
    public PlatypusInternalFrame(FormRunner aOwner)
    {
        super();
        owner = aOwner;
    }

    public FormRunner getOwner() {
        return owner;
    }
}
