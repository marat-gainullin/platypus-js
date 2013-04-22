/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.view;

import com.eas.client.model.gui.view.visibilitygraph.PathedVertex;
import com.eas.client.model.gui.view.visibilitygraph.Segment;
import java.awt.Point;
import java.util.List;

/**
 *
 * @author Gala
 */
public class RelationDesignInfo {

    protected PathedVertex firstVertex;
    protected PathedVertex lastVertex;
    protected Segment firstSlot;
    protected Segment lastSlot;
    protected int[] connectorX;
    protected int[] connectorY;
    protected List<Point> connector;
    protected List<EstimatedArc> connectorEstimatedArcs;

    public int[] getConnectorX() {
        return connectorX;
    }

    public int[] getConnectorY() {
        return connectorY;
    }

    public List<Point> getConnector() {
        return connector;
    }

    public void setConnector(List<Point> aValue) {
        connector = aValue;
    }

    public List<EstimatedArc> getConnectorEstimatedArcs() {
        return connectorEstimatedArcs;
    }

    public void setConnectorEstimatedArcs(List<EstimatedArc> aValue) {
        connectorEstimatedArcs = aValue;
    }

    public Segment getFirstSlot() {
        return firstSlot;
    }

    public Segment getLastSlot() {
        return lastSlot;
    }

    public PathedVertex getFirstVertex() {
        return firstVertex;
    }

    public PathedVertex getLastVertex() {
        return lastVertex;
    }

    public void setConnectorX(int[] aValue) {
        connectorX = aValue;
    }

    public void setConnectorY(int[] aValue) {
        connectorY = aValue;
    }

    public void setFirstSlot(Segment aValue) {
        firstSlot = aValue;
    }

    public void setLastSlot(Segment aValue) {
        lastSlot = aValue;
    }

    public void setFirstVertex(PathedVertex aValue) {
        firstVertex = aValue;
    }

    public void setLastVertex(PathedVertex aValue) {
        lastVertex = aValue;
    }
    
}
