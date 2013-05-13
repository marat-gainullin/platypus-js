/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view;

import com.bearsoft.routing.Connector;
import com.eas.client.model.gui.view.model.Segment;

/**
 *
 * @author mg
 */
public class RelationDesignInfo {

    protected Segment firstSlot;
    protected Segment lastSlot;
    protected Connector connector;

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector aValue) {
        connector = aValue;
    }

    public Segment getFirstSlot() {
        return firstSlot;
    }

    public Segment getLastSlot() {
        return lastSlot;
    }

    public void setFirstSlot(Segment aValue) {
        firstSlot = aValue;
    }

    public void setLastSlot(Segment aValue) {
        lastSlot = aValue;
    }
    
}
