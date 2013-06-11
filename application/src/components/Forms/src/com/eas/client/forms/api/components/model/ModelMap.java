/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.client.controls.geopane.JGeoPane;
import com.eas.client.forms.api.Component;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.mousetools.MouseTools;
import com.eas.script.ScriptFunction;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.List;
import java.util.Map;
import org.geotools.map.MapLayer;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class ModelMap extends Component<DbMap> {

    protected ModelMap(DbMap aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelMap() {
        super();
        setDelegate(new DbMap());
    }

    @ScriptFunction(jsDoc = "Map service URL.")
    public String getBackingUrl() {
        return delegate.getBackingUrl();
    }

    @ScriptFunction
    public void setBackingUrl(String aUrl) {
        delegate.setBackingUrl(aUrl);
    }

    @ScriptFunction(jsDoc = "Goes to specified geo position.")
    public void goToGeoPosition(Point aPosition) throws Exception {
        delegate.goToGeoPosition(aPosition);
    }

    @ScriptFunction(jsDoc = "Gets geo position.")
    public Point getGeoPosition() throws Exception {
        return delegate.getGeoPosition();
    }

    @ScriptFunction(jsDoc = "Fits map to maximum extent.")
    public void fit() throws Exception {
        delegate.fit();
    }

    @ScriptFunction(jsDoc = "Fits map to specified area.")
    public void fit(Geometry aArea) throws Exception {
        delegate.fit(aArea);
    }

    @ScriptFunction(jsDoc = "Hits to the specified point.")
    public List<SelectionEntry> hit(Point aHitPoint) throws Exception {
        return delegate.hit(aHitPoint);
    }

    @ScriptFunction(jsDoc = "Hits to the specified polygon.")
    public List<SelectionEntry> hit(Polygon aHitPoly) throws Exception {
        return delegate.hit(aHitPoly, true);
    }

    @ScriptFunction(jsDoc = "Hits to the specified point.")
    public List<SelectionEntry> hitSelection(Point aHitPoint) throws Exception {
        return delegate.hitSelection(aHitPoint);
    }

    @ScriptFunction(jsDoc = "Selects specified entries.")
    public void select(List<SelectionEntry> aSelectionEntries) {
        delegate.select(aSelectionEntries);
    }

    @ScriptFunction(jsDoc = "Gets map's geo pane.")
    public JGeoPane getPane() {
        return delegate.getPane();
    }

    @ScriptFunction(jsDoc = "Gets map's tools.")
    public MouseTools getTools() {
        return delegate.getTools();
    }

    @ScriptFunction(jsDoc = "Map's on event.")
    public Function getOnEvent() {
        return delegate.getOnEvent();
    }

    @ScriptFunction
    public void setOnEvent(Function aValue) {
        delegate.setOnEvent(aValue);
    }

    @ScriptFunction(jsDoc = "Adds new layer.")
    public MapLayer addLayer(String aLayerTitle, ScriptableRowset<?> aRowset, Class<?> aGeometryClass, Map<String, Object> aStyleAttributes) throws Exception {
        return delegate.addLayer(aLayerTitle, aRowset, aGeometryClass, aStyleAttributes);
    }

    @ScriptFunction(jsDoc = "Removes layer by specified title.")
    public MapLayer removeLayer(String aLayerTitle) throws Exception {
        return delegate.removeLayer(aLayerTitle);
    }

    @ScriptFunction(jsDoc = "Removes all layers.")
    public MapLayer[] removeAllLayers() {
        return delegate.removeAllLayers();
    }

    @ScriptFunction(jsDoc = "Gets map's layer by title.")
    public MapLayer getLayer(String aLayerTitle) {
        return delegate.getLayer(aLayerTitle);
    }

    @ScriptFunction(jsDoc = "Tranaforms point from cartesian to geo coordinate system.")
    public Point cartesian2Geo(Point aPt) throws Exception {
        return delegate.cartesian2Geo(aPt);
    }

    @ScriptFunction(jsDoc = "Tranaforms point from  geo to cartesian coordinate system.")
    public Point geo2Cartesian(Point aPt) throws Exception {
        return delegate.geo2Cartesian(aPt);
    }

    @ScriptFunction(jsDoc = "Tranaforms point from cartesian to screen coordinate system.")
    public Point cartesian2Screen(Point aPt) throws Exception {
        return delegate.cartesian2Screen(aPt);
    }

    @ScriptFunction(jsDoc = "Tranaforms point from  screen to cartesian coordinate system.")
    public Point screen2Cartesian(Point aPt) throws Exception {
        return delegate.screen2Cartesian(aPt);
    }
}
