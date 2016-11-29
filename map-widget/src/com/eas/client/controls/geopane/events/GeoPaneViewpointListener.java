/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.events;

/**
 * Inteface intended to listen events of view point changes, i.e. translates or scales.
 * @author mg
 */
public interface GeoPaneViewpointListener {

    public void scaled(ViewpointScaledEvent aEvent);

    public void translated(ViewpointTranslatedEvent aEvent);
}
