/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.cache;

import com.eas.client.controls.geopane.TileUtils;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;

/**
 *
 * @author mg
 */
public abstract class MapTilesCache extends TilesCache {

    public class RenderingTask implements Runnable {

        protected RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        /**
         * tile coordinates in tile space
         */
        protected Point tilePoint;
        /**
         * Simply bounds of tile's image
         */
        protected Rectangle paintArea = new Rectangle(-tileSize / 2, -tileSize / 2, tileSize, tileSize);
        /**
         * Tile's center coordinatees in screen space.
         */
        protected Point tileCenter;
        /**
         * Tile coordinates in map's cartesian space
         */
        protected ReferencedEnvelope tileAoi;
        protected GTRenderer taskRenderer;
        protected AffineTransform tileTransform;
        protected boolean stopped = false;

        public RenderingTask(Point aTilePoint) throws NoninvertibleTransformException {
            super();
            tilePoint = new Point(aTilePoint);
            tileCenter = expandPointFromCell(tilePoint);
            calcAoi();
            taskRenderer = archieveRenderer();
            Map<Object, Object> hints = new HashMap<>();
            hints.put(StreamingRenderer.ADVANCED_PROJECTION_HANDLING_KEY, Boolean.TRUE);
            taskRenderer.setRendererHints(hints);
            tileTransform = new AffineTransform(cartesian2ScreenTransform);
        }

        @Override
        public void run() {
            if (!isStopped() && isActual()) {
                BufferedImage img = (BufferedImage) imagesQueue.poll();
                if (img == null) {
                    //img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(tileSize, tileSize, imageType==BufferedImage.TYPE_INT_ARGB?Transparency.BITMASK:Transparency.OPAQUE);
                    //img.setAccelerationPriority(1);
                    img = new BufferedImage(tileSize, tileSize, imageType);
                }
                //BufferedImage img = new BufferedImage(tileSize, tileSize, imageType);
                prepareImageTile(img);
                Graphics g = img.createGraphics();
                if (g instanceof Graphics2D) {
                    ((Graphics2D) g).setRenderingHints(renderingHints);
                }
                g.translate(tileSize / 2, tileSize / 2);
                tileTransform.translate(-tileCenter.x / tileTransform.getScaleX(), -tileCenter.y / tileTransform.getScaleY());
                assert g instanceof Graphics2D;
                assert taskRenderer != null;
                if (!isStopped() && isActual()) {
                    taskRenderer.setMapContent(mapDisplayContext);
                    try {
                        taskRenderer.paint((Graphics2D) g, paintArea, tileAoi, tileTransform);
                    } catch (Exception ex) {
                        Logger.getLogger(MapTilesCache.class.getName()).severe(ex.getMessage());
                    }
                    g.dispose();
                    // If renderer will be stopped while taskRenderer.paint() we'll get a bad image, so we can't put it to the cache
                    // All other cases are tolerable.
                    // For example, if all work is completed, but task is nevertheless stopped.
                    // It's not good, but it's also not harmful.
                    if (!isStopped() && isActual()) {
                        put(tilePoint, img);
                    } else {
                        imagesQueue.add(img);
                    }
                } else {
                    g.dispose();
                    imagesQueue.add(img);
                }
            }
        }

        public synchronized boolean isStopped() {
            return stopped;
        }

        public synchronized void setStopped(boolean stopped) {
            this.stopped = stopped;
        }

        protected boolean isActual() {
            return true;
        }

        protected void prepareImageTile(BufferedImage img) {
            Graphics2D g = img.createGraphics();
            g.setBackground(background);
            g.clearRect(0, 0, tileSize, tileSize);
            g.dispose();
        }

        /**
         * Rendered area in screen space related to tiles grid origin
         * @return
         */
        public Rectangle getRenderedArea() {
            return TileUtils.expandRectFromCell(tilePoint, tileSize);
        }

        private void calcAoi() throws NoninvertibleTransformException {
            Point2D.Double[] pts = new Point2D.Double[4];
            pts[0] = new Point2D.Double(tileCenter.x - tileHalf, tileCenter.y - tileHalf);
            pts[1] = new Point2D.Double(tileCenter.x + tileHalf, tileCenter.y - tileHalf);
            pts[2] = new Point2D.Double(tileCenter.x - tileHalf, tileCenter.y + tileHalf);
            pts[3] = new Point2D.Double(tileCenter.x + tileHalf, tileCenter.y + tileHalf);
            cartesian2ScreenTransform.inverseTransform(pts[0], pts[0]);
            cartesian2ScreenTransform.inverseTransform(pts[1], pts[1]);
            cartesian2ScreenTransform.inverseTransform(pts[2], pts[2]);
            cartesian2ScreenTransform.inverseTransform(pts[3], pts[3]);
            double x1 = Double.MAX_VALUE;
            double x2 = -Double.MAX_VALUE;
            double y1 = Double.MAX_VALUE;
            double y2 = -Double.MAX_VALUE;
            for (Point2D.Double pt : pts) {
                x1 = Math.min(x1, pt.x);
                y1 = Math.min(y1, pt.y);
                x2 = Math.max(x2, pt.x);
                y2 = Math.max(y2, pt.y);
            }
            tileAoi = new ReferencedEnvelope(x1, x2, y1, y2, mapDisplayContextCrs);
        }

        protected GTRenderer archieveRenderer() {
            return renderer;
        }

        public Point getTilePoint() {
            return tilePoint;
        }
    }
    /**
     * Contains local query envelope (area of interest), coordinate reference system and list of layers.
     */
    protected MapContent mapDisplayContext;
    // Added to avoid synchronization problems with getCoordinateReferenceSystem() method of DefaultMapContext
    protected CoordinateReferenceSystem mapDisplayContextCrs;
    /**
     * Cartesian to screen affine transformation. Initially hold information about user's view point;
     */
    protected AffineTransform cartesian2ScreenTransform;
    protected GTRenderer renderer = new StreamingRenderer();
    protected MathTransform2D mapToCartesianTransform;
    protected MathTransform2D cartesian2MapTransform;

    public MapTilesCache(MapContent aDisplayContext, AffineTransform aTransform) {
        super();
        mapDisplayContext = aDisplayContext;
        mapDisplayContextCrs = mapDisplayContext.getCoordinateReferenceSystem();
        cartesian2ScreenTransform = aTransform;
    }

    public MapTilesCache(int aCacheSize, MapContent aDisplayContext, AffineTransform aTransform) {
        this(aDisplayContext, aTransform);
        cacheSize = aCacheSize;
    }
    
    @Override
    protected abstract Image renderTile(Point ptKey);

    public MapContent getMapDisplayContext() {
        return mapDisplayContext;
    }

    public MathTransform2D getMap2CartesianTransform() throws FactoryException {
        if (mapToCartesianTransform == null) {
            if (mapDisplayContextCrs instanceof ProjectedCRS) {
                ProjectedCRS pCrs = (ProjectedCRS) mapDisplayContextCrs;
                MathTransform transform = CRS.findMathTransform(pCrs.getBaseCRS(), pCrs);
                assert transform instanceof MathTransform2D;
                mapToCartesianTransform = (MathTransform2D) transform;
                return mapToCartesianTransform;
            }
            return null;
        } else {
            return mapToCartesianTransform;
        }
    }

    public MathTransform2D getCartesian2MapTransform() throws FactoryException, org.opengis.referencing.operation.NoninvertibleTransformException {
        if (cartesian2MapTransform == null) {
            MathTransform2D transform = getMap2CartesianTransform();
            cartesian2MapTransform = transform.inverse();
        }
        return cartesian2MapTransform;
    }
}
