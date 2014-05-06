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
    
    protected Form owner;
    
    public PlatypusInternalFrame(Form aOwner)
    {
        super();
        owner = aOwner;
    }

    public Form getOwner() {
        return owner;
    }
}
