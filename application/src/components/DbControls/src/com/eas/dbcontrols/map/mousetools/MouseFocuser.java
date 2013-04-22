/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.map.mousetools;

import com.eas.client.controls.geopane.mousetools.MouseToolCapability;
import com.eas.dbcontrols.map.DbMap;
import java.awt.event.MouseEvent;

/**
 *
 * @author mg
 */
public class MouseFocuser extends MapTool{

    public MouseFocuser(DbMap aMap)
    {
        super(aMap);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        map.getPane().requestFocus();
    }

    @Override
    public boolean isCapable(MouseToolCapability aCapability) {
        return aCapability == MouseToolCapability.BUTTONS;
    }
}
