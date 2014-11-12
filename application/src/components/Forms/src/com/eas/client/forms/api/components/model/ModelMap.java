/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.client.controls.geopane.JGeoPane;
import com.eas.client.forms.api.Component;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.mousetools.MouseTools;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.List;
import java.util.Map;
import jdk.nashorn.api.scripting.JSObject;
import org.geotools.map.Layer;

/**
 *
 * @author mg
 */
public class ModelMap extends Component<DbMap> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * Experimental. A model component that shows a map.\n"
            + " * Unsupported in HTML5 client.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelMap() {
        super();
        setDelegate(new DbMap());
    }

    protected ModelMap(DbMap aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    private static final String BACKING_URL_JSDOC = ""
            + "/**\n"
            + "* The map tiles service URL.\n"
            + "*/";

    @ScriptFunction(jsDoc = BACKING_URL_JSDOC)
    public String getBackingUrl() {
        return delegate.getBackingUrl();
    }

    @ScriptFunction
    public void setBackingUrl(String aUrl) {
        delegate.setBackingUrl(aUrl);
    }

    private static final String GO_TO_GEO_POSITION_JSDOC = ""
            + "/**\n"
            + "* Makes map move to the specified geo position.\n"
            + "* @param position the position on the map.\n"
            + "*/";

    @ScriptFunction(jsDoc = GO_TO_GEO_POSITION_JSDOC, params = {"position"})
    public void goToGeoPosition(Point aPosition) throws Exception {
        delegate.goToGeoPosition(aPosition);
    }

    private static final String GEO_POSITION_JSDOC = ""
            + "/**\n"
            + "* The current geo position on the map.\n"
            + "*/";

    @ScriptFunction(jsDoc = GEO_POSITION_JSDOC)
    public Point getGeoPosition() throws Exception {
        return delegate.getGeoPosition();
    }

    public void fit() throws Exception {
        delegate.fit();
    }

    private static final String FIT_JSDOC = ""
            + "/**\n"
            + "* Fits the map to the specified area. If area parameter is not provided fits the map to the maximum extent. \n"
            + "* @param area the <code>Geometry</code> of the specified area (optional) \n"
            + "*/";

    @ScriptFunction(jsDoc = FIT_JSDOC, params = {"area"})
    public void fit(Geometry aArea) throws Exception {
        delegate.fit(aArea);
    }

    private static final String HIT_JSDOC = ""
            + "/**\n"
            + "* Hits to the specified point.\n"
            + "* @param hitObject the object to hit, can be either a Point or a Polygon instance.\n"
            + "* @return an array of <code>SelectionEntry</code> elements\n"
            + "*/";

    @ScriptFunction(jsDoc = HIT_JSDOC, params = {"hitObject"})
    public List<SelectionEntry> hit(Point aHitPoint) throws Exception {
        return delegate.hit(aHitPoint);
    }

    public List<SelectionEntry> hit(Polygon aHitPoly) throws Exception {
        return delegate.hit(aHitPoly, true);
    }

    private static final String HIT_SELECTION_JSDOC = "/**\n"
            + "* Hits the selection on the specified point.\n"
            + "* @param hitPoint the Point to hit.\n"
            + "* @return an array of <code>SelectionEntry</code> elements.\n"
            + "*/";

    @ScriptFunction(jsDoc = HIT_SELECTION_JSDOC, params = {"hitPoint"})
    public List<SelectionEntry> hitSelection(Point aHitPoint) throws Exception {
        return delegate.hitSelection(aHitPoint);
    }

    private static final String SELECT_JSDOC = ""
            + "/**\n"
            + "* Selects specified entries.\n"
            + "* @param selectionEntries the array of <code>SelectionEntry</code> elements to select.\n"
            + "*/";

    @ScriptFunction(jsDoc = SELECT_JSDOC, params = {"selectionEntries"})
    public void select(List<SelectionEntry> aSelectionEntries) {
        delegate.select(aSelectionEntries);
    }

    private static final String PANE_JSDOC = ""
            + "/**\n"
            + "* The map's geo pane (read only).\n"
            + "*/";

    @ScriptFunction(jsDoc = PANE_JSDOC)
    public JGeoPane getPane() {
        return delegate.getPane();
    }

    private static final String TOOLS_JSDOC = ""
            + "/**\n"
            + "* The map's mouse tools.\n"
            + "*/";

    @ScriptFunction(jsDoc = TOOLS_JSDOC)
    public MouseTools getTools() {
        return delegate.getTools();
    }

    private static final String ON_EVENT_JSDOC = ""
            + "/**\n"
            + "* The map's event handler function.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_EVENT_JSDOC)
    public JSObject getOnEvent() {
        return delegate.getOnEvent();
    }

    @ScriptFunction
    public void setOnEvent(JSObject aValue) {
        delegate.setOnEvent(aValue);
    }

    private static final String ADD_LAYER_JSDOC = ""
            + "/**\n"
            + "* Adds new layer to the map.\n"
            + "* @param layerTitle the layer's title.\n"
            + "* @param rowset the layer's data.\n"
            + "* @param geometryClass the geometry class.\n"
            + "* @param styleAttributes the layer's style attributes.\n"
            + "* @return <code>MapLayer</code> instance.\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_LAYER_JSDOC, params = {"layerTitle", "rowset", "geometryClass", "styleAttributes"})
    public Layer addLayer(String aLayerTitle, ApplicationEntity<?, ?, ?> aRowset, Class<?> aGeometryClass, Map<String, Object> aStyleAttributes) throws Exception {
        return delegate.addLayer(aLayerTitle, aRowset, aGeometryClass, aStyleAttributes);
    }

    private static final String REMOVE_LAYER_JSDOC = ""
            + "/**\n"
            + "* Removes layer by the specified title.\n"
            + "* @param layerTitle the layer's title.\n"
            + "* @return <code>MapLayer</code> instance.\n"
            + "*/";

    @ScriptFunction(jsDoc = REMOVE_LAYER_JSDOC, params={"layerTitle"})
    public Layer removeLayer(String aLayerTitle) throws Exception {
        return delegate.removeLayer(aLayerTitle);
    }

    private static final String REMOVE_ALL_LAYER_JSDOC = ""
            + "/**\n"
            + "* Removes all layers of the map.\n"
            + "* @return an array of <code>MapLayer</code> instances.\n"
            + "*/";

    @ScriptFunction(jsDoc = REMOVE_ALL_LAYER_JSDOC)
    public Layer[] removeAllLayers() {
        return delegate.removeAllLayers();
    }

    private static final String LAYER_JSDOC = ""
            + "/**\n"
            + "* Gets map's layer by the title.\n"
            + "* @param layerTitle the layer's title.\n"
            + "* @return an <code>MapLayer</code> instance.\n"
            + "*/";

    @ScriptFunction(jsDoc = LAYER_JSDOC, params = {"layerTitle"})
    public Layer getLayer(String aLayerTitle) {
        return delegate.getLayer(aLayerTitle);
    }

    private static final String CARTESIAN2GEO_JSDOC = ""
            + "/**\n"
            + "* Transforms point from cartesian to geo coordinate system.\n"
            + "* @param point the <code>Point</code> to transform.\n"
            + "* @return an tranformed <code>Point</code> instance.\n"
            + "*/";

    @ScriptFunction(jsDoc = CARTESIAN2GEO_JSDOC, params = {"point"})
    public Point cartesian2Geo(Point aPt) throws Exception {
        return delegate.cartesian2Geo(aPt);
    }

    private static final String GEO2CARTESIAN_JSDOC = ""
            + "/**\n"
            + "* Transforms point from geo to cartesian coordinate system.\n"
            + "* @param point the <code>Point</code> to transform.\n"
            + "* @return an tranformed <code>Point</code> instance.\n"
            + "*/";

    @ScriptFunction(jsDoc = GEO2CARTESIAN_JSDOC, params = {"point"})
    public Point geo2Cartesian(Point aPt) throws Exception {
        return delegate.geo2Cartesian(aPt);
    }

    private static final String CARTESIAN2SCREEN_JSDOC = ""
            + "/**\n"
            + "* Transforms point from cartesian to screen coordinate system.\n"
            + "* @param point the <code>Point</code> to transform.\n"
            + "* @return an tranformed <code>Point</code> instance.\n"
            + "*/";

    @ScriptFunction(jsDoc = CARTESIAN2SCREEN_JSDOC, params = {"point"})
    public Point cartesian2Screen(Point aPt) throws Exception {
        return delegate.cartesian2Screen(aPt);
    }

    private static final String SCREEN2CARTESIAN_JSDOC = ""
            + "/**\n"
            + "* Tranaforms point from  screen to cartesian coordinate system.\n"
            + "* @param point the <code>Point</code> to transform.\n"
            + "* @return an tranformed <code>Point</code> instance.\n"
            + "*/";

    @ScriptFunction(jsDoc = SCREEN2CARTESIAN_JSDOC, params = {"point"})
    public Point screen2Cartesian(Point aPt) throws Exception {
        return delegate.screen2Cartesian(aPt);
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject)publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
