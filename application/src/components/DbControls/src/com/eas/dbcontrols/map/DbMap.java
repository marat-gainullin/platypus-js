/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map;

import com.bearsoft.rowset.Row;
import com.eas.client.controls.geopane.JGeoPane;
import com.eas.client.controls.geopane.actions.DownAction;
import com.eas.client.controls.geopane.actions.InfoAction;
import com.eas.client.controls.geopane.actions.LeftAction;
import com.eas.client.controls.geopane.actions.RightAction;
import com.eas.client.controls.geopane.actions.UpAction;
import com.eas.client.controls.geopane.actions.ZoomInAction;
import com.eas.client.controls.geopane.actions.ZoomOutAction;
import com.eas.client.controls.geopane.events.GeoPaneMouseListener;
import com.eas.client.controls.geopane.events.GeoPaneViewpointListener;
import com.eas.client.controls.geopane.events.MapClickedEvent;
import com.eas.client.controls.geopane.events.ViewpointScaledEvent;
import com.eas.client.controls.geopane.events.ViewpointTranslatedEvent;
import com.eas.client.geo.GisUtilities;
import com.eas.client.geo.PointSymbol;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.geo.datastore.RowsFeatureSource;
import com.eas.client.geo.selectiondatastore.SelectionDataStore;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.client.model.ModelEntityRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.IconCache;
import com.eas.dbcontrols.RowsetsDbControl;
import com.eas.dbcontrols.map.actions.ClearSelectionAction;
import com.eas.dbcontrols.map.actions.DeleteAction;
import com.eas.dbcontrols.map.mousetools.MouseTools;
import com.eas.design.Undesignable;
import com.eas.script.HasPublished;
import com.eas.script.ScriptUtils;
import com.eas.store.PropertiesSimpleFactory;
import com.eas.util.gis.GeometryUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import jdk.nashorn.api.scripting.JSObject;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author pk, mg
 */
public class DbMap extends JPanel implements DbControl, RowsetsDbControl, PropertiesSimpleFactory {

    public void bindRowsetFeatureDescriptors() {
        if (model != null) {
            // Resolve entities links
            for (final RowsetFeatureDescriptor rfd : features) {
                if (rfd.getEntity() == null && rfd.getRef() != null) {
                    final ApplicationEntity<?, ?, ?> featureEntity = model.getEntityById(rfd.getRef().getEntityId());
                    rfd.setEntity(featureEntity);
                }
            }
        }
    }

    public double calculateCurrentHitBuffer() throws Exception {
        Point2D.Double cartPt = pane.awtScreen2Cartesian(new java.awt.Point(0, 0));
        Point2D.Double cartPt1 = pane.awtScreen2Cartesian(new java.awt.Point(hitPrecision, hitPrecision));
        cartPt = pane.cartesian2Geo(cartPt);
        cartPt1 = pane.cartesian2Geo(cartPt1);
        double bufferZoneWidth = Math.abs(cartPt1.x - cartPt.x);
        return bufferZoneWidth;
    }

    protected class PaneSizeReflector extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            try {
                pane.translateView(0, 0, true);
            } catch (Exception ex) {
                Logger.getLogger(DbMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected class ViewpointScriptInformer implements GeoPaneViewpointListener {

        @Override
        public void scaled(ViewpointScaledEvent aEvent) {
            fireScriptEvent(aEvent);
        }

        @Override
        public void translated(ViewpointTranslatedEvent aEvent) {
            fireScriptEvent(aEvent);
        }
    }

    protected class MouseScriptInformer implements GeoPaneMouseListener {

        @Override
        public void clicked(MapClickedEvent aEvent) {
            fireScriptEvent(aEvent);
        }
    }
    public static final int DEFAULTHIT_PRECISION = 4;
    public static final String BEFORE_SHOW_TOOL_TIP_FUNCTION_NAME = "onBeforeToolTipShow";
    protected int hitPrecision = DEFAULTHIT_PRECISION;
    private ApplicationModel<?, ?, ?, ?> model;
    private JGeoPane pane;
    private ProjectedCRS projectedCrs;
    protected MouseTools tools;
    private String backingUrl;
    private Object published;
    protected SelectionDataStore selectionStore;
    protected List<SelectionEntry> emptySelectionList = new ArrayList<>();
    protected final DatamodelDataStore dataStore = new DatamodelDataStore();
    protected JSObject onEvent;
    protected JSObject onToolTip;
    protected String mapTitle;
    protected List<RowsetFeatureDescriptor> features = new ArrayList<>();
    protected String crsWkt;

    public DbMap() {
        setLayout(new BorderLayout());
        initializeDesign();
    }

    public void injectPublished(Object aValue) {
        published = aValue;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        int width = d.width;
        int height = d.height;
        if (width < DbControlsUtils.DB_CONTROLS_DEFAULT_WIDTH) {
            width = DbControlsUtils.DB_CONTROLS_DEFAULT_WIDTH;
        }
        if (height < DbControlsUtils.DB_CONTROLS_DEFAULT_HEIGHT) {
            height = DbControlsUtils.DB_CONTROLS_DEFAULT_HEIGHT;
        }
        return new Dimension(width, height);
    }

    public void initializeDesign() {
        if (getComponentCount() == 0) {
            setLayout(new BorderLayout());
            JLabel cb = new JLabel(this.getClass().getSimpleName().replace("Db", "Model"), IconCache.getIcon("16x16/map.png"), SwingConstants.LEADING);
            cb.setOpaque(false);
            add(cb, BorderLayout.CENTER);
        }
    }

    @Override
    public ApplicationModel<?, ?, ?, ?> getModel() {
        return model;
    }

    @Override
    public void setModel(ApplicationModel<?, ?, ?, ?> aValue) {
        if (model != null) {
            model.getChangeSupport().removePropertyChangeListener(this);
        }
        model = aValue;
        if (model != null) {
            model.getChangeSupport().addPropertyChangeListener(this);
        }
    }

    @Override
    public void beginUpdate() {
    }

    @Undesignable
    public JSObject getMapEventListener() {
        return onEvent;
    }

    public void setMapEventListener(JSObject aValue) {
        onEvent = aValue;
    }

    public String getMapTitle() {
        return mapTitle;
    }

    public void setMapTitle(String aValue) {
        mapTitle = aValue;
    }

    @Undesignable
    public List<RowsetFeatureDescriptor> getFeatures() {
        return features;
    }

    public void setFeatures(List<RowsetFeatureDescriptor> aValue) {
        features = aValue;
    }

    public String getCrsWkt() {
        return crsWkt;
    }

    public void setCrsWkt(String aValue) {
        crsWkt = aValue;
    }

    public void cleanup() {
        //...
        pane = null;
    }

    public void configure() {
        cleanup();
        bindRowsetFeatureDescriptors();
        final Logger logger = Logger.getLogger(DbMap.class.getName());
        try {
            logger.fine("Configuring geo pane with feature layers."); //NOI18N
            checkFeatureDescriptorsCrsWkt();
            if (projectedCrs == null) {
                throw new NullPointerException("Projected CRS havn't been created.");
            }
            final MapContent mapContext = new MapContent(projectedCrs),
                    activeMapContext = new MapContent(projectedCrs),
                    screenMapContext = new MapContent(projectedCrs);
            dataStore.setFeatureDescriptors(features);
            if (mapTitle != null && !mapTitle.isEmpty()) {
                mapContext.setTitle(mapTitle);
            }
            for (RowsetFeatureDescriptor featureDescriptor : features) {
                logger.fine(String.format("Adding layer for feature %s", featureDescriptor.getTypeName())); //NOI18N
                final Style style = featureDescriptor.getStyle().isEmpty() ? null : featureDescriptor.getStyle().buildStyle(featureDescriptor.getGeometryBindingClass(), projectedCrs.getConversionFromBase().getMathTransform().toWKT());
                if (featureDescriptor.isActive()) {
                    activeMapContext.addLayer(new FeatureLayer(dataStore.getFeatureSource(featureDescriptor.getTypeName()), style));
                } else {
                    mapContext.addLayer(new FeatureLayer(dataStore.getFeatureSource(featureDescriptor.getTypeName()), style));
                }
            }

            selectionStore = new SelectionDataStore(projectedCrs.getBaseCRS());
            screenMapContext.addLayer(new FeatureLayer(selectionStore.getFeatureSource(SelectionDataStore.SELECTION_FEATURE_PHANTOM_TYPE_NAME), GisUtilities.buildSelectionPhantomStyle()));
            screenMapContext.addLayer(new FeatureLayer(selectionStore.getFeatureSource(SelectionDataStore.SELECTION_FEATURE_TYPE_NAME), GisUtilities.buildSelectionStyle()));
            screenMapContext.layers().get(screenMapContext.layers().size() - 1).setSelected(true);

            logger.fine("Setting up the geo pane."); //NOI18N
            if (backingUrl != null) {
                pane = new JGeoPane(mapContext, activeMapContext, screenMapContext, backingUrl);
            } else {
                pane = new JGeoPane(mapContext, activeMapContext, screenMapContext, true);
            }
            pane.addGeoPaneViewpointListener(new ViewpointScriptInformer());
            pane.addGeoPaneMouseListener(new MouseScriptInformer());
            pane.setBackground(getBackground());
            pane.scaleView(8e-4, 8e-4, false);
            add(pane, BorderLayout.CENTER);
            configureActions();
            pane.addComponentListener(new PaneSizeReflector());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Creating a map context", ex); //NOI18N
        }
    }

    public void checkFeatureDescriptorsCrsWkt() throws FactoryException {
        projectedCrs = createProjectedCrs();
        if (projectedCrs != null) {
            for (RowsetFeatureDescriptor descriptor : features) {
                if (descriptor.getCrsWkt() == null || descriptor.getCrsWkt().isEmpty()) {
                    GeographicCRS geoCrs = projectedCrs.getBaseCRS();
                    descriptor.setCrsWkt(geoCrs.toWKT());
                }
            }
        } else {
            Logger.getLogger(DbMapDesignInfo.class.getName()).log(Level.SEVERE, "Can't create coordinate system. Probably bad configuration.");
        }
    }

    public ProjectedCRS createProjectedCrs() throws FactoryException {
        if (crsWkt != null && !crsWkt.isEmpty()) {
            CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
            return (ProjectedCRS) crsFactory.createFromWKT(crsWkt);
        } else {
            return null;
        }
    }

    /**
     * Adds new layer to the DbMap component, based on ScriptableRowset
     * instance.
     *
     * @param aLayerTitle Title of created layer.
     * @param aEntity ScriptableRowset instance, the new layer will be based on.
     * @param aGeometryClass Class of the geometry to be used in new layer.
     * @param aStyleAttributes
     * @return MapLayer instance, just created.
     * @throws java.lang.Exception
     */
    public Layer addLayer(String aLayerTitle, ApplicationEntity<?, ?, ?> aEntity, Class<?> aGeometryClass, Map<String, Object> aStyleAttributes) throws Exception {
        Layer layer;
        MapContent lightweightMapContext = pane.getLightweightMapContext();
        synchronized (lightweightMapContext) {
            RowsetFeatureDescriptor newFeatureDescriptor = new RowsetFeatureDescriptor(aLayerTitle, aEntity, new ModelEntityRef(aEntity.getEntityId()));
            newFeatureDescriptor.setActive(true);
            newFeatureDescriptor.setGeometryBindingClass(aGeometryClass);
            newFeatureDescriptor.setCrsWkt(projectedCrs.getBaseCRS().toWKT());
            dataStore.getFeatureDescriptors().put(aLayerTitle, newFeatureDescriptor);
            SimpleFeatureSource newFeatureSource = dataStore.getFeatureSource(aLayerTitle);
            Object oSize = aStyleAttributes.get("size");
            if (oSize instanceof Number) {
                newFeatureDescriptor.getStyle().setSize(((Number) oSize).floatValue());
            }
            Object oOpacity = aStyleAttributes.get("opacity");
            if (oOpacity instanceof Number) {
                newFeatureDescriptor.getStyle().setOpacity(((Number) oOpacity).intValue());
            }
            Object oLineColor = aStyleAttributes.get("lineColor");
            if (oLineColor instanceof Color) {
                newFeatureDescriptor.getStyle().setLineColor((Color) oLineColor);
            }
            Object oHaloColor = aStyleAttributes.get("haloColor");
            if (oHaloColor instanceof Color) {
                newFeatureDescriptor.getStyle().setHaloColor((Color) oHaloColor);
            }
            Object oFont = aStyleAttributes.get("font");
            if (oFont instanceof Font) {
                newFeatureDescriptor.getStyle().setFont((Font) oFont);
            }
            Object oFillColor = aStyleAttributes.get("fillColor");
            if (oFillColor instanceof Color) {
                newFeatureDescriptor.getStyle().setFillColor((Color) oFillColor);
            }
            Object oPointSymbol = aStyleAttributes.get("pointSymbol");
            if (oPointSymbol instanceof PointSymbol) {
                newFeatureDescriptor.getStyle().setPointSymbol((PointSymbol) oPointSymbol);
            }
            Style newStyle = newFeatureDescriptor.getStyle().buildStyle(newFeatureDescriptor.getGeometryBindingClass(), projectedCrs.getConversionFromBase().getMathTransform().toWKT());
            lightweightMapContext.addLayer(new FeatureLayer(newFeatureSource, newStyle));
            layer = lightweightMapContext.layers().get(lightweightMapContext.layers().size() - 1);
            layer.addMapLayerListener(pane.getLightChangesReflector());
        }
        pane.clearLightweightCache();
        pane.repaint();
        return layer;
    }

    public Layer removeLayer(String aLayerTitle) throws Exception {
        Layer removed;
        MapContent lightweightMapContext = pane.getLightweightMapContext();
        synchronized (lightweightMapContext) {
            removed = findLayer(aLayerTitle, lightweightMapContext.layers());
            if (removed != null) {
                lightweightMapContext.removeLayer(removed);
                removed.removeMapLayerListener(pane.getLightChangesReflector());
            }
            dataStore.clearTypeInfoCache(aLayerTitle);
            dataStore.getFeatureDescriptors().remove(aLayerTitle);
        }
        pane.clearLightweightCache();
        pane.repaint();
        return removed;
    }

    public Layer[] removeAllLayers() {
        List<Layer> lightweightLayers;
        MapContent lightweightMapContext = pane.getLightweightMapContext();
        synchronized (lightweightMapContext) {
            lightweightLayers = lightweightMapContext.layers();
            for (Layer layer : lightweightLayers) {
                layer.removeMapLayerListener(pane.getLightChangesReflector());
            }
            lightweightLayers.clear();
        }
        pane.clearLightweightCache();
        pane.repaint();
        return lightweightLayers.toArray(new Layer[0]);
    }

    public Layer getLayer(String aLayerTitle) {
        MapContent generalMapContext = pane.getGeneralMapContext();
        Layer found;
        synchronized (generalMapContext) {// may this is not need to do because changes are made only on lightweight context
            found = findLayer(aLayerTitle, generalMapContext.layers());
        }
        if (found == null) {
            MapContent lightweightMapContext = pane.getLightweightMapContext();
            synchronized (lightweightMapContext) {
                found = findLayer(aLayerTitle, lightweightMapContext.layers());
            }
        }
        return found;
    }

    private Layer findLayer(String aLayerTitle, List<Layer> layers) {
        for (Layer layer : layers) {
            FeatureSource<?, ?> layersource = layer.getFeatureSource();
            if (layersource instanceof RowsFeatureSource) {
                RowsFeatureSource rSource = (RowsFeatureSource) layersource;
                if (rSource.getName() != null) {
                    String layerTitle = rSource.getName().getLocalPart();
                    if (layerTitle != null && layerTitle.equals(aLayerTitle)) {
                        return layer;
                    }
                }
            }
        }
        return null;
    }

    public Point cartesian2Geo(Point aPt) throws FactoryException, TransformException {
        Point2D.Double res = pane.cartesian2Geo(new Point2D.Double(aPt.getX(), aPt.getY()));
        return GeometryUtils.createPoint(res);
    }

    public Point geo2Cartesian(Point aPt) throws FactoryException, TransformException {
        Point2D.Double res = pane.geo2Cartesian(new Point2D.Double(aPt.getX(), aPt.getY()));
        return GeometryUtils.createPoint(res);
    }

    public Point cartesian2Screen(Point aPt) throws FactoryException, TransformException {
        java.awt.Point res = pane.cartesian2Screen(new Point2D.Double(aPt.getX(), aPt.getY()));
        return GeometryUtils.createPoint(res.x, res.y);
    }

    public Point screen2Cartesian(Point aPt) throws NoninvertibleTransformException {
        Point2D.Double res = pane.screen2Cartesian(new java.awt.Point((int) Math.round(aPt.getX()), (int) Math.round(aPt.getY())));
        return GeometryUtils.createPoint(res);
    }

    @Override
    public void endUpdate() {
    }

    public void fireCellEditingCompleted() {
    }

    @Override
    public boolean isUpdating() {
        return false;
    }

    @Override
    public Object createPropertyObjectInstance(String aSimpleClassName) {
        return DbControlsUtils.createDesignInfoBySimpleClassName(aSimpleClassName);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            if (evt != null && evt.getSource() == model
                    && "runtime".equals(evt.getPropertyName())) {
                if (Boolean.FALSE.equals(evt.getOldValue())
                        && Boolean.TRUE.equals(evt.getNewValue())) {
                    configure();
                } else if (Boolean.FALSE.equals(evt.getNewValue())) {
                    cleanup();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DbMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getBackingUrl() {
        if (pane != null) {
            return pane.getBackingUrl();
        } else {
            return backingUrl;
        }
    }

    public void setBackingUrl(String aUrl) {
        if (pane != null) {
            pane.setBackingUrl(aUrl);
        } else {
            backingUrl = aUrl;
        }
    }

    public void goToGeoPosition(Point aPosition) throws Exception {
        if (pane != null && aPosition != null) {
            pane.goToGeoPosition(new java.awt.Point.Double(aPosition.getX(), aPosition.getY()));
        }
    }

    public Point getGeoPosition() throws Exception {
        if (pane != null) {
            java.awt.Point.Double position = pane.getGeoPosition();
            return GeometryUtils.createPoint(position.x, position.y);
        } else {
            return null;
        }
    }

    public void fit() throws Exception {
        if (pane != null) {
            pane.fit();
        }
    }

    public void fit(Geometry aArea) throws Exception {
        if (pane != null) {
            pane.fit(aArea);
        }
    }

    /**
     * Tests if <code>aHitPoint</code> intersects any of shapes.
     *
     * @param aHitPoint Point to test with. The point must be in geo
     * coordiantes.
     * @return Returns a list, containing hit results.
     * @throws Exception
     */
    public List<SelectionEntry> hit(Point aHitPoint) throws Exception {
        double bufferZoneWidth = calculateCurrentHitBuffer();
        Polygon hitPoly = GeometryUtils.constructRectyPolygon(new Point2D.Double(aHitPoint.getX() - bufferZoneWidth, aHitPoint.getY() - bufferZoneWidth), new Point2D.Double(aHitPoint.getX() + bufferZoneWidth, aHitPoint.getY() + bufferZoneWidth));
        return hit(hitPoly, true);
    }

    public List<SelectionEntry> nonSelectableHit(Point aHitPoint) throws Exception {
        double bufferZoneWidth = calculateCurrentHitBuffer();
        Polygon hitPoly = GeometryUtils.constructRectyPolygon(new Point2D.Double(aHitPoint.getX() - bufferZoneWidth, aHitPoint.getY() - bufferZoneWidth), new Point2D.Double(aHitPoint.getX() + bufferZoneWidth, aHitPoint.getY() + bufferZoneWidth));
        return hit(hitPoly, false);
    }

    public List<SelectionEntry> hit(Polygon aHitPoly, boolean needSelectable) throws Exception {
        List<SelectionEntry> hitedResults = new ArrayList<>();
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
        for (Layer layer : pane.getLightweightMapContext().layers()) {
            FeatureSource fs = layer.getFeatureSource();
            RowsetFeatureDescriptor featureDescriptor = dataStore.getFeatureDescriptors().get(fs.getName().getLocalPart());
            if ((needSelectable && featureDescriptor.isSelectable()) || !needSelectable) {
                Filter filter = ff.intersects(ff.property(fs.getSchema().getGeometryDescriptor().getLocalName()), ff.literal(aHitPoly));
                FeatureCollection<SimpleFeatureType, SimpleFeature> layerResults = fs.getFeatures(filter);
                FeatureIterator<SimpleFeature> fIt = layerResults.features();
                while (fIt.hasNext()) {
                    SimpleFeature feature = fIt.next();
                    GisUtilities.convertSimpleFeature2SelectionEntries(feature, hitedResults, featureDescriptor);
                }
            }
        }
        return hitedResults;
    }

    /**
     * Tests if <code>aHitPoint</code> intersects any of already selected
     * shapes.
     *
     * @param aHitPoint Point to test with. The point must be in geo
     * coordiantes.
     * @return Returns a list, containing hit results.
     * @throws Exception
     */
    public List<SelectionEntry> hitSelection(Point aHitPoint) throws Exception {
        if (!selectionStore.isEmpty()) {
            double bufferZoneWidth = calculateCurrentHitBuffer();
            Polygon hitPoly = GeometryUtils.constructRectyPolygon(new Point2D.Double(aHitPoint.getX() - bufferZoneWidth, aHitPoint.getY() - bufferZoneWidth), new Point2D.Double(aHitPoint.getX() + bufferZoneWidth, aHitPoint.getY() + bufferZoneWidth));
            return hitSelection(hitPoly);
        } else {
            return emptySelectionList;
        }
    }

    protected List<SelectionEntry> hitSelection(Polygon aHitPoly) throws Exception {
        if (!selectionStore.isEmpty()) {
            List<SelectionEntry> hitedResults = new ArrayList<>();
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
            for (Layer layer : pane.getScreenContext().layers()) {
                if (layer.isSelected()) {
                    FeatureSource<SimpleFeatureType, SimpleFeature> fs = (FeatureSource<SimpleFeatureType, SimpleFeature>) layer.getFeatureSource();
                    Filter filter = ff.intersects(ff.property(fs.getSchema().getGeometryDescriptor().getLocalName()), ff.literal(aHitPoly));
                    FeatureCollection<SimpleFeatureType, SimpleFeature> layerResults = fs.getFeatures(filter);
                    FeatureIterator<SimpleFeature> fIt = layerResults.features();
                    while (fIt.hasNext()) {
                        SimpleFeature feature = fIt.next();
                        Object oAttribute = feature.getAttribute(SelectionEntry.THIS_ENTRY_ATTR_NAME);
                        assert oAttribute instanceof SelectionEntry;
                        hitedResults.add((SelectionEntry) oAttribute);
                    }
                }
            }
            return hitedResults;
        } else {
            return emptySelectionList;
        }
    }

    public void select(List<SelectionEntry> aSelectionEntries) {
        selectionStore.add2Selection(aSelectionEntries);
    }

    /**
     * Test whether a point is on one of points in selection Strictly selected
     * !points! are checked, not undelying features!
     *
     * @param aPoint2HitWith Point to test selected points against. The point
     * must be in geo coordiantes.
     * @return True if hitted, false otherwise
     * @throws Exception
     */
    public boolean selectedPointHitted(Point aPoint2HitWith) throws Exception {
        return !hitSelection(aPoint2HitWith).isEmpty();
    }

    /**
     * Tests whether a point is on one of the selected geometries, rather than
     * points
     *
     * @param aPoint2HitWith Point to test selected geometries against. The
     * point must be in geo coordiantes.
     * @return True if hitted, false otherwise
     * @throws Exception
     */
    public boolean selectedGeometryHitted(Point aPoint2HitWith) throws Exception {
        if (!getSelection().isEmpty()) {
            double bufferZoneWidth = calculateCurrentHitBuffer();
            Polygon hitPoly = GeometryUtils.constructRectyPolygon(new Point2D.Double(aPoint2HitWith.getX() - bufferZoneWidth, aPoint2HitWith.getY() - bufferZoneWidth), new Point2D.Double(aPoint2HitWith.getX() + bufferZoneWidth, aPoint2HitWith.getY() + bufferZoneWidth));
            Set<Geometry> selectedGeometries = GisUtilities.convertSelectionEntries2Geometries(getSelection().getSelection());
            for (Geometry geom : selectedGeometries) {
                if (geom.intersects(hitPoly)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void fireSelectionChanged() {
        selectionStore.fireSelectionChanged();
    }

    public SelectionDataStore getSelection() {
        return selectionStore;
    }

    public JGeoPane getPane() {
        return pane;
    }

    public MouseTools getTools() {
        return tools;
    }

    private void configureActions() {
        addAction(new LeftAction(pane));
        addAction(new RightAction(pane));
        addAction(new UpAction(pane));
        addAction(new DownAction(pane));
        addAction(new ZoomInAction(pane));
        addAction(new ZoomOutAction(pane));
        addAction(new InfoAction(pane));
        addAction(new DeleteAction(this));
        addAction(new ClearSelectionAction(this));

        tools = new MouseTools(this);
        tools.install(MouseTools.NAVIGATION);
    }

    private void addAction(Action aAction) {
        assert pane != null : "Geo Pane must be already contructed and configured!";
        pane.getInputMap().put((KeyStroke) aAction.getValue(Action.ACCELERATOR_KEY), aAction.getClass().getSimpleName());
        pane.getActionMap().put(aAction.getClass().getSimpleName(), aAction);
    }

    public void fireScriptEvent(final HasPublished aEvent) {
        if (onEvent != null && published != null) {
            try {
                onEvent.call(published, new Object[]{aEvent.getPublished()});
            } catch (Exception ex) {
                Logger.getLogger(DbMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String beforeToolTipShow(Row aRow, ApplicationEntity<?, ?, ?> aEntity) throws Exception {
        if (onToolTip != null) {
            Object result = ScriptUtils.toJava(onToolTip.call(published, new Object[]{aRow != null ? aRow.getPublished() : null}));
            if (result instanceof String) {
                return (String) result;
            }
        }
        return null;
    }

    @Undesignable
    public JSObject getOnEvent() {
        return onEvent;
    }

    public void setOnEvent(JSObject aValue) {
        onEvent = aValue;
    }
}
