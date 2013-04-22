/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author vv
 */
public class PlatypusServerCustomizer extends JPanel {
    
    private final PlatypusServerInstance serverInstance;
    
    public PlatypusServerCustomizer(PlatypusServerInstance aServerInstance) {
        super();
        serverInstance = aServerInstance;
        add(new JLabel(serverInstance.getDisplayName()));
    }
            
}
