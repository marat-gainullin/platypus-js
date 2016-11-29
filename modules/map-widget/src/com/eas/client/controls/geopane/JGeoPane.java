/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane;

import com.eas.client.controls.geopane.cache.MapTilesCache.RenderingTask;
import com.eas.client.controls.geopane.cache.*;
import com.eas.client.controls.geopane.cache.webtiles.AsyncWebMapTilesCache;
import com.eas.client.controls.geopane.events.*;
import com.vividsolutions.jts.geom.*;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.event.MapLayerEvent;
import org.geotools.map.event.MapLayerListener;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.resources.BoundingBoxes;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author mg
 */
public class JGeoPane extends JTiledPane {

    public static final double SCALE_MIN_BOUNDARY = 1.6e-5;
    public static final double SCALE_MAX_BOUNDARY = 1e+3;
    protected AffineTransform cartesian2Screen;
    protected MapLayerListener generalChangesReflector;
    protected MapLayerListener lightChangesReflector;
    protected MapLayerListener screenChangesReflector;
    protected Set<GeoPaneViewpointListener> viewListeners = new HashSet<>();
    protected Set<GeoPaneMouseListener> mouseListeners = new HashSet<>();
    protected MapContent screenContext;
    protected GTRenderer screenRenderer = new StreamingRenderer();

    /**
     * Scales current view point transformation.
     * @param sx Amount to scale by along the x axis.
     * @param sy Amount to scale by along the y axis.
     * @param isLast Flag, indicating that this scale is last in sequence of arbitrary view point operations.
     * @throws Exception
     */
    public void scaleView(double sx, double sy, boolean isLast) throws Exception {
        recalcViewPoint();
        double targetScaleX = cartesian2Screen.getScaleX() * sx;
        double targetScaleY = cartesian2Screen.getScaleY() * sy;
        if (targetScaleX >= SCALE_MIN_BOUNDARY && targetScaleX <= SCALE_MAX_BOUNDARY
                && targetScaleY >= SCALE_MIN_BOUNDARY && targetScaleY <= SCALE_MAX_BOUNDARY) {
            cartesian2Screen.scale(sx, sy);
            cache.scaleChanged();
            lightweightCache.scaleChanged();
            if (isLast) {
                invalidate();
                fireViewScaled(sx, sy, aoiToGeoGeometry(), aoiToCartesianGeometry());
            }
        }
    }

    /**
     * Translates current view point transformation.
     * @param tx Amount to translate by along the x axis.
     * @param ty Amount to translate by along the y axis.
     * @param isLast Flag, indicating that this translate is last in sequence of arbitrary view point operations.
     * @throws Exception
     */
    public void translateView(double tx, double ty, boolean isLast) throws Exception {
        recalcViewPoint();
        cartesian2Screen.translate(tx, ty);
        clearCaches();
        if (isLast) {
            invalidate();
            fireViewTranslated(tx, ty, aoiToGeoGeometry(), aoiToCartesianGeometry());
        }
    }

    /**
     * Translates current screen tiles grid to avoid actual view point transformation.
     * Such grid translating leads to view point changing without any geo recalculations.
     * This method also fires view point changed event if <code>isLast</code> param is true.
     * @param x Amount of offset in screen coordinate space along horizontal axis.
     * @param y Amount of offset in screen coordinate space along vertical axis.
     * @param isLast Flag, indicating that this translate call is last call in some sequence.
     * If true, view point change event will be fired.
     * @throws Exception
     */
    @Override
    public void translateGrid(int x, int y, boolean isLast) throws Exception {
        super.translateGrid(x, y, isLast);
        if (isLast) {
            Point2D.Double screenCenterBefore = screen2Cartesian(new Point(0, 0));
            Point2D.Double screenCenterAfter = screen2Cartesian(new Point(x, y));
            fireViewTranslated(screenCenterAfter.x - screenCenterBefore.x, screenCenterAfter.y - screenCenterBefore.y, aoiToGeoGeometry(), aoiToCartesianGeometry());
        }
    }

    public String viewToString() {
        return "View point tranform:\n" + cartesian2Screen.toString();
    }

    public String aoiToCartesianString() throws NoninvertibleTransformException {
        Dimension size = getSize();
        Point2D.Double lu = awtScreen2Cartesian(new Point(0, 0));
        Point2D.Double br = awtScreen2Cartesian(new Point(size.width, size.height));
        return "Area of interest in cartesian:\n" + "\tleft up corner: " + lu.toString() + "\n" + "\tright bottom corner" + br.toString();
    }

    public String aoiToGeoString() throws NoninvertibleTransformException, FactoryException, TransformException {
        Dimension size = getSize();
        Point2D.Double lu = awtScreen2Cartesian(new Point(0, 0));
        lu = cartesian2Geo(lu);
        Point2D.Double br = awtScreen2Cartesian(new Point(size.width, size.height));
        br = cartesian2Geo(br);
        return "Area of interest in lon/lat:\n" + "\tleft up corner: " + lu.toString() + "\n" + "\tright bottom corner" + br.toString();
    }

    public Geometry aoiToGeoGeometry() throws Exception {
        Dimension size = getSize();
        Point2D.Double lu = awtScreen2Cartesian(new Point(0, 0));
        lu = cartesian2Geo(lu);
        Point2D.Double br = awtScreen2Cartesian(new Point(size.width, size.height));
        br = cartesian2Geo(br);
        return constructRectyPolygonGeometry(lu, br);
    }

    public static Geometry constructRectyPolygonGeometry(Point2D.Double aLeftUpperCorner, Point2D.Double aBottomRightCorner) {
        GeometryFactory gFactory = new GeometryFactory();
        CoordinateSequenceFactory csFactory = gFactory.getCoordinateSequenceFactory();
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(aLeftUpperCorner.x, aLeftUpperCorner.y);
        coordinates[1] = new Coordinate(aBottomRightCorner.x, aLeftUpperCorner.y);
        coordinates[2] = new Coordinate(aBottomRightCorner.x, aBottomRightCorner.y);
        coordinates[3] = new Coordinate(aLeftUpperCorner.x, aBottomRightCorner.y);
        coordinates[4] = new Coordinate(aLeftUpperCorner.x, aLeftUpperCorner.y);
        CoordinateSequence cSeq = csFactory.create(coordinates);
        return gFactory.createPolygon(new LinearRing(cSeq, gFactory), null);
    }

    public static Geometry createPointGeometry(Point2D.Double aPoint) {
        GeometryFactory gFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(aPoint.x, aPoint.y);
        return gFactory.createPoint(coordinate);
    }

    public Geometry aoiToCartesianGeometry() throws Exception {
        Dimension size = getSize();
        Point2D.Double lu = awtScreen2Cartesian(new Point(0, 0));
        Point2D.Double br = awtScreen2Cartesian(new Point(size.width, size.height));
        return constructRectyPolygonGeometry(lu, br);
    }

    public double snapScale(double coef) {
        if (getBackingUrl() != null && !getBackingUrl().isEmpty()) {
            double powValue = Math.log(coef) / Math.log(2);
            Long lPowValue = Math.round(powValue);
            return Math.pow(2, lPowValue);
        } else {
            return coef;
        }
    }

    protected class MapLayerListenerAdapter implements MapLayerListener {

        @Override
        public void layerChanged(MapLayerEvent event) {
        }

        @Override
        public void layerShown(MapLayerEvent event) {
        }

        @Override
        public void layerHidden(MapLayerEvent event) {
        }

        @Override
        public void layerSelected(MapLayerEvent event) {
        }

        @Override
        public void layerDeselected(MapLayerEvent event) {
        }

        @Override
        public void layerPreDispose(MapLayerEvent mle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    protected class LayersChangesReflector extends MapLayerListenerAdapter {

        protected TilesCache layersCache;

        public LayersChangesReflector(TilesCache aCache) {
            super();
            layersCache = aCache;
        }

        @Override
        public void layerChanged(MapLayerEvent event) {
            layersCache.clear();
            repaint();
        }
    }

    protected class ScreenChangesReflector extends MapLayerListenerAdapter {

        @Override
        public void layerChanged(MapLayerEvent event) {
            repaint();
        }
    }

    protected class TileRenderedRepainter implements RenderingTaskListener {

        @Override
        public void taskCompleted(RenderingTask aTask) {
            repaint();
        }
    }

    @Override
    public synchronized void repaint() {
        super.repaint();
    }

    public JGeoPane(MapContent aContext, MapContent aLightweightContext, boolean aAsync) {
        this(aContext, aLightweightContext, null, aAsync);
    }

    public JGeoPane(MapContent aContext, MapContent aLightweightContext, MapContent aScreenContext, boolean aAsync) {
        super();
        setLayout(new GeoLayout());
        screenContext = aScreenContext;
        cartesian2Screen = new AffineTransform();
        if (aAsync) {
            cacheLock = new ReentrantReadWriteLock();
            lightweightCacheLock = new ReentrantReadWriteLock();
            
            cache = new AsyncMapTilesCache(aContext, cacheLock, cartesian2Screen);
            lightweightCache = new AsyncMapTilesCache(aLightweightContext, lightweightCacheLock, cartesian2Screen);
            ((AsyncMapTilesCache) cache).addRenderingTaskListener(new TileRenderedRepainter());
            if (lightweightCache instanceof AsyncMapTilesCache) {
                ((AsyncMapTilesCache) lightweightCache).addRenderingTaskListener(new TileRenderedRepainter());
            }
        } else {
            cache = new SyncMapTilesCache(aContext, cartesian2Screen);
            lightweightCache = new SyncMapTilesCache(aLightweightContext, cartesian2Screen);
        }
        cache.setBackground(getBackground());
        lightweightCache.setImageType(BufferedImage.TYPE_INT_ARGB);
        addLayersListeners();
    }

    public JGeoPane(MapContent aContext, MapContent aLightweightContext, String aBackingUrl) {
        this(aContext, aLightweightContext, null, aBackingUrl);
    }

    public JGeoPane(MapContent aContext, MapContent aLightweightContext, MapContent aScreenContext, String aBackingUrl) {
        super();
        setLayout(new GeoLayout());
        screenContext = aScreenContext;
        cartesian2Screen = new AffineTransform();
        cacheLock = new ReentrantReadWriteLock();
        lightweightCacheLock = new ReentrantReadWriteLock();
        if (aBackingUrl != null && !aBackingUrl.isEmpty()) {
            cache = GeoPaneUtils.createWebTilesCache(aContext, cacheLock, cartesian2Screen, aBackingUrl);
            lightweightCache = new AsyncMapTilesCache(aLightweightContext, lightweightCacheLock, cartesian2Screen);
        } else {
            cache = new AsyncMapTilesCache(aContext, cacheLock, cartesian2Screen);
            lightweightCache = new AsyncMapTilesCache(aLightweightContext, lightweightCacheLock, cartesian2Screen);
        }
        assert cache instanceof AsyncMapTilesCache;
        ((AsyncMapTilesCache) cache).addRenderingTaskListener(new TileRenderedRepainter());
        if (lightweightCache instanceof AsyncMapTilesCache) {
            ((AsyncMapTilesCache) lightweightCache).addRenderingTaskListener(new TileRenderedRepainter());
        }
        cache.setBackground(getBackground());
        lightweightCache.setImageType(BufferedImage.TYPE_INT_ARGB);
        addLayersListeners();
    }

    public MapContent getScreenContext() {
        return screenContext;
    }

    public void setScreenContext(MapContent aValue) {
        screenContext = aValue;
    }

    private void removeLayersListeners() {
        MapContent generalContext = ((MapTilesCache) cache).getMapDisplayContext();
        MapContent lightweightContext = ((MapTilesCache) lightweightCache).getMapDisplayContext();
        if (generalChangesReflector != null) {
            for (Layer layer : generalContext.layers()) {
                layer.removeMapLayerListener(generalChangesReflector);
            }
        }
        if (lightChangesReflector != null) {
            for (Layer layer : lightweightContext.layers()) {
                layer.removeMapLayerListener(lightChangesReflector);
            }
        }
        if (screenContext != null && screenChangesReflector != null) {
            for (Layer layer : screenContext.layers()) {
                layer.removeMapLayerListener(screenChangesReflector);
            }
        }
    }

    private void addLayersListeners() {
        MapContent generalContext = ((MapTilesCache) cache).getMapDisplayContext();
        MapContent lightweightContext = ((MapTilesCache) lightweightCache).getMapDisplayContext();
        generalChangesReflector = new LayersChangesReflector(cache);
        lightChangesReflector = new LayersChangesReflector(lightweightCache);
        screenChangesReflector = new ScreenChangesReflector();
        for (Layer layer : generalContext.layers()) {
            layer.addMapLayerListener(generalChangesReflector);
        }
        for (Layer layer : lightweightContext.layers()) {
            layer.addMapLayerListener(lightChangesReflector);
        }
        if (screenContext != null) {
            for (Layer layer : screenContext.layers()) {
                layer.addMapLayerListener(screenChangesReflector);
            }
        }
    }

    public MapLayerListener getGeneralChangesReflector() {
        return generalChangesReflector;
    }

    public MapLayerListener getLightChangesReflector() {
        return lightChangesReflector;
    }

    @Override
    protected void paintScreenContext(Graphics2D g) {
        if (screenContext != null) {
            synchronized (screenContext) {
                try {
                    // let's paint selection and phanton layers
                    screenRenderer.setMapContent(screenContext);
                    Rectangle clip = g.getClipBounds();
                    Point2D.Double lu = screen2Cartesian(clip.getLocation());
                    Point2D.Double br = screen2Cartesian(new Point(clip.x + clip.width, clip.y + clip.height));
                    Geometry aoiGeometry = constructRectyPolygonGeometry(lu, br);
                    Envelope aoiEnvelope = new ReferencedEnvelope(aoiGeometry.getEnvelopeInternal(), screenContext.getCoordinateReferenceSystem());
                    screenRenderer.paint(g, clip, aoiEnvelope, cartesian2Screen);
                } catch (NoninvertibleTransformException ex) {
                    Logger.getLogger(JGeoPane.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        super.paintScreenContext(g);
    }

    public MapContent getGeneralMapContext() {
        assert cache instanceof MapTilesCache : "JGeoPane instance requires MapTilesCache instance as general cache";
        return ((MapTilesCache) cache).getMapDisplayContext();
    }

    public MapContent getLightweightMapContext() {
        assert lightweightCache instanceof MapTilesCache : "JGeoPane instance requires MapTilesCache instance as general cache";
        return ((MapTilesCache) lightweightCache).getMapDisplayContext();
    }
           
    public String getBackingUrl() {
        if (cache instanceof AsyncWebMapTilesCache) {
            return ((AsyncWebMapTilesCache) cache).getBackingUrl();
        } else {
            return null;
        }
    }

    public void setBackingUrl(String aBackingUrl) {
        String oldBackingUrl = getBackingUrl();
        if (oldBackingUrl == null) {
            oldBackingUrl = "";
        }
        if (aBackingUrl == null) {
            aBackingUrl = "";
        }
        if (!oldBackingUrl.equals(aBackingUrl)) {
            removeLayersListeners();
            MapContent generalMapContext = getGeneralMapContext();
            assert cache instanceof AsyncMapTilesCache;
            ((AsyncMapTilesCache) cache).shutdown();
            if (aBackingUrl.isEmpty()) {
                cache = new AsyncMapTilesCache(generalMapContext, cacheLock, cartesian2Screen);
            } else {
                cache = GeoPaneUtils.createWebTilesCache(generalMapContext, cacheLock, cartesian2Screen, aBackingUrl);
            }
            cache.setBackground(getBackground());
            ((AsyncMapTilesCache) cache).addRenderingTaskListener(new TileRenderedRepainter());
            cache.scaleChanged();
            lightweightCache.scaleChanged();
            addLayersListeners();
        }
        repaint();
    }

    public void fit() throws Exception {
        MapContent generalMapContext = getGeneralMapContext();
        Dimension size = getSize();
        Envelope projectedBounds = generalMapContext.getMaxBounds();
        double destWidth = projectedBounds.getWidth();
        double destHeight = projectedBounds.getHeight();
        Coordinate centre = projectedBounds.centre();
        Point2D.Double screenLT = awtScreen2Cartesian(new Point(0, 0));
        Point2D.Double screenBR = awtScreen2Cartesian(new Point(size.width, size.height));
        double srcWidth = screenBR.x - screenLT.x;
        double srcHeight = screenBR.y - screenLT.y;
        double sx = srcWidth / destWidth;
        double sy = srcHeight / destHeight;
        double coef = Math.min(sx, sy);
        coef = snapScale(coef);
        scaleView(coef, coef, false);
        Point2D.Double projectedScreenCenter = screen2Cartesian(new Point(0, 0));
        translateView(projectedScreenCenter.x - centre.x, projectedScreenCenter.y - centre.y, true);
        repaint();
    }

    /**
     * Fits screen to specified geometry bounds.
     * @param aArea A geometry in geo coordinates space.
     * @throws Exception
     */
    public void fit(Geometry aArea) throws Exception {

        Geometry bounds = aArea.getBoundary();
        Envelope envBounds = bounds.getEnvelopeInternal();
        Point2D.Double leftUpCorner = new Point2D.Double(envBounds.getMinX(), envBounds.getMinY());
        Point2D.Double rightBottomCorner = new Point2D.Double(envBounds.getMaxX(), envBounds.getMaxY());
        Point2D.Double cartlu = geo2Cartesian(leftUpCorner);
        Point2D.Double cartrb = geo2Cartesian(rightBottomCorner);
        double destWidth = Math.abs(cartrb.getX() - cartlu.getX());
        double destHeight = Math.abs(cartrb.getY() - cartlu.getY());
        Coordinate centre = new Coordinate((cartrb.getX() + cartlu.getX()) / 2, (cartrb.getY() + cartlu.getY()) / 2);

        Dimension size = getSize();
        Point2D.Double screenLT = awtScreen2Cartesian(new Point(0, 0));
        Point2D.Double screenBR = awtScreen2Cartesian(new Point(size.width, size.height));

        double srcWidth = screenBR.x - screenLT.x;
        double srcHeight = screenBR.y - screenLT.y;
        double sx = srcWidth / destWidth;
        double sy = srcHeight / destHeight;
        double coef = Math.min(sx, sy);
        coef = snapScale(coef);
        scaleView(coef, coef, false);
        
        Point2D.Double projectedScreenCenter = screen2Cartesian(new Point(0, 0));
        translateView(projectedScreenCenter.x - centre.x, projectedScreenCenter.y - centre.y, true);
        repaint();
    }

    public Point.Double getGeoPosition() throws Exception {
        return cartesian2Geo(screen2Cartesian(new Point(0, 0)));
    }

    public void goToGeoPosition(Point.Double aGeoPosition) throws Exception {
        Point2D.Double projectedScreenCenter = screen2Cartesian(new Point(0, 0));
        Point2D.Double projectedTarget = geo2Cartesian(aGeoPosition);
        translateView(projectedScreenCenter.x - projectedTarget.x, projectedScreenCenter.y - projectedTarget.y, true);
        repaint();
    }

    /**
     * Ajusts view point according to tiles grid offset.
     * E.g. converts grid offset to view point offset.
     * Clears grid offset. Doesn't clear any caches or repaint.
     * @throws NoninvertibleTransformException
     */
    protected void recalcViewPoint() throws NoninvertibleTransformException {
        Point2D.Double screenCenterOnCartesian = screen2Cartesian(new Point(0, 0));
        Point2D.Double screenViewpointOnCartesian = screen2Cartesian(new Point(-offset.x, -offset.y));
        cartesian2Screen.translate(screenViewpointOnCartesian.getX() - screenCenterOnCartesian.getX(), screenViewpointOnCartesian.getY() - screenCenterOnCartesian.getY());
        clearGridTranslating();
    }

    public void clearCaches() {
        cache.clear();
        lightweightCache.clear();
    }

    public void clearLightweightCache() {
        lightweightCache.clear();
    }

    public Point2D.Double cartesian2Geo(Point2D.Double aPt) throws FactoryException, TransformException {
        MathTransform2D transform = ((MapTilesCache) cache).getCartesian2MapTransform();
        Point2D.Double res = new Point2D.Double();
        return (Point2D.Double) transform.transform(aPt, res);
    }

    public Point2D.Double geo2Cartesian(Point2D.Double aPt) throws FactoryException, TransformException {
        MathTransform2D transform = ((MapTilesCache) cache).getMap2CartesianTransform();
        Point2D.Double res = new Point2D.Double();        
        return (Point2D.Double) transform.transform(aPt, res);
    }

    public Point cartesian2Screen(Point2D.Double aPt) throws FactoryException, TransformException {
        Point2D.Double screenPt = new Point2D.Double();
        cartesian2Screen.transform(aPt, screenPt);
        return new Point((int) Math.round(screenPt.getX()), (int) Math.round(screenPt.getY()));
    }

    /**
     * Converts screen point to cartesian space. Takes into account inner tiles grid offset.
     * @param aScreenPoint Point, expressed in AWT events coordinate space. E.g. center of the control will have coordinates (size.width/2, size.height/2).
     * @return Point in cartesian space.
     * @throws NoninvertibleTransformException
     */
    public Point2D.Double awtScreen2Cartesian(Point aScreenPoint) throws NoninvertibleTransformException {
        assert cartesian2Screen != null;
        Dimension size = getSize();
        Point2D.Double centeredScreenPt = new Point2D.Double(aScreenPoint.x - size.width / 2 + offset.x, aScreenPoint.y - size.height / 2 + offset.y);
        Point2D.Double cartesianPt = new Point2D.Double();
        return (Point2D.Double) cartesian2Screen.inverseTransform(centeredScreenPt, cartesianPt);
    }

    /**
     * Converts screen point to cartesian space. Takes into account inner tiles grid offset.
     * @param aScreenPoint Point, expressed in centered screen coordinate space. E.g. center of the control will have coordinates (0, 0).
     * @return Point in cartesian space.
     * @throws NoninvertibleTransformException
     */
    public Point2D.Double screen2Cartesian(Point aScreenPoint) throws NoninvertibleTransformException {
        assert cartesian2Screen != null;
        Point2D.Double centeredScreenPt = new Point2D.Double(aScreenPoint.x + offset.x, aScreenPoint.y + offset.y);
        Point2D.Double cartesianPt = new Point2D.Double();
        return (Point2D.Double) cartesian2Screen.inverseTransform(centeredScreenPt, cartesianPt);
    }

    protected void fireViewTranslated(double aDx, double aDy, Geometry aNewGeoAreaOfInterest, Geometry aNewCartesianAreaOfInterest) throws Exception {
        ViewpointTranslatedEvent event = new ViewpointTranslatedEvent(cartesian2Screen, aDx, aDy, aNewGeoAreaOfInterest, aNewCartesianAreaOfInterest);
        for (GeoPaneViewpointListener l : viewListeners) {
            try {
                l.translated(event);
            } catch (Exception ex) {
                Logger.getLogger(JGeoPane.class.getName()).log(Level.SEVERE, "while firing an event: \"viewTranslatedEvent\" on listener: " + l.toString(), ex);
            }
        }
    }

    protected void fireViewScaled(double aScaleX, double aScaleY, Geometry aNewGeoAreaOfInterest, Geometry aNewCartesianAreaOfInterest) throws Exception {
        ViewpointScaledEvent event = new ViewpointScaledEvent(cartesian2Screen, aScaleX, aScaleY, aNewGeoAreaOfInterest, aNewCartesianAreaOfInterest);
        for (GeoPaneViewpointListener l : viewListeners) {
            try {
                l.scaled(event);
            } catch (Exception ex) {
                Logger.getLogger(JGeoPane.class.getName()).log(Level.SEVERE, "while firing an event: \"viewScaledEvent\" on listener: " + l.toString(), ex);
            }
        }
    }

    public void fireMapClicked(MouseEvent awtEvent, Point2D.Double cartesianPoint, Point2D.Double geoPoint) {
        MapClickedEvent event = new MapClickedEvent(awtEvent, createPointGeometry(cartesianPoint), createPointGeometry(geoPoint));
        for (GeoPaneMouseListener l : mouseListeners) {
            try {
                l.clicked(event);
            } catch (Exception ex) {
                Logger.getLogger(JGeoPane.class.getName()).log(Level.SEVERE, "while firing an event: \"mapClickedEvent\" on listener: " + l.toString(), ex);
            }
        }
    }

    public void addGeoPaneViewpointListener(GeoPaneViewpointListener l) {
        viewListeners.add(l);
    }

    public boolean removeGeoPaneViewpointListener(GeoPaneViewpointListener l) {
        return viewListeners.remove(l);
    }

    public void addGeoPaneMouseListener(GeoPaneMouseListener l) {
        mouseListeners.add(l);
    }

    public boolean removeGeoPaneMouseListener(GeoPaneMouseListener l) {
        return mouseListeners.remove(l);
    }
}
