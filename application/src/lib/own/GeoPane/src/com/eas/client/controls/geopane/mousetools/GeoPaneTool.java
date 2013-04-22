/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.controls.geopane.mousetools;

import com.eas.client.controls.geopane.JGeoPane;
import java.awt.event.MouseAdapter;

/**
 *
 * @author mg
 */
public abstract class GeoPaneTool extends MouseAdapter{

    protected JGeoPane pane;

    public GeoPaneTool(JGeoPane aPane)
    {
        super();
        pane = aPane;
    }

    public abstract boolean isCapable(MouseToolCapability aCapability);

}
