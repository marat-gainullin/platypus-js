/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.actions;

import com.eas.client.controls.geopane.JGeoPane;
import javax.swing.AbstractAction;

/**
 *
 * @author mg
 */
public abstract class GeoPaneAction extends AbstractAction {

    protected JGeoPane pane;

    public GeoPaneAction(JGeoPane aPane) {
        super();
        pane = aPane;
    }
}
